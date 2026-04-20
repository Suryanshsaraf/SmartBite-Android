package com.smartbite.database;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.smartbite.models.Restaurant;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for Restaurant-related database operations
 */
public class RestaurantDAO {
    private DatabaseHelper dbHelper;

    public RestaurantDAO(DatabaseHelper dbHelper) {
        this.dbHelper = dbHelper;
    }

    /**
     * Get all restaurants
     * @return List of all restaurants
     */
    public List<Restaurant> getAllRestaurants() {
        List<Restaurant> restaurants = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(DatabaseHelper.TABLE_RESTAURANTS, null, 
                               null, null, null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            do {
                Restaurant restaurant = new Restaurant();
                restaurant.setId(cursor.getInt(cursor.getColumnIndex(DatabaseHelper.COLUMN_ID)));
                restaurant.setName(cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_NAME)));
                restaurant.setCuisine(cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_CUISINE)));
                restaurant.setRating(cursor.getFloat(cursor.getColumnIndex(DatabaseHelper.COLUMN_RATING)));
                restaurant.setLatitude(cursor.getDouble(cursor.getColumnIndex(DatabaseHelper.COLUMN_LATITUDE)));
                restaurant.setLongitude(cursor.getDouble(cursor.getColumnIndex(DatabaseHelper.COLUMN_LONGITUDE)));
                restaurants.add(restaurant);
            } while (cursor.moveToNext());
            cursor.close();
        }
        return restaurants;
    }

    /**
     * Get restaurant by ID
     * @param id Restaurant ID
     * @return Restaurant object if found, null otherwise
     */
    public Restaurant getRestaurantById(int id) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String selection = DatabaseHelper.COLUMN_ID + " = ?";
        String[] selectionArgs = {String.valueOf(id)};
        
        Cursor cursor = db.query(DatabaseHelper.TABLE_RESTAURANTS, null, selection, 
                               selectionArgs, null, null, null);

        Restaurant restaurant = null;
        if (cursor != null && cursor.moveToFirst()) {
            restaurant = new Restaurant();
            restaurant.setId(cursor.getInt(cursor.getColumnIndex(DatabaseHelper.COLUMN_ID)));
            restaurant.setName(cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_NAME)));
            restaurant.setCuisine(cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_CUISINE)));
            restaurant.setRating(cursor.getFloat(cursor.getColumnIndex(DatabaseHelper.COLUMN_RATING)));
            restaurant.setLatitude(cursor.getDouble(cursor.getColumnIndex(DatabaseHelper.COLUMN_LATITUDE)));
            restaurant.setLongitude(cursor.getDouble(cursor.getColumnIndex(DatabaseHelper.COLUMN_LONGITUDE)));
            cursor.close();
        }
        return restaurant;
    }

    /**
     * Search restaurants by name or cuisine
     * @param query Search query
     * @return List of matching restaurants
     */
    public List<Restaurant> searchRestaurants(String query) {
        List<Restaurant> restaurants = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        
        String selection = DatabaseHelper.COLUMN_NAME + " LIKE ? OR " + 
                         DatabaseHelper.COLUMN_CUISINE + " LIKE ?";
        String[] selectionArgs = {"%" + query + "%", "%" + query + "%"};

        Cursor cursor = db.query(DatabaseHelper.TABLE_RESTAURANTS, null, selection, 
                               selectionArgs, null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            do {
                Restaurant restaurant = new Restaurant();
                restaurant.setId(cursor.getInt(cursor.getColumnIndex(DatabaseHelper.COLUMN_ID)));
                restaurant.setName(cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_NAME)));
                restaurant.setCuisine(cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_CUISINE)));
                restaurant.setRating(cursor.getFloat(cursor.getColumnIndex(DatabaseHelper.COLUMN_RATING)));
                restaurant.setLatitude(cursor.getDouble(cursor.getColumnIndex(DatabaseHelper.COLUMN_LATITUDE)));
                restaurant.setLongitude(cursor.getDouble(cursor.getColumnIndex(DatabaseHelper.COLUMN_LONGITUDE)));
                restaurants.add(restaurant);
            } while (cursor.moveToNext());
            cursor.close();
        }
        return restaurants;
    }

    /**
     * Insert a new restaurant
     * @param restaurant Restaurant object to insert
     * @return ID of the newly inserted restaurant
     */
    public long insert(Restaurant restaurant) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COLUMN_NAME, restaurant.getName());
        values.put(DatabaseHelper.COLUMN_CUISINE, restaurant.getCuisine());
        values.put(DatabaseHelper.COLUMN_RATING, restaurant.getRating());
        values.put(DatabaseHelper.COLUMN_LATITUDE, restaurant.getLatitude());
        values.put(DatabaseHelper.COLUMN_LONGITUDE, restaurant.getLongitude());
        return db.insert(DatabaseHelper.TABLE_RESTAURANTS, null, values);
    }

    /**
     * Update restaurant details
     * @param restaurant Restaurant object with updated information
     * @return number of rows affected
     */
    public int update(Restaurant restaurant) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COLUMN_NAME, restaurant.getName());
        values.put(DatabaseHelper.COLUMN_CUISINE, restaurant.getCuisine());
        values.put(DatabaseHelper.COLUMN_RATING, restaurant.getRating());
        values.put(DatabaseHelper.COLUMN_LATITUDE, restaurant.getLatitude());
        values.put(DatabaseHelper.COLUMN_LONGITUDE, restaurant.getLongitude());
        
        String whereClause = DatabaseHelper.COLUMN_ID + " = ?";
        String[] whereArgs = {String.valueOf(restaurant.getId())};
        return db.update(DatabaseHelper.TABLE_RESTAURANTS, values, whereClause, whereArgs);
    }

    /**
     * Delete a restaurant
     * @param restaurantId ID of the restaurant to delete
     * @return number of rows affected
     */
    public int delete(int restaurantId) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        String whereClause = DatabaseHelper.COLUMN_ID + " = ?";
        String[] whereArgs = {String.valueOf(restaurantId)};
        return db.delete(DatabaseHelper.TABLE_RESTAURANTS, whereClause, whereArgs);
    }
}