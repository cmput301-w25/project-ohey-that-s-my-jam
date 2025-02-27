package com.otmj.otmjapp.Helper;

import androidx.annotation.NonNull;

public enum FirestoreCollections {
    Users("users"),
    MoodEvents("mood_events"),
    Follows("follows"),
    FollowRequests("follow_requests");

    public final String name;

    private FirestoreCollections(String name) {
        this.name = name;
    }

    @NonNull
    @Override
    public String toString() {
        return name;
    }
}
