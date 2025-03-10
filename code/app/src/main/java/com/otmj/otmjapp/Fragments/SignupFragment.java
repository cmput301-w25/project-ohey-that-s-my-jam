package com.otmj.otmjapp.Fragments;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.snackbar.Snackbar;
import com.otmj.otmjapp.Helper.UserManager;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.otmj.otmjapp.MainActivity;
import com.otmj.otmjapp.Models.DatabaseObject;
import com.otmj.otmjapp.Models.User;
import com.otmj.otmjapp.R;
import com.otmj.otmjapp.databinding.FragmentSignupBinding;

import java.util.ArrayList;

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

        binding.signupButton.setOnClickListener(this::signup);
        binding.signupLogin.setOnClickListener(v ->
                NavHostFragment.findNavController(SignupFragment.this)
                        .navigate(R.id.action_signupToLogin));
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
        } else if (emailText.isBlank()) {
            email.setError("This field cannot be blank");
            return false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(emailText).matches()) {
            email.setError("Invalid email address");
            return false;
        } else if (passwordText.isBlank()) {
            password.setError("This field cannot be blank");
            return false;
        } else if (passwordText.length() < 8) {
            password.setError("Password must be at least 8 characters long");
            return false;
        }

        return true;
    }

    private void signup(View view) {
        if (!validateFields()) {
            return;
        }

        String username = binding.signupEditUsername.getText().toString(),
                email = binding.signupEditEmail.getText().toString(),
                password = binding.signupEditPassword.getText().toString();

        // Disable button while process is ongoing
        setButtonStatus(true);

        User newUser = new User(username, email, password, null);

        UserManager userManager = UserManager.getInstance();
        userManager.signup(newUser, new UserManager.AuthenticationCallback() {
            @Override
            public void onAuthenticated(ArrayList<User> authenticatedUsers) {
                NavHostFragment.findNavController(SignupFragment.this)
                        .navigate(R.id.action_registerSucess);
            }

            @Override
            public void onAuthenticationFailure(String reason) {
                Snackbar.make(view, reason, Snackbar.LENGTH_LONG)
                        .show();
                setButtonStatus(false);
            }
        });
    }

    private void setButtonStatus(boolean signingUp) {
        binding.signupButton.setEnabled(!signingUp);
        binding.signupButton.setText(signingUp
                ? R.string.signing_up
                : R.string.signup);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}