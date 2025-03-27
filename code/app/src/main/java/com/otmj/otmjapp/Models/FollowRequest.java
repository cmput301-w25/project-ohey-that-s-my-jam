package com.otmj.otmjapp.Models;

/**
 * Represents a follow request between two users.
 */
public class FollowRequest extends Follow {
    /**
     * Default constructor required for Firebase serialization.
     * Initializes the follower and followee IDs as empty strings.
     */
    FollowRequest() {
        super("", "");
    }

    /**
     * Constructs a new FollowRequest instance.
     *
     * @param followerID The ID of the user sending the follow request as a string.
     * @param followeeID The ID of the user receiving the follow request as a string.
     */
    public FollowRequest(String followerID, String followeeID){
        super(followerID, followeeID);
    }
}
