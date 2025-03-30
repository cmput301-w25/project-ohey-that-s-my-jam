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
import com.otmj.otmjapp.Models.MoodEvent;
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
    private final FirestoreDB<MoodEvent> db;
    /**
     * Observable object that callers can observe to get notified of changes
     */
    private final MutableLiveData<ArrayList<MusicEvent>> musicHistory;
    private MoodHistoryFilter lastFilter = null;
    public MusicEventsManager(List<String> userIDs) {
        assert !userIDs.isEmpty();

        this.userIDs = new ArrayList<>(userIDs);
        this.db = new FirestoreDB<>(FirestoreCollections.MoodEvents.name);
        this.db.addCollectionListener(() -> {
            if (lastFilter != null) {
                getMusicEvents(lastFilter);
            }
        });

        musicHistory = new MutableLiveData<>(new ArrayList<>());
    }

    private LiveData<ArrayList<MusicEvent>> getMusicEvents(@NonNull MoodHistoryFilter filter) { //TODO: music history filter not working
        // We need the users associated with each mood event
        UserManager.getInstance().getUsers(userIDs, new UserManager.AuthenticationCallback() {
            @Override
            public void onAuthenticated(ArrayList<User> authenticatedUsers) {
                // Get all mood events from specified users
                db.getDocuments(filter.getFilter(), MoodEvent.class, new FirestoreDB.DBCallback<>() {
                    @Override
                    public void onSuccess(ArrayList<MoodEvent> result) {
                        lastFilter = filter;

                        ArrayList<MusicEvent> musicEvents = new ArrayList<>();
                        // For each mood event
                        for (MoodEvent moodEvent : result) {
                            if(null != moodEvent.getMusicEvent()) {
                                moodEvent.getMusicEvent().setUser(moodEvent.getUser());
                                musicEvents.add(moodEvent.getMusicEvent());
                            }
                        }

                        musicHistory.setValue(musicEvents);
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

        return musicHistory;
    }

    /**
     * Gets all public mood events from user(s)
     * @param customFilter A filter specifies the condition for the mood event to be returned
     *                     and how to sort it
     *
     * @return An observable value that returns all the music events.
     * @see #getMusicEvents(MoodHistoryFilter)
     */
    public LiveData<ArrayList<MusicEvent>> getPublicMusicEvents(MoodHistoryFilter customFilter) {
        if (customFilter == null) {
            customFilter = MoodHistoryFilter.Default(userIDs);
        }
        customFilter.addFilter(MoodHistoryFilter.PublicMoodEvents());

        return getMusicEvents(customFilter);
    }

    /**
     * Gets all private mood events from user(s)
     * @param customFilter A filter specifies the condition for the mood event to be returned
     *                     and how to sort it
     *
     * @return An observable value that returns all the mood events.
     * @see #getMusicEvents(MoodHistoryFilter)
     */
    public LiveData<ArrayList<MusicEvent>> getUserMusicEvents(MoodHistoryFilter customFilter) {
        return getMusicEvents((null != customFilter)
                ? customFilter
                : MoodHistoryFilter.Default(userIDs));
    }

    /**
     * Uploads the album art of a music event to Firebase Storage.
     * <p>
     * @param imageView  The ImageView object containing the image to be uploaded.
     * @param imageName  The name of the image file
     * @param moodEvent The MoodEvent object that contains the MusicEvent with the album art
     */
    public void uploadAlbumArtToStorage(ImageView imageView, String imageName, MoodEvent moodEvent) {
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

            getDownloadURL("track_albums/" + filename, url -> {
                Log.d("MusicEventsManager", "Download URL: " + url);
                moodEvent.getMusicEvent().setAlbumArtURL(url);

                // update the mood event so that its reference to the music event contains the
                // album art url
                db.updateDocument(moodEvent);

            });
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
}
