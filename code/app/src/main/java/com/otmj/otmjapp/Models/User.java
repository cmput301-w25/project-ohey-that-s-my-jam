package com.otmj.otmjapp.Models;

import android.graphics.Bitmap;

/**
 * User class extneds Entity class
 * this class stories the user name and profile picture of a user
 */
public class User extends Entity{
    private String username;
    private Bitmap profilePicture;
    private int userId;

    /**
     * User class constructor
     * @param username
     *              user name of a user
     * @param profilePicture
     *              optional profile picture of a user, if don't have one please input null
     */
    public User(String username, Bitmap profilePicture, int userId) {
        this.username = username;
        this.profilePicture = profilePicture;
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
     * return userID
     * @return userId
     *            userId
     */
    public int getUserId() {
        return userId;
    }

    /**
     * set UserId
     * @param userId
     *          userId
     */
    public void setUserId(int userId) {
        this.userId = userId;
    }

    /**
     * Create a unique UserId for a new User, will check with db to avoid duplicate
     * @return unique_UserId
     *              unique_UserId
     */
    public static int createUserId(){
        // this method is not done yet, will return 0 as a place holder
        return 0;
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
