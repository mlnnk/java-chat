package chat;
import java.io.Serializable;

public class MessageBroadcastResult extends MessageResult implements Serializable {

    private static final long serialVersionUID = 1L;

    public MessageBroadcastResult(String errorMessage) {
        super(Protocol.CMD_BROADCAST, errorMessage);
    }

    public MessageBroadcastResult() {
        super(Protocol.CMD_BROADCAST);
    }
}