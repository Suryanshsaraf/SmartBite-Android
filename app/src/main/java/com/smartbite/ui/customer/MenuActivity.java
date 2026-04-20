package com.smartbite.ui.customer;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.smartbite.R;
import com.smartbite.adapters.MenuAdapter;
import com.smartbite.database.DatabaseHelper;
import com.smartbite.database.MenuItemDAO;
import com.smartbite.database.RestaurantDAO;
import com.smartbite.models.MenuItem;
import com.smartbite.models.Restaurant;
import com.smartbite.utils.Constants;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

/**
 * MenuActivity displays restaurant menu items
 * Allows adding items to cart
 */
public class MenuActivity extends AppCompatActivity implements MenuAdapter.OnCartUpdateListener {
    private RecyclerView menuList;
    private MenuAdapter adapter;
    private ExtendedFloatingActionButton viewCartFab;
    private int restaurantId;
    private Restaurant restaurant;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        // Get restaurant ID from intent
        restaurantId = getIntent().getIntExtra(Constants.EXTRA_RESTAURANT_ID, -1);
        if (restaurantId == -1) {
            Toast.makeText(this, "Invalid restaurant", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Get restaurant details
        RestaurantDAO restaurantDAO = new RestaurantDAO(DatabaseHelper.getInstance(this));
        restaurant = restaurantDAO.getRestaurantById(restaurantId);
        
        if (restaurant == null) {
            Toast.makeText(this, "Restaurant not found", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Setup toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(restaurant.getName());
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        // Initialize views
        initializeViews();
        loadMenuItems();
    }

    private void initializeViews() {
        menuList = findViewById(R.id.menuList);
        viewCartFab = findViewById(R.id.viewCartFab);

        // Setup RecyclerView
        menuList.setLayoutManager(new LinearLayoutManager(this));
        adapter = new MenuAdapter(this, this);
        menuList.setAdapter(adapter);

        // Setup cart button
        viewCartFab.setOnClickListener(v -> {
            if (adapter.getCartItems().isEmpty()) {
                Toast.makeText(this, "Cart is empty", Toast.LENGTH_SHORT).show();
                return;
            }
            Intent intent = new Intent(this, CartActivity.class);
            intent.putExtra(Constants.EXTRA_RESTAURANT_ID, restaurantId);
            intent.putExtra(Constants.EXTRA_CART_ITEMS, (Serializable) adapter.getCartItems());
            startActivity(intent);
        });
    }

    private void loadMenuItems() {
        MenuItemDAO menuItemDAO = new MenuItemDAO(DatabaseHelper.getInstance(this));
        List<MenuItem> items = menuItemDAO.getMenuItemsByRestaurant(restaurantId);
        adapter.setMenuItems(items);
    }

    @Override
    public void onCartUpdated(int totalItems, double totalAmount) {
        if (totalItems > 0) {
            viewCartFab.setText(String.format(Locale.getDefault(), 
                "View Cart (%d) • ₹%.2f", totalItems, totalAmount));
        } else {
            viewCartFab.setText("View Cart (0)");
        }
    }

    @Override
    public boolean onOptionsItemSelected(android.view.MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}