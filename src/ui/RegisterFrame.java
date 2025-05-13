package ui;

import service.NotificationService;
import service.UserService;
import util.FileHandler;

import javax.swing.*;
import java.awt.*;

public class RegisterFrame extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JComboBox<String> roleCombo;
    private UserService userService;
    private NotificationService notificationService;

    public RegisterFrame() {
        userService = new UserService(new FileHandler<>());
        notificationService = new NotificationService(new FileHandler<>());
        setTitle("Register");
        setSize(400, 400);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
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
        add(new JLabel("Role:"));
        roleCombo = new JComboBox<>(new String[]{"Customer", "ProjectManager", "ServiceProvider", "Admin"});
        add(roleCombo);
        JButton registerButton = new JButton("Register");
        registerButton.addActionListener(e -> register());
        add(registerButton);
    }

    private void register() {
        String username = usernameField.getText();
        String password = new String(passwordField.getPassword());
        String role = (String) roleCombo.getSelectedItem();

        if (userService.register(username, password, role)) {
            notificationService.sendNotification(username, "Registration Successful",
                    "Welcome to Event Management System!\nUsername: " + username + "\nPassword: " + password);
            JOptionPane.showMessageDialog(this, "Registration successful! Check notifications.txt.");
            dispose();
        } else {
            JOptionPane.showMessageDialog(this, "Username already exists!");
        }
    }
}