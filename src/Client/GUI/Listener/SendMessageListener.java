package Client.GUI.Listener;

import Client.Client;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

/**
 * Client.GUI
 *
 * @Created by Long - StudentID : 18120455
 * @Date 13/07/2020 - 5:15 PM
 * @Description
 **/
public class SendMessageListener implements ActionListener {

    private final String userName;
    private final JTextField textField;

    public SendMessageListener(String userName, JTextField textField) {
        this.userName = userName;
        this.textField = textField;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        try {

            String text = textField.getText().trim();
            if (text.length() == 0)
                return;

            Client.sendMessageCommandToServer(userName, text);
            textField.setText("");
            textField.requestFocus();
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
    }
}
