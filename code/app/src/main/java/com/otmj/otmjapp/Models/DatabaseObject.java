package com.otmj.otmjapp.Models;

import com.otmj.otmjapp.Helper.FirestoreDB;

public class DatabaseObject {
    private final String id;
    private final FirestoreDB db;
    public DatabaseObject(String id, FirestoreDB db) {
        this.id = id;
        this.db = db;
    }

    public String getId() {
        return id;
    }
}
