package com.something.vendorapp.adapter;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.something.vendorapp.R;
import com.something.vendorapp.activity.ItemActivity;
import com.something.vendorapp.model.ItemHelper;
import com.squareup.picasso.Picasso;

import java.util.List;

public class ItemsAdapter extends RecyclerView.Adapter<ItemsAdapter.ItemsViewHolder> {

    Context mContext;
    List<ItemHelper> groceryItems;
    ItemActivity itemActivity;

    public ItemsAdapter(Context mContext, List<ItemHelper> groceryItems, ItemActivity itemActivity) {
        this.mContext = mContext;
        this.groceryItems = groceryItems;
        this.itemActivity = itemActivity;
    }

    @NonNull
    @Override
    public ItemsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View root;
        root = LayoutInflater.from(mContext).inflate(R.layout.grocery_item_layout,parent,false);
        ItemsAdapter.ItemsViewHolder holder = new ItemsAdapter.ItemsViewHolder(root);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ItemsViewHolder holder, int position) {

        ItemHelper groceryItem = groceryItems.get(position);
        Uri itemUri = Uri.parse(groceryItem.getItemImage());
        Picasso.get().load(itemUri).into(holder.itemImage);
        holder.itemText.setText(groceryItem.getItemName() + " - " + groceryItem.getItemQty());
        holder.itemPrice.setText(groceryItem.getItemPrice());
        if(groceryItem.getItemAvailability().equalsIgnoreCase("Out of stock"))
        {
            holder.itemAvailibility.setVisibility(View.VISIBLE);
        }
        holder.popupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popup = new PopupMenu(mContext, holder.popupButton);
                //inflating menu from xml resource
                popup.inflate(R.menu.items_menu);
                //adding click listener
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.menu_edit:
                                itemActivity.editDetails(groceryItem.getItemKey(), groceryItem.getItemName(),groceryItem.getItemPrice(),groceryItem.getItemQty(),groceryItem.getItemImage());
                                break;
                            case R.id.menu_out_of_stock:
                                itemActivity.itemAvailabilityStatus(groceryItem.getItemKey(),"Out of Stock");
                                break;
                            case R.id.menu_in_stock:
                                itemActivity.itemAvailabilityStatus(groceryItem.getItemKey(),"In Stock");
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
        return groceryItems.size();
    }

    public class ItemsViewHolder extends RecyclerView.ViewHolder
    {

        private ImageView itemImage;
        private TextView itemText;
        private ImageButton popupButton;
        private TextView itemPrice;
        private ImageView itemAvailibility;


        public ItemsViewHolder(@NonNull View itemView) {
            super(itemView);
            itemImage = itemView.findViewById(R.id.itemImage);
            itemText = itemView.findViewById(R.id.itemTitle);
            itemPrice = itemView.findViewById(R.id.itemPrice);
            popupButton = itemView.findViewById(R.id.recycler_popup);
            itemAvailibility = itemView.findViewById(R.id.itemAvailabilityIV);

        }
    }
}
