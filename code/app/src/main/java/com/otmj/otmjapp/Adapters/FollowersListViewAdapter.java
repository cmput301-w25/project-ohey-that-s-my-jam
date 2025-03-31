package com.otmj.otmjapp.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.otmj.otmjapp.Helper.ImageHandler;
import com.otmj.otmjapp.Models.User;
import com.otmj.otmjapp.R;
import java.util.ArrayList;
import java.util.List;

public class FollowersListViewAdapter extends ArrayAdapter<User> implements Filterable {

    private final Context context;
    private final List<User> originalList;
    private List<User> filteredList;

    public FollowersListViewAdapter(Context context, ArrayList<User> followersList) {
        super(context, 0, followersList);
        this.context = context;
        this.originalList = new ArrayList<>(followersList); // copy
        this.filteredList = new ArrayList<>(followersList); // active view
    }

    @Override
    public int getCount() {
        return filteredList.size();
    }

    @Override
    public User getItem(int position) {
        return filteredList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View listItemView = convertView;
        if (listItemView == null) {
            listItemView = LayoutInflater.from(context).inflate(R.layout.user_block, parent, false);
        }

        User user = getItem(position);
        assert user != null;

        TextView userNameTextView = listItemView.findViewById(R.id.username);
        userNameTextView.setText(user.getUsername());

        ImageView profileImageView = listItemView.findViewById(R.id.profile_image);
        String profilePicUrl = user.getProfilePictureLink();

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
