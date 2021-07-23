package com.something.vendorapp.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.something.vendorapp.R;
import com.something.vendorapp.fragment.AllOrderFragment;
import com.something.vendorapp.model.OrderPlaced;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class AllOrdersPlacedAdapter extends RecyclerView.Adapter<AllOrdersPlacedAdapter.AllOrdersPlacedViewHolder> {

    Context mContext;
    List<OrderPlaced> orderPlacedList;
    AllOrderFragment allOrderFragment;

    public AllOrdersPlacedAdapter(Context mContext, List<OrderPlaced> orderPlacedList, AllOrderFragment allOrderFragment) {
        this.mContext = mContext;
        this.orderPlacedList = orderPlacedList;
        this.allOrderFragment = allOrderFragment;
    }

    @NonNull
    @NotNull
    @Override
    public AllOrdersPlacedViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View root;
        root = LayoutInflater.from(mContext).inflate(R.layout.order_list_item,parent,false);
        AllOrdersPlacedAdapter.AllOrdersPlacedViewHolder holder = new AllOrdersPlacedAdapter.AllOrdersPlacedViewHolder(root);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull AllOrdersPlacedViewHolder holder, int position) {

        OrderPlaced orderPlacedItem = orderPlacedList.get(position);
        holder.orderAddress.setText(orderPlacedItem.getOrder_address());
        holder.orderItems.setText(orderPlacedItem.getOrder_items());
        holder.orderDate.setText("Ordered on: " + orderPlacedItem.getOrder_date());
        holder.orderStatus.setText(orderPlacedItem.getOrder_status());
        if(orderPlacedItem.getOrder_status().equalsIgnoreCase("PENDING"))
            holder.orderStatus.setTextColor(Color.RED);
        else if(orderPlacedItem.getOrder_status().equalsIgnoreCase("DELIVERED"))
            holder.orderStatus.setTextColor(Color.GREEN);
        else if(orderPlacedItem.getOrder_status().equalsIgnoreCase("IN PROCESS"))
            holder.orderStatus.setTextColor(Color.YELLOW);
        holder.popupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popup = new PopupMenu(mContext, holder.popupButton);
                //inflating menu from xml resource
                popup.inflate(R.menu.order_status_menu);
                //adding click listener
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.menu_pending:
                                //handle menu1 click
                                allOrderFragment.setStatus(orderPlacedItem.getAll_order_key(),"PENDING",orderPlacedItem.getUser_key(),orderPlacedItem.getOrder_key());

                                break;
                            case R.id.menu_success:
                                //handle menu2 click
                                allOrderFragment.setStatus(orderPlacedItem.getAll_order_key(),"DELIVERED",orderPlacedItem.getUser_key(),orderPlacedItem.getOrder_key());
                                break;
                            case R.id.menu_process:
                                //handle menu2 click
                                allOrderFragment.setStatus(orderPlacedItem.getAll_order_key(),"IN PROCESS",orderPlacedItem.getUser_key(),orderPlacedItem.getOrder_key());
                                break;
                        }
                        return false;
                    }
                });
                //displaying the popup
                popup.show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return orderPlacedList.size();
    }

    public static class AllOrdersPlacedViewHolder extends RecyclerView.ViewHolder {

        private TextView orderAddress;
        private TextView orderItems;
        private TextView orderStatus;
        private TextView orderDate;
        private ImageButton popupButton;

        public AllOrdersPlacedViewHolder(@NonNull View itemView) {
            super(itemView);
            orderAddress = itemView.findViewById(R.id.order_recipient_textview);
            orderItems = itemView.findViewById(R.id.order_details_textview);
            orderStatus = itemView.findViewById(R.id.status_name);
            orderDate = itemView.findViewById(R.id.order_date);
            popupButton = itemView.findViewById(R.id.recycler_popup);
        }
    }
}
