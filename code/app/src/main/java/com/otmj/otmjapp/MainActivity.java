package com.otmj.otmjapp;

import android.os.Bundle;

import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;

import android.view.View;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.otmj.otmjapp.Models.Follow;
import com.otmj.otmjapp.databinding.ActivityMainBinding;

import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration appBarConfiguration;
    private ActivityMainBinding binding;

    // Create list of followers
    //private ArrayList<Follow> FollowersList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Set Adapter and ListView
        //FollowersListAdapter adapter = new FollowersListAdapter(getApplicationContext(), R.layout.user_block, followersList);

        // Create ListView
        //ListView listview = findViewById(R.id.FollowersListViewAdapter);
        //listview.setAdapter(adapter);

        // Create Custom Adapter...


        // intialize followerslist

        // code for Adapter for followers list  (branch 162)
        //ReyclerView recyclerView = findViewById(R.id.FollowersRecycleViewAdapter);
        // setUpFollowersTestList();
        // Create adapter AFTER you create models (data)

        //FollowersRecyclerViewAdapter adapter = new FollowersRecyclerViewAdapter(this, FollowersTestList);

        //recyclerView.setAdapter(adapter);
        //recyclerView.setLayoutManager(new LinearLayoutManager(this));


        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);

        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment_content_main);
        NavController navController = navHostFragment.getNavController();

        appBarConfiguration = new AppBarConfiguration.Builder(navController.getGraph()).build();
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, appBarConfiguration)
                || super.onSupportNavigateUp();
    }
}