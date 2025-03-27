package com.otmj.otmjapp.Models;

import androidx.annotation.NonNull;

import com.otmj.otmjapp.R;
import java.io.Serializable;
import java.security.InvalidParameterException;

/**
 * Stores all the possible emotional states accepted in the app.
 *
 * Represents an emotional state with an associated emoji and color.
 * Each emotional state is linked to a drawable (emoji) and a color from 'colors.xml'.
 */
public enum EmotionalState implements Serializable {

    Anger(R.drawable.anger, R.color.anger),
    Fear(R.drawable.fear, R.color.fear),
    Surprise(R.drawable.surprise, R.color.surprise),
    Shame(R.drawable.shame, R.color.shame),
    Happy(R.drawable.happy, R.color.happy),
    Disgust(R.drawable.disgust, R.color.disgust),
    Sad(R.drawable.sad, R.color.sad),
    Confuse(R.drawable.confuse, R.color.confuse);

    public final int emoji;
    public final int color;

    /**
     * Creates an emotional state with given emoji text and colour.
     *
     * @param emoji Emoji text of emotional state.
     * @param color Assigned a colour for emotional state to ensure uniformity across the app.
     */
    EmotionalState(int emoji, int color) {
        this.emoji = emoji;
        this.color = color;
    }

    /**
     * Creates an emotional state from color. This is desired since all
     * the colors are stored in the 'colors.xml` file.
     *
     * @param color Color of emotional state (already specified in `colors.xml`).
     * @return EmotionalState with the given color.
     */
    public static EmotionalState fromColor(int color) {
        for (EmotionalState e : values()) {
            if (color == e.color) {
                return e;
            }
        }

        throw new InvalidParameterException("Only use colors defined in 'colors.xml'");
    }

    public static EmotionalState fromString(String text) {
        for (EmotionalState e : values()) {
            if (text.equalsIgnoreCase(e.name())) {
                return e;
            }
        }

        throw new InvalidParameterException("Unknown text string");
    }

    /**
     * Alias function for in-built name() function.
     *
     * @return String equivalent of enum
     */
    public String getDescription() {
        return name();
    }

    /**
     * Gets the emoji resource ID for this emotional state.
     *
     * @return The drawable resource ID of the emoji.
     */
    public int getEmoji(){
        return this.emoji;
    }

    /**
     * Returns the uppercase string representation of this emotional state.
     *
     * @return The name of the emotional state in uppercase.
     */
    @NonNull
    @Override
    public String toString() {
        return name().toUpperCase();
    }
}
