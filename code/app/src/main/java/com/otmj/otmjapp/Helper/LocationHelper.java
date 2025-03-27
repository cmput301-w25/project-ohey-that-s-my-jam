package com.otmj.otmjapp.Helper;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.util.Log;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.core.app.ActivityCompat;


import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class LocationHelper {
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1001;
    private final Activity activity;
    private final FusedLocationProviderClient fusedLocationClient;

    public interface LocationCallback {
        void onLocationResult(Location location);
        void onLocationError(String error);
    }

    public interface AddressCallback {
        void onAddressResult(String country, String state, String city);
        void onAddressError(String error);
    }

    public LocationHelper(Activity activity) {
        this.activity = activity;
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(activity);
    }

    private void requestLocationPermission() {
        ActivityCompat.requestPermissions(activity,
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                LOCATION_PERMISSION_REQUEST_CODE);
    }

    public void getCurrentLocation(LocationCallback callback) {
        if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_COARSE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {
            requestLocationPermission();
            callback.onLocationError("Location Permission denied");
            return;
        }
        fusedLocationClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, null)
                .addOnSuccessListener(location -> {
                    if (location != null) {
                        callback.onLocationResult(location);
                    } else {
                        callback.onLocationError("Location is null");
                    }
                })
                .addOnFailureListener(e -> callback.onLocationError(e.getMessage()));
    }

    public void getAddressFromLocation(Location location, AddressCallback callback) {
        new Thread(() -> {
            Geocoder geocoder = new Geocoder(activity, Locale.getDefault());
            try {
                List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                if (addresses != null && !addresses.isEmpty()) {
                    Address address = addresses.get(0);
                    String country = address.getCountryName();
                    String state = address.getAdminArea();
                    String city = address.getLocality();

                    // Ensure callback is called on the main thread
                    activity.runOnUiThread(() -> callback.onAddressResult(country, state, city));
                } else {
                    activity.runOnUiThread(() -> callback.onAddressError("No address found"));
                }
            } catch (IOException e) {
                e.printStackTrace();
                activity.runOnUiThread(() -> callback.onAddressError(e.getMessage()));
            }
        }).start();
    }
}
