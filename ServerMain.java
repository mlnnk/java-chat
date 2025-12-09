package chat;
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
                SSocket = new ServerSocket();
                SSocket.setReuseAddress(true);
                SSocket.bind(new InetSocketAddress(port));
<<<<<<< HEAD


=======
                
               
>>>>>>> 8d4a810eda4bcfa0dd0b63691182f8200565ebc0
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
            if (receiver != sender) {
                try {
                    receiver.getClientHandler().SendMessage(message);
                } catch (Exception e) {
                    System.err.println("Не удалось отправить сообщение клиенту " +
                            receiver.getClientNickname());
                }
            }
        }
    }

    public void broadcastMessage(String message) {
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

    public void removeClient(ClientThread clientThread) {
        if (clients.remove(clientThread)) {
            System.out.println("Клиент " +
                    (clientThread.getClientNickname() != null ?
                            clientThread.getClientNickname() : "неизвестный") +
                    " удален из списка");

            System.out.println("Осталось клиентов: " + clients.size());
        }
    }

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
    
    private void stopServer() throws IOException {
        
        for(var Client:clients) {
        	Client.getClientHandler().disconnect();
        	
        }
        if (SSocket != null && !SSocket.isClosed()) {
            SSocket.close();
            System.out.println("Серверный сокет закрыт");
        }
    }

<<<<<<< HEAD
    private void stopServer() throws IOException {

        for(var Client:clients) {
            Client.getClientHandler().disconnect();
        }
        if (SSocket != null && !SSocket.isClosed()) {
            SSocket.close();
            System.out.println("Серверный сокет закрыт");
        }
    }

=======
>>>>>>> 8d4a810eda4bcfa0dd0b63691182f8200565ebc0
    public void shutdown() {
        try {
            stopServer();
        } catch (IOException e) {
            System.err.println("Ошибка при остановке сервера: " + e.getMessage());
        }
    }
<<<<<<< HEAD

=======
    
    
>>>>>>> 8d4a810eda4bcfa0dd0b63691182f8200565ebc0
    public static void main(String[] args) {
        ServerMain server = new ServerMain();
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                System.out.println("Выключение сервера...");
                server.stopServer();
            } catch (IOException e) {
                System.err.println("Проблема во время выключения: " + e.getMessage());
            }
        }));
<<<<<<< HEAD

        server.initialize();

        new ExitWaiter(server).start();


=======
        
        server.initialize();
        
        new ExitWaiter(server).start();
        
        
>>>>>>> 8d4a810eda4bcfa0dd0b63691182f8200565ebc0
        if (server.SSocket == null) {
            System.out.println("Не удалось создать серверный сокет");
            return;
        }
        
        System.out.println("Сервер ожидает подключений...");

        while (!server.SSocket.isClosed()) {
            try {
                Socket clientSocket = server.SSocket.accept();

                System.out.println("Новое подключение (ожидание имени)...");

                ClientHandler clientHandler = new ClientHandler(clientSocket);
                server.add(clientHandler);}
<<<<<<< HEAD

            catch (SocketException e) {

                if (server.SSocket == null || server.SSocket.isClosed()) {
                    System.out.println("Сервер завершает работу");
                    break;
                } else {
                    System.err.println("Ошибка сокета: " + e.getMessage());
                }
=======
            
                catch (SocketException e) {
                    
                    if (server.SSocket == null || server.SSocket.isClosed()) {
                        System.out.println("Сервер завершает работу");
                        break;
                    } else {
                        System.err.println("Ошибка сокета: " + e.getMessage());
                    }
>>>>>>> 8d4a810eda4bcfa0dd0b63691182f8200565ebc0
            } catch (IOException e) {
                e.printStackTrace();
                break;
            }
<<<<<<< HEAD

=======
            
>>>>>>> 8d4a810eda4bcfa0dd0b63691182f8200565ebc0
        }
    }
}

<<<<<<< HEAD
class ExitWaiter {
    ServerMain server;

    ExitWaiter(ServerMain server) {
        this.server = server;
    }

    public void start() {
        Thread waiter = new Thread(() -> {
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(System.in));

=======
 class ExitWaiter {
	 ServerMain server;
	 ExitWaiter(ServerMain server){
		 this.server=server;
	 }
    public void start() {
        Thread waiter = new Thread(() -> {
            BufferedReader reader = new BufferedReader(
                new InputStreamReader(System.in));
            
>>>>>>> 8d4a810eda4bcfa0dd0b63691182f8200565ebc0
            try {
                while (true) {
                    if ("exit".equals(reader.readLine())) {
                        server.shutdown();
                        break;
                    }
                }
            } catch (IOException e) {
<<<<<<< HEAD
                //
            }
        });

        waiter.setDaemon(true);
        waiter.start();
    }
}
=======
                // 
            }
        });
        
        waiter.setDaemon(true);
        waiter.start();
    }
}



>>>>>>> 8d4a810eda4bcfa0dd0b63691182f8200565ebc0
