package Client.GUI;

import Client.GUI.Listener.LoginListener;
import Client.GUI.Listener.RegisterListener;
import Client.Utilities.*;

import javax.swing.*;
import java.awt.*;

/**
 * Client
 *
 * @Created by Long - StudentID : 18120455
 * @Date 07/07/2020 - 11:49 AM
 * @Description
 **/
public class LoginGUI extends JFrame {

    private final Container container;
    private final JLabel userLabel;
    private final JLabel passwordLabel;
    private final JTextField userTextField;
    private final JPasswordField passwordField;
    private final JButton loginButton;
    private final JButton registerButton;
    private final JLabel logo;
    private SpringLayout springLayout;

    private static final int TEXT_FIELD_COLUMN_NUMBER = 12;

    public LoginGUI() {

        container = getContentPane();
        userLabel = new JLabel("Tên tài khoản:");
        passwordLabel = new JLabel("Mật khẩu:");
        userTextField = new JTextField();
        passwordField = new JPasswordField();
        loginButton = new JButton("Đăng nhập");
        registerButton = new JButton("Đăng ký");
        logo = Utilities.getImage(Utilities.LOGO_ICON, true);
    }

    public void createAndShowGUI() {

        JFrame.setDefaultLookAndFeelDecorated(false);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setTitle("Đăng nhập");
        setVisible(true);

        //Calling methods inside constructor.
        setLayoutManager();
        addComponentsToContainer();
        setLocationAndSize();
        addListenerToComponents();

        setPreferredSize(new Dimension(250, 300));
        setResizable(false);
        pack();
    }
    public void setLayoutManager() {

        springLayout = new SpringLayout();
        container.setLayout(springLayout);
    }

    public void setLocationAndSize() {
        //Setting location and Size of each components.
        // Logo
        springLayout.putConstraint(SpringLayout.HORIZONTAL_CENTER, logo, 0, SpringLayout.HORIZONTAL_CENTER, container);
        springLayout.putConstraint(SpringLayout.NORTH, logo, 3 * Utilities.SPACE_SIZE, SpringLayout.NORTH, container);

        // userLabel
        springLayout.putConstraint(SpringLayout.WEST, userLabel, 2 * Utilities.SPACE_SIZE, SpringLayout.WEST, container);
        springLayout.putConstraint(SpringLayout.NORTH, userLabel, 2 * Utilities.SPACE_SIZE, SpringLayout.SOUTH, logo);

        // userTextField
        userTextField.setColumns(TEXT_FIELD_COLUMN_NUMBER);
        springLayout.putConstraint(SpringLayout.WEST, userTextField, Utilities.SPACE_SIZE, SpringLayout.EAST, userLabel);
        springLayout.putConstraint(SpringLayout.NORTH, userTextField, 0, SpringLayout.NORTH, userLabel);

        // passwordField
        passwordField.setColumns(TEXT_FIELD_COLUMN_NUMBER);
        springLayout.putConstraint(SpringLayout.WEST, passwordField, 0, SpringLayout.WEST, userTextField);
        springLayout.putConstraint(SpringLayout.NORTH, passwordField, Utilities.SPACE_SIZE, SpringLayout.SOUTH, userTextField);

        // passwordLabel
        springLayout.putConstraint(SpringLayout.WEST, passwordLabel, 0, SpringLayout.WEST, userLabel);
        springLayout.putConstraint(SpringLayout.NORTH, passwordLabel, 0, SpringLayout.NORTH, passwordField);

        // loginButton
        springLayout.putConstraint(SpringLayout.HORIZONTAL_CENTER, loginButton, 0, SpringLayout.HORIZONTAL_CENTER, container);
        springLayout.putConstraint(SpringLayout.NORTH, loginButton, 3 * Utilities.SPACE_SIZE, SpringLayout.SOUTH, passwordField);

        // registerButton
        springLayout.putConstraint(SpringLayout.HORIZONTAL_CENTER, registerButton, 0, SpringLayout.HORIZONTAL_CENTER, container);
        springLayout.putConstraint(SpringLayout.NORTH, registerButton, Utilities.SPACE_SIZE, SpringLayout.SOUTH, loginButton);
    }

    public void addComponentsToContainer() {
        //Adding each components to the Container
        container.add(logo);
        container.add(userLabel);
        container.add(passwordLabel);
        container.add(userTextField);
        container.add(passwordField);
        container.add(loginButton);
        container.add(registerButton);
    }

    public void addListenerToComponents() {
        registerButton.addActionListener(new RegisterListener());
        loginButton.addActionListener(new LoginListener(userTextField, passwordField));
    }
}
