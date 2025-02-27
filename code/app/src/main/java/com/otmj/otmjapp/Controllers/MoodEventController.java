package com.otmj.otmjapp.Controllers;

import android.graphics.Bitmap;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.firebase.firestore.Filter;
import com.otmj.otmjapp.Helper.FirestoreCollections;
import com.otmj.otmjapp.Helper.FirestoreDB;
import com.otmj.otmjapp.Helper.MoodHistoryFilter;
import com.otmj.otmjapp.Models.DatabaseObject;
import com.otmj.otmjapp.Models.MoodEvent;

import java.util.ArrayList;
import java.util.Objects;

/**
 * Helper class to work with database in retrieving mood events
 * of specified user(s).
 * Most methods return a LiveData object. This is because getting
 * data from a database happens asynchronously, we use LiveData
 * to be able to observe when its value is set.
 */
public class MoodEventController {

    public interface ImageDownloadCallback {
        public void onSuccess(Bitmap image);
        public void onFailure(Exception e);
    }

    /**
     * Specifies the user(s) from which to get mood events
     */
    private final ArrayList<String> userIDs;
    /**
     * Holds its own reference to the mood events db collection
     */
    private final FirestoreDB<MoodEvent> db;
    /**
     * Observable object that callers can observe to get notified of changes
     */
    private final MutableLiveData<ArrayList<DatabaseObject<MoodEvent>>> moodHistory;

    public MoodEventController(ArrayList<String> userIDs) {
        assert !userIDs.isEmpty();

        this.userIDs = userIDs;
        this.db = new FirestoreDB<>(FirestoreCollections.MoodEvents.name);

        moodHistory = new MutableLiveData<>(new ArrayList<>());
        getMoodEvents(null); // Populate mood history
    }

    /**
     * Gets all mood events from user(s)
     * @return An observable value that returns all the mood events.
     * @see #getMoodEvents(MoodHistoryFilter)
     */
    public LiveData<ArrayList<DatabaseObject<MoodEvent>>> getMoodEvents() {
        // Assume that mood history has been populated (see constructor)
        return moodHistory;
    }

    /**
     * Gets all mood events from user(s) that match the provided filters
     * @param customFilter A filter specifies the condition for the mood event to be returned
     * @return An observable that returns the filtered mood events
     */
    public LiveData<ArrayList<DatabaseObject<MoodEvent>>> getMoodEvents(MoodHistoryFilter customFilter) {
        // Specify to return only mood events where the 'user' property is one of `users`
        Filter filter = Filter.inArray("userID", userIDs);
        // If a custom filter is specified, AND it to `filter`
        if (customFilter != null) {
            filter = Filter.and(filter, customFilter.getFilter());
        }

        db.getDocuments(filter, new FirestoreDB.DBCallback<>() {
            @Override
            public void onSuccess(ArrayList<DatabaseObject<MoodEvent>> result) {
                moodHistory.setValue(result);
            }

            @Override
            public void onFailure(Exception e) {
                // TODO: Do something about error
            }
        });

        return moodHistory;
    }

    /**
     * Insert new mood event to database
     * @param moodEvent Mood event to insert
     */
    public void addMoodEvent(MoodEvent moodEvent) {
        db.addDocument(moodEvent, new FirestoreDB.DBCallback<>() {
            @Override
            public void onSuccess(ArrayList<DatabaseObject<MoodEvent>> result) {
                ArrayList<DatabaseObject<MoodEvent>> arr =
                    Objects.requireNonNull(moodHistory.getValue());
                arr.add(result.get(0));

                moodHistory.notifyAll(); // Notify observers
            }

            @Override
            public void onFailure(Exception e) {
                // TODO: Show error message
            }
        });
    }

    /**
     * Downloads the image of a mood event to the user's local storage
     * @param moodEvent A mood event retrieved from the database might have an image link property.
     *                  So, we download the image specified by the link
     * @param downloadCallback Callback for handling success or failure of download operation
     */
   public void downloadMoodEventImage(MoodEvent moodEvent, ImageDownloadCallback downloadCallback) {
        assert moodEvent.getImageLink() != null;

        // TODO: Implement downloading
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
