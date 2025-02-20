package com.otmj.otmjapp;

public class Follow {
    private int follower;
    private int followee;

    public Follow(int follower, int followee) {
        this.follower = follower;
        this.followee = followee;
    }

    public int getFollower() {
        return follower;
    }

    public void setFollower(int follower) {
        this.follower = follower;
    }

    public int getFollowee() {
        return followee;
    }

    public void setFollowee(int followee) {
        this.followee = followee;
    }

    @Override
    public String toString() {
        return "Follow{" +
                "follower=" + follower +
                ", followee=" + followee +
                '}';
    }
}
