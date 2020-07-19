package Client.GUI;

import Client.Client;
import Client.GUI.Listener.DoubleClickAdapter;
import Client.GUI.Listener.EmojiListener;
import Client.GUI.Listener.LogoutListener;
import Client.GUI.Listener.SendBroadCastListener;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.io.IOException;

import java.util.List;

/**
 * Client.GUI
 *
 * @Created by Long - StudentID : 18120455
 * @Date 08/07/2020 - 2:18 PM
 * @Description
 **/
public class MainUI extends JFrame {

    private final JPanel container;

    private JPanel sidePanel;
    private JList<String> listOnlineUser;
    private JList<String> listOfflineUser;
    private DefaultListModel<String> listOnlineUserModel;
    private DefaultListModel<String> listOfflineUserModel;
    private JScrollPane scrollOnlineUsersPane;
    private JScrollPane scrollOfflineUsersPane;

    private JPanel chatPanel;
    private JScrollPane chatScroll;
    private JTextArea chatArea;
    private JTextField chatInputField;
    private JButton sendButton;

    private static final int HEIGHT = 400;

    private StringBuilder allMessages;

    public MainUI() throws HeadlessException, IOException {

        container = new JPanel();
        setUpSidePanel();
        setUpChatPanel();

        Client.sendUpdateCommandToServer();
        allMessages = new StringBuilder();
    }

    private void setUpChatPanel() {

        chatPanel = new JPanel();
        container.add(chatPanel);
        chatPanel.setLayout(new BoxLayout(chatPanel, BoxLayout.Y_AXIS));
        chatPanel.setPreferredSize(new Dimension(500, HEIGHT));

        chatArea = new JTextArea(20, 20);
        chatArea.setBorder(BorderFactory.createLoweredSoftBevelBorder());
        chatArea.setLineWrap(true);
        chatArea.setWrapStyleWord(true);
        chatArea.setEditable(false);
        chatScroll = new JScrollPane(chatArea);
        chatPanel.add(chatScroll);

        JPanel emojiPanel = new JPanel();
        emojiPanel.setBorder(BorderFactory.createLoweredBevelBorder());
        emojiPanel.setBackground(Color.white);
        emojiPanel.setLayout(new GridLayout(0, 10));

        chatPanel.add(emojiPanel);

        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new BoxLayout(inputPanel, BoxLayout.X_AXIS));
        chatInputField = new JTextField();
        chatInputField.setBorder(BorderFactory.createLoweredBevelBorder());
        chatInputField.setColumns(20);
        chatInputField.setFocusable(true);

        sendButton = new JButton("Gửi");
        sendButton.addActionListener(new SendBroadCastListener(chatInputField));
        sendButton.setMnemonic(KeyEvent.VK_ENTER);

        inputPanel.add(new JScrollPane(chatInputField));
        inputPanel.add(sendButton);

        createEmojiButtonsForPanel(emojiPanel);
        chatPanel.add(inputPanel);
    }

    private void setUpSidePanel() {
        sidePanel = new JPanel();
        container.add(sidePanel);
        sidePanel.setLayout(new BoxLayout(sidePanel, BoxLayout.Y_AXIS));
        sidePanel.setPreferredSize(new Dimension(200, HEIGHT));

        listOnlineUserModel = new DefaultListModel<>();
        listOfflineUserModel = new DefaultListModel<>();

        JButton logoutButton = new JButton("Đăng xuất");
        logoutButton.addActionListener(new LogoutListener());
        sidePanel.add(logoutButton);

        listOnlineUser = new JList<>(listOnlineUserModel);
        listOnlineUser.addMouseListener(new DoubleClickAdapter());
        listOnlineUser.setBorder(BorderFactory.createLoweredSoftBevelBorder());
        listOnlineUser.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        scrollOnlineUsersPane = new JScrollPane(listOnlineUser);
        scrollOnlineUsersPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        listOfflineUser = new JList<>(listOfflineUserModel);
        listOfflineUser.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        listOfflineUser.setBorder(BorderFactory.createLoweredSoftBevelBorder());
        scrollOfflineUsersPane = new JScrollPane(listOfflineUser);
        scrollOfflineUsersPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
    }

    public void createAndShowGUI() {

        // Create and set up a frame window
        JFrame.setDefaultLookAndFeelDecorated(true);
        setTitle("Chat");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        // Set the window to be visible as the default to be false
        setVisible(true);

        setContentPane(container);
        setLayout(new FlowLayout());

        sidePanel.add(new JLabel("-----------INFO-----------"));
        JLabel displayName = new JLabel(Client.currentAccount);
        displayName.setBorder(BorderFactory.createLoweredSoftBevelBorder());
        sidePanel.add(displayName);

        sidePanel.add(new JLabel("Danh sách online"));
        sidePanel.add(scrollOnlineUsersPane);

        sidePanel.add(new JLabel("Danh sách offline"));
        sidePanel.add(scrollOfflineUsersPane);

        pack();
    }

    public void addMessage(String displayName, String message) {

        allMessages.append(displayName).append(": ").append(message).append("\n");
        chatArea.setText(allMessages.toString());
    }

    public void updateListUsers(List<String> online, List<String> offline) {

        listOnlineUserModel.removeAllElements();
        listOfflineUserModel.removeAllElements();

        if (online != null) {
            for (String user : online)
                listOnlineUserModel.addElement(user);
        }

        if (offline != null) {
            for (String user : offline)
                listOfflineUserModel.addElement(user);
        }
    }

    private void createEmojiButtonsForPanel(JPanel panel) {

        for (String emoji : Client.emojiList) {
            JButton button = new JButton(emoji);
            button.addActionListener(new EmojiListener(chatInputField, emoji));
            panel.add(button);
        }
    }
}
