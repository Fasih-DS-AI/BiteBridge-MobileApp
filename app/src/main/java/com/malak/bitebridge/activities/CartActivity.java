package com.malak.bitebridge.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.malak.bitebridge.R;
import com.malak.bitebridge.adapters.CartAdapter;
import com.malak.bitebridge.models.CartItem;
import com.malak.bitebridge.utils.CartManager;

import java.util.ArrayList;
import java.util.List;

public class CartActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private CartAdapter adapter;
    private CartManager cartManager;
    private TextView tvSubtotal, tvTotal;
    private Button btnPlaceOrder, btnClearCart;
    private static final double DELIVERY_FEE = 8.0;
    private List<CartItem> cartItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        cartManager = CartManager.getInstance(this);

        recyclerView = findViewById(R.id.rv_cart_items);
        tvSubtotal = findViewById(R.id.tv_subtotal);
        tvTotal = findViewById(R.id.tv_total);
        btnPlaceOrder = findViewById(R.id.btn_place_order);
        btnClearCart = findViewById(R.id.btn_clear_cart);

        TextView btnBack = findViewById(R.id.btn_back);
        btnBack.setOnClickListener(v -> finish());

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(false);

        cartItems = new ArrayList<>();

        adapter = new CartAdapter(this, cartItems,
                new CartAdapter.OnQuantityChangeListener() {
                    @Override
                    public void onIncrease(CartItem item) {
                        cartManager.increaseQuantity(item.getItemId());
                        refreshUI();
                    }

                    @Override
                    public void onDecrease(CartItem item) {
                        cartManager.decreaseQuantity(item.getItemId());
                        refreshUI();
                    }
                });

        recyclerView.setAdapter(adapter);

        btnPlaceOrder.setOnClickListener(v -> {
            if (cartManager.isEmpty()) {
                Toast.makeText(this,
                        "Your cart is empty!", Toast.LENGTH_SHORT).show();
                return;
            }
            startActivity(new Intent(this, OrderConfirmActivity.class));
        });

        btnClearCart.setOnClickListener(v ->
                new AlertDialog.Builder(this)
                        .setTitle("Clear Cart")
                        .setMessage("Remove all items from cart?")
                        .setPositiveButton("Yes", (dialog, which) -> {
                            cartManager.clearCart();
                            refreshUI();
                        })
                        .setNegativeButton("No", null)
                        .show());
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshUI();
    }

    private void refreshUI() {
        cartItems.clear();
        cartItems.addAll(cartManager.getCartItems());
        adapter.notifyDataSetChanged();

        double subtotal = cartManager.getTotal();
        double total = subtotal + DELIVERY_FEE;
        tvSubtotal.setText(String.format("$ %.2f", subtotal));
        tvTotal.setText(String.format("$ %.2f", total));
    }
}