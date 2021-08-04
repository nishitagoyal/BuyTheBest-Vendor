package com.something.vendorapp.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.something.vendorapp.R;
import com.something.vendorapp.adapter.ItemsAdapter;
import com.something.vendorapp.model.ItemHelper;
import com.something.vendorapp.model.Shared;

import java.util.ArrayList;
import java.util.List;

public class ItemActivity extends AppCompatActivity {

    List<ItemHelper> groceryItems;
    ItemsAdapter itemsAdapter;
    RecyclerView itemRecyclerView;
    FirebaseDatabase rootnode;
    DatabaseReference reference, itemReference, addToCartReference;
    String categoryName,categoryKey;
    Shared shared;
    TextView txt_item;
    DisplayMetrics metrics;
    int width, height;
    ImageView noItemsIV;
    ProgressBar progressBar;
    SearchView searchView;
    CardView searchCardView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item);
        Intent intent = getIntent();
        categoryName = intent.getStringExtra("categoryName");
        categoryKey = intent.getStringExtra("categoryKey");
        txt_item = findViewById(R.id.txt_heading);
        txt_item.setText(categoryName);
        initViews();
    }

    private void initViews() {
        itemRecyclerView = findViewById(R.id.itemListRecyclerView);
        groceryItems = new ArrayList<>();
        itemsAdapter = new ItemsAdapter(ItemActivity.this,groceryItems, ItemActivity.this);
        itemRecyclerView.setAdapter(itemsAdapter);
        rootnode = FirebaseDatabase.getInstance();
        reference = rootnode.getReference("categories");
        addToCartReference = rootnode.getReference("users");
        shared = new Shared(ItemActivity.this);
        metrics = getResources().getDisplayMetrics();
        width = metrics.widthPixels;
        height = metrics.heightPixels;
        progressBar = findViewById(R.id.items_progress);
        noItemsIV = findViewById(R.id.empty_item);
        searchView = findViewById(R.id.search_view);
        searchCardView = findViewById(R.id.search_cardView);
        populateList();

    }

    private void populateList() {

        groceryItems.clear();
        itemReference = reference.child(categoryKey).child("items");
        itemReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot ds: snapshot.getChildren())
                {
                    ItemHelper groceryItem = ds.getValue(ItemHelper.class);
                    groceryItems.add(groceryItem);
                }

                if(groceryItems.isEmpty()) {
                    noItemsIV.setVisibility(View.VISIBLE);
                    searchCardView.setVisibility(View.GONE);
                }

                itemRecyclerView.setAdapter(new ItemsAdapter(ItemActivity.this, groceryItems,ItemActivity.this));
                progressBar.setVisibility(View.GONE);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    public void itemAvailabilityStatus(String key, String status)
    {
        itemReference = reference.child(categoryKey).child("items");
        itemReference.child(key).child("itemAvailability").setValue(status);
        populateList();
    }

    public void editDetails(String key, String itemName, String itemPrice, String itemQty)
    {
        final Dialog dialog = new Dialog(ItemActivity.this);
        dialog.setContentView(R.layout.edit_item_dialog);
        dialog.setTitle("Edit Details");
        dialog.getWindow().setLayout((6 * width)/7, LinearLayout.LayoutParams.WRAP_CONTENT);
        itemReference = reference.child(categoryKey).child("items");
        Button button = (Button) dialog.findViewById(R.id.editDetailsButton);
        TextInputEditText editName = dialog.findViewById(R.id.name_editText);
        TextInputEditText editPrice = dialog.findViewById(R.id.phoneNum_editText);
        TextInputEditText editQty = dialog.findViewById(R.id.address_editText);
        editName.setText(itemName);
        editPrice.setText(itemPrice);
        editQty.setText(itemQty);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                if(!editName.getText().toString().isEmpty() && !editPrice.getText().toString().isEmpty() && !editQty.getText().toString().isEmpty())
                {
                    String name = editName.getText().toString();
                    String price = editPrice.getText().toString();
                    String Qty = editQty.getText().toString();
                    itemReference.child(key).child("itemName").setValue(name);
                    itemReference.child(key).child("itemPrice").setValue(price);
                    itemReference.child(key).child("itemQty").setValue(Qty);
                    Toast.makeText(ItemActivity.this,"Item edited successfully!",Toast.LENGTH_SHORT).show();
                    populateList();
                    dialog.dismiss();

                }
                if(editName.getText().toString().isEmpty())
                {
                    editName.setError(getString(R.string.field_required));
                }
                if(editPrice.getText().toString().isEmpty())
                {
                    editPrice.setError(getString(R.string.field_required));
                }
                if(editQty.getText().toString().isEmpty())
                {
                    editQty.setError(getString(R.string.field_required));
                }
            }
        });
        dialog.show();
    }

    @Override
    public void onStart() {
        super.onStart();
        if(searchView!=null)
        {
            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    return false;
                }

                @Override
                public boolean onQueryTextChange(String newText) {
                    search(newText);
                    return false;
                }
            });
        }
    }

    private void search(String str) {
        List<ItemHelper> itemSearchList = new ArrayList<>();
        for(ItemHelper itemHelperObject: groceryItems)
        {
            if(itemHelperObject.getItemName().toLowerCase().contains(str.toLowerCase()))
                itemSearchList.add(itemHelperObject);
        }
        itemRecyclerView.setAdapter(new ItemsAdapter(ItemActivity.this, itemSearchList,ItemActivity.this));


    }

}