package com.otmj.otmjapp.Models;

import com.google.firebase.firestore.DocumentId;
import com.google.firebase.firestore.Exclude;

import java.io.Serializable;

/**
 * Wrapper class for objects that are retrieved from database
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

    @Exclude
    public String getID() {
        return this.ID;
    }
}
