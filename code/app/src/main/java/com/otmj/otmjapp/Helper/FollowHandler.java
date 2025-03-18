package com.otmj.otmjapp.Helper;

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
    private final UserManager userManager;
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
        userManager = UserManager.getInstance();
        currentUser = userManager.getCurrentUser();
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

    /**
     * Retrieves the IDs of followeers of followees for a given user based on the specified follow type.
     *
     * @param userID     The ID of the user for whom to fetch follower or followee IDs.
     * @param followType The type of follow relationship to fetch (e.g., Followers or Following).
     * @param callback   A callback to handle the result of the operation. The callback provides a list of user IDs.
     */
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

    /**
     * Retrieves the user IDs of the requests sent to the current user.
     *
     * @param callback  A callback to handle the result of the operation. The callback provides a list of user IDs.
     */
    public void getRequestIDs(FollowIDCallback callback) {
        Filter filter = Filter.equalTo("followeeID", currentUser.getID());

        requestDB.getDocuments(filter, FollowRequest.class, new FirestoreDB.DBCallback<>() {
            @Override
            public void onSuccess(ArrayList<FollowRequest> result) {
                ArrayList<String> ids = new ArrayList<>();

                for (FollowRequest f : result) {
                    ids.add(f.getFollowerID());
                }

                callback.result(ids);
            }

            @Override
            public void onFailure(Exception e) { callback.result(new ArrayList<>()); }
        });
    }

    /**
     * Retrieves the {@link User} objects for followers or followees of a given user based on the specified follow type.
     *
     * @param userID     The ID of the user for whom to fetch followers or followees.
     * @param followType The type of follow relationship to fetch (e.g., Followers or Following).
     * @param callback   A callback to handle the result of the operation. The callback provides a list of user objects.
     */
    private void getFollows(String userID, FollowType followType, FollowCallback callback) {
       getFollowIDs(userID, followType, ids -> {
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
     * Retrieves the {@link User} objects for the users who have sent follow requests to the current user.
     *
     * @param callback  A callback to handle the result of the operation. The callback provides a list of user objects.
     */
    public void getRequests(FollowCallback callback) {
        getRequestIDs(ids -> userManager.getUsers(ids, new UserManager.AuthenticationCallback() {
            @Override
            public void onAuthenticated(ArrayList<User> authenticatedUsers) {
                callback.onSuccess(authenticatedUsers);
            }

            @Override
            public void onAuthenticationFailure(String reason) {
                callback.onFailure(new Exception(reason));
            }
        }));
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

    /**
     * Fetches all the users that the current user is NOT following
     *
     * @param callback      the callback to handle the result, passing the list of followers or an error
     */
    public void fetchNotFollowingUsers(FollowCallback callback) {
        // Step 1: Get all users
        userManager.getAllUsers(new UserManager.AuthenticationCallback() {
            @Override
            public void onAuthenticated(ArrayList<User> allUsers) {
                // Step 2: Get users the current user is following
                fetchFollowing(currentUser.getID(), new FollowCallback() {
                    @Override
                    public void onSuccess(ArrayList<User> followingUsers) {
                        ArrayList<String> followingIDs = new ArrayList<>();
                        for (User user : followingUsers) {
                            followingIDs.add(user.getID());
                        }

                        // Step 3: Filter out users that are in the following list
                        ArrayList<User> notFollowingUsers = new ArrayList<>();
                        for (User user : allUsers) {
                            if (!followingIDs.contains(user.getID()) && !user.getID().equals(currentUser.getID())) {
                                notFollowingUsers.add(user);
                            }
                        }

                        callback.onSuccess(notFollowingUsers);
                    }

                    @Override
                    public void onFailure(Exception e) {
                        callback.onFailure(e);
                    }
                });
            }

            @Override
            public void onAuthenticationFailure(String reason) {
                callback.onFailure(new Exception(reason));
            }
        });
    }
}