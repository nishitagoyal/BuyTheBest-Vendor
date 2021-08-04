package com.something.vendorapp.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.something.vendorapp.R;
import com.something.vendorapp.adapter.CategoriesAdapter;
import com.something.vendorapp.model.CategoryHelper;

import java.util.ArrayList;
import java.util.List;

public class CatagoriesActivity extends AppCompatActivity {

    RecyclerView categoriesRecyclerView;
    List<CategoryHelper> categoriesList;
    DatabaseReference databaseReference;
    ProgressBar progressBar;
    ImageView emptyCart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_catagories);
        categoriesRecyclerView = findViewById(R.id.categoriesListRecyclerView);
        progressBar = findViewById(R.id.categories_progress);
        categoriesList = new ArrayList<>();
        emptyCart = findViewById(R.id.empty_cart);
        databaseReference = FirebaseDatabase.getInstance().getReference("categories");
        populateList();
    }

    public void populateList() {
        categoriesList.clear();
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot ds: snapshot.getChildren())
                {
                    CategoryHelper categories = ds.getValue(CategoryHelper.class);
                    categoriesList.add(categories);
                }

                if(categoriesList.isEmpty())
                    emptyCart.setVisibility(View.VISIBLE);
                GridLayoutManager layoutManager=new GridLayoutManager(CatagoriesActivity.this,3);
                categoriesRecyclerView.setLayoutManager(layoutManager);
                categoriesRecyclerView.setAdapter(new CategoriesAdapter(CatagoriesActivity.this,categoriesList,this::OnItemClick, CatagoriesActivity.this));
                progressBar.setVisibility(View.GONE);
            }

            public void OnItemClick(int position) {
                CategoryHelper categories = categoriesList.get(position);
                String category_key = categories.getCategoryKey();
                String category_name = categories.getCategoryName();
                Intent intent = new Intent(CatagoriesActivity.this, ItemActivity.class);
                intent.putExtra("categoryKey",category_key);
                intent.putExtra("categoryName",category_name);
                startActivity(intent);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void deleteCategory(String categoryKey)
    {

        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which)
                {

                    case DialogInterface.BUTTON_POSITIVE:
                        databaseReference.child(categoryKey).removeValue();
                        populateList();
                        dialog.dismiss();
                        Toast.makeText(CatagoriesActivity.this,"Category successfully deleted!",Toast.LENGTH_LONG).show();
                        break;

                    case DialogInterface.BUTTON_NEGATIVE:
                        dialog.dismiss();
                        break;

                }
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(CatagoriesActivity.this);
        builder.setTitle("Are you sure?").setMessage("Deleting this category will remove all the associated items.").setPositiveButton("Yes", dialogClickListener)
                .setNegativeButton("No", dialogClickListener).show();

    }
}