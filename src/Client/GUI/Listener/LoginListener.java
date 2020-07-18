package Client.GUI.Listener;

import Client.Client;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

/**
 * Client
 *
 * @Created by Long - StudentID : 18120455
 * @Date 07/07/2020 - 11:18 PM
 * @Description
 **/
public class LoginListener implements ActionListener {

    private final JTextField userName;
    private final JPasswordField password;

    public LoginListener(JTextField userName, JPasswordField password) {
        this.userName = userName;
        this.password = password;
    }

    @Override
    public void actionPerformed(ActionEvent e) {

        try {

            if (Client.isInputInvalid(userName.getText()) || Client.isInputInvalid(String.valueOf(password.getPassword()))) {
                JOptionPane.showMessageDialog(null, "Không được điền ít hơn " +
                                Client.MIN_LENGTH + " kí tự hoặc chứa ký tự đặc biệt!",
                        "Register error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            Client.sendLoginCommandToServer(userName.getText(), String.valueOf(password.getPassword()));

        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
    }
}
