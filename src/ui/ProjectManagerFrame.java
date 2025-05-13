package ui;

import java.awt.*;
import java.util.List;
import java.util.stream.Collectors;
import javax.swing.*;
import model.Event;
import model.Notification;
import model.User;
import service.EventService;
import service.NotificationService;
import service.UserService;
import util.FileHandler;

public class ProjectManagerFrame extends JFrame {
    private User user;
    private JTextArea eventDetailsArea;
    private final EventService eventService;
    private final NotificationService notificationService;
    private final UserService userService;

    public ProjectManagerFrame(User user) {
        this.user = user;
        eventService = new EventService(new FileHandler<>());
        notificationService = new NotificationService(new FileHandler<>());
        userService = new UserService(new FileHandler<>());
        setTitle("Project Manager Dashboard - " + user.getUsername());
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        initComponents();
        updateEventDetails();
        setVisible(true);
    }

    private void initComponents() {
        JTabbedPane tabbedPane = new JTabbedPane();

        // View Requests Tab
        JPanel viewRequestsPanel = new JPanel(new BorderLayout());
        eventDetailsArea = new JTextArea();
        eventDetailsArea.setEditable(false);
        viewRequestsPanel.add(new JScrollPane(eventDetailsArea), BorderLayout.CENTER);
        JButton refreshRequestsButton = new JButton("Refresh Requests");
        refreshRequestsButton.addActionListener(e -> updateEventDetails());
        viewRequestsPanel.add(refreshRequestsButton, BorderLayout.SOUTH);
        tabbedPane.addTab("View Requests", viewRequestsPanel);

        // Assign to SP Tab
        JPanel assignSPPanel = new JPanel(new GridLayout(4, 2, 10, 10));
        JComboBox<String> eventIdCombo = new JComboBox<>();
        JComboBox<String> spUsernameCombo = new JComboBox<>();
        updateEventCombo(eventIdCombo);
        updateSPCombo(spUsernameCombo);
        assignSPPanel.add(new JLabel("Event ID:"));
        assignSPPanel.add(eventIdCombo);
        assignSPPanel.add(new JLabel("Service Provider:"));
        assignSPPanel.add(spUsernameCombo);
        JButton assignButton = new JButton("Assign to SP");
        assignButton.addActionListener(e -> assignToSP(eventIdCombo, spUsernameCombo));
        assignSPPanel.add(assignButton);
        JButton autoAssignButton = new JButton("Auto-Assign to SP");
        autoAssignButton.addActionListener(e -> autoAssignToSP());
        assignSPPanel.add(autoAssignButton);
        tabbedPane.addTab("Assign to SP", assignSPPanel);

        // Contact Customer Tab
        JPanel contactCustomerPanel = new JPanel(new BorderLayout());
        JTextField contactEventIdField = new JTextField();
        JTextArea contactMessageArea = new JTextArea();
        JPanel contactInputPanel = new JPanel(new GridLayout(2, 2));
        contactInputPanel.add(new JLabel("Event ID:"));
        contactInputPanel.add(contactEventIdField);
        contactCustomerPanel.add(contactInputPanel, BorderLayout.NORTH);
        contactCustomerPanel.add(new JScrollPane(contactMessageArea), BorderLayout.CENTER);
        JButton sendCustomerMessageButton = new JButton("Send Message");
        sendCustomerMessageButton.addActionListener(e -> contactCustomer(contactEventIdField.getText(), contactMessageArea.getText()));
        contactCustomerPanel.add(sendCustomerMessageButton, BorderLayout.SOUTH);
        tabbedPane.addTab("Contact Customer", contactCustomerPanel);

        // Generate Bill Tab
        JPanel generateBillPanel = new JPanel(new BorderLayout());
        JPanel billInputPanel = new JPanel(new GridLayout(2, 2, 10, 10));
        JComboBox<String> billEventIdCombo = new JComboBox<>();
        updateEventCombo(billEventIdCombo);
        billInputPanel.add(new JLabel("Event ID:"));
        billInputPanel.add(billEventIdCombo);
        JButton generateBillButton = new JButton("Generate Bill");
        generateBillButton.addActionListener(e -> generateBill((String) billEventIdCombo.getSelectedItem()));
        billInputPanel.add(generateBillButton);
        generateBillPanel.add(billInputPanel, BorderLayout.NORTH);
        JTextArea billPreviewArea = new JTextArea();
        billPreviewArea.setEditable(false);
        generateBillPanel.add(new JScrollPane(billPreviewArea), BorderLayout.CENTER);
        tabbedPane.addTab("Generate Bill", generateBillPanel);

        // Notifications Tab
        JPanel notificationsPanel = new JPanel(new BorderLayout());
        JTextArea notificationsArea = new JTextArea();
        notificationsArea.setEditable(false);
        notificationsPanel.add(new JScrollPane(notificationsArea), BorderLayout.CENTER);
        JButton viewNotificationsButton = new JButton("View Notifications");
        viewNotificationsButton.addActionListener(e -> viewNotifications(notificationsArea));
        notificationsPanel.add(viewNotificationsButton, BorderLayout.SOUTH);
        tabbedPane.addTab("Notifications", notificationsPanel);

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

    private void updateEventDetails() {
        List<Event> events = eventService.getAllEvents();
        StringBuilder sb = new StringBuilder();
        for (Event event : events) {
            sb.append("Event ID: ").append(event.getEventId())
                    .append(", Customer: ").append(event.getCustomerUsername())
                    .append(", Type: ").append(event.getEventType())
                    .append(", Status: ").append(event.getStatus())
                    .append("\n");
        }
        eventDetailsArea.setText(sb.length() > 0 ? sb.toString() : "No events found.");
    }

    private void updateEventCombo(JComboBox<String> eventIdCombo) {
        eventIdCombo.removeAllItems();
        List<Event> events = eventService.getEventsByStatus("Assigned to PM");
        for (Event event : events) {
            eventIdCombo.addItem(event.getEventId());
        }
    }

    private void updateSPCombo(JComboBox<String> spUsernameCombo) {
        spUsernameCombo.removeAllItems();
        List<User> users = userService.getAllUsers();
        for (User currentUser : users) {
            if (currentUser.getRole().equals("ServiceProvider")) {
                spUsernameCombo.addItem(currentUser.getUsername());
            }
        }
    }

    private void assignToSP(JComboBox<String> eventIdCombo, JComboBox<String> spUsernameCombo) {
        String eventId = (String) eventIdCombo.getSelectedItem();
        String spUsername = (String) spUsernameCombo.getSelectedItem();
        if (eventId == null || spUsername == null) {
            JOptionPane.showMessageDialog(this, "Please select an Event ID and Service Provider!");
            return;
        }
        String result = eventService.assignToSP(eventId, spUsername);
        if (result.equals("Event assigned to Service Provider!")) {
            notificationService.sendNotification(spUsername, "New Event Assignment",
                    "You have been assigned an event!\nEvent ID: " + eventId);
        }
        JOptionPane.showMessageDialog(this, result);
        updateEventDetails();
        updateEventCombo(eventIdCombo);
    }

    private void autoAssignToSP() {
        List<User> users = userService.getAllUsers();
        List<String> spUsernames = users.stream()
                .filter(u -> u.getRole().equals("ServiceProvider"))
                .map(User::getUsername)
                .collect(Collectors.toList());
        int assignedCount = eventService.autoAssignToSP(spUsernames);
        if (assignedCount == 0) {
            JOptionPane.showMessageDialog(this, spUsernames.isEmpty() ? "No Service Providers available!" :
                    "No events in 'Assigned to PM' status found!");
        } else {
            List<Event> assignedEvents = eventService.getEventsByStatus("Assigned to SP");
            for (Event event : assignedEvents) {
                notificationService.sendNotification(event.getServiceProvider(), "New Event Assignment",
                        "You have been assigned an event!\nEvent ID: " + event.getEventId());
            }
            JOptionPane.showMessageDialog(this, assignedCount + " events assigned to Service Providers!");
            updateEventDetails();
            updateEventCombo(new JComboBox<>());
        }
    }

    private void contactCustomer(String eventId, String message) {
        if (eventId.isEmpty() || message.trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Event ID and message cannot be empty!");
            return;
        }
        Event event = eventService.getEventById(eventId);
        if (event != null) {
            notificationService.sendNotification(event.getCustomerUsername(), "Message from PM", message);
            JOptionPane.showMessageDialog(this, "Message saved to notifications.txt for customer!");
        } else {
            JOptionPane.showMessageDialog(this, "Event ID not found!");
        }
    }

    private void generateBill(String eventId) {
        if (eventId == null || eventId.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please select an Event ID!");
            return;
        }
        Event event = eventService.getEventById(eventId);
        if (event != null) {
            String bill = "Bill for Event ID: " + event.getEventId() + "\n" +
                    "Customer: " + event.getCustomerUsername() + "\n" +
                    "Event Type: " + event.getEventType() + "\n" +
                    "Date: " + event.getDate() + "\n" +
                    "Venue: " + event.getVenue() + "\n" +
                    "Budget: $" + event.getBudget() + "\n" +
                    "Status: " + event.getStatus() + "\n" +
                    (event.getServiceProvider() != null ? "Service Provider: " + event.getServiceProvider() : "");
            notificationService.sendNotification(event.getCustomerUsername(), "Event Bill", bill);
            JOptionPane.showMessageDialog(this, "Bill generated and sent to customer!\nCheck Notifications tab.");
        } else {
            JOptionPane.showMessageDialog(this, "Event ID not found!");
        }
    }

    private void viewNotifications(JTextArea notificationsArea) {
        List<Notification> notifications = notificationService.getNotificationsByRecipient("ProjectManager");
        StringBuilder sb = new StringBuilder();
        for (Notification notification : notifications) {
            sb.append("Subject: ").append(notification.getSubject())
                    .append("\nMessage: ").append(notification.getMessage())
                    .append("\nTimestamp: ").append(notification.getTimestamp())
                    .append("\n\n");
        }
        notificationsArea.setText(sb.length() > 0 ? sb.toString() : "No notifications found.");
    }
}
