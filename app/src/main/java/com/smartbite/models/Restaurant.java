package com.smartbite.models;

/**
 * Restaurant model class containing restaurant details
 */
public class Restaurant {
    private int id;
    private String name;
    private String cuisine;
    private float rating;
    private double latitude;
    private double longitude;

    // Default constructor
    public Restaurant() {}

    // Constructor with all fields
    public Restaurant(int id, String name, String cuisine, float rating, double latitude, double longitude) {
        this.id = id;
        this.name = name;
        this.cuisine = cuisine;
        this.rating = rating;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getCuisine() { return cuisine; }
    public void setCuisine(String cuisine) { this.cuisine = cuisine; }

    public float getRating() { return rating; }
    public void setRating(float rating) { this.rating = rating; }

    public double getLatitude() { return latitude; }
    public void setLatitude(double latitude) { this.latitude = latitude; }

    public double getLongitude() { return longitude; }
    public void setLongitude(double longitude) { this.longitude = longitude; }
}