package com.otmj.otmjapp.Helper;

import android.provider.ContactsContract;

import androidx.annotation.NonNull;

import com.google.firebase.firestore.Filter;
import com.otmj.otmjapp.Models.DatabaseObject;
import com.otmj.otmjapp.Models.User;
import java.util.ArrayList;

/**
 * Manages user authentication and retrieval from the Firestore database.
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
     * @param user User to add
     */
    public void addUser(User user) {
        db.addDocument(user, new FirestoreDB.DBCallback<>() {
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
     * @param callback        Handles database response
     */
    public void checkUsername(String enteredUsername, FirestoreDB.DBCallback<User> callback) {
        Filter byUsername = Filter.equalTo("username", enteredUsername);
        db.getDocuments(byUsername, callback);
    }

    public User getCurrentUser() {
        // TODO: Get user that is currently logged in from (maybe) SharedPrefs
        throw new UnsupportedOperationException("Not implemented yet");
    }
}
