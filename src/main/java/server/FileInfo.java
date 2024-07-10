package server;

public class FileInfo {

    private String fileId;
    private String fileName;
    private long fileSize;

    public FileInfo(String fileId, String fileName, long fileSize) {
        this.fileId = fileId;
        this.fileName = fileName;
        this.fileSize = fileSize;
    }

    public String getFileId() {
        return fileId;
    }

    public String getFileName() {
        return fileName;
    }

    public long getFileSize() {
        return fileSize;
    }
}

