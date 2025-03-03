package com.otmj.otmjapp.Helper;

import android.provider.ContactsContract;

import com.google.firebase.firestore.Filter;
import com.otmj.otmjapp.Models.DatabaseObject;
import com.otmj.otmjapp.Models.User;
import java.util.ArrayList;

/**
 * Manages user authentification and retrieval from the Firestore database.
 */

public class UserManager {
    private final FirestoreDB<User> db;

    public interface AuthenticationCallback {
        void onAuthenticated(DatabaseObject<User> authenticatedUser);
        void onAuthenticationFailure(String reason);
        void onFailure(Exception e);
    }

    public UserManager() {
        this.db = new FirestoreDB<>(FirestoreCollections.Users.name);
    }

    /**
     * Attempts to authenticate a user with the provided username and password.
     *
     * @param enteredUsername the username entered by the user
     * @param enteredPassword the password entered by the user
     * @param callback        the callback provides an interface for handling
     *                        authentication success and failure
     */

    public void login(String enteredUsername, String enteredPassword, AuthenticationCallback callback) {
        Filter byUsername = Filter.equalTo("username", enteredUsername);
        db.getDocuments(byUsername, new FirestoreDB.DBCallback<>() {
            @Override
            public void onSuccess(ArrayList<DatabaseObject<User>> result) {
                if (!result.isEmpty()) {
                    DatabaseObject<User> dob = result.get(0);
                    User u = dob.getObject();

                    if (u.isPassword(enteredPassword)) {
                        callback.onAuthenticated(dob);
                    } else {
                        callback.onAuthenticationFailure("Wrong password");
                    }
                } else {
                    callback.onAuthenticationFailure("No such user");
                }
            }

            @Override
            public void onFailure(Exception e) {
                callback.onFailure(e);
            }
        });

    }

    /**
     * Adds a new user to the database.
     * @param user
     */
    public void addUser(User user) {
        db.addDocument(user, new FirestoreDB.DBCallback<User>() {
            @Override
            public void onSuccess(ArrayList<DatabaseObject<User>> result) {
                // will be implemented later on when it has been decided what should happen on success
            }

            @Override
            public void onFailure(Exception e) {
                // will be implemented later on when it has been decided what should happen on failure
            }
        });
    }

    /**
     * Checks if a given username is already in use.
     *
     * @param enteredUsername the username to check for availability
     * @return {@code true} if the username is available, {@code false} otherwise
     */
    public boolean checkUsername(String enteredUsername) {
        final boolean[] validUsername = {false};

        db.getDocuments(Filter.equalTo("username", enteredUsername), new FirestoreDB.DBCallback<User>() {
            @Override
            public void onSuccess(ArrayList<DatabaseObject<User>> result) {
                if(result.isEmpty()) {
                    validUsername[0] = true;
                }
            }

            @Override
            public void onFailure(Exception e) {
                // will be implemented later on when it has been decided what should happen on failure
            }
        });

        return validUsername[0]; // Boolean value is returned for controller to use accordingly
    }

    /**
     * Updates the username of a given user if the new username is available.
     *
     * @param user        the user whose username is to be updated
     * @param newUsername the new username to be set
     * @return {@code true} if the username was successfully updated, {@code false} otherwise
     */
    public boolean updateUsername(User user, String newUsername) {
        boolean validUsername = checkUsername(newUsername);

        if(validUsername) {
            db.getDocuments(Filter.equalTo("username", user.getUsername()), new FirestoreDB.DBCallback<User>() {
                @Override
                public void onSuccess(ArrayList<DatabaseObject<User>> result) {
                    DatabaseObject<User> currentUser = result.get(0);
                    currentUser.getObject()
                            .setUsername(newUsername);
                    currentUser.save();
                }

                @Override
                public void onFailure(Exception e) {
                    // will be implemented later on when it has been decided what should happen on failure
                }
            });
        }

        return validUsername; // boolean value is returned for controller to use accordingly
    }
}