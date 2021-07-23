package com.something.vendorapp.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.something.vendorapp.R;
import com.something.vendorapp.model.OrderPlaced;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class OrdersPlacedAdapter extends RecyclerView.Adapter<OrdersPlacedAdapter.OrderPlacedViewHolder>{

    Context mContext;
    List<OrderPlaced> orderPlacedList;

    public OrdersPlacedAdapter(Context mContext, List<OrderPlaced> orderPlacedList) {
        this.mContext = mContext;
        this.orderPlacedList = orderPlacedList;
    }

    @NonNull
    @NotNull
    @Override
    public OrderPlacedViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View root;
        root = LayoutInflater.from(mContext).inflate(R.layout.order_list_item,parent,false);
        OrdersPlacedAdapter.OrderPlacedViewHolder holder = new OrdersPlacedAdapter.OrderPlacedViewHolder(root);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull OrderPlacedViewHolder holder, int position) {

        OrderPlaced orderPlacedItem = orderPlacedList.get(position);
        holder.orderAddress.setText(orderPlacedItem.getOrder_address());
        holder.orderItems.setText(orderPlacedItem.getOrder_items());
        holder.orderDate.setText("Ordered on: " + orderPlacedItem.getOrder_date());
        holder.orderStatus.setText(orderPlacedItem.getOrder_status());
        if(orderPlacedItem.getOrder_status().equalsIgnoreCase("PENDING"))
            holder.orderStatus.setTextColor(Color.RED);
        else if(orderPlacedItem.getOrder_status().equalsIgnoreCase("DELIVERED"))
            holder.orderStatus.setTextColor(Color.GREEN);

    }

    @Override
    public int getItemCount() {
        return orderPlacedList.size();
    }

    public static class OrderPlacedViewHolder extends RecyclerView.ViewHolder {

        private TextView orderAddress;
        private TextView orderItems;
        private TextView orderStatus;
        private TextView orderDate;

        public OrderPlacedViewHolder(@NonNull View itemView) {
            super(itemView);
            orderAddress = itemView.findViewById(R.id.order_recipient_textview);
            orderItems = itemView.findViewById(R.id.order_details_textview);
            orderStatus = itemView.findViewById(R.id.status_name);
            orderDate = itemView.findViewById(R.id.order_date);
        }
    }
}
