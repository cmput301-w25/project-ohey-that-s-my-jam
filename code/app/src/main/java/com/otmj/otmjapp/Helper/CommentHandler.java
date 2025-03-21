package com.otmj.otmjapp.Helper;

import android.util.Log;

import com.google.firebase.firestore.Filter;
import com.otmj.otmjapp.Adapters.CommentAdapter;
import com.otmj.otmjapp.Models.Comment;
import com.otmj.otmjapp.Models.User;

import java.util.ArrayList;
import java.util.List;

public class CommentHandler {

    public interface CommentCountsCallback {
        void result(int count);
    }

    private static final String TAG = "CommentHandler";
    private final FirestoreDB<Comment> db;

    public CommentHandler() {
        db = new FirestoreDB<>(FirestoreCollections.Comments.name);
    }

    /**
     * Retrieves comments from Firestore for a specific MoodEvent ID.
     */
    private void getComments(String moodEventId, FirestoreDB.DBCallback<Comment> callback) {
        Filter filter = Filter.equalTo("moodEventId", moodEventId);
        DBSortOption sortOption = new DBSortOption("timestamp", true);

        db.getDocuments(filter, Comment.class, callback, sortOption);
    }

    /**
     * Get comments with the associated user details and send data to adapter
     */
    public void loadComments(String moodEventId, CommentAdapter commentsAdapter) {
        getComments(moodEventId, new FirestoreDB.DBCallback<>() {
            @Override
            public void onSuccess(ArrayList<Comment> comments) {
                UserManager userManager = UserManager.getInstance();

                commentsAdapter.clear();
                // For each comment
                for (Comment comment : comments) {
                    // Get the user that made the comment
                    userManager.getUsers(List.of(comment.getUserId()), new UserManager.AuthenticationCallback() {
                        @Override
                        public void onAuthenticated(ArrayList<User> users) {
                            if (!users.isEmpty()) {
                                comment.setUser(users.get(0));

                                commentsAdapter.add(comment);
                                commentsAdapter.notifyDataSetChanged();
                            } else {
                                Log.e(TAG, "User document does not exist.");
                            }
                        }

                        @Override
                        public void onAuthenticationFailure(String reason) {
                            Log.e(TAG, "Error fetching user data");
                        }
                    });
                }
            }

            @Override
            public void onFailure(Exception e) {
                Log.e(TAG, "Error listening for comment changes", e);
            }
        });
    }

    /**
     * Saves a new comment to Firestore and ensures UI is updated only after successful save.
     */
    public void addComment(String commentText,
                           String moodEventId,
                           String userId,
                           CommentAdapter commentsAdapter) {
        // Create a comment
        Comment comment = new Comment(userId, moodEventId, commentText);
        // Add it to the db
        db.addDocument(comment, new FirestoreDB.DBCallback<>() {
            @Override
            public void onSuccess(ArrayList<Comment> result) {
                loadComments(moodEventId, commentsAdapter);
            }

            @Override
            public void onFailure(Exception e) {
                Log.e("Firestore", "Error adding comment", e);
            }
        });
    }

    public void getCommentCount(String moodEventId, CommentCountsCallback callback) {
        getComments(moodEventId, new FirestoreDB.DBCallback<>() {
            @Override
            public void onSuccess(ArrayList<Comment> result) {
                callback.result(result.size());
            }

            @Override
            public void onFailure(Exception e) {
                callback.result(-1);
            }
        });
    }
}
