package server;

import java.io.*;
import java.net.*;
import java.util.Scanner;

public class Client {
    static final String exitCommand = new String("/exit");
    static final String connectCommand = new String("/connect");
    Socket clientSocket = null;
    BufferedReader stdIn;
    BufferedReader serverIn = null;
    PrintWriter serverOut = null;
    private String nickname;

    public Client() {
    }

    void initialize(BufferedReader stdIn) {
        String serverIP = "0";
        int serverPort = 0;

        try {
            System.out.println("Введите ваше имя (никнейм):");
            nickname = stdIn.readLine();

            System.out.println("Введите IP-адрес сервера:");
            serverIP = stdIn.readLine();
            System.out.println("Введите порт сервера:");
            serverPort = Integer.parseInt(stdIn.readLine());

            clientSocket = new Socket(serverIP, serverPort);
            this.stdIn = stdIn;
        } catch (IOException e) {
            System.out.println("Произошла ошибка.");
            //e.printStackTrace();
        }
    }

    void connect() throws IOException {
        serverOut = new PrintWriter(clientSocket.getOutputStream(), true);

        serverOut.println("/nickname " + nickname);

        ReceivedHandler handler = new ReceivedHandler(clientSocket.getInputStream());
        handler.start();

        String userInput;
        while ((userInput = stdIn.readLine()) != null) {
            if (userInput.equals("/exit")) {
                serverOut.println("/exit");
                break;
            }
            serverOut.println(userInput);
        }

        serverOut.close();
        clientSocket.close();
    }

    public static void main(String[] args) {
        BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in));
        System.out.println("Введите /connect для подключения к серверу и /exit для выхода");
        String command = null;
        Client cl = null;

        while (true) {
            try {
                command = stdIn.readLine();
            } catch (IOException e) {
                System.out.println("Произошла ошибка.");
                //e.printStackTrace();
            }

            if (command.equals(connectCommand)) {
                cl = new Client();
                cl.initialize(stdIn);
                break;
            } else if (command.equals(exitCommand)) {
                System.exit(0);
            } else {
                System.out.println("Нет такой команды");
            }
        }

        try {
            cl.connect();
        } catch (IOException e) {
            System.out.println("Произошла ошибка.");
            //e.printStackTrace();
        }
    }
}

class ReceivedHandler extends Thread {
    private Scanner input;

    ReceivedHandler(InputStream inputStream) {
        input = new Scanner(new InputStreamReader(inputStream));
    }

    public void run() {
        String received = "";
        while (input.hasNext()) {
            received = input.nextLine();
            System.out.println(received);
        }
    }
}