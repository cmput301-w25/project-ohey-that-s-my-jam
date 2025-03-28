package com.otmj.otmjapp.Helper;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.Filter;
import com.otmj.otmjapp.Fragments.UserProfileFragment;
import com.otmj.otmjapp.Models.User;
import com.otmj.otmjapp.R;

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

    public UserManager(FirestoreDB<User> db) { // Constructor with custom FirestoreDB instance for testing
        this.db = db;
    }

    public static UserManager getInstance() {
        return instance;
    }

    /**
     * Attempts to authenticate a user with the provided username and password.
     *
     * @param enteredUsername The username entered by the user.
     * @param enteredPassword The password entered by the user.
     * @param callback        The callback provides an interface for handling
     *                        authentication success and failure.
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
     *
     * @param user The user to add.
     * @param callback Callback to handle authentication results.
     */
    public void signup(User user, @NonNull AuthenticationCallback callback) {
        // Check that username doesn't already exist
        checkIfUserExists(user, correct -> {
            // If user doesn't exist
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
        });
    }

    /**
     * Logs out the current user and navigates back to the welcome screen.
     *
     * @param page The current fragment page from which to logout.
     */
    public void logout(Fragment page) {
        if (!(page instanceof UserProfileFragment)) {
           Log.e("UserManager", "Can only logout from profile page!");
        } else if (currentUser != null) {
            currentUser = null;
            NavHostFragment.findNavController(page).navigate(R.id.logoutFromApp);
        }
    }

    /**
     * Retrieves users based on a list of user IDs.
     *
     * @param userIDs List of user IDs to fetch.
     * @param callback Callback to return the authenticated users or an error message.
     */
    public void getUsers(List<String> userIDs, @NonNull AuthenticationCallback callback) {
        if (userIDs == null || userIDs.isEmpty()) {
            callback.onAuthenticated(new ArrayList<>()); // Return an empty list
            return;
        }

        Filter byID = Filter.inArray(FieldPath.documentId(), userIDs);
        db.getDocuments(byID, User.class, new FirestoreDB.DBCallback<>() {
            @Override
            public void onSuccess(ArrayList<User> result) {
                // If the number of users returned matches the number of IDs
                if (result.size() == userIDs.size() && !result.isEmpty()) {
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
     * Gets the corresponding user given their username.
     *
     * @param username The username of the user to retrieve
     * @param callback Callback to return the user or an error message.
     */
    public void getUser(String username, @NonNull AuthenticationCallback callback) {
        db.getDocuments(User.class, new FirestoreDB.DBCallback<>() {
            @Override
            public void onSuccess(ArrayList<User> result) {
                for(User u : result) {
                    if(u.getUsername().equals(username)) {
                        callback.onAuthenticated(new ArrayList<>(List.of(u)));

                        return;
                    }
                }
            }

            @Override
            public void onFailure(Exception e) {
                Log.e("UserManager", "Error fetching users: " + e.getMessage());
                callback.onAuthenticationFailure(e.getMessage());
            }
        });
    }

    /**
     * Checks if a given user exists in the database by checking their username or email.
     *
     * @param user The user's details to check.
     * @param callback Callback to handle the result, returns true if user exists and false if not.
     */
    public void checkIfUserExists(User user, CheckCallback callback) {
        Filter byUsernameOrEmail = Filter.or(
                Filter.equalTo("username", user.getUsername()),
                Filter.equalTo("emailAddress", user.getEmailAddress())
        );

        db.getDocuments(byUsernameOrEmail, User.class, new FirestoreDB.DBCallback<>() {
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
     * Gets the currently logged-in user.
     *
     * @return A `User` object representing the currently logged-in user.
     */
    public User getCurrentUser() {
        return currentUser;
    }

    /**
     * Grabs all users from the database.
     *
     * @param callback Callback to return the list of users or an error message.
     */
    public void getAllUsers(@NonNull AuthenticationCallback callback) {
        db.getDocuments(User.class, new FirestoreDB.DBCallback<>() {
            @Override
            public void onSuccess(ArrayList<User> result) {
                // Log fetched users
                Log.d("UserManager", "Fetched Users: " + result.toString());

                callback.onAuthenticated(result);
            }

            @Override
            public void onFailure(Exception e) {
                Log.e("UserManager", "Error fetching users: " + e.getMessage());
                callback.onAuthenticationFailure(e.getMessage());
            }
        });
    }

}
