package com.otmj.otmjapp.Models;

public class ImageModel {
    private String imageUrl;

    public ImageModel() { } // Required for Firestore

    public ImageModel(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getImageUrl() {
        return imageUrl;
    }
}

