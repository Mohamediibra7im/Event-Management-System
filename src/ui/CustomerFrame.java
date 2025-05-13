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

public class CustomerFrame extends JFrame {
    private User user;
    private JTextArea eventDetailsArea;
    private EventService eventService;
    private NotificationService notificationService;

    public CustomerFrame(User user) {
        this.user = user;
        eventService = new EventService(new FileHandler<>());
        notificationService = new NotificationService(new FileHandler<>());
        setTitle("Customer Dashboard - " + user.getUsername());
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

        // Book Event Tab
        JPanel bookEventPanel = new JPanel(new GridLayout(5, 2, 10, 10));
        JTextField eventTypeField = new JTextField();
        JTextField dateField = new JTextField();
        JTextField venueField = new JTextField();
        JTextField budgetField = new JTextField();
        bookEventPanel.add(new JLabel("Event Type:"));
        bookEventPanel.add(eventTypeField);
        bookEventPanel.add(new JLabel("Date (YYYY-MM-DD):"));
        bookEventPanel.add(dateField);
        bookEventPanel.add(new JLabel("Venue:"));
        bookEventPanel.add(venueField);
        bookEventPanel.add(new JLabel("Budget:"));
        bookEventPanel.add(budgetField);
        JButton bookButton = new JButton("Book Event");
        bookButton.addActionListener(e -> bookEvent(eventTypeField, dateField, venueField, budgetField));
        bookEventPanel.add(bookButton);
        tabbedPane.addTab("Book Event", bookEventPanel);

        // Manage Bookings Tab
        JPanel manageBookingsPanel = new JPanel(new BorderLayout());
        eventDetailsArea = new JTextArea();
        eventDetailsArea.setEditable(false);
        manageBookingsPanel.add(new JScrollPane(eventDetailsArea), BorderLayout.CENTER);
        JPanel manageInputPanel = new JPanel(new GridLayout(6, 2, 10, 10));
        JTextField editEventIdField = new JTextField();
        JTextField editEventTypeField = new JTextField();
        JTextField editDateField = new JTextField();
        JTextField editVenueField = new JTextField();
        JTextField editBudgetField = new JTextField();
        manageInputPanel.add(new JLabel("Event ID:"));
        manageInputPanel.add(editEventIdField);
        manageInputPanel.add(new JLabel("New Event Type:"));
        manageInputPanel.add(editEventTypeField);
        manageInputPanel.add(new JLabel("New Date (YYYY-MM-DD):"));
        manageInputPanel.add(editDateField);
        manageInputPanel.add(new JLabel("New Venue:"));
        manageInputPanel.add(editVenueField);
        manageInputPanel.add(new JLabel("New Budget:"));
        manageInputPanel.add(editBudgetField);
        JButton editButton = new JButton("Edit Booking");
        editButton.addActionListener(e -> editEvent(editEventIdField.getText(), editEventTypeField.getText(),
                editDateField.getText(), editVenueField.getText(), editBudgetField.getText()));
        manageInputPanel.add(editButton);
        JButton cancelButton = new JButton("Cancel Booking");
        cancelButton.addActionListener(e -> cancelEvent(editEventIdField.getText()));
        manageInputPanel.add(cancelButton);
        manageBookingsPanel.add(manageInputPanel, BorderLayout.SOUTH);
        tabbedPane.addTab("Manage Bookings", manageBookingsPanel);

        // Contact PM Tab
        JPanel contactPMPanel = new JPanel(new BorderLayout());
        JTextArea messageArea = new JTextArea();
        contactPMPanel.add(new JScrollPane(messageArea), BorderLayout.CENTER);
        JButton sendMessageButton = new JButton("Send Message to PM");
        sendMessageButton.addActionListener(e -> contactPM(messageArea.getText()));
        contactPMPanel.add(sendMessageButton, BorderLayout.SOUTH);
        tabbedPane.addTab("Contact PM", contactPMPanel);

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
        List<Event> events = eventService.getEventsByCustomer(user.getUsername());
        StringBuilder sb = new StringBuilder();
        for (Event event : events) {
            sb.append("Event ID: ").append(event.getEventId())
                    .append(", Type: ").append(event.getEventType())
                    .append(", Date: ").append(event.getDate())
                    .append(", Venue: ").append(event.getVenue())
                    .append(", Budget: ").append(event.getBudget())
                    .append(", Status: ").append(event.getStatus())
                    .append("\n");
        }
        eventDetailsArea.setText(sb.length() > 0 ? sb.toString() : "No bookings found.");
    }

    private void bookEvent(JTextField eventTypeField, JTextField dateField, JTextField venueField, JTextField budgetField) {
        String eventId = eventService.bookEvent(user.getUsername(), eventTypeField.getText(), dateField.getText(),
                venueField.getText(), budgetField.getText());
        if (eventId.startsWith("Event")) {
            JOptionPane.showMessageDialog(this, eventId);
        } else {
            notificationService.sendNotification(user.getUsername(), "Event Booking Confirmation",
                    "Your event has been booked!\nEvent ID: " + eventId + "\nType: " + eventTypeField.getText() +
                            "\nDate: " + dateField.getText());
            JOptionPane.showMessageDialog(this, "Event booked successfully! Check Notifications tab.");
            eventTypeField.setText("");
            dateField.setText("");
            venueField.setText("");
            budgetField.setText("");
            updateEventDetails();
        }
    }

    private void editEvent(String eventId, String eventType, String date, String venue, String budgetStr) {
        if (eventId.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Event ID is required!");
            return;
        }
        String result = eventService.editEvent(eventId, user.getUsername(), eventType, date, venue, budgetStr);
        if (result.equals("Event updated successfully!")) {
            notificationService.sendNotification(user.getUsername(), "Event Updated",
                    "Event ID: " + eventId + " has been updated.");
        }
        JOptionPane.showMessageDialog(this, result);
        updateEventDetails();
    }

    private void cancelEvent(String eventId) {
        if (eventId.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Event ID is required!");
            return;
        }
        String result = eventService.cancelEvent(eventId, user.getUsername());
        if (result.equals("Event cancelled successfully!")) {
            notificationService.sendNotification(user.getUsername(), "Event Cancelled",
                    "Event ID: " + eventId + " has been cancelled.");
        }
        JOptionPane.showMessageDialog(this, result);
        updateEventDetails();
    }

    private void contactPM(String message) {
        if (message.trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Message cannot be empty!");
            return;
        }
        notificationService.sendNotification("ProjectManager", "Message from Customer: " + user.getUsername(), message);
        JOptionPane.showMessageDialog(this, "Message saved to notifications.txt for Project Manager!");
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