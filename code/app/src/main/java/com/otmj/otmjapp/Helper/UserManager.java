package com.otmj.otmjapp.Helper;

import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.Filter;
import com.google.firebase.firestore.FirebaseFirestore;
import com.otmj.otmjapp.Models.DatabaseObject;
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
        void onAuthenticated(ArrayList<DatabaseObject<User>> authenticatedUsers);
        void onAuthenticationFailure(String reason);
    }

    /**
     * Callback to return a boolean result
     */
    public interface CheckCallback {
        void answer(boolean correct);
    }

    // Singleton patter
    private static final UserManager instance = new UserManager();
    private final FirestoreDB<User> db;
    private DatabaseObject<User> currentUser = null;

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

    public void login(String enteredUsername, String enteredPassword, AuthenticationCallback callback) {
        Filter byUsername = Filter.equalTo("username", enteredUsername);
        db.getDocuments(byUsername, new FirestoreDB.DBCallback<>() {
            @Override
            public void onSuccess(ArrayList<DatabaseObject<User>> result) {
                if (!result.isEmpty()) {
                    DatabaseObject<User> dob = result.get(0);
                    User u = dob.getObject();

                    if (u.isPassword(enteredPassword)) {
                        currentUser = dob;
                        callback.onAuthenticated(new ArrayList<>(List.of(dob)));
                    } else {
                        callback.onAuthenticationFailure("Wrong password");
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

    public void getUsers(ArrayList<String> userIDs, AuthenticationCallback callback) {
        Filter byID = Filter.inArray(FieldPath.documentId(), userIDs);
        db.getDocuments(byID, new FirestoreDB.DBCallback<>() {
            @Override
            public void onSuccess(ArrayList<DatabaseObject<User>> result) {
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
        db.getDocuments(byUsername, new FirestoreDB.DBCallback<User>() {
            @Override
            public void onSuccess(ArrayList<DatabaseObject<User>> result) {
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
        if (currentUser != null) {
            return currentUser.getObject();
        }
        return null;
    }
}
