package com.something.vendorapp.adapter;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.something.vendorapp.R;
import com.something.vendorapp.activity.CatagoriesActivity;
import com.something.vendorapp.model.CategoryHelper;
import com.squareup.picasso.Picasso;

import java.util.List;

public class CategoriesAdapter extends RecyclerView.Adapter<CategoriesAdapter.CategoriesViewHolder> {

    Context context;
    List<CategoryHelper> categoriesList;
    private OnViewClickListener mOnViewClickListener;
    CatagoriesActivity catagoriesActivity;

    public CategoriesAdapter(Context context, List<CategoryHelper> categoriesList, OnViewClickListener mOnViewClickListener, CatagoriesActivity catagoriesActivity) {
        this.context = context;
        this.categoriesList = categoriesList;
        this.mOnViewClickListener = mOnViewClickListener;
        this.catagoriesActivity = catagoriesActivity;
    }

    @NonNull
    @Override
    public CategoriesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View root;
        root = LayoutInflater.from(context).inflate(R.layout.categories_list_item,parent,false);
        CategoriesAdapter.CategoriesViewHolder holder = new CategoriesAdapter.CategoriesViewHolder(root,mOnViewClickListener);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull CategoriesViewHolder holder, int position) {
        CategoryHelper categories = categoriesList.get(position);
        Uri categoryUri = Uri.parse(categories.getCategoryImage());
        Picasso.get().load(categoryUri).into(holder.iv_category_image);
        holder.tv_category_title.setText(categories.getCategoryName());
        holder.popupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popup = new PopupMenu(context, holder.popupButton);
                //inflating menu from xml resource
                popup.inflate(R.menu.categories_menu);
                //adding click listener
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.menu_delete:
                                catagoriesActivity.deleteCategory(categories.getCategoryKey());
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
        return categoriesList.size();
    }


    public static class CategoriesViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        private ImageView iv_category_image;
        private TextView tv_category_title;
        OnViewClickListener onViewClickListener;
        private ImageButton popupButton;

        public CategoriesViewHolder(@NonNull View itemView, OnViewClickListener onViewClickListener) {
            super(itemView);
            iv_category_image = itemView.findViewById(R.id.categoriesImage);
            tv_category_title = itemView.findViewById(R.id.categoriesTitle);
            popupButton = itemView.findViewById(R.id.recycler_popup);
            this.onViewClickListener = onViewClickListener;
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            onViewClickListener.OnItemClick(getAdapterPosition());
        }
    }

    public interface OnViewClickListener {
        void OnItemClick(int position);
    }
}
