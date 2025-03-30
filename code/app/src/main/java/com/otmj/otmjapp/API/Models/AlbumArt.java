package com.otmj.otmjapp.API.Models;

import com.google.gson.annotations.SerializedName;

public class AlbumArt {
    @SerializedName("url")
    private final String url;
    // default constructor required for field initialization
    AlbumArt() {
        this.url = "";
    }

    public String getURL() {
        return url;
    }
}
