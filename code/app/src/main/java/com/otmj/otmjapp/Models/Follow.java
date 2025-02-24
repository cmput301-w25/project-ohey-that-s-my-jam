package com.otmj.otmjapp.Models;

/**
 * Follow class extends Entity class
 * Follow class for storing the following userId and the followed userId
 */
public class Follow extends Entity{
    private int follower;
    private int followee;

    /**
     * constructor for follow class
     * @param follower
     *              The UserId following
     * @param followee
     *              The UserId being followed
     */
    public Follow(int follower, int followee) {
        this.follower = follower;
        this.followee = followee;
    }

    /**
     * return the UserId following
     * @return follower
     *              the UserId following
     */
    public int getFollower() {
        return follower;
    }

    /**
     * set userId following
     * @param follower
     *              the UserId following
     */
    public void setFollower(int follower) {
        this.follower = follower;
    }

    /**
     * return userId being followed
     * @return  followee
     *               userId being followed
     */
    public int getFollowee() {
        return followee;
    }

    /**
     * set userId being followed
     * @param followee
     *              userId being followed
     */
    public void setFollowee(int followee) {
        this.followee = followee;
    }

    /**
     * String representation of Follow class
     * @return Follow
     */
    @Override
    public String toString() {
        return "Follow{" +
                "follower=" + follower +
                ", followee=" + followee +
                '}';
    }
}
