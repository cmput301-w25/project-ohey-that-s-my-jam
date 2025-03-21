package com.otmj.otmjapp.Models;


public class Comment {
    private String userId;
    private String username;
    private String commentText;
    private String timestamp;
    private String moodEventId;
    private String profilePictureUrl;
    public Comment() {}

    public Comment(String userId, String username, String commentText, String timestamp, String moodEventId, String profilePictureUrl) {
        this.userId = userId;
        this.username = username;
        this.commentText = commentText;
        this.timestamp = timestamp;
        this.moodEventId = moodEventId;
        this.profilePictureUrl = profilePictureUrl;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getCommentText() {
        return commentText;
    }

    public void setCommentText(String commentText) {
        this.commentText = commentText;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getMoodEventId() {
        return moodEventId;
    }

    public void setMoodEventId(String moodEventId) {
        this.moodEventId = moodEventId;
    }

    public String getProfilePictureUrl() {
        return profilePictureUrl;
    }

    public void setProfilePictureUrl(String profilePictureUrl) {
        this.profilePictureUrl = profilePictureUrl;
    }


}