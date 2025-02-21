package com.otmj.otmjapp.Models;

import com.otmj.otmjapp.Helper.FirestoreDB;
import com.otmj.otmjapp.Utilities.DB;

import java.io.Serializable;

public class DatabaseObject {
    private final long ID;
    private final FirestoreDB dbInstance;
    private final Serializable object;

    public DatabaseObject(long ID, FirestoreDB dbInstance, Serializable object) {
        this.ID = ID;
        this.dbInstance = dbInstance;
        this.object = object;
    }

    public void save() {
    }

    public void delete() {
    }
}
