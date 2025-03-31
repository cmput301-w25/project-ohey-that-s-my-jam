package com.otmj.otmjapp.Models;

import com.google.firebase.firestore.Exclude;
import com.otmj.otmjapp.API.Models.Track;

import java.util.Objects;

/**
 * Represents a music event - the association of a track with a feeling.
 * A music event is associated with some mood event
 */
public class MusicEvent extends DatabaseObject {
    private String moodEventID;
    private String userID;
    // Actual music event information
    private Track track;
    private String feeling;
    // ----------------------------
    @Exclude
    private MoodEvent moodEvent;
    @Exclude
    private User user;

    // Empty constructor for firebase
    MusicEvent() {}

    /**
     * Constructor for MusicEvent
     * @param track          The track associated with the music event.
     * @param feeling        The feeling evoked by the track.
     */
    public MusicEvent(Track track, String feeling) {
        this.track = track;
        this.feeling = feeling;
    }

    public String getMoodEventID() {
        return moodEventID;
    }

    public void setMoodEventID(String moodEventID) {
        this.moodEventID = moodEventID;
    }

    @Exclude
    public MoodEvent getMoodEvent() {
        return moodEvent;
    }

    public void setMoodEvent(MoodEvent moodEvent) {
        this.moodEvent = moodEvent;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    @Exclude
    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Track getTrack() {
        return track;
    }

    public void setTrack(Track track) {
        this.track = track;
    }

    public String getFeeling() {
        return feeling;
    }

    public void setFeeling(String feeling) {
        this.feeling = feeling;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof MusicEvent)) return false;
        MusicEvent that = (MusicEvent) o;
        return Objects.equals(moodEventID, that.moodEventID)
                && Objects.equals(userID, that.userID)
                && Objects.equals(track, that.track)
                && Objects.equals(feeling, that.feeling);
    }

    @Override
    public int hashCode() {
        return Objects.hash(moodEventID, userID, track, feeling);
    }
}
