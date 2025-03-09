package com.otmj.otmjapp.Controllers;

import android.graphics.Bitmap;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.firebase.firestore.Filter;
import com.otmj.otmjapp.Helper.FirestoreCollections;
import com.otmj.otmjapp.Helper.FirestoreDB;
import com.otmj.otmjapp.Helper.MoodHistoryFilter;
import com.otmj.otmjapp.Helper.UserManager;
import com.otmj.otmjapp.Models.MoodEvent;
import com.otmj.otmjapp.Models.User;

import java.util.ArrayList;
import java.util.List;

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
    private final MutableLiveData<ArrayList<MoodEvent>> moodHistory;

    public MoodEventController(List<String> userIDs) {
        assert !userIDs.isEmpty();

        this.userIDs = new ArrayList<>(userIDs);
        this.db = new FirestoreDB<>(FirestoreCollections.MoodEvents.name);

        moodHistory = new MutableLiveData<>(new ArrayList<>());
        getMoodEvents(null); // Populate mood history
    }

    /**
     * Gets all mood events from user(s)
     * @return An observable value that returns all the mood events.
     * @see #getMoodEvents(MoodHistoryFilter)
     */
    public LiveData<ArrayList<MoodEvent>> getMoodEvents() {
        // Assume that mood history has been populated (see constructor)
        return moodHistory;
    }

    /**
     * Gets all mood events from user(s) that match the provided filters. (We get the user's
     * information before retrieving mood events.)
     * @param customFilter A filter specifies the condition for the mood event to be returned
     * @return An observable that returns the filtered mood events
     */
    public LiveData<ArrayList<MoodEvent>> getMoodEvents(MoodHistoryFilter customFilter) {
        // We need the users associated with each mood event
        UserManager.getInstance().getUsers(userIDs, new UserManager.AuthenticationCallback() {
            @Override
            public void onAuthenticated(ArrayList<User> authenticatedUsers) {
                // Specify to return only mood events where the 'user' property is one of `users`
                Filter filter = Filter.inArray("userID", userIDs);
                // If a custom filter is specified, AND it to `filter`
                if (customFilter != null) {
                    filter = Filter.and(filter, customFilter.getFilter());
                }

                // Get all mood events from specified users
                db.getDocuments(filter, MoodEvent.class, new FirestoreDB.DBCallback<>() {
                    @Override
                    public void onSuccess(ArrayList<MoodEvent> result) {
                        ArrayList<MoodEvent> moodEvents = new ArrayList<>();
                        // For each mood event
                        for (MoodEvent moodEvent : result) {
                            moodEvents.add(moodEvent);

                            // Look through all the users
                            for (User u : authenticatedUsers) {
                                // When we get the user associated with the mood event
                                if (u.getID().equals(moodEvent.getUserID())) {
                                    moodEvent.setUser(u);
                                    break;
                                }
                            }
                        }

                        moodHistory.setValue(moodEvents);
                    }

                    @Override
                    public void onFailure(Exception e) {
                        // TODO: Do something about error
                    }
                });
            }

            @Override
            public void onAuthenticationFailure(String reason) {
                // TODO: Handle case where we're unable to get users
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
            public void onSuccess(ArrayList<MoodEvent> result) {
                if (!result.isEmpty()) {
                    MoodEvent m = result.get(0);

                    if (moodHistory.getValue() == null) {
                        moodHistory.setValue(new ArrayList<>(List.of(m)));
                    } else {
                        moodHistory.getValue().add(m);
                    }
                } else {
                    // TODO: Handle this
                }
            }

            @Override
            public void onFailure(Exception e) {
                // TODO: Show error message
            }
        });
    }

    public void updateMoodEvent(MoodEvent moodEvent) {
        db.updateDocument(moodEvent);

        ArrayList<MoodEvent> moodEvents = moodHistory.getValue();
        if (moodEvents != null) {
            int index = moodEvents.indexOf(moodEvent);
            moodEvents.set(index, moodEvent);
        }
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
