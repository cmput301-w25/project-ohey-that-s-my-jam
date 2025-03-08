package com.otmj.otmjapp.Models;

import com.google.firebase.firestore.Exclude;

import java.io.Serializable;
import java.util.Map;

/**
 * Wrapper class for objects that are retrieved from database
 */
public abstract class DatabaseObject implements Serializable {
    @Exclude
    private String ID;

    public DatabaseObject() {}

    public DatabaseObject(String ID) {
        this.ID = ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getID() {
        return this.ID;
    }

    public abstract Map<String, Object> toMap();
}
