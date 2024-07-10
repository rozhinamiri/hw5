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
                showOptions(socket);
            } else {
                System.out.println("Invalid username or password.");
            }

        } catch (IOException | NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

    private static void showOptions(Socket socket) {
        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.println("1. View Files");
            System.out.println("2. Upload File");
            System.out.println("3. Download File");
            System.out.println("4. Logout");
            System.out.print("Choose an option: ");
            int choice = scanner.nextInt();
            scanner.nextLine(); // consume newline

            switch (choice) {
                case 1:
                    viewFiles(socket);
                    break;
                case 2:
                    UploadFile.uploadFile(socket);
                    break;
                case 3:
                    downloadFile(socket);
                    break;
                case 4:
                    return;
                default:
                    System.out.println("Invalid option. Please try again.");
            }
        }
    }

    private static void viewFiles(Socket socket) {
        try {
            DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
            DataInputStream dis = new DataInputStream(socket.getInputStream());

            dos.writeUTF("VIEW_FILES");

            String response = dis.readUTF();
            if (response.equals("NO_FILES")) {
                System.out.println("No files available.");
                return;
            }

            System.out.println("Files available:");
            int fileCount = dis.readInt();
            for (int i = 0; i < fileCount; i++) {
                String fileName = dis.readUTF();
                System.out.println((i + 1) + ". " + fileName);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void downloadFile(Socket socket) {
        Scanner scanner = new Scanner(System.in);
        try {
            System.out.print("Enter the file ID to download: ");
            String fileId = scanner.nextLine();

            DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
            DataInputStream dis = new DataInputStream(socket.getInputStream());

            dos.writeUTF("DOWNLOAD");
            dos.writeUTF(fileId);

            String response = dis.readUTF();
            if (response.equals("FILE_NOT_FOUND")) {
                System.out.println("File not found.");
                return;
            }

            long fileSize = dis.readLong();
            String fileName = dis.readUTF(); // read file name from server

            DownloadFile.downloadFile(socket, fileId, fileName);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
