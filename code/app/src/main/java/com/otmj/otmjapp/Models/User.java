package com.otmj.otmjapp.Models;

import androidx.annotation.NonNull;

import java.util.Collections;
import java.util.Map;

/**
 * User class extends Entity class
 * This class stores the user's details
 */
public class User extends DatabaseObject {
    private String username;
    private String emailAddress;
    private String password;
    private String profilePictureLink;

    /**
     * User class constructor
     * @param username
     *              user name of a user
     * @param emailAddress
     *              email address of a user
     * @param password
     *              password of a user
     * @param profilePictureLink
     *              optional profile picture of a user, if don't have one please input null
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
     * return username
     * @return
     *      username
     */
    public String getUsername() {
        return username;
    }

    /**
     * set username
     * @param username
     *              set user name to the input username
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * return email address
     * @return
     *      email address
     */
    public String getEmailAddress() {
        return emailAddress;
    }

    /**
     * set email address
     * @param emailAddress
     *              set email address to the input email address
     */
    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    /**
     * return user password
     * @return password
     */
    public String getPassword() {
        return password;
    }

    /**
     * set new password
     * @param oldPassword
     *                  current password of user
     * @param newPassword
     *                  new password of user
     */
    public void setPassword(String oldPassword, String newPassword) {
        if (isPassword(oldPassword)) {
            password = newPassword;
        }
    }

    /**
     * return whether the input password is the same as this.password
     * @param input text to check
     * @return isPassword correct
     */
    public boolean isPassword(String input){
        return (input.equals(this.password));
    }

    /**
     * return profile picture Link
     * @return  profilePictureLink
     *      String of the profilePictureLink
     */
    public String getProfilePictureLink() {
        return profilePictureLink;
    }

    /**
     * set profile picture as the input profilePictureLink
     * @param profilePictureLink
     *                  String of the profilePictureLink to set
     */
    public void setProfilePictureLink(String profilePictureLink) {
        this.profilePictureLink = profilePictureLink;
    }

    /**
     * User's class implementation. This static method creates a User
     * from a map.
     */
    public static User fromMap(Map<String, Object> map) {
        return new User(
                (String) map.get("username"),
                (String) map.get("emailAddress"),
                (String) map.get("password"),
                (String) map.get("profilePictureLink")
        );
    }

    @Override
    public Map<String, Object> toMap() {
        return Map.of(
            "username", username,
            "emailAddress", emailAddress,
            "password", password,
            "profilePictureLink", profilePictureLink
        );
    }

    /**
     * return the string of username
     * @return User
     *          the string representation of a User
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
