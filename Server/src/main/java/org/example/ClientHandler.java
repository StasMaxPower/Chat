package org.example;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class ClientHandler {

    private Server server;
    private Socket socket;
    private DataOutputStream out;
    private DataInputStream in;

    public ClientHandler(Server server, Socket socket) {
        this.server = server;
        this.socket = socket;

        new Thread(() -> work()).start();

    }

    private void work() {
        try {
            in = new DataInputStream(socket.getInputStream());
            out = new DataOutputStream(socket.getOutputStream());

            while (true) {
                String message = in.readUTF();
                System.out.println("Client: " + message);
                //out.writeUTF("server: " + message);

                if ("/exit".equals(message)) {
                    System.out.println("Клиент отключился");
                    break;
                }

                server.broadcastMessage(message);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (socket != null) {
                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void sendMessage(String message){
        try {
            out.writeUTF(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
