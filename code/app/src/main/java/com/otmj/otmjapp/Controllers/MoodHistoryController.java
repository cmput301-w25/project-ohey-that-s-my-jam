package com.otmj.otmjapp.Controllers;

import com.otmj.otmjapp.Helper.FirestoreCollections;
import com.otmj.otmjapp.Helper.FirestoreDB;

public class MoodHistoryController {
    private final FirestoreDB db;

    public MoodHistoryController() {
        this.db = new FirestoreDB(FirestoreCollections.MoodEvents.name);
    }


}
