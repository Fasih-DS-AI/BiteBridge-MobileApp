package com.malak.bitebridge.activities;

import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.material.snackbar.Snackbar;
import com.malak.bitebridge.R;
import com.malak.bitebridge.models.MenuItem;
import com.malak.bitebridge.utils.CartManager;
import com.malak.bitebridge.utils.NetworkReceiver;

public class FoodDetailActivity extends AppCompatActivity {

    private int quantity = 1;
    private MenuItem menuItem;

    private TextView tvName, tvPrice, tvCategory,
            tvDescription, tvQuantity, tvLineTotal;
    private Button btnIncrease, btnDecrease, btnAddToCart;
    private ImageView ivFood;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_detail);

        menuItem = (MenuItem) getIntent()
                .getSerializableExtra("menu_item");

        if (menuItem == null) {
            finish();
            return;
        }

        tvName = findViewById(R.id.tv_detail_name);
        tvPrice = findViewById(R.id.tv_detail_price);
        tvCategory = findViewById(R.id.tv_detail_category);
        tvDescription = findViewById(R.id.tv_detail_description);
        tvQuantity = findViewById(R.id.tv_quantity);
        tvLineTotal = findViewById(R.id.tv_line_total);
        btnIncrease = findViewById(R.id.btn_increase_qty);
        btnDecrease = findViewById(R.id.btn_decrease_qty);
        btnAddToCart = findViewById(R.id.btn_add_to_cart);
        ivFood = findViewById(R.id.iv_food_detail_image);

        TextView btnBack = findViewById(R.id.btn_back);
        btnBack.setOnClickListener(v -> finish());

        // Populate fields
        tvName.setText(menuItem.getName());
        tvPrice.setText(String.format("$ %.2f", menuItem.getPrice()));
        tvCategory.setText(menuItem.getCategory());
        tvDescription.setText(menuItem.getDescription());

        // Load image with Glide
        if (menuItem.getImageUrl() != null &&
                !menuItem.getImageUrl().isEmpty()) {
            Glide.with(this)
                    .load(menuItem.getImageUrl())
                    .placeholder(android.R.drawable.ic_menu_gallery)
                    .error(android.R.drawable.ic_menu_gallery)
                    .centerCrop()
                    .into(ivFood);
        }

        // Show offline banner if no internet
        if (!NetworkReceiver.isConnected(this)) {
            Snackbar.make(
                            findViewById(android.R.id.content),
                            "⚠️ No internet — showing cached data",
                            Snackbar.LENGTH_INDEFINITE)
                    .setBackgroundTint(
                            getResources().getColor(R.color.warning))
                    .setTextColor(
                            getResources().getColor(R.color.white))
                    .setAction("OK", v -> {})
                    .show();
        }

        updateQuantityUI();

        btnIncrease.setOnClickListener(v -> {
            quantity++;
            updateQuantityUI();
        });

        btnDecrease.setOnClickListener(v -> {
            if (quantity > 1) {
                quantity--;
                updateQuantityUI();
            }
        });

        btnAddToCart.setOnClickListener(v -> {
            CartManager cart = CartManager.getInstance(this);
            for (int i = 0; i < quantity; i++) {
                cart.addItem(menuItem);
            }
            Toast.makeText(this,
                    quantity + "x " + menuItem.getName() +
                            " added to cart!",
                    Toast.LENGTH_SHORT).show();
            finish();
        });
    }

    private void updateQuantityUI() {
        tvQuantity.setText(String.valueOf(quantity));
        double lineTotal = menuItem.getPrice() * quantity;
        tvLineTotal.setText(String.format("$ %.2f", lineTotal));
    }
}