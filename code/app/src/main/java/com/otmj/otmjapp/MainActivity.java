package com.otmj.otmjapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKeys;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.otmj.otmjapp.API.Auth.SpotifyAPIManager;
import com.otmj.otmjapp.Fragments.MoodEventAddEditDialogFragment;
import com.otmj.otmjapp.databinding.ActivityMainBinding;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration appBarConfiguration;
    private ActivityMainBinding binding;
    private static SharedPreferences sharedPrefs;
    private static boolean authFlowStarted;

    // Create list of followers
    //private ArrayList<Follow> FollowersList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initializeSharedPrefs();

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);

        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment_content_main);
        NavController navController = navHostFragment.getNavController();

        appBarConfiguration = new AppBarConfiguration.Builder(navController.getGraph()).build();
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);

        navController.addOnDestinationChangedListener((controller, destination, arguments) -> {
            if(getSupportActionBar() != null) {
                getSupportActionBar().setTitle("");
            }
        });

        BottomNavigationView bottomNavigationView = binding.bottomNavigation;
        NavigationUI.setupWithNavController(bottomNavigationView, navController);

        binding.addMoodEventButton.setOnClickListener(v -> {
            new MoodEventAddEditDialogFragment().show(getSupportFragmentManager(), null);
        });
    }

    //@Override
    //public boolean onCreateOptionsMenu(Menu menu){
    //    getMenuInflater().inflate(R.menu.people_you_may_know_menu, menu);

    //    MenuItem menuItem = menu.findItem(R.id.action_search);

    //    SearchView searchView = (SearchView) menuItem.getActionView();
    //    searchView.setQueryHint("Search for a User");

    //    searchView.setOnQuery

    //    return super.onCreateOptionsMenu(menu);

    //}

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, appBarConfiguration)
                || super.onSupportNavigateUp();
    }

    @Override
    public void onNewIntent(@NonNull Intent intent) {
        super.onNewIntent(intent);
        authFlowStarted = true;

        Log.d("MainActivity", "Intent received: " + intent.toString());
        Log.d("MainActivityAdress", "Activity address: " + this.toString());

        setIntent(intent);
        handleLoginResponse(intent);
    }

    private void handleLoginResponse(Intent intent) {
        String authCode = Objects.requireNonNull(intent.getData()).getQueryParameter("code");

        SpotifyAPIManager authManager = new SpotifyAPIManager(this);
        authManager.getAccessToken(authCode);
    }

    private void initializeSharedPrefs() { //TODO: maybe move to SharedPreferencesHelper to make it singleton
        try {
            String masterKeyAlias = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC);

            sharedPrefs = EncryptedSharedPreferences.create(
                    "secret_shared_prefs",
                    masterKeyAlias,
                    this,
                    EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                    EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            );
        } catch (GeneralSecurityException | IOException e) {
            Log.e("SharedPreferencesHelper", "Error creating EncryptedSharedPreferences", e);
        }
    }

    public static SharedPreferences getSharedPrefs() {
        return sharedPrefs;
    }

    public static boolean authFlowStarted() {
        return authFlowStarted;
    }

    public static void setAuthFlowStarted(boolean value) {
        authFlowStarted = value;
    }
}
