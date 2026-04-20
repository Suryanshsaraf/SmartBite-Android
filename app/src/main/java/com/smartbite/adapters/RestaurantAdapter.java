package com.smartbite.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.button.MaterialButton;
import com.smartbite.R;
import com.smartbite.models.Restaurant;
import com.smartbite.ui.customer.MenuActivity;
import com.smartbite.utils.Constants;

import java.util.ArrayList;
import java.util.List;

/**
 * Adapter for displaying restaurant items in a RecyclerView
 */
public class RestaurantAdapter extends RecyclerView.Adapter<RestaurantAdapter.RestaurantViewHolder> {
    private Context context;
    private List<Restaurant> restaurants;
    private List<Restaurant> fullRestaurantsList;

    public RestaurantAdapter(Context context) {
        this.context = context;
        this.restaurants = new ArrayList<>();
        this.fullRestaurantsList = new ArrayList<>();
    }

    @NonNull
    @Override
    public RestaurantViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_restaurant, parent, false);
        return new RestaurantViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RestaurantViewHolder holder, int position) {
        Restaurant restaurant = restaurants.get(position);
        holder.bind(restaurant);
    }

    @Override
    public int getItemCount() {
        return restaurants.size();
    }

    public void setRestaurants(List<Restaurant> restaurants) {
        this.restaurants = restaurants;
        this.fullRestaurantsList = new ArrayList<>(restaurants);
        notifyDataSetChanged();
    }

    /**
     * Filter restaurants based on search query
     * @param query Search text
     */
    public void filter(String query) {
        restaurants.clear();
        if (query.isEmpty()) {
            restaurants.addAll(fullRestaurantsList);
        } else {
            query = query.toLowerCase();
            for (Restaurant restaurant : fullRestaurantsList) {
                if (restaurant.getName().toLowerCase().contains(query) ||
                    restaurant.getCuisine().toLowerCase().contains(query)) {
                    restaurants.add(restaurant);
                }
            }
        }
        notifyDataSetChanged();
    }

    class RestaurantViewHolder extends RecyclerView.ViewHolder {
        private ImageView restaurantImage;
        private TextView restaurantName;
        private TextView cuisineType;
        private RatingBar ratingBar;
        private MaterialButton viewMenuButton;

        RestaurantViewHolder(@NonNull View itemView) {
            super(itemView);
            restaurantImage = itemView.findViewById(R.id.restaurantImage);
            restaurantName = itemView.findViewById(R.id.restaurantName);
            cuisineType = itemView.findViewById(R.id.cuisineType);
            ratingBar = itemView.findViewById(R.id.ratingBar);
            viewMenuButton = itemView.findViewById(R.id.viewMenuButton);
        }

        void bind(Restaurant restaurant) {
            restaurantName.setText(restaurant.getName());
            cuisineType.setText(restaurant.getCuisine());
            ratingBar.setRating(restaurant.getRating());

            // Load restaurant image using Glide
            // In a real app, you would load from a URL or resource
            Glide.with(context)
                 .load(android.R.drawable.sym_def_app_icon) // Placeholder
                 .centerCrop()
                 .into(restaurantImage);

            viewMenuButton.setOnClickListener(v -> {
                Intent intent = new Intent(context, MenuActivity.class);
                intent.putExtra(Constants.EXTRA_RESTAURANT_ID, restaurant.getId());
                context.startActivity(intent);
            });
        }
    }
}