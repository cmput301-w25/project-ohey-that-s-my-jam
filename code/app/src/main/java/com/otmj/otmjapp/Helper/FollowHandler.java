package com.otmj.otmjapp.Helper;

import android.util.Log;

import com.google.firebase.firestore.Filter;

import com.otmj.otmjapp.Models.Follow;
import com.otmj.otmjapp.Models.FollowRequest;
import com.otmj.otmjapp.Models.User;

import java.util.ArrayList;

/**
 * Manages follow requests and follow relationships between users.
 */
public class FollowHandler {
    private final User currentUser;
    private final FirestoreDB<FollowRequest> requestDB;
    private final FirestoreDB<Follow> followDB;

    public enum FollowType {Followers, Following}

    public interface FollowCountCallback {
        /**
         * Provides access to the 'followers' or 'following' counts of the user
         *
         * @param amount the number of followers or following
         */
        void result(int amount);
    }

    public interface FollowIDCallback {
        void result(ArrayList<String> ids);
    }

    public interface FollowCallback {
        void onSuccess(ArrayList<User> followList);
        void onFailure(Exception e);
    }

    public FollowHandler() {
        this.currentUser = UserManager.getInstance().getCurrentUser();
        requestDB = new FirestoreDB<>(FirestoreCollections.FollowRequests.name);
        followDB = new FirestoreDB<>(FirestoreCollections.Follows.name);
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
        Filter getFollowRequest = Filter.and(
                Filter.equalTo("followerID", followerID),
                Filter.equalTo("followeeID", currentUser.getID())
        );

        requestDB.getDocuments(getFollowRequest, FollowRequest.class, new FirestoreDB.DBCallback<>() {
            @Override
            public void onSuccess(ArrayList<FollowRequest> result) {
                FollowRequest follower = result.get(0);

                Follow newFollow = new Follow(follower.getFollowerID(), currentUser.getID());
                followDB.addDocument(newFollow, null); // callback can be implemented later

                requestDB.deleteDocument(follower);
            }

            @Override
            public void onFailure(Exception e) { /*can be implemented later*/}
        });
    }

    public void getFollowIDs(String userID, FollowType followType, FollowIDCallback callback) {
        Filter filter;
        if (followType == FollowType.Followers) {
            filter = Filter.equalTo("followeeID", userID);
        } else {
            filter = Filter.equalTo("followerID", userID);
        }

        followDB.getDocuments(filter, Follow.class, new FirestoreDB.DBCallback<>() {
            @Override
            public void onSuccess(ArrayList<Follow> result) {
                ArrayList<String> ids = new ArrayList<>();
                for (Follow f : result) {
                    if (followType == FollowType.Followers) {
                        ids.add(f.getFollowerID());
                    } else {
                        ids.add(f.getFolloweeID());
                    }
                }

                callback.result(ids);
            }

            @Override
            public void onFailure(Exception e) {
                callback.result(new ArrayList<>());
            }
        });
    }

    private void getFollows(String userID, FollowType followType, FollowCallback callback) {
       getFollowIDs(userID, followType, ids -> {
           UserManager userManager = UserManager.getInstance();
           userManager.getUsers(ids, new UserManager.AuthenticationCallback() {
               @Override
               public void onAuthenticated(ArrayList<User> authenticatedUsers) {
                   callback.onSuccess(authenticatedUsers);
               }

               @Override
               public void onAuthenticationFailure(String reason) {
                   callback.onFailure(new Exception(reason));
               }
           });
       });
    }

    /**
     * Gets the number of followers or following the user.
     *
     * @param followType either 'followers' or 'following'
     * @param callback   the callback to handle the result
     */
    public void getFollowCount(String userID, FollowType followType, FollowCountCallback callback) {
        getFollowIDs(userID, followType, ids -> callback.result(ids.size()));
    }

    /**
     * Fetches the list of followers for the specified user.
     *
     * @param callback      the callback to handle the result, passing the list of followers or an error
     */
    public void fetchFollowers(String userID, FollowCallback callback) {
        getFollows(userID, FollowType.Followers, callback);
    }

    /**
     * Fetches the list of user the specified user is following
     *
     * @param callback      the callback to handle the result, passing the list of followers or an error
     */
    public void fetchFollowing(String userID, FollowCallback callback) {
        getFollows(userID, FollowType.Following, callback);
    }
}