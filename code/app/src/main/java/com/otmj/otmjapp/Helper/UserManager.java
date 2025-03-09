package com.otmj.otmjapp.Helper;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.Filter;
import com.otmj.otmjapp.Models.User;
import java.util.ArrayList;
import java.util.List;

/**
 * Manages user authentication and retrieval from the Firestore database.
 */

public class UserManager {
    /**
     * Callback to return list of `User` objects, or an error message
     */
    public interface AuthenticationCallback {
        void onAuthenticated(ArrayList<User> authenticatedUsers);
        void onAuthenticationFailure(String reason);
    }

    /**
     * Callback to return a boolean result
     */
    public interface CheckCallback {
        void answer(boolean correct);
    }

    // Singleton pattern
    private static final UserManager instance = new UserManager();
    private final FirestoreDB<User> db;
    private User currentUser = null;

    private UserManager() {
        this.db = new FirestoreDB<>(FirestoreCollections.Users.name);
    }

    public static UserManager getInstance() {
        return instance;
    }

    /**
     * Attempts to authenticate a user with the provided username and password.
     *
     * @param enteredUsername the username entered by the user
     * @param enteredPassword the password entered by the user
     * @param callback        the callback provides an interface for handling
     *                        authentication success and failure
     */

    public void login(String enteredUsername,
                      String enteredPassword,
                      @NonNull AuthenticationCallback callback) {
        Filter byUsername = Filter.equalTo("username", enteredUsername);
        db.getDocuments(byUsername, User.class, new FirestoreDB.DBCallback<>() {
            @Override
            public void onSuccess(ArrayList<User> result) {
                if (!result.isEmpty()) {
                    User u = result.get(0);

                    if (u.isPassword(enteredPassword)) {
                        currentUser = u;
                        callback.onAuthenticated(new ArrayList<>(List.of(u)));
                    } else {
                        callback.onAuthenticationFailure("Wrong username or password");
                    }
                } else {
                    callback.onAuthenticationFailure("No such user");
                }
            }

            @Override
            public void onFailure(Exception e) {
                callback.onAuthenticationFailure(e.getMessage());
            }
        });

    }

    /**
     * Adds a new user to the database and makes them the current user.
     * @param user User to add
     */
    public void signup(User user, @NonNull AuthenticationCallback callback) {
        // Check that username doesn't already exist
        checkUsername(user.getUsername(), new CheckCallback() {
            @Override
            public void answer(boolean correct) {
                // If username doesn't doesn't exist
                if (!correct) {
                    db.addDocument(user, new FirestoreDB.DBCallback<>() {
                        @Override
                        public void onSuccess(ArrayList<User> result) {
                            if (!result.isEmpty()) {
                                User u = result.get(0);

                                currentUser = u;
                                callback.onAuthenticated(new ArrayList<>(List.of(u)));
                            } else {
                                callback.onAuthenticationFailure("Unable to access server");
                            }
                        }

                        @Override
                        public void onFailure(Exception e) {
                            callback.onAuthenticationFailure(e.getMessage());
                        }
                    });
                } else {
                    callback.onAuthenticationFailure("User already exists");
                }
            }
        });
    }

    public void getUsers(ArrayList<String> userIDs, @NonNull AuthenticationCallback callback) {
        Filter byID = Filter.inArray(FieldPath.documentId(), userIDs);
        db.getDocuments(byID, User.class, new FirestoreDB.DBCallback<>() {
            @Override
            public void onSuccess(ArrayList<User> result) {
                // If the number of users returned matches the number of IDs
                if (result.size() == userIDs.size()) {
                    callback.onAuthenticated(result);
                } else {
                    callback.onAuthenticationFailure("Users not found");
                }
            }

            @Override
            public void onFailure(Exception e) {
                callback.onAuthenticationFailure(e.getMessage());
            }
        });
    }

    /**
     * Checks if a given username is already in use.
     *
     * @param enteredUsername the username to check for availability
     * @param callback        Handles database response
     */
    public void checkUsername(String enteredUsername, CheckCallback callback) {
        Filter byUsername = Filter.equalTo("username", enteredUsername);
        db.getDocuments(byUsername, User.class, new FirestoreDB.DBCallback<>() {
            @Override
            public void onSuccess(ArrayList<User> result) {
                boolean userExists = !result.isEmpty();
                callback.answer(userExists);
            }

            @Override
            public void onFailure(Exception e) {
                callback.answer(false);
            }
        });
    }

    /**
     * Get currently logged in user
     * @return A `User` object if there's a user that is logged in
     */
    public User getCurrentUser() {
        return currentUser;
    }
}
