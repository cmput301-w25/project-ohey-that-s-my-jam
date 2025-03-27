package com.otmj.otmjapp.Models;

import android.location.Location;

/**
 * Represents a location with latitude and longitude values.
 * Provides methods to convert the location into a `Location` object.
 */
public class SimpleLocation {
    private double latitude;
    private double longitude;

    public SimpleLocation(){}

    /**
     * Constructs a SimpleLocation with given latitude and longitude.
     *
     * @param latitude The latitude of location as a double.
     * @param longitude The longitude of location as a double.
     */
    public SimpleLocation(double latitude, double longitude){
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    /**
     * Converts a SimpleLocation object to a Location object.
     *
     * @return A Location object containing the latitude and longitude of this SimpleLocation.
     */
    public Location toLocation(){
        Location temp_location = new Location("custom_provider");
        temp_location.setLatitude(this.latitude);
        temp_location.setLongitude(this.getLongitude());
        return temp_location;

    }

    /**
     * Returns a SimpleLocation.
     *
     * @return The SimpleLocation as a string.
     */
    @Override
    public String toString() {
        return "SimpleLocation{" +
                "latitude=" + latitude +
                ", longitude=" + longitude +
                '}';
    }
}

