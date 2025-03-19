package com.otmj.otmjapp.Helper;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.UUID;

/**
 * ImageHandler class that manages image uploading, fetching, loading, and deleting.
 */
public class ImageHandler {

    /**
     * Uploads an image to Firebase Storage and returns the download URL.
     *
     * @param imageUri  The URI of the image to be uploaded.
     * @param callback  The callback to handle success or failure.
     */
    public static void uploadImage(Uri imageUri, UploadCallback callback) {
        if (imageUri == null) {
            if (callback != null) callback.onFailure(new IllegalArgumentException("Image URI is null"));
            return;
        }

        StorageReference storageRef = FirebaseStorage.getInstance().getReference()
                .child("images/" + UUID.randomUUID().toString() + ".jpg");

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
}
