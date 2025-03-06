package com.otmj.otmjapp.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.otmj.otmjapp.Models.MoodEvent;

import java.util.List;

public class FollowersListAdapter extends RecyclerView.Adapter<FollowersListAdapter.ViewHolder> {
    private Context context;
    private List<MoodEvent> followerList;

    // Constructor
    public FollowersListAdapter(Context context, List<MoodEvent> followerList) {
        this.context = context;
        this.followerList = followerList;
    }

    // ViewHolder class that binds follower data to the UI
    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView usernameTextView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            usernameTextView = itemView.findViewById(R.id.usernameTextView);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_follower, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        MoodEvent follower = followerList.get(position);
        holder.usernameTextView.setText(follower.getUsername());
    }

    @Override
    public int getItemCount() {
        return followerList.size();
    }
}

