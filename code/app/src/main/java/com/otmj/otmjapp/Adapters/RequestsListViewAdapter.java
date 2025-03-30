package com.otmj.otmjapp.Adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.otmj.otmjapp.Helper.FollowHandler;
import com.otmj.otmjapp.Helper.ImageHandler;
import com.otmj.otmjapp.Helper.UserManager;
import com.otmj.otmjapp.Models.User;
import com.otmj.otmjapp.R;

import java.util.ArrayList;

public class RequestsListViewAdapter extends ArrayAdapter<User> {
    private final FollowHandler followHandler;
    public RequestsListViewAdapter(Context context, ArrayList<User> followersList) {
        super(context, 0, followersList); // Pass context, layout resource, and the data
        followHandler = new FollowHandler();
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        // If the list is empty, return an empty view
        if (getCount() == 0) {
            // You can choose to return a placeholder view here if necessary.
            return new View(getContext()); // Empty view if the list is empty
        }

        View listItemView = convertView;

        if(null == listItemView) {
            listItemView = LayoutInflater.from(getContext()).inflate(R.layout.request_block, parent, false);
        }

        User user = getItem(position);
        assert user != null;

        // Set the username
        TextView userNameTextView = listItemView.findViewById(R.id.username);
        userNameTextView.setText(user.getUsername());

        // Get the profile picture URL
        String profilePicUrl = user.getProfilePictureLink();

        // Check if the profile picture URL is null or empty
        if (profilePicUrl == null || profilePicUrl.isEmpty()) {
            profilePicUrl = "android.resource://com.otmj.otmjapp/drawable/placeholder_image"; // Use a placeholder image
        }

        Button confirmRequest = listItemView.findViewById(R.id.confirm_request_button);
        confirmRequest.setOnClickListener(view -> {
            View request = (View) view.getParent();
            TextView usernameField = request.findViewById(R.id.username);
            String username = usernameField.getText().toString();

            UserManager.getInstance().getUser(username, new UserManager.AuthenticationCallback() {
                @Override
                public void onAuthenticated(ArrayList<User> authenticatedUsers) {
                    User requester = authenticatedUsers.get(0);
                    followHandler.acceptFollowRequest(requester.getID());

                    remove(user);
                    // TODO: show the confirmed request screen once created
                }

                @Override
                public void onAuthenticationFailure(String reason) {
                    Log.e("FollowListFragment", "Error getting user: " + reason);
                }
            });
        });

        ImageView profileImageView = listItemView.findViewById(R.id.profile_image);
        // Load the profile image if available
        if (user.getProfilePictureLink() != null && !user.getProfilePictureLink().isEmpty()) {
            ImageHandler.loadCircularImage(listItemView.getContext(), user.getProfilePictureLink(), profileImageView);
        } else {
            profileImageView.setImageResource(R.drawable.profile_placeholder); // default image
        }

        return listItemView;
    }
}
