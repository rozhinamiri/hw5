package server;

import models.Database;
import models.Users;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {

    private static final int PORT = 8888; // Changed port number to avoid conflict

    public static void main(String[] args) {
        // Load data from JSON at startup
        Database.loadFromJSON();

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            // Save data to JSON at shutdown
            Database.saveToJSON();
        }));

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
