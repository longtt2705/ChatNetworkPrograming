package Client.GUI.Listener;

import Client.Client;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

/**
 * Client
 *
 * @Created by Long - StudentID : 18120455
 * @Date 07/07/2020 - 11:22 PM
 * @Description
 **/
public class RegisterListener implements ActionListener {

    @Override
    public void actionPerformed(ActionEvent e) {

        JTextField userName = new JTextField();
        JTextField password = new JPasswordField();

        JPanel panel = new JPanel(new GridLayout(0, 1));
        panel.add(new JLabel("Tên Đăng nhập:"));
        panel.add(userName);
        panel.add(new JLabel("Mật khẩu:"));
        panel.add(password);

        int result = JOptionPane.showConfirmDialog(new JFrame(), panel,
                "Đăng ký tài khoản", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            try {

                if (Client.isInputInvalid(userName.getText()) || Client.isInputInvalid(password.getText()))
                {
                    JOptionPane.showMessageDialog(null, "Không được điền ít hơn " +
                                    Client.MIN_LENGTH + " kí tự hoặc chứa ký tự đặc biệt!",
                            "Register error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                Client.sendRegisterCommandToServer(userName.getText(), password.getText());
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        }

    }
}
