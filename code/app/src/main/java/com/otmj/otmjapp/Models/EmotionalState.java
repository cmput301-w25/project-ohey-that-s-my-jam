package com.otmj.otmjapp.Models;

import android.graphics.Bitmap;

import androidx.annotation.NonNull;

import com.otmj.otmjapp.R;

public enum EmotionalState {

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

    private EmotionalState(String emoji, int color) {
        this.emoji = emoji;
        this.color = color;
    }

    public String getDescription() {
        return name();
    }
}
