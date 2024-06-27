package org.example;

import org.example.Authentification.AuthService;
import org.example.Authentification.SimpleAuthService;

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
    private AuthService authService;

    public AuthService getAuthService() {
        return authService;
    }

    public Server() {
        authService = new SimpleAuthService();
        try {
            server = new ServerSocket(port);
            System.out.println("Сервер успешно запущен");

            while (true) {
                socket = server.accept();
                new ClientHandler(this, socket);
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

    public void broadcastMessage(String message) {
        for (ClientHandler client : clients) {
            client.sendMessage(message);
        }
    }

    public void subscribe(ClientHandler handler) {
        clients.add(handler);
    }

    public void unsubscribe(ClientHandler handler) {
        clients.remove(handler);
    }
}
