package client;
import models.Database;
import utils.NetworkUtil;


import java.io.*;
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
            scanner.nextLine();

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
            int fileCount = dis.readInt();


            if (fileCount ==0) {
                System.out.println("No files available.");
                return;
            }

            System.out.println("Files available:");
            for (int i = 0; i < fileCount; i++) {
                String fileName = dis.readUTF();
                String fileId = dis.readUTF();
                System.out.println((i + 1) + ". " + fileName+"->"+fileId);
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
            } else if (response.equals("DOWNLOAD_SUCCESS")) {
                String fileName = dis.readUTF();
                long fileSize = dis.readLong();

                System.out.print("Enter the directory to save the file: ");
                String saveDir = scanner.nextLine();
                File savePath = new File(saveDir, fileName);

                try (FileOutputStream fos = new FileOutputStream(savePath)) {
                    byte[] buffer = new byte[4096];
                    int bytesRead;
                    long totalRead = 0;

                    while (totalRead < fileSize && (bytesRead = dis.read(buffer, 0, (int) Math.min(buffer.length, fileSize - totalRead))) != -1) {
                        fos.write(buffer, 0, bytesRead);
                        totalRead += bytesRead;
                    }

                    System.out.println("Download complete!");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
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
                scanner.nextLine();

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