package com.otmj.otmjapp.API.Models;

import com.google.gson.annotations.SerializedName;

public class UserAuth {
    @SerializedName("code")
    private final String code;
    @SerializedName("state")
    private final String state;

    UserAuth() {
        this.code = "";
        this.state = "";
    }

    public String getCode() {
        return code;
    }

    public String getState() {
        return state;
    }
}
