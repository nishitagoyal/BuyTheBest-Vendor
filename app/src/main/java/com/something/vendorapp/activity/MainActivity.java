package com.something.vendorapp.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.something.vendorapp.R;
import com.something.vendorapp.adapter.MainAdapter;
import com.something.vendorapp.model.Shared;


public class MainActivity extends AppCompatActivity {

    TabLayout tabLayout;
    ViewPager viewPager;
    ImageView logout_button;
    Shared shared;
    FloatingActionButton addFAB, additemFAB, addCategoryFAB, catalogueFAB;
    boolean isFABOpen = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tabLayout = findViewById(R.id.tabLayout);
        viewPager = findViewById(R.id.viewPager);
        shared = new Shared(getApplicationContext());
        logout_button = findViewById(R.id.logout_button);
        addFAB = findViewById(R.id.add_fab);
        additemFAB = findViewById(R.id.add_item_fab);
        addCategoryFAB = findViewById(R.id.add_category_fab);
        catalogueFAB = findViewById(R.id.catalogue_fab);
        tabLayout.addTab(tabLayout.newTab().setText(getResources().getString(R.string.tab_text_1)));
        tabLayout.addTab(tabLayout.newTab().setText(getResources().getString(R.string.tab_text_2)));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
        final MainAdapter adapter = new MainAdapter(this,getSupportFragmentManager(), tabLayout.getTabCount());
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }
            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }
            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });

        logout_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shared.setFirstTimeLaunched(true);
                Intent intent = new Intent (MainActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });

        addFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!isFABOpen){
                    showFABMenu();
                }else {
                    closeFABMenu();
                }
            }
        });
        additemFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, AddItemActivity.class);
                startActivity(intent);
            }
        });

        catalogueFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, CatagoriesActivity.class);
                startActivity(intent);
            }
        });

        addCategoryFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, AddCategoryActivity.class);
                startActivity(intent);
            }
        });
    }
    private void showFABMenu(){
        isFABOpen=true;
        additemFAB.animate().translationY(-getResources().getDimension(R.dimen.standard_55));
        addCategoryFAB.animate().translationY(-getResources().getDimension(R.dimen.standard_105));
        catalogueFAB.animate().translationY(-getResources().getDimension(R.dimen.standard_155));
    }

    private void closeFABMenu(){
        isFABOpen=false;
        additemFAB.animate().translationY(0);
        addCategoryFAB.animate().translationY(0);
        catalogueFAB.animate().translationY(0);
    }

}