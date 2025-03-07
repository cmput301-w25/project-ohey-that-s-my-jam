package com.otmj.otmjapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.otmj.otmjapp.Models.Follow;

import java.util.ArrayList;

public class FollowersRecyclerViewAdapter extends RecyclerView.Adapter<FollowersRecyclerViewAdapter.MyViewHolder> {
    // class variables
    private Context context;
    private ArrayList<Follow> followersList;

    //Constructor for our Adapter Class
    public FollowersRecyclerViewAdapter(Context context, ArrayList<Follow> followersList) {
        this.context = context;
        this.followersList = followersList;
    }

    // Basic Skeleton for RecyclerView Adapter
    // Generated Methods
    @NonNull
    @Override
    public FollowersRecyclerViewAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // 'Inflates' layout (giving a look to our rows)
        // Update
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.recycler_view_row, parent, false);

        return new FollowersRecyclerViewAdapter.MyViewHolder(view);
    }
    //alternative code?
    //public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
    //    View view = LayoutInflater.from(context).inflate(R.layout.recycler_view_row, parent, false);
    //    return new MyViewHolder(view);
    //}

    @Override
    public void onBindViewHolder(@NonNull FollowersRecyclerViewAdapter.MyViewHolder holder, int position) {
        // assigning values to each of the rows as they cycle back onto the screen
        // this is dependent on the position of the recycler view

        //update the data on each of rows
        // change value of holder that's passed in

        holder.tvUsername.setText(.get(position).get);
        holder.
    }

    //alt code to onbind
    //public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
    //    Follow follow = followersList.get(position);
    //    holder.tvUsername.setText(follow.getFollowerID()); // TEMP: Display follower ID until we fetch username
    //}
    @Override
    public int getItemCount() {
        // counts total item amount in FollowersList (might do dummy test list)
        // helps the binding process to update the item index
        return followersList.size();
    }

    //suggested chatgpt updating list dynamically
    public void setData(ArrayList<Follow> newFollowers) {
        this.followersList = newFollowers;
        notifyDataSetChanged();
    }

    // Create MyViewHolder Class
    public static class MyViewHolder extends RecyclerView.ViewHolder { // must be static
        //grabs all the views of follower card and assigns them to variables, similiar to oncreate methods

        ImageView ivProfilePic; // profile pic
        TextView tvUsername; // username of follower

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            ivProfilePic = itemView.findViewById(R.id.profile_image);
            tvUsername = itemView.findViewById(R.id.username);
        }
    }
}

