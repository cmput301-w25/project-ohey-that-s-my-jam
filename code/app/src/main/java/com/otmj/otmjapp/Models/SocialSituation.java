package com.otmj.otmjapp.Models;

import androidx.annotation.NonNull;

import java.io.Serializable;

public enum SocialSituation implements Serializable {
    Alone,
    With_One_Other_Person,
    With_Two_Or_Several_People,
    With_A_Crowd;

    @NonNull
    @Override
    public String toString() {
        return name().replaceAll("_", " ");
    }

    public static SocialSituation fromText(String text) {
        for (SocialSituation s : values()) {
            if (s.toString().equalsIgnoreCase(text)) {
                return s;
            }
        }

        return Alone;
    }
}
