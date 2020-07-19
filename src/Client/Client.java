package Client;

import Client.GUI.*;

import javax.swing.*;
import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Client.GUI
 *
 * @Created by Long - StudentID : 18120455
 * @Date 03/07/2020 - 9:51 PM
 * @Description
 **/

public class Client {

    public enum ViewLevel {
        LOGIN,
        MAIN_PROGRESS,
        CHAT_BOX
    }

    public static int PORT = 5555;
    public static DataInputStream is;
    public static DataOutputStream os;
    public static int MIN_LENGTH = 6;
    private static Socket socket;
    private static HandleResponseThread handleResponseThread;
    private static final HashMap<String, ChatBoxGUI> chatBoxMap = new HashMap<>();

    // Commands
    public static final String LOGIN_COMMAND = "LOGIN";
    public static final String REGISTER_COMMAND = "REGISTER";
    public static final String SEND_COMMAND = "SEND";
    public static final String UPDATE_COMMAND = "UPDATE";
    public static final String LOGOUT_COMMAND = "LOGOUT";
    public static final String SEND_FILE_COMMAND = "SEND_FILE";
    public static final String ESTABLISH_SEND_FILE = "ESTABLISH_SEND_FILE";
    public static final String ESTABLISH_RECEIVE_FILE = "ESTABLISH_RECEIVE_FILE";

    public static String currentAccount;
    public static JFrame currentFrame;
    public static List<String> emojiList;

    private Client() {}

    public static void main(String[] arg) {

        // Initialize Emoji List
        initializeEmojiList();

        // Create UI
        invokeGUI(ViewLevel.LOGIN, null);

        // Connect to Server
        tryToConnectToServer();

    }

    public static void sendRegisterCommandToServer(String userName, String password) throws IOException {

        if (tryToConnectToServer())
            return;

        os.writeUTF(REGISTER_COMMAND + "/" + userName + "/" + password + "/");
    }

    public static void sendLoginCommandToServer(String userName, String password) throws IOException {

        if (tryToConnectToServer())
            return;

        os.writeUTF(LOGIN_COMMAND + "/" + userName + "/" + password + "/");

    }

    public static void sendMessageCommandToServer(String destination, String message) throws IOException {

        if (tryToConnectToServer())
            return;

        os.writeUTF(SEND_COMMAND + "/" + destination + "/" + message);

    }

    public static void sendUpdateCommandToServer() throws IOException {

        if (tryToConnectToServer())
            return;

        os.writeUTF(UPDATE_COMMAND);

    }

    public static void sendLogOutCommandToServer() throws IOException {

        if (tryToConnectToServer())
            return;

        os.writeUTF(LOGOUT_COMMAND);
    }

    public static boolean isInputInvalid(String string) {

        if (string.length() < MIN_LENGTH)
            return true;

        // ascii '!' --> '/'
        for (int i = 33; i <= 47; i++) {
            if (string.contains(String.valueOf((char)i)))
                return true;
        }

        // ascii ' ' --> '/'
        for (int i = 32; i <= 47; i++) {
            if (i == 46) //'.'
                continue;
            if (string.contains(String.valueOf((char)i)))
                return true;
        }

        // ascii ':' --> '@'
        for (int i = 58; i <= 64; i++) {
            if (string.contains(String.valueOf((char)i)))
                return true;
        }

        // ascii '[' --> '`'
        for (int i = 91; i <= 96; i++) {
            if (i == 95) //'_'
                continue;
            if (string.contains(String.valueOf((char)i)))
                return true;
        }

        // ascii '{' --> '~'
        for (int i = 123; i <= 126; i++) {
            if (string.contains(String.valueOf((char)i)))
                return true;
        }

        return false;
    }

    private static boolean tryToConnectToServer() {
        if (socket == null) {
            try {
                // connect to server
                socket = new Socket("localhost", PORT);

                // Get input/output stream
                is = new DataInputStream(socket.getInputStream());
                os = new DataOutputStream(socket.getOutputStream());

            } catch (IOException e) {
                socket = null;
            }

            if (socket == null) {
                JOptionPane.showMessageDialog(null, "Không thể kết nối tới server",
                        "Connection Error", JOptionPane.ERROR_MESSAGE);
                return true;
            }
        }

        // Create a new thread to handle response from server
        if (handleResponseThread == null) {
            HandleResponseThread thread = new HandleResponseThread();
            thread.start();
            handleResponseThread = thread;
        }

        return false;
    }

    public static void disposeAllChatBox() {

        chatBoxMap.forEach((k, v) -> v.dispose());
        chatBoxMap.clear();
    }

    public static void invokeGUI(ViewLevel viewLevel, String userName) {

        switch (viewLevel) {
            case LOGIN -> SwingUtilities.invokeLater(() -> {
                if (currentFrame != null)
                    currentFrame.dispose();

                LoginGUI temp = new LoginGUI();
                currentFrame = temp;
                temp.createAndShowGUI();
            });

            case MAIN_PROGRESS -> SwingUtilities.invokeLater(() -> {
                if (currentFrame != null)
                    currentFrame.dispose();

                MainUI temp = null;
                try {
                    temp = new MainUI();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                currentFrame = temp;
                temp.createAndShowGUI();
            });

            case CHAT_BOX -> SwingUtilities.invokeLater(() -> {

                if (!chatBoxMap.containsKey(userName)) {

                    ChatBoxGUI box = new ChatBoxGUI(userName);
                    chatBoxMap.put(userName, box);
                    box.createAndShowGUI();
                }});
            }

    }

    public static void createChatBoxWithMessage(String userName, String message) {

        SwingUtilities.invokeLater(() -> {

            if (!chatBoxMap.containsKey(userName)) {

                ChatBoxGUI box = new ChatBoxGUI(userName);
                chatBoxMap.put(userName, box);
                box.createAndShowGUI();
                box.addMessage(userName, message);
            }});
    }

    public static void createChatBoxToReceiveFile(String userName) {

        SwingUtilities.invokeLater(() -> {

            if (!chatBoxMap.containsKey(userName)) {

                ChatBoxGUI box = new ChatBoxGUI(userName);
                chatBoxMap.put(userName, box);
                box.createAndShowGUI();
            }});
    }

    public static HashMap<String, ChatBoxGUI> getChatBoxMap() {
        return chatBoxMap;
    }

    public static Socket getSocket() {
        return socket;
    }

    public static void initializeEmojiList() {

        emojiList = new ArrayList<>();

        emojiList.add("\uD83D\uDE00");
        emojiList.add("\uD83D\uDE18");
        emojiList.add("\uD83D\uDE44");
        emojiList.add("\uD83D\uDE04");
        emojiList.add("\uD83D\uDE36");
        emojiList.add("\uD83D\uDE06");
        emojiList.add("\uD83D\uDE05");
        emojiList.add("\uD83D\uDE0F");
        emojiList.add("\uD83D\uDE02");
        emojiList.add("\uD83D\uDE42");
        emojiList.add("\uD83D\uDE09");
        emojiList.add("\uD83D\uDE0A");
        emojiList.add("\uD83D\uDE07");
        emojiList.add("\uD83D\uDE0D");
        emojiList.add("\uD83D\uDE18");
        emojiList.add("\uD83D\uDE17");
        emojiList.add("\uD83D\uDE1A");
        emojiList.add("\uD83D\uDE0B");
        emojiList.add("\uD83D\uDE10");
        emojiList.add("\uD83D\uDE1B");

    }
}

