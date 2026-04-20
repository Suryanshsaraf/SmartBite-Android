package com.smartbite.ui.delivery;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.material.button.MaterialButton;
import com.smartbite.R;
import com.smartbite.database.DatabaseHelper;
import com.smartbite.database.OrderDAO;
import com.smartbite.database.RestaurantDAO;
import com.smartbite.models.Order;
import com.smartbite.models.Restaurant;
import com.smartbite.utils.Constants;
import com.smartbite.utils.LocationHelper;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class MapTrackingActivity extends AppCompatActivity implements
        LocationHelper.LocationUpdateListener {

    private GoogleMap map;
    private LocationHelper locationHelper;
    private TextView distanceText;
    private TextView estimatedTimeText;
    private MaterialButton startNavigationButton;
    private Order order;
    private Restaurant restaurant;
    private Location currentLocation;
    private String deliveryAddress;
    private static final int LOCATION_PERMISSION_REQUEST = 1000;
    private static final String TAG = "MapTrackingActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_tracking);

        int orderId = getIntent().getIntExtra(Constants.EXTRA_ORDER_ID, -1);
        deliveryAddress = getIntent().getStringExtra(Constants.EXTRA_DELIVERY_ADDRESS);
        if (orderId == -1) {
            Toast.makeText(this, "Invalid order", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        DatabaseHelper dbHelper = DatabaseHelper.getInstance(this);
        OrderDAO orderDAO = new OrderDAO(dbHelper);
        RestaurantDAO restaurantDAO = new RestaurantDAO(dbHelper);

        order = orderDAO.getOrderById(orderId);
        if (order == null) {
            Toast.makeText(this, "Order not found", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        restaurant = restaurantDAO.getRestaurantById(order.getRestaurantId());
        if (restaurant == null) {
            Log.e(TAG, "Restaurant not found for ID: " + order.getRestaurantId());
            Toast.makeText(this, "Restaurant not found", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Delivery Route");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        initializeViews();
        locationHelper = LocationHelper.getInstance(this);

        if (checkLocationPermission()) {
            setupMap();
        } else {
            requestLocationPermission();
        }
    }

    private void initializeViews() {
        distanceText = findViewById(R.id.distanceText);
        estimatedTimeText = findViewById(R.id.estimatedTimeText);
        startNavigationButton = findViewById(R.id.startNavigationButton);

        startNavigationButton.setOnClickListener(v -> startNavigation());
    }

    private void setupMap() {
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(googleMap -> {
                map = googleMap;
                if (ActivityCompat.checkSelfPermission(this,
                        Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    map.setMyLocationEnabled(true);
                    locationHelper.startLocationUpdates(this, this);
                }
            });
        }
    }

    private boolean checkLocationPermission() {
        return ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    private void requestLocationPermission() {
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                LOCATION_PERMISSION_REQUEST);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                         @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                setupMap();
            } else {
                Toast.makeText(this, "Location permission required", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    @Override
    public void onLocationUpdated(Location location) {
        currentLocation = location;
        updateMap();
    }

    private void updateMap() {
        if (map == null || currentLocation == null || restaurant == null) {
            Log.w(TAG, "updateMap: map, currentLocation, or restaurant is null");
            return;
        }

        LatLng currentLatLng = new LatLng(currentLocation.getLatitude(),
                currentLocation.getLongitude());
        LatLng restaurantLatLng = new LatLng(restaurant.getLatitude(),
                restaurant.getLongitude());

        map.clear();

        map.addMarker(new MarkerOptions()
                .position(currentLatLng)
                .title("Your Location"));

        map.addMarker(new MarkerOptions()
                .position(restaurantLatLng)
                .title(restaurant.getName())
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));

        // Add marker for delivery address
        if (deliveryAddress != null && !deliveryAddress.isEmpty()) {
            try {
                Geocoder geocoder = new Geocoder(this, Locale.getDefault());
                List<Address> addresses = geocoder.getFromLocationName(deliveryAddress, 1);
                if (addresses != null && !addresses.isEmpty()) {
                    Address address = addresses.get(0);
                    LatLng deliveryLatLng = new LatLng(address.getLatitude(), address.getLongitude());
                    map.addMarker(new MarkerOptions()
                            .position(deliveryLatLng)
                            .title("Delivery Address")
                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
                    map.addPolyline(new PolylineOptions()
                            .add(restaurantLatLng, deliveryLatLng)
                            .width(5)
                            .color(Color.GREEN));
                }
            } catch (IOException e) {
                e.printStackTrace();
                Log.e(TAG, "Error geocoding address: " + deliveryAddress);
            }
        }

        map.addPolyline(new PolylineOptions()
                .add(currentLatLng, restaurantLatLng)
                .width(5)
                .color(Color.BLUE));

        map.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, Constants.DEFAULT_ZOOM));

        float distance = LocationHelper.calculateDistance(
                currentLocation.getLatitude(), currentLocation.getLongitude(),
                restaurant.getLatitude(), restaurant.getLongitude());

        distanceText.setText(String.format("Distance: %.1f km", distance));
        estimatedTimeText.setText(String.format("Estimated time: %d mins",
                Math.round(distance * 3)));
    }

    private void startNavigation() {
        if (deliveryAddress == null || deliveryAddress.isEmpty()) {
            Toast.makeText(this, "Delivery address not available", Toast.LENGTH_SHORT).show();
            return;
        }

        Uri gmmIntentUri = Uri.parse("google.navigation:q=" + Uri.encode(deliveryAddress));
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
        mapIntent.setPackage("com.google.android.apps.maps");

        if (mapIntent.resolveActivity(getPackageManager()) != null) {
            startActivity(mapIntent);
        } else {
            Toast.makeText(this, "Google Maps not installed", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (locationHelper != null) {
            locationHelper.stopLocationUpdates();
        }
    }
}