package com.smartbite.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.smartbite.R;
import com.smartbite.database.DatabaseHelper;
import com.smartbite.database.UserDAO;
import com.smartbite.models.Order;
import com.smartbite.models.User;
import com.smartbite.utils.Constants;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Adapter for displaying delivery orders in a RecyclerView
 */
public class DeliveryOrderAdapter extends RecyclerView.Adapter<DeliveryOrderAdapter.OrderViewHolder> {
    private Context context;
    private List<Order> orders;
    private OnOrderActionListener listener;
    private UserDAO userDAO;

    public interface OnOrderActionListener {
        void onViewMap(Order order);
        void onUpdateStatus(Order order, String newStatus);
    }

    public DeliveryOrderAdapter(Context context, OnOrderActionListener listener) {
        this.context = context;
        this.orders = new ArrayList<>();
        this.listener = listener;
        this.userDAO = new UserDAO(DatabaseHelper.getInstance(context));
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_delivery_order, parent, false);
        return new OrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        Order order = orders.get(position);
        holder.bind(order);
    }

    @Override
    public int getItemCount() {
        return orders.size();
    }

    public void setOrders(List<Order> orders) {
        this.orders = orders;
        notifyDataSetChanged();
    }

    class OrderViewHolder extends RecyclerView.ViewHolder {
        private TextView customerName;
        private TextView deliveryAddress;
        private TextView orderTotal;
        private MaterialButton viewMapButton;
        private MaterialButton updateStatusButton;

        OrderViewHolder(@NonNull View itemView) {
            super(itemView);
            customerName = itemView.findViewById(R.id.customerName);
            deliveryAddress = itemView.findViewById(R.id.deliveryAddress);
            orderTotal = itemView.findViewById(R.id.orderTotal);
            viewMapButton = itemView.findViewById(R.id.viewMapButton);
            updateStatusButton = itemView.findViewById(R.id.updateStatusButton);
        }

        void bind(Order order) {
            // Get user details
            User customer = userDAO.getUserById(order.getCustomerId());
            if (customer != null) {
                customerName.setText(customer.getUsername());
            }

            deliveryAddress.setText(order.getDeliveryAddress());
            orderTotal.setText(String.format(Locale.getDefault(), "Total: ₹%.2f", order.getTotalPrice()));

            // Update button text based on status
            if (Constants.STATUS_PENDING.equals(order.getDeliveryStatus())) {
                updateStatusButton.setText(R.string.btn_pick_up);
            } else if (Constants.STATUS_PICKED_UP.equals(order.getDeliveryStatus())) {
                updateStatusButton.setText(R.string.btn_deliver);
            } else {
                updateStatusButton.setVisibility(View.GONE);
            }

            // Set click listeners
            viewMapButton.setOnClickListener(v -> listener.onViewMap(order));
            updateStatusButton.setOnClickListener(v -> {
                String newStatus = Constants.STATUS_PENDING.equals(order.getDeliveryStatus()) ?
                    Constants.STATUS_PICKED_UP : Constants.STATUS_DELIVERED;
                listener.onUpdateStatus(order, newStatus);
            });
        }
    }
}