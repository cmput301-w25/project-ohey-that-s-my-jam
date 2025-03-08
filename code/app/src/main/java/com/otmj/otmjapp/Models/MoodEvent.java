package com.otmj.otmjapp.Models;

import android.location.Location;

import com.google.firebase.firestore.Exclude;
import com.google.firebase.firestore.ServerTimestamp;
import com.google.type.DateTime;

import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.Date;
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
    private Date createdDate;
    private EmotionalState emotionalState;
    private String trigger;
    private SocialSituation socialSituation;
    private Location location;
    private String reason;
    private String imageLink;

    public MoodEvent(String userID,
                     EmotionalState emotionalState,
                     String trigger,
                     SocialSituation socialSituation,
                     boolean includeLocation,
                     String reason,
                     String imageLink) {
        this.userID = userID;
        this.emotionalState = emotionalState;
        this.trigger = trigger;
        this.socialSituation = socialSituation;
        this.reason = reason;
        this.imageLink = imageLink;

        if (includeLocation) {
            // TODO: Get device's location
        }
    }

    public MoodEvent(String userID,
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
        this.createdDate = Calendar.getInstance().getTime();
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

    public void setEmotionalState(EmotionalState emotionalState) {
        this.emotionalState = emotionalState;
    }

    public String getTrigger() {
        return trigger;
    }

    public void setTrigger(String trigger) {
        this.trigger = trigger;
    }

    public SocialSituation getSocialSituation() {
        return socialSituation;
    }

    public void setSocialSituation(SocialSituation socialSituation) {
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
                (Date) map.get("createdDate"),
                EmotionalState.fromColor((int) Objects.requireNonNull(map.get("emotionalState"))),
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MoodEvent moodEvent = (MoodEvent) o;
        return Objects.equals(userID, moodEvent.userID)
                && Objects.equals(createdDate, moodEvent.createdDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userID, createdDate);
    }
}
