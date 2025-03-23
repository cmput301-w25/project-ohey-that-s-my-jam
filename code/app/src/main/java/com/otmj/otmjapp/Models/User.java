package com.otmj.otmjapp.Models;

import androidx.annotation.NonNull;

import java.util.Collections;
import java.util.Map;
import java.util.Objects;

/**
 * User class extends Entity class
 * This class stores the user's details
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
     * Two `User`s are equal if they have the same property values.
     * It is necessary to implement this because the default implementation
     * does an instance comparison
     * @param o Object to compare with
     * @return True if the object has the same values as this object
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
