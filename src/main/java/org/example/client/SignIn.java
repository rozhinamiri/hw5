package org.example.client;

import utils.HashUtil;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;

public class SignIn {

    public static void signIn(Socket socket) {
        Scanner scanner = new Scanner(System.in);
        try {
            System.out.print("Enter username: ");
            String username = scanner.nextLine();
            System.out.print("Enter password: ");
            String password = scanner.nextLine();

            String hashedPassword = HashUtil.hashPassword(password);

            DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
            dos.writeUTF("LOGIN");
            dos.writeUTF(username);
            dos.writeUTF(hashedPassword);

            System.out.println("Log-in successful!");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
