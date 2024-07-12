package client;

import server.FileTransferServer;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.concurrent.atomic.AtomicInteger;

class FileChunkReceiver implements Runnable {
    private DatagramSocket clientSocket;
    private byte[][] chunks;
    private AtomicInteger receivedChunks;
    private long allChunks;
    private ThreadInterface threadInterface;

    public FileChunkReceiver(DatagramSocket clientSocket, byte[][] chunks, AtomicInteger receivedChunks,
                             long allChunks, ThreadInterface threadInterface) {
        this.clientSocket = clientSocket;
        this.chunks = chunks;
        this.receivedChunks = receivedChunks;
        this.allChunks = allChunks;
        this.threadInterface = threadInterface;
    }

    @Override
    public void run() {
        try {
            byte[] receiveBuffer = new byte[FileTransferServer.CHUNK_SIZE + 4];

            while (true) {
                // System.out.println(receivedChunks.get());
                DatagramPacket receivePacket = new DatagramPacket(receiveBuffer, receiveBuffer.length);
                clientSocket.receive(receivePacket);
                String recievedMessage = new String(receivePacket.getData(), 0, receivePacket.getLength());

                // System.out.println(recievedMessage);

                if (recievedMessage.startsWith("Done")) {
                    receivedChunks.incrementAndGet();

                    if (receivedChunks.get() >= FileTransferServer.TOTAL_CHUNKS) {
                        System.out.println("Done");
                        threadInterface.onAllThreadsDone();
                        return;
                    }
                } else {
                    byte[] data = receivePacket.getData();
                    int chunkIndex = ((data[0] & 0xFF) << 24) | ((data[1] & 0xFF) << 16) | ((data[2] & 0xFF) << 8)
                            | (data[3] & 0xFF);

                    if (chunkIndex >= 0 && chunkIndex < chunks.length) {
                        byte[] chunkData = new byte[receivePacket.getLength() - 4];
                        System.arraycopy(data, 4, chunkData, 0, chunkData.length);

                        if (chunks[chunkIndex] == null) {
                            chunks[chunkIndex] = chunkData;
                            // receivedChunks.incrementAndGet();
                        } else {
                            chunks[chunkIndex] = concatenate(chunks[chunkIndex], chunkData);
                            // receivedChunks.incrementAndGet();
                        }
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static byte[] concatenate(byte[] array1, byte[] array2) {
        // Create a new array to hold the combined contents
        byte[] result = new byte[array1.length + array2.length];

        // Copy the first array into the result array
        System.arraycopy(array1, 0, result, 0, array1.length);

        // Copy the second array into the result array, starting where the first array
        // ends
        System.arraycopy(array2, 0, result, array1.length, array2.length);

        return result;
    }
}