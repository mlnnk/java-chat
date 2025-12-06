package chat;
import java.io.*;
import java.net.*;
public class ClientHandler{
	static final String exitCommand=new String("/exit");
	
		private Socket socket=null;
		private PrintWriter out;
		private BufferedReader in;
		private String nickname;
		
	
		public ClientHandler(Socket socket) throws IOException {
			this.socket=socket;
		
			in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			out= new PrintWriter(socket.getOutputStream(),true);
		}
	
		
		
	
	
	public void SendMessage(String message) {
		out.println(message);
	}
	public String getNickname() {
		return nickname;
	}
	public String readMessage() throws IOException {
        return in.readLine();
    }
    
	
	
}
