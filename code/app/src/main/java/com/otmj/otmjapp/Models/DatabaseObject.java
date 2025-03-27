package com.otmj.otmjapp.Models;

import com.google.firebase.firestore.DocumentId;

import java.io.Serializable;

/**
 * Wrapper class that serves as a base for objects that are retrieved from database.
 * Extended by other model classes that need to associate an ID with their instance.
 */
public abstract class DatabaseObject implements Serializable {
    @DocumentId
    private String ID;

    public DatabaseObject() { }

    public DatabaseObject(String ID) {
        this.ID = ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getID() {
        return this.ID;
    }
}
