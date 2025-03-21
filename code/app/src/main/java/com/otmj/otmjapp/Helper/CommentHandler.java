package com.otmj.otmjapp.Helper;

import android.util.Log;
import android.widget.Toast;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.otmj.otmjapp.Adapters.CommentAdapter;
import com.otmj.otmjapp.Models.Comment;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class CommentHandler {
    private static final String TAG = "CommentHandler";
    private final FirebaseFirestore db;

    public CommentHandler() {
        this.db = FirebaseFirestore.getInstance();
    }

    /**
     * Retrieves comments from Firestore for a specific MoodEvent ID.
     */
    public void loadComments(String moodEventId, CommentAdapter commentsAdapter) {
        db.collection("comments")
                .whereEqualTo("moodEventId", moodEventId)
                .orderBy("timestamp", Query.Direction.DESCENDING)
                // Real time reload
                .addSnapshotListener((queryDocumentSnapshots, e) -> {
                    if (e != null) {
                        Log.e(TAG, "Error listening for comment changes", e);
                        return;
                    }

                    if (queryDocumentSnapshots == null) return;

                    List<Comment> comments = new ArrayList<>();
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        Comment comment = document.toObject(Comment.class);
                        comments.add(comment);
                    }

                    commentsAdapter.clear();
                    commentsAdapter.addAll(comments);
                    commentsAdapter.notifyDataSetChanged();
                });
    }



    /**
     * Saves a new comment to Firestore and ensures UI is updated only after successful save.
     */
    public void addComment(String commentText, String moodEventId, CommentAdapter commentsAdapter, String userId, String username) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Fetch additional user details (if needed)
        db.collection("users").document(userId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String profilePictureUrl = documentSnapshot.getString("profilePictureUrl");
                        if (profilePictureUrl == null) profilePictureUrl = "";

                        // Create the Comment object
                        Comment comment = new Comment(
                                userId,
                                username,
                                commentText,
                                new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date()),
                                moodEventId,
                                profilePictureUrl
                        );

                        // Upload the comment to Firestore
                        db.collection("comments")
                                .add(comment)
                                .addOnSuccessListener(documentReference -> {
                                    // Reload the comments after successfully adding
                                    loadComments(moodEventId, commentsAdapter);
                                })
                                .addOnFailureListener(e -> Log.e("Firestore", "Error adding comment", e));
                    } else {
                        Log.e("Firestore", "User document does not exist");
                    }
                })
                .addOnFailureListener(e -> Log.e("Firestore", "Error fetching user data", e));
    }
}