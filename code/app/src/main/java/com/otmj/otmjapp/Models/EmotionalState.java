package com.otmj.otmjapp.Models;

import androidx.annotation.NonNull;

import com.otmj.otmjapp.R;
import java.io.Serializable;
import java.security.InvalidParameterException;

/**
 * Stores all the possible emotional states accepted in the app
 */
public enum EmotionalState implements Serializable {

    Anger(R.drawable.anger, R.color.anger),
    Fear(R.drawable.fear, R.color.fear),
    Surprise(R.drawable.surprise, R.color.surprise),
    Shame(R.drawable.shame, R.color.shame),
    Happy(R.drawable.happy, R.color.happy),
    Disgust(R.drawable.disgust, R.color.disgust),
    Sad(R.drawable.sad, R.color.sad),
    Confusion(R.drawable.confuse, R.color.confuse);

    public final int emoji;
    public final int color;

    /**
     * Create an emotional state with given emoji text and colour
     * @param emoji Emoji text of emotional state
     * @param color Assigned colour for emotional state to ensure uniformity across the app
     */
    EmotionalState(int emoji, int color) {
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
     * Create emotional state from mood
     * @param mood
     * @return
     */
    public static EmotionalState fromMood(String mood){
        for (EmotionalState e : values()) {
            if (mood.equals(e.name())) {
                return e;
            }
        }

        throw new InvalidParameterException("Only use mood defined in user stories");
    }

    /**
     * Alias function for in-built name() function
     * @return String equivalent of enum
     */
    public String getDescription() {
        return name();
    }

    /**
     * return emoji
     * @return
     */
    public int getEmoji(){
        return this.emoji;
    }

    @NonNull
    @Override
    public String toString() {
        return name().toLowerCase() + " " + emoji;
    }
}
