package client;

import models.Database;
import server.FileTransferServer;
import utils.NetworkUtil;

import java.io.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class Client {

    private static Socket socket;
    private static Scanner scanner = new Scanner(System.in);

    private static final int FILE_SERVER_PORT = 9876;
    private static final int CHUNK_SIZE = 4096;
    private static final int TOTAL_CHUNKS = 5;

    public static Object lock = new Object();

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

            if (fileCount == 0) {
                System.out.println("No files available.");
                return;
            }

            System.out.println("Files available:");
            for (int i = 0; i < fileCount; i++) {
                String fileName = dis.readUTF();
                String fileId = dis.readUTF();
                System.out.println((i + 1) + ". " + fileName + "->" + fileId);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void downloadFile(Socket socket) {
        try {
            InetAddress serverAddress = InetAddress.getByName("localhost");
            DatagramSocket clientSocket = new DatagramSocket();
            Scanner scanner = new Scanner(System.in);

            System.out.print("Enter the file ID to download: ");
            String fileId = scanner.nextLine();

            // Send download request
            String request = "DOWNLOAD " + fileId;
            byte[] sendBuffer = request.getBytes();
            DatagramPacket sendPacket = new DatagramPacket(sendBuffer, sendBuffer.length, serverAddress,
                    FILE_SERVER_PORT);
            clientSocket.send(sendPacket);

            byte[] receiveBuffer = new byte[1024];
            DatagramPacket receivePacket = new DatagramPacket(receiveBuffer, receiveBuffer.length);
            clientSocket.receive(receivePacket);

            String response = new String(receivePacket.getData(), 0, receivePacket.getLength());
            if (response.startsWith("FILE_NOT_FOUND")) {
                System.out.println("File not found.");
                return;
            } else if (response.startsWith("DOWNLOAD_SUCCESS")) {
                String[] parts = response.split(" ");
                String fileName = parts[1];
                long fileSize = Long.parseLong(parts[2]);
                long allChunks = Long.parseLong(parts[3]);

                System.out.println(fileSize);
                System.out.println(allChunks);

                System.out.print("Enter the directory to save the file: ");
                String saveDir = scanner.nextLine();
                File savePath = new File(saveDir, fileName);

                // Array to store chunks
                byte[][] chunks = new byte[TOTAL_CHUNKS][];
                AtomicInteger receivedChunks = new AtomicInteger(0);

                Thread[] threads = new Thread[TOTAL_CHUNKS];

                for (int i = 0; i < TOTAL_CHUNKS; i++) {
                    threads[i] = new Thread(new FileChunkReceiver(clientSocket, chunks,
                            receivedChunks, allChunks, new ThreadInterface() {
                        @Override
                        public void onAllThreadsDone() {
                            for (Thread thread : threads) {
                                thread.interrupt();
                            }

                            // Write received chunks to file
                            try (RandomAccessFile raf = new RandomAccessFile(savePath, "rw")) {
                                for (int i = 0; i < TOTAL_CHUNKS; i++) {
                                    if (chunks[i] != null) {
                                        raf.write(chunks[i]);
                                    }
                                }

                                System.out.println("Download is done!");
                            } catch (Exception ex) {

                            }
                        }
                    }));
                    threads[i].start();
                }

            }
        } catch (Exception e) {
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