package com.otmj.otmjapp.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.otmj.otmjapp.R;
import com.otmj.otmjapp.databinding.FragmentSignupBinding;
import com.otmj.otmjapp.databinding.FragmentTimelineBinding;

public class TimelineFragment extends Fragment {

    private FragmentTimelineBinding binding;

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        if (container != null) {
            container.removeAllViews();
            container.clearDisappearingChildren();
        }

        binding = FragmentTimelineBinding.inflate(inflater, container, false);
        return binding.getRoot();

    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}