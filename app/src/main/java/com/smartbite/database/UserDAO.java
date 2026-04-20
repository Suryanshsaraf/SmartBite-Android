package com.smartbite.database;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.smartbite.models.User;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for User-related database operations
 */
public class UserDAO {
    private DatabaseHelper dbHelper;

    public UserDAO(DatabaseHelper dbHelper) {
        this.dbHelper = dbHelper;
    }

    /**
     * Insert a new user into the database
     * @param user User object to insert
     * @return ID of the newly inserted user
     */
    public long insert(User user) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COLUMN_USERNAME, user.getUsername());
        values.put(DatabaseHelper.COLUMN_EMAIL, user.getEmail());
        values.put(DatabaseHelper.COLUMN_PASSWORD, user.getPassword());
        values.put(DatabaseHelper.COLUMN_ROLE, user.getRole());
        return db.insert(DatabaseHelper.TABLE_USERS, null, values);
    }

    /**
     * Get user by email and password for login
     * @param email User's email
     * @param password User's password
     * @return User object if found, null otherwise
     */
    public User getUserByCredentials(String email, String password) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String[] columns = {
            DatabaseHelper.COLUMN_ID,
            DatabaseHelper.COLUMN_USERNAME,
            DatabaseHelper.COLUMN_EMAIL,
            DatabaseHelper.COLUMN_ROLE
        };
        String selection = DatabaseHelper.COLUMN_EMAIL + " = ? AND " + 
                         DatabaseHelper.COLUMN_PASSWORD + " = ?";
        String[] selectionArgs = {email, password};

        Cursor cursor = db.query(DatabaseHelper.TABLE_USERS, columns, selection, 
                               selectionArgs, null, null, null);

        User user = null;
        if (cursor != null && cursor.moveToFirst()) {
            user = new User();
            user.setId(cursor.getInt(cursor.getColumnIndex(DatabaseHelper.COLUMN_ID)));
            user.setUsername(cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_USERNAME)));
            user.setEmail(cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_EMAIL)));
            user.setRole(cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_ROLE)));
            cursor.close();
        }
        return user;
    }

    public User getUserById(long id) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String[] columns = {
                DatabaseHelper.COLUMN_ID,
                DatabaseHelper.COLUMN_USERNAME,
                DatabaseHelper.COLUMN_EMAIL,
                DatabaseHelper.COLUMN_ROLE,
                DatabaseHelper.COLUMN_PHONE,
                DatabaseHelper.COLUMN_ADDRESS
        };
        String selection = DatabaseHelper.COLUMN_ID + " = ?";
        String[] selectionArgs = {String.valueOf(id)};

        Cursor cursor = db.query(DatabaseHelper.TABLE_USERS, columns, selection,
                selectionArgs, null, null, null);

        User user = null;
        if (cursor != null && cursor.moveToFirst()) {
            user = new User();
            user.setId(cursor.getInt(cursor.getColumnIndex(DatabaseHelper.COLUMN_ID)));
            user.setUsername(cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_USERNAME)));
            user.setEmail(cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_EMAIL)));
            user.setRole(cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_ROLE)));
            user.setPhone(cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_PHONE)));
            user.setAddress(cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_ADDRESS)));
            cursor.close();
        }
        return user;
    }

    /**
     * Check if email already exists
     * @param email Email to check
     * @return true if email exists, false otherwise
     */
    public boolean isEmailExists(String email) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String selection = DatabaseHelper.COLUMN_EMAIL + " = ?";
        String[] selectionArgs = {email};
        Cursor cursor = db.query(DatabaseHelper.TABLE_USERS, null, selection, 
                               selectionArgs, null, null, null);
        boolean exists = cursor != null && cursor.getCount() > 0;
        if (cursor != null) {
            cursor.close();
        }
        return exists;
    }

    /**
     * Get all delivery partners
     * @return List of delivery partner users
     */
    public List<User> getAllDeliveryPartners() {
        List<User> deliveryPartners = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String selection = DatabaseHelper.COLUMN_ROLE + " = ?";
        String[] selectionArgs = {"delivery"};

        Cursor cursor = db.query(DatabaseHelper.TABLE_USERS, null, selection, 
                               selectionArgs, null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            do {
                User user = new User();
                user.setId(cursor.getInt(cursor.getColumnIndex(DatabaseHelper.COLUMN_ID)));
                user.setUsername(cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_USERNAME)));
                user.setEmail(cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_EMAIL)));
                user.setRole(cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_ROLE)));
                deliveryPartners.add(user);
            } while (cursor.moveToNext());
            cursor.close();
        }
        return deliveryPartners;
    }

    /**
     * Update user details
     * @param user User object with updated information
     * @return number of rows affected
     */
    public boolean updateUser(User user) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COLUMN_USERNAME, user.getUsername());
        values.put(DatabaseHelper.COLUMN_EMAIL, user.getEmail());
        values.put(DatabaseHelper.COLUMN_PHONE, user.getPhone());
        values.put(DatabaseHelper.COLUMN_ADDRESS, user.getAddress());
        if (user.getPassword() != null) {
            values.put(DatabaseHelper.COLUMN_PASSWORD, user.getPassword());
        }
        String whereClause = DatabaseHelper.COLUMN_ID + " = ?";
        String[] whereArgs = {String.valueOf(user.getId())};
        int rows = db.update(DatabaseHelper.TABLE_USERS, values, whereClause, whereArgs);
        return rows > 0;
    }
}