public class ChatMessage {
    private String sender;
    private String receiver;
    private String message;
    private String timestamp;

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