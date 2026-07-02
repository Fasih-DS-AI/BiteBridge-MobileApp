package com.malak.bitebridge.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.malak.bitebridge.R;
import com.malak.bitebridge.activities.FoodDetailActivity;
import com.malak.bitebridge.adapters.MenuAdapter;
import com.malak.bitebridge.database.MenuRepository;
import com.malak.bitebridge.models.MenuItem;
import com.malak.bitebridge.utils.CartManager;

import java.util.List;

public class MenuFragment extends Fragment {

    private RecyclerView recyclerView;
    private MenuAdapter adapter;
    private MenuRepository menuRepository;
    private ChipGroup chipGroup;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_menu, container, false);

        recyclerView = view.findViewById(R.id.rv_menu_items);
        chipGroup = view.findViewById(R.id.chip_group_categories);
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
        setupCategoryChips();

        return view;
    }

    private void setupCategoryChips() {
        // Add "All" chip first
        Chip allChip = new Chip(requireContext());
        allChip.setText("All");
        allChip.setCheckable(true);
        allChip.setChecked(true);
        allChip.setChipBackgroundColorResource(R.color.primary);
        allChip.setTextColor(getResources().getColor(R.color.white));
        allChip.setOnClickListener(v -> adapter.filterByCategory("All"));
        chipGroup.addView(allChip);

        // Add category chips
        List<String> categories = menuRepository.getCategories();
        for (String category : categories) {
            Chip chip = new Chip(requireContext());
            chip.setText(category);
            chip.setCheckable(true);
            chip.setOnClickListener(v -> adapter.filterByCategory(category));
            chipGroup.addView(chip);
        }
    }
}