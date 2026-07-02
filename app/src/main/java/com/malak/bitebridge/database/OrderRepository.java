package com.malak.bitebridge.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.malak.bitebridge.models.Order;
import com.malak.bitebridge.models.OrderItem;

import java.util.ArrayList;
import java.util.List;

public class OrderRepository {

    private DatabaseHelper dbHelper;

    public OrderRepository(Context context) {
        dbHelper = new DatabaseHelper(context);
    }

    public long insertOrder(Order order) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues cv = new ContentValues();
        cv.put("user_id", order.getUserId());
        cv.put("total", order.getTotal());
        cv.put("type", order.getType());
        cv.put("address", order.getAddress());
        cv.put("status", order.getStatus());
        cv.put("timestamp", order.getTimestamp());

        long orderId = db.insert("orders", null, cv);

        // Insert each order item
        for (OrderItem item : order.getItems()) {
            ContentValues itemCv = new ContentValues();
            itemCv.put("order_id", orderId);
            itemCv.put("item_id", item.getMenuItemId());
            itemCv.put("quantity", item.getQuantity());
            itemCv.put("unit_price", item.getUnitPrice());
            itemCv.put("notes", item.getNotes());
            db.insert("order_items", null, itemCv);
        }

        return orderId;
    }

    public List<Order> getUserOrders(int userId) {
        List<Order> orders = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        Cursor cursor = db.query("orders", null,
                "user_id = ?", new String[]{String.valueOf(userId)},
                null, null, "timestamp DESC");

        while (cursor.moveToNext()) {
            Order order = new Order();
            order.setOrderId(cursor.getInt(
                    cursor.getColumnIndexOrThrow("order_id")));
            order.setUserId(cursor.getInt(
                    cursor.getColumnIndexOrThrow("user_id")));
            order.setTotal(cursor.getDouble(
                    cursor.getColumnIndexOrThrow("total")));
            order.setType(cursor.getString(
                    cursor.getColumnIndexOrThrow("type")));
            order.setAddress(cursor.getString(
                    cursor.getColumnIndexOrThrow("address")));
            order.setStatus(cursor.getString(
                    cursor.getColumnIndexOrThrow("status")));
            order.setTimestamp(cursor.getLong(
                    cursor.getColumnIndexOrThrow("timestamp")));
            order.setItems(getOrderItems(order.getOrderId()));
            orders.add(order);
        }
        cursor.close();
        return orders;
    }

    private List<OrderItem> getOrderItems(int orderId) {
        List<OrderItem> items = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        Cursor cursor = db.rawQuery(
                "SELECT oi.*, mi.name FROM order_items oi " +
                        "JOIN menu_items mi ON oi.item_id = mi.item_id " +
                        "WHERE oi.order_id = ?",
                new String[]{String.valueOf(orderId)});

        while (cursor.moveToNext()) {
            OrderItem item = new OrderItem(
                    cursor.getInt(cursor.getColumnIndexOrThrow("item_id")),
                    cursor.getString(cursor.getColumnIndexOrThrow("name")),
                    cursor.getInt(cursor.getColumnIndexOrThrow("quantity")),
                    cursor.getDouble(cursor.getColumnIndexOrThrow("unit_price")),
                    cursor.getString(cursor.getColumnIndexOrThrow("notes"))
            );
            items.add(item);
        }
        cursor.close();
        return items;
    }
}