public class ChatMessage {
    private final String sender;
    private final String receiver;
    private final String message;
    private final String timestamp;

    public ChatMessage(String sender, String receiver, String message, String timestamp) {
        this.sender = sender;
        this.receiver = receiver;
        this.message = message;
        this.timestamp = timestamp;
    }

    public String toFileString() {
        return sender + "," + receiver + "," + message + "," + timestamp;
    }

    public String toDisplayString() {
        return "[" + timestamp + "] " + sender + ": " + message;
    }
}