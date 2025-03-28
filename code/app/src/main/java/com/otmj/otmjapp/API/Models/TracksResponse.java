package com.otmj.otmjapp.API.Models;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class TracksResponse {
    @SerializedName("tracks")
    private TracksContainer tracksContainer;

    public static class TracksContainer {
        @SerializedName("items")
        private ArrayList<Track> tracks;
    }

    public ArrayList<Track> getTracks() {
        return tracksContainer != null ? tracksContainer.tracks : new ArrayList<>();
    }
}