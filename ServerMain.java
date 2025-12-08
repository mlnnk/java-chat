import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

public class ServerMain {
    int port;
    ServerSocket SSocket;
    private List<ClientThread> clients = new CopyOnWriteArrayList<>();

    void initialize() {
        BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in));

        boolean socketCreated = false;
        while (!socketCreated) {
            try {
                System.out.println("Введите порт сервера (или 'exit' для выхода):");
                String input = stdIn.readLine();

                if ("exit".equalsIgnoreCase(input)) {
                    return;
                }

                port = Integer.parseInt(input);

                if (port < 1 || port > 65535) {
                    System.out.println("Порт должен быть в диапазоне 1-65535");
                    continue;
                }

                SSocket = new ServerSocket(port);
                socketCreated = true;
                System.out.println("Сервер запущен на порту " + port);

            } catch (NumberFormatException e) {
                System.out.println("Ошибка: введите корректное число");
            } catch (IllegalArgumentException e) {
                System.out.println("Ошибка: некорректный номер порта");
            } catch (IOException e) {
                System.out.println("Ошибка при создании серверного сокета: " + e.getMessage());
                System.out.println("Попробуйте другой порт");
            }
        }
    }

    public void broadcastMessage(String message, ClientThread sender) {
        String senderName = sender.getClientNickname();

        for (ClientThread receiver : clients) {
            // Не отправляем сообщение отправителю
            if (receiver != sender) {
                try {
                    receiver.getClientHandler().SendMessage(message);
                } catch (Exception e) {
                    // Если не удалось отправить, клиент вероятно отключился
                    System.err.println("Не удалось отправить сообщение клиенту " +
                            receiver.getClientNickname());
                }
            }
        }
    }

    public void broadcastMessage(String message) {
        // Рассылка всем клиентам (например, для серверных сообщений)
        for (ClientThread receiver : clients) {
            try {
                receiver.getClientHandler().SendMessage(message);
            } catch (Exception e) {
                System.err.println("Не удалось отправить сообщение клиенту " +
                        receiver.getClientNickname());
            }
        }
    }

    public void add(ClientHandler clientHandler) {
        ClientThread thread = new ClientThread(clientHandler, this);
        thread.start();
        clients.add(thread);
    }

    // Удаление клиента из списка
    public void removeClient(ClientThread clientThread) {
        if (clients.remove(clientThread)) {
            System.out.println("Клиент " +
                    (clientThread.getClientNickname() != null ?
                            clientThread.getClientNickname() : "неизвестный") +
                    " удален из списка");

            // Выводим текущее количество клиентов
            System.out.println("Осталось клиентов: " + clients.size());
        }
    }

    // Получение списка всех имен подключенных клиентов
    public List<String> getConnectedUserNames() {
        List<String> names = new ArrayList<>();
        for (ClientThread client : clients) {
            String name = client.getClientNickname();
            if (name != null) {
                names.add(name);
            }
        }
        return names;
    }

    public static void main(String[] args) {
        ServerMain server = new ServerMain();
        server.initialize();

        if (server.SSocket == null) {
            System.out.println("Не удалось создать серверный сокет");
            return;
        }

        System.out.println("Сервер ожидает подключений...");

        while (true) {
            try {
                Socket clientSocket = server.SSocket.accept();

                // Не выводим IP, только факт подключения
                System.out.println("Новое подключение (ожидание имени)...");

                ClientHandler clientHandler = new ClientHandler(clientSocket);
                server.add(clientHandler);

            } catch (IOException e) {
                e.printStackTrace();
                break;
            }
        }
    }
}