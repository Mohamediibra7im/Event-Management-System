import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.List;

public class ServiceProvider extends User {
    public ServiceProvider(String username, String password) {
        super(username, password, "ServiceProvider");
    }

    @Override
    public void showDashboard() {
        JFrame frame = new JFrame("Service Provider Dashboard");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(700, 500);
        frame.setLayout(new BorderLayout());

        JTabbedPane tabbedPane = new JTabbedPane();

        // View Requests Tab
        tabbedPane.addTab("View Requests", createViewRequestsPanel());

        // Contact Project Manager Tab
        tabbedPane.addTab("Contact Project Manager", createContactPMPanel());

        // Logout Tab
        JPanel logoutPanel = new JPanel(new BorderLayout());
        JButton logoutButton = new JButton("Logout");
        logoutButton.addActionListener(e -> frame.dispose());
        logoutPanel.add(logoutButton, BorderLayout.CENTER);
        tabbedPane.addTab("Logout", logoutPanel);

        frame.add(tabbedPane, BorderLayout.CENTER);
        frame.setVisible(true);
    }

    private JPanel createViewRequestsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        List<Event> events = DataHandler.loadEvents();
        List<Event> assignedEvents = new ArrayList<>();
        for (Event event : events) {
            if (!event.getAssignedPM().isEmpty()) {
                assignedEvents.add(event);
            }
        }
        String[] columnNames = {"Event ID", "Customer", "Details", "Status"};
        Object[][] data = new Object[assignedEvents.size()][4];
        for (int i = 0; i < assignedEvents.size(); i++) {
            Event event = assignedEvents.get(i);
            data[i][0] = event.getEventId();
            data[i][1] = event.getCustomerUsername();
            data[i][2] = event.getDetails();
            data[i][3] = event.getStatus();
        }
        JTable table = new JTable(data, columnNames);
        JScrollPane scrollPane = new JScrollPane(table);
        panel.add(scrollPane, BorderLayout.CENTER);

        JButton setPriceButton = new JButton("Set Price and Ready Date");
        panel.add(setPriceButton, BorderLayout.SOUTH);

        setPriceButton.addActionListener(e -> {
            int selectedRow = table.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(panel, "Please select an event from the table.");
                return;
            }
            String eventId = (String) table.getValueAt(selectedRow, 0);
            String priceStr = JOptionPane.showInputDialog("Enter Price:");
            String readyDate = JOptionPane.showInputDialog("Enter Ready Date (e.g., 2025-06-01):");
            try {
                double price = Double.parseDouble(priceStr);
                for (Event event : assignedEvents) {
                    if (event.getEventId().equals(eventId)) {
                        event.setPrice(price);
                        event.setReadyDate(readyDate);
                        event.setStatus("Price Set by SP");
                        DataHandler.updateEvent(event);
                        JOptionPane.showMessageDialog(panel, "Price and ready date set.");
                        // Optionally refresh the table here
                        return;
                    }
                }
                JOptionPane.showMessageDialog(panel, "Event not found.");
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(panel, "Invalid price format.");
            }
        });

        return panel;
    }

    private JPanel createContactPMPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        List<User> users = DataHandler.loadUsers();
        List<String> pmUsernames = new ArrayList<>();
        for (User user : users) {
            if ("ProjectManager".equals(user.getRole())) {
                pmUsernames.add(user.getUsername());
            }
        }
        JComboBox<String> pmCombo = new JComboBox<>(pmUsernames.toArray(new String[0]));
        JTextArea chatArea = new JTextArea();
        chatArea.setEditable(false);
        JTextField messageField = new JTextField();
        JButton sendButton = new JButton("Send");

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(new JLabel("Select Project Manager:"), BorderLayout.WEST);
        topPanel.add(pmCombo, BorderLayout.CENTER);

        panel.add(topPanel, BorderLayout.NORTH);
        panel.add(new JScrollPane(chatArea), BorderLayout.CENTER);

        JPanel inputPanel = new JPanel(new BorderLayout());
        inputPanel.add(messageField, BorderLayout.CENTER);
        inputPanel.add(sendButton, BorderLayout.EAST);
        panel.add(inputPanel, BorderLayout.SOUTH);

        pmCombo.addActionListener(e -> {
            chatArea.setText("");
            String pmUsername = (String) pmCombo.getSelectedItem();
            if (pmUsername != null) {
                List<ChatMessage> messages = DataHandler.loadChatMessages(getUsername(), pmUsername);
                for (ChatMessage msg : messages) {
                    chatArea.append(msg.toDisplayString() + "\n");
                }
            }
        });

        sendButton.addActionListener(e -> {
            String pmUsername = (String) pmCombo.getSelectedItem();
            String message = messageField.getText();
            if (pmUsername != null && !message.isEmpty()) {
                ChatMessage chatMessage = new ChatMessage(getUsername(), pmUsername, message, new Date().toString());
                DataHandler.saveChatMessage(chatMessage);
                chatArea.append(chatMessage.toDisplayString() + "\n");
                messageField.setText("");
            }
        });

        // Load initial chat if any PM exists
        if (!pmUsernames.isEmpty()) {
            pmCombo.setSelectedIndex(0);
            pmCombo.getActionListeners()[0].actionPerformed(null);
        }

        return panel;
    }
}