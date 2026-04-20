package com.smartbite.utils;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Looper;
import androidx.core.app.ActivityCompat;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

/**
 * Helper class for managing location-related functionality
 */
public class LocationHelper {
    private static LocationHelper instance;
    private FusedLocationProviderClient fusedLocationClient;
    private LocationCallback locationCallback;
    private Location lastKnownLocation;
    private LocationUpdateListener listener;

    public interface LocationUpdateListener {
        void onLocationUpdated(Location location);
    }

    private LocationHelper(Context context) {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(context);
    }

    public static synchronized LocationHelper getInstance(Context context) {
        if (instance == null) {
            instance = new LocationHelper(context.getApplicationContext());
        }
        return instance;
    }

    /**
     * Start receiving location updates
     * @param context Application context
     * @param listener Listener for location updates
     */
    public void startLocationUpdates(Context context, LocationUpdateListener listener) {
        this.listener = listener;

        if (ActivityCompat.checkSelfPermission(context,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        LocationRequest locationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(Constants.LOCATION_UPDATE_INTERVAL);

        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) {
                    return;
                }
                for (Location location : locationResult.getLocations()) {
                    lastKnownLocation = location;
                    if (LocationHelper.this.listener != null) {
                        LocationHelper.this.listener.onLocationUpdated(location);
                    }
                }
            }
        };

        fusedLocationClient.requestLocationUpdates(locationRequest,
                locationCallback,
                Looper.getMainLooper());
    }

    /**
     * Stop receiving location updates
     */
    public void stopLocationUpdates() {
        if (fusedLocationClient != null && locationCallback != null) {
            fusedLocationClient.removeLocationUpdates(locationCallback);
        }
    }

    /**
     * Get last known location
     * @return Last known location or null
     */
    public Location getLastKnownLocation() {
        return lastKnownLocation;
    }

    /**
     * Calculate distance between two locations
     * @param lat1 First location latitude
     * @param lon1 First location longitude
     * @param lat2 Second location latitude
     * @param lon2 Second location longitude
     * @return Distance in kilometers
     */
    public static float calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        float[] results = new float[1];
        Location.distanceBetween(lat1, lon1, lat2, lon2, results);
        return results[0] / 1000; // Convert meters to kilometers
    }
}