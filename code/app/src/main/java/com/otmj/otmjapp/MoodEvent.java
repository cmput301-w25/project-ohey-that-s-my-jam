package com.otmj.otmjapp;

import android.graphics.Bitmap;
import android.location.Location;

import java.util.Date;

public class MoodEvent {

    private Date date;
    private EmotionalState emotionalState;
    private String trigger;
    private String socialSituation;
    private Location location;
    private String reason;
    private Bitmap photo;


    public MoodEvent(Date date, EmotionalState emotionalState, String trigger, String socialSituation, Location location, String reason , Bitmap photo){
        // trigger, socialSituation, location, reason, photoId could be optional, if user didn't given please input null for argument
        this.date = date;
        this.emotionalState = emotionalState;
        this.trigger = trigger;
        this.socialSituation = socialSituation;
        this.location = location;
        this.reason = reason;
        this.photo = photo;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public EmotionalState getEmotionalState() {
        return emotionalState;
    }

    public void setEmotionalState(EmotionalState emotionalState){
        this.emotionalState = emotionalState;
    }

    public String getTrigger() {
        return trigger;
    }

    public void setTrigger(String trigger) {
        this.trigger = trigger;
    }

    public String getSocialSituation() {
        return socialSituation;
    }

    public void setSocialSituation(String socialSituation) {
        this.socialSituation = socialSituation;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public Bitmap getPhoto() {
        return photo;
    }

    public void setPhoto(Bitmap photo) {
        this.photo = photo;
    }

    @Override
    public String toString() {
        return "MoodEvent{" +
                "date=" + date +
                ", trigger='" + trigger + '\'' +
                ", socialSituation='" + socialSituation + '\'' +
                ", reason='" + reason + '\'' +
                '}';
    }
}
