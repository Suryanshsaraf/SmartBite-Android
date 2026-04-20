package com.smartbite.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.smartbite.R;
import com.smartbite.models.MenuItem;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Adapter for displaying menu items in a RecyclerView
 * Handles item quantity updates and cart management
 */
public class MenuAdapter extends RecyclerView.Adapter<MenuAdapter.MenuViewHolder> {
    private Context context;
    private List<MenuItem> menuItems;
    private Map<Integer, Integer> itemQuantities; // ItemId -> Quantity
    private OnCartUpdateListener cartUpdateListener;

    public interface OnCartUpdateListener {
        void onCartUpdated(int totalItems, double totalAmount);
    }

    public MenuAdapter(Context context, OnCartUpdateListener listener) {
        this.context = context;
        this.menuItems = new ArrayList<>();
        this.itemQuantities = new HashMap<>();
        this.cartUpdateListener = listener;
    }

    @NonNull
    @Override
    public MenuViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_menu, parent, false);
        return new MenuViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MenuViewHolder holder, int position) {
        MenuItem item = menuItems.get(position);
        holder.bind(item);
    }

    @Override
    public int getItemCount() {
        return menuItems.size();
    }

    public void setMenuItems(List<MenuItem> items) {
        this.menuItems = items;
        notifyDataSetChanged();
    }

    /**
     * Get current cart items with quantities
     * @return Map of item IDs to quantities
     */
    public Map<Integer, Integer> getCartItems() {
        return new HashMap<>(itemQuantities);
    }

    /**
     * Clear all items from cart
     */
    public void clearCart() {
        itemQuantities.clear();
        notifyDataSetChanged();
        updateCartSummary();
    }

    private void updateCartSummary() {
        int totalItems = 0;
        double totalAmount = 0;
        for (MenuItem item : menuItems) {
            int quantity = itemQuantities.getOrDefault(item.getId(), 0);
            totalItems += quantity;
            totalAmount += quantity * item.getPrice();
        }
        cartUpdateListener.onCartUpdated(totalItems, totalAmount);
    }

    class MenuViewHolder extends RecyclerView.ViewHolder {
        private ImageView itemImage;
        private TextView itemName;
        private TextView itemPrice;
        private TextView quantityText;
        private ImageButton decreaseButton;
        private ImageButton increaseButton;

        MenuViewHolder(@NonNull View itemView) {
            super(itemView);
            itemImage = itemView.findViewById(R.id.itemImage);
            itemName = itemView.findViewById(R.id.itemName);
            itemPrice = itemView.findViewById(R.id.itemPrice);
            quantityText = itemView.findViewById(R.id.quantityText);
            decreaseButton = itemView.findViewById(R.id.decreaseButton);
            increaseButton = itemView.findViewById(R.id.increaseButton);
        }

        void bind(MenuItem item) {
            itemName.setText(item.getItemName());
            itemPrice.setText(String.format(Locale.getDefault(), "₹%.2f", item.getPrice()));
            
            int quantity = itemQuantities.getOrDefault(item.getId(), 0);
            quantityText.setText(String.valueOf(quantity));
            
            // Load item image
            if (item.getImageUrl() != null) {
                Glide.with(context)
                     .load(item.getImageUrl())
                     .centerCrop()
                     .placeholder(android.R.drawable.sym_def_app_icon)
                     .into(itemImage);
            }

            decreaseButton.setOnClickListener(v -> {
                int currentQty = itemQuantities.getOrDefault(item.getId(), 0);
                if (currentQty > 0) {
                    itemQuantities.put(item.getId(), currentQty - 1);
                    if (currentQty - 1 == 0) {
                        itemQuantities.remove(item.getId());
                    }
                    quantityText.setText(String.valueOf(currentQty - 1));
                    updateCartSummary();
                }
            });

            increaseButton.setOnClickListener(v -> {
                int currentQty = itemQuantities.getOrDefault(item.getId(), 0);
                itemQuantities.put(item.getId(), currentQty + 1);
                quantityText.setText(String.valueOf(currentQty + 1));
                updateCartSummary();
            });
        }
    }
}