package com.otmj.otmjapp.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.otmj.otmjapp.Fragments.UserProfileFragment;
import com.otmj.otmjapp.Helper.ImageHandler;
import com.otmj.otmjapp.Models.Comment;
import com.otmj.otmjapp.R;

import java.util.List;

public class CommentAdapter extends ArrayAdapter<Comment> {

    private final Context context;

    public CommentAdapter(Context context, List<Comment> comments) {
        super(context, R.layout.comment_block, comments);
        this.context = context;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.comment_block, parent, false);
        }

        Comment comment = getItem(position);
        if (comment == null) {
            return convertView;
        }

        // Bind data to the views in comment_block.xml
        TextView usernameTextView = convertView.findViewById(R.id.comment_username);
        TextView commentTextView = convertView.findViewById(R.id.comment_text);
        TextView timestampTextView = convertView.findViewById(R.id.comment_timestamp);
        ImageView profileImageView = convertView.findViewById(R.id.comment_profile_picture); // Add this to your layout

        usernameTextView.setText(comment.getUser() != null
                ? comment.getUser().getUsername()
                : "Unknown User");
        commentTextView.setText(comment.getCommentText() != null
                ? comment.getCommentText()
                : "No comment");
        timestampTextView.setText(comment.getTimestamp() != null
                ? comment.getTimestamp().toString()
                : "Unknown Time");

        // Load profile picture using Glide
        if (comment.getUser() != null
                && comment.getUser().getProfilePictureLink() != null
                && !comment.getUser().getProfilePictureLink().isEmpty()) {
            ImageHandler.loadCircularImage(context, comment.getUser().getProfilePictureLink(), profileImageView);
        } else {
            profileImageView.setImageResource(R.drawable.profile_placeholder); // A default profile picture
        }

        // Make the username clickable
        usernameTextView.setOnClickListener(v -> {
            Intent intent = new Intent(context, UserProfileFragment.class);
            intent.putExtra("userId", comment.getUserId());  // Pass user ID to profile page
            context.startActivity(intent);
        });

        return convertView;
    }
}
