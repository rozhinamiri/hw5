package server;

import models.Users;

import java.util.ArrayList;
import java.util.List;

public class UserHandler {

    private List<Users> userList;

    public UserHandler() {
        userList = new ArrayList<>();
    }

    public void addUser(Users user) {
        userList.add(user);
    }

    public Users getUser(String username) {
        for (Users user : userList) {
            if (user.getUsername().equals(username)) {
                return user;
            }
        }
        return null;
    }

    public List<Users> getUserList() {
        return userList;
    }
}

