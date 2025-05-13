package ui;

import model.Event;
import model.Notification;
import model.User;
import service.EventService;
import service.NotificationService;
import util.FileHandler;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class ServiceProviderFrame extends JFrame {
    private User user;
    private JTextArea eventDetailsArea;
    private EventService eventService;
    private NotificationService notificationService;

    public ServiceProviderFrame(User user) {
        this.user = user;
        eventService = new EventService(new FileHandler<>());
        notificationService = new NotificationService(new FileHandler<>());
        setTitle("Service Provider Dashboard - " + user.getUsername());
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

        // Set Price Tab
        JPanel setPricePanel = new JPanel(new GridLayout(3, 2, 10, 10));
        JTextField priceEventIdField = new JTextField();
        JTextField priceField = new JTextField();
        setPricePanel.add(new JLabel("Event ID:"));
        setPricePanel.add(priceEventIdField);
        setPricePanel.add(new JLabel("Price:"));
        setPricePanel.add(priceField);
        JButton setPriceButton = new JButton("Set Price");
        setPriceButton.addActionListener(e -> setPrice(priceEventIdField, priceField));
        setPricePanel.add(setPriceButton);
        tabbedPane.addTab("Set Price", setPricePanel);

        // Set Ready Date Tab
        JPanel setReadyDatePanel = new JPanel(new GridLayout(3, 2, 10, 10));
        JTextField readyEventIdField = new JTextField();
        JTextField readyDateField = new JTextField();
        setReadyDatePanel.add(new JLabel("Event ID:"));
        setReadyDatePanel.add(readyEventIdField);
        setReadyDatePanel.add(new JLabel("Ready Date (YYYY-MM-DD):"));
        setReadyDatePanel.add(readyDateField);
        JButton setReadyDateButton = new JButton("Set Ready Date");
        setReadyDateButton.addActionListener(e -> setReadyDate(readyEventIdField, readyDateField));
        setReadyDatePanel.add(setReadyDateButton);
        tabbedPane.addTab("Set Ready Date", setReadyDatePanel);

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
            new LoginFrame();
        });
        bottomPanel.add(logoutButton);
        add(bottomPanel, BorderLayout.SOUTH);
    }

    private void updateEventDetails() {
        List<Event> events = eventService.getEventsByServiceProvider(user.getUsername());
        StringBuilder sb = new StringBuilder();
        for (Event event : events) {
            sb.append("Event ID: ").append(event.getEventId())
                    .append(", Type: ").append(event.getEventType())
                    .append(", Budget: ").append(event.getBudget())
                    .append(", Status: ").append(event.getStatus())
                    .append("\n");
        }
        eventDetailsArea.setText(sb.length() > 0 ? sb.toString() : "No events assigned.");
    }

    private void setPrice(JTextField eventIdField, JTextField priceField) {
        String eventId = eventIdField.getText();
        String priceStr = priceField.getText();
        if (eventId.isEmpty() || priceStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "All fields are required!");
            return;
        }
        String result = eventService.setPrice(eventId, user.getUsername(), priceStr);
        if (result.equals("Price updated!")) {
            notificationService.sendNotification("ProjectManager", "Price Update for Event: " + eventId,
                    "Price set to: " + priceStr);
            eventIdField.setText("");
            priceField.setText("");
            updateEventDetails();
        }
        JOptionPane.showMessageDialog(this, result);
    }

    private void setReadyDate(JTextField eventIdField, JTextField readyDateField) {
        String eventId = eventIdField.getText();
        String readyDate = readyDateField.getText();
        if (eventId.isEmpty() || readyDate.isEmpty()) {
            JOptionPane.showMessageDialog(this, "All fields are required!");
            return;
        }
        String result = eventService.setReadyDate(eventId, user.getUsername(), readyDate);
        if (result.equals("Ready date updated!")) {
            notificationService.sendNotification("ProjectManager", "Ready Date for Event: " + eventId,
                    "Ready Date set to: " + readyDate);
            eventIdField.setText("");
            readyDateField.setText("");
            updateEventDetails();
        }
        JOptionPane.showMessageDialog(this, result);
    }

    private void viewNotifications(JTextArea notificationsArea) {
        List<Notification> notifications = notificationService.getNotificationsByRecipient(user.getUsername());
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