package com.otmj.otmjapp.Helper;

import com.otmj.otmjapp.Models.DatabaseObject;
import com.otmj.otmjapp.Models.User;

import java.util.ArrayList;

public class UserManager {
    private final FirestoreDB db;

    public UserManager(FirestoreDB db) {
        this.db = db;
    }

    /**
     * Checks if a given username is already in use.
     *
     * @param username the username to check for availability
     * @return {@code true} if the username is available, {@code false} otherwise
     */
    public boolean checkUsername(String username) {
        final boolean[] validUsername = {true};

        db.getDocuments(new FirestoreDB.DBCallback() {
            @Override
            public void onSuccess(ArrayList<DatabaseObject> result) {
                for (DatabaseObject object : result) {
                    User user = (User) object.getObject();

                    if(user.getUsername().equals(username)) {
                        validUsername[0] = false;

                        break;
                    }
                }
            }

            @Override
            public void onFailure(Exception e) {} // might not be necessary
        });

        return validUsername[0]; // Boolean value is returned for controller to use accordingly
    }

    /**
     * Checks if a given email is already in use.
     *
     * @param email the email to check for availability
     * @return {@code true} if the email is available, {@code false} otherwise
     */
    public boolean checkEmail(String email) {
        final boolean[] validEmail = {true};

        db.getDocuments(new FirestoreDB.DBCallback() {
            @Override
            public void onSuccess(ArrayList<DatabaseObject> result) {
                for (DatabaseObject object : result) {
                    User user = (User) object.getObject();

                    if(user.getEmail().equals(email)) {
                        validEmail[0] = false;

                        break;
                    }
                }
            }

            @Override
            public void onFailure(Exception e) {} // might not be necessary
        });

        return validEmail[0]; // Boolean value is returned for controller to use accordingly
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
            user.setUsername(newUsername);

            DatabaseObject document = new DatabaseObject(user.getId(), user, db);
            document.save();
        }

        return validUsername; // boolean value is returned for controller to use accordingly
    }
}
