package org.example;

import commands.Commands;
import exception.ExitException;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ClientHandler {
    private Server server;
    private Socket socket;
    private DataOutputStream out;
    private DataInputStream in;
    private String nickname;

    public ClientHandler(Server server, Socket socket) {
        new Thread(() ->
        {
            try {
                this.server = server;
                this.socket = socket;
                in = new DataInputStream(socket.getInputStream());
                out = new DataOutputStream(socket.getOutputStream());
                work();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void work() {
        try {
            //цикл обработки служебных сообщений
            while (true) {
                String message = in.readUTF();
                if (message.startsWith("/")) {
                    if (message.startsWith(Commands.EXIT)) {
                        out.writeUTF(Commands.EXIT);
                        server.unsubscribe(this);
                        throw new ExitException();
                    }
                    if (message.startsWith(Commands.AUTH)) {
                        String[] parts = message.split(" ");
                        String login = parts[1];
                        String password = parts[2];
                        nickname = server.getAuthService().getNickName(login, password);
                        if (nickname == null) {
                            out.writeUTF(Commands.AUTHERROR);
                        } else {
                            server.subscribe(this);
                            out.writeUTF(String.format("%s %s", Commands.AUTHOK, nickname));
                            break;
                        }
                    }
                }
            }
            //цикл работы
            while (true) {
                String message = in.readUTF();
                System.out.println("Client: " + message);
                //out.writeUTF("server: " + message);

                if (Commands.EXIT.equals(message)) {
                    server.unsubscribe(this);
                    System.out.println("Клиент отключился");
                    break;
                }
                LocalDateTime date = LocalDateTime.now();
                DateTimeFormatter df = DateTimeFormatter.ofPattern("HH:mm:ss");
                server.broadcastMessage(String.format("[%s %s] %s",df.format(date), nickname, message));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                out.writeUTF(Commands.EXIT);
                if (socket != null) {
                    socket.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void sendMessage(String message) {
        try {
            out.writeUTF(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
