package com.otmj.otmjapp.Models;

import com.otmj.otmjapp.Helper.FirestoreDB;

/**
 * Wrapper class for objects that are retrieved from database
 */
// TODO: Add Java Docs
public class DatabaseObject<T extends Entity> {
    private final String ID;
    private final T object;
    private FirestoreDB db;

    public DatabaseObject(String ID, T object, FirestoreDB db) {
        this.ID = ID;
        this.object = object;
        this.db = db;
    }

    public String getID() {
        return this.ID;
    }

    public T getObject() {
        return object;
    }

    public void save() {
        if (db != null) {
            db.updateDocument(this);
        }
    }

    public void delete() {
        if (db != null) {
            db.deleteDocument(this);
            db = null;
        }
    }
}
