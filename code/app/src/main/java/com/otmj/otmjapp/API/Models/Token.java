package com.otmj.otmjapp.API.Models;

import com.google.gson.annotations.SerializedName;

public class Token {
    @SerializedName("access_token")
    private final String accessToken;
    @SerializedName("expires_in")
    private final long expiresIn;

    // default constructor required for field initialization
    Token() {
        this.accessToken = "";
        this.expiresIn = 0;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public long getExpiresIn() {
        return expiresIn;
    }
}
