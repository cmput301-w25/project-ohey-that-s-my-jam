package com.otmj.otmjapp.Helper;

import android.graphics.Bitmap;
import android.util.Log;

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

        moodHistory = new MutableLiveData<>(new ArrayList<>());
    }

    /**
     * Gets all mood events from user(s)
     * @return An observable value that returns all the mood events.
     * @see #getMoodEvents(MoodHistoryFilter)
     */
    public LiveData<ArrayList<MoodEvent>> getMoodEvents() {
        // Assume that mood history has been populated (see constructor)
        if (lastFilter == null) {
            return moodHistory;
        } else {
            // If previously getMoodEvents was called with a filter,
            // we need to get all mood events again
            return getMoodEvents(null);
        }
    }

    /**
     * Gets all mood events from user(s) that match the provided filters. (We get the user's
     * information before retrieving mood events.)
     *
     * @param customFilter A filter specifies the condition for the mood event to be returned
     *                     and how to sort it
     * @return An observable that returns the filtered mood events
     */
    private LiveData<ArrayList<MoodEvent>> getMoodEvents(MoodHistoryFilter customFilter) {
        // We need the users associated with each mood event
        UserManager.getInstance().getUsers(userIDs, new UserManager.AuthenticationCallback() {
            @Override
            public void onAuthenticated(ArrayList<User> authenticatedUsers) {
                MoodHistoryFilter filter = (null != customFilter)
                        ? customFilter
                        : MoodHistoryFilter.Default(userIDs);

                // Get all mood events from specified users
                db.getDocuments(filter.getFilter(), MoodEvent.class, new FirestoreDB.DBCallback<>() {
                    @Override
                    public void onSuccess(ArrayList<MoodEvent> result) {
                        lastFilter = customFilter;

                        ArrayList<MoodEvent> moodEvents = new ArrayList<>();
                        // For each mood event //
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

                        if(moodEvents.isEmpty()) {
                            moodHistory.setValue(new ArrayList<>());
                        } else {
                            moodHistory.setValue(moodEvents);
                        }
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
     * @see #getMoodEvents(MoodHistoryFilter)
     */
    public LiveData<ArrayList<MoodEvent>> getPublicMoodEvents(MoodHistoryFilter customFilter) {
        MoodHistoryFilter defaultFilter = new MoodHistoryFilter(Filter.equalTo("privacy", MoodEvent.Privacy.Public),
                                   new DBSortOption("createdDate", true));
        customFilter = (null != customFilter)
                       ? customFilter
                       : defaultFilter;


        return getMoodEvents(customFilter);
    }

    /**
     * Gets all private mood events from user(s)
     * @param customFilter A filter specifies the condition for the mood event to be returned
     *                     and how to sort it
     *
     * @return An observable value that returns all the mood events.
     * @see #getMoodEvents(MoodHistoryFilter)
     */
    public LiveData<ArrayList<MoodEvent>> getUserMoodEvents(MoodHistoryFilter customFilter) {
        return getMoodEvents(customFilter);
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