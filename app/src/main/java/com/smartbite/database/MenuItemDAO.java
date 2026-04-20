package com.smartbite.database;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.smartbite.models.MenuItem;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for Menu Item-related database operations
 */
public class MenuItemDAO {
    private DatabaseHelper dbHelper;

    public MenuItemDAO(DatabaseHelper dbHelper) {
        this.dbHelper = dbHelper;
    }

    /**
     * Get all menu items for a specific restaurant
     * @param restaurantId ID of the restaurant
     * @return List of menu items for the given restaurant
     */
    public List<MenuItem> getMenuItemsByRestaurant(int restaurantId) {
        List<MenuItem> menuItems = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String selection = DatabaseHelper.COLUMN_RESTAURANT_ID + " = ?";
        String[] selectionArgs = {String.valueOf(restaurantId)};
        
        Cursor cursor = db.query(DatabaseHelper.TABLE_MENU_ITEMS, null, selection,
                               selectionArgs, null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            do {
                menuItems.add(cursorToMenuItem(cursor));
            } while (cursor.moveToNext());
            cursor.close();
        }
        return menuItems;
    }

    /**
     * Get a menu item by its ID
     * @param itemId ID of the menu item
     * @return MenuItem object if found, null otherwise
     */
    public MenuItem getMenuItemById(int itemId) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String selection = DatabaseHelper.COLUMN_ID + " = ?";
        String[] selectionArgs = {String.valueOf(itemId)};

        Cursor cursor = db.query(DatabaseHelper.TABLE_MENU_ITEMS, null, selection,
                selectionArgs, null, null, null);

        MenuItem menuItem = null;
        if (cursor != null && cursor.moveToFirst()) {
            menuItem = cursorToMenuItem(cursor);
            cursor.close();
        }
        return menuItem;
    }

    private MenuItem cursorToMenuItem(Cursor cursor) {
        MenuItem menuItem = new MenuItem();
        menuItem.setId(cursor.getInt(cursor.getColumnIndex(DatabaseHelper.COLUMN_ID)));
        menuItem.setRestaurantId(cursor.getInt(cursor.getColumnIndex(DatabaseHelper.COLUMN_RESTAURANT_ID)));
        menuItem.setItemName(cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_ITEM_NAME)));
        menuItem.setPrice(cursor.getDouble(cursor.getColumnIndex(DatabaseHelper.COLUMN_PRICE)));
        menuItem.setImageUrl(cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_IMAGE_URL)));
        return menuItem;
    }
}
