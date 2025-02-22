package com.otmj.otmjapp;

import android.graphics.Bitmap;

public class EmotionalState {

    private Bitmap emoji;
    private String colorHex;
    private MoodType description;

    public EmotionalState(Bitmap emoji, String colorHex, String description) {
        this.emoji = emoji;
        this.colorHex = colorHex;
        this.description = MoodType.valueOf(description);
    }

    public enum MoodType{
        ANDER, CONFUSION, DISGUST, FEAR, HAPPINESS, SADNESS, SHAME, SURPRISE
    }

    public Bitmap getEmoji() {
        return emoji;
    }

    public void setEmoji(Bitmap emoji) {
        this.emoji = emoji;
    }

    public String getColorHex() {
        return colorHex;
    }

    public void setColorHex(String colorHex) {
        this.colorHex = colorHex;
    }

    public MoodType getDescription() {
        return description;
    }

    @Override
    public String toString() {
        return "EmotionalState{" +
                "colorHex='" + colorHex + '\'' +
                ", description=" + description +
                '}';
    }
}
