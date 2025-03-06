package com.otmj.otmjapp.Models;

import android.location.Location;

import com.google.firebase.firestore.Exclude;
import com.google.firebase.firestore.ServerTimestamp;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.Map;
import java.util.Objects;

// TODO: Update moodeventcontroller to set user
// TODO: Create initial activity

public class MoodEvent extends Entity {
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
    private LocalDateTime createdDate;
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
                      LocalDateTime createdDate,
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

    public LocalDateTime getCreatedDate() {
        return createdDate;
    }

    public EmotionalState getEmotionalState() {
        return emotionalState;
    }

    public User getUser() {
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
     * Mood Event's class implementation. This static method creates a MoodEvent
     * from a map.
     * @see Entity#fromMap(Map)
     */
    public static MoodEvent fromMap(Map<String, Object> map) {
        return new MoodEvent(
                (String) map.get("userID"),
                (LocalDateTime) map.get("createdDate"),
                EmotionalState.fromColor((int) Objects.requireNonNull(map.get("emotionalState"))),
                (String) map.get("trigger"),
                SocialSituation.fromText((String) map.get("socialSituation")),
                (Location) map.get("location"),
                (String) map.get("reason"),
                (String) map.get("imageLink")
        );
    }

    /**
     * Custom implementation for storing MoodEvent in database.
     * This static method creates a map from a MoodEvent.
     * @see Entity#toMap()
     */
    public Map<String, Object> toMap() {
       return Map.of(
               "userID", userID,
               "createdDate", createdDate,
               "emotionalState", emotionalState.color, // Store enum with only its color
               "trigger", trigger,
               "socialSituation", socialSituation.toString(), // Store enum with its string
               "location", location,
               "reason", reason,
               "imageLink", imageLink
       );
    }
}
