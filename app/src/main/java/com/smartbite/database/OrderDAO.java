package com.smartbite.database;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.smartbite.models.Order;
import java.util.ArrayList;
import java.util.List;

public class OrderDAO {
    private DatabaseHelper dbHelper;

    public OrderDAO(DatabaseHelper dbHelper) {
        this.dbHelper = dbHelper;
    }

    public long createOrder(Order order) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COLUMN_CUSTOMER_ID, order.getCustomerId());
        values.put(DatabaseHelper.COLUMN_RESTAURANT_ID, order.getRestaurantId());
        values.put(DatabaseHelper.COLUMN_ITEM_LIST, order.getItemList());
        values.put(DatabaseHelper.COLUMN_TOTAL_PRICE, order.getTotalPrice());
        values.put(DatabaseHelper.COLUMN_DELIVERY_STATUS, "Pending");
        values.put(DatabaseHelper.COLUMN_DELIVERY_ADDRESS, order.getDeliveryAddress());
        return db.insert(DatabaseHelper.TABLE_ORDERS, null, values);
    }

    public List<Order> getCustomerOrders(int customerId) {
        List<Order> orders = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        
        String selection = DatabaseHelper.COLUMN_CUSTOMER_ID + " = ?";
        String[] selectionArgs = {String.valueOf(customerId)};
        
        Cursor cursor = db.query(DatabaseHelper.TABLE_ORDERS, null, selection, 
                               selectionArgs, null, null, DatabaseHelper.COLUMN_ID + " DESC");

        if (cursor != null && cursor.moveToFirst()) {
            do {
                orders.add(cursorToOrder(cursor));
            } while (cursor.moveToNext());
            cursor.close();
        }
        return orders;
    }

    public List<Order> getDeliveryPartnerOrders(int deliveryPartnerId) {
        List<Order> orders = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        
        String selection = DatabaseHelper.COLUMN_DELIVERY_PARTNER_ID + " = ? AND " +
                         DatabaseHelper.COLUMN_DELIVERY_STATUS + " != ?";
        String[] selectionArgs = {String.valueOf(deliveryPartnerId), "Delivered"};
        
        Cursor cursor = db.query(DatabaseHelper.TABLE_ORDERS, null, selection, 
                               selectionArgs, null, null, DatabaseHelper.COLUMN_ID + " DESC");

        if (cursor != null && cursor.moveToFirst()) {
            do {
                orders.add(cursorToOrder(cursor));
            } while (cursor.moveToNext());
            cursor.close();
        }
        return orders;
    }

    public Order getOrderById(int orderId) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String selection = DatabaseHelper.COLUMN_ID + " = ?";
        String[] selectionArgs = {String.valueOf(orderId)};

        Cursor cursor = db.query(DatabaseHelper.TABLE_ORDERS, null, selection,
                selectionArgs, null, null, null);

        Order order = null;
        if (cursor != null && cursor.moveToFirst()) {
            order = cursorToOrder(cursor);
            cursor.close();
        }
        return order;
    }

    public int updateOrderStatus(int orderId, String status, int deliveryPartnerId) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COLUMN_DELIVERY_STATUS, status);
        values.put(DatabaseHelper.COLUMN_DELIVERY_PARTNER_ID, deliveryPartnerId);
        
        String whereClause = DatabaseHelper.COLUMN_ID + " = ?";
        String[] whereArgs = {String.valueOf(orderId)};
        return db.update(DatabaseHelper.TABLE_ORDERS, values, whereClause, whereArgs);
    }

    public int updatePickupMessage(int orderId, String message) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COLUMN_PICKUP_MESSAGE, message);

        String whereClause = DatabaseHelper.COLUMN_ID + " = ?";
        String[] whereArgs = {String.valueOf(orderId)};
        return db.update(DatabaseHelper.TABLE_ORDERS, values, whereClause, whereArgs);
    }

    public List<Order> getPendingOrders() {
        List<Order> orders = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        
        String selection = DatabaseHelper.COLUMN_DELIVERY_STATUS + " = ? AND " +
                         DatabaseHelper.COLUMN_DELIVERY_PARTNER_ID + " IS NULL";
        String[] selectionArgs = {"Pending"};
        
        Cursor cursor = db.query(DatabaseHelper.TABLE_ORDERS, null, selection, 
                               selectionArgs, null, null, DatabaseHelper.COLUMN_ID + " ASC");

        if (cursor != null && cursor.moveToFirst()) {
            do {
                orders.add(cursorToOrder(cursor));
            } while (cursor.moveToNext());
            cursor.close();
        }
        return orders;
    }

    private Order cursorToOrder(Cursor cursor) {
        Order order = new Order();
        if (cursor.getColumnIndex(DatabaseHelper.COLUMN_ID) != -1) {
            order.setId(cursor.getInt(cursor.getColumnIndex(DatabaseHelper.COLUMN_ID)));
        }
        if (cursor.getColumnIndex(DatabaseHelper.COLUMN_CUSTOMER_ID) != -1) {
            order.setCustomerId(cursor.getInt(cursor.getColumnIndex(DatabaseHelper.COLUMN_CUSTOMER_ID)));
        }
        if (cursor.getColumnIndex(DatabaseHelper.COLUMN_RESTAURANT_ID) != -1) {
            order.setRestaurantId(cursor.getInt(cursor.getColumnIndex(DatabaseHelper.COLUMN_RESTAURANT_ID)));
        }
        if (cursor.getColumnIndex(DatabaseHelper.COLUMN_ITEM_LIST) != -1) {
            order.setItemList(cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_ITEM_LIST)));
        }
        if (cursor.getColumnIndex(DatabaseHelper.COLUMN_TOTAL_PRICE) != -1) {
            order.setTotalPrice(cursor.getDouble(cursor.getColumnIndex(DatabaseHelper.COLUMN_TOTAL_PRICE)));
        }
        if (cursor.getColumnIndex(DatabaseHelper.COLUMN_DELIVERY_STATUS) != -1) {
            order.setDeliveryStatus(cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_DELIVERY_STATUS)));
        }
        if (cursor.getColumnIndex(DatabaseHelper.COLUMN_DELIVERY_PARTNER_ID) != -1) {
            order.setDeliveryPartnerId(cursor.getInt(cursor.getColumnIndex(DatabaseHelper.COLUMN_DELIVERY_PARTNER_ID)));
        }
        if (cursor.getColumnIndex(DatabaseHelper.COLUMN_DELIVERY_ADDRESS) != -1) {
            order.setDeliveryAddress(cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_DELIVERY_ADDRESS)));
        }
        if (cursor.getColumnIndex(DatabaseHelper.COLUMN_PICKUP_MESSAGE) != -1) {
            order.setPickupMessage(cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_PICKUP_MESSAGE)));
        }
        return order;
    }
}