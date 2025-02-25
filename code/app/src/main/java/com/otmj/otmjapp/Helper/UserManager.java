package com.otmj.otmjapp.Helper;

import com.google.firebase.firestore.auth.User;
import com.otmj.otmjapp.Models.DatabaseObject;

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

        db.getDocuments(new FirestoreDB.DBCallback() {
            @Override
            public void onSuccess(ArrayList<DatabaseObject> result) {
                for (DatabaseObject object : result) {
                    User potentialMatch = (User) object.getObject();

                    if(potentialMatch.getUsername().equals(enteredUsername)) {
                        if(potentialMatch.isPassword(enteredPassword)) {
                            user[0] = potentialMatch;

                            break;
                        }
                    }
                }
            }

            @Override
            public void onFailure(Exception e) {} // might not be necessary
        });

        return user[0]; // return the authenticated user
    }
}