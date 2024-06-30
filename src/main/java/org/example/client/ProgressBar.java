package org.example.client;


public class ProgressBar {

    private long totalSize;
    private long downloaded;

    public ProgressBar(long totalSize) {
        this.totalSize = totalSize;
        this.downloaded = 0;
    }

    public synchronized void update(long bytesRead) {
        downloaded += bytesRead;
        int percent = (int) ((downloaded * 100) / totalSize);
        System.out.print("\rDownloading: " + percent + "%");
    }
}
