package com.malak.bitebridge.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "bitebridge.db";
    private static final int DATABASE_VERSION = 1;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL("CREATE TABLE users (" +
                "user_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "name TEXT NOT NULL, " +
                "email TEXT UNIQUE NOT NULL, " +
                "password TEXT NOT NULL, " +
                "phone TEXT, " +
                "is_admin INTEGER DEFAULT 0)");

        db.execSQL("CREATE TABLE menu_items (" +
                "item_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "name TEXT NOT NULL, " +
                "category TEXT, " +
                "price REAL NOT NULL, " +
                "description TEXT, " +
                "image_url TEXT, " +
                "is_available INTEGER DEFAULT 1)");

        db.execSQL("CREATE TABLE orders (" +
                "order_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "user_id INTEGER, " +
                "total REAL, " +
                "type TEXT, " +
                "address TEXT, " +
                "status TEXT DEFAULT 'Pending', " +
                "timestamp INTEGER)");

        db.execSQL("CREATE TABLE order_items (" +
                "line_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "order_id INTEGER, " +
                "item_id INTEGER, " +
                "quantity INTEGER, " +
                "unit_price REAL, " +
                "notes TEXT)");

        // Seed default menu items
        seedMenuItems(db);
    }

    private void seedMenuItems(SQLiteDatabase db) {
        String insert = "INSERT INTO menu_items (name, category, price, description, image_url, is_available) VALUES ";

        db.execSQL(insert + "('Margherita Pizza', 'Pizza', 32.0, 'Classic pizza with fresh basil and mozzarella', 'https://images.unsplash.com/photo-1604068549290-dea0e4a305ca?w=400', 1)");
        db.execSQL(insert + "('Pepperoni Pizza', 'Pizza', 38.0, 'Loaded with pepperoni and cheese', 'https://images.unsplash.com/photo-1628840042765-356cda07504e?w=400', 1)");
        db.execSQL(insert + "('Grilled Chicken Burger', 'Burgers', 28.0, 'Juicy grilled chicken with lettuce and mayo', 'https://images.unsplash.com/photo-1568901346375-23c9450c58cd?w=400', 1)");
        db.execSQL(insert + "('Beef Burger', 'Burgers', 30.0, 'Classic beef patty with pickles and sauce', 'https://images.unsplash.com/photo-1553979459-d2229ba7433b?w=400', 1)");
        db.execSQL(insert + "('Caesar Salad', 'Salads', 22.0, 'Romaine lettuce, parmesan, croutons', 'https://images.unsplash.com/photo-1546793665-c74683f339c1?w=400', 1)");
        db.execSQL(insert + "('Spicy Chicken Wrap', 'Wraps', 24.0, 'Spicy chicken, lettuce, tomato, sauce', 'https://images.unsplash.com/photo-1626700051175-6818013e1d4f?w=400', 1)");
        db.execSQL(insert + "('French Fries (Large)', 'Sides', 12.0, 'Crispy golden fries', 'https://images.unsplash.com/photo-1630384060421-cb20d0e0649d?w=400', 1)");
        db.execSQL(insert + "('Coca-Cola 330ml', 'Drinks', 6.0, 'Ice cold Coca-Cola', 'https://images.unsplash.com/photo-1554866585-cd94860890b7?w=400', 1)");
        db.execSQL(insert + "('Fresh Orange Juice', 'Drinks', 10.0, 'Freshly squeezed orange juice', 'https://images.unsplash.com/photo-1613478223719-2ab802602423?w=400', 1)");
        db.execSQL(insert + "('Chocolate Brownie', 'Desserts', 18.0, 'Warm chocolate brownie with ice cream', 'https://images.unsplash.com/photo-1564355808539-22fda35bed7e?w=400', 1)");
}

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS order_items");
        db.execSQL("DROP TABLE IF EXISTS orders");
        db.execSQL("DROP TABLE IF EXISTS menu_items");
        db.execSQL("DROP TABLE IF EXISTS users");
        onCreate(db);
    }
}