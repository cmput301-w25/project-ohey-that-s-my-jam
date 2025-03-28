package com.otmj.otmjapp.API.Models;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class TracksResponse {
    @SerializedName("items")
    private ArrayList<Track> tracks;

    // default constructor required for field initialization
    TracksResponse() {
        this.tracks = null;
    }

    public ArrayList<Track> getTracks() {
        return tracks;
    }
}
