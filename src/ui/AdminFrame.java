package ui;

import java.awt.*;
import java.util.List;
import java.util.stream.Collectors;
import javax.swing.*;
import model.Event;
import model.User;
import service.EventService;
import service.NotificationService;
import service.UserService;
import util.FileHandler;

public class AdminFrame extends JFrame {
    // Removed unused field 'user'
    private JTextArea userDetailsArea, eventDetailsArea;
    private final UserService userService;
    private final EventService eventService;
    private final NotificationService notificationService;

    public AdminFrame(User user) {
        // Removed assignment to unused field 'user'
        userService = new UserService(new FileHandler<>());
        eventService = new EventService(new FileHandler<>());
        notificationService = new NotificationService(new FileHandler<>());
        setTitle("Admin Dashboard - " + user.getUsername());
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        initComponents();
        updateUserDetails();
        updateEventDetails();
        setVisible(true);
    }

    private void initComponents() {
        JTabbedPane tabbedPane = new JTabbedPane();

        // View Users Tab
        JPanel viewUsersPanel = new JPanel(new BorderLayout());
        userDetailsArea = new JTextArea();
        userDetailsArea.setEditable(false);
        viewUsersPanel.add(new JScrollPane(userDetailsArea), BorderLayout.CENTER);
        JButton refreshUsersButton = new JButton("Refresh Users");
        refreshUsersButton.addActionListener(e -> updateUserDetails());
        viewUsersPanel.add(refreshUsersButton, BorderLayout.SOUTH);
        tabbedPane.addTab("View Users", viewUsersPanel);

        // View Events Tab
        JPanel viewEventsPanel = new JPanel(new BorderLayout());
        eventDetailsArea = new JTextArea();
        eventDetailsArea.setEditable(false);
        viewEventsPanel.add(new JScrollPane(eventDetailsArea), BorderLayout.CENTER);
        JButton refreshEventsButton = new JButton("Refresh Events");
        refreshEventsButton.addActionListener(e -> updateEventDetails());
        viewEventsPanel.add(refreshEventsButton, BorderLayout.SOUTH);
        tabbedPane.addTab("View Events", viewEventsPanel);

        // Add User Tab
        JPanel addUserPanel = new JPanel(new GridLayout(4, 2, 10, 10));
        JTextField addUsernameField = new JTextField();
        JPasswordField addPasswordField = new JPasswordField();
        JComboBox<String> addRoleCombo = new JComboBox<>(new String[]{"Customer", "ProjectManager", "ServiceProvider", "Admin"});
        addUserPanel.add(new JLabel("Username:"));
        addUserPanel.add(addUsernameField);
        addUserPanel.add(new JLabel("Password:"));
        addUserPanel.add(addPasswordField);
        addUserPanel.add(new JLabel("Role:"));
        addUserPanel.add(addRoleCombo);
        JButton addUserButton = new JButton("Add User");
        addUserButton.addActionListener(e -> addUser(addUsernameField, addPasswordField, addRoleCombo));
        addUserPanel.add(addUserButton);
        tabbedPane.addTab("Add User", addUserPanel);

        // Update User Tab
        JPanel updateUserPanel = new JPanel(new GridLayout(4, 2, 10, 10));
        JTextField updateUsernameField = new JTextField();
        JTextField updatePasswordField = new JTextField();
        JTextField updateRoleField = new JTextField();
        updateUserPanel.add(new JLabel("Username:"));
        updateUserPanel.add(updateUsernameField);
        updateUserPanel.add(new JLabel("New Password:"));
        updateUserPanel.add(updatePasswordField);
        updateUserPanel.add(new JLabel("New Role:"));
        updateUserPanel.add(updateRoleField);
        JButton updateUserButton = new JButton("Update User");
        updateUserButton.addActionListener(e -> updateUser(updateUsernameField, updatePasswordField, updateRoleField));
        updateUserPanel.add(updateUserButton);
        tabbedPane.addTab("Update User", updateUserPanel);

        // Delete User Tab
        JPanel deleteUserPanel = new JPanel(new GridLayout(2, 2, 10, 10));
        JTextField deleteUsernameField = new JTextField();
        deleteUserPanel.add(new JLabel("Username:"));
        deleteUserPanel.add(deleteUsernameField);
        JButton deleteUserButton = new JButton("Delete User");
        deleteUserButton.addActionListener(e -> deleteUser(deleteUsernameField));
        deleteUserPanel.add(deleteUserButton);
        tabbedPane.addTab("Delete User", deleteUserPanel);

        // Assign to PM Tab
        JPanel assignPMPanel = new JPanel(new GridLayout(4, 2, 10, 10));
        JComboBox<String> assignEventIdCombo = new JComboBox<>();
        JComboBox<String> pmUsernameCombo = new JComboBox<>();
        updateEventCombo(assignEventIdCombo);
        updatePMCombo(pmUsernameCombo);
        assignPMPanel.add(new JLabel("Event ID:"));
        assignPMPanel.add(assignEventIdCombo);
        assignPMPanel.add(new JLabel("Project Manager:"));
        assignPMPanel.add(pmUsernameCombo);
        JButton assignPMButton = new JButton("Assign to PM");
        assignPMButton.addActionListener(e -> assignToPM(assignEventIdCombo, pmUsernameCombo));
        assignPMPanel.add(assignPMButton);
        JButton autoAssignButton = new JButton("Auto-Assign Events");
        autoAssignButton.addActionListener(e -> autoAssignEvents());
        assignPMPanel.add(autoAssignButton);
        tabbedPane.addTab("Assign to PM", assignPMPanel);

        add(tabbedPane, BorderLayout.CENTER);

        // Logout Button
        JPanel bottomPanel = new JPanel();
        JButton logoutButton = new JButton("Logout");
        logoutButton.addActionListener(e -> {
            dispose();
            LoginFrame loginFrame = new LoginFrame();
            loginFrame.setVisible(true);
        });
        bottomPanel.add(logoutButton);
        add(bottomPanel, BorderLayout.SOUTH);
    }

    private void updateUserDetails() {
        List<User> users = userService.getAllUsers();
        users.sort((u1, u2) -> u1.getUsername().compareToIgnoreCase(u2.getUsername())); // Sort users alphabetically
        StringBuilder sb = new StringBuilder();
        for (User u : users) {
            sb.append("Username: ").append(u.getUsername())
                    .append(", Role: ").append(u.getRole())
                    .append("\n");
        }
        userDetailsArea.setText(sb.length() > 0 ? sb.toString() : "No users found.");
    }

    private void updateEventDetails() {
        List<Event> events = eventService.getAllEvents();
        StringBuilder sb = new StringBuilder();
        for (Event event : events) {
            sb.append("Event ID: ").append(event.getEventId())
                    .append(", Customer: ").append(event.getCustomerUsername())
                    .append(", Type: ").append(event.getEventType())
                    .append(", Date: ").append(event.getDate())
                    .append(", Venue: ").append(event.getVenue())
                    .append(", Budget: ").append(event.getBudget())
                    .append(", Status: ").append(event.getStatus())
                    .append("\n");
        }
        eventDetailsArea.setText(sb.length() > 0 ? sb.toString() : "No events found.");
    }

    private void updateEventCombo(JComboBox<String> eventIdCombo) {
        eventIdCombo.removeAllItems();
        List<Event> events = eventService.getEventsByStatus("Pending");
        for (Event event : events) {
            eventIdCombo.addItem(event.getEventId());
        }
    }

    private void updatePMCombo(JComboBox<String> pmUsernameCombo) {
        pmUsernameCombo.removeAllItems();
        List<User> users = userService.getAllUsers();
        for (User user : users) {
            if (user.getRole().equals("ProjectManager")) {
                pmUsernameCombo.addItem(user.getUsername());
            }
        }
    }

    private void addUser(JTextField usernameField, JPasswordField passwordField, JComboBox<String> roleCombo) {
        String username = usernameField.getText();
        String password = new String(passwordField.getPassword());
        String role = (String) roleCombo.getSelectedItem();

        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Username and password are required!");
            return;
        }

        if (userService.register(username, password, role)) {
            notificationService.sendNotification(username, "Registration Successful",
                    "Welcome to Event Management System!\nUsername: " + username + "\nPassword: " + password);
            JOptionPane.showMessageDialog(this, "User added successfully!");
            updateUserDetails();
            usernameField.setText("");
            passwordField.setText("");
        } else {
            JOptionPane.showMessageDialog(this, "Username already exists!");
        }
    }

    private void updateUser(JTextField usernameField, JTextField passwordField, JTextField roleField) {
        String username = usernameField.getText();
        String newPassword = passwordField.getText();
        String newRole = roleField.getText();

        if (username.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Username is required!");
            return;
        }

        if (userService.updateUser(username, newPassword, newRole)) {
            JOptionPane.showMessageDialog(this, "User updated!");
            updateUserDetails();
            usernameField.setText("");
            passwordField.setText("");
            roleField.setText("");
        } else {
            JOptionPane.showMessageDialog(this, "User not found!");
        }
    }

    private void deleteUser(JTextField usernameField) {
        String username = usernameField.getText();
        if (username.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Username is required!");
            return;
        }
        if (userService.deleteUser(username)) {
            JOptionPane.showMessageDialog(this, "User deleted!");
            updateUserDetails();
            usernameField.setText("");
        } else {
            JOptionPane.showMessageDialog(this, "User not found!");
        }
    }

    private void assignToPM(JComboBox<String> eventIdCombo, JComboBox<String> pmUsernameCombo) {
        String eventId = (String) eventIdCombo.getSelectedItem();
        String pmUsername = (String) pmUsernameCombo.getSelectedItem();
        if (eventId == null || pmUsername == null) {
            JOptionPane.showMessageDialog(this, "Please select an Event ID and Project Manager!");
            return;
        }
        String result = eventService.assignToPM(eventId, pmUsername);
        if (result.equals("Event assigned to Project Manager!")) {
            notificationService.sendNotification(pmUsername, "New Event Assignment",
                    "You have been assigned an event!\nEvent ID: " + eventId);
        }
        JOptionPane.showMessageDialog(this, result);
        updateEventDetails();
        updateEventCombo(eventIdCombo);
    }

    private void autoAssignEvents() {
        List<User> users = userService.getAllUsers();
        List<String> pmUsernames = users.stream()
                .filter(u -> u.getRole().equals("ProjectManager"))
                .map(User::getUsername)
                .collect(Collectors.toList());
        int assignedCount = eventService.autoAssignEvents(pmUsernames);
        if (assignedCount == 0) {
            JOptionPane.showMessageDialog(this, pmUsernames.isEmpty() ? "No Project Managers available!" :
                    "No unassigned events found!");
        } else {
            List<Event> assignedEvents = eventService.getEventsByStatus("Assigned to PM");
            for (Event event : assignedEvents) {
                notificationService.sendNotification(event.getCustomerUsername(), "New Event Assignment",
                        "Your event has been assigned to a Project Manager!\nEvent ID: " + event.getEventId());
            }
            JOptionPane.showMessageDialog(this, assignedCount + " events assigned to Project Managers!");
            updateEventDetails();
            updateEventCombo(new JComboBox<>());
        }
    }
}