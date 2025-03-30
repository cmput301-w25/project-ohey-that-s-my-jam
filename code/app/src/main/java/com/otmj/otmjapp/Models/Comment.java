package com.otmj.otmjapp.Models;

import com.google.firebase.firestore.Exclude;
import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;
import java.util.Objects;

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

    public Comment(String userId,
                   String moodEventId,
                    String commentText) {
        this.userId = userId;
        this.moodEventId = moodEventId;
        this.commentText = commentText;
    }

    public String getUserId() {
        return userId;
    }

    public void setUser(final User user) {
        this.user = user;
    }

    public User getUser() {
        return user;
    }

    public String getCommentText() {
        return commentText;
    }

    public void setCommentText(String commentText) {
        this.commentText = commentText;
    }

    public Date getTimestamp() {
        if (timestamp == null) {
            timestamp = new Date();
        }
        return timestamp;
    }

    public String getMoodEventId() {
        return moodEventId;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Comment)) return false;
        Comment comment = (Comment) o;
        return Objects.equals(moodEventId, comment.moodEventId)
                && Objects.equals(userId, comment.userId)
                && Objects.equals(timestamp, comment.timestamp)
                && Objects.equals(commentText, comment.commentText);
    }

    @Override
    public int hashCode() {
        return Objects.hash(moodEventId, userId, timestamp, commentText);
    }
}
