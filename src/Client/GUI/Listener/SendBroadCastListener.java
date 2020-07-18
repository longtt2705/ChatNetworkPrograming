package Client.GUI.Listener;

import Client.Client;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

/**
 * Client.GUI
 *
 * @Created by Long - StudentID : 18120455
 * @Date 10/07/2020 - 8:37 PM
 * @Description
 **/
public class SendBroadCastListener implements ActionListener {

    private final JTextField textField;

    public SendBroadCastListener(JTextField textField) {
        this.textField = textField;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        try {

            String text = textField.getText().trim();
            if (text.length() == 0)
                return;

            Client.sendMessageCommandToServer("ALL", text);
            textField.setText("");
            textField.requestFocus();
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
    }
}
