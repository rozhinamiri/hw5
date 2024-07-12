package server;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

class FileChunkSender implements Runnable {
    private DatagramSocket serverSocket;
    private File file;
    private long start;
    private long end;
    private int chunkIndex;
    private InetAddress clientAddress;
    private int clientPort;

    private int progress = 0;

    public FileChunkSender(DatagramSocket serverSocket, File file, long start, long end, int chunkIndex,
                           InetAddress clientAddress, int clientPort) {
        this.serverSocket = serverSocket;
        this.file = file;
        this.start = start;
        this.end = end;
        this.chunkIndex = chunkIndex;
        this.clientAddress = clientAddress;
        this.clientPort = clientPort;
    }

    @Override
    public void run() {
        try (RandomAccessFile raf = new RandomAccessFile(file, "r")) {
            raf.seek(start);

            byte[] buffer = new byte[FileTransferServer.CHUNK_SIZE];
            long remaining = end - start;
            int bytesRead;

            while (remaining > 0 && (bytesRead = raf.read(buffer, 0, (int) Math.min(buffer.length, remaining))) != -1) {
                byte[] chunkData = new byte[bytesRead + 4];
                System.arraycopy(buffer, 0, chunkData, 4, bytesRead);

                // Add chunk index as the first 4 bytes
                chunkData[0] = (byte) (chunkIndex >> 24);
                chunkData[1] = (byte) (chunkIndex >> 16);
                chunkData[2] = (byte) (chunkIndex >> 8);
                chunkData[3] = (byte) (chunkIndex);

                DatagramPacket sendPacket = new DatagramPacket(chunkData, chunkData.length, clientAddress, clientPort);
                serverSocket.send(sendPacket);

                remaining -= bytesRead;
            }

            System.out.println(remaining);
            sendResponse("Done " + chunkIndex);

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