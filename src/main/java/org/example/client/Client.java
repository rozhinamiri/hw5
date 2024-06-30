package org.example.client;


import utils.NetworkUtil;

import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;

public class Client {

    private static Socket socket;
    private static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        try {
            socket = NetworkUtil.createClientSocket("localhost", 12345);

            while (true) {
                System.out.println("1. Sign Up");
                System.out.println("2. Log In");
                System.out.println("3. Exit");
                System.out.print("Choose an option: ");
                int choice = scanner.nextInt();
                scanner.nextLine(); // consume newline

                switch (choice) {
                    case 1:
                        SignUp.signUp(socket);
                        break;
                    case 2:
                        SignIn.signIn(socket);
                        break;
                    case 3:
                        socket.close();
                        System.exit(0);
                        break;
                    default:
                        System.out.println("Invalid option. Please try again.");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
