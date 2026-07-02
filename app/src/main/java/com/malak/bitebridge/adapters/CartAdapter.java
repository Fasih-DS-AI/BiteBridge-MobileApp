package com.malak.bitebridge.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.malak.bitebridge.R;
import com.malak.bitebridge.models.CartItem;

import java.util.List;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder> {

    private List<CartItem> cartItems;
    private Context context;
    private OnQuantityChangeListener listener;

    public interface OnQuantityChangeListener {
        void onIncrease(CartItem item);
        void onDecrease(CartItem item);
    }

    public CartAdapter(Context context, List<CartItem> items,
                       OnQuantityChangeListener listener) {
        this.context = context;
        this.cartItems = items;
        this.listener = listener;
    }

    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent,
                                             int viewType) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.item_cart, parent, false);
        return new CartViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CartViewHolder holder, int position) {
        CartItem item = cartItems.get(position);

        holder.tvName.setText(item.getName());
        holder.tvPrice.setText(String.format("$ %.2f each", item.getUnitPrice()));
        holder.tvQuantity.setText(String.valueOf(item.getQuantity()));
        holder.tvSubtotal.setText(String.format("$ %.2f", item.getSubtotal()));

        holder.btnIncrease.setOnClickListener(v -> listener.onIncrease(item));
        holder.btnDecrease.setOnClickListener(v -> listener.onDecrease(item));
    }

    @Override
    public int getItemCount() {
        return cartItems.size();
    }

    public void refresh(List<CartItem> newItems) {
        cartItems.clear();
        cartItems.addAll(newItems);
        notifyDataSetChanged();
    }

    static class CartViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvPrice, tvQuantity, tvSubtotal;
        Button btnIncrease, btnDecrease;

        CartViewHolder(View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tv_cart_item_name);
            tvPrice = itemView.findViewById(R.id.tv_cart_item_price);
            tvQuantity = itemView.findViewById(R.id.tv_quantity);
            tvSubtotal = itemView.findViewById(R.id.tv_cart_item_subtotal);
            btnIncrease = itemView.findViewById(R.id.btn_increase);
            btnDecrease = itemView.findViewById(R.id.btn_decrease);
        }
    }
}