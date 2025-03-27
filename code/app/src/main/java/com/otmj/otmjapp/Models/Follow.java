package com.otmj.otmjapp.Models;

import java.util.Collections;
import java.util.Map;

/**
 * Represents a follow relationship between two users.
 * Stores the IDs of both the following user (follower) and the followed user (followee).
 */
public class Follow extends DatabaseObject {
    private final String followerID;
    private final String followeeID;

    /**
     * Initializes both follower and followee IDs as empty strings.
     * Default constructor required for Firebase serialization.
     */
    Follow() {
        this.followerID = "";
        this.followeeID = "";
    }

    /**
     * Constructs a new Follow instance.
     *
     * @param followerID The ID of the user following as a string.
     * @param followeeID The ID of the user being followed as a string.
     */
    public Follow(String followerID, String followeeID) {
        this.followerID = followerID;
        this.followeeID = followeeID;
    }

    /**
     * Returns the ID of the user who is following.
     *
     * @return The follower's user ID as a string.
     */
    public String getFollowerID() {
        return followerID;
    }

    /**
     * Returns the ID of the user being followed.
     *
     * @return The followee's user ID as a string.
     */
    public String getFolloweeID() {
        return followeeID;
    }
}
