package client;

import models.Database;

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
            DataInputStream dis = new DataInputStream(socket.getInputStream());
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
           String response = dis.readUTF();
            System.out.println(response);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

