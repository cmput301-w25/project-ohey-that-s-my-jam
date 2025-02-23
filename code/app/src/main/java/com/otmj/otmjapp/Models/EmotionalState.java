package com.otmj.otmjapp.Models;

import com.otmj.otmjapp.R;
import java.io.Serializable;
import java.security.InvalidParameterException;

/**
 * Stores all the possible emotional states accepted in the app
 */
public enum EmotionalState implements Serializable {

    Anger("😡", R.color.anger),
    Fear("😱", R.color.fear),
    Surprise("😲", R.color.surprise),
    Shame("😔", R.color.shame),
    Happy("😆", R.color.happy),
    Disgust("🤢", R.color.disgust),
    Sad("😢", R.color.sad),
    Confusion("🤨", R.color.confuse);

    public final String emoji;
    public final int color;

    /**
     * Create an emotional state with given emoji text and colour
     * @param emoji Emoji text of emotional state
     * @param color Assigned colour for emotional state to ensure uniformity across the app
     */
    EmotionalState(String emoji, int color) {
        this.emoji = emoji;
        this.color = color;
    }

    /**
     * Create emotional state from color. This is desired since all
     * the colors are stored in the 'colors.xml` file
     * @param color Color of emotional state (already specified in `colors.xml`)
     * @return EmotionalState with the given color
     */
    public static EmotionalState fromColor(int color) {
        for (EmotionalState e : values()) {
            if (color == e.color) {
                return e;
            }
        }

        throw new InvalidParameterException("Only use colors defined in 'colors.xml'");
    }

    /**
     * Alias function for in-built name() function
     * @return String equivalent of enum
     */
    public String getDescription() {
        return name();
    }
}
