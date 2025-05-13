package service;

import model.Event;
import util.DataAccessObject;

import java.util.List;
import java.util.UUID;

public class EventService {
    private final DataAccessObject<Event> eventDao;
    private static final String EVENT_FILE = "events.txt";

    public EventService(DataAccessObject<Event> eventDao) {
        this.eventDao = eventDao;
    }

    public String bookEvent(String customerUsername, String eventType, String date, String venue, String budgetStr) {
        if (eventType.isEmpty() || date.isEmpty() || venue.isEmpty() || budgetStr.isEmpty()) {
            return "All fields are required!";
        }
        double budget;
        try {
            budget = Double.parseDouble(budgetStr);
        } catch (NumberFormatException e) {
            return "Invalid budget format!";
        }
        String eventId = UUID.randomUUID().toString();
        List<Event> events = eventDao.load(EVENT_FILE);
        events.add(new Event(eventId, customerUsername, eventType, date, venue, budget));
        eventDao.save(events, EVENT_FILE);
        return eventId;
    }

    public String editEvent(String eventId, String customerUsername, String eventType, String date, String venue, String budgetStr) {
        List<Event> events = eventDao.load(EVENT_FILE);
        for (Event event : events) {
            if (event.getEventId().equals(eventId) && event.getCustomerUsername().equals(customerUsername)) {
                boolean updated = false;
                if (!eventType.isEmpty()) {
                    event.setEventType(eventType);
                    updated = true;
                }
                if (!date.isEmpty()) {
                    event.setDate(date);
                    updated = true;
                }
                if (!venue.isEmpty()) {
                    event.setVenue(venue);
                    updated = true;
                }
                if (!budgetStr.isEmpty()) {
                    try {
                        event.setBudget(Double.parseDouble(budgetStr));
                        updated = true;
                    } catch (NumberFormatException e) {
                        return "Invalid budget format!";
                    }
                }
                if (updated) {
                    event.setStatus("Updated");
                    eventDao.save(events, EVENT_FILE);
                    return "Event updated successfully!";
                }
                return "No changes provided!";
            }
        }
        return "Event ID not found or not owned by you!";
    }

    public String cancelEvent(String eventId, String customerUsername) {
        List<Event> events = eventDao.load(EVENT_FILE);
        boolean removed = events.removeIf(e -> e.getEventId().equals(eventId) && e.getCustomerUsername().equals(customerUsername));
        if (removed) {
            eventDao.save(events, EVENT_FILE);
            return "Event cancelled successfully!";
        }
        return "Event ID not found or not owned by you!";
    }

    public String assignToPM(String eventId, String pmUsername) {
        List<Event> events = eventDao.load(EVENT_FILE);
        for (Event event : events) {
            if (event.getEventId().equals(eventId) && event.getStatus().equals("Pending")) {
                event.setStatus("Assigned to PM");
                eventDao.save(events, EVENT_FILE);
                return "Event assigned to Project Manager!";
            }
        }
        return "Event ID not found or not in Pending status!";
    }

    public int autoAssignEvents(List<String> pmUsernames) {
        if (pmUsernames.isEmpty()) {
            return 0;
        }
        List<Event> events = eventDao.load(EVENT_FILE);
        int assignedCount = 0;
        for (int i = 0; i < events.size(); i++) {
            Event event = events.get(i);
            if (event.getStatus().equals("Pending")) {
                event.setStatus("Assigned to PM");
                assignedCount++;
            }
        }
        if (assignedCount > 0) {
            eventDao.save(events, EVENT_FILE);
        }
        return assignedCount;
    }

    public String assignToSP(String eventId, String spUsername) {
        List<Event> events = eventDao.load(EVENT_FILE);
        for (Event event : events) {
            if (event.getEventId().equals(eventId) && event.getStatus().equals("Assigned to PM")) {
                event.setServiceProvider(spUsername);
                event.setStatus("Assigned to SP");
                eventDao.save(events, EVENT_FILE);
                return "Event assigned to Service Provider!";
            }
        }
        return "Event ID not found or not in Assigned to PM status!";
    }

    public int autoAssignToSP(List<String> spUsernames) {
        if (spUsernames.isEmpty()) {
            return 0;
        }
        List<Event> events = eventDao.load(EVENT_FILE);
        int assignedCount = 0;
        for (Event event : events) {
            if (event.getStatus().equals("Assigned to PM")) {
                event.setServiceProvider(spUsernames.get(assignedCount % spUsernames.size()));
                event.setStatus("Assigned to SP");
                assignedCount++;
            }
        }
        if (assignedCount > 0) {
            eventDao.save(events, EVENT_FILE);
        }
        return assignedCount;
    }

    public String setPrice(String eventId, String spUsername, String priceStr) {
        try {
            double price = Double.parseDouble(priceStr);
            List<Event> events = eventDao.load(EVENT_FILE);
            for (Event event : events) {
                if (event.getEventId().equals(eventId) && event.getServiceProvider().equals(spUsername)) {
                    event.setBudget(price);
                    event.setStatus("Price Set");
                    eventDao.save(events, EVENT_FILE);
                    return "Price updated!";
                }
            }
            return "Event ID not found or not assigned to you!";
        } catch (NumberFormatException e) {
            return "Invalid price format!";
        }
    }

    public String setReadyDate(String eventId, String spUsername, String readyDate) {
        List<Event> events = eventDao.load(EVENT_FILE);
        for (Event event : events) {
            if (event.getEventId().equals(eventId) && event.getServiceProvider().equals(spUsername)) {
                event.setDate(readyDate);
                event.setStatus("Ready");
                eventDao.save(events, EVENT_FILE);
                return "Ready date updated!";
            }
        }
        return "Event ID not found or not assigned to you!";
    }

    public List<Event> getEventsByCustomer(String customerUsername) {
        List<Event> events = eventDao.load(EVENT_FILE);
        return events.stream()
                .filter(e -> e.getCustomerUsername().equals(customerUsername))
                .toList();
    }

    public List<Event> getAllEvents() {
        return eventDao.load(EVENT_FILE);
    }

    public List<Event> getEventsByStatus(String status) {
        List<Event> events = eventDao.load(EVENT_FILE);
        return events.stream()
                .filter(e -> e.getStatus().equals(status))
                .toList();
    }

    public List<Event> getEventsByServiceProvider(String spUsername) {
        List<Event> events = eventDao.load(EVENT_FILE);
        return events.stream()
                .filter(e -> spUsername.equals(e.getServiceProvider()))
                .toList();
    }

    public Event getEventById(String eventId) {
        List<Event> events = eventDao.load(EVENT_FILE);
        return events.stream()
                .filter(e -> e.getEventId().equals(eventId))
                .findFirst()
                .orElse(null);
    }
}