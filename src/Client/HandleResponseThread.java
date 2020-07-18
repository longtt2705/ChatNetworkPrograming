package Client;

import Client.GUI.ChatBoxGUI;
import Client.GUI.MainUI;

import javax.swing.*;
import java.io.DataInputStream;
import java.io.IOException;
import java.net.SocketException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/**
 * Client
 *
 * @Created by Long - StudentID : 18120455
 * @Date 08/07/2020 - 10:16 AM
 * @Description
 **/
public class HandleResponseThread extends Thread {

    public static final String REGEX = "/";
    public static final String SUCCESSFUL = "SUCCESSFUL";
    public static final String FAIL = "FAIL";
    public static final String NULL = "NULL";

    @Override
    public void run() {

        DataInputStream is = Client.is;

        while (true) {
            try {

                String receiveMessage = is.readUTF();
                String command = receiveMessage.split(REGEX)[0];

                switch (command) {
                    case Client.LOGIN_COMMAND -> {
                        String result = receiveMessage.split(REGEX)[1];
                        if (result.equals(SUCCESSFUL)) {

                            Client.currentAccount = receiveMessage.split(REGEX)[2];
                            Client.invokeGUI(Client.ViewLevel.MAIN_PROGRESS, null);
                        }

                        else
                            JOptionPane.showMessageDialog(null, "Sai tên đăng nhập hoặc mật khẩu!",
                                    "Login error", JOptionPane.ERROR_MESSAGE);
                    }

                    case Client.REGISTER_COMMAND -> {
                        String result = receiveMessage.split(REGEX)[1];
                        if (result.equals(SUCCESSFUL))
                            JOptionPane.showMessageDialog(null, "Đăng ký tài khoản thành công!");
                        else
                            JOptionPane.showMessageDialog(null, "Tên đăng nhập đã tồn tại!",
                                    "Register error", JOptionPane.ERROR_MESSAGE);
                    }

                    case Client.SEND_COMMAND -> {
                        String[] temp = receiveMessage.split(REGEX, 4);
                        String sendToUser = temp[2];
                        String sendFromUser = temp[1];
                        String message = temp[3];

                        if (sendToUser.equals("ALL")) {
                            MainUI mainUI = null;

                            if (Client.currentFrame instanceof MainUI)
                            mainUI = (MainUI) Client.currentFrame;

                            if (mainUI != null)
                                mainUI.addMessage(sendFromUser, message);
                        } else {

                            HashMap<String, ChatBoxGUI> map = Client.getChatBoxMap();
                            // check if send to self
                            if (!map.containsKey(sendToUser)) {

                                if (!map.containsKey(sendFromUser))
                                    Client.createChatBoxWithMessage(sendFromUser, message);
                                else {
                                    map.get(sendFromUser).addMessage(sendFromUser, message);
                                }
                            } else {
                                map.get(sendToUser).addMessage(sendFromUser, message);
                            }
                        }
                    }

                    case Client.UPDATE_COMMAND -> {
                        MainUI mainUI = null;

                        if (Client.currentFrame instanceof MainUI)
                            mainUI = (MainUI) Client.currentFrame;

                        if (mainUI != null) {

                            String listOnline = receiveMessage.split(REGEX)[1];
                            String listOffline = receiveMessage.split(REGEX)[2];
                            mainUI.updateListUsers(stringUsersToList(listOnline), stringUsersToList(listOffline));

//                            HashMap<String, ChatBoxGUI> boxMap = Client.getChatBoxMap();
//
//                            for (String user : listOffline.split(",")) {
//                                if (boxMap.containsKey(user)) {
//                                    boxMap.get(user).dispose();
//                                    boxMap.remove(user);
//                                }
//                            }
                        }
                    }

                    case Client.ESTABLISH_RECEIVE_FILE -> {

                        String[] splited = receiveMessage.split(REGEX);
                        int code = Integer.parseInt(splited[1]);
                        String sendFromUser = splited[2];
                        String fileName = splited[3];
                        long fileLength = is.readLong();
                        new ReceiveFileThread(code, sendFromUser, fileName, fileLength).start();
                    }
                }

            } catch (SocketException socketException) {
                int input = JOptionPane.showConfirmDialog(Client.currentFrame, "Mất kết nối tới server!",
                        "Connection Error", JOptionPane.OK_CANCEL_OPTION);
                if (input == JOptionPane.OK_OPTION || input == JOptionPane.CANCEL_OPTION)
                    System.exit(0);

                try {
                    is.close();
                    Client.getSocket().close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private List<String> stringUsersToList(String line) {

        if (line.equals(NULL))
            return null;
        return Arrays.asList(line.split(","));
    }
}
