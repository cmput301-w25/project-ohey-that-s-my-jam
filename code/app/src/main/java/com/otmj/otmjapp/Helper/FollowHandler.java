package com.otmj.otmjapp.Helper;

import com.google.firebase.firestore.Filter;
import com.otmj.otmjapp.Models.FirestoreCollections;
import com.otmj.otmjapp.Models.Follow;
import com.otmj.otmjapp.Models.FollowRequest;
import com.otmj.otmjapp.Models.User;

import java.util.ArrayList;
import java.util.function.Consumer;

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
     * Checks if a follow request has already been sent by the current user to a target user.
     *
     * @param followeeID The ID of the user receiving the request.
     * @param callback     A callback to handle the result (true if request exists, false otherwise).
     */
    public void hasFollowRequestBeenSent(String followeeID, Consumer<Boolean> callback) {
        Filter filter = Filter.and(
                Filter.equalTo("followerID", currentUser.getID()),
                Filter.equalTo("followeeID", followeeID)
        );

        requestDB.getDocuments(filter, FollowRequest.class, new FirestoreDB.DBCallback<>() {
            @Override
            public void onSuccess(ArrayList<FollowRequest> result) {
                callback.accept(!result.isEmpty()); // Returns true if any request exists
            }

            @Override
            public void onFailure(Exception e) {
                callback.accept(false); // If the query fails, assume no request was sent
            }
        });
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
                FollowRequest request = result.get(0);

                Follow newFollow = new Follow(request.getFollowerID(), currentUser.getID());
                followDB.addDocument(newFollow, null); // callback can be implemented later

                requestDB.deleteDocument(request);
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
    private void getRequestIDs(FollowIDCallback callback) {
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
     * Gets the number of user that have requested to follow the current user
     * @param callback The callback to handle the result
     */
    public void getRequestCount(FollowCountCallback callback) {
        getRequestIDs(ids -> callback.result(ids.size()));
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
     * Fetches all the users that the current user is NOT following, excluding the current user.
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
                        // Step 3: Filter out users that are in the following list and the current user
                        ArrayList<User> notFollowingUsers = new ArrayList<>();
                        for (User user : allUsers) {
                            // Exclude the current user and users the current user is following
                            if (!user.equals(currentUser) && !followingUsers.contains(user)) {
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

    /**
     * Checks if the current user is following a specific user.
     *
     * @param targetUserID The ID of the user to check.
     * @param callback A callback to receive the result (true if following, false otherwise).
     */
    public void isFollowing(String targetUserID, Consumer<Boolean> callback) {
        Filter filter = Filter.and(
                Filter.equalTo("followerID", currentUser.getID()),
                Filter.equalTo("followeeID", targetUserID)
        );

        followDB.getDocuments(filter, Follow.class, new FirestoreDB.DBCallback<>() {
            @Override
            public void onSuccess(ArrayList<Follow> result) {
                callback.accept(!result.isEmpty());
            }

            @Override
            public void onFailure(Exception e) {
                callback.accept(false); // fallback if query fails
            }
        });
    }

    /**
     * Unfollows a user by removing the follow relationship from the database.
     *
     * @param followeeID The ID of the user to unfollow.
     */
    public void unfollowUser(String followeeID) {
        // Define a filter to identify the follow relationship between the current user and the target user
        Filter filter = Filter.and(
                Filter.equalTo("followerID", currentUser.getID()),
                Filter.equalTo("followeeID", followeeID)
        );

        // Get the follow relationship from the database
        followDB.getDocuments(filter, Follow.class, new FirestoreDB.DBCallback<Follow>() {
            @Override
            public void onSuccess(ArrayList<Follow> result) {
                if (!result.isEmpty()) {
                    // If a follow relationship exists, remove it
                    followDB.deleteDocument(result.get(0));
                }
            }

            @Override
            public void onFailure(Exception e) {
                // Handle any failure during the unfollow operation
            }
        });
    }
}
