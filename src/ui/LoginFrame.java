package ui;

import model.User;
import service.UserService;
import util.FileHandler;

import javax.swing.*;
import java.awt.*;

public class LoginFrame extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private UserService userService;

    public LoginFrame() {
        userService = new UserService(new FileHandler<>());
        setTitle("Event Management System - Login");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new GridLayout(4, 2, 10, 10));
        initComponents();
        setVisible(true);
    }

    private void initComponents() {
        add(new JLabel("Username:"));
        usernameField = new JTextField();
        add(usernameField);
        add(new JLabel("Password:"));
        passwordField = new JPasswordField();
        add(passwordField);
        JButton loginButton = new JButton("Login");
        loginButton.addActionListener(e -> login());
        add(loginButton);
        JButton registerButton = new JButton("Register");
        registerButton.addActionListener(e -> new RegisterFrame());
        add(registerButton);
    }

    private void login() {
        String username = usernameField.getText();
        String password = new String(passwordField.getPassword());
        User user = userService.login(username, password);
        if (user != null) {
            dispose();
            switch (user.getRole()) {
                case "Customer":
                    new CustomerFrame(user);
                    break;
                case "ProjectManager":
                    new ProjectManagerFrame(user);
                    break;
                case "ServiceProvider":
                    new ServiceProviderFrame(user);
                    break;
                case "Admin":
                    new AdminFrame(user);
                    break;
            }
        } else {
            JOptionPane.showMessageDialog(this, "Invalid credentials!");
        }
    }
}