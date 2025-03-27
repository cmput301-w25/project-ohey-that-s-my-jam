package com.otmj.otmjapp.Models;

import androidx.annotation.NonNull;

import java.io.Serializable;

/**
 * Stores all the social situations accepted in the app.
 */
public enum SocialSituation implements Serializable {
    Alone,
    With_1_Other,
    With_2_Others,
    With_A_Crowd;

    /**
     * Gets enum value name without underscores.
     *
     * @return Name without underscores.
     */
    @NonNull
    @Override
    public String toString() {
        return name().replaceAll("_", " ");
    }

    /**
     * Creates SocialSituation from text.
     *
     * @param text Raw text that might look like one of the social situations.
     * @return SocialSituation that closely resembles text (or Alone, by default).
     */
    public static SocialSituation fromText(String text) {
        for (SocialSituation s : values()) {
            String nameWithoutUnderscores = s.toString();
            if (nameWithoutUnderscores.equalsIgnoreCase(text)) {
                return s;
            }
        }

        return Alone;
    }
}
