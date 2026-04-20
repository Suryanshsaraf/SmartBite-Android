package com.smartbite.models;

/**
 * User model class representing both customers and delivery partners
 */
public class User {
    private int id;
    private String username;
    private String email;
    private String password;
    private String role;  // "customer" or "delivery"
    private String phone;
    private String address;

    // Default constructor
    public User() {}

    // Constructor with all fields
    public User(int id, String username, String email, String password, String role, String phone, String address) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.password = password;
        this.role = role;
        this.phone = phone;
        this.address = address;
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public boolean isCustomer() { return "customer".equals(role); }
    public boolean isDeliveryPartner() { return "delivery".equals(role); }
}