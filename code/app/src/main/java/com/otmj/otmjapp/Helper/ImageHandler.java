package com.otmj.otmjapp.Helper;

import android.content.ContentResolver;
import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.net.Uri;
import android.util.Log;
import android.webkit.MimeTypeMap;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.UUID;

/**
 * ImageHandler class that manages image uploading, fetching, loading, and deleting.
 */
public class ImageHandler {

    public static void uploadImage(Context context, Uri imageUri, UploadCallback callback) {
        if (imageUri == null) {
            if (callback != null) callback.onFailure(new IllegalArgumentException("Image URI is null"));
            return;
        }

        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Ensure user is logged in
        if (auth.getCurrentUser() == null) {
            Log.e("Firebase Upload", "User not signed in!");
            if (callback != null) callback.onFailure(new SecurityException("You must be signed in to upload!"));
            return;
        }

        String userId = auth.getCurrentUser().getUid();
        DocumentReference userRef = db.collection("users").document(userId);

        // Check if user is an admin before allowing upload
        userRef.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists() && documentSnapshot.contains("isAdmin")) {
                boolean isAdmin = documentSnapshot.getBoolean("isAdmin");

                if (isAdmin) {
                    // User is an admin, proceed with upload
                    uploadToFirebaseStorage(context, imageUri, callback);
                } else {
                    // User is NOT an admin
                    Log.e("Firebase Upload", "User is not an admin!");
                    if (callback != null) callback.onFailure(new SecurityException("You are not authorized to upload images!"));
                }
            } else {
                Log.e("Firebase Upload", "User document missing 'isAdmin' field!");
                if (callback != null) callback.onFailure(new SecurityException("Authorization check failed!"));
            }
        }).addOnFailureListener(e -> {
            Log.e("Firebase Upload", "Error fetching user data: " + e.getMessage());
            if (callback != null) callback.onFailure(e);
        });
    }

    /**
     * This function actually uploads the file to Firebase Storage after the admin check.
     */
    private static void uploadToFirebaseStorage(Context context, Uri imageUri, UploadCallback callback) {
        String fileExtension = getFileExtension(context, imageUri);
        String fileName = "images/" + UUID.randomUUID().toString() + "." + fileExtension;
        StorageReference storageRef = FirebaseStorage.getInstance().getReference().child(fileName);

        storageRef.putFile(imageUri)
                .addOnSuccessListener(taskSnapshot ->
                        storageRef.getDownloadUrl().addOnSuccessListener(downloadUri -> {
                            String imageUrl = downloadUri.toString();
                            if (callback != null) callback.onSuccess(imageUrl);
                        })
                )
                .addOnFailureListener(e -> {
                    if (callback != null) callback.onFailure(e);
                });
    }


    /**
     * Loads an image into an ImageView using Glide.
     *
     * @param context   The context from which this method is called.
     * @param imageUrl  The URL of the image to be loaded.
     * @param imageView The ImageView where the image will be displayed.
     */
    public static void loadImage(Context context, String imageUrl, ImageView imageView) {
        Glide.with(context)
                .load(imageUrl)
                .into(imageView);
    }

    /**
     * Deletes an image from Firebase Storage.
     *
     * @param imageUrl The URL of the image to be deleted.
     */
    public static void deleteImage(String imageUrl) {
        if (imageUrl == null || imageUrl.isEmpty()) {
            Log.e("Image Delete", "Cannot delete: Image URL is null or empty");
            return;
        }

        StorageReference imageRef = FirebaseStorage.getInstance().getReferenceFromUrl(imageUrl);

        imageRef.delete()
                .addOnSuccessListener(aVoid -> Log.d("Image Delete", "Image successfully deleted: " + imageUrl))
                .addOnFailureListener(e -> Log.e("Image Delete", "Failed to delete image: " + e.getMessage()));
    }

    /**
     * Callback interface for handling upload results.
     */
    public interface UploadCallback {
        void onSuccess(String imageUrl);
        void onFailure(Exception e);
    }

    /**
     * Retrieves the file extension of a given Uri by extracting its MIME type.
     * This ensures the correct format is used when uploading images to Firebase Storage.
     *
     * @param context The application context, used to access the ContentResolver.
     * @param uri     The URI of the file whose extension needs to be determined.
     * @return        The file extension (e.g., "jpg", "png"), or null if unknown.
     */
    private static String getFileExtension(Context context, Uri uri) {
        // Get an instance of ContentResolver to access file details
        ContentResolver contentResolver = context.getContentResolver();

        // Get an instance of MimeTypeMap, which maps MIME types to file extensions
        MimeTypeMap mime = MimeTypeMap.getSingleton();

        // Retrieve the MIME type (e.g., "image/png", "image/jpeg") of the given file URI
        String mimeType = contentResolver.getType(uri);

        // Use MimeTypeMap to convert the MIME type into a file extension (e.g., "png" from "image/png")
        return mime.getExtensionFromMimeType(mimeType);
    }

    /**
     * Retrieves the file size of a given image from its Uri.
     * If the file cannot be accessed, it logs an error and returns 0.
     *
     * @param context  The application context, used to access the ContentResolver.
     * @param imageUri The Uri of the image whose size needs to be determined.
     * @return The file size in bytes. Returns 0 if the size cannot be determined.
     */
    public static long getFileSize(Context context, Uri imageUri) {
        AssetFileDescriptor fileDescriptor = null;
        long fileSize = 0;

        try {
            // Open file descriptor to retrieve metadata
            fileDescriptor = context.getContentResolver().openAssetFileDescriptor(imageUri, "r");

            // If the descriptor is valid, get the file length
            if (fileDescriptor != null) {
                fileSize = fileDescriptor.getLength();
            }
        } catch (FileNotFoundException e) {
            Log.e("File Size Check", "Error retrieving file size", e);
        } finally {
            // Close the file descriptor to free up system resources
            if (fileDescriptor != null) {
                try {
                    fileDescriptor.close();
                } catch (IOException e) {
                    Log.e("File Size Check", "Error closing file descriptor", e);
                }
            }
        }
        return fileSize;
    }

}
