package com.otmj.otmjapp.API.Models;

import com.google.gson.annotations.SerializedName;

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
}
