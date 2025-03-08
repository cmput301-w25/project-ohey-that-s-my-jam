package com.otmj.otmjapp.Helper;

import com.google.firebase.firestore.Filter;

import com.otmj.otmjapp.Models.DatabaseObject;
import com.otmj.otmjapp.Models.Follow;
import com.otmj.otmjapp.Models.FollowRequest;
import com.otmj.otmjapp.Models.User;

import java.util.ArrayList;

public class FollowHandler {
    private final User currentUser;
    private final FirestoreDB<FollowRequest> requestDB;
    public FollowHandler(UserManager userManager) {
        this.currentUser = userManager.getCurrentUser();
        requestDB = new FirestoreDB<>(FirestoreCollections.FollowRequests.name);
    }

    /**
     * Creates a new follow request.
     * @param followerUsername
     */
    public void sendFollowRequest(String followerUsername) {
        FollowRequest followRequest = new FollowRequest(currentUser.getUsername(), followerUsername);
        requestDB.addDocument(followRequest, null); // callback can be implemented later
    }

    /**
     * Creates a 'follow' relationship between two users.
     * @param followerUsername
     */
    public void acceptFollowRequest(String followerUsername) {
        Filter getFollowRequest = Filter.and(Filter.equalTo("followerID", followerUsername), Filter.equalTo("followeeID", currentUser.getUsername()));

        requestDB.getDocuments(getFollowRequest, new FirestoreDB.DBCallback<FollowRequest>() {
            @Override
            public void onSuccess(ArrayList<DatabaseObject<FollowRequest>> result) {
                DatabaseObject<FollowRequest> request = result.get(0);
                Follow newFollow = new Follow(request.getObject().getFollowerID(), request.getObject().getFolloweeID());

                FirestoreDB<Follow> followDB = new FirestoreDB<>(FirestoreCollections.Follows.name);
                followDB.addDocument(newFollow, null); // callback can be implemented later

                request.delete();
            }

            @Override
            public void onFailure(Exception e) { /*can be implemented later*/}
            });
    }
}
