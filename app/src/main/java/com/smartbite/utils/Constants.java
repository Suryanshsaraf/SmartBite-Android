package com.smartbite.utils;

/**
 * Constants class to store app-wide constant values
 */
public class Constants {
    // SharedPreferences
    public static final String PREF_NAME = "SmartBitePrefs";
    public static final String KEY_USER_ID = "userId";
    public static final String KEY_USER_ROLE = "userRole";
    public static final String KEY_USERNAME = "username";
    
    // Intent extras
    public static final String EXTRA_RESTAURANT_ID = "restaurantId";
    public static final String EXTRA_ORDER_ID = "orderId";
    public static final String EXTRA_DELIVERY_ADDRESS = "deliveryAddress";
    public static final String EXTRA_CART_ITEMS = "cartItems";
    
    // Order status
    public static final String STATUS_PENDING = "Pending";
    public static final String STATUS_PICKED_UP = "Picked Up";
    public static final String STATUS_DELIVERED = "Delivered";
    
    // User roles
    public static final String ROLE_CUSTOMER = "customer";
    public static final String ROLE_DELIVERY = "delivery";
    
    // Request codes
    public static final int REQUEST_LOCATION_PERMISSION = 1001;
    public static final int REQUEST_ENABLE_GPS = 1002;
    
    // Map constants
    public static final float DEFAULT_ZOOM = 15f;
    public static final int LOCATION_UPDATE_INTERVAL = 10000; // 10 seconds
    
    // Validation constants
    public static final int MIN_PASSWORD_LENGTH = 6;
    public static final String EMAIL_PATTERN = 
        "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
}