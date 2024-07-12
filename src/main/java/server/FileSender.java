package server;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class FileSender implements Runnable {
    private DatagramSocket serverSocket;
    private String fileId;
    private InetAddress clientAddress;
    private int clientPort;

    public FileSender(DatagramSocket serverSocket, String fileId, InetAddress clientAddress, int clientPort) {
        this.serverSocket = serverSocket;
        this.fileId = fileId;
        this.clientAddress = clientAddress;
        this.clientPort = clientPort;
    }

    public static File findFilePathById(String directoryPath, String id) {
        File directory = new File(directoryPath);

        if (!directory.exists() || !directory.isDirectory()) {
            System.err.println("The provided path is not a valid directory.");
            return null;
        }

        FilenameFilter filter = new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return name.startsWith(id + "_");
            }
        };

        File[] matchingFiles = directory.listFiles(filter);

        if (matchingFiles != null && matchingFiles.length > 0) {
            return matchingFiles[0];
        }

        return null;
    }

    @Override
    public void run() {
        try {
            // Simulated database lookup
            // File file = findFilePathById("server_files", fileId);
            File file = new File("server_files/819830cafeec50a623b9bd024f1eb7b82a5f6a4fae789ff0a0696937a844bf_gpu.png");

            if (file == null) {
                sendResponse("FILE_NOT_FOUND");
                return;
            }

            long fileSize = file.length();
            long chunkSize = fileSize / FileTransferServer.TOTAL_CHUNKS;
            long remainingSize = fileSize % FileTransferServer.TOTAL_CHUNKS;

            long allChunks = (long) Math.ceil((double) fileSize / (double) FileTransferServer.CHUNK_SIZE);

            sendResponse("DOWNLOAD_SUCCESS " + file.getName() + " " + fileSize + " " + allChunks);

            for (int i = 0; i < FileTransferServer.TOTAL_CHUNKS; i++) {
                long start = i * chunkSize;
                long end = (i == FileTransferServer.TOTAL_CHUNKS - 1) ? (fileSize)
                        : (start + chunkSize);

                System.out.println("Start = " + start + " end = " + end);

                new Thread(new FileChunkSender(serverSocket, file, start, end, i, clientAddress, clientPort)).start();
                // break;
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void sendResponse(String message) throws IOException {
        byte[] sendBuffer = message.getBytes();
        DatagramPacket sendPacket = new DatagramPacket(sendBuffer, sendBuffer.length, clientAddress, clientPort);
        serverSocket.send(sendPacket);
    }
}