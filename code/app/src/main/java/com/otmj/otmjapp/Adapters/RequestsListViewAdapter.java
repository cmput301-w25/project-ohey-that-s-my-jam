package com.otmj.otmjapp.Adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
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
import java.util.List;

public class RequestsListViewAdapter extends ArrayAdapter<User> implements Filterable {

    private final Context context;
    private final FollowHandler followHandler;
    private final List<User> originalList;
    private List<User> filteredList;

    public RequestsListViewAdapter(Context context, ArrayList<User> followersList) {
        super(context, 0, followersList);
        this.context = context;
        this.followHandler = new FollowHandler();
        this.originalList = new ArrayList<>(followersList);
        this.filteredList = new ArrayList<>(followersList);
    }

    @Override
    public int getCount() {
        return filteredList.size();
    }

    @Override
    public User getItem(int position) {
        return filteredList.get(position);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View listItemView = convertView;

        if (listItemView == null) {
            listItemView = LayoutInflater.from(context).inflate(R.layout.request_block, parent, false);
        }

        User user = getItem(position);
        assert user != null;

        TextView userNameTextView = listItemView.findViewById(R.id.username);
        userNameTextView.setText(user.getUsername());

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

                    // Remove from both lists
                    filteredList.remove(user);
                    originalList.remove(user);
                    notifyDataSetChanged();
                }

                @Override
                public void onAuthenticationFailure(String reason) {
                    Log.e("FollowListFragment", "Error getting user: " + reason);
                }
            });
        });

        String profilePicUrl = user.getProfilePictureLink();
        ImageView profileImageView = listItemView.findViewById(R.id.profile_image);
        ImageHandler.loadCircularImage(
                context,
                (profilePicUrl == null || profilePicUrl.isEmpty()) ? "" : profilePicUrl,
                profileImageView
        );

        return listItemView;
    }

    @Override
    public Filter getFilter() {
        return new Filter() {

            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                List<User> filtered = new ArrayList<>();

                if (constraint == null || constraint.length() == 0) {
                    filtered.addAll(originalList);
                } else {
                    String query = constraint.toString().toLowerCase().trim();
                    for (User user : originalList) {
                        if (user.getUsername().toLowerCase().contains(query)) {
                            filtered.add(user);
                        }
                    }
                }

                FilterResults results = new FilterResults();
                results.values = filtered;
                results.count = filtered.size();
                return results;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                filteredList = (List<User>) results.values;
                notifyDataSetChanged();
            }
        };
    }
}
