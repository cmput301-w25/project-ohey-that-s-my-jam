package com.otmj.otmjapp.Helper;

import android.util.Log;

import com.google.firebase.firestore.Filter;

import com.google.firebase.firestore.FirebaseFirestore;
import com.otmj.otmjapp.Models.DatabaseObject;
import com.otmj.otmjapp.Models.Entity;
import com.otmj.otmjapp.Models.Follow;
import com.otmj.otmjapp.Models.FollowRequest;
import com.otmj.otmjapp.Models.User;

import java.util.ArrayList;
import java.util.Map;

/**
 * Manages follow requests and follow relationships between users.
 */
public class FollowHandler {
    private final User currentUser;
    private final FirestoreDB requestDB;
    private final FirestoreDB followDB;

    public enum FollowType {Followers, Following}

    public interface FollowCallback {
        /**
         * Provides access to the 'followers' or 'following' counts of the user
         *
         * @param amount the number of followers or following
         */
        void result(int amount);
    }

    public FollowHandler(UserManager userManager) {
        this.currentUser = userManager.getCurrentUser();
        requestDB = new FirestoreDB(FirestoreCollections.FollowRequests.name);
        followDB = new FirestoreDB(FirestoreCollections.Follows.name);
    }

    /**
     * Creates a new follow request.
     *
     * @param followerID the ID of the user who sends the request
     */
    public void sendFollowRequest(String followerID) {
        FollowRequest followRequest = new FollowRequest(currentUser.getID(), followerID);
        requestDB.addDocument(followRequest, null); // callback can be implemented later
    }

    /**
     * Creates a 'follow' relationship between two users.
     *
     * @param followerID the ID of the user who sends the request
     */
    public void acceptFollowRequest(String followerID) {
        Filter getFollowRequest = Filter.and(Filter.equalTo("followerID", followerID), Filter.equalTo("followeeID", currentUser.getID()));

        requestDB.getDocuments(getFollowRequest, new FirestoreDB.DBCallback() {
            @Override
            public void onSuccess(ArrayList<Entity> result) {
                Entity request = result.get(0);

                FollowRequest follower = (FollowRequest) Follow.fromMap(request.objectMap);
                follower.setID(request.ID);

                Follow newFollow = new Follow(follower.getFollowerID(), currentUser.getID());
                followDB.addDocument(newFollow, null); // callback can be implemented later

                requestDB.deleteDocument(follower);
            }

            @Override
            public void onFailure(Exception e) { /*can be implemented later*/}
        });
    }

    /**
     * Gets the number of followers or following the user.
     *
     * @param followType either 'followers' or 'following'
     * @param callback the callback to handle the result
     */
    public void getFollowCount(FollowType followType, FollowCallback callback) {
        Filter getFollowAmount;

        if(followType == FollowType.Followers) {
            getFollowAmount = Filter.equalTo("followeeID", currentUser.getID());
        } else {
            getFollowAmount = Filter.equalTo("followerID", currentUser.getID());
        }

        followDB.getDocuments(getFollowAmount, new FirestoreDB.DBCallback() {
            @Override
            public void onSuccess(ArrayList<Entity> result) {
                callback.result(result.size());
            }

            @Override
            public void onFailure(Exception e) {/*can be implemented later*/}
        });
    }

    /**
     * Fetches the list of followers for the specified user.
     *
     * This method queries the Firestore database for all documents where
     * the `followeeID` matches the `currentUserID`. The follower IDs are
     * extracted from these documents and used to fetch user data.
     *
     * @param currentUserID the ID of the current user whose followers are being fetched
     * @param callback the callback to handle the result, passing the list of followers or an error
     */
    public void fetchFollowers(String currentUserID, final FollowerCallback callback) {
        // Step 1: Get all follow documents where followeeID == currentUserID
        Filter filter = Filter.equalTo("followeeID", currentUserID);
        followDB.getDocuments(filter, new FirestoreDB.DBCallback() {
            @Override
            public void onSuccess(ArrayList<Entity> followEntities) {
                // Step 2: Extract followerIDs
                ArrayList<String> followerIDs = new ArrayList<>();
                for (Entity followEntity : followEntities) {
                    String followerID = (String) followEntity.objectMap.get("followerID");
                    if (followerID != null) {
                        followerIDs.add(followerID);
                    }
                }

                // Step 3: Fetch user data for each followerID
                fetchUsers(followerIDs, callback);
            }

            @Override
            public void onFailure(Exception e) {
                Log.e("FollowHandler", "Failed to fetch followers", e);
                callback.onFailure(e);
            }
        });
    }

    /**
     * Fetches user data for a list of follower IDs.
     *
     * This method queries the Firestore database for each user ID in the
     * `followerIDs` list and collects the corresponding user data.
     *
     * @param followerIDs the list of follower IDs to fetch user data for
     * @param callback the callback to handle the result, passing the list of users or an error
     */
    private void fetchUsers(ArrayList<String> followerIDs, final FollowerCallback callback) {
        ArrayList<User> users = new ArrayList<>();
        for (String followerID : followerIDs) {
            Filter filter = Filter.equalTo("id", followerID);
            followDB.getDocuments(filter, new FirestoreDB.DBCallback() {
                @Override
                public void onSuccess(ArrayList<Entity> userEntities) {
                    for (Entity userEntity : userEntities) {
                        User user = User.fromMap(userEntity.objectMap);
                        users.add(user);
                    }

                    // Once all users are fetched, pass the list to the callback
                    callback.onFollowersFetched(users);
                }

                @Override
                public void onFailure(Exception e) {
                    Log.e("FollowHandler", "Failed to fetch user data", e);
                    callback.onFailure(e);
                }
            });
        }
    }
    /**
     * Callback interface for handling follower data.
     *
     * This interface defines the methods to handle successful fetching of followers
     * or failure during the process.
     */
    public interface FollowerCallback {
        /**
         * Called when the list of followers has been successfully fetched.
         *
         * @param followers the list of User objects representing the followers
         */
        void onFollowersFetched(ArrayList<User> followers);  // Pass the list of User objects
        /**
         * Called if there is an error during the fetching process.
         *
         * @param e the exception that occurred during the fetch operation
         */
        void onFailure(Exception e);
    }
}