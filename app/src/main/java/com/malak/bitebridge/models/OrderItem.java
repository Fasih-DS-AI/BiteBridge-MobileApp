package com.malak.bitebridge.models;

import java.io.Serializable;

public class OrderItem implements Serializable {
    private int menuItemId;
    private String name;
    private int quantity;
    private double unitPrice;
    private String notes;

    public OrderItem() {}

    public OrderItem(int menuItemId, String name,
                     int quantity, double unitPrice, String notes) {
        this.menuItemId = menuItemId;
        this.name = name;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        this.notes = notes;
    }

    public double getSubtotal() { return unitPrice * quantity; }

    public int getMenuItemId() { return menuItemId; }
    public String getName() { return name; }
    public int getQuantity() { return quantity; }
    public double getUnitPrice() { return unitPrice; }
    public String getNotes() { return notes; }

    public void setMenuItemId(int menuItemId) { this.menuItemId = menuItemId; }
    public void setName(String name) { this.name = name; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
    public void setUnitPrice(double unitPrice) { this.unitPrice = unitPrice; }
    public void setNotes(String notes) { this.notes = notes; }
}