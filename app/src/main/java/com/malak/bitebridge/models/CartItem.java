package com.malak.bitebridge.models;

import java.io.Serializable;

public class CartItem implements Serializable {

    private int itemId;
    private String name;
    private double unitPrice;
    private int quantity;
    private String imageUrl;
    private String notes;

    public CartItem() {}

    public CartItem(MenuItem item, int quantity) {
        this.itemId = item.getItemId();
        this.name = item.getName();
        this.unitPrice = item.getPrice();
        this.quantity = quantity;
        this.imageUrl = item.getImageUrl();
        this.notes = "";
    }

    public double getSubtotal() {
        return unitPrice * quantity;
    }

    public int getItemId() { return itemId; }
    public String getName() { return name; }
    public double getUnitPrice() { return unitPrice; }
    public int getQuantity() { return quantity; }
    public String getImageUrl() { return imageUrl; }
    public String getNotes() { return notes; }

    public void setItemId(int itemId) { this.itemId = itemId; }
    public void setName(String name) { this.name = name; }
    public void setUnitPrice(double unitPrice) { this.unitPrice = unitPrice; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
    public void setNotes(String notes) { this.notes = notes; }
}