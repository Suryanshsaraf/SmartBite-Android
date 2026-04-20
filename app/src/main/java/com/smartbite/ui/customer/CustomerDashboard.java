package com.smartbite.ui.customer;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.textfield.TextInputEditText;
import com.smartbite.R;
import com.smartbite.adapters.RestaurantAdapter;
import com.smartbite.database.DatabaseHelper;
import com.smartbite.database.RestaurantDAO;
import com.smartbite.models.Restaurant;
import com.smartbite.ui.login.LoginActivity;
import com.smartbite.ui.profile.ProfileActivity;
import com.smartbite.utils.Constants;

import java.util.List;

/**
 * CustomerDashboard displays list of restaurants
 * Handles restaurant search and navigation to menu
 */
public class CustomerDashboard extends AppCompatActivity {
    private RecyclerView restaurantsList;
    private RestaurantAdapter adapter;
    private SwipeRefreshLayout swipeRefresh;
    private TextInputEditText searchInput;
    private RestaurantDAO restaurantDAO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_dashboard);

        // Initialize database
        DatabaseHelper dbHelper = DatabaseHelper.getInstance(this);
        restaurantDAO = new RestaurantDAO(dbHelper);

        // Setup toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Initialize views
        initializeViews();
        setupListeners();
        loadRestaurants();
    }

    private void initializeViews() {
        restaurantsList = findViewById(R.id.restaurantsList);
        swipeRefresh = findViewById(R.id.swipeRefresh);
        searchInput = findViewById(R.id.searchInput);

        // Setup RecyclerView
        restaurantsList.setLayoutManager(new LinearLayoutManager(this));
        adapter = new RestaurantAdapter(this);
        restaurantsList.setAdapter(adapter);

        // Setup Cart FAB
        findViewById(R.id.cartFab).setOnClickListener(v -> {
            startActivity(new Intent(this, CartActivity.class));
        });
    }

    private void setupListeners() {
        // Setup swipe refresh
        swipeRefresh.setOnRefreshListener(this::loadRestaurants);

        // Setup search
        searchInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                adapter.filter(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void loadRestaurants() {
        List<Restaurant> restaurants = restaurantDAO.getAllRestaurants();
        adapter.setRestaurants(restaurants);
        swipeRefresh.setRefreshing(false);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_customer, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_profile) {
            startActivity(new Intent(this, ProfileActivity.class));
            return true;
        } else if (id == R.id.action_orders) {
            startActivity(new Intent(this, OrderHistoryActivity.class));
            return true;
        } else if (id == R.id.action_logout) {
            logout();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void logout() {
        // Clear shared preferences
        SharedPreferences prefs = getSharedPreferences(Constants.PREF_NAME, MODE_PRIVATE);
        prefs.edit().clear().apply();

        // Redirect to login
        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadRestaurants();
    }
}