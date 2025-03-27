package com.otmj.otmjapp.Models;

import com.google.firebase.firestore.Exclude;
import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;

/**
 * Represents a comment made on a mood event.
 * A comment is associated with a specific mood event, the user who wrote it and includes
 * the date and time it was created.
 */
public class Comment extends DatabaseObject {
    private final String moodEventId;
    private final String userId;
    @Exclude
    private User user = null;
    @ServerTimestamp
    private Date timestamp;
    private String commentText;

    public Comment() {
        userId = "";
        moodEventId = "";
    }

    /**
     * Creates a new Comment by accepting three parameters.
     *
     * @param userId      The unique document ID of a user in the users collection of the database.
     * @param moodEventId The unique document ID of a mood event in the mood_events collection of the database.
     * @param commentText The text content of the comment.
     */
    public Comment(String userId,
                   String moodEventId,
                    String commentText) {
        this.userId = userId;
        this.moodEventId = moodEventId;
        this.commentText = commentText;
    }

    /**
     * Gets the ID of the user who made the comment.
     *
     * @return The user ID as string.
     */
    public String getUserId() {
        return userId;
    }

    /**
     * Sets the user associated with this comment.
     *
     * @param user The User object representing the commenter.
     */
    public void setUser(final User user) {
        this.user = user;
    }

    /**
     * Gets the user object associated with this comment.
     *
     * @return The User object if it was set, otherwise returns null.
     */
    public User getUser() {
        return user;
    }

    /**
     * Gets the text of the comment.
     *
     * @return The comment text as a String.
     */
    public String getCommentText() {
        return commentText;
    }

    /**
     * Sets the text of the comment.
     *
     * @param commentText The new comment text.
     */
    public void setCommentText(String commentText) {
        this.commentText = commentText;
    }

    /**
     * Gets the timestamp of when the comment was created.
     * If the timestamp is null, it initializes to the current date.
     *
     * @return The timestamp of the comment as a Date object.
     */
    public Date getTimestamp() {
        if (timestamp == null) {
            timestamp = new Date();
        }
        return timestamp;
    }

    /**
     * Gets the ID of the mood event that this comment is associated with.
     *
     * @return The mood event ID as a String.
     */
    public String getMoodEventId() {
        return moodEventId;
    }
}
