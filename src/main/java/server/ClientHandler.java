package server;

import models.Database;
import models.Users;
import utils.HashUtil;

import java.io.*;
import java.net.Socket;
import java.security.NoSuchAlgorithmException;
import java.util.List;

public class ClientHandler extends Thread {
    private Socket socket;
    private DataInputStream dis;
    private DataOutputStream dos;

    public ClientHandler(Socket socket) {
        this.socket = socket;

        try {
            dis = new DataInputStream(socket.getInputStream());
            dos = new DataOutputStream(socket.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        try {
            while (true) {
                String action = dis.readUTF();
                System.out.println("Action received: " + action);

                switch (action) {
                    case "SIGNUP":
                        handleSignUp();
                        break;
                    case "LOGIN":
                        handleLogin();
                        break;
                    case "UPLOAD":
                        handleUpload();
                        break;
                    case "DOWNLOAD":
                        handleDownload();
                        break;
                    case "VIEW_FILES":
                        handleViewFiles();
                        break;
                    default:
                        dos.writeUTF("INVALID_ACTION");
                        break;
                }
            }
        } catch (EOFException e) {
            System.out.println("Client disconnected");
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (dis != null) dis.close();
                if (dos != null) dos.close();
                if (socket != null) socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void handleSignUp() throws IOException {
        String username = dis.readUTF();
        String hashedPassword = dis.readUTF();

        synchronized (Database.usersList) {
            for (Users user : Database.usersList) {
                if (user.getUsername().equals(username)) {
                    dos.writeUTF("USERNAME_EXISTS");
                    return;
                }
            }

            Database.usersList.add(new Users(username, hashedPassword));
            System.out.println("User signed up: " + username);
            dos.writeUTF("SIGNUP_SUCCESS");
        }
    }

    private void handleLogin() throws IOException {
        String username = dis.readUTF();
        String hashedPassword = dis.readUTF();

        System.out.println("Attempting login for username: " + username + " with hashedPassword: " + hashedPassword);

        synchronized (Database.usersList) {
            for (Users user : Database.usersList) {
                if (user.getUsername().equals(username) && user.getHashedPassword().equals(hashedPassword)) {
                    dos.writeUTF("LOGIN_SUCCESS");
                    System.out.println("Login successful for username: " + username);
                    return;
                }
            }

            dos.writeUTF("LOGIN_FAILURE");
            System.out.println("Login failed for username: " + username);
        }
    }

    private void handleUpload() throws IOException {
        String fileName = dis.readUTF();
        long fileSize = dis.readLong();
        String fileId = null;
        try {
            fileId = HashUtil.hashFileName(fileName);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
        System.out.println(fileName);

        File directory = new File("server_files");
        if (!directory.exists()) {
            directory.mkdirs();  // Creates the directory, including any necessary but nonexistent parent directories
        }

        // Create the file within the directory
        File file = new File(directory, fileId + "_" + fileName);
        try (FileOutputStream fos = new FileOutputStream(file)) {
            byte[] buffer = new byte[4096];
            int bytesRead;
            while (fileSize > 0 && (bytesRead = dis.read(buffer, 0, (int) Math.min(buffer.length, fileSize))) != -1) {
                fos.write(buffer, 0, bytesRead);
                fileSize -= bytesRead;
            }
        } catch (IOException e) {
            e.printStackTrace();  // Handle the exception appropriately
        }

        synchronized (Database.fileList) {
            Database.fileList.add(new FileInfo(fileId, fileName, file.length()));
        }

        dos.writeUTF("UPLOAD_SUCCESS");
    }
    private void handleViewFiles() throws IOException {
        synchronized (Database.fileList) {
            dos.writeInt(Database.fileList.size());
            for (FileInfo fileInfo : Database.fileList) {
                System.out.println(fileInfo.getFileName());
                System.out.println(fileInfo);
            }
            dos.writeUTF("VIEW_FILES_COMPLETE");
        }

    }


    private void handleDownload() throws IOException {
        String fileId = dis.readUTF();

        synchronized (Database.fileList) {
            for (FileInfo fileInfo : Database.fileList) {
                if (fileInfo.getFileId().equals(fileId)) {
                    dos.writeLong(fileInfo.getFileSize());

                    File file = new File("server_files/" + fileId);
                    try (FileInputStream fis = new FileInputStream(file)) {
                        byte[] buffer = new byte[4096];
                        int bytesRead;
                        while ((bytesRead = fis.read(buffer)) != -1) {
                            dos.write(buffer, 0, bytesRead);
                        }
                    }
                    return;
                }
            }
        }

        dos.writeUTF("FILE_NOT_FOUND");
    }
}
