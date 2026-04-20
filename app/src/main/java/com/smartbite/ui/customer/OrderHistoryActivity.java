package com.smartbite.ui.customer;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.smartbite.R;
import com.smartbite.adapters.OrderHistoryAdapter;
import com.smartbite.database.DatabaseHelper;
import com.smartbite.database.OrderDAO;
import com.smartbite.models.Order;
import com.smartbite.utils.Constants;

import java.util.ArrayList;
import java.util.List;

public class OrderHistoryActivity extends AppCompatActivity {

    private RecyclerView ordersRecyclerView;
    private OrderHistoryAdapter adapter;
    private OrderDAO orderDAO;
    private List<Order> orderList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_history);

        // Setup toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        // Initialize DAO
        orderDAO = new OrderDAO(DatabaseHelper.getInstance(this));

        // Initialize RecyclerView
        ordersRecyclerView = findViewById(R.id.ordersRecyclerView);
        ordersRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new OrderHistoryAdapter(orderList);
        ordersRecyclerView.setAdapter(adapter);

        loadOrderHistory();
    }

    private void loadOrderHistory() {
        SharedPreferences prefs = getSharedPreferences(Constants.PREF_NAME, MODE_PRIVATE);
        long customerId = prefs.getLong(Constants.KEY_USER_ID, -1);

        if (customerId != -1) {
            orderList = orderDAO.getCustomerOrders((int) customerId);
            adapter.setOrders(orderList);
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
    protected void onResume() {
        super.onResume();
        loadOrderHistory();
    }
}