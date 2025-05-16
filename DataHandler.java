import java.io.*;
import java.util.*;

public class DataHandler {
    private static final String USERS_FILE = "users.txt";
    private static final String EVENTS_FILE = "events.txt";
    private static final String CHATS_FILE = "chats.txt";

    public static boolean saveUser(User user) {
        // Check for duplicate username
        List<User> existingUsers = loadUsers();
        for (User existingUser : existingUsers) {
            if (existingUser.getUsername().equals(user.getUsername())) {
                return false; // Duplicate username
            }
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(USERS_FILE, true))) {
            writer.write(user.getUsername() + "," + user.getRole() + "," + user.getPassword() + "\n");
            return true;
        } catch (IOException e) {
            System.err.println("Error saving user: " + e.getMessage());
            return false;
        }
    }

    public static List<User> loadUsers() {
        List<User> users = new ArrayList<>();
        File file = new File(USERS_FILE);
        if (!file.exists()) {
            System.out.println("Users file does not exist: " + USERS_FILE);
            return users;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",", -1); // Handle empty fields
                if (parts.length == 3) {
                    String username = parts[0];
                    String role = parts[1];
                    String password = parts[2];
                    switch (role) {
                        case "Customer" -> users.add(new Customer(username, password));
                        case "ProjectManager" -> users.add(new ProjectManager(username, password));
                        case "ServiceProvider" -> users.add(new ServiceProvider(username, password));
                        case "Admin" -> users.add(new Admin(username, password));
                        default -> System.out.println("Invalid role in users file: " + role);
                    }
                } else {
                    System.out.println("Invalid line in users file: " + line);
                }
            }
        } catch (IOException e) {
            System.err.println("Error loading users: " + e.getMessage());
        }
        return users;
    }

    public static void saveEvent(Event event) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(EVENTS_FILE, true))) {
            // Save event fields separated by |, matching loadEvents()
            // eventId|customerUsername|details|status|assignedPM|price|readyDate
            writer.write(event.getEventId() + "|" +
                        event.getCustomerUsername() + "|" +
                        event.getDetails().replace("|", "/") + "|" +
                        event.getStatus() + "|" +
                        event.getAssignedPM() + "|" +
                        event.getPrice() + "|" +
                        (event.getReadyDate() == null ? "" : event.getReadyDate())
            );
            writer.newLine();
        } catch (IOException e) {
            System.err.println("Error loading events: " + e.getMessage());
        }
    }

    public static List<Event> loadEvents() {
        List<Event> events = new ArrayList<>();
        File file = new File(EVENTS_FILE);
        if (!file.exists()) return events;

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("\\|", -1);
                if (parts.length == 7) {
                    Event event = new Event(parts[0], parts[1], parts[2]);
                    event.setStatus(parts[3]);
                    event.setAssignedPM(parts[4]);
                    try {
                        event.setPrice(Double.parseDouble(parts[5]));
                    } catch (NumberFormatException e) {
                        event.setPrice(0.0);
                    }
                    event.setReadyDate(parts[6]);
                    events.add(event);
                } else {
                    System.out.println("Skipping invalid line: " + line + ", parts: " + parts.length);
                }
            }
        } catch (IOException e) {
            System.err.println("Error updating event: " + e.getMessage());
        }
        return events;
    }

    public static void updateEvent(Event updatedEvent) {
        List<Event> events = loadEvents();
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(EVENTS_FILE))) {
            for (Event event : events) {
                if (event.getEventId().equals(updatedEvent.getEventId())) {
                    writer.write(updatedEvent.toFileString() + "\n");
                } else {
                    writer.write(event.toFileString() + "\n");
                }
            }
        } catch (IOException e) {
            System.err.println("Error saving chat message: " + e.getMessage());
        }
    }

    public static void saveChatMessage(ChatMessage message) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(CHATS_FILE, true))) {
            writer.write(message.toFileString() + "\n");
        } catch (IOException e) {
            System.err.println("Error saving chat message: " + e.getMessage());
        }
    }

    public static List<ChatMessage> loadChatMessages(String user1, String user2) {
        List<ChatMessage> messages = new ArrayList<>();
        File file = new File(CHATS_FILE);
        if (!file.exists()) return messages;

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",", -1);
                if (parts.length == 4) {
                    String sender = parts[0];
                    String receiver = parts[1];
                    if ((sender.equals(user1) && receiver.equals(user2)) || (sender.equals(user2) && receiver.equals(user1))) {
                        messages.add(new ChatMessage(sender, receiver, parts[2], parts[3]));
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Error loading chat messages: " + e.getMessage());
        }
        return messages;
    }
}
