package com.smartbite.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.smartbite.R;
import com.smartbite.models.Order;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;
import java.util.Locale;

public class OrderHistoryAdapter extends RecyclerView.Adapter<OrderHistoryAdapter.OrderViewHolder> {

    private List<Order> orders;

    public OrderHistoryAdapter(List<Order> orders) {
        this.orders = orders;
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_order_history, parent, false);
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

        private TextView orderIdText;
        private TextView orderStatusText;
        private TextView orderItemsText;
        private TextView totalPriceText;
        private TextView pickupMessageText;

        public OrderViewHolder(@NonNull View itemView) {
            super(itemView);
            orderIdText = itemView.findViewById(R.id.orderIdText);
            orderStatusText = itemView.findViewById(R.id.orderStatusText);
            orderItemsText = itemView.findViewById(R.id.orderItemsText);
            totalPriceText = itemView.findViewById(R.id.totalPriceText);
            pickupMessageText = itemView.findViewById(R.id.pickupMessageText);
        }

        public void bind(Order order) {
            orderIdText.setText("Order #" + order.getId());
            orderStatusText.setText("Status: " + order.getDeliveryStatus());
            totalPriceText.setText(String.format(Locale.getDefault(), "Total: ₹%.2f", order.getTotalPrice()));

            // Build the item list string
            StringBuilder itemsBuilder = new StringBuilder();
            try {
                JSONArray itemsArray = new JSONArray(order.getItemList());
                for (int i = 0; i < itemsArray.length(); i++) {
                    JSONObject item = itemsArray.getJSONObject(i);
                    // In a real app, you would fetch the item name from the database
                    itemsBuilder.append("Item ID: ").append(item.getInt("itemId"));
                    itemsBuilder.append(" (x").append(item.getInt("quantity")).append(")\n");
                }
            } catch (JSONException e) {
                e.printStackTrace();
                itemsBuilder.append("Error parsing items");
            }
            orderItemsText.setText(itemsBuilder.toString());

            if (order.getPickupMessage() != null && !order.getPickupMessage().isEmpty()) {
                pickupMessageText.setVisibility(View.VISIBLE);
                pickupMessageText.setText(order.getPickupMessage());
            } else {
                pickupMessageText.setVisibility(View.GONE);
            }
        }
    }
}