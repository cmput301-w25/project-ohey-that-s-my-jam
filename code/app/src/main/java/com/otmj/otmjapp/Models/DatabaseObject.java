package com.otmj.otmjapp.Models;

import com.otmj.otmjapp.Helper.FirestoreDB;

public class DatabaseObject {
    private final String ID;
    private final Entity object;
    private FirestoreDB db;

    public DatabaseObject(String ID, Entity object, FirestoreDB db) {
        this.ID = ID;
        this.object = object;
        this.db = db;
    }

    public String getID() {
        return this.ID;
    }

    public <T extends Entity> T getObject() {
        return (T) this.object;
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
