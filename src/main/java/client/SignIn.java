package client;

import utils.HashUtil;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.security.NoSuchAlgorithmException;
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
            DataInputStream dis = new DataInputStream(socket.getInputStream());
            dos.writeUTF("LOGIN");
            dos.writeUTF(username);
            dos.writeUTF(hashedPassword);

            String response = dis.readUTF();
            System.out.println(response);
            if (response.equals("LOGIN_SUCCESS")) {
                System.out.println("Log-in successful!");
                Client.showFileOptions(socket);
            } else {
                System.out.println("Invalid username or password.");
            }

        } catch (IOException | NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

}
