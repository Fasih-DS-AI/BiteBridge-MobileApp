package com.malak.bitebridge.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.malak.bitebridge.R;
import com.malak.bitebridge.database.OrderRepository;
import com.malak.bitebridge.firebase.FirebaseManager;
import com.malak.bitebridge.models.CartItem;
import com.malak.bitebridge.models.Order;
import com.malak.bitebridge.models.OrderItem;
import com.malak.bitebridge.utils.CartManager;
import com.malak.bitebridge.utils.NetworkReceiver;
import com.malak.bitebridge.utils.NotificationHelper;
import com.malak.bitebridge.utils.SessionManager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class OrderConfirmActivity extends AppCompatActivity {

    private boolean isDelivery = true;
    private static final double DELIVERY_FEE = 8.0;
    private static final int LOCATION_PERMISSION_CODE = 101;

    private LinearLayout llDelivery, llPickup;
    private EditText etAddress, etNotes;
    private TextView tvItemsTotal, tvDeliveryFee, tvTotal, tvAddressLabel;
    private Button btnConfirm, btnDetectLocation;

    private CartManager cartManager;
    private SessionManager sessionManager;
    private FusedLocationProviderClient locationClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_confirm);

        cartManager = CartManager.getInstance(this);
        sessionManager = SessionManager.getInstance(this);
        locationClient = LocationServices.getFusedLocationProviderClient(this);

        llDelivery = findViewById(R.id.ll_delivery);
        llPickup = findViewById(R.id.ll_pickup);
        etAddress = findViewById(R.id.et_address);
        etNotes = findViewById(R.id.et_notes);
        tvItemsTotal = findViewById(R.id.tv_items_total);
        tvDeliveryFee = findViewById(R.id.tv_delivery_fee);
        tvTotal = findViewById(R.id.tv_total);
        tvAddressLabel = findViewById(R.id.tv_address_label);
        btnConfirm = findViewById(R.id.btn_confirm);
        btnDetectLocation = findViewById(R.id.btn_detect_location);

        TextView btnBack = findViewById(R.id.btn_back);
        btnBack.setOnClickListener(v -> finish());

        updateOrderTypeSummary();

        llDelivery.setOnClickListener(v -> {
            isDelivery = true;
            updateOrderTypeSummary();
        });

        llPickup.setOnClickListener(v -> {
            isDelivery = false;
            updateOrderTypeSummary();
        });

        btnDetectLocation.setOnClickListener(v -> detectLocation());
        btnConfirm.setOnClickListener(v -> placeOrder());
    }

    private void detectLocation() {
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_CODE);
            return;
        }
        doGetLocation();
    }

    private void doGetLocation() {
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) return;

        btnDetectLocation.setText("📍 Detecting...");
        btnDetectLocation.setEnabled(false);

        // Use getCurrentLocation instead of getLastLocation
        // works better on emulators
        com.google.android.gms.location.LocationRequest locationRequest =
                com.google.android.gms.location.LocationRequest.create()
                        .setPriority(
                                com.google.android.gms.location.LocationRequest
                                        .PRIORITY_HIGH_ACCURACY)
                        .setInterval(1000)
                        .setNumUpdates(1);

        com.google.android.gms.location.LocationCallback locationCallback =
                new com.google.android.gms.location.LocationCallback() {
                    @Override
                    public void onLocationResult(
                            @NonNull com.google.android.gms.location
                                    .LocationResult result) {
                        android.location.Location location =
                                result.getLastLocation();
                        if (location != null) {
                            Geocoder geocoder = new Geocoder(
                                    OrderConfirmActivity.this,
                                    Locale.getDefault());
                            try {
                                List<Address> addresses =
                                        geocoder.getFromLocation(
                                                location.getLatitude(),
                                                location.getLongitude(), 1);
                                if (addresses != null &&
                                        !addresses.isEmpty()) {
                                    String addressLine =
                                            addresses.get(0)
                                                    .getAddressLine(0);
                                    etAddress.setText(addressLine);
                                    Toast.makeText(
                                            OrderConfirmActivity.this,
                                            "📍 Location detected!",
                                            Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(
                                            OrderConfirmActivity.this,
                                            "Could not read address," +
                                                    " type manually",
                                            Toast.LENGTH_SHORT).show();
                                }
                            } catch (IOException e) {
                                Toast.makeText(
                                        OrderConfirmActivity.this,
                                        "Geocoder error, type manually",
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                        btnDetectLocation.setText("📍 Detect My Location");
                        btnDetectLocation.setEnabled(true);
                        locationClient.removeLocationUpdates(this);
                    }
                };

        locationClient.requestLocationUpdates(
                locationRequest,
                locationCallback,
                android.os.Looper.getMainLooper());
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(
                requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_CODE) {
            if (grantResults.length > 0 &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                doGetLocation();
            } else {
                Toast.makeText(this,
                        "Location permission denied. " +
                                "Please type address manually.",
                        Toast.LENGTH_LONG).show();
            }
        }
    }

    private void updateOrderTypeSummary() {
        double subtotal = cartManager.getTotal();
        double fee = isDelivery ? DELIVERY_FEE : 0;
        double total = subtotal + fee;

        tvItemsTotal.setText(String.format("$ %.2f", subtotal));
        tvDeliveryFee.setText(isDelivery ?
                String.format("$ %.2f", DELIVERY_FEE) : "Free");
        tvTotal.setText(String.format("$ %.2f", total));

        if (isDelivery) {
            llDelivery.setBackgroundColor(
                    getResources().getColor(R.color.primary));
            llPickup.setBackgroundColor(
                    getResources().getColor(R.color.divider));
            tvAddressLabel.setText("Delivery Address");
            etAddress.setEnabled(true);
            btnDetectLocation.setVisibility(
                    android.view.View.VISIBLE);
        } else {
            llDelivery.setBackgroundColor(
                    getResources().getColor(R.color.divider));
            llPickup.setBackgroundColor(
                    getResources().getColor(R.color.primary));
            tvAddressLabel.setText("Pickup — No address needed");
            etAddress.setEnabled(false);
            btnDetectLocation.setVisibility(
                    android.view.View.GONE);
        }
    }

    private void placeOrder() {
        // Block if no internet
        if (!NetworkReceiver.isConnected(this)) {
            new AlertDialog.Builder(this)
                    .setTitle("No Internet Connection")
                    .setMessage("You need an internet connection " +
                            "to place an order. " +
                            "Please check your connection and try again.")
                    .setPositiveButton("OK", null)
                    .show();
            return;
        }

        if (isDelivery &&
                etAddress.getText().toString().trim().isEmpty()) {
            Toast.makeText(this,
                    "Please enter a delivery address",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        List<OrderItem> orderItems = new ArrayList<>();
        for (CartItem ci : cartManager.getCartItems()) {
            orderItems.add(new OrderItem(
                    ci.getItemId(),
                    ci.getName(),
                    ci.getQuantity(),
                    ci.getUnitPrice(),
                    etNotes.getText().toString().trim()
            ));
        }

        double fee = isDelivery ? DELIVERY_FEE : 0;
        double total = cartManager.getTotal() + fee;

        Order order = new Order();
        order.setUserId(sessionManager.getUserId());
        order.setItems(orderItems);
        order.setTotal(total);
        order.setType(isDelivery ? "delivery" : "pickup");
        order.setAddress(isDelivery ?
                etAddress.getText().toString().trim() : "Pickup");
        order.setStatus("Pending");
        order.setTimestamp(System.currentTimeMillis());

        OrderRepository orderRepo = new OrderRepository(this);
        long orderId = orderRepo.insertOrder(order);
        order.setOrderId((int) orderId);

        FirebaseManager.getInstance().pushOrder(order);
        cartManager.clearCart();

        new NotificationHelper(this)
                .sendOrderPlacedNotification(orderId, total);
        new AlertDialog.Builder(this)
                .setTitle("Order Placed! 🎉")
                .setMessage("Your order #" + orderId +
                        " has been placed!\n\nTotal: $" +
                        String.format("%.2f", total))
                .setPositiveButton("Back to Menu", (dialog, which) -> {
                    startActivity(new Intent(this, HomeActivity.class)
                            .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                    finish();
                })
                .setCancelable(false)
                .show();
    }
}