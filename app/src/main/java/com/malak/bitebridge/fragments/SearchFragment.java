package com.malak.bitebridge.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.malak.bitebridge.R;
import com.malak.bitebridge.activities.FoodDetailActivity;
import com.malak.bitebridge.adapters.MenuAdapter;
import com.malak.bitebridge.database.MenuRepository;
import com.malak.bitebridge.models.MenuItem;
import com.malak.bitebridge.utils.CartManager;

import java.util.List;

public class SearchFragment extends Fragment {

    private EditText etSearch;
    private RecyclerView recyclerView;
    private MenuAdapter adapter;
    private MenuRepository menuRepository;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search, container, false);

        etSearch = view.findViewById(R.id.et_search);
        recyclerView = view.findViewById(R.id.rv_search_results);
        menuRepository = new MenuRepository(requireContext());

        recyclerView.setLayoutManager(
                new LinearLayoutManager(requireContext()));

        List<MenuItem> allItems = menuRepository.getAllItems();

        adapter = new MenuAdapter(requireContext(), allItems,
                new MenuAdapter.OnItemClickListener() {
                    @Override
                    public void onItemClick(MenuItem item) {
                        Intent intent = new Intent(getActivity(),
                                FoodDetailActivity.class);
                        intent.putExtra("menu_item", item);
                        startActivity(intent);
                    }

                    @Override
                    public void onAddToCart(MenuItem item) {
                        CartManager.getInstance(requireContext()).addItem(item);
                        Toast.makeText(requireContext(),
                                item.getName() + " added to cart!",
                                Toast.LENGTH_SHORT).show();
                    }
                });

        recyclerView.setAdapter(adapter);

        // Live search as user types
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {
                adapter.filter(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        return view;
    }
}