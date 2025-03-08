package com.otmj.otmjapp.Helper;

import com.google.firebase.firestore.Filter;

import com.otmj.otmjapp.Models.DatabaseObject;
import com.otmj.otmjapp.Models.Entity;
import com.otmj.otmjapp.Models.Follow;
import com.otmj.otmjapp.Models.FollowRequest;
import com.otmj.otmjapp.Models.User;

import java.util.ArrayList;

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
}
