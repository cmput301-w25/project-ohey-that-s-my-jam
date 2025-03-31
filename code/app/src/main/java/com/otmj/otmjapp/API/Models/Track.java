package com.otmj.otmjapp.API.Models;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.Objects;

public class Track {
    @SerializedName("album")
    private final Album album;
    @SerializedName("artists")
    private final ArrayList<Artist> artists;
    @SerializedName("duration_ms")
    private final int duration;
    @SerializedName("name")
    private final String title;
    @SerializedName("preview_url")
    private final String previewURL;

    // default constructor required for field initialization
    Track() {
        this.album = null;
        this.artists = null;
        this.title = "";
        this.previewURL = "";
        this.duration = 0;
    }

    public String getTitle() {
        return title;
    }

    public ArrayList<Artist> getArtists() {
        return artists;
    }

    public Album getAlbum() {
        return album;
    }

    public String getPreviewURL() {
        return previewURL;
    }

    public int getDuration() {
        return duration;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Track)) return false;
        Track track = (Track) o;
        return duration == track.duration
                && Objects.equals(album, track.album)
                && Objects.equals(artists, track.artists)
                && Objects.equals(title, track.title)
                && Objects.equals(previewURL, track.previewURL);
    }

    @Override
    public int hashCode() {
        return Objects.hash(album, artists, duration, title, previewURL);
    }
}
