package com.malak.bitebridge.models;

import java.io.Serializable;
import java.util.List;

public class Order implements Serializable {
    private int orderId;
    private int userId;
    private List<OrderItem> items;
    private double total;
    private String status;
    private String type;       // "delivery" or "pickup"
    private String address;
    private long timestamp;
    private String firebaseKey;

    public Order() {}

    public int getOrderId() { return orderId; }
    public int getUserId() { return userId; }
    public List<OrderItem> getItems() { return items; }
    public double getTotal() { return total; }
    public String getStatus() { return status; }
    public String getType() { return type; }
    public String getAddress() { return address; }
    public long getTimestamp() { return timestamp; }
    public String getFirebaseKey() { return firebaseKey; }

    public void setOrderId(int orderId) { this.orderId = orderId; }
    public void setUserId(int userId) { this.userId = userId; }
    public void setItems(List<OrderItem> items) { this.items = items; }
    public void setTotal(double total) { this.total = total; }
    public void setStatus(String status) { this.status = status; }
    public void setType(String type) { this.type = type; }
    public void setAddress(String address) { this.address = address; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }
    public void setFirebaseKey(String firebaseKey) { this.firebaseKey = firebaseKey; }
}