package com.otmj.otmjapp.Fragments;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.otmj.otmjapp.Helper.FollowHandler;
import com.otmj.otmjapp.Helper.LocationHelper;
import com.otmj.otmjapp.Helper.MoodEventsManager;
import com.otmj.otmjapp.Helper.UserManager;
import com.otmj.otmjapp.Models.MoodEvent;
import com.otmj.otmjapp.Models.User;
import com.otmj.otmjapp.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

public class MapsFragment extends Fragment {

    private User user;
    private MoodEventsManager moodEventsManager;
    private ArrayList<MoodEvent> moodEventList;
    private ArrayList<Marker> markerList;
    private int currentMarkerIndex = 0;
    private GoogleMap gMap;
    private Location currentLocation;

    private final OnMapReadyCallback onMapReadyCallback = new OnMapReadyCallback() {

        /**
         * Manipulates the map once available.
         * This callback is triggered when the map is ready to be used.
         * This is where we can add markers or lines, add listeners or move the camera.
         * In this case, we just add a marker near Sydney, Australia.
         * If Google Play services is not installed on the device, the user will be prompted to
         * install it inside the SupportMapFragment. This method will only be triggered once the
         * user has installed Google Play services and returned to the app.
         */
        @Override
        public void onMapReady(@NonNull GoogleMap googleMap) {
            gMap = googleMap;
            getCurrentLocation();
        }
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_maps, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        markerList = new ArrayList<>();
        
        UserManager userManager = UserManager.getInstance();
        user = userManager.getCurrentUser();

        // Get prev and next map buttons
        ImageButton prevButton = view.findViewById(R.id.prev_marker_button),
                nextButton = view.findViewById(R.id.next_marker_button);
        prevButton.setOnClickListener(v -> {
            if (!markerList.isEmpty()) {
                currentMarkerIndex--;
                // Wrap around
                if (currentMarkerIndex < 0) {
                    currentMarkerIndex = markerList.size() - 1;
                }
                moveCameraTo(markerList.get(currentMarkerIndex).getPosition());
            }
        });

        nextButton.setOnClickListener(v -> {
            if (!markerList.isEmpty()) {
                currentMarkerIndex++;
                // Wrap around
                if (currentMarkerIndex >= markerList.size()) {
                    currentMarkerIndex = 0;
                }
                moveCameraTo(markerList.get(currentMarkerIndex).getPosition());
            }
        });

        // Get filter button
        ImageButton filterButton = view.findViewById(R.id.filter_button);
        filterButton.setVisibility(View.GONE); // Hide it at first

        ArrayList<String> usersList = new ArrayList<>();
        usersList.add(user.getID());
        new FollowHandler().fetchFollowing(user.getID(), new FollowHandler.FollowCallback() {
            @Override
            public void onSuccess(ArrayList<User> followList) {
                // If the user is following someone, then show filter button
                if (!followList.isEmpty()) {
                    filterButton.setVisibility(View.VISIBLE);
                    for (User followUser : followList) {
                        usersList.add(followUser.getID());
                    }
                }
            }

            @Override
            public void onFailure(Exception e) {
                Log.e("Follow", "Map cannot get all following users");
            }
        });

        getMyMoodEvents();

        SupportMapFragment mapFragment =
                (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(onMapReadyCallback);
        }

        filterButton.setOnClickListener(v -> {
            PopupMenu popup = new PopupMenu(requireContext(), v);
            popup.getMenuInflater().inflate(R.menu.map_filter_menu, popup.getMenu());
            popup.setOnMenuItemClickListener(item -> {
                int id = item.getItemId();
                if (id == R.id.my_mood_events) {
                    getMyMoodEvents();
                    return true;
                } else if (id == R.id.all_mood_events) {
                    getNMostRecentMoodEvents(usersList, 3, false);
                    return true;
                } else if (id == R.id.recent_mood_events) {
                    getNMostRecentMoodEvents(usersList, 1, true);
                    return true;
                }
                return false;
            });
            popup.show();
        });
    }

    /**
     * Given an ID to some drawable, get its bitmap description
     * @param context   Context from which to get drawable
     * @param resId     Drawable ID
     * @return          A description that defines a bitmap image
     */
    private BitmapDescriptor bitmapDescriptorFromVector(Context context, int resId) {
        // Get drawable from id
        Drawable vectorDrawable = ContextCompat.getDrawable(context, resId);
        if (vectorDrawable == null) {
            return null;
        }
        vectorDrawable.setBounds(0, 0, vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight());
        // Create a blank bitmap that matches the width and height of the drawable
        Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(),
                vectorDrawable.getIntrinsicHeight(),
                Bitmap.Config.ARGB_8888);
        // Create canvas that will be used to draw on
        Canvas canvas = new Canvas(bitmap);
        // Draw drawable onto bitmap using canvas
        vectorDrawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }

    /**
     * Create list of {@link MarkerOptions} from location, emoji and username of mood events
     * @param moodEvents    List of {@link MoodEvent}
     * @return              List of corresponding marker options from each mood event
     */
    private ArrayList<MarkerOptions> createMapMarkers(ArrayList<MoodEvent> moodEvents) {
        if (moodEvents == null) {
            return new ArrayList<>();
        }

        ArrayList<MarkerOptions> markersList = new ArrayList<>();
        for (int i = 0; i < moodEvents.size(); i++) {
            markersList.add(new MarkerOptions()
                    .position(getNoisyPosition(moodEvents.get(i)))
                    .icon(bitmapDescriptorFromVector(getContext(), moodEvents.get(i).getEmotionalState().getEmoji()))
                    .title(moodEvents.get(i).getUser().getUsername()));
        }
        return markersList;
    }

    /**
     * Add noise to the location of a {@link MoodEvent} so that mood events in the same location
     * will not be on top of each other
     * @param moodEvent     Mood event
     * @return              Noise-infused Latitude-Longitude of location of mood event
     */
    @NonNull
    private static LatLng getNoisyPosition(MoodEvent moodEvent) {
        Random random = new Random();

        // Add noise from (-2*2000, 2*2000) to both lat and long,
        // so that mood events in the same location will not overlap
        double noise1 = random.nextGaussian() / 2000,
                noise2 = random.nextGaussian() / 2000;

        // Add separate noise to lat and long
        return new LatLng(
                moodEvent.getLocation().getLatitude() + noise1,
                moodEvent.getLocation().getLongitude() + noise2
        );
    }

    /**
     * Get at most n mood events from each user
     * @param userIDs       Users to get mood events from
     * @param n             Maximum number of mood events to get for each user
     * @param within5KM     Whether to filter out mood events that are more than 5 KM
     */
    private void getNMostRecentMoodEvents(ArrayList<String> userIDs, int n, boolean within5KM) {
        if (userIDs.isEmpty() || n <= 0) {
            return;
        }

        moodEventsManager = new MoodEventsManager(userIDs);
        // What to do after mood events with location are gotten:
        moodEventsManager.getMoodEventsWithLocation().observe(
                getViewLifecycleOwner(),
                moodEvents -> {
                        Log.d("mapFragment","moodevents: " + moodEvents.size());
                        moodEventList.clear();
                        if (!moodEvents.isEmpty()) {
                            HashMap<String, ArrayList<MoodEvent>> moodEventsPerUser = new HashMap<>();
                            for (MoodEvent moodEvent : moodEvents) {
                                // If this is the first time this userID is seen
                                if (!moodEventsPerUser.containsKey(moodEvent.getUserID())) {
                                    // Add to map with current mood event
                                    moodEventsPerUser.put(
                                            moodEvent.getUserID(),
                                            new ArrayList<>(List.of(moodEvent))
                                    );
                                } else {
                                    ArrayList<MoodEvent> userMoodEvents =
                                        moodEventsPerUser.get(moodEvent.getUserID());
                                    // Only add if we don't have up to n mood events for the user
                                    if (userMoodEvents != null && userMoodEvents.size() < n) {
                                        userMoodEvents.add(moodEvent);
                                    } else {
                                        continue;
                                    }
                                }
                                // At this point, we're only adding a maximum of n mood events per user
                                moodEventList.add(moodEvent);
                            }
                        }
                    if (within5KM) {
                        getMoodEventsIn5km();
                    }
                    updateMap();
                }
        );
    }

    /**
     * Get only current user's mood events
     */
    private void getMyMoodEvents() {
        moodEventsManager = new MoodEventsManager(List.of(user.getID()));
        moodEventsManager.getMyMoodEventsWithLocation().observe(
            getViewLifecycleOwner(),
            moodEvents -> {
                if (!moodEvents.isEmpty()) {
                    moodEventList = moodEvents;
                    updateMap();
                }
        });
    }

    /**
     * Delete mood events that are farther than 5 KM from current location
     */
    private void getMoodEventsIn5km() {
        if (currentLocation == null) {
            Toast.makeText(getContext(), "Unable to get location.", Toast.LENGTH_LONG).show();
            return;
        }

        int i = 0;
        // Go through all mood events
        while (i < moodEventList.size()) {
            // If it's farther than 5*10^3 m away
            if (currentLocation.distanceTo(moodEventList.get(i).getLocation().toLocation()) > 5000) {
                moodEventList.remove(i);
            } else {
                i++;
            }
        }
        
        // If there are no mood events that are close, show message
        if (moodEventList.isEmpty()) {
            Toast.makeText(getContext(), "No mood events nearby", Toast.LENGTH_LONG)
                    .show();
        }
    }

    /**
     * Get user's current location using {@link LocationHelper} utility class
     */
    private void getCurrentLocation() {
        LocationHelper locationHelper = new LocationHelper(requireActivity());
        locationHelper.getCurrentLocation(new LocationHelper.LocationCallback() {
            @Override
            public void onLocationResult(Location location) {
                currentLocation = location;
                Log.d("MapFragment", "I got the location");
            }

            @Override
            public void onLocationError(String error) {
                Log.e("MapFragment","Cannot get location");
            }
        });
    }

    /**
     * Move Google Map to centre at specified position
     * @param position Position to centre at
     */
    private void moveCameraTo(LatLng position) {
        gMap.moveCamera(CameraUpdateFactory.newLatLngZoom(position, 16.0f));
    }

    /**
     * Clear old marker and set new ones
     */
    private void updateMap() {
        for (Marker marker : markerList) {
            marker.remove();
        }
        currentMarkerIndex = 0; // Reset marker index
        markerList.clear();

        if (!moodEventList.isEmpty()) {
            ArrayList<MarkerOptions> markerOptions = createMapMarkers(moodEventList);
            for (MarkerOptions markerOption : markerOptions) {
                Marker marker = gMap.addMarker(markerOption);
                markerList.add(marker);
            }
            // Go to first mood event
            moveCameraTo(markerOptions.get(0).getPosition());
        }
    }
}
