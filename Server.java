package chat;
import java.io.*;

import java.net.*;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
public class Server {
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

public void broadcastMessage(String message, ClientThread sender){
	for(var receiver: clients) {
		if(receiver!=sender) {
			receiver.getClientHandler().SendMessage(message);
		}
	}
}
public void add(ClientHandler Th) {
	ClientThread  th =new ClientThread(Th,this);
	th.start();
	clients.add(th);
	System.out.println("added");
}



	public static void main(String[] args) {
		 Server server = new Server();
		    server.initialize();
		    
		    
		    if (server.SSocket == null) {
		        System.out.println("Не удалось создать серверный сокет");
		        return;
		    }
		    while(true) {
		    
		    	try {
		        
		        server.add(new ClientHandler(server.SSocket.accept()));
		           
		        
		    	}
		     catch (IOException e) {
		        e.printStackTrace();
		    }
                            
		    }

}
	}

