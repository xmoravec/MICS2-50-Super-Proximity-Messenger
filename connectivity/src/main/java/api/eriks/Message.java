package api.eriks;

public class Message {
    private ConnectionServiceImpl.Endpoint endpoint;
    private String message;
    private long timestamp;
    private boolean isMyChat;

    public Message(String message, boolean isMyChat, long timestamp) {
        this.message = message;
        this.isMyChat = isMyChat;
        this.timestamp = timestamp;
    }

    public Message(ConnectionServiceImpl.Endpoint endpoint, String message, boolean isMyChat, long timestamp) {
        this.endpoint = endpoint;
        this.message = message;
        this.isMyChat = isMyChat;
        this.timestamp = timestamp;
    }

    public ConnectionServiceImpl.Endpoint getEndpoint() {
        return endpoint;
    }

    public void setEndpoint(ConnectionServiceImpl.Endpoint endpoint) {
        this.endpoint = endpoint;
    }

    public String getMessage() {
        return message;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public boolean isMyChat() {
        return isMyChat;
    }

    public void setMyChat(boolean myChat) {
        isMyChat = myChat;
    }
}
