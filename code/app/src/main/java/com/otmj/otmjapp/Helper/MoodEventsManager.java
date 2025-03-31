package com.otmj.otmjapp.Helper;

import android.graphics.Bitmap;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.otmj.otmjapp.Models.FirestoreCollections;
import com.otmj.otmjapp.Models.MoodEvent;
import com.otmj.otmjapp.Models.MoodHistoryFilter;
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
        void onSuccess(Bitmap image);
        void onFailure(Exception e);
    }

    public interface MoodEventCallback {
        void onComplete(MoodEvent moodEvent);
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

        db.addCollectionListener(() -> {
            if (lastFilter != null) {
                getMoodEvents(lastFilter);
            }
        });

        moodHistory = new MutableLiveData<>(new ArrayList<>());
    }

    /**
     * Get specific mood event from database
     * @param moodEventID   ID of mood event
     * @param callback      Called when mood event retrieved
     */
    public void getMoodEvent(String moodEventID, MoodEventCallback callback) {
        db.getDocuments(
                MoodHistoryFilter.SpecificMoodEvent(moodEventID).getFilter(),
                MoodEvent.class,
                new FirestoreDB.DBCallback<>() {
                    @Override
                    public void onSuccess(ArrayList<MoodEvent> result) {
                        if (!result.isEmpty()) {
                            callback.onComplete(result.get(0));
                        } else {
                            callback.onComplete(null);
                        }
                    }

                    @Override
                    public void onFailure(Exception e) {
                        callback.onComplete(null);
                    }
                }
        );
    }

    /**
     * Gets all mood events from user(s) that match the provided filters. (We get the user's
     * information before retrieving mood events.)
     *
     * @param filter    A filter specifies the condition for the mood event to be returned
     *                  and how to sort it
     * @return An observable that returns the filtered mood events
     */
    private LiveData<ArrayList<MoodEvent>> getMoodEvents(@NonNull MoodHistoryFilter filter) {
        // We need the users associated with each mood event
        UserManager.getInstance().getUsers(userIDs, new UserManager.AuthenticationCallback() {
            @Override
            public void onAuthenticated(ArrayList<User> authenticatedUsers) {
                // Get all mood events from specified users
                db.getDocuments(filter.getFilter(), MoodEvent.class, new FirestoreDB.DBCallback<>() {
                    @Override
                    public void onSuccess(ArrayList<MoodEvent> result) {
                        lastFilter = filter;

                        ArrayList<MoodEvent> moodEvents = new ArrayList<>();
                        // For each mood event
                        for (MoodEvent moodEvent : result) {
                            // If the filter contains a query text and mood event has "reason"
                            if (filter.getQueryText() != null && moodEvent.getReason() != null) {
                                // Then only add mood events that match query
                                String reason = moodEvent.getReason().trim().toLowerCase();
                                if (reason.contains(filter.getQueryText())) {
                                    moodEvents.add(moodEvent);
                                } else {
                                    // Move to next mood event if current mood event does not match
                                    continue;
                                }
                            } else {
                                moodEvents.add(moodEvent);
                            }

                            // Look through all the users
                            for (User u : authenticatedUsers) {
                                // When we get the user associated with the mood event
                                if (u.getID().equals(moodEvent.getUserID())) {
                                    moodEvent.setUser(u);
                                    break;
                                }
                            }

//                            Log.d("MoodEventsManager", moodEvent.toString());
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
                Log.d("MoodEventsManager", reason);
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
        if (customFilter == null) {
            customFilter = MoodHistoryFilter.Default(userIDs);
        }
        customFilter.addFilter(MoodHistoryFilter.PublicMoodEvents());

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
    public LiveData<ArrayList<MoodEvent>> getAllMoodEvents(MoodHistoryFilter customFilter) {
        return getMoodEvents((null != customFilter)
                ? customFilter
                : MoodHistoryFilter.Default(userIDs));
    }

    /**
     * Get moodEvents that have location and is public
     * @return An observable value that returns mood events that have location.
     */
    public LiveData<ArrayList<MoodEvent>> getMoodEventsWithLocation() {
        MoodHistoryFilter filter = MoodHistoryFilter.Default(userIDs);
        // Get only public mood events
        filter.addFilter(MoodHistoryFilter.PublicMoodEvents());
        // That have a location
        filter.addFilter(MoodHistoryFilter.HasLocation());

        return getMoodEvents(filter);
    }

    /**
     * Get moodEvents that have location
     * @return An observable value that returns mood events that have location.
     */
    public LiveData<ArrayList<MoodEvent>> getMyMoodEventsWithLocation() {
        // to get the moodevents with locations that current user has
        MoodHistoryFilter filter = MoodHistoryFilter.Default(userIDs);
        // That have a location
        filter.addFilter(MoodHistoryFilter.HasLocation());

        return getMoodEvents(filter);
    }

    /**
     * Insert new mood event to database and specify what to do when inserted
     * @param moodEvent Mood event to insert
     * @param callback  Specifies what to do after mood event is inserted into database
     */
    public void addMoodEvent(MoodEvent moodEvent, MoodEventCallback callback) {
        db.addDocument(moodEvent, new FirestoreDB.DBCallback<MoodEvent>() {
            @Override
            public void onSuccess(ArrayList<MoodEvent> result) {
                if (!result.isEmpty()) {
                    callback.onComplete(result.get(0));
                }
            }

            @Override
            public void onFailure(Exception e) {
                // TODO: Show error message
            }
        });
    }
  
    /**
     * Insert new mood event to database
     * @param moodEvent Mood event to insert
     */
    public void addMoodEvent(MoodEvent moodEvent) {
       addMoodEvent(moodEvent, m -> {
            if (moodHistory.getValue() == null) {
                moodHistory.setValue(new ArrayList<>(List.of(m)));
            } else {
                moodHistory.getValue().add(m);
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