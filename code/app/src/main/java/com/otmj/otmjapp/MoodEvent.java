package com.otmj.otmjapp;

import java.util.Date;

public class MoodEvent {

    public enum MoodType{
        ANGER,
        CONFUSION,
        DISGUST,
        FEAR,
        HAPPINESS,
        SADNESS,
        SHAME,
        SURPRISE
    }

    private Date date;
    private MoodType emotionalState;
    private String trigger;
    private String socialSituation;
    private String location;
    private String reason;
    private int photoId;


    public MoodEvent(Date date, String emotionalState, String trigger, String socialSituation, String location, String reason , int photoId){
        // trigger, socialSituation, location, reason, photoId could be optional, if user didn't given please input null for argument
        this.date = date;
        this.emotionalState = MoodType.valueOf(emotionalState);
        this.trigger = trigger;
        this.socialSituation = socialSituation;
        this.location = location;
        this.reason = reason;
        this.photoId = photoId;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public MoodType getEmotionalState() {
        return emotionalState;
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

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public int getPhotoId() {
        return photoId;
    }

    public void setPhotoId(int photoId) {
        this.photoId = photoId;
    }

    @Override
    public String toString() {
        return "MoodEvent{" +
                "date=" + date +
                ", emotionalState=" + emotionalState +
                ", trigger='" + trigger + '\'' +
                ", socialSituation='" + socialSituation + '\'' +
                ", location='" + location + '\'' +
                ", reason='" + reason + '\'' +
                ", photoId=" + photoId +
                '}';
    }
}
