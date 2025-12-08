package chat;
import java.io.Serializable;

public class MessageConnect extends Message implements Serializable {

    private static final long serialVersionUID = 1L;

    public String userNic;

    public MessageConnect(String userNic) {
        super(Protocol.CMD_CONNECT);
        this.userNic = userNic;
    }

    public MessageConnect() {
        super(Protocol.CMD_CONNECT);
    }
}