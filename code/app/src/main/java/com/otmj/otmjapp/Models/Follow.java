package com.otmj.otmjapp.Models;

/**
 * Follow class extends Entity class
 * Follow class for storing the following user and the followed user
 */
public class Follow extends Entity{
    private User follower;
    private User followee;

    /**
     * constructor for follow class
     * @param follower
     *              The User following
     * @param followee
     *              The User being followed
     */
    public Follow(User follower, User followee) {
        this.follower = follower;
        this.followee = followee;
    }

    /**
     * return the User following
     * @return follower
     *              the User following
     */
    public User getFollower() {
        return follower;
    }

    /**
     * set user following
     * @param follower
     *              the User following
     */
    public void setFollower(int follower) {
        this.follower = follower;
    }

    /**
     * return user being followed
     * @return  followee
     *               user being followed
     */
    public User getFollowee() {
        return followee;
    }

    /**
     * set user being followed
     * @param followee
     *              user being followed
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
                "follower=" + follower.getUsername() +
                ", followee=" + followee.getUsername() +
                '}';
    }
}
