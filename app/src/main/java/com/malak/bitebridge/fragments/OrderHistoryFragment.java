package com.malak.bitebridge.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.malak.bitebridge.R;
import com.malak.bitebridge.adapters.OrderHistoryAdapter;
import com.malak.bitebridge.database.OrderRepository;
import com.malak.bitebridge.models.Order;
import com.malak.bitebridge.utils.SessionManager;

import java.util.List;

public class OrderHistoryFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(
                R.layout.fragment_orders, container, false);

        RecyclerView recyclerView = view.findViewById(R.id.rv_orders);
        recyclerView.setLayoutManager(
                new LinearLayoutManager(requireContext()));

        int userId = SessionManager.getInstance(
                requireContext()).getUserId();
        OrderRepository orderRepo =
                new OrderRepository(requireContext());
        List<Order> orders = orderRepo.getUserOrders(userId);

        if (orders.isEmpty()) {
            // Show empty state
            TextView tv = new TextView(requireContext());
            tv.setText("No orders yet!\nStart ordering delicious food 🍕");
            tv.setTextSize(16);
            tv.setGravity(android.view.Gravity.CENTER);
            tv.setPadding(32, 64, 32, 32);
            tv.setTextColor(getResources().getColor(R.color.text_secondary));
            ((android.widget.LinearLayout) view).addView(tv);
        } else {
            recyclerView.setAdapter(
                    new OrderHistoryAdapter(requireContext(), orders));
        }

        return view;
    }
}