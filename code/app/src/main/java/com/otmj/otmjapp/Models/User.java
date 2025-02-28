package com.otmj.otmjapp.Models;

import android.graphics.Bitmap;
import android.location.Location;

import java.util.Map;

/**
 * User class extneds Entity class
 * this class stories the user name and profile picture of a user
 */
public class User extends Entity {
    private String username;
    private String profilePictureLink;
    private String password;

    /**
     * User class constructor
     * @param username
     *              user name of a user
     * @param profilePictureLink
     *              optional profile picture of a user, if don't have one please input null
     */
    public User(String username, String password, String profilePictureLink) {
        this.username = username;
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
     * return user password
     * @return password
     */
    public String getPassword() {
        return password;
    }

    /**
     * set new password
     * @param password
     *              set this.password as password
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * return whether the input password is the same as this.password
     * @param input
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
    public void setProfilePicture(String profilePictureLink) {
        this.profilePictureLink = profilePictureLink;
    }

    /**
     * User's class implementation. This static method creates a User
     * from a map.
     * @see Entity#fromMap(Map)
     */
    public static User fromMap(Map<String, Object> map) {
        return new User(
                (String) map.get("username"),
                (String) map.get("password"),
                (String) map.get("profilePictureLink")
        );
    }

    /**
     * return the string of username
     * @return User
     *          the string representation of a User
     */
    @Override
    public String toString() {
        return "User{" +
                "username='" + username + '\'' +
                ", profilePictureLink='" + profilePictureLink + '\'' +
                ", password='" + password + '\'' +
                '}';
    }
}
