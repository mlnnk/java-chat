package chat;
import java.io.*;
import java.net.*;

public class ClientHandler {
    static final String exitCommand = new String("/exit");
    static final String nicknameCommand = new String("/nickname");

    private Socket socket = null;
    private PrintWriter out;
    private BufferedReader in;
    private String nickname;
    private boolean isConnected;

    public ClientHandler(Socket socket) throws IOException {
        this.socket = socket;
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        out = new PrintWriter(socket.getOutputStream(), true);
        isConnected = true;
    }

    public void SendMessage(String message) {
        if (isConnected && out != null) {
            out.println(message);
            out.flush();
        }
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String readMessage() throws IOException {
        if (isConnected && in != null) {
            return in.readLine();
        }
        return null;
    }

    public void disconnect() throws IOException {
        isConnected = false;
        if (socket != null && !socket.isClosed()) {
            socket.close();
        }
        if (in != null) {
            in.close();
        }
        if (out != null) {
            out.close();
        }
    }

    public boolean isConnected() {
        return isConnected && socket != null && !socket.isClosed() && socket.isConnected();
    }
}