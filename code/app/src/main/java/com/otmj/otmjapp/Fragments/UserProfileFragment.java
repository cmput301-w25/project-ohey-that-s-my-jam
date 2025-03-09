package com.otmj.otmjapp.Fragments;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.otmj.otmjapp.Helper.UserManager;
import com.otmj.otmjapp.Models.User;
import com.otmj.otmjapp.R;
import com.otmj.otmjapp.databinding.MyProfileBinding;

public class UserProfileFragment extends Fragment {
    private MyProfileBinding binding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout using binding and return the root view.
        binding = MyProfileBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Disable the buttons.
        //binding.followersButton.setClickable(false);
        binding.followingButton.setClickable(false);

        // Get UserID
        UserManager user_manager = UserManager.getInstance();

        // Dummy data
        User user = new User("Kai", "kaiiscool@example.com", "123456", "https://exampleImageLink");
        Bitmap bitmapProfileImage = BitmapFactory.decodeResource(getResources(), R.drawable.dummy_profile_image);
        int numFollowers = 5;
        int numFollowing = 10;

        // Set data to views using binding.
        String placeholder = " ";
        binding.profileImage.setImageBitmap(bitmapProfileImage);
        binding.username.setText(user.getUsername());
        binding.numFollowersTextView.setText(String.valueOf(numFollowers));
        binding.numFollowingTextview.setText(String.valueOf(numFollowing));

        // Connecting Followers Button on Profile Page
        Button followersButton = binding.followersButton;
        followersButton.setVisibility(View.VISIBLE); // Make sure it's visible
        followersButton.setEnabled(true); // Make sure it's clickable
        binding.followersButton.setOnClickListener(v -> {
            // Navigate to the Followers List Fragment
            Navigation.findNavController(v).navigate(R.id.followersListFragment);
        });

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null; // Prevent memory leaks.
    }
}
