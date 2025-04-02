package com.otmj.otmjapp.Helper;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import com.google.firebase.firestore.Filter;
import com.otmj.otmjapp.Models.FirestoreCollections;
import com.otmj.otmjapp.Models.MoodEvent;
import com.otmj.otmjapp.Models.MusicEvent;

import java.util.ArrayList;
import java.util.List;

public class MusicEventsManager {
    public interface MusicEventCallback {
        void onComplete(MusicEvent musicEvent);
    }

    /**
     * Specifies the user(s) from which to get music events
     */
    private final ArrayList<String> userIDs;
    /**
     * Holds its own reference to the mood events db collection
     */
    private final FirestoreDB<MusicEvent> db;
    /**
     * Observable object that callers can observe to get notified of changes
     */
    private final MutableLiveData<ArrayList<MusicEvent>> musicHistory;

    public MusicEventsManager(List<String> userIDs) {
        assert !userIDs.isEmpty();

        this.userIDs = new ArrayList<>(userIDs);
        this.db = new FirestoreDB<>(FirestoreCollections.MusicEvents.name);

        musicHistory = new MutableLiveData<>(new ArrayList<>());
    }

    /**
     * Get all music events of given users
     * @param onlyPublic    Specifies whether to get only public music events
     *                      or only both private and public
     * @param callback      Callback to handle result
     */
    public void getAllMusicEvents(boolean onlyPublic, MusicEventCallback callback) {
        Filter filter = Filter.inArray("userID", userIDs);
        db.getDocuments(filter, MusicEvent.class, new FirestoreDB.DBCallback<>() {
            @Override
            public void onSuccess(ArrayList<MusicEvent> result) {
                MoodEventsManager moodEventsManager = new MoodEventsManager(userIDs);
                for (MusicEvent musicEvent : result) {
                    // Get mood event details
                    moodEventsManager.getMoodEvent(musicEvent.getMoodEventID(), m -> {
                        if (m != null) {
                            // If onlyPublic is set, then only return public music event
                            if (!onlyPublic || m.getPrivacy() == MoodEvent.Privacy.Public) {
                                musicEvent.setMoodEvent(m);
                                musicEvent.setUser(m.getUser());

                                callback.onComplete(musicEvent);
                            }
                        }
                    });
                }
            }

            @Override
            public void onFailure(Exception e) {
                Log.e("MusicEventsManager", "Unable to retrieve music events");
            }
        });
    }

    /**
     * Get music event of specified mood event of one of the given users
     * @param moodEventID   Associated mood event id
     */
    public void getAssociatedMusicEvent(String moodEventID, MusicEventCallback callback) {
        Filter filter = Filter.and(
                Filter.inArray("userID", userIDs),
                Filter.equalTo("moodEventID", moodEventID)
        );
        db.getDocuments(filter, MusicEvent.class, new FirestoreDB.DBCallback<>() {
            @Override
            public void onSuccess(ArrayList<MusicEvent> result) {
                if (!result.isEmpty()) {
                    callback.onComplete(result.get(0));
                }
            }

            @Override
            public void onFailure(Exception e) {}
        });
    }

    /**
     * Insert new music event to database
     * @param musicEvent Music event to insert
     */
    public void addMusicEvent(MusicEvent musicEvent) {
        db.addDocument(musicEvent, new FirestoreDB.DBCallback<>() {
            @Override
            public void onSuccess(ArrayList<MusicEvent> result) {
                if (!result.isEmpty()) {
                    MusicEvent m = result.get(0);

                    if (musicHistory.getValue() == null) {
                        musicHistory.setValue(new ArrayList<>(List.of(m)));
                    } else {
                        musicHistory.getValue().add(m);
                    }
                } else {
                    Log.e("MusicEventsManager", "Unable to add music event to DB");
                }
            }

            @Override
            public void onFailure(Exception e) {
                Log.e("MusicEventsManager", e.toString());
            }
        });
    }

    /**
     * Updates the album art of a music event in the database.
     * @param musicEvent The MusicEvent object to update.
     */
    public void updateMusicEvent(MusicEvent musicEvent) {
        ArrayList<MusicEvent> musicEvents = musicHistory.getValue();
        if (musicEvents != null) {
            int indexOfMusicEvent = musicEvents.indexOf(musicEvent);
            if (indexOfMusicEvent != -1) {
                // Update music event at that index
                musicEvents.remove(indexOfMusicEvent);
                musicEvents.add(indexOfMusicEvent, musicEvent);
            }
        }
        db.updateDocument(musicEvent);
    }

    /**
     * Deletes a music event from the database.
     * @param musicEvent The MusicEvent object to delete.
     */
    public void deleteMusicEvent(MusicEvent musicEvent) {
        if (musicHistory.getValue() != null) {
            musicHistory.getValue().remove(musicEvent);
        }
        db.deleteDocument(musicEvent);
    }

    /**
     * Delete the music event associated with specified mood event
     * @param moodEventID   ID of mood event associated with some music event
     */
    public void deleteMusicEvent(String moodEventID) {
        // First get the music event
        getAssociatedMusicEvent(moodEventID, m -> {
            // Then delete it
            deleteMusicEvent(moodEventID);
        });
    }
}
