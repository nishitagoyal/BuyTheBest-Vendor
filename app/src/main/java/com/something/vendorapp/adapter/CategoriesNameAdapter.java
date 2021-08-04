package com.something.vendorapp.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.something.vendorapp.R;
import com.something.vendorapp.model.CategoryHelper;

import java.util.List;

public class CategoriesNameAdapter extends RecyclerView.Adapter<CategoriesNameAdapter.CategoriesNameViewHolder> {


    Context context;
    List<CategoryHelper> categoriesList;

    public CategoriesNameAdapter(Context context, List<CategoryHelper> categoriesList) {
        this.context = context;
        this.categoriesList = categoriesList;
    }

    @NonNull
    @Override
    public CategoriesNameViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View root;
        root = LayoutInflater.from(context).inflate(R.layout.category_name_item_layout,parent,false);
        CategoriesNameAdapter.CategoriesNameViewHolder holder = new CategoriesNameAdapter.CategoriesNameViewHolder(root);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull CategoriesNameViewHolder holder, int position) {
        CategoryHelper categories = categoriesList.get(position);
        holder.categoryName.setText(categories.getCategoryName());
    }

    @Override
    public int getItemCount() {
        return categoriesList.size();
    }

    public class CategoriesNameViewHolder extends RecyclerView.ViewHolder
    {

        private TextView categoryName;

        public CategoriesNameViewHolder(@NonNull View itemView) {
            super(itemView);
            categoryName = itemView.findViewById(R.id.categoryTitle);
        }
    }
}
