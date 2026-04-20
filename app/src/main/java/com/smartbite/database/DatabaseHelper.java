package com.smartbite.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * DatabaseHelper class for managing SQLite database operations
 * Handles table creation, upgrades, and provides access to DAOs
 */
public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "smartbite.db";
    private static final int DATABASE_VERSION = 4; // Incremented version

    // Table Names
    public static final String TABLE_USERS = "users";
    public static final String TABLE_RESTAURANTS = "restaurants";
    public static final String TABLE_MENU_ITEMS = "menu_items";
    public static final String TABLE_ORDERS = "orders";

    // Common column names
    public static final String COLUMN_ID = "id";

    // Users Table Columns
    public static final String COLUMN_USERNAME = "username";
    public static final String COLUMN_EMAIL = "email";
    public static final String COLUMN_PASSWORD = "password";
    public static final String COLUMN_ROLE = "role";
    public static final String COLUMN_PHONE = "phone";
    public static final String COLUMN_ADDRESS = "address";

    // Restaurants Table Columns
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_CUISINE = "cuisine";
    public static final String COLUMN_RATING = "rating";
    public static final String COLUMN_LATITUDE = "latitude";
    public static final String COLUMN_LONGITUDE = "longitude";

    // Menu Items Table Columns
    public static final String COLUMN_RESTAURANT_ID = "restaurant_id";
    public static final String COLUMN_ITEM_NAME = "item_name";
    public static final String COLUMN_PRICE = "price";
    public static final String COLUMN_IMAGE_URL = "image_url";

    // Orders Table Columns
    public static final String COLUMN_CUSTOMER_ID = "customer_id";
    public static final String COLUMN_ITEM_LIST = "item_list";
    public static final String COLUMN_TOTAL_PRICE = "total_price";
    public static final String COLUMN_DELIVERY_STATUS = "delivery_status";
    public static final String COLUMN_DELIVERY_PARTNER_ID = "delivery_partner_id";
    public static final String COLUMN_DELIVERY_ADDRESS = "delivery_address";
    public static final String COLUMN_PICKUP_MESSAGE = "pickup_message";

    // Create Table Statements
    private static final String CREATE_TABLE_USERS = "CREATE TABLE " + TABLE_USERS + "("
            + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + COLUMN_USERNAME + " TEXT NOT NULL,"
            + COLUMN_EMAIL + " TEXT NOT NULL,"
            + COLUMN_PASSWORD + " TEXT NOT NULL,"
            + COLUMN_ROLE + " TEXT NOT NULL,"
            + COLUMN_PHONE + " TEXT,"
            + COLUMN_ADDRESS + " TEXT)";

    private static final String CREATE_TABLE_RESTAURANTS = "CREATE TABLE " + TABLE_RESTAURANTS + "("
            + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + COLUMN_NAME + " TEXT NOT NULL,"
            + COLUMN_CUISINE + " TEXT NOT NULL,"
            + COLUMN_RATING + " REAL,"
            + COLUMN_LATITUDE + " REAL,"
            + COLUMN_LONGITUDE + " REAL)";

    private static final String CREATE_TABLE_MENU_ITEMS = "CREATE TABLE " + TABLE_MENU_ITEMS + "("
            + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + COLUMN_RESTAURANT_ID + " INTEGER,"
            + COLUMN_ITEM_NAME + " TEXT NOT NULL,"
            + COLUMN_PRICE + " REAL NOT NULL,"
            + COLUMN_IMAGE_URL + " TEXT,"
            + "FOREIGN KEY(" + COLUMN_RESTAURANT_ID + ") REFERENCES " 
            + TABLE_RESTAURANTS + "(" + COLUMN_ID + "))";

    private static final String CREATE_TABLE_ORDERS = "CREATE TABLE " + TABLE_ORDERS + "("
            + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + COLUMN_CUSTOMER_ID + " INTEGER,"
            + COLUMN_RESTAURANT_ID + " INTEGER,"
            + COLUMN_ITEM_LIST + " TEXT NOT NULL,"
            + COLUMN_TOTAL_PRICE + " REAL NOT NULL,"
            + COLUMN_DELIVERY_STATUS + " TEXT NOT NULL,"
            + COLUMN_DELIVERY_PARTNER_ID + " INTEGER,"
            + COLUMN_DELIVERY_ADDRESS + " TEXT,"
            + COLUMN_PICKUP_MESSAGE + " TEXT,"
            + "FOREIGN KEY(" + COLUMN_CUSTOMER_ID + ") REFERENCES " 
            + TABLE_USERS + "(" + COLUMN_ID + "),"
            + "FOREIGN KEY(" + COLUMN_RESTAURANT_ID + ") REFERENCES " 
            + TABLE_RESTAURANTS + "(" + COLUMN_ID + "),"
            + "FOREIGN KEY(" + COLUMN_DELIVERY_PARTNER_ID + ") REFERENCES " 
            + TABLE_USERS + "(" + COLUMN_ID + "))";

    private static DatabaseHelper instance;

    // Singleton pattern for DatabaseHelper
    public static synchronized DatabaseHelper getInstance(Context context) {
        if (instance == null) {
            instance = new DatabaseHelper(context.getApplicationContext());
        }
        return instance;
    }

    private DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Creating required tables
        db.execSQL(CREATE_TABLE_USERS);
        db.execSQL(CREATE_TABLE_RESTAURANTS);
        db.execSQL(CREATE_TABLE_MENU_ITEMS);
        db.execSQL(CREATE_TABLE_ORDERS);
        
        // Insert sample data
        insertSampleData(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older tables if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ORDERS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_MENU_ITEMS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_RESTAURANTS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);

        // Create tables again
        onCreate(db);
    }

    /**
     * Insert sample data into the database for testing
     */
    private void insertSampleData(SQLiteDatabase db) {
        // Sample Users
        db.execSQL("INSERT INTO " + TABLE_USERS + " VALUES(1, 'john_doe', 'john@example.com', 'password123', 'customer', '9876543210', '123 Main St, Mumbai')");
        db.execSQL("INSERT INTO " + TABLE_USERS + " VALUES(2, 'jane_smith', 'jane@example.com', 'password123', 'delivery', '9876543211', '456 Park Ave, Mumbai')");

        // Sample Restaurants
        db.execSQL("INSERT INTO " + TABLE_RESTAURANTS + " VALUES(1, 'Tasty Bites', 'Indian', 4.5, 19.0760, 72.8777)");
        db.execSQL("INSERT INTO " + TABLE_RESTAURANTS + " VALUES(2, 'Pizza Paradise', 'Italian', 4.3, 19.0760, 72.8777)");

        // Sample Menu Items
        db.execSQL("INSERT INTO " + TABLE_MENU_ITEMS + " VALUES(1, 1, 'Butter Chicken', 299.99, 'butter_chicken.jpg')");
        db.execSQL("INSERT INTO " + TABLE_MENU_ITEMS + " VALUES(2, 1, 'Paneer Tikka', 249.99, 'paneer_tikka.jpg')");
        db.execSQL("INSERT INTO " + TABLE_MENU_ITEMS + " VALUES(3, 2, 'Margherita Pizza', 199.99, 'margherita.jpg')");
        db.execSQL("INSERT INTO " + TABLE_MENU_ITEMS + " VALUES(4, 2, 'Pepperoni Pizza', 249.99, 'pepperoni.jpg')");
    }
}