package com.smartbite.models;

/**
 * Order model class representing food orders
 */
public class Order {
    private int id;
    private int customerId;
    private int restaurantId;
    private String itemList; // JSON string of items and quantities
    private double totalPrice;
    private String deliveryStatus; // "Pending", "Picked Up", "Delivered"
    private int deliveryPartnerId;
    private String deliveryAddress;
    private String pickupMessage;

    // Default constructor
    public Order() {}

    // Constructor with all fields
    public Order(int id, int customerId, int restaurantId, String itemList, 
                double totalPrice, String deliveryStatus, int deliveryPartnerId, String deliveryAddress, String pickupMessage) {
        this.id = id;
        this.customerId = customerId;
        this.restaurantId = restaurantId;
        this.itemList = itemList;
        this.totalPrice = totalPrice;
        this.deliveryStatus = deliveryStatus;
        this.deliveryPartnerId = deliveryPartnerId;
        this.deliveryAddress = deliveryAddress;
        this.pickupMessage = pickupMessage;
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getCustomerId() { return customerId; }
    public void setCustomerId(int customerId) { this.customerId = customerId; }

    public int getRestaurantId() { return restaurantId; }
    public void setRestaurantId(int restaurantId) { this.restaurantId = restaurantId; }

    public String getItemList() { return itemList; }
    public void setItemList(String itemList) { this.itemList = itemList; }

    public double getTotalPrice() { return totalPrice; }
    public void setTotalPrice(double totalPrice) { this.totalPrice = totalPrice; }

    public String getDeliveryStatus() { return deliveryStatus; }
    public void setDeliveryStatus(String deliveryStatus) { this.deliveryStatus = deliveryStatus; }

    public int getDeliveryPartnerId() { return deliveryPartnerId; }
    public void setDeliveryPartnerId(int deliveryPartnerId) { this.deliveryPartnerId = deliveryPartnerId; }

    public String getDeliveryAddress() { return deliveryAddress; }
    public void setDeliveryAddress(String deliveryAddress) { this.deliveryAddress = deliveryAddress; }

    public String getPickupMessage() { return pickupMessage; }
    public void setPickupMessage(String pickupMessage) { this.pickupMessage = pickupMessage; }
}