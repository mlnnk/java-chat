import java.io.IOException;

public class ClientThread extends Thread {
    private ClientHandler handler;
    private ServerMain server;
    private boolean registered = false;

    public ClientThread(ClientHandler handler, ServerMain server) {
        this.server = server;
        this.handler = handler;
    }

    public void run() {
        try {
            String firstMessage = handler.readMessage();
            if (firstMessage != null && firstMessage.startsWith("/nickname ")) {
                String nickname = firstMessage.substring(10);
                handler.setNickname(nickname);
                registered = true;

                server.broadcastMessage("Пользователь " + nickname + " присоединился к чату", this);
                System.out.println("Пользователь " + nickname + " подключился");

                handler.SendMessage("Добро пожаловать в чат, " + nickname + "!");
            } else {
                handler.SendMessage("Ошибка: необходимо указать имя с помощью /nickname <имя>");
                handler.disconnect();
                return;
            }

            while (registered && handler.isConnected()) {
                try {
                    String message = handler.readMessage();
                    if (message == null) {
                        break;
                    }

                    if (message.equals("/exit")) {
                        server.broadcastMessage("Пользователь " + handler.getNickname() + " покинул чат", this);
                        System.out.println("Пользователь " + handler.getNickname() + " отключился");
                        break;
                    }

                    String formattedMessage = handler.getNickname() + ": " + message;

                    server.broadcastMessage(formattedMessage, this);

                    System.out.println(handler.getNickname() + ": " + message);

                } catch (IOException e) {
                    if (registered) {
                        server.broadcastMessage("Пользователь " + handler.getNickname() + " отключился", this);
                        System.out.println("Пользователь " + handler.getNickname() + " отключился (ошибка соединения)");
                    }
                    break;
                }
            }
        } catch (IOException e) {
            System.err.println("Ошибка при регистрации пользователя: " + e.getMessage());
        } finally {
            try {
                handler.disconnect();
            } catch (IOException e) {
                System.out.println("Произошла ошибка.");
                //e.printStackTrace();
            }
            server.removeClient(this);
        }
    }

    public ClientHandler getClientHandler() {
        return handler;
    }

    public String getClientNickname() {
        return handler != null ? handler.getNickname() : null;
    }
}