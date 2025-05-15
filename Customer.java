import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.List;

public class Customer extends User {
    public Customer(String username, String password) {
        super(username, password, "Customer");
    }

    @Override
    public void showDashboard() {
        JFrame frame = new JFrame("Customer Dashboard");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(700, 500);
        frame.setLayout(new BorderLayout());

        JTabbedPane tabbedPane = new JTabbedPane();

        // Book Event Tab
        tabbedPane.addTab("Book Event", createBookEventPanel(frame));

        // Manage Booking Tab
        tabbedPane.addTab("Manage Booking", createManageBookingPanel());

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

    private JPanel createBookEventPanel(JFrame parentFrame) {
        JPanel panel = new JPanel(new GridLayout(6, 2));
        JLabel nameLabel = new JLabel("Event Name:");
        JTextField nameField = new JTextField();
        JLabel dateLabel = new JLabel("Event Date:");
        JTextField dateField = new JTextField();
        JLabel locationLabel = new JLabel("Location:");
        JTextField locationField = new JTextField();
        JLabel detailsLabel = new JLabel("Additional Details:");
        JTextArea detailsArea = new JTextArea();
        JButton submitButton = new JButton("Submit");

        panel.add(nameLabel);
        panel.add(nameField);
        panel.add(dateLabel);
        panel.add(dateField);
        panel.add(locationLabel);
        panel.add(locationField);
        panel.add(detailsLabel);
        panel.add(detailsArea);
        panel.add(new JLabel());
        panel.add(submitButton);

        submitButton.addActionListener(e -> {
            String name = nameField.getText().trim();
            String date = dateField.getText().trim();
            String location = locationField.getText().trim();
            String details = detailsArea.getText().trim();

            if (!name.isEmpty() && !date.isEmpty() && !location.isEmpty() && !details.isEmpty()) {
                String eventId = UUID.randomUUID().toString();
                String fullDetails = "Name: " + name + ", Date: " + date + ", Location: " + location + ", Details: " + details;
                Event event = new Event(eventId, getUsername(), fullDetails);
                DataHandler.saveEvent(event);

                JOptionPane.showMessageDialog(parentFrame, 
                    "Event booked!\nReservation Number: " + eventId + 
                    "\nYour login password: " + getPassword());

                // Refresh the Manage Booking tab if it exists
                JTabbedPane tabbedPane = (JTabbedPane) SwingUtilities.getAncestorOfClass(JTabbedPane.class, parentFrame.getContentPane().getComponent(0));
                if (tabbedPane != null) {
                    int manageBookingIndex = tabbedPane.indexOfTab("Manage Booking");
                    if (manageBookingIndex != -1) {
                        tabbedPane.setComponentAt(manageBookingIndex, createManageBookingPanel());
                    }
                }
            } else {
                JOptionPane.showMessageDialog(parentFrame, "Please fill in all fields.");
            }
        });

        return panel;
    }

    private JPanel createManageBookingPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        List<Event> events = DataHandler.loadEvents();
        List<Event> myEvents = new ArrayList<>();
        for (Event event : events) {
            String eventUsername = event.getCustomerUsername().trim();
            String currentUsername = getUsername().trim();
            if (eventUsername.equalsIgnoreCase(currentUsername)) {
                myEvents.add(event);
            }
        }
        String[] columnNames = {"Event ID", "Details", "Status", "Price"};
        Object[][] data = new Object[myEvents.size()][4];
        for (int i = 0; i < myEvents.size(); i++) {
            Event event = myEvents.get(i);
            data[i][0] = event.getEventId();
            data[i][1] = event.getDetails();
            data[i][2] = event.getStatus();
            data[i][3] = event.getPrice();
        }
        JTable table = new JTable(data, columnNames);
        JScrollPane scrollPane = new JScrollPane(table);
        panel.removeAll();
        panel.add(scrollPane, BorderLayout.CENTER);

        if (myEvents.isEmpty()) {
            JLabel noEventsLabel = new JLabel("No bookings found.", SwingConstants.CENTER);
            panel.add(noEventsLabel, BorderLayout.NORTH);
        }

        panel.revalidate();
        panel.repaint();
        return panel;
    }

    private JPanel createContactPMPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        JTextArea chatArea = new JTextArea();
        chatArea.setEditable(false);
        JTextField messageField = new JTextField();
        JButton sendButton = new JButton("Send");

        String pmUsername = "";
        List<Event> events = DataHandler.loadEvents();
        for (Event event : events) {
            if (event.getCustomerUsername().equals(getUsername()) && !event.getAssignedPM().isEmpty()) {
                pmUsername = event.getAssignedPM();
                break;
            }
        }

        if (pmUsername.isEmpty()) {
            chatArea.setText("No Project Manager assigned yet.");
            panel.add(new JScrollPane(chatArea), BorderLayout.CENTER);
            return panel;
        }

        String finalPmUsername = pmUsername;
        List<ChatMessage> messages = DataHandler.loadChatMessages(getUsername(), finalPmUsername);
        for (ChatMessage msg : messages) {
            chatArea.append(msg.toDisplayString() + "\n");
        }

        JPanel inputPanel = new JPanel(new BorderLayout());
        inputPanel.add(messageField, BorderLayout.CENTER);
        inputPanel.add(sendButton, BorderLayout.EAST);

        panel.add(new JScrollPane(chatArea), BorderLayout.CENTER);
        panel.add(inputPanel, BorderLayout.SOUTH);

        sendButton.addActionListener(e -> {
            String message = messageField.getText();
            if (!message.isEmpty()) {
                ChatMessage chatMessage = new ChatMessage(getUsername(), finalPmUsername, message, new Date().toString());
                DataHandler.saveChatMessage(chatMessage);
                chatArea.append(chatMessage.toDisplayString() + "\n");
                messageField.setText("");
            }
        });

        return panel;
    }
}