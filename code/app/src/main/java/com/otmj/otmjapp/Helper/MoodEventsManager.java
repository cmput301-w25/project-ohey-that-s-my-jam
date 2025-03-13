package com.otmj.otmjapp.Helper;

import android.graphics.Bitmap;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.firebase.firestore.Filter;
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
public class MoodEventsManager {

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
    private MoodHistoryFilter lastFilter = null;

    public MoodEventsManager(List<String> userIDs) {
        assert !userIDs.isEmpty();

        this.userIDs = new ArrayList<>(userIDs);
        this.db = new FirestoreDB<>(FirestoreCollections.MoodEvents.name);

        db.addCollectionListener(() -> getMoodEvents(lastFilter, MoodEvent.Privacy.Public)); // use here might be incorrect, verify what this code does

        moodHistory = new MutableLiveData<>(new ArrayList<>());
        getMoodEvents(null, MoodEvent.Privacy.Public); // Populate mood history
    }

    /**
     * Gets all mood events from user(s)
     * @return An observable value that returns all the mood events.
     * @see #getMoodEvents(MoodHistoryFilter,MoodEvent.Privacy)
     */
    public LiveData<ArrayList<MoodEvent>> getMoodEvents() {
        // Assume that mood history has been populated (see constructor)
        if (lastFilter == null) {
            return moodHistory;
        } else {
            // If previously getMoodEvents was called with a filter,
            // we need to get all mood events again
            return getMoodEvents(null, MoodEvent.Privacy.Public);
        }
    }

    /**
     * Gets all mood events from user(s) that match the provided filters. (We get the user's
     * information before retrieving mood events.)
     * @param customFilter A filter specifies the condition for the mood event to be returned
     *                     and how to sort it
     * @param privacy The privacy of the mood event (public or private). This is an additional filter
     *                it ensures that only mood events with the specified privacy are returned.
     *
     * @return An observable that returns the filtered mood events
     */
    private LiveData<ArrayList<MoodEvent>> getMoodEvents(MoodHistoryFilter customFilter, MoodEvent.Privacy privacy) {
        // We need the users associated with each mood event
        UserManager.getInstance().getUsers(userIDs, new UserManager.AuthenticationCallback() {
            @Override
            public void onAuthenticated(ArrayList<User> authenticatedUsers) {
                MoodHistoryFilter filter = (customFilter != null)
                        ? customFilter
                        : MoodHistoryFilter.Default(userIDs);

                // Get all mood events from specified users
                db.getDocuments(filter.getFilter(), MoodEvent.class, new FirestoreDB.DBCallback<>() {
                    @Override
                    public void onSuccess(ArrayList<MoodEvent> result) {
                        lastFilter = customFilter;

                        ArrayList<MoodEvent> moodEvents = new ArrayList<>();
                        // For each mood event
                        for (MoodEvent moodEvent : result) {
                            if(moodEvent.getPrivacy() == privacy) { // Process only the mood events that match the privacy
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
                        }

                        moodHistory.setValue(moodEvents);
                    }

                    @Override
                    public void onFailure(Exception e) {
                        // TODO: Do something about error
                        Log.d("MoodEventsManager", e.toString());
                    }
                }, filter.getSortOption());
            }

            @Override
            public void onAuthenticationFailure(String reason) {
                // TODO: Handle case where we're unable to get users
            }
        });

        return moodHistory;
    }

    /**
     * Gets all public mood events from user(s)
     * @param customFilter A filter specifies the condition for the mood event to be returned
     *                     and how to sort it
     *
     * @return An observable value that returns all the mood events.
     * @see #getMoodEvents(MoodHistoryFilter,MoodEvent.Privacy)
     */
    public LiveData<ArrayList<MoodEvent>> getPublicMoodEvents(MoodHistoryFilter customFilter) {
        return getMoodEvents(customFilter, MoodEvent.Privacy.Public);
    }

    /**
     * Gets all private mood events from user(s)
     * @param customFilter A filter specifies the condition for the mood event to be returned
     *                     and how to sort it
     *
     * @return An observable value that returns all the mood events.
     * @see #getMoodEvents(MoodHistoryFilter,MoodEvent.Privacy)
     */
    public LiveData<ArrayList<MoodEvent>> getUserMoodEvents(MoodHistoryFilter customFilter) {
        return getMoodEvents(customFilter, MoodEvent.Privacy.Private);
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

    /**
     * Deletes mood event
     * @param moodEvent mood event to delete
     */
    public void deleteMoodEvent(MoodEvent moodEvent) {
        db.deleteDocument(moodEvent);
    }
}