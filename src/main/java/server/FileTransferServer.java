package server;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class FileTransferServer {
    public static final int PORT = 9876;
    public static final int CHUNK_SIZE = 4096;
    public static final int TOTAL_CHUNKS = 5;

    public static void main(String[] args) throws IOException {
        DatagramSocket serverSocket = new DatagramSocket(PORT);
        byte[] receiveBuffer = new byte[1024];

        while (true) {
            DatagramPacket receivePacket = new DatagramPacket(receiveBuffer, receiveBuffer.length);
            serverSocket.receive(receivePacket);
            String request = new String(receivePacket.getData(), 0, receivePacket.getLength());

            if (request.startsWith("DOWNLOAD")) {
                String fileId = request.split(" ")[1];
                InetAddress clientAddress = receivePacket.getAddress();
                int clientPort = receivePacket.getPort();

                // Handle the download request in a new thread
                new Thread(new FileSender(serverSocket, fileId, clientAddress, clientPort)).start();
            }
        }
    }
}