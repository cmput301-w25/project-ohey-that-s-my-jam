package com.otmj.otmjapp.Helper;

import com.otmj.otmjapp.Models.DatabaseObject;
import com.otmj.otmjapp.Models.User;

import java.util.ArrayList;

public class UserManager {
    private final FirestoreDB db;

    public UserManager(FirestoreDB db) {
        this.db = db;
    }

    public ArrayList<User> getUsers() {
        return db.getDocuments("users", User.class);
    }

    public void addUser(User user) {
        db.addDocument("users", user);
    }

    public void updateUsername(User user, String newUsername) {
        user.setUsername(newUsername);
        // do check for valid username before updating
        db.updateDocument("users", user.getId(), user);
    }

    public boolean validUser(String enteredUsername) {
        ArrayList<User> users = db.getDocuments("users", User.class);

        for(User user : users) {
            if(user.getUsername().equals(enteredUsername)) {
                return true;
            }
        }

        return false;
    }

    public boolean validateLogin(String enteredUsername, String enteredPassword) {
        if(validUser(enteredUsername)) {

        }

        return false;
    }
}
