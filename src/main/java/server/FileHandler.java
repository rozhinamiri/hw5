package server;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class FileHandler {

    private List<FileInfo> fileList;

    public FileHandler() {
        fileList = new ArrayList<>();
        loadFiles();
    }

    private void loadFiles() {
        File dir = new File("server_files");
        if (!dir.exists()) {
            dir.mkdir();
        }

        for (File file : dir.listFiles()) {
            if (file.isFile()) {
                fileList.add(new FileInfo(file.getName(), file.getName(), file.length()));
            }
        }
    }

    public List<FileInfo> getFileList() {
        return fileList;
    }
}

