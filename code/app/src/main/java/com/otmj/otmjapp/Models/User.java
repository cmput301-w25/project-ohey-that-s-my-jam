package com.otmj.otmjapp.Models;

import android.graphics.Bitmap;

/**
 * User class extneds Entity class
 * this class stories the user name and profile picture of a user
 */
public class User extends Entity{
    private String username;
    private Bitmap profilePicture;

    /**
     * User class constructor
     * @param username
     *              user name of a user
     * @param profilePicture
     *              optional profile picture of a user, if don't have one please input null
     */
    public User(String username, Bitmap profilePicture) {
        this.username = username;
        this.profilePicture = profilePicture;
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
     * return profile picture
     * @return  profilePicture
     *      Bitmap of the profilePicture
     */
    public Bitmap getProfilePicture() {
        return profilePicture;
    }

    /**
     * set profile picture as the input profilePicture
     * @param profilePicture
     *                  Bitmap of the profilePicture to set
     */
    public void setProfilePicture(Bitmap profilePicture) {
        this.profilePicture = profilePicture;
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
                '}';
    }
}
