package com.otmj.otmjapp;

public class FollowRequest {

    private int followee;
    private int follower;
    public FollowRequest(int follower, int followee) {
        // follower: requested userId
        // followee: requesting userID
        this.followee = followee;
        this.follower = follower;
    }

    public int getFollowee() {
        return followee;
    }

    public void setFollowee(int followee) {
        this.followee = followee;
    }

    public int getFollower() {
        return follower;
    }

    public void setFollower(int follower) {
        this.follower = follower;
    }

    @Override
    public String toString() {
        return "FollowRequest{" +
                "followee=" + followee +
                ", follower=" + follower +
                '}';
    }
}
