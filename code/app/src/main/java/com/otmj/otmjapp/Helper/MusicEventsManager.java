package com.otmj.otmjapp.Helper;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.ImageView;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.otmj.otmjapp.Models.MusicEvent;
import com.otmj.otmjapp.Models.User;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

public class MusicEventsManager {
    public interface URICallback {
        void onSuccess(String uri);
    }

    /**
     * Specifies the user(s) from which to get mood events
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
    private MusicHistoryFilter lastFilter = null;
    public MusicEventsManager(List<String> userIDs) {
        assert !userIDs.isEmpty();

        this.userIDs = new ArrayList<>(userIDs);
        this.db = new FirestoreDB<>(FirestoreCollections.MusicEvents.name);
        this.db.addCollectionListener(() -> {
            if (lastFilter != null) {
                getMusicEvents(lastFilter);
            }
        });

        musicHistory = new MutableLiveData<>(new ArrayList<>());
    }

    private LiveData<ArrayList<MusicEvent>> getMusicEvents(@NonNull MusicHistoryFilter filter) {
        UserManager.getInstance().getUsers(userIDs, new UserManager.AuthenticationCallback() {
            @Override
            public void onAuthenticated(ArrayList<User> authenticatedUsers) {
                // Get all mood events from specified users
                db.getDocuments(filter.getFilter(), MusicEvent.class, new FirestoreDB.DBCallback<>() {
                    @Override
                    public void onSuccess(ArrayList<MusicEvent> result) {
                        lastFilter = filter;

                        ArrayList<MusicEvent> musicEvents = new ArrayList<>();

                        for (MusicEvent musicEvent : result) {
                            // If the filter contains a query text and mood event has "reason"
                            if(filter.getQueryText() != null && musicEvent.getFeeling() != null) {
                                // Then only add music events that match query
                                String reason = musicEvent.getFeeling().trim().toLowerCase();
                                if (reason.contains(filter.getQueryText())) {
                                    musicEvents.add(musicEvent);
                                }
                            } else {
                                musicEvents.add(musicEvent);
                            }

                            // Look through all the users
                            for (User u : authenticatedUsers) {
                                // When we get the user associated with the mood event
                                if (u.getID().equals(musicEvent.getUser().getID())) {
                                    musicEvent.setUser(u);
                                    break;
                                }
                            }
                        }

                        musicHistory.setValue(musicEvents);
                    }

                    @Override
                    public void onFailure(Exception e) {
                        // TODO: Do something about error
                        Log.d("MusicEventsManager", e.toString());
                    }
                }, filter.getSortOption());
            }

            @Override
            public void onAuthenticationFailure(String reason) {
                // TODO: Handle case where we're unable to get users
                Log.d("MusicEventsManager", reason);
            }
        });

        return musicHistory;
    }

    /**
     * Gets all public mood events from user(s)
     * @param customFilter A filter specifies the condition for the mood event to be returned
     *                     and how to sort it
     *
     * @return An observable value that returns all the music events.
     * @see #getMusicEvents(MusicHistoryFilter)
     */
    public LiveData<ArrayList<MusicEvent>> getPublicMusicEvents(MusicHistoryFilter customFilter) {
        if (customFilter == null) {
            customFilter = MusicHistoryFilter.Default(userIDs);
        }
        customFilter.addFilter(MusicHistoryFilter.PublicMoodEvents());

        return getMusicEvents(customFilter);
    }

    /**
     * Gets all private mood events from user(s)
     * @param customFilter A filter specifies the condition for the mood event to be returned
     *                     and how to sort it
     *
     * @return An observable value that returns all the mood events.
     * @see #getMusicEvents(MusicHistoryFilter)
     */
    public LiveData<ArrayList<MusicEvent>> getUserMusicEvents(MusicHistoryFilter customFilter) {
        return getMusicEvents((null != customFilter)
                ? customFilter
                : MusicHistoryFilter.Default(userIDs));
    }

    /**
     * Insert new music event to database
     * <p>
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
                    // TODO: Handle this
                }
            }

            @Override
            public void onFailure(Exception e) {
                // TODO: Show error message
            }
        });
    }

    /**
     * Uploads the album art of a music event to Firebase Storage.
     * <p>
     * @param imageView  The ImageView object containing the image to be uploaded.
     * @param imageName  The name of the image file
     * @param musicEvent The MusicEvent object associated with the image.
     */
    public void uploadAlbumArtToStorage(ImageView imageView, String imageName, MusicEvent musicEvent) {
        // create reference to file in storage
        StorageReference storageRef = FirebaseStorage.getInstance().getReference();
        String filename = imageName + ".jpg";
        StorageReference albumArtImagesRef = storageRef.child("track_albums/" + filename);

        Log.d("FirebaseStorage", "Upload path: " + albumArtImagesRef.toString());

        // get bitmap from ImageView
        imageView.setDrawingCacheEnabled(true);
        imageView.buildDrawingCache();

        Bitmap bitmap = ((BitmapDrawable) imageView.getDrawable()).getBitmap();

        // compress bitmap into JPEG format
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 80, baos);
        byte[] data = baos.toByteArray();

        // sends byte array to to Firebase Storage
        UploadTask uploadTask = albumArtImagesRef.putBytes(data);

        uploadTask.addOnFailureListener(e -> {
            Log.d("MusicEventsManager", "Image upload failed: " + e.getMessage());
        }).addOnSuccessListener(taskSnapshot -> {
            Log.d("MusicEventsManager", "Image upload successful");

            getDownloadURL("track_albums/" + filename, musicEvent::setAlbumArtURL);
        });
    }

    /**
     * Deletes the album art from Firebase Storage.
     * <p>
     * @param musicEvent The MusicEvent object associated with the image.
     */
    public void deleteAlbumArtFromStorage(MusicEvent musicEvent) {
        String filename = musicEvent.getTrack().getTitle() + musicEvent.getTrack().getArtists().get(0) + ".jpg";

        // create reference to file in storage
        StorageReference storageRef = FirebaseStorage.getInstance().getReference();
        StorageReference albumArtImagesRef = storageRef.child("track_albums/" + filename);

        //delete file
        albumArtImagesRef.delete()
                .addOnSuccessListener(aVoid -> {
                    Log.d("MusicEventsManager", "Image deletion successful");
                })
                .addOnFailureListener(e -> {
                    Log.e("FirebaseStorage", "Error deleting file: " + e.getMessage());
                });
    }

    /**
     * Retrieves the download url for a track's album art.
     * <p>
     * @param filepath The path to the album art's image in Firebase Storage.
     * @param callback The callback to invoke when the download URL is retrieved.
     */
    public void getDownloadURL(String filepath, URICallback callback) {
        StorageReference storageRef = FirebaseStorage.getInstance().getReference().child(filepath);

        storageRef.getDownloadUrl().addOnSuccessListener(uri -> {
            Log.d("MusicEventsManager", "Download URL: " + uri.toString());
            callback.onSuccess(uri.toString());
        }).addOnFailureListener(e -> {
            Log.d("MusicEventsManager", "Download URL failed: " + e.getMessage());
        });
    }

    /**
     * Updates the album art of a music event in the database.
     * <p>
     * @param musicEvent The MusicEvent object to update.
     */
    public void updateMusicEvent(MusicEvent musicEvent) {
        db.updateDocument(musicEvent);
    }

    /**
     * Deletes a music event from the database.
     * <p>
     * @param musicEvent The MusicEvent object to delete.
     */
    public void deleteMusicEvent(MusicEvent musicEvent) { //TODO should be called with deleteMoodEvent
        deleteAlbumArtFromStorage(musicEvent);
        db.deleteDocument(musicEvent);
    }
}
