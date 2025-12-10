package views;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import java.io.*;
import java.net.*;
import java.util.Scanner;
import java.util.regex.Pattern;

public class PRApplication extends Application {

    private Socket clientSocket = null;
    private PrintWriter serverOut = null;
    private Scanner serverIn = null;
    private boolean isConnected = false;

    private String nickname = "";
    private String serverIP = "";
    private int serverPort = 0;

    private TextArea chatArea;
    private TextField messageField;
    private Stage primaryStage;
    private Label statusLabel;
    private Stage loginStage;

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        showLoginWindow();
    }

    private void showLoginWindow() {
        loginStage = new Stage();

        TextField nameField = new TextField();
        nameField.setPromptText("Никнейм");

        TextField ipField = new TextField();
        ipField.setText("localhost");
        ipField.setPromptText("IP сервера или localhost");

        TextField portField = new TextField();
        portField.setText("8080");
        portField.setPromptText("Порт");

        Button connectBtn = new Button("Подключиться");
        statusLabel = new Label();
        statusLabel.setStyle("-fx-text-fill: red;");

        connectBtn.setOnAction(e -> {
            statusLabel.setText("");

            nickname = nameField.getText().trim();
            serverIP = ipField.getText().trim();
            String portText = portField.getText().trim();

            boolean isValid = true;

            if (nickname.isEmpty()) {
                statusLabel.setText("Введите никнейм!");
                isValid = false;
            } else if (nickname.length() < 2) {
                statusLabel.setText("Никнейм слишком короткий!");
                isValid = false;
            }

            if (isValid && serverIP.isEmpty()) {
                statusLabel.setText("Введите IP-адрес!");
                isValid = false;
            } else if (isValid && !isValidIP(serverIP)) {
                statusLabel.setText("Некорректный IP-адрес! Используйте localhost или IP вида 192.168.1.1");
                isValid = false;
            }

            if (isValid && portText.isEmpty()) {
                statusLabel.setText("Введите порт!");
                isValid = false;
            } else if (isValid) {
                try {
                    serverPort = Integer.parseInt(portText);
                    if (serverPort < 1 || serverPort > 65535) {
                        statusLabel.setText("Порт должен быть от 1 до 65535!");
                        isValid = false;
                    }
                } catch (NumberFormatException ex) {
                    statusLabel.setText("Порт должен быть числом!");
                    isValid = false;
                }
            }

            if (isValid) {
                connectBtn.setDisable(true);
                connectBtn.setText("Подключение...");
                statusLabel.setText("Попытка подключения...");
                statusLabel.setStyle("-fx-text-fill: blue;");

                new Thread(() -> connectToServer()).start();
            }
        });

        nameField.setOnAction(e -> connectBtn.fire());
        ipField.setOnAction(e -> connectBtn.fire());
        portField.setOnAction(e -> connectBtn.fire());

        VBox root = new VBox(10,
                new Label("Подключение к чату"),
                new Label("Никнейм (минимум 2 символа):"), nameField,
                new Label("IP сервера:"), ipField,
                new Label("Порт (1-65535):"), portField,
                connectBtn, statusLabel
        );
        root.setPadding(new Insets(20));

        loginStage.setScene(new Scene(root, 350, 400));
        loginStage.setTitle("Вход в чат");

        loginStage.setOnCloseRequest(e -> {
            Platform.exit();
            System.exit(0);
        });

        loginStage.show();
    }

    private boolean isValidIP(String ip) {
        if (ip.equalsIgnoreCase("localhost")) {
            return true;
        }

        String ipPattern = "^((25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}" +
                "(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)$";
        return Pattern.matches(ipPattern, ip);
    }

    private void connectToServer() {
        try {
            clientSocket = new Socket();
            clientSocket.connect(new InetSocketAddress(serverIP, serverPort), 5000);

            serverOut = new PrintWriter(clientSocket.getOutputStream(), true);
            serverIn = new Scanner(new InputStreamReader(clientSocket.getInputStream()));

            serverOut.println("/nickname " + nickname);

            if (serverIn.hasNextLine()) {
                String response = serverIn.nextLine();

                Platform.runLater(() -> {
                    if (response.contains("Добро пожаловать") || response.contains("присоединился")) {
                        isConnected = true;
                        if (loginStage != null && loginStage.isShowing()) {
                            loginStage.close();
                        }
                        showChatWindow();
                    } else {
                        statusLabel.setText("Ошибка сервера: " + response);
                        statusLabel.setStyle("-fx-text-fill: red;");
                        resetConnectButton();
                        disconnect();
                    }
                });
            } else {
                Platform.runLater(() -> {
                    statusLabel.setText("Сервер не ответил");
                    statusLabel.setStyle("-fx-text-fill: red;");
                    resetConnectButton();
                    disconnect();
                });
            }

            new Thread(this::receiveMessages).start();

        } catch (UnknownHostException e) {
            Platform.runLater(() -> {
                statusLabel.setText("Неизвестный хост: " + serverIP);
                statusLabel.setStyle("-fx-text-fill: red;");
                resetConnectButton();
            });
        } catch (SocketTimeoutException e) {
            Platform.runLater(() -> {
                statusLabel.setText("Таймаут подключения (5 сек)");
                statusLabel.setStyle("-fx-text-fill: red;");
                resetConnectButton();
            });
        } catch (ConnectException e) {
            Platform.runLater(() -> {
                statusLabel.setText("Не удалось подключиться к серверу");
                statusLabel.setStyle("-fx-text-fill: red;");
                resetConnectButton();
            });
        } catch (IOException e) {
            Platform.runLater(() -> {
                statusLabel.setText("Ошибка подключения: " + e.getMessage());
                statusLabel.setStyle("-fx-text-fill: red;");
                resetConnectButton();
            });
        }
    }

    private void resetConnectButton() {
        Platform.runLater(() -> {
            Scene scene = statusLabel.getScene();
            if (scene != null) {
                VBox root = (VBox) scene.getRoot();
                for (var node : root.getChildren()) {
                    if (node instanceof Button button && button.getText().contains("Подключ")) {
                        button.setDisable(false);
                        button.setText("Подключиться");
                        break;
                    }
                }
            }
        });
    }

    private void showChatWindow() {
        chatArea = new TextArea();
        chatArea.setEditable(false);
        chatArea.setPrefSize(500, 300);
        chatArea.setStyle("-fx-font-family: 'Consolas'; -fx-font-size: 12px;");

        messageField = new TextField();
        messageField.setPromptText("Введите сообщение...");

        Button sendBtn = new Button("Отправить");
        Button exitBtn = new Button("Выйти");

        sendBtn.setOnAction(e -> sendMessage());
        messageField.setOnAction(e -> sendMessage());
        exitBtn.setOnAction(e -> {
            disconnect();
            if (primaryStage != null && primaryStage.isShowing()) {
                primaryStage.close();
            }
        });

        VBox root = new VBox(10,
                new Label("Чат: " + nickname + " @ " + serverIP + ":" + serverPort),
                chatArea,
                messageField,
                new HBox(10, sendBtn, exitBtn)
        );
        root.setPadding(new Insets(20));

        primaryStage.setScene(new Scene(root, 600, 500));
        primaryStage.setTitle("Чат - " + nickname);

        primaryStage.setOnCloseRequest(e -> {
            disconnect();
            Platform.exit();
            System.exit(0);
        });

        primaryStage.show();

        chatArea.appendText("Подключен к серверу " + serverIP + ":" + serverPort + "\n");
        messageField.requestFocus();
    }

    private void sendMessage() {
        String message = messageField.getText().trim();
        if (!message.isEmpty() && isConnected) {
            if (message.equals("/exit")) {
                disconnect();
                if (primaryStage != null && primaryStage.isShowing()) {
                    primaryStage.close();
                }
            } else {
                serverOut.println(message);
                chatArea.appendText("Вы: " + message + "\n");
                messageField.clear();
            }
        }
    }

    private void receiveMessages() {
        try {
            while (isConnected && serverIn != null && serverIn.hasNextLine()) {
                String message = serverIn.nextLine();
                Platform.runLater(() -> {
                    chatArea.appendText(message + "\n");
                    chatArea.setScrollTop(Double.MAX_VALUE);
                });
            }
        } catch (Exception e) {
            Platform.runLater(() -> {
                if (isConnected) {
                    chatArea.appendText("\n*** Соединение с сервером потеряно ***\n");
                    isConnected = false;
                }
            });
        }
    }

    private void disconnect() {
        isConnected = false;
        try {
            if (serverOut != null) {
                serverOut.println("/exit");
                serverOut.close();
            }
            if (serverIn != null) serverIn.close();
            if (clientSocket != null && !clientSocket.isClosed()) {
                clientSocket.close();
            }
        } catch (IOException e) {
            System.err.println("Ошибка при отключении: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}