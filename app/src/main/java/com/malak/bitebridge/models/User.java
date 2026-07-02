package com.malak.bitebridge.models;

public class User {
    private int userId;
    private String name;
    private String email;
    private String phone;
    private boolean isAdmin;

    public User() {}

    public User(int userId, String name, String email,
                String phone, boolean isAdmin) {
        this.userId = userId;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.isAdmin = isAdmin;
    }

    public int getUserId() { return userId; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    public String getPhone() { return phone; }
    public boolean isAdmin() { return isAdmin; }

    public void setUserId(int userId) { this.userId = userId; }
    public void setName(String name) { this.name = name; }
    public void setEmail(String email) { this.email = email; }
    public void setPhone(String phone) { this.phone = phone; }
    public void setAdmin(boolean admin) { isAdmin = admin; }
}