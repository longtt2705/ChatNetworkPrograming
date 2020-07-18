package Server;

import java.io.*;
import java.net.Socket;
import java.net.SocketException;
import java.util.HashMap;
import java.util.List;


/**
 * Server
 *
 * @Created by Long - StudentID : 18120455
 * @Date 05/07/2020 - 4:46 PM
 * @Description
 **/
public class HandleRequestThread extends Thread {

    public static final String REGEX = "/";
    public static final String SUCCESSFUL = "SUCCESSFUL";
    public static final String FAIL = "FAIL";
    public static final String NULL = "NULL";

    private final Socket socket;
    private UserAccount account;
    private boolean isTransferThread = false;

    public HandleRequestThread(Socket socket) {
        this.socket = socket;

    }

    public boolean isTransferThread() {
        return isTransferThread;
    }

    public void setAccount(UserAccount account) {
        this.account = account;
    }

    public Socket getSocket() {
        return socket;
    }

    public UserAccount getAccount() {
        return account;
    }

    @Override
    public void run() {
        DataInputStream inputStream = null;
        DataOutputStream outputStream = null;

        try {

            inputStream = new DataInputStream(socket.getInputStream());
            outputStream = new DataOutputStream(socket.getOutputStream());


            do {
                String receiveMessage;
                if (socket.isClosed())
                    throw new SocketException();

                receiveMessage = inputStream.readUTF();

                String[] messageSplit = receiveMessage.split(REGEX);
                String command = "";

                if (messageSplit.length > 0)
                    command = receiveMessage.split(REGEX)[0];
                System.out.println(receiveMessage);

                switch (command) {
                    case Server.LOGIN_COMMAND -> {

                        if (loginCommandExecute(receiveMessage)) {

                            String userName = receiveMessage.split(REGEX)[1];
                            String password = receiveMessage.split(REGEX)[2];

                            outputStream.writeUTF(Server.LOGIN_COMMAND + REGEX + SUCCESSFUL + REGEX + userName);
                            setAccount(new UserAccount(userName, password));

                            Server.updateUserOnline(getAccount().getUserName(), this);
                            announceUpdateBroadCast();
                        } else
                            outputStream.writeUTF(Server.LOGIN_COMMAND + REGEX + FAIL);
                    }

                    case Server.REGISTER_COMMAND -> {

                        if (registerCommandExecute(receiveMessage))
                            outputStream.writeUTF(Server.REGISTER_COMMAND + REGEX + SUCCESSFUL);
                        else
                            outputStream.writeUTF(Server.REGISTER_COMMAND + REGEX + FAIL);
                    }

                    case Server.SEND_COMMAND -> directMessage(receiveMessage);

                    case Server.UPDATE_COMMAND -> announceUpdateBroadCast();

                    case Server.LOGOUT_COMMAND -> {

                        Server.updateUserOffline(getAccount().getUserName(), this);
                        announceUpdateBroadCast();
                        setAccount(null);
                    }

                    case Server.ESTABLISH_SEND_FILE -> {

                        isTransferThread = true;
                        String userName = messageSplit[1];
                        setAccount(new UserAccount(userName, null));
                        String desName = messageSplit[2];
                        String fileName = messageSplit[3];
                        long fileLength = inputStream.readLong();

                        HashMap<Integer, TransferFileConnection> map = Server.getTransferFileConnectionMap();
                        TransferFileConnection con = new TransferFileConnection(getSocket(),
                                getAccount().getUserName(), desName, this);

                        map.put(con.hashCode(), con);
                        announceToReceiveUser(con.hashCode(), desName, fileName, fileLength);
                    }

                    case Server.ESTABLISH_RECEIVE_FILE -> {

                        if (messageSplit[1].equals(SUCCESSFUL)) {

                            isTransferThread = true;
                            int code = Integer.parseInt(messageSplit[2]);
                            HashMap<Integer, TransferFileConnection> map = Server.getTransferFileConnectionMap();

                            TransferFileConnection con = map.get(code);
                            if (con != null && con.getReceiveUser() == null) {

                                con.setReceiveUser(getSocket());
                                sendEstablishSendFileSuccess(con.getSendUser(), String.valueOf(con.hashCode()));
                                sendEstablishReceiveFileSuccess(con.getReceiveUser());


                            } else {
                                sendEstablishReceiveFileFail(getSocket());
                            }
                        }

                    }

                    case Server.SEND_FILE_COMMAND -> {

                        int length = inputStream.readInt();
                        byte[] buffer = new byte[length]; // or 4096, or more
                        inputStream.readFully(buffer, 0, buffer.length);
                        System.out.println(buffer.length);
                        directFile(receiveMessage, buffer);
                    }
                }
            } while (true);
        } catch (SocketException socketException) {

            if (getAccount() != null) {
                Server.updateUserOffline(getAccount().getUserName(), this);
                announceUpdateBroadCast();
            }
        } catch (IOException e) {
            e.printStackTrace();

        } finally {
            try {
                getSocket().close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private boolean registerCommandExecute(String receiveMessage) {

        String userName = receiveMessage.split(REGEX)[1];
        String password = receiveMessage.split(REGEX)[2];

        List<UserAccount> list = Server.getUserAccountList();
        if (Server.getUserByUsername(userName) != null)
            return false;

        UserAccount user = new UserAccount(userName, password);
        list.add(user);
        Server.getUserOfflineList().add(user);
        announceUpdateBroadCast();
        Server.updateFileUserList(list);
        return true;
    }

    private boolean loginCommandExecute(String receiveMessage) {

        String userName = receiveMessage.split(REGEX)[1];
        String password = receiveMessage.split(REGEX)[2];

        return Server.getUserByUsernameAndPassword(userName, password) != null;
    }

    private void announceToReceiveUser(int connectCode, String desName, String fileName, long fileLength) throws IOException {

        List<UserAccount> list = Server.getUserOnlineList();
        for (UserAccount userAccount : list) {

            if (userAccount.getUserName().equals(desName)) {
                List<HandleRequestThread> requestThreads = userAccount.getHandleThreads();

                for (HandleRequestThread thread : requestThreads) {
                    if (!thread.isTransferThread)
                        sendConfirmReceiveData(thread.getSocket(), connectCode, getAccount().getUserName(), fileName, fileLength);
                }
                break;
            }
        }
    }

    private void directFile(String receiveMessage, byte[] data) throws IOException {

        String[] temp = receiveMessage.split(REGEX, 4);
        int code = Integer.parseInt(temp[1]);

        TransferFileConnection connection = Server.getTransferFileConnectionMap().get(code);
        if (connection != null) {
            connection.sendData(data);

        }
    }

    private void sendConfirmReceiveData(Socket destination, int connectCode, String userName, String fileName, long fileLength) throws IOException {

        DataOutputStream os = new DataOutputStream(destination.getOutputStream());

        os.writeUTF(Server.ESTABLISH_RECEIVE_FILE + REGEX + connectCode + REGEX + userName + REGEX + fileName);
        os.writeLong(fileLength);
    }

    private void sendEstablishSendFileSuccess(Socket destination, String connectCode) throws IOException {

        DataOutputStream os = new DataOutputStream(destination.getOutputStream());

        os.writeUTF(Server.ESTABLISH_SEND_FILE + REGEX + SUCCESSFUL + REGEX + connectCode);
    }

    private void sendEstablishSendFileFail(Socket destination) throws IOException {

        DataOutputStream os = new DataOutputStream(destination.getOutputStream());

        os.writeUTF(Server.ESTABLISH_SEND_FILE + REGEX + FAIL);
    }

    private void sendEstablishReceiveFileSuccess(Socket destination) throws IOException {

        DataOutputStream os = new DataOutputStream(destination.getOutputStream());

        os.writeUTF(Server.ESTABLISH_RECEIVE_FILE + REGEX + SUCCESSFUL);
    }

    private void sendEstablishReceiveFileFail(Socket destination) throws IOException {

        DataOutputStream os = new DataOutputStream(destination.getOutputStream());

        os.writeUTF(Server.ESTABLISH_RECEIVE_FILE + REGEX + FAIL);
    }

    private void directMessage(String receiveMessage) {

        String[] temp = receiveMessage.split(REGEX, 3);
        try {
            if (temp[1].equals("ALL")) {
                sendBroadCast(temp[2]);
            } else {
                String desName = temp[1];
                String message = temp[2];

                List<UserAccount> list = Server.getUserOnlineList();
                for (UserAccount userAccount : list) {

                    if (userAccount.getUserName().equals(desName)) {
                        List<HandleRequestThread> requestThreads = userAccount.getHandleThreads();

                        for (HandleRequestThread thread : requestThreads) {
                            if (!thread.isTransferThread)
                                sendMessageToClient(thread.getSocket(), desName, message);
                        }

                        break;
                    }
                }

                // send to myself
                for (UserAccount userAccount : list) {
                    if (userAccount.getUserName().equals(getAccount().getUserName())) {
                        List<HandleRequestThread> requestThreads = userAccount.getHandleThreads();

                        for (HandleRequestThread thread : requestThreads) {
                            if (!thread.isTransferThread)
                                sendMessageToClient(thread.getSocket(), desName, message);
                        }
                        break;
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    void sendMessageToRoom(Socket destination, String message) throws IOException {

        DataOutputStream os = new DataOutputStream(destination.getOutputStream());

        os.writeUTF(Server.SEND_COMMAND + REGEX + account.getUserName() + REGEX + "ALL" + REGEX + message);
    }

    void sendMessageToClient(Socket destination, String desName, String message) throws IOException {

        DataOutputStream os = new DataOutputStream(destination.getOutputStream());

        os.writeUTF(Server.SEND_COMMAND + REGEX + account.getUserName() + REGEX + desName + REGEX + message);
    }

    void sendAnnounceMessageToClient(Socket destination, String message) throws IOException {

        DataOutputStream os = new DataOutputStream(destination.getOutputStream());

        os.writeUTF(Server.SEND_COMMAND + REGEX + "->" + REGEX + "ALL" + REGEX + message);
    }

    private void sendBroadCast(String message) {
        List<UserAccount> list = Server.getUserOnlineList();
        for (UserAccount userAccount : list) {
            try {

                List <HandleRequestThread> requestThreads = userAccount.getHandleThreads();

                for (HandleRequestThread thread : requestThreads) {
                    if (!thread.isTransferThread)
                        sendMessageToRoom(thread.getSocket(), message);
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void sendUpdateCommand() {
        try {

            DataOutputStream os = new DataOutputStream(socket.getOutputStream());

            String listOnline = Server.accountsToListString(Server.getUserOnlineList());
            String listOffline = Server.accountsToListString(Server.getUserOfflineList());

            os.writeUTF(Server.UPDATE_COMMAND + REGEX + listOnline + REGEX + listOffline + REGEX);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void announceUpdateBroadCast() {

        List<UserAccount> list = Server.getUserOnlineList();
        for (UserAccount userAccount : list) {

            List <HandleRequestThread> requestThreads = userAccount.getHandleThreads();

            for (HandleRequestThread thread : requestThreads) {
                if (!thread.isTransferThread)
                    thread.sendUpdateCommand();
            }
        }
    }

    public void removeTransferFileConnection(TransferFileConnection fileConnection) throws IOException {

        Server.getTransferFileConnectionMap().remove(fileConnection.hashCode());
        sendEstablishSendFileFail(fileConnection.getSendUser());
        fileConnection.freeStream();
    }
}
