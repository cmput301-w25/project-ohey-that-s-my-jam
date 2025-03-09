package com.otmj.otmjapp.Models;

import android.location.Location;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.Exclude;
import com.google.firebase.firestore.ServerTimestamp;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

// TODO: Create initial activity

public class MoodEvent extends DatabaseObject {
    private final String userID;
    /**
     * Will be set when mood event is retrieved from database
     * Should not be saved directly in database
     */
    @Exclude // from database
    private User user = null;
    /**
     * ServerTimestamp annotation automatically grabs the date and time
     * the model was added to the database
     */
    @ServerTimestamp
    private Timestamp createdDate;
    private EmotionalState emotionalState;
    private String trigger;
    private SocialSituation socialSituation;
    private Location location;
    private String reason;
    private String imageLink;

    public MoodEvent(String userID,
                     int emotionColor,
                     String trigger,
                     String socialSituation,
                     boolean includeLocation,
                     String reason,
                     String imageLink) {
        this.userID = userID;
        this.emotionalState = EmotionalState.fromColor(emotionColor);
        this.trigger = trigger;
        this.socialSituation = SocialSituation.fromText(socialSituation);
        this.reason = reason;
        this.imageLink = imageLink;

        if (includeLocation) {
            // TODO: Get device's location
        }
    }

    public MoodEvent(String userID,
                      Timestamp createdDate,
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

    public Timestamp getCreatedDate() {
        return createdDate;
    }

    public EmotionalState getEmotionalState() {
        return emotionalState;
    }

    public String getUserID() {
        return userID;
    }

    /**
     * Get user associated with mood event
     * @return A constant `User` object
     */
    public final User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
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

    public String getImageLink() {
        return imageLink;
    }

    public void setImageLink(String link) {
        this.imageLink = link;
    }

    /**
     * This static method creates a MoodEvent from a map.
     */
    public static MoodEvent fromMap(Map<String, Object> map) {
        return new MoodEvent(
                (String) map.get("userID"),
                (Timestamp) map.get("createdDate"),
                EmotionalState.fromColor(((Number) Objects.requireNonNull(map.get("emotionalState"))).intValue()),
                (String) map.get("trigger"),
                SocialSituation.fromText((String) map.get("socialSituation")),
                (Location) map.get("location"),
                (String) map.get("reason"),
                (String) map.get("imageLink")
        );
    }

    /**
     * This static method creates a map from a MoodEvent.
     */
    @Override
    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("userID", userID);
        map.put("createdDate", createdDate);
        map.put("emotionalState", emotionalState != null ? emotionalState.color : null); // Store enum with only its color
        map.put("trigger", trigger);
        map.put("socialSituation", socialSituation != null ? socialSituation.toString() : null); // Store enum as string
        map.put("location", location);
        map.put("reason", reason);
        map.put("imageLink", imageLink);
        return map;
    }
}
