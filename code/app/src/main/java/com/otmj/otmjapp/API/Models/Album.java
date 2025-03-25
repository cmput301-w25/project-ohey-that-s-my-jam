package com.otmj.otmjapp.API.Models;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class Album {
    @SerializedName("images")
    private final ArrayList<AlbumArt> images;
    @SerializedName("name")
    private final String title;

    // default constructor required for field initialization
    Album() {
        this.images = null;
        this.title = "";
    }

    public ArrayList<AlbumArt> getImages() {
        return images;
    }

    public String getTitle() {
        return title;
    }
}
