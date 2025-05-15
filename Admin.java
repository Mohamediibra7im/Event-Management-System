import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.util.*;
import java.util.List;

public class Admin extends User {
    public Admin(String username, String password) {
        super(username, password, "Admin");
    }

    @Override
    public void showDashboard() {
        JFrame frame = new JFrame("Admin Dashboard");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(700, 500);
        frame.setLayout(new BorderLayout());

        JTabbedPane tabbedPane = new JTabbedPane();

        // Manage Users Tab
        tabbedPane.addTab("Manage Users", createManageUsersPanel());

        // Assign Events Tab
        tabbedPane.addTab("Assign Events to PM", createAssignEventsPanel());

        // View All Events Tab
        tabbedPane.addTab("View All Events", createViewEventsPanel());

        // Logout Tab
        JPanel logoutPanel = new JPanel(new BorderLayout());
        JButton logoutButton = new JButton("Logout");
        logoutButton.addActionListener(e -> frame.dispose());
        logoutPanel.add(logoutButton, BorderLayout.CENTER);
        tabbedPane.addTab("Logout", logoutPanel);

        frame.add(tabbedPane, BorderLayout.CENTER);
        frame.setVisible(true);
    }

    private JPanel createManageUsersPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        // Table for users
        List<User> users = DataHandler.loadUsers();
        String[] columnNames = {"Username", "Role"};
        Object[][] data = new Object[users.size()][2];
        for (int i = 0; i < users.size(); i++) {
            data[i][0] = users.get(i).getUsername();
            data[i][1] = users.get(i).getRole();
        }
        JTable table = new JTable(data, columnNames);
        JScrollPane tableScroll = new JScrollPane(table);

        JPanel inputPanel = new JPanel(new GridLayout(3, 2));
        JLabel usernameLabel = new JLabel("Username:");
        JTextField usernameField = new JTextField();
        JLabel roleLabel = new JLabel("Role:");
        JComboBox<String> roleCombo = new JComboBox<>(new String[]{"Customer", "ProjectManager", "ServiceProvider", "Admin"});
        JButton updateRoleButton = new JButton("Update Role");
        JButton deleteButton = new JButton("Delete User");

        inputPanel.add(usernameLabel);
        inputPanel.add(usernameField);
        inputPanel.add(roleLabel);
        inputPanel.add(roleCombo);
        inputPanel.add(updateRoleButton);
        inputPanel.add(deleteButton);

        panel.add(tableScroll, BorderLayout.CENTER);
        panel.add(inputPanel, BorderLayout.SOUTH);

        // When a row is selected, fill the fields
        table.getSelectionModel().addListSelectionListener(e -> {
            int selectedRow = table.getSelectedRow();
            if (selectedRow != -1) {
                usernameField.setText((String) table.getValueAt(selectedRow, 0));
                roleCombo.setSelectedItem(table.getValueAt(selectedRow, 1));
            }
        });

        updateRoleButton.addActionListener(e -> {
            String username = usernameField.getText().trim();
            String newRole = (String) roleCombo.getSelectedItem();
            if (username.isEmpty()) {
                JOptionPane.showMessageDialog(panel, "Please select a user to update.");
                return;
            }
            List<User> usersList = DataHandler.loadUsers();
            boolean updated = false;
            for (User user : usersList) {
                if (user.getUsername().equals(username)) {
                    // Create a new user object with the new role and same username/password
                    User updatedUser;
                    switch (newRole) {
                        case "Customer":
                            updatedUser = new Customer(user.getUsername(), user.getPassword());
                            break;
                        case "ProjectManager":
                            updatedUser = new ProjectManager(user.getUsername(), user.getPassword());
                            break;
                        case "ServiceProvider":
                            updatedUser = new ServiceProvider(user.getUsername(), user.getPassword());
                            break;
                        case "Admin":
                            updatedUser = new Admin(user.getUsername(), user.getPassword());
                            break;
                        default:
                            return;
                    }
                    // Replace user in list
                    usersList.set(usersList.indexOf(user), updatedUser);
                    updated = true;
                    break;
                }
            }
            if (updated) {
                // Save all users
                try (BufferedWriter writer = new BufferedWriter(new FileWriter("users.txt"))) {
                    for (User user : usersList) {
                        writer.write(user.getUsername() + "," + user.getRole() + "," + user.getPassword() + "\n");
                    }
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
                JOptionPane.showMessageDialog(panel, "User role updated.");
                // Refresh table
                refreshUsersTable(table);
            } else {
                JOptionPane.showMessageDialog(panel, "User not found.");
            }
        });

        deleteButton.addActionListener(e -> {
            String username = usernameField.getText().trim();
            if (username.isEmpty()) {
                JOptionPane.showMessageDialog(panel, "Please select a user to delete.");
                return;
            }
            List<User> usersList = DataHandler.loadUsers();
            boolean removed = usersList.removeIf(user -> user.getUsername().equals(username));
            if (removed) {
                try (BufferedWriter writer = new BufferedWriter(new FileWriter("users.txt"))) {
                    for (User user : usersList) {
                        writer.write(user.getUsername() + "," + user.getRole() + "," + user.getPassword() + "\n");
                    }
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
                JOptionPane.showMessageDialog(panel, "User deleted.");
                // Refresh table
                refreshUsersTable(table);
            } else {
                JOptionPane.showMessageDialog(panel, "User not found.");
            }
        });

        return panel;
    }

    // Helper to refresh the users table after update/delete
    private void refreshUsersTable(JTable table) {
        List<User> users = DataHandler.loadUsers();
        String[][] data = new String[users.size()][2];
        for (int i = 0; i < users.size(); i++) {
            data[i][0] = users.get(i).getUsername();
            data[i][1] = users.get(i).getRole();
        }
        String[] columnNames = {"Username", "Role"};
        table.setModel(new javax.swing.table.DefaultTableModel(data, columnNames));
    }

    private JPanel createAssignEventsPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        JTextArea eventsArea = new JTextArea();
        eventsArea.setEditable(false);
        List<Event> events = DataHandler.loadEvents();
        for (Event event : events) {
            eventsArea.append("ID: " + event.getEventId() + ", Customer: " + event.getCustomerUsername() +
                    ", Details: " + event.getDetails() + ", Assigned PM: " + event.getAssignedPM() + "\n");
        }

        JPanel inputPanel = new JPanel(new GridLayout(2, 2));
        JLabel eventIdLabel = new JLabel("Event ID:");
        JTextField eventIdField = new JTextField();
        JLabel pmLabel = new JLabel("Project Manager Username:");
        JTextField pmField = new JTextField();
        JButton assignButton = new JButton("Assign");

        inputPanel.add(eventIdLabel);
        inputPanel.add(eventIdField);
        inputPanel.add(pmLabel);
        inputPanel.add(pmField);
        inputPanel.add(new JLabel());
        inputPanel.add(assignButton);

        panel.add(new JScrollPane(eventsArea), BorderLayout.CENTER);
        panel.add(inputPanel, BorderLayout.SOUTH);

        assignButton.addActionListener(e -> {
            String eventId = eventIdField.getText();
            String pmUsername = pmField.getText();
            for (Event event : events) {
                if (event.getEventId().equals(eventId)) {
                    event.setAssignedPM(pmUsername);
                    event.setStatus("Assigned");
                    DataHandler.updateEvent(event);
                    eventsArea.setText("");
                    for (Event ev : events) {
                        eventsArea.append("ID: " + ev.getEventId() + ", Customer: " + ev.getCustomerUsername() +
                                ", Details: " + ev.getDetails() + ", Assigned PM: " + ev.getAssignedPM() + "\n");
                    }
                    JOptionPane.showMessageDialog(panel, "Event assigned to " + pmUsername);
                    return;
                }
            }
            JOptionPane.showMessageDialog(panel, "Event not found.");
        });

        return panel;
    }

    private JPanel createViewEventsPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        List<Event> events = DataHandler.loadEvents();
        String[] columnNames = {"Event ID", "Customer", "Details", "Status", "Assigned PM"};
        Object[][] data = new Object[events.size()][5];
        for (int i = 0; i < events.size(); i++) {
            Event event = events.get(i);
            data[i][0] = event.getEventId();
            data[i][1] = event.getCustomerUsername();
            data[i][2] = event.getDetails();
            data[i][3] = event.getStatus();
            data[i][4] = event.getAssignedPM();
        }
        JTable table = new JTable(data, columnNames);
        JScrollPane scrollPane = new JScrollPane(table);

        panel.add(scrollPane, BorderLayout.CENTER);
        return panel;
    }
}