package com.malak.bitebridge.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.malak.bitebridge.models.MenuItem;

import java.util.ArrayList;
import java.util.List;

public class MenuRepository {

    private DatabaseHelper dbHelper;

    public MenuRepository(Context context) {
        dbHelper = new DatabaseHelper(context);
    }

    public List<MenuItem> getAllItems() {
        List<MenuItem> list = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query("menu_items", null,
                "is_available = 1", null, null, null, "category");

        while (cursor.moveToNext()) {
            list.add(cursorToMenuItem(cursor));
        }
        cursor.close();
        return list;
    }

    public List<MenuItem> getByCategory(String category) {
        List<MenuItem> list = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query("menu_items", null,
                "category = ? AND is_available = 1",
                new String[]{category}, null, null, null);

        while (cursor.moveToNext()) {
            list.add(cursorToMenuItem(cursor));
        }
        cursor.close();
        return list;
    }

    public List<MenuItem> search(String query) {
        List<MenuItem> list = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String like = "%" + query + "%";
        Cursor cursor = db.query("menu_items", null,
                "name LIKE ? OR category LIKE ?",
                new String[]{like, like}, null, null, null);

        while (cursor.moveToNext()) {
            list.add(cursorToMenuItem(cursor));
        }
        cursor.close();
        return list;
    }

    public long insertItem(MenuItem item) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("name", item.getName());
        cv.put("category", item.getCategory());
        cv.put("price", item.getPrice());
        cv.put("description", item.getDescription());
        cv.put("image_url", item.getImageUrl());
        cv.put("is_available", item.isAvailable() ? 1 : 0);
        return db.insert("menu_items", null, cv);
    }

    public void updateItem(MenuItem item) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("name", item.getName());
        cv.put("category", item.getCategory());
        cv.put("price", item.getPrice());
        cv.put("description", item.getDescription());
        cv.put("image_url", item.getImageUrl());
        cv.put("is_available", item.isAvailable() ? 1 : 0);
        db.update("menu_items", cv, "item_id = ?",
                new String[]{String.valueOf(item.getItemId())});
    }

    public void deleteItem(int itemId) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.delete("menu_items", "item_id = ?",
                new String[]{String.valueOf(itemId)});
    }

    public List<String> getCategories() {
        List<String> categories = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT DISTINCT category FROM menu_items WHERE is_available = 1", null);
        while (cursor.moveToNext()) {
            categories.add(cursor.getString(0));
        }
        cursor.close();
        return categories;
    }

    private MenuItem cursorToMenuItem(Cursor cursor) {
        return new MenuItem(
                cursor.getInt(cursor.getColumnIndexOrThrow("item_id")),
                cursor.getString(cursor.getColumnIndexOrThrow("name")),
                cursor.getString(cursor.getColumnIndexOrThrow("category")),
                cursor.getDouble(cursor.getColumnIndexOrThrow("price")),
                cursor.getString(cursor.getColumnIndexOrThrow("description")),
                cursor.getString(cursor.getColumnIndexOrThrow("image_url")),
                cursor.getInt(cursor.getColumnIndexOrThrow("is_available")) == 1
        );
    }
}