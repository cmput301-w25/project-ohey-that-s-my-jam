package com.otmj.otmjapp.Models;

import com.google.firebase.firestore.Exclude;
import com.otmj.otmjapp.API.Models.Track;

/**
 * Represents a music event - the association of a track with a feeling.
 * A music event is associated with some mood event
 */
public class MusicEvent extends DatabaseObject {
    private String moodEventID;
    private Track track;
    private String feeling;
    private String albumArtURL;
    @Exclude
    private MoodEvent moodEvent;

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

    public MoodEvent getMoodEvent() {
        return moodEvent;
    }

    public void setMoodEvent(MoodEvent moodEvent) {
        this.moodEvent = moodEvent;
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

    public String getAlbumArtURL() {
        return albumArtURL;
    }

    public void setAlbumArtURL(String albumArtURL) {
        this.albumArtURL = albumArtURL;
    }
}
