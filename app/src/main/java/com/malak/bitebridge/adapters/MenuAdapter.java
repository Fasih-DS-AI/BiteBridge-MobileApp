package com.malak.bitebridge.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.malak.bitebridge.R;
import com.malak.bitebridge.models.MenuItem;
import com.bumptech.glide.Glide;
import java.util.ArrayList;
import java.util.List;

public class MenuAdapter extends RecyclerView.Adapter<MenuAdapter.MenuViewHolder> {

    private List<MenuItem> menuList;
    private List<MenuItem> menuListFull;
    private Context context;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(MenuItem item);
        void onAddToCart(MenuItem item);
    }

    public MenuAdapter(Context context, List<MenuItem> list,
                       OnItemClickListener listener) {
        this.context = context;
        this.menuList = new ArrayList<>(list);
        this.menuListFull = new ArrayList<>(list);
        this.listener = listener;
    }

    @NonNull
    @Override
    public MenuViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.item_menu_card, parent, false);
        return new MenuViewHolder(view);
    }

    
    @Override
    public void onBindViewHolder(@NonNull MenuViewHolder holder, int position) {
        MenuItem item = menuList.get(position);

        holder.tvName.setText(item.getName());
        holder.tvDescription.setText(item.getDescription());
        holder.tvPrice.setText(String.format("$ %.2f", item.getPrice()));

        // Load image with Glide
        if (item.getImageUrl() != null && !item.getImageUrl().isEmpty()) {
            Glide.with(context)
                    .load(item.getImageUrl())
                    .placeholder(R.drawable.ic_cart)
                    .error(R.drawable.ic_cart)
                    .centerCrop()
                    .into(holder.ivFood);
    }

    holder.itemView.setOnClickListener(v -> listener.onItemClick(item));
    holder.btnAdd.setOnClickListener(v -> listener.onAddToCart(item));
}

    @Override
    public int getItemCount() {
        return menuList.size();
    }

    public void updateList(List<MenuItem> newList) {
        menuList.clear();
        menuList.addAll(newList);
        notifyDataSetChanged();
    }

    public void filterByCategory(String category) {
        menuList.clear();
        if (category.equals("All")) {
            menuList.addAll(menuListFull);
        } else {
            for (MenuItem item : menuListFull) {
                if (item.getCategory().equals(category)) {
                    menuList.add(item);
                }
            }
        }
        notifyDataSetChanged();
    }

    public void filter(String query) {
        menuList.clear();
        if (query.isEmpty()) {
            menuList.addAll(menuListFull);
        } else {
            String lower = query.toLowerCase();
            for (MenuItem item : menuListFull) {
                if (item.getName().toLowerCase().contains(lower) ||
                        item.getCategory().toLowerCase().contains(lower)) {
                    menuList.add(item);
                }
            }
        }
        notifyDataSetChanged();
    }

    static class MenuViewHolder extends RecyclerView.ViewHolder {
        ImageView ivFood;
        TextView tvName, tvDescription, tvPrice;
        Button btnAdd;

        MenuViewHolder(View itemView) {
            super(itemView);
            ivFood = itemView.findViewById(R.id.iv_food_image);
            tvName = itemView.findViewById(R.id.tv_item_name);
            tvDescription = itemView.findViewById(R.id.tv_item_description);
            tvPrice = itemView.findViewById(R.id.tv_item_price);
            btnAdd = itemView.findViewById(R.id.btn_add);
        }
    }
}