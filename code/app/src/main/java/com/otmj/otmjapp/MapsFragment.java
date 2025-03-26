package com.otmj.otmjapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;

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
import com.otmj.otmjapp.Helper.MoodHistoryFilter;
import com.otmj.otmjapp.Helper.UserManager;
import com.otmj.otmjapp.Models.EmotionalState;
import com.otmj.otmjapp.Models.MoodEvent;
import com.otmj.otmjapp.Models.SimpleLocation;
import com.otmj.otmjapp.Models.SocialSituation;
import com.otmj.otmjapp.Models.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MapsFragment extends Fragment {

    private UserManager userManager;
    private User user;
    private MoodEventsManager moodEventsManager;
    private ArrayList<MoodEvent> moodEventList;
    private FollowHandler followHandler;
    private ArrayList<Marker> markerList;
    private GoogleMap gMap;
    private Location currentLocation;

    private OnMapReadyCallback callback = new OnMapReadyCallback() {

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
        public void onMapReady(GoogleMap googleMap) {
            //LatLng sydney = new LatLng(-34, 151);
            //googleMap.addMarker(new MarkerOptions().position(sydney)
            //        .icon(bitmapDescriptorFromVector(getContext(),R.drawable.anger))
            //        .title("This is testing"));
            gMap = googleMap;
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(53.52074251736023, -113.51185378614043), 14.0f));
//            if (markerOptionsList != null) {
//                for (MarkerOptions markerOption : markerOptionsList) {
//                    Marker marker = gMap.addMarker(markerOption);
//                    markerList.add(marker);
//                }
//                gMap.moveCamera(CameraUpdateFactory.newLatLngZoom(markerOptionsList.get(0).getPosition(), 15.0f));
//            }
//            else {
//
//            }
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
        getCurrentLocation();
        markerList = new ArrayList<Marker>();
        userManager = UserManager.getInstance();
        user = userManager.getCurrentUser();
        followHandler = new FollowHandler();
        // usersList doesn't include current user id
        ArrayList<String> usersList = new ArrayList<String>();
        followHandler.fetchFollowing(user.getID(), new FollowHandler.FollowCallback() {
            @Override
            public void onSuccess(ArrayList<User> followList) {
                for (User followUser : followList) {
                    usersList.add(followUser.getID());
                }
            }

            @Override
            public void onFailure(Exception e) {
                Log.e("Follow", "Map cannot get all following users");
            }
        });
        //getting filter button
        ImageButton filterButton = view.findViewById(R.id.filter_button);


        // dummy data
        /*
        MoodEvent moodEvent_1 = new MoodEvent(user.getUsername(), EmotionalState.Anger, SocialSituation.Alone, true, "This is Testing", null, MoodEvent.Privacy.Private);
        moodEvent_1.setLocation(new SimpleLocation(53.5241094844906, -113.53191824188693));
        MoodEvent moodEvent_2 = new MoodEvent(user.getUsername(), EmotionalState.Fear, SocialSituation.Alone, true, "This is Testing", null, MoodEvent.Privacy.Private);
        moodEvent_2.setLocation(new SimpleLocation(53.52385159665378, -113.51762837701048));
        MoodEvent moodEvent_3 = new MoodEvent(user.getUsername(), EmotionalState.Confuse, SocialSituation.Alone, true, "This is Testing", null, MoodEvent.Privacy.Private);
        moodEvent_3.setLocation(new SimpleLocation(53.52157604769606, -113.51280554761468));
        MoodEvent moodEvent_4 = new MoodEvent(user.getUsername(), EmotionalState.Disgust, SocialSituation.Alone, true, "This is Testing", null, MoodEvent.Privacy.Private);
        moodEvent_4.setLocation(new SimpleLocation(53.5195127776507, -113.51747527131539));
        MoodEvent moodEvent_5 = new MoodEvent(user.getUsername(), EmotionalState.Happy, SocialSituation.Alone, true, "This is Testing", null, MoodEvent.Privacy.Private);
        moodEvent_5.setLocation(new SimpleLocation(53.527461883497445, -113.51262692430373));
        MoodEvent moodEvent_6 = new MoodEvent(user.getUsername(), EmotionalState.Sad, SocialSituation.Alone, true, "This is Testing", null, MoodEvent.Privacy.Private);
        moodEvent_6.setLocation(new SimpleLocation(53.51811849081261, -113.51177468252371));
        moodEvents.add(moodEvent_1);
        moodEvents.add(moodEvent_2);
        moodEvents.add(moodEvent_3);
        moodEvents.add(moodEvent_4);
        moodEvents.add(moodEvent_5);
        moodEvents.add(moodEvent_6);
        */
        // dummy data end

//        markerOptionsList = createMapMarkers(moodEventList);
        getMyMoodEvents();

        SupportMapFragment mapFragment =
                (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(callback);
        }

        filterButton.setOnClickListener(v -> {
            PopupMenu popup = new PopupMenu(requireContext(), v);
            popup.getMenuInflater().inflate(R.menu.map_filter_menu, popup.getMenu());
            popup.setOnMenuItemClickListener(item -> {
                int id = item.getItemId();
                if (id == R.id.my_moodevents) {
                    getMyMoodEvents();
                    return true;
                } else if (id == R.id.following_moodevents) {
                    getFollowingMoodEvents(usersList);
                    return true;
                } else if (id == R.id.following_users) {
                    getMostRecentMoodEvents(usersList);
                    return true;
                }
                return false;
            });
            popup.show();
        });
    }

    private BitmapDescriptor bitmapDescriptorFromVector(Context context, int resId) {
        Drawable vectorDrawable = ContextCompat.getDrawable(context, resId);
        if (vectorDrawable == null) {
            return null;
        }
        vectorDrawable.setBounds(0, 0, vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight());
        Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(),
                vectorDrawable.getIntrinsicHeight(),
                Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        vectorDrawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }

    private ArrayList<MarkerOptions> createMapMarkers(ArrayList<MoodEvent> moodEvents){
        if (moodEvents == null){
            return null;
        }
        ArrayList<MarkerOptions> markersList = new ArrayList<MarkerOptions>();
        for (int i = 0; i < moodEvents.size(); i++){
            markersList.add(new MarkerOptions()
                    .position(new LatLng(moodEvents.get(i).getLocation().getLatitude(),moodEvents.get(i).getLocation().getLongitude()))
                    .icon(bitmapDescriptorFromVector(getContext(), moodEvents.get(i).getEmotionalState().getEmoji()))
                    .title(moodEvents.get(i).getUser().getUsername()));
        }
        return markersList;
    }

    private void getMyMoodEvents(){
        ArrayList<String> myId = new ArrayList<String>();
        myId.add(user.getID());
        moodEventsManager = new MoodEventsManager(myId);
        LiveData<ArrayList<MoodEvent>> myMoodEvents = moodEventsManager.getMyMoodEventsWithLocation();
        myMoodEvents.observe(getViewLifecycleOwner(), moodEvents -> {
           if (!moodEvents.isEmpty()) {
               moodEventList = moodEvents;
               updateMap();
           }
        });
    }

    private void getFollowingMoodEvents(ArrayList<String> userList){
        moodEventsManager = new MoodEventsManager(userList);
        LiveData<ArrayList<MoodEvent>> allMoodEvents = moodEventsManager.getMoodEventsWithLocation();
        allMoodEvents.observe(getViewLifecycleOwner(), moodEvents -> {
            Log.d("mapFragment","moodevents: "+moodEvents.size());
            moodEventList.clear();
            if (!moodEvents.isEmpty()) {
                HashMap<String, ArrayList<MoodEvent>> moodEventsPerUser = new HashMap<>();
                for (MoodEvent moodEvent : moodEvents) {
                    // If this is the first time this userID is seen
                    if (!moodEventsPerUser.containsKey(moodEvent.getUserID())) {
                        // Add to map with current mood event
                        moodEventsPerUser.put(moodEvent.getUserID(), new ArrayList<>(List.of(moodEvent)));
                    } else {
                        ArrayList<MoodEvent> userMoodEvents = moodEventsPerUser.get(moodEvent.getUserID());
                        // Only add if we don't have up to 3 mood events for the user
                        if (userMoodEvents != null && userMoodEvents.size() < 3) {
                            userMoodEvents.add(moodEvent);
                        } else {
                            continue;
                        }
                    }
                    // At this point, we're only adding a maximum of 3 mood events per user
                    moodEventList.add(moodEvent);
                }
            }
            updateMap();
        });
    }

    private void getMostRecentMoodEvents(ArrayList<String> userList){
        moodEventsManager = new MoodEventsManager(userList);
        LiveData<ArrayList<MoodEvent>> allMoodEvents = moodEventsManager.getMoodEventsWithLocation();
        allMoodEvents.observe(getViewLifecycleOwner(), moodEvents -> {
            Log.d("mapFragment","moodevents: "+moodEvents.size());
            moodEventList.clear();
            if (!moodEvents.isEmpty()) {
                HashMap<String, ArrayList<MoodEvent>> moodEventsPerUser = new HashMap<>();
                for (MoodEvent moodEvent : moodEvents) {
                    // If this is the first time this userID is seen
                    if (!moodEventsPerUser.containsKey(moodEvent.getUserID())) {
                        // Add to map with current mood event
                        moodEventsPerUser.put(moodEvent.getUserID(), new ArrayList<>(List.of(moodEvent)));
                    } else{
                        continue;
                    }
                    // At this point, we're only adding a maximum of 3 mood events per user
                    moodEventList.add(moodEvent);
                }
                getMoodEventsIn5km();
            }
            updateMap();
        });
    }

    private void getCurrentLocation(){
        LocationHelper locationHelper = new LocationHelper(requireActivity());
        locationHelper.getCurrentLocation(new LocationHelper.LocationCallback() {
            @Override
            public void onLocationResult(Location location) {
                currentLocation = location;
            }

            @Override
            public void onLocationError(String error) {
                Log.e("mapFragmnet","Cannot get location");
            }
        });
    }

    private void getMoodEventsIn5km(){
        ArrayList<MoodEvent> deleteList = new ArrayList<MoodEvent>();
        for (int i = 0; i < moodEventList.size(); i++){
            if(currentLocation.distanceTo(moodEventList.get(i).getLocation().toLocation()) > 5000){
                Log.d("mapfragment","moodEvent over: " + i);
                deleteList.add(moodEventList.get(i));
            }
        }
        for (MoodEvent m: deleteList) {
            moodEventList.remove(m);
        }
    }


    private void updateMap(){
        for (Marker marker : markerList) {
            marker.remove();
        }
        markerList.clear();
        if(!moodEventList.isEmpty()) {
            ArrayList<MarkerOptions> markerOptions = createMapMarkers(moodEventList);

            for (MarkerOptions markerOption : markerOptions) {
                Marker marker = gMap.addMarker(markerOption);
                markerList.add(marker);
            }
            gMap.moveCamera(CameraUpdateFactory.newLatLngZoom(markerOptions.get(0).getPosition(), 14.0f));
        }
    }
}