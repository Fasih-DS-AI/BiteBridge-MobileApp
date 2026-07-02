package com.malak.bitebridge.activities;

import android.Manifest;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.malak.bitebridge.R;
import com.malak.bitebridge.fragments.ChatFragment;
import com.malak.bitebridge.fragments.MenuFragment;
import com.malak.bitebridge.fragments.OrderHistoryFragment;
import com.malak.bitebridge.fragments.ProfileFragment;
import com.malak.bitebridge.fragments.SearchFragment;
import com.malak.bitebridge.utils.NetworkReceiver;

public class HomeActivity extends AppCompatActivity
        implements NetworkReceiver.NetworkListener {

    private NetworkReceiver networkReceiver;
    private View rootView;
    private boolean wasDisconnected = false;
    private FloatingActionButton fabCart; // CLASS FIELD

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        rootView = findViewById(android.R.id.content);

        // Request notification permission on Android 13+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ActivityCompat.checkSelfPermission(this,
                    Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{
                                Manifest.permission.POST_NOTIFICATIONS},
                        102);
            }
        }

        BottomNavigationView bottomNav =
                findViewById(R.id.bottom_navigation);
        fabCart = findViewById(R.id.fab_cart); // ASSIGN FIELD

        loadFragment(new MenuFragment());

        bottomNav.setOnItemSelectedListener(item -> {
            Fragment selected;
            int id = item.getItemId();
            if (id == R.id.nav_menu) {
                selected = new MenuFragment();
                fabCart.setVisibility(View.VISIBLE);
            } else if (id == R.id.nav_search) {
                selected = new SearchFragment();
                fabCart.setVisibility(View.VISIBLE);
            } else if (id == R.id.nav_orders) {
                selected = new OrderHistoryFragment();
                fabCart.setVisibility(View.GONE);
            } else if (id == R.id.nav_chat) {
                selected = new ChatFragment();
                fabCart.setVisibility(View.GONE);
            } else if (id == R.id.nav_profile) {
                selected = new ProfileFragment();
                fabCart.setVisibility(View.GONE);
            } else {
                selected = new MenuFragment();
                fabCart.setVisibility(View.VISIBLE);
            }
            loadFragment(selected);
            return true;
        });

        fabCart.setOnClickListener(v ->
                startActivity(new Intent(
                        HomeActivity.this, CartActivity.class)));

        // Init network receiver
        networkReceiver = new NetworkReceiver(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        IntentFilter filter = new IntentFilter(
                ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(networkReceiver, filter);

        boolean connected = NetworkReceiver.isConnected(this);
        if (!connected) {
            showNoInternetBanner();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        try {
            unregisterReceiver(networkReceiver);
        } catch (IllegalArgumentException e) {
            // Receiver not registered, ignore
        }
    }

    @Override
    public void onNetworkChanged(boolean isConnected) {
        runOnUiThread(() -> {
            if (!isConnected) {
                wasDisconnected = true;
                showNoInternetBanner();
            } else if (wasDisconnected) {
                wasDisconnected = false;
                showBackOnlineBanner();
            }
        });
    }

    private void showNoInternetBanner() {
        Snackbar snackbar = Snackbar.make(rootView,
                "⚠️ No internet connection — " +
                        "menu loaded from local data",
                Snackbar.LENGTH_INDEFINITE);
        snackbar.setBackgroundTint(
                getResources().getColor(R.color.warning));
        snackbar.setTextColor(
                getResources().getColor(R.color.white));
        snackbar.setAction("OK", v -> snackbar.dismiss());
        snackbar.show();
    }

    private void showBackOnlineBanner() {
        Snackbar snackbar = Snackbar.make(rootView,
                "✅ Back online!",
                Snackbar.LENGTH_SHORT);
        snackbar.setBackgroundTint(
                getResources().getColor(R.color.success));
        snackbar.setTextColor(
                getResources().getColor(R.color.white));
        snackbar.show();
    }

    private void loadFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit();
    }
}