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
            // Первое сообщение должно быть именем пользователя
            String firstMessage = handler.readMessage();
            if (firstMessage != null && firstMessage.startsWith("/nickname ")) {
                String nickname = firstMessage.substring(10); // Убираем "/nickname "
                handler.setNickname(nickname);
                registered = true;

                // Уведомляем всех о подключении
                server.broadcastMessage("Пользователь " + nickname + " присоединился к чату", this);
                System.out.println("Пользователь " + nickname + " подключился");

                // Отправляем приветственное сообщение только этому клиенту
                handler.SendMessage("Добро пожаловать в чат, " + nickname + "!");
            } else {
                handler.SendMessage("Ошибка: необходимо указать имя с помощью /nickname <имя>");
                handler.disconnect();
                return;
            }

            // Основной цикл обработки сообщений
            while (registered && handler.isConnected()) {
                try {
                    String message = handler.readMessage();
                    if (message == null) {
                        break; // Клиент отключился
                    }

                    if (message.equals("/exit")) {
                        // Уведомляем об отключении
                        server.broadcastMessage("Пользователь " + handler.getNickname() + " покинул чат", this);
                        System.out.println("Пользователь " + handler.getNickname() + " отключился");
                        break;
                    }

                    // Формируем сообщение с именем отправителя
                    String formattedMessage = handler.getNickname() + ": " + message;

                    // Рассылаем всем КРОМЕ отправителя
                    server.broadcastMessage(formattedMessage, this);

                    // Логируем на сервере (без дублирования)
                    System.out.println(handler.getNickname() + ": " + message);

                } catch (IOException e) {
                    if (registered) {
                        // Уведомляем об отключении
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
                e.printStackTrace();
            }
            // Удаляем клиента из списка
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