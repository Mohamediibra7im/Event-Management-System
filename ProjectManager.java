import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.List;

public class ProjectManager extends User {
    public ProjectManager(String username, String password) {
        super(username, password, "ProjectManager");
    }

    @Override
    public void showDashboard() {
        JFrame frame = new JFrame("Project Manager Dashboard");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(700, 500);
        frame.setLayout(new BorderLayout());

        JTabbedPane tabbedPane = new JTabbedPane();

        // Assigned Events Tab
        tabbedPane.addTab("Assigned Events", createViewEventsPanel());

        // Contact Service Provider Tab
        tabbedPane.addTab("Contact Service Provider", createContactSPPanel());

        // Contact Customer Tab
        tabbedPane.addTab("Contact Customer", createContactCustomerPanel());

        // Logout Tab
        JPanel logoutPanel = new JPanel(new BorderLayout());
        JButton logoutButton = new JButton("Logout");
        logoutButton.addActionListener(e -> frame.dispose());
        logoutPanel.add(logoutButton, BorderLayout.CENTER);
        tabbedPane.addTab("Logout", logoutPanel);

        frame.add(tabbedPane, BorderLayout.CENTER);
        frame.setVisible(true);
    }

    private JPanel createViewEventsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        List<Event> events = DataHandler.loadEvents();
        List<Event> assignedEvents = new ArrayList<>();
        for (Event event : events) {
            if (event.getAssignedPM().equals(getUsername())) {
                assignedEvents.add(event);
            }
        }
        String[] columnNames = {"Event ID", "Customer", "Details", "Status", "Price", "Ready Date"};
        Object[][] data = new Object[assignedEvents.size()][6];
        for (int i = 0; i < assignedEvents.size(); i++) {
            Event event = assignedEvents.get(i);
            data[i][0] = event.getEventId();
            data[i][1] = event.getCustomerUsername();
            data[i][2] = event.getDetails();
            data[i][3] = event.getStatus();
            data[i][4] = event.getPrice();
            data[i][5] = event.getReadyDate();
        }
        JTable table = new JTable(data, columnNames);
        JScrollPane scrollPane = new JScrollPane(table);
        panel.add(scrollPane, BorderLayout.CENTER);

        JButton sendPriceButton = new JButton("Send Price to Customer");
        panel.add(sendPriceButton, BorderLayout.SOUTH);

        sendPriceButton.addActionListener(e -> {
            int selectedRow = table.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(panel, "Please select an event from the table.");
                return;
            }
            String eventId = (String) table.getValueAt(selectedRow, 0);
            for (Event event : assignedEvents) {
                if (event.getEventId().equals(eventId) && event.getAssignedPM().equals(getUsername())) {
                    event.setStatus("Price Set");
                    DataHandler.updateEvent(event);
                    JOptionPane.showMessageDialog(panel, "Price sent to customer.");
                    // Optionally refresh the table here
                    return;
                }
            }
            JOptionPane.showMessageDialog(panel, "Event not found or not assigned to you.");
        });

        return panel;
    }

    private JPanel createContactSPPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        List<User> users = DataHandler.loadUsers();
        List<String> spUsernames = new ArrayList<>();
        for (User user : users) {
            if ("ServiceProvider".equals(user.getRole())) {
                spUsernames.add(user.getUsername());
            }
        }
        JComboBox<String> spCombo = new JComboBox<>(spUsernames.toArray(new String[0]));
        JTextArea chatArea = new JTextArea();
        chatArea.setEditable(false);
        JTextField messageField = new JTextField();
        JButton sendButton = new JButton("Send");

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(new JLabel("Select Service Provider:"), BorderLayout.WEST);
        topPanel.add(spCombo, BorderLayout.CENTER);

        panel.add(topPanel, BorderLayout.NORTH);
        panel.add(new JScrollPane(chatArea), BorderLayout.CENTER);

        JPanel inputPanel = new JPanel(new BorderLayout());
        inputPanel.add(messageField, BorderLayout.CENTER);
        inputPanel.add(sendButton, BorderLayout.EAST);
        panel.add(inputPanel, BorderLayout.SOUTH);

        spCombo.addActionListener(e -> {
            chatArea.setText("");
            String spUsername = (String) spCombo.getSelectedItem();
            if (spUsername != null) {
                List<ChatMessage> messages = DataHandler.loadChatMessages(getUsername(), spUsername);
                for (ChatMessage msg : messages) {
                    chatArea.append(msg.toDisplayString() + "\n");
                }
            }
        });

        sendButton.addActionListener(e -> {
            String spUsername = (String) spCombo.getSelectedItem();
            String message = messageField.getText();
            if (spUsername != null && !message.isEmpty()) {
                ChatMessage chatMessage = new ChatMessage(getUsername(), spUsername, message, new Date().toString());
                DataHandler.saveChatMessage(chatMessage);
                chatArea.append(chatMessage.toDisplayString() + "\n");
                messageField.setText("");
            }
        });

        // Load initial chat if any SP exists
        if (!spUsernames.isEmpty()) {
            spCombo.setSelectedIndex(0);
            spCombo.getActionListeners()[0].actionPerformed(null);
        }

        return panel;
    }

    private JPanel createContactCustomerPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        List<Event> events = DataHandler.loadEvents();
        // Only customers who have bookings assigned to this PM
        Set<String> assignedCustomerUsernames = new HashSet<>();
        for (Event event : events) {
            if (event.getAssignedPM().equals(getUsername())) {
                assignedCustomerUsernames.add(event.getCustomerUsername());
            }
        }
        JComboBox<String> customerCombo = new JComboBox<>(assignedCustomerUsernames.toArray(new String[0]));
        JTextArea chatArea = new JTextArea();
        chatArea.setEditable(false);
        JTextField messageField = new JTextField();
        JButton sendButton = new JButton("Send");

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(new JLabel("Select Assigned Customer:"), BorderLayout.WEST);
        topPanel.add(customerCombo, BorderLayout.CENTER);

        panel.add(topPanel, BorderLayout.NORTH);
        panel.add(new JScrollPane(chatArea), BorderLayout.CENTER);

        JPanel inputPanel = new JPanel(new BorderLayout());
        inputPanel.add(messageField, BorderLayout.CENTER);
        inputPanel.add(sendButton, BorderLayout.EAST);
        panel.add(inputPanel, BorderLayout.SOUTH);

        customerCombo.addActionListener(e -> {
            chatArea.setText("");
            String customerUsername = (String) customerCombo.getSelectedItem();
            if (customerUsername != null) {
                List<ChatMessage> messages = DataHandler.loadChatMessages(getUsername(), customerUsername);
                for (ChatMessage msg : messages) {
                    chatArea.append(msg.toDisplayString() + "\n");
                }
            }
        });

        sendButton.addActionListener(e -> {
            String customerUsername = (String) customerCombo.getSelectedItem();
            String message = messageField.getText();
            if (customerUsername != null && !message.isEmpty()) {
                ChatMessage chatMessage = new ChatMessage(getUsername(), customerUsername, message, new Date().toString());
                DataHandler.saveChatMessage(chatMessage);
                chatArea.append(chatMessage.toDisplayString() + "\n");
                messageField.setText("");
            }
        });

        // Load initial chat if any assigned customer exists
        if (customerCombo.getItemCount() > 0) {
            customerCombo.setSelectedIndex(0);
            customerCombo.getActionListeners()[0].actionPerformed(null);
        }

        return panel;
    }
}