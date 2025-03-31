package com.otmj.otmjapp.API.Models;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class Track {
    @SerializedName("album")
    private final Album album;
    @SerializedName("artists")
    private final ArrayList<Artist> artists;
    @SerializedName("duration_ms")
    private final int duration;
    @SerializedName("name")
    private final String title;
    @SerializedName("id")
    private final String id;

    // default constructor required for field initialization
    Track() {
        this.album = null;
        this.artists = null;
        this.title = "";
        this.id = "";
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

    public String getID() {
        return id;
    }

    public int getDuration() {
        return duration;
    }
}
