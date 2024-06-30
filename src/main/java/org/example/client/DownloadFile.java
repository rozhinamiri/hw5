package org.example.client;
import java.io.*;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class DownloadFile {

    public static void downloadFile(Socket socket, String fileId, String fileName) {
        try {
            // Request file download from server
            DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
            dos.writeUTF("DOWNLOAD");
            dos.writeUTF(fileId);

            // Read file size
            DataInputStream dis = new DataInputStream(socket.getInputStream());
            long fileSize = dis.readLong();

            File file = new File(fileName);
            FileOutputStream fos = new FileOutputStream(file);

            // Define the number of threads based on file size and network speed (simplified example)
            int numThreads = (int) (fileSize / (1024 * 1024 * 10)) + 1;
            ExecutorService executor = Executors.newFixedThreadPool(numThreads);
            ProgressBar progressBar = new ProgressBar(fileSize);

            for (int i = 0; i < numThreads; i++) {
                long start = i * (fileSize / numThreads);
                long end = (i + 1) * (fileSize / numThreads) - 1;

                if (i == numThreads - 1) {
                    end = fileSize - 1;
                }

                executor.execute(new DownloadTask(socket, fos, start, end, progressBar));
            }

            executor.shutdown();
            while (!executor.isTerminated()) {
                // Wait for all threads to finish
            }

            fos.close();
            System.out.println("Download complete!");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static class DownloadTask implements Runnable {
        private Socket socket;
        private FileOutputStream fos;
        private long start;
        private long end;
        private ProgressBar progressBar;

        public DownloadTask(Socket socket, FileOutputStream fos, long start, long end, ProgressBar progressBar) {
            this.socket = socket;
            this.fos = fos;
            this.start = start;
            this.end = end;
            this.progressBar = progressBar;
        }

        @Override
        public void run() {
            try {
                DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
                dos.writeUTF("DOWNLOAD_PART");
                dos.writeLong(start);
                dos.writeLong(end);

                DataInputStream dis = new DataInputStream(socket.getInputStream());
                byte[] buffer = new byte[4096];
                int bytesRead;
                long totalRead = 0;

                while ((bytesRead = dis.read(buffer)) != -1) {
                    fos.write(buffer, 0, bytesRead);
                    totalRead += bytesRead;
                    progressBar.update(totalRead);
                    if (totalRead >= (end - start + 1)) {
                        break;
                    }
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
