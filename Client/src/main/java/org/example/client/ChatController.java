package org.example.client;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

public class ChatController implements Initializable {

    @FXML
    public TextArea textarea;

    @FXML
    public TextField textField;

    private DataInputStream in;
    private DataOutputStream out;

    private Socket socket;
    private int port = 7777;
    private String host = "localhost";

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

        new Thread(() -> work()).start();


        Platform.runLater(() -> textField.requestFocus());
    }

    private void work(){
        try {
            socket = new Socket(host, port);
            in = new DataInputStream(socket.getInputStream());
            out = new DataOutputStream(socket.getOutputStream());

            while (true) {
                String message = in.readUTF();

                if (message.endsWith("/exit")) {
                    break;
                }

                textarea.appendText(message + "\n");
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (socket != null) {
                try {
                    socket.close();
                    //System.exit(0);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}