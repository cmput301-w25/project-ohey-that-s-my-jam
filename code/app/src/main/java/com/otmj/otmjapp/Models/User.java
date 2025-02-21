package com.otmj.otmjapp.Models;

public class User extends DatabaseObject {
    private String username;

    public User(String id) {
        super(id);
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
