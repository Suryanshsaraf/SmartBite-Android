package com.smartbite.ui.customer;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.smartbite.R;
import com.smartbite.adapters.CartAdapter;
import com.smartbite.database.DatabaseHelper;
import com.smartbite.database.MenuItemDAO;
import com.smartbite.database.OrderDAO;
import com.smartbite.models.MenuItem;
import com.smartbite.models.Order;
import com.smartbite.utils.Constants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class CartActivity extends AppCompatActivity {
    private RecyclerView cartList;
    private TextView subtotalText;
    private TextView deliveryFeeText;
    private TextView totalText;
    private MaterialButton placeOrderButton;
    private TextInputEditText addressInput;
    private OrderDAO orderDAO;
    private MenuItemDAO menuItemDAO;
    private CartAdapter adapter;
    private Map<Integer, Integer> cartItems;
    private double subtotal = 0;
    private final double DELIVERY_FEE = 40.0; // Fixed delivery fee

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        // Initialize database
        DatabaseHelper dbHelper = DatabaseHelper.getInstance(this);
        orderDAO = new OrderDAO(dbHelper);
        menuItemDAO = new MenuItemDAO(dbHelper);

        // Get cart items from intent
        cartItems = (HashMap<Integer, Integer>) getIntent().getSerializableExtra(Constants.EXTRA_CART_ITEMS);

        // Setup toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        initializeViews();
        setupClickListeners();
        loadCartItems();
    }

    private void initializeViews() {
        cartList = findViewById(R.id.cartList);
        subtotalText = findViewById(R.id.subtotalText);
        deliveryFeeText = findViewById(R.id.deliveryFeeText);
        totalText = findViewById(R.id.totalText);
        placeOrderButton = findViewById(R.id.placeOrderButton);
        addressInput = findViewById(R.id.addressInput);

        cartList.setLayoutManager(new LinearLayoutManager(this));
        adapter = new CartAdapter(new ArrayList<>());
        cartList.setAdapter(adapter);
    }

    private void setupClickListeners() {
        placeOrderButton.setOnClickListener(v -> placeOrder());
    }

    private void loadCartItems() {
        List<MenuItem> items = new ArrayList<>();
        if (cartItems != null) {
            for (Integer itemId : cartItems.keySet()) {
                // This is not efficient, but for this small project it is ok
                // In a real project, you'd want to get all items in one query
                MenuItem item = menuItemDAO.getMenuItemById(itemId);
                if (item != null) {
                    items.add(item);
                }
            }
        }
        adapter.setCartItems(items);
        updateTotals();
    }

    private void updateTotals() {
        subtotal = 0;
        if (cartItems != null) {
            for (Map.Entry<Integer, Integer> entry : cartItems.entrySet()) {
                MenuItem item = menuItemDAO.getMenuItemById(entry.getKey());
                if (item != null) {
                    subtotal += item.getPrice() * entry.getValue();
                }
            }
        }

        subtotalText.setText(String.format(Locale.getDefault(), "₹%.2f", subtotal));
        deliveryFeeText.setText(String.format(Locale.getDefault(), "₹%.2f", DELIVERY_FEE));
        double total = subtotal + DELIVERY_FEE;
        totalText.setText(String.format(Locale.getDefault(), "₹%.2f", total));
    }

    private void placeOrder() {
        String address = addressInput.getText().toString().trim();
        if (address.isEmpty()) {
            addressInput.setError("Address cannot be empty");
            return;
        }

        int restaurantId = getIntent().getIntExtra(Constants.EXTRA_RESTAURANT_ID, -1);
        if (restaurantId == -1) {
            Toast.makeText(this, "Invalid restaurant", Toast.LENGTH_SHORT).show();
            return;
        }

        // Get user ID from shared preferences
        SharedPreferences prefs = getSharedPreferences(Constants.PREF_NAME, MODE_PRIVATE);
        int userId = prefs.getInt(Constants.KEY_USER_ID, -1);
        if (userId == -1) {
            Toast.makeText(this, "Please login again", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            // Create JSON array of items
            JSONArray itemsArray = new JSONArray();
            if (cartItems != null) {
                for (Map.Entry<Integer, Integer> entry : cartItems.entrySet()) {
                    JSONObject item = new JSONObject();
                    item.put("itemId", entry.getKey());
                    item.put("quantity", entry.getValue());
                    itemsArray.put(item);
                }
            }

            // Create order
            Order order = new Order();
            order.setCustomerId(userId);
            order.setRestaurantId(restaurantId);
            order.setItemList(itemsArray.toString());
            order.setTotalPrice(subtotal + DELIVERY_FEE);
            order.setDeliveryStatus(Constants.STATUS_PENDING);
            order.setDeliveryAddress(address);

            // Save order to database
            long orderId = orderDAO.createOrder(order);
            if (orderId > 0) {
                Toast.makeText(this, R.string.msg_order_placed, Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(this, "Failed to place order", Toast.LENGTH_SHORT).show();
            }

        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(this, "Error creating order", Toast.LENGTH_SHORT).show();
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
}