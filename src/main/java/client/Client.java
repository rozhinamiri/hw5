package client;
import utils.NetworkUtil;


import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;

public class Client {

    private static Socket socket;
    private static Scanner scanner = new Scanner(System.in);
    static void showFileOptions(Socket socket) throws IOException {
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
                    return; // Exit to main menu
                default:
                    System.out.println("Invalid option. Please try again.");
            }
        }
    }

    private static void viewFiles(Socket socket) throws IOException {
        DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
        dos.writeUTF("VIEW_FILES");

        DataInputStream dis = new DataInputStream(socket.getInputStream());
        int fileCount = dis.readInt();
        System.out.println("Files available for download:");
        for (int i = 0; i < fileCount; i++) {
            String fileId = dis.readUTF();
            String fileName = dis.readUTF();
            System.out.println(fileId + ": " + fileName);
        }
    }

    private static void downloadFile(Socket socket) throws IOException {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter the file ID to download: ");
        String fileId = scanner.nextLine();
        System.out.print("Enter the name to save the file as: ");
        String fileName = scanner.nextLine();
        DownloadFile.downloadFile(socket, fileId, fileName);
    }


    public static void main(String[] args) {
        try {
            socket = NetworkUtil.createClientSocket("localhost", 8888);

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