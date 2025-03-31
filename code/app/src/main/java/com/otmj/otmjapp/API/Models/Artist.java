package com.otmj.otmjapp.API.Models;

import com.google.gson.annotations.SerializedName;

import java.util.Objects;

public class Artist {
    @SerializedName("name")
    private final String name;

    // default constructor required for field initialization
    Artist() {
        this.name = "";
    }

    public String getName() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Artist)) return false;
        Artist artist = (Artist) o;
        return Objects.equals(name, artist.name);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(name);
    }
}
