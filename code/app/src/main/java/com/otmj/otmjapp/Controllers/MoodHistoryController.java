package com.otmj.otmjapp.Controllers;

import com.google.android.gms.tasks.Task;
import com.otmj.otmjapp.Helper.FirestoreCollections;
import com.otmj.otmjapp.Helper.FirestoreDB;
import com.otmj.otmjapp.Helper.MoodHistoryFilter;
import com.otmj.otmjapp.Models.DatabaseObject;
import com.otmj.otmjapp.Models.MoodEvent;
import com.otmj.otmjapp.Models.User;

import java.util.ArrayList;

/**
 * Helper class to work with database in retrieving mood events
 * of specified user(s).
 */
public class MoodHistoryController {
    /**
     * Specifies the user(s) from which to get mood events
     */
    private final User[] users;
    /**
     * Holds its own reference to the mood events db collection
     */
    private final FirestoreDB<MoodEvent> moodEventsDB;

    public MoodHistoryController(User... users) {
        assert users.length >= 1;

        this.users = users.clone();
        this.moodEventsDB = new FirestoreDB<>(FirestoreCollections.MoodEvents.name);
    }

    /**
     * Gets all mood events from user(s)
     * @return An asynchronous task that returns all the mood events
     */
    public Task<ArrayList<MoodEvent>> getMoodEvents() {

    }

    /**
     * Gets all mood events from user(s) that match the provided filters
     * @param filter A filter specifies the condition for the mood event to be returned
     * @return An asynchronous task that returns the filtered mood events
     * @see #getMoodEvents()
     */
    public Task<ArrayList<MoodEvent>> getMoodEvents(MoodHistoryFilter filter) {

    }

    /**
     * Downloads the image of a mood event to the user's local storage
     * @param moodEvent A mood event retrieved from the database might have an image link property.
     *                  So, we download the image specified by the link
     * @return An asynchronous task to the image file path
     */
    public Task<String> downloadMoodEventImage(DatabaseObject<MoodEvent> moodEvent) {

    }
}
