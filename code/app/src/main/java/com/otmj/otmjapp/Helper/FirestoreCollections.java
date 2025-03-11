package com.otmj.otmjapp.Helper;

import androidx.annotation.NonNull;

/**
 * An enumeration of Firestore collections.
 * Each enum corresponds to a Firestore collection and provides its name as a string.
 */
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
