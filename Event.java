public class Event {
    private String eventId;
    private String customerUsername;
    private String details;
    private String status;
    private String assignedPM;
    private double price;
    private String readyDate;

    public Event(String eventId, String customerUsername, String details) {
        this.eventId = eventId;
        this.customerUsername = customerUsername;
        this.details = details;
        this.status = "Pending";
        this.assignedPM = "";
        this.price = 0.0;
        this.readyDate = "";
    }

    public String toFileString() {
        return String.join("|", eventId, customerUsername, details, status, 
                          assignedPM, String.valueOf(price), readyDate);
    }

    public String getEventId() { return eventId; }
    public String getCustomerUsername() { return customerUsername; }
    public String getDetails() { return details; }
    public String getStatus() { return status; }
    public String getAssignedPM() { return assignedPM; }
    public double getPrice() { return price; }
    public String getReadyDate() { return readyDate; }

    public void setStatus(String status) { this.status = status; }
    public void setAssignedPM(String assignedPM) { this.assignedPM = assignedPM; }
    public void setPrice(double price) { this.price = price; }
    public void setReadyDate(String readyDate) { this.readyDate = readyDate; }
}