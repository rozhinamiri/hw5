package org.example.client;


import utils.HashUtil;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;

public class SignUp {

    public static void signUp(Socket socket) {
        Scanner scanner = new Scanner(System.in);
        try {
            System.out.print("Enter username: ");
            String username = scanner.nextLine();
            System.out.print("Enter password: ");
            String password = scanner.nextLine();

            String hashedPassword = HashUtil.hashPassword(password);

            DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
            dos.writeUTF("SIGNUP");
            dos.writeUTF(username);
            dos.writeUTF(hashedPassword);

            System.out.println("Sign-up successful!");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

