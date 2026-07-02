package com.malak.bitebridge.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.malak.bitebridge.models.User;

public class UserRepository {

    private DatabaseHelper dbHelper;

    public UserRepository(Context context) {
        dbHelper = new DatabaseHelper(context);
    }

    public long registerUser(String name, String email,
                             String password, String phone) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("name", name);
        cv.put("email", email);
        cv.put("password", password);
        cv.put("phone", phone);
        cv.put("is_admin", 0);
        return db.insert("users", null, cv);
    }

    public User loginUser(String email, String password) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query("users", null,
                "email = ? AND password = ?",
                new String[]{email, password}, null, null, null);

        if (cursor.moveToFirst()) {
            User user = new User(
                    cursor.getInt(cursor.getColumnIndexOrThrow("user_id")),
                    cursor.getString(cursor.getColumnIndexOrThrow("name")),
                    cursor.getString(cursor.getColumnIndexOrThrow("email")),
                    cursor.getString(cursor.getColumnIndexOrThrow("phone")),
                    cursor.getInt(cursor.getColumnIndexOrThrow("is_admin")) == 1
            );
            cursor.close();
            return user;
        }
        cursor.close();
        return null;
    }

    public boolean emailExists(String email) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query("users", null,
                "email = ?", new String[]{email},
                null, null, null);
        boolean exists = cursor.getCount() > 0;
        cursor.close();
        return exists;
    }
    public void makeAdmin(String email) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("is_admin", 1);
        db.update("users", cv, "email = ?", new String[]{email});
    }
}