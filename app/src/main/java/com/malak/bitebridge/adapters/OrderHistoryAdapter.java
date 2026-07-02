package com.malak.bitebridge.adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.malak.bitebridge.R;
import com.malak.bitebridge.models.Order;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class OrderHistoryAdapter extends
        RecyclerView.Adapter<OrderHistoryAdapter.OrderViewHolder> {

    private List<Order> orders;
    private Context context;

    public OrderHistoryAdapter(Context context, List<Order> orders) {
        this.context = context;
        this.orders = orders;
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent,
                                              int viewType) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.item_order, parent, false);
        return new OrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder,
                                 int position) {
        Order order = orders.get(position);

        holder.tvOrderId.setText("Order #" + order.getOrderId());

        // Format date
        SimpleDateFormat sdf = new SimpleDateFormat(
                "dd MMM yyyy, hh:mm a", Locale.getDefault());
        holder.tvOrderDate.setText(
                sdf.format(new Date(order.getTimestamp())));

        // Order type with emoji
        holder.tvOrderType.setText(
                order.getType().equals("delivery") ?
                        "🚗 Delivery" : "🏪 Pickup");

        // Items count
        int itemCount = order.getItems() != null ?
                order.getItems().size() : 0;
        holder.tvItemsCount.setText(itemCount + " item(s)");

        // Total
        holder.tvTotal.setText(
                String.format("$ %.2f", order.getTotal()));

        // Status badge color
        holder.tvStatus.setText(order.getStatus());
        switch (order.getStatus()) {
            case "Pending":
                holder.tvStatus.setBackgroundColor(
                        Color.parseColor("#757575"));
                break;
            case "Preparing":
                holder.tvStatus.setBackgroundColor(
                        Color.parseColor("#2980B9"));
                break;
            case "Ready":
                holder.tvStatus.setBackgroundColor(
                        Color.parseColor("#27AE60"));
                break;
            case "Delivered":
                holder.tvStatus.setBackgroundColor(
                        Color.parseColor("#27AE60"));
                break;
            default:
                holder.tvStatus.setBackgroundColor(
                        Color.parseColor("#757575"));
        }
    }

    @Override
    public int getItemCount() {
        return orders.size();
    }

    static class OrderViewHolder extends RecyclerView.ViewHolder {
        TextView tvOrderId, tvOrderDate, tvOrderType,
                tvItemsCount, tvTotal, tvStatus;

        OrderViewHolder(View itemView) {
            super(itemView);
            tvOrderId = itemView.findViewById(R.id.tv_order_id);
            tvOrderDate = itemView.findViewById(R.id.tv_order_date);
            tvOrderType = itemView.findViewById(R.id.tv_order_type);
            tvItemsCount = itemView.findViewById(R.id.tv_order_items_count);
            tvTotal = itemView.findViewById(R.id.tv_order_total);
            tvStatus = itemView.findViewById(R.id.tv_order_status);
        }
    }
}