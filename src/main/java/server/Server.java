package server;

import models.Users;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Server {

    private static final int PORT = 8888; // Changed port number to avoid conflict


    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Server started on port " + PORT);

            while (true) {
                Socket socket = serverSocket.accept();
                System.out.println("Client connected: " + socket.getInetAddress());

                ClientHandler clientHandler = new ClientHandler(socket);
                clientHandler.start();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
