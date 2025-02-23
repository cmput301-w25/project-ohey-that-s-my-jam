package com.otmj.otmjapp.Models;

import com.otmj.otmjapp.R;
import java.io.Serializable;
import java.security.InvalidParameterException;

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

    EmotionalState(String emoji, int color) {
        this.emoji = emoji;
        this.color = color;
    }

    public static EmotionalState fromColor(int color) {
        for (EmotionalState e : values()) {
            if (color == e.color) {
                return e;
            }
        }

        throw new InvalidParameterException("Only use colors defined in 'colors.xml'");
    }

    public String getDescription() {
        return name();
    }
}
