package client;

import utils.HashUtil;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.security.NoSuchAlgorithmException;
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
            DataInputStream dis = new DataInputStream(socket.getInputStream());
            DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
            dos.writeUTF("SIGNUP");
            dos.writeUTF(username);
            dos.writeUTF(hashedPassword);
            String response = dis.readUTF();
            if (response.equals("USERNAME_EXISTS")) {
                System.out.println("Username exists");
            }
            else if (response.equals("SIGNUP_SUCCESS")){
                System.out.println("Sign-up successful!");
            }


        } catch (IOException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }
}

