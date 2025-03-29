package com.otmj.otmjapp.API.Models;

import android.util.Log;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class TracksContainer {
    @SerializedName("items")
    private List<Track> items;
        //private List<Track> tracks;

    TracksContainer() {
        this.items = null;
    }

    public List<Track> getTracks() {
        for(Track track: items) {
            Log.d("TracksContainer", "Entry key: " + track.getTitle());
        }

        return null;
    }
}
