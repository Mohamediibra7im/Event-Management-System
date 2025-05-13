package model;

import java.io.Serializable;

public class Event implements Serializable {
    private String eventId;
    private String customerUsername;
    private String eventType;
    private String date;
    private String venue;
    private double budget;
    private String status;
    private String serviceProvider;

    public Event(String eventId, String customerUsername, String eventType, String date, String venue, double budget) {
        this.eventId = eventId;
        this.customerUsername = customerUsername;
        this.eventType = eventType;
        this.date = date;
        this.venue = venue;
        this.budget = budget;
        this.status = "Pending";
    }

    // Getters and setters
    public String getEventId() { return eventId; }
    public void setEventId(String eventId) { this.eventId = eventId; }
    public String getCustomerUsername() { return customerUsername; }
    public void setCustomerUsername(String customerUsername) { this.customerUsername = customerUsername; }
    public String getEventType() { return eventType; }
    public void setEventType(String eventType) { this.eventType = eventType; }
    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }
    public String getVenue() { return venue; }
    public void setVenue(String venue) { this.venue = venue; }
    public double getBudget() { return budget; }
    public void setBudget(double budget) { this.budget = budget; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getServiceProvider() { return serviceProvider; }
    public void setServiceProvider(String serviceProvider) { this.serviceProvider = serviceProvider; }
}