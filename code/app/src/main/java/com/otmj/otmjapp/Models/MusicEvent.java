package com.otmj.otmjapp.Models;

import com.google.gson.annotations.SerializedName;
import com.otmj.otmjapp.API.Models.Track;

/**
 * Represents a music event - the association of a track with a feeling.
 */
public class MusicEvent extends DatabaseObject {
    private Track track;
    @SerializedName("associatedMood")
    private String associatedMood;
    private String feeling;
    private User user;
    private String albumArtURL;
    private Privacy privacy;

    // empty constructor for firebase
    MusicEvent() {}
    //TODO: make sure on edit mood event, album art is shown
    /**
     * Constructor for MusicEvent
     * <p>
     * @param track          The track associated with the music event.
     * @param feeling        The feeling evoked by the track.
     */
    public MusicEvent(Track track,
                      String feeling) {
        this.track = track;
        this.feeling = feeling;
    }

    public Track getTrack() {
        return track;
    }

    public void setTrack(Track track) {
        this.track = track;
    }

    public String getAssociatedMood() {
        return associatedMood;
    }

    public void setAssociatedMood(Object value) {
        if (value instanceof Long) {
            this.associatedMood = String.valueOf(value);
        } else if (value instanceof String) {
            this.associatedMood = (String) value;
        }
    }

    public String getFeeling() {
        return feeling;
    }

    public void setFeeling(String feeling) {
        this.feeling = feeling;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getAlbumArtURL() {
        return albumArtURL;
    }

    public void setAlbumArtURL(String albumArtURL) {
        this.albumArtURL = albumArtURL;
    }

    public Privacy getPrivacy() { return privacy; }

    public void setPrivacy(Privacy privacy) { this.privacy = privacy; }
}
