package com.otmj.otmjapp.Controllers;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.firebase.firestore.Filter;
import com.otmj.otmjapp.Helper.FirestoreCollections;
import com.otmj.otmjapp.Helper.FirestoreDB;
import com.otmj.otmjapp.Helper.MoodHistoryFilter;
import com.otmj.otmjapp.Models.DatabaseObject;
import com.otmj.otmjapp.Models.MoodEvent;
import com.otmj.otmjapp.Models.User;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Helper class to work with database in retrieving mood events
 * of specified user(s).
 * Most methods return a LiveData object. This is because getting
 * data from a database happens asynchronously, we use LiveData
 * to be able to observe when its value is set.
 */
public class MoodHistoryController {
    /**
     * Specifies the user(s) from which to get mood events
     */
    private final ArrayList<String> userIDs;
    /**
     * Holds its own reference to the mood events db collection
     */
    private final FirestoreDB<MoodEvent> moodEventsDB;
    /**
     * Observable object that callers can observe to get notified of changes
     */
    private final MutableLiveData<ArrayList<DatabaseObject<MoodEvent>>> moodHistory;

    public MoodHistoryController(String... userIDs) {
        assert userIDs.length >= 1;

        this.userIDs = new ArrayList<>(Arrays.asList(userIDs));
        this.moodEventsDB = new FirestoreDB<>(FirestoreCollections.MoodEvents.name);

        moodHistory = new MutableLiveData<>();
    }

    /**
     * Gets all mood events from user(s)
     * @return An observable value that returns all the mood events.
     * @see #getMoodEvents(MoodHistoryFilter)
     */
    public LiveData<ArrayList<DatabaseObject<MoodEvent>>> getMoodEvents() {
        return getMoodEvents(null);
    }

    /**
     * Gets all mood events from user(s) that match the provided filters
     * @param filter A filter specifies the condition for the mood event to be returned
     * @return An observable that returns the filtered mood events
     */
    public LiveData<ArrayList<DatabaseObject<MoodEvent>>> getMoodEvents(MoodHistoryFilter filter) {
        // Specify to return only mood events where the 'user' property is one of `users`
        Filter fromUsers = Filter.inArray("userID", userIDs);

        Filter filterWithUserFilter;
        if (filter != null) {
            filterWithUserFilter = Filter.and(fromUsers, filter.getFilter());
        } else {
            filterWithUserFilter = fromUsers;
        }

        moodEventsDB.getDocuments(filterWithUserFilter, new FirestoreDB.DBCallback<>() {
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
     * Downloads the image of a mood event to the user's local storage
     * @param moodEvent A mood event retrieved from the database might have an image link property.
     *                  So, we download the image specified by the link
     * @return An observable to the image file path
     */
    public LiveData<String> downloadMoodEventImage(DatabaseObject<MoodEvent> moodEvent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
