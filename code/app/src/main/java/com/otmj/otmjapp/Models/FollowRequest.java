package com.otmj.otmjapp.Models;

/**
 * Represents a follow request between two users.
 */
public class FollowRequest extends Follow {
    // Need an empty constructor for Firebase
    FollowRequest() {
        super("", "");
    }

    public FollowRequest(String followerID, String followeeID){
        super(followerID, followeeID);
    }
}
