package org.example.client;

import commands.Commands;
import exception.ExitException;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.URL;
import java.util.ResourceBundle;

public class ChatController implements Initializable {
    @FXML
    public TextArea textarea;
    @FXML
    public TextField textField;
    @FXML
    public TextField loginField;
    @FXML
    public PasswordField passwordField;
    @FXML
    public HBox authForm;
    @FXML
    public HBox messageForm;
    private DataInputStream in;
    private DataOutputStream out;
    private Socket socket;
    private int port = 7777;
    private String host = "localhost";
    private boolean authenticated;
    private String nickname;

    public void setAuthenticated(boolean authenticated) {
        this.authenticated = authenticated;
        messageForm.setVisible(authenticated);
        messageForm.setManaged(authenticated);
        authForm.setVisible(!authenticated);
        authForm.setManaged(!authenticated);

        if (!authenticated) {
            nickname = "";
        } else {
            Platform.runLater(() -> textField.requestFocus());
        }
    }

    @FXML
    protected void onSendButtonClick() {
        try {
            String message = textField.getText();
            textField.clear();
            // LocalDateTime date = LocalDateTime.now();
            // DateTimeFormatter df = DateTimeFormatter.ofPattern("HH:mm:ss");
            out.writeUTF(message);
            textField.requestFocus();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        authenticated = false;
        // new Thread(() -> work()).start();
        //  Platform.runLater(() -> textField.requestFocus());
    }

    private void work() {
        try {
            //цикл авторизации
            while (true) {
                String message = in.readUTF();
                if (message.startsWith("/")) {
                    if (message.startsWith(Commands.EXIT)) {
                        setAuthenticated(false);
                        throw new ExitException();
                    }
                    if (message.startsWith(Commands.AUTHOK)) {
                        // /authok nickname
                        nickname = message.split(" ")[1];
                        setAuthenticated(true);
                        textarea.clear();
                        break;
                    }
                    if (Commands.AUTHERROR.equals(message)) {
                        setAuthenticated(false);
                        textarea.appendText("Неверный логин и пароль\n");
                        throw new ExitException();
                    }
                } else {
                    textarea.appendText(message);
                }
            }
            //цикл работы
            while (true) {
                String message = in.readUTF();
                if (message.endsWith(Commands.EXIT)) {
                    break;
                }
                textarea.appendText(message + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            setAuthenticated(false);
            if (socket != null) {
                try {
                    socket.close();
                    //System.exit(0);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @FXML
    public void tryToAction(ActionEvent actionEvent) {
        new Thread(() -> {
            //либо уже соединились либо нет
            connect();
            //запрос на авторизацию "/auth login password"
            try {
                out.writeUTF(String.format("%s %s %s", Commands.AUTH, loginField.getText(), passwordField.getText()));
                passwordField.clear();
                work();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();

    }

    private void connect() {
        try {
            if (socket == null || socket.isClosed()) {
                socket = new Socket(host, port);
                in = new DataInputStream(socket.getInputStream());
                out = new DataOutputStream(socket.getOutputStream());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}