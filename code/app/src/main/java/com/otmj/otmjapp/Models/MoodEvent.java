package com.otmj.otmjapp.Models;

import android.graphics.Bitmap;
import android.location.Location;

import java.util.Date;
import java.util.Map;

public class MoodEvent extends Entity {
    private final String userID;
    private final Date createdDate;
    private EmotionalState emotionalState;
    private String trigger;
    private SocialSituation socialSituation;
    private Location location;
    private String reason;
    private String imageLink;

    public MoodEvent(String userID,
                     Date createdDate,
                     int emotionColor,
                     String trigger,
                     String socialSituation,
                     boolean includeLocation,
                     String reason,
                     String imageLink) {
        this.userID = userID;
        this.createdDate = createdDate;
        this.emotionalState = EmotionalState.fromColor(emotionColor);
        this.trigger = trigger;
        this.socialSituation = SocialSituation.fromText(socialSituation);
        this.reason = reason;
        this.imageLink = imageLink;

        if (includeLocation) {
            // TODO: Get device's location
        }
    }

    private MoodEvent(String userID,
                      Date createdDate,
                      EmotionalState emotionalState,
                      String trigger,
                      SocialSituation socialSituation,
                      Location location,
                      String reason,
                      String imageLink) {
        // TODO: Ensure trigger is at most 1 word
        // TODO: Ensure reason is at most 3 words

        this.userID = userID;
        this.createdDate = createdDate;
        this.emotionalState = emotionalState;
        this.trigger = trigger;
        this.socialSituation = socialSituation;
        this.location = location;
        this.reason = reason;
        this.imageLink = imageLink;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public EmotionalState getEmotionalState() {
        return emotionalState;
    }

    public void setEmotionalState(int color) {
        this.emotionalState = EmotionalState.fromColor(color);
    }

    public String getTrigger() {
        return trigger;
    }

    public void setTrigger(String trigger) {
        this.trigger = trigger;
    }

    public String getSocialSituation() {
        return socialSituation.toString();
    }

    public void setSocialSituation(String socialSituation) {
        this.socialSituation = SocialSituation.fromText(socialSituation);
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
        throw new UnsupportedOperationException("Not yet implemented");
    }

    public void setImageLink(String link) {
        this.imageLink = link;
    }

    /**
     * Mood Event's class implementation. This static method creates a MoodEvent
     * from a map.
     * @see Entity#fromMap(Map)
     */
    public static MoodEvent fromMap(Map<String, Object> map) {
        // TODO: Fix emotional state and social situation
        return new MoodEvent(
                (String) map.get("userID"),
                (Date) map.get("createdDate"),
                (EmotionalState) map.get("emotionalState"),
                (String) map.get("trigger"),
                (SocialSituation) map.get("socialSituation"),
                (Location) map.getOrDefault("location", null),
                (String) map.get("reason"),
                (String) map.getOrDefault("imageLink", "")
        );
    }
}
