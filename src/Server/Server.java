package Server;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Server
 *
 * @Created by Long - StudentID : 18120455
 * @Date 03/07/2020 - 9:51 PM
 * @Description
 **/


public class Server {

    private static List<UserAccount> userAccountList;
    private static List<UserAccount> userOfflineList;
    private static final List<UserAccount> userOnlineList = new ArrayList<UserAccount>();
    private static List<HandleRequestThread> handleRequestThreads;
    private static final HashMap<Integer, TransferFileConnection> transferFileConnectionMap = new HashMap<Integer, TransferFileConnection>();

    private static final String FILE_USER_LIST_NAME = "user_list.txt";
    private static final int PORT = 5555;

    public static final String LOGIN_COMMAND = "LOGIN";
    public static final String REGISTER_COMMAND = "REGISTER";
    public static final String SEND_COMMAND = "SEND";
    public static final String UPDATE_COMMAND = "UPDATE";
    public static final String LOGOUT_COMMAND = "LOGOUT";
    public static final String SEND_FILE_COMMAND = "SEND_FILE";
    public static final String ESTABLISH_SEND_FILE = "ESTABLISH_SEND_FILE";
    public static final String ESTABLISH_RECEIVE_FILE = "ESTABLISH_RECEIVE_FILE";


    public static void main(String[] args) {

        try {
            userAccountList = parseStringToListUsers();
            // Clone all user
            userOfflineList = new ArrayList<UserAccount>(userAccountList);

            ServerSocket serverSocket = new ServerSocket(PORT);
            handleRequestThreads = new ArrayList<HandleRequestThread>();

            do {
                System.out.println("Waiting for a Client");

                Socket socket = serverSocket.accept(); //synchronous
                System.out.println(socket.getRemoteSocketAddress() + " has connected!");

                HandleRequestThread handleRequestThread = new HandleRequestThread(socket);
                handleRequestThread.start();
                handleRequestThreads.add(handleRequestThread);

            }
            while (true);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static List<UserAccount> parseStringToListUsers() throws IOException {

        File file = new File(FILE_USER_LIST_NAME);
        if (!file.exists())
            file.createNewFile();

        BufferedReader bufferedFile = new BufferedReader( new FileReader(FILE_USER_LIST_NAME));
        List<UserAccount> list = new ArrayList<>();

        while (true) {
            String line = bufferedFile.readLine();
            if (line == null)
                break;

            list.add(UserAccount.parseStringToUser(line));
        }

        bufferedFile.close();
        return list;
    }

    public static void updateFileUserList(List<UserAccount> userAccountList) {

        String tmpFileName = "tmp_try.txt";

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(tmpFileName))) {

            for (UserAccount account : userAccountList)
                bw.write(account.toString() + "\n");

        } catch (IOException fileNotFoundException) {
            fileNotFoundException.printStackTrace();
        }

        // Once everything is complete, close the stream and delete old file..
        File oldFile = new File(FILE_USER_LIST_NAME);
        oldFile.delete();

        // And rename tmp file's name to old file name
        File newFile = new File(tmpFileName);
        newFile.renameTo(oldFile);
    }

    public static UserAccount getUserByUsernameAndPassword(String username, String password) {

        for (UserAccount userAccount: userAccountList) {
            if (userAccount.getUserName().equals(username) && userAccount.getPassword().equals(password))
                return userAccount;
        }

        return null;
    }

    public static UserAccount getUserByUsername(String username) {

        for (UserAccount userAccount: userAccountList) {
            if (userAccount.getUserName().equals(username))
                return userAccount;
        }

        return null;
    }

    public static List<UserAccount> getUserAccountList() {
        return userAccountList;
    }

    public static List<HandleRequestThread> getHandleRequestThreads() {
        return handleRequestThreads;
    }

    public static void updateUserOnline(String username, HandleRequestThread thread) {

        for (UserAccount userAccount: userAccountList) {

            if (userAccount.getUserName().equals(username)) {

                // Check if account is offline
                if (userOfflineList.contains(userAccount)) {
                    // Announcement
                    for (UserAccount user : userOnlineList) {
                        try {

                            if (user.getUserName().equals(username))
                                continue;

                            List<HandleRequestThread> list = user.getHandleThreads();
                            for (HandleRequestThread requestThread : list) {
                                thread.sendAnnounceMessageToClient(requestThread.getSocket(),
                                        thread.getAccount().getUserName() + " đã tham gia phòng.");
                            }

                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                    // removes from offline list
                    userOfflineList.remove(userAccount);
                }

                userAccount.getHandleThreads().add(thread);
                if (!userOnlineList.contains(userAccount))
                    userOnlineList.add(userAccount);
                break;
            }
        }
    }

    public static void updateUserOffline(String username, HandleRequestThread thread) {

        for (UserAccount userAccount: userAccountList) {

            if (userAccount.getUserName().equals(username)) {

                userAccount.getHandleThreads().remove(thread);

                // removes from online list
                if (userAccount.getHandleThreads().isEmpty()) {
                    userOnlineList.remove(userAccount);

                    for (UserAccount user : userOnlineList) {
                        try {

                            if (user.getUserName().equals(username))
                                continue;

                            List <HandleRequestThread> list = user.getHandleThreads();
                            for (HandleRequestThread requestThread : list) {
                                thread.sendAnnounceMessageToClient(requestThread.getSocket(),
                                        thread.getAccount().getUserName() + " đã rời phòng.");
                            }

                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    if (!userOfflineList.contains(userAccount))
                        userOfflineList.add(userAccount);
                }

                break;
            }
        }
    }

    public static String accountsToListString(List<UserAccount> list) {

        StringBuilder sb = new StringBuilder();
        if (list.isEmpty())
            return HandleRequestThread.NULL;

        for (UserAccount account: list) {

            sb.append(account.getUserName()).append(",");
        }

        return sb.toString();
    }

    public static List<UserAccount> getUserOfflineList() {
        return userOfflineList;
    }

    public static List<UserAccount> getUserOnlineList() {
        return userOnlineList;
    }

    public static HashMap<Integer, TransferFileConnection> getTransferFileConnectionMap() {
        return transferFileConnectionMap;
    }
}


