package service;

import model.Notification;
import util.DataAccessObject;

import java.util.Date;
import java.util.List;

public class NotificationService {
    private final DataAccessObject<Notification> notificationDao;
    private static final String NOTIFICATION_FILE = "notifications.txt";

    public NotificationService(DataAccessObject<Notification> notificationDao) {
        this.notificationDao = notificationDao;
    }

    public void sendNotification(String recipientUsername, String subject, String message) {
        List<Notification> notifications = notificationDao.load(NOTIFICATION_FILE);
        notifications.add(new Notification(recipientUsername, subject, message, new Date().toString()));
        notificationDao.save(notifications, NOTIFICATION_FILE);
    }

    public List<Notification> getNotificationsByRecipient(String recipientUsername) {
        List<Notification> notifications = notificationDao.load(NOTIFICATION_FILE);
        return notifications.stream()
                .filter(n -> n.getRecipientUsername().equals(recipientUsername))
                .toList();
    }
}