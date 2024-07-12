package models;

// import com.google.gson.Gson;
// import com.google.gson.reflect.TypeToken;
import server.FileInfo;

import java.io.*;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class Database {
    public static List<Users> usersList = new ArrayList<>();
    public static List<FileInfo> fileList = new ArrayList<>();

    private static final String USERS_FILE = "users.json";
    private static final String FILES_FILE = "files.json";
    // private static final Gson gson = new Gson();

    public static void saveToJSON() {
        try (Writer writer = new FileWriter(USERS_FILE)) {
            // gson.toJson(usersList, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }

        try (Writer writer = new FileWriter(FILES_FILE)) {
            // gson.toJson(fileList, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void loadFromJSON() {
        try (Reader reader = new FileReader(USERS_FILE)) {
            // Type userListType = new TypeToken<ArrayList<Users>>() {}.getType();
            // usersList = gson.fromJson(reader, userListType);
        } catch (FileNotFoundException e) {
            System.out.println("Users file not found, starting with an empty list.");
        } catch (IOException e) {
            e.printStackTrace();
        }

        try (Reader reader = new FileReader(FILES_FILE)) {
            // Type fileListType = new TypeToken<ArrayList<FileInfo>>() {}.getType();
            // fileList = gson.fromJson(reader, fileListType);
        } catch (FileNotFoundException e) {
            System.out.println("Files file not found, starting with an empty list.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
