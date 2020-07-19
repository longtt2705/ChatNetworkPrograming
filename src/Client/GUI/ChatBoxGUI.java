package Client.GUI;

import Client.Client;
import Client.GUI.Listener.EmojiListener;
import Client.GUI.Listener.SendFileListener;
import Client.GUI.Listener.SendMessageListener;
import Client.Utilities.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

/**
 * Client
 *
 * @Created by Long - StudentID : 18120455
 * @Date 03/07/2020 - 9:55 AM
 * @Description
 **/


public class ChatBoxGUI extends JFrame {

    private String desName;
    private JTextField chatInputField;
    private JButton sendButton;
    private JPanel mainPanel;
    private JTextArea displayTextPanel;
    private StringBuilder allMessages;
    private JProgressBar bar;

    public ChatBoxGUI(String desName) throws HeadlessException {

        this.desName = desName;
        allMessages = new StringBuilder();
        bar = new JProgressBar();
    }

    /**
     * Create the GUI and show it.  For thread safety,
     * this method should be invoked from the
     * event-dispatching thread.
     */
    public void createAndShowGUI() {

        JFrame.setDefaultLookAndFeelDecorated(false);
        setTitle("Chat");
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        createContentPane();
        setContentPane(mainPanel);
        mainPanel.setBorder(BorderFactory.createTitledBorder(desName));

        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {

                Client.getChatBoxMap().remove(desName);
            }
        });

        setVisible(true);
        setResizable(false);
        chatInputField.requestFocus();
        pack();
    }

    public void createContentPane() {

        mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        displayTextPanel = new JTextArea();
        displayTextPanel.setBackground(Color.white);
        displayTextPanel.setLineWrap(true);
        displayTextPanel.setWrapStyleWord(true);
        displayTextPanel.setEditable(false);

        JScrollPane chatAreaScroll = new JScrollPane(displayTextPanel, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        chatAreaScroll.setPreferredSize(new Dimension(0, 250));
        chatAreaScroll.setBorder(BorderFactory.createLoweredSoftBevelBorder());
        mainPanel.add(chatAreaScroll);

        JPanel utilityPanel = new JPanel();
        utilityPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        JButton transferFileButton = new JButton();
        Utilities.setICon(transferFileButton, Utilities.TRANSFER_ICON);
        utilityPanel.add(transferFileButton);

        bar.setStringPainted(true);
        bar.setValue(0);
        utilityPanel.add(bar);
        transferFileButton.addActionListener(new SendFileListener(bar, desName, this));
        mainPanel.add(utilityPanel);

        JPanel emojiPanel = new JPanel();
        emojiPanel.setBorder(BorderFactory.createLoweredBevelBorder());
        emojiPanel.setBackground(Color.white);
        emojiPanel.setLayout(new GridLayout(0, 10));
        mainPanel.add(emojiPanel);

        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new BoxLayout(inputPanel, BoxLayout.X_AXIS));
        chatInputField = new JTextField();
        chatInputField.setBorder(BorderFactory.createLoweredBevelBorder());
        chatInputField.setColumns(20);

        sendButton = new JButton("Gửi");
        sendButton.addActionListener(new SendMessageListener(desName, chatInputField));
        sendButton.setMnemonic(KeyEvent.VK_ENTER);

        inputPanel.add(new JScrollPane(chatInputField));
        inputPanel.add(sendButton);

        createEmojiButtonsForPanel(emojiPanel);
        mainPanel.add(inputPanel);
    }

    public void addMessage(String displayName, String message) {

        allMessages.append(displayName).append(": ").append(message).append("\n");
        displayTextPanel.setText(allMessages.toString());
    }

    public JProgressBar getBar() {
        return bar;
    }

    private void createEmojiButtonsForPanel(JPanel panel) {

        for (String emoji : Client.emojiList) {
            JButton button = new JButton(emoji);
            button.addActionListener(new EmojiListener(chatInputField, emoji));
            panel.add(button);
        }
    }
}
