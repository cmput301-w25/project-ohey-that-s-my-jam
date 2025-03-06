package com.otmj.otmjapp.Fragments;

import android.os.Bundle;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.otmj.otmjapp.R;
import com.otmj.otmjapp.databinding.FragmentSignupBinding;

public class SignupFragment extends Fragment {

    private FragmentSignupBinding binding;

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {

        binding = FragmentSignupBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.signupButton.setOnClickListener(v -> signup());
    }

    private boolean validateFields() {
        EditText username = binding.signupEditUsername,
                email = binding.signupEditEmail,
                password =  binding.signupEditPassword;
        // Ensure all fields are filled properly

        String usernameText = username.getText().toString(),
                emailText = email.getText().toString(),
                passwordText = password.getText().toString();

        if (usernameText.isBlank()) {
            username.setError("This field cannot be blank");
            return false;
        } else if (usernameText.length() < 3) {
            username.setError("Username must be at least 3 characters long");
            return false;
        } else if (passwordText.isBlank()) {
            password.setError("This filed cannot be blank");
            return false;
        } else if (passwordText.length() < 8) {
            username.setError("Password must be at least 8 characters long");
            return false;
        } else if (emailText.isBlank()) {
            email.setError("This field cannot be blank");
            return false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(emailText).matches()) {
            email.setError("Invalid email address");
            return false;
        }

        return true;
    }

    private void signup() {
        if (!validateFields()) {
            return;
        }


    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}