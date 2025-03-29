package com.otmj.otmjapp.API.Models;

import com.google.gson.annotations.SerializedName;

public class AccessToken extends Token {

    @SerializedName("refresh_token")
    private final String refreshToken;

    // default constructor required for field initialization
    AccessToken() { // TODO: research how use of super() changes with inheritance on such objects
        super();
        this.refreshToken = "";
    }

    public String getRefreshToken() {
        return refreshToken; //TODO: when retrieved, save to shared preferences
    }
}
