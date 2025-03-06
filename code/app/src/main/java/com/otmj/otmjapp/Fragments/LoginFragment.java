package com.otmj.otmjapp.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

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

        binding.loginButton.setOnClickListener(v -> login());
        binding.loginCreateAccount.setOnClickListener(v ->
                NavHostFragment.findNavController(LoginFragment.this)
                        .navigate(R.id.action_loginToSignup));
    }

    private void login() {
        EditText username = binding.loginEditUsername,
                password = binding.loginEditPassword;
        String usernameText = username.toString(),
                passwordText = password.toString();

        if (usernameText.isBlank()) {
            username.setError("This field cannot be blank");
            return;
        }

        if (passwordText.isBlank()) {
            password.setError("This filed cannot be blank");
            return;
        }

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
                binding.loginWelcomeText.setText(reason);
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