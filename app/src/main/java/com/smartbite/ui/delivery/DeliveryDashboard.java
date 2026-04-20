package com.smartbite.ui.delivery;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.smartbite.R;
import com.smartbite.adapters.DeliveryOrderAdapter;
import com.smartbite.database.DatabaseHelper;
import com.smartbite.database.OrderDAO;
import com.smartbite.database.UserDAO;
import com.smartbite.models.Order;
import com.smartbite.models.User;
import com.smartbite.ui.login.LoginActivity;
import com.smartbite.ui.profile.ProfileActivity;
import com.smartbite.utils.Constants;

import java.util.List;

/**
 * DeliveryDashboard shows active delivery orders
 * Allows delivery partners to update order status
 */
public class DeliveryDashboard extends AppCompatActivity implements 
        DeliveryOrderAdapter.OnOrderActionListener {
    
    private RecyclerView ordersList;
    private TextView emptyView;
    private SwipeRefreshLayout swipeRefresh;
    private DeliveryOrderAdapter adapter;
    private OrderDAO orderDAO;
    private UserDAO userDAO;
    private int deliveryPartnerId;
    private String deliveryPartnerName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delivery_dashboard);

        // Get delivery partner ID
        SharedPreferences prefs = getSharedPreferences(Constants.PREF_NAME, MODE_PRIVATE);
        deliveryPartnerId = prefs.getInt(Constants.KEY_USER_ID, -1);
        deliveryPartnerName = prefs.getString(Constants.KEY_USERNAME, "");
        if (deliveryPartnerId == -1) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        // Initialize database
        DatabaseHelper dbHelper = DatabaseHelper.getInstance(this);
        orderDAO = new OrderDAO(dbHelper);
        userDAO = new UserDAO(dbHelper);

        // Setup toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        initializeViews();
        setupListeners();
        loadOrders();
    }

    private void initializeViews() {
        ordersList = findViewById(R.id.ordersList);
        emptyView = findViewById(R.id.emptyView);
        swipeRefresh = findViewById(R.id.swipeRefresh);

        ordersList.setLayoutManager(new LinearLayoutManager(this));
        adapter = new DeliveryOrderAdapter(this, this);
        ordersList.setAdapter(adapter);
    }

    private void setupListeners() {
        swipeRefresh.setOnRefreshListener(this::loadOrders);
    }

    private void loadOrders() {
        // Fetch all pending orders that are not assigned to any delivery partner
        List<Order> orders = orderDAO.getPendingOrders();
        adapter.setOrders(orders);
        updateEmptyView(orders.isEmpty());
        swipeRefresh.setRefreshing(false);
    }

    private void updateEmptyView(boolean isEmpty) {
        ordersList.setVisibility(isEmpty ? View.GONE : View.VISIBLE);
        emptyView.setVisibility(isEmpty ? View.VISIBLE : View.GONE);
    }

    @Override
    public void onViewMap(Order order) {
        Intent intent = new Intent(this, MapTrackingActivity.class);
        intent.putExtra(Constants.EXTRA_ORDER_ID, order.getId());
        intent.putExtra(Constants.EXTRA_DELIVERY_ADDRESS, order.getDeliveryAddress());
        startActivity(intent);
    }

    @Override
    public void onUpdateStatus(Order order, String newStatus) {
        if (newStatus.equals(Constants.STATUS_PICKED_UP)) {
            showPickupMessageDialog(order, newStatus);
        } else {
            orderDAO.updateOrderStatus(order.getId(), newStatus, deliveryPartnerId);
            loadOrders(); // Refresh the list after updating
        }
    }

    private void showPickupMessageDialog(Order order, String newStatus) {
        String message = "Order was successful! Picked up by " + deliveryPartnerName;
        orderDAO.updateOrderStatus(order.getId(), newStatus, deliveryPartnerId);
        orderDAO.updatePickupMessage(order.getId(), message);
        loadOrders();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_delivery, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_profile) {
            startActivity(new Intent(this, ProfileActivity.class));
            return true;
        } else if (id == R.id.action_logout) {
            logout();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void logout() {
        SharedPreferences prefs = getSharedPreferences(Constants.PREF_NAME, MODE_PRIVATE);
        prefs.edit().clear().apply();
        
        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadOrders();
    }
}