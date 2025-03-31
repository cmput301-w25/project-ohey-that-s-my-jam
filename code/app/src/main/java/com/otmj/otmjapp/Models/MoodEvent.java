package com.otmj.otmjapp.Models;

import androidx.annotation.NonNull;

import com.google.firebase.firestore.Exclude;
import com.google.firebase.firestore.PropertyName;
import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;
import java.util.Objects;

/**
 * Represents a mood event in the app; includes the user's emotional state, social situation, trigger, etc.
 */
public class MoodEvent extends DatabaseObject {
    private final String userID;
    /**
     * Will be set when mood event is retrieved from database
     * Should not be saved directly in database
     */
    private User user = null;
    /**
     * ServerTimestamp annotation automatically grabs the date and time
     * the model was added to the database
     */
    @ServerTimestamp
    private Date createdDate;
    @Exclude
    private EmotionalState emotionalState;
    /**
     * Dummy variable to help with deserialization of `EmotionalState` from database
     */
    @PropertyName("emotionalState")
    private String emotionalStateText;
    private SocialSituation socialSituation;
    private SimpleLocation simpleLocation;
    private String reason;
    private String imageLink;

    public enum Privacy {
        Public,
        Private
    }
    private Privacy privacy;

    MoodEvent() {
        userID = "";
    }

    public MoodEvent(String userID,
                     EmotionalState emotionalState,
                     SocialSituation socialSituation,
                     boolean includeLocation,
                     String reason,
                     String imageLink,
                     MoodEvent.Privacy privacy) {
        this.userID = userID;
        setEmotionalState(emotionalState);
        this.socialSituation = socialSituation;
        this.reason = reason;
        this.imageLink = imageLink;
        this.privacy = privacy;

//        if (includeLocation) {
//            // TODO: Get device's location
//        }
    }

    /**
     * Constructor used by database.
     * @param emotionalState String representation of EmotionalState enum
     * @param socialSituation String representation of SocialSituation enum
     */
    public MoodEvent(String userID,
                     String emotionalState,
                     String socialSituation,
                     boolean includeLocation,
                     String reason,
                     String imageLink,
                     MoodEvent.Privacy privacy) {
        this(
                userID,
                EmotionalState.fromString(emotionalState),
                SocialSituation.fromText(socialSituation),
                includeLocation,
                reason,
                imageLink,
                privacy
        );
    }

    public Date getCreatedDate() {
        if (createdDate == null) {
            createdDate = new Date();
        }
        return createdDate;
    }

    public void setCreatedDate(Date date) {
        this.createdDate = date;
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
    @Exclude // from database
    public final User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void setEmotionalState(EmotionalState emotionalState) {
        this.emotionalState = emotionalState;
        this.emotionalStateText = emotionalState.name();
    }

    public SocialSituation getSocialSituation() {
        return socialSituation;
    }

    public void setSocialSituation(SocialSituation socialSituation) {
        this.socialSituation = socialSituation;
    }

    public SimpleLocation getLocation() {
        return simpleLocation;
    }

    public void setLocation(SimpleLocation location) {
        this.simpleLocation = location;
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

    public Privacy getPrivacy() {
        return privacy;
    }

    public void setPrivacy(Privacy privacy) {
        this.privacy = privacy;
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

    @NonNull
    @Override
    public String toString() {
        return "MoodEvent{" +
                "userID='" + userID + '\'' +
                ", user=" + user +
                ", createdDate=" + createdDate +
                ", emotionalState=" + emotionalState +
                ", socialSituation=" + socialSituation +
                ", location=" + simpleLocation +
                ", reason='" + reason + '\'' +
                ", imageLink='" + imageLink + '\'' +
                ", privacy=" + privacy +
                '}';
    }
}
