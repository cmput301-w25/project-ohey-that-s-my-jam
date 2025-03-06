package com.otmj.otmjapp.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.google.android.material.snackbar.Snackbar;
import com.otmj.otmjapp.Helper.UserManager;
import com.otmj.otmjapp.Models.DatabaseObject;
import com.otmj.otmjapp.Models.User;
import com.otmj.otmjapp.R;
import com.otmj.otmjapp.databinding.FragmentLoginBinding;

import java.util.ArrayList;

public class LoginFragment extends Fragment {

    private FragmentLoginBinding binding;

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        binding = FragmentLoginBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.loginButton.setOnClickListener(this::login);
        binding.loginCreateAccount.setOnClickListener(v ->
                NavHostFragment.findNavController(LoginFragment.this)
                        .navigate(R.id.action_loginToSignup));
    }

    private boolean validateFields() {
        EditText username = binding.loginEditUsername,
                password = binding.loginEditPassword;
        String usernameText = username.toString(),
                passwordText = password.toString();

        if (usernameText.isBlank()) {
            username.setError("This field cannot be blank");
            return false;
        } else if (passwordText.isBlank()) {
            password.setError("This filed cannot be blank");
            return false;
        }

        return true;
    }

    private void login(View view) {
        if (!validateFields()) {
            return;
        }

        String usernameText = binding.loginEditUsername.toString(),
                passwordText = binding.loginEditPassword.toString();

        setButtonStatus(true);

        UserManager userManager = UserManager.getInstance();
        userManager.login(usernameText, passwordText, new UserManager.AuthenticationCallback() {
            @Override
            public void onAuthenticated(ArrayList<DatabaseObject<User>> authenticatedUsers) {
                NavHostFragment.findNavController(LoginFragment.this)
                        .navigate(R.id.action_loginSuccess);
            }

            @Override
            public void onAuthenticationFailure(String reason) {
                Snackbar.make(view, reason, Snackbar.LENGTH_LONG)
                        .show();
                setButtonStatus(false);
            }
        });
    }

    private void setButtonStatus(boolean loggingIn) {
        binding.loginButton.setEnabled(!loggingIn);
        binding.loginButton.setText(loggingIn
                ? R.string.logging_in
                : R.string.login);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}