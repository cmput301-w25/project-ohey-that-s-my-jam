package com.otmj.otmjapp.Helper;

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
}