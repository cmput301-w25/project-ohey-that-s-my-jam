package com.otmj.otmjapp.Models;

import androidx.annotation.NonNull;

import java.util.Collections;
import java.util.Map;
import java.util.Objects;

/**
 * Class for a user object that stores the user's details.
 */
public class User extends DatabaseObject {
    private String username;
    private String emailAddress;
    private String password;
    private String profilePictureLink;

    // Need an empty constructor for Firebase
    User() {}

    /**
     * User class constructor
     * @param username The user name of a user as a string.
     * @param emailAddress The email address of a user as a string.
     * @param password The password of a user as a string.
     * @param profilePictureLink The optional profile picture of a user. If nothing was set, null must be provided.
     */
    public User(String username,
                String emailAddress,
                String password,
                String profilePictureLink) {
        this.username = username;
        this.emailAddress = emailAddress;
        this.password = password;
        this.profilePictureLink = profilePictureLink;
    }

    /**
     * Returns username as a string.
     * @return username
     */
    public String getUsername() {
        return username;
    }

    /**
     * Sets username.
     * @param username Sets user name to the input username.
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * Returns email address.
     * @return Email address.
     */
    public String getEmailAddress() {
        return emailAddress;
    }

    /**
     * Sets email address.
     * @param emailAddress Sets email address to the input email address
     */
    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    /**
     * Returns user password.
     * @return user password.
     */
    public String getPassword() {
        return password;
    }

    /**
     * Sets new password
     * @param oldPassword The current password of user
     * @param newPassword The new password of user
     */
    public void setPassword(String oldPassword, String newPassword) {
        if (isPassword(oldPassword)) {
            password = newPassword;
        }
    }

    /**
     * Return whether the input password is the same as this.password.
     * @param input The input text to check.
     * @return A boolean confirming whether the password correct or not.
     */
    public boolean isPassword(String input){
        return (input.equals(this.password));
    }

    /**
     * Returns profile picture Link.
     * @return A string of the profilePictureLink.
     */
    public String getProfilePictureLink() {
        return profilePictureLink;
    }

    /**
     * Set profile picture as the input profilePictureLink.
     * @param profilePictureLink A string of the profilePictureLink to set.
     */
    public void setProfilePictureLink(String profilePictureLink) {
        this.profilePictureLink = profilePictureLink;
    }

    /**
     * Two `User`s are equal if they have the same property values.
     * It is necessary to implement this because the default implementation does an instance comparison.
     * @param o An object to compare with.
     * @return A boolean; True if the object has the same values as this object.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof User)) return false;
        User user = (User) o;
        return Objects.equals(username, user.username)
                && Objects.equals(emailAddress, user.emailAddress)
                && Objects.equals(password, user.password)
                && Objects.equals(profilePictureLink, user.profilePictureLink);
    }

    @Override
    public int hashCode() {
        return Objects.hash(username, emailAddress, password);
    }

    /**
     * Returns the string of username.
     * @return The string representation of a User.
     */
    @NonNull
    @Override
    public String toString() {
        return "User{" +
                "username='" + username + '\'' +
                "emailAddress='" + emailAddress + '\'' +
                ", profilePictureLink='" + profilePictureLink + '\'' +
                ", password='" + password + '\'' +
                '}';
    }

}
