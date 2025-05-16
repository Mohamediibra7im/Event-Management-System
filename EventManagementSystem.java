import javax.swing.*;
import java.awt.*;
import java.util.List;

public class EventManagementSystem {
    public static void main(String[] args) {
        JFrame frame = new JFrame("Event Management System");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 300);
        frame.setLayout(new BorderLayout(10, 10));

        JTabbedPane tabbedPane = new JTabbedPane();

        // Login Tab
        JPanel loginPanel = createLoginPanel(frame);
        tabbedPane.addTab("Login", loginPanel);

        // Register Tab
        JPanel registerPanel = createRegisterPanel(frame);
        tabbedPane.addTab("Register", registerPanel);

        frame.add(tabbedPane, BorderLayout.CENTER);
        frame.setVisible(true);
    }

    private static JPanel createLoginPanel(JFrame frame) {
        JPanel loginPanel = new JPanel(new GridLayout(4, 2, 10, 10));
        loginPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel usernameLabel = new JLabel("Username:");
        JTextField usernameField = new JTextField();
        JLabel passwordLabel = new JLabel("Password:");
        JPasswordField passwordField = new JPasswordField();
        JButton loginButton = new JButton("Login");

        loginPanel.add(usernameLabel);
        loginPanel.add(usernameField);
        loginPanel.add(passwordLabel);
        loginPanel.add(passwordField);
        loginPanel.add(new JLabel());
        loginPanel.add(loginButton);
        loginPanel.add(new JLabel());
        loginPanel.add(new JLabel()); // Spacer

        loginButton.addActionListener(e -> {
            String username = usernameField.getText().trim();
            String password = new String(passwordField.getPassword()).trim();

            // Validate inputs
            if (username.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(frame, "Username and password cannot be empty.");
                return;
            }

            List<User> users = DataHandler.loadUsers();
            // Debug: print loaded users
            System.out.println("Loaded users (count: " + users.size() + "):");
            if (users.isEmpty()) {
                System.out.println("No users loaded from file!");
            }
            for (User user : users) {
                System.out.println("Username: " + user.getUsername() + ", Role: " + user.getRole());
            }

            // Authenticate user
            for (User user : users) {
                if (user.getUsername().equals(username) && user.getPassword().equals(password)) {
                    JOptionPane.showMessageDialog(frame, "Login successful! Welcome, " + username + " (" + user.getRole() + ").");
                    usernameField.setText("");
                    passwordField.setText("");
                    // Show the dashboard for the logged-in user
                    user.showDashboard();
                    return;
                }
            }

            System.out.println("Authentication failed for username: " + username);
            JOptionPane.showMessageDialog(frame, "Invalid username or password.");
        });

        return loginPanel;
    }

    private static JPanel createRegisterPanel(JFrame frame) {
        JPanel registerPanel = new JPanel(new GridLayout(5, 2, 10, 10));
        registerPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel regUsernameLabel = new JLabel("Username:");
        JTextField regUsernameField = new JTextField();
        JLabel regPasswordLabel = new JLabel("Password:");
        JPasswordField regPasswordField = new JPasswordField();
        JLabel regEmailLabel = new JLabel("Email:");
        JTextField regEmailField = new JTextField();
        JLabel roleLabel = new JLabel("Role:");
        JComboBox<String> roleCombo = new JComboBox<>(new String[]{"Customer", "ProjectManager", "ServiceProvider", "Admin"});
        JButton submitButton = new JButton("Register");

        registerPanel.add(regUsernameLabel);
        registerPanel.add(regUsernameField);
        registerPanel.add(regPasswordLabel);
        registerPanel.add(regPasswordField);
        registerPanel.add(regEmailLabel);
        registerPanel.add(regEmailField);
        registerPanel.add(roleLabel);
        registerPanel.add(roleCombo);
        registerPanel.add(new JLabel());
        registerPanel.add(submitButton);

        submitButton.addActionListener(e -> {
            String username = regUsernameField.getText().trim();
            String password = new String(regPasswordField.getPassword()).trim();
            String email = regEmailField.getText().trim();
            String role = (String) roleCombo.getSelectedItem();

            // Validate inputs
            if (username.isEmpty() || password.isEmpty() || email.isEmpty()) {
                JOptionPane.showMessageDialog(frame, "Username, password, and email cannot be empty.");
                return;
            }

            User user;
            switch (role) {
                case "Customer":
                    user = new Customer(username, password); 
                    break;
                case "ProjectManager":
                    user = new ProjectManager(username, password); 
                    break;
                case "ServiceProvider":
                    user = new ServiceProvider(username, password); 
                    break;
                case "Admin":
                    user = new Admin(username, password); 
                    break;
                default:
                    JOptionPane.showMessageDialog(frame, "Invalid role selected.");
                    return;
            }

            if (DataHandler.saveUser(user)) {
                JOptionPane.showMessageDialog(frame, "User registered successfully!");
                regUsernameField.setText("");
                regPasswordField.setText("");
                regEmailField.setText("");
            } else {
                JOptionPane.showMessageDialog(frame, "Registration failed: Username already exists.");
            }
        });

        return registerPanel;
    }
}
