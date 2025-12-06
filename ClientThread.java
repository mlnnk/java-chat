package chat;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.*;
public class ClientThread extends Thread{
    private ClientHandler handler;
    private Server server;
    public ClientThread(ClientHandler handler, Server server) {
    	this.server=server;
    	this.handler=handler;
    	
    }
    
	public void run() {
		
		while (true) {
            String message;
			try {
				message = handler.readMessage();
				server.broadcastMessage(message, this);
				System.out.println("sentMEssage");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
            
        }
	}

public ClientHandler getClientHandler() {
	return handler;
}}
	
	

