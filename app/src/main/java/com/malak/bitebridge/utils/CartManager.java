package com.malak.bitebridge.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.malak.bitebridge.models.CartItem;
import com.malak.bitebridge.models.MenuItem;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class CartManager {

    private static CartManager instance;
    private List<CartItem> cartItems;
    private SharedPreferences prefs;
    private static final String PREF_KEY = "cart_items";
    private static final String PREF_NAME = "BiteBridgeCart";

    private CartManager(Context context) {
        prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        cartItems = loadCart();
    }

    public static CartManager getInstance(Context context) {
        if (instance == null) {
            instance = new CartManager(context.getApplicationContext());
        } else {
            // Always reload from prefs to stay in sync
            instance.cartItems = instance.loadCart();
        }
        return instance;
    }

    public void addItem(MenuItem item) {
        for (CartItem ci : cartItems) {
            if (ci.getItemId() == item.getItemId()) {
                ci.setQuantity(ci.getQuantity() + 1);
                saveCart();
                return;
            }
        }
        cartItems.add(new CartItem(item, 1));
        saveCart();
    }

    public void removeItem(int itemId) {
        cartItems.removeIf(ci -> ci.getItemId() == itemId);
        saveCart();
    }

    public void increaseQuantity(int itemId) {
        for (CartItem ci : cartItems) {
            if (ci.getItemId() == itemId) {
                ci.setQuantity(ci.getQuantity() + 1);
                saveCart();
                return;
            }
        }
    }

    public void decreaseQuantity(int itemId) {
        for (CartItem ci : cartItems) {
            if (ci.getItemId() == itemId) {
                if (ci.getQuantity() > 1) {
                    ci.setQuantity(ci.getQuantity() - 1);
                } else {
                    cartItems.remove(ci);
                }
                saveCart();
                return;
            }
        }
    }

    public double getTotal() {
        double total = 0;
        for (CartItem ci : cartItems) {
            total += ci.getSubtotal();
        }
        return total;
    }

    public int getItemCount() {
        int count = 0;
        for (CartItem ci : cartItems) {
            count += ci.getQuantity();
        }
        return count;
    }

    public void clearCart() {
        cartItems.clear();
        saveCart();
    }

    public List<CartItem> getCartItems() {
        return cartItems;
    }

    public boolean isEmpty() {
        return cartItems.isEmpty();
    }

    private void saveCart() {
        Gson gson = new Gson();
        prefs.edit().putString(PREF_KEY, gson.toJson(cartItems)).apply();
    }

    private List<CartItem> loadCart() {
        String json = prefs.getString(PREF_KEY, null);
        if (json == null) return new ArrayList<>();
        Gson gson = new Gson();
        Type type = new TypeToken<List<CartItem>>() {}.getType();
        List<CartItem> loaded = gson.fromJson(json, type);
        return loaded != null ? loaded : new ArrayList<>();
    }
}