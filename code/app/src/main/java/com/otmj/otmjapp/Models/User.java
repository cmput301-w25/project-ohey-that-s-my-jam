package com.otmj.otmjapp.Models;

import com.otmj.otmjapp.Helper.FirestoreDB;

public class User extends DatabaseObject {
    private String username;

    public User(String id, FirestoreDB db) {
        super(id, db);
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
