package com.otmj.otmjapp.Fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import androidx.fragment.app.Fragment;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.otmj.otmjapp.Adapters.FollowersListViewAdapter;
import com.otmj.otmjapp.Models.User;
import com.otmj.otmjapp.R;

import java.util.ArrayList;
import java.util.Map;


public class FollowersListFragment extends Fragment {

    private FirebaseFirestore db;

    public FollowersListFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.list_screen, container, false);

        // Initialize Firestore
        db = FirebaseFirestore.getInstance();

        // Fetch followers data from Firestore and set up the ListView
        fetchFollowersFromFirestore(rootView);

        return rootView;
    }

    // Queries the DB and creates a list of ids of the followers - followerIDs
    public void fetchFollowersFromFirestore(View rootView) {

        // Todo: Hard coded current user value as getCurrentUser() doesn't return proper data yet
        // Get the instance of UserManager (singleton)
        // UserManager userManager = UserManager.getInstance();
        // Get the current user using the instance
        // User currentUser = userManager.getCurrentUser();


        // Fetch the followers for the current user using their userID
        db.collection("follows")
                .whereEqualTo("followeeID", "1") // Query for followers based on the current user's ID
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    ArrayList<String> followerIDs = new ArrayList<>(); // List of followerIDs that follow current user

                    // Check if any documents are returned
                    if (queryDocumentSnapshots.isEmpty()) {
                        Log.d("Firestore", "No documents found for followeeID = 1.");
                    } else {
                        // Get all follower IDs
                        for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                            String followerID = documentSnapshot.getString("followerID");
                            followerIDs.add(followerID); // Collect follower IDs
                            Log.d("Firestore", "Found follower ID: " + followerID);  // Log each follower ID found
                        }
                    }

                    // Log the entire list of follower IDs to check its contents
                    Log.d("Firestore", "Fetched follower IDs: " + followerIDs);

                    // Check if the list is empty
                    if (followerIDs.isEmpty()) {
                        Log.d("Firestore", "No followers found for the current user.");
                    } else {
                        // Once we have the list of follower IDs, query the `users` collection to get details
                        fetchUserDetails(rootView, followerIDs);
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("Firestore", "Error getting documents: ", e);
                });
    }


    public void fetchUserDetails(View rootView, ArrayList<String> followerIDs) {
        // Check if followerIDs is empty
        if (followerIDs.isEmpty()) {
            Log.d("Firestore", "No follower IDs to query.");
            return;
        }

        Log.d("Firestore", "Fetching user details for follower IDs: " + followerIDs);

        // Query the users collection to get data for each follower ID
        db.collection("users")
                .whereIn("id", followerIDs) // Query for users with IDs in the list of followerIDs
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (queryDocumentSnapshots.isEmpty()) {
                        Log.d("Firestore", "No users found for the given follower IDs.");
                    } else {
                        ArrayList<User> followersList = new ArrayList<>();

                        // Log the number of documents returned
                        Log.d("Firestore", "Found " + queryDocumentSnapshots.size() + " users.");

                        for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                            // Get the document data
                            Map<String, Object> documentData = documentSnapshot.getData();

                            // Check in the weird case the data is null
                            if (documentData != null) {
                                // Log the document ID and the data
                                Log.d("Firestore", "User ID: " + documentSnapshot.getId() + " Data: " + documentData);

                                // Create User object from the document data using fromMap() method
                                User user = User.fromMap(documentData);

                                // Add the User object to the followers list
                                followersList.add(user);
                            } else {
                                Log.w("Firestore", "Document data is null for userID: " + documentSnapshot.getId());
                            }
                        }

                        // Log the followers list size before passing it to the adapter
                        Log.d("Firestore", "Followers list size: " + followersList.size());

                        // Once you have the list of User objects, set up the ListView
                        setUpFollowersList(rootView, followersList); // Pass the rootView and followers list to the adapter
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("Firestore", "Error getting user data: ", e);
                });
    }




    public void setUpFollowersList(View rootView, ArrayList<User> followersList) {
    // Initialize the adapter and set it to the ListView
    FollowersListViewAdapter adapter = new FollowersListViewAdapter(getContext(), followersList);
    ListView listView = rootView.findViewById(R.id.user_list_view);
    listView.setAdapter(adapter);
}

}
