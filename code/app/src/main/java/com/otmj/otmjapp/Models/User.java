package com.otmj.otmjapp.Models;

import android.graphics.Bitmap;
import android.location.Location;

import java.util.Map;

/**
 * User class extneds Entity class
 * this class stories the user name and profile picture of a user
 */
public class User extends Entity{
    private String username;
    private String profilePictureLink;
    private final String userId;
    private String password;

    /**
     * User class constructor
     * @param username
     *              user name of a user
     * @param profilePictureLink
     *              optional profile picture of a user, if don't have one please input null
     */
    public User(String username, String password, String profilePictureLink, String userId) {
        this.username = username;
        this.password = password;
        this.profilePictureLink = profilePictureLink;
        this.userId = userId;
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
     * return userID
     * @return userId
     *            userId
     */
    public String getUserId() {
        return userId;
    }

    /**
     * Create a unique UserId for a new User, will check with db to avoid duplicate
     * @return unique_UserId
     *              unique_UserId
     */
    public static String createUserId(){
        // this method is not done yet, will return -1 as a place holder
        return "-1";
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
                (String) map.get("profilePictureLink"),
                (String) map.get("userId")
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
                ", userId='" + userId + '\'' +
                ", password='" + password + '\'' +
                '}';
    }
}
