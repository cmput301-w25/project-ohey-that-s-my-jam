package com.otmj.otmjapp.Helper;

import android.content.ContentResolver;
import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.net.Uri;
import android.util.Log;
import android.webkit.MimeTypeMap;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.UUID;

/**
 * ImageHandler class that manages image uploading, fetching, loading, and deleting.
 */
public class ImageHandler {

    private static final String TAG = "ImageHandler";

    /**
     * Uploads an image to Firebase Storage and retrieves the download URL.
     * The download URL is returned asynchronously via the UploadCallback.
     *
     * @param context  The context of the calling activity/fragment.
     * @param imageUri The Uri of the image to be uploaded.
     * @param callback The callback to return the uploaded image URL.
     */
    public static void uploadToFirebaseStorage(Context context, Uri imageUri, UploadCallback callback) {
        if (imageUri == null) {
            Log.e("Firebase Upload", "Image URI is null");
            if (callback != null) callback.onFailure(new IllegalArgumentException("Image URI is null"));
            return;
        }

        String fileExtension = getFileExtension(context, imageUri);
        if (fileExtension == null || fileExtension.isEmpty()) {
            fileExtension = "jpg"; // Default to jpg if unknown
        }

        // Generate a unique filename to prevent overwrites
        String fileName = "images/" + UUID.randomUUID().toString() + "." + fileExtension;
        StorageReference storageRef = FirebaseStorage.getInstance().getReference().child(fileName);

        UploadTask uploadTask = storageRef.putFile(imageUri);

        uploadTask.addOnFailureListener(e -> {
            Log.e("Firebase Upload", "Upload failed: " + e.getMessage());
            if (callback != null) callback.onFailure(e);
        }).addOnSuccessListener(taskSnapshot -> {
            // Retrieve the download URL after a successful upload
            storageRef.getDownloadUrl().addOnSuccessListener(downloadUri -> {
                String imageUrl = downloadUri.toString();
                Log.d("Firebase Upload", "Upload successful, URL: " + imageUrl);
                if (callback != null) callback.onSuccess(imageUrl);
            }).addOnFailureListener(e -> {
                Log.e("Firebase Upload", "Failed to get download URL: " + e.getMessage());
                if (callback != null) callback.onFailure(e);
            });
        });
    }

    /**
     * Callback interface for handling upload results.
     */
    public interface UploadCallback {
        void onSuccess(String imageUrl);
        void onFailure(Exception e);
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
            Log.e(TAG, "Cannot delete: Image URL is null or empty");
            return;
        }

        StorageReference imageRef = FirebaseStorage.getInstance().getReferenceFromUrl(imageUrl);
        imageRef.delete()
                .addOnSuccessListener(aVoid -> Log.d(TAG, "Image successfully deleted: " + imageUrl))
                .addOnFailureListener(e -> Log.e(TAG, "Failed to delete image: " + e.getMessage()));
    }


    /**
     * Retrieves the file extension of a given Uri by extracting its MIME type.
     *
     * @param context The application context.
     * @param uri     The URI of the file whose extension needs to be determined.
     * @return The file extension (e.g., "jpg", "png"), or null if unknown.
     */
    public static String getFileExtension(Context context, Uri uri) {
        ContentResolver contentResolver = context.getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        String mimeType = contentResolver.getType(uri);
        return mime.getExtensionFromMimeType(mimeType);
    }

    /**
     * Retrieves the file size of a given image from its Uri.
     * If the file cannot be accessed, it logs an error and returns 0.
     *
     * @param context  The application context.
     * @param imageUri The Uri of the image whose size needs to be determined.
     * @return The file size in bytes. Returns 0 if the size cannot be determined.
     */
    public static long getImageSize(Context context, Uri imageUri) {
        AssetFileDescriptor fileDescriptor = null;
        long fileSize = 0;

        try {
            fileDescriptor = context.getContentResolver().openAssetFileDescriptor(imageUri, "r");

            if (fileDescriptor != null) {
                fileSize = fileDescriptor.getLength();
            }
        } catch (FileNotFoundException e) {
            Log.e(TAG, "Error retrieving image file size", e);
        } finally {
            if (fileDescriptor != null) {
                try {
                    fileDescriptor.close();
                } catch (IOException e) {
                    Log.e(TAG, "Error closing image file descriptor", e);
                }
            }
        }
        return fileSize;
    }
}
