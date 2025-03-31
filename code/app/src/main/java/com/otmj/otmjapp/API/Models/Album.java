package com.otmj.otmjapp.API.Models;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Album {
    @SerializedName("images")
    private final List<AlbumArt> images;
    @SerializedName("name")
    private final String title;

    // default constructor required for field initialization
    Album() {
        this.images = null;
        this.title = "";
    }

    public ArrayList<AlbumArt> getImages() {
        return (ArrayList<AlbumArt>) images;
    }

    public String getTitle() {
        return title;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Album)) return false;
        Album album = (Album) o;
        return Objects.equals(images, album.images) && Objects.equals(title, album.title);
    }

    @Override
    public int hashCode() {
        return Objects.hash(images, title);
    }
}
