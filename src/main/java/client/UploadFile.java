package client;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class UploadFile {

    public static void uploadFile(Socket socket) {
        Scanner scanner = new Scanner(System.in);
        try {
            System.out.print("Enter file path: ");
            String filePath = scanner.nextLine();
            File file = new File(filePath);

            if (!file.exists() || !file.isFile()) {
                System.out.println("Invalid file.");
                return;
            }

            DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
            dos.writeUTF("UPLOAD");
            dos.writeUTF(file.getName());
            dos.writeLong(file.length());

            FileInputStream fis = new FileInputStream(file);
            byte[] buffer = new byte[4096];
            int bytesRead;

            while ((bytesRead = fis.read(buffer)) != -1) {
                dos.write(buffer, 0, bytesRead);
            }

            fis.close();
            System.out.println("Upload complete!");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

