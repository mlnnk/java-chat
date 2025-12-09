import java.io.Serializable;

public class MessageBroadcast extends Message implements Serializable {

    private static final long serialVersionUID = 1L;

    public String sender;
    public String message;

    public MessageBroadcast(String sender, String message) {
        super(Protocol.CMD_BROADCAST);
        this.sender = sender;
        this.message = message;
    }

    @Override
    public String toString() {
        return sender + ": " + message;
    }
}