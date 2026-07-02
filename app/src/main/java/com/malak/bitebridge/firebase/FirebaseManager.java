package com.malak.bitebridge.firebase;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.malak.bitebridge.models.Order;

public class FirebaseManager {

    private static FirebaseManager instance;
    private DatabaseReference ordersRef;

    private FirebaseManager() {
        FirebaseDatabase db = FirebaseDatabase.getInstance();
        ordersRef = db.getReference("bitebridge/orders");
    }

    public static FirebaseManager getInstance() {
        if (instance == null) {
            instance = new FirebaseManager();
        }
        return instance;
    }

    public void pushOrder(Order order) {
        String key = ordersRef.push().getKey();
        if (key != null) {
            order.setFirebaseKey(key);
            ordersRef.child(key).setValue(order);
        }
    }

    public void listenToOrderStatus(String orderId,
                                    ValueEventListener listener) {
        ordersRef.child(orderId).child("status")
                .addValueEventListener(listener);
    }

    public void listenToAllOrders(ChildEventListener listener) {
        ordersRef.addChildEventListener(listener);
    }
}