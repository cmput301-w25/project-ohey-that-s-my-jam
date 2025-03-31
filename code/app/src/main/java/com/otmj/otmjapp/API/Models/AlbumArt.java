package com.otmj.otmjapp.API.Models;

import com.google.gson.annotations.SerializedName;

import java.util.Objects;

public class AlbumArt {
    @SerializedName("url")
    private final String url;

    /*USE DIMENSIONS 300 x 300 (find what units are) for image when displaying in listview*/

    // default constructor required for field initialization
    AlbumArt() {
        this.url = "";
    }

    public String getURL() {
        return url;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof AlbumArt)) return false;
        AlbumArt albumArt = (AlbumArt) o;
        return Objects.equals(url, albumArt.url);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(url);
    }
}
