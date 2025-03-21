package com.otmj.otmjapp.Models;

import android.location.Location;

public class SimpleLocation {
    private double latitude;
    private double longitude;

    public SimpleLocation(){}

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

    public Location toLocation(){
        Location temp_location = new Location("custom_provider");
        temp_location.setLatitude(this.latitude);
        temp_location.setLongitude(this.getLongitude());
        return temp_location;

    }

    @Override
    public String toString() {
        return "SimpleLocation{" +
                "latitude=" + latitude +
                ", longitude=" + longitude +
                '}';
    }
}

