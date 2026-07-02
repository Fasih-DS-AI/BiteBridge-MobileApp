package com.malak.bitebridge.models;

import java.io.Serializable;

public class MenuItem implements Serializable {
    private int itemId;
    private String name;
    private String category;
    private double price;
    private String description;
    private String imageUrl;
    private boolean isAvailable;

    public MenuItem() {}

    public MenuItem(int itemId, String name, String category,
                    double price, String description,
                    String imageUrl, boolean isAvailable) {
        this.itemId = itemId;
        this.name = name;
        this.category = category;
        this.price = price;
        this.description = description;
        this.imageUrl = imageUrl;
        this.isAvailable = isAvailable;
    }

    public int getItemId() { return itemId; }
    public String getName() { return name; }
    public String getCategory() { return category; }
    public double getPrice() { return price; }
    public String getDescription() { return description; }
    public String getImageUrl() { return imageUrl; }
    public boolean isAvailable() { return isAvailable; }

    public void setItemId(int itemId) { this.itemId = itemId; }
    public void setName(String name) { this.name = name; }
    public void setCategory(String category) { this.category = category; }
    public void setPrice(double price) { this.price = price; }
    public void setDescription(String description) { this.description = description; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
    public void setAvailable(boolean available) { isAvailable = available; }
}