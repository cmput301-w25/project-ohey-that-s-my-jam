package com.otmj.otmjapp.Models;

import com.otmj.otmjapp.API.Models.Track;

import java.time.LocalDateTime;

/**
 * Represents a music event - the association of a track with a feeling.
 */
public class MusicEvent extends DatabaseObject {
    private Track track;
    private LocalDateTime createdDate;
    private int associatedMood;
    private String feeling;
    private User user;
    private String albumArtURL;
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
        this.createdDate = LocalDateTime.now();
        this.feeling = feeling;
    }

    public Track getTrack() {
        return track;
    }

    public void setTrack(Track track) {
        this.track = track;
    }

    public LocalDateTime getCreatedDate() {
        return createdDate;
    }
    public String getFeeling() {
        return feeling;
    }

    public void setFeeling(String feeling) {
        this.feeling = feeling;
    }

    public int getAssociatedMood() {
        return associatedMood;
    }

    public void setAssociatedMood(int associatedMood) {
        this.associatedMood = associatedMood;
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
}
