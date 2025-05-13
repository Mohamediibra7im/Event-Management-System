package model;

import java.io.Serializable;

public class Notification implements Serializable {
    private String recipientUsername;
    private String subject;
    private String message;
    private String timestamp;

    public Notification(String recipientUsername, String subject, String message, String timestamp) {
        this.recipientUsername = recipientUsername;
        this.subject = subject;
        this.message = message;
        this.timestamp = timestamp;
    }

    // Getters and setters
    public String getRecipientUsername() { return recipientUsername; }
    public void setRecipientUsername(String recipientUsername) { this.recipientUsername = recipientUsername; }
    public String getSubject() { return subject; }
    public void setSubject(String subject) { this.subject = subject; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    public String getTimestamp() { return timestamp; }
    public void setTimestamp(String timestamp) { this.timestamp = timestamp; }
}