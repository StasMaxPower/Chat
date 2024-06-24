package org.example;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.CopyOnWriteArrayList;

public class Server {

    private ServerSocket server;
    private Socket socket;
    private int port = 7777;
    private List<ClientHandler> clients = new CopyOnWriteArrayList<>();

    public Server() {
        try {
            server = new ServerSocket(port);
            System.out.println("Сервер успешно запущен");

            while (true) {
                socket = server.accept();
                ClientHandler handler = new ClientHandler(this, socket);
                clients.add(handler);
                System.out.println("Клиент успешно соединился " + socket.getPort());
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (socket != null) {
                try {
                    socket.close();
                    server.close();

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void broadcastMessage(String message){
        for (ClientHandler client : clients) {
            client.sendMessage(message);
        }
    }

}
