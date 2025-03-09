package com.otmj.otmjapp.Models;

import java.util.Collections;
import java.util.Map;

/**
 * Follow class extends Entity class
 * Follow class for storing the following userId and the followed userId
 */
public class Follow extends DatabaseObject {
    private final String followerID;
    private final String followeeID;

    // Need an empty constructor for Firebase
    Follow() {
        this.followerID = "";
        this.followeeID = "";
    }

    /**
     * constructor for follow class
     * @param followerID
     *              The UserId following
     * @param followeeID
     *              The UserId being followed
     */
    public Follow(String followerID, String followeeID) {
        this.followerID = followerID;
        this.followeeID = followeeID;
    }

    /**
     * return the UserId following
     * @return follower
     *              the UserId following
     */
    public String getFollowerID() {
        return followerID;
    }

    /**
     * return userId being followed
     * @return  followee
     *               userId being followed
     */
    public String getFolloweeID() {
        return followeeID;
    }
}
