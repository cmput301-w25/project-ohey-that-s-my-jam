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
    private final FirestoreDB db;
    public UserManager(FirestoreDB db) {
        this.db = db;
    }

    /**
     * Attempts to authenticate a user with the provided username and password.
     *
     * @param enteredUsername the username entered by the user
     * @param enteredPassword the password entered by the user
     * @return the authenticated {@link User} object if login is successful, or {@code null} otherwise
     */

    public User login(String enteredUsername, String enteredPassword) {
        final User[] user = {null};

        db.getDocuments(Filter.equalTo("username", enteredUsername), new FirestoreDB.DBCallback<User>() {
            @Override
            public void onSuccess(ArrayList<DatabaseObject<User>> result) {
                if (!result.isEmpty()) {
                    User u = result.get(0).getObject();

                    if (u.getPassword().equals(enteredPassword)) {
                        user[0] = u;
                    }
                }
            }

            @Override
            public void onFailure(Exception e) {
                // will be implemented later on when it has been decided what should happen on failure
            }
        });

        return user[0]; // return the authenticated user
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