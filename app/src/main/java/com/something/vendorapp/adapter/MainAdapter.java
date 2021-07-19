package com.something.vendorapp.adapter;

import android.content.Context;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.something.vendorapp.fragment.AllOrderFragment;
import com.something.vendorapp.fragment.TodayOrderFragment;

public class MainAdapter extends FragmentPagerAdapter {

    Context context;
    int totalTabs;

    public MainAdapter(Context c, FragmentManager fm, int totalTabs) {
        super(fm);
        context = c;
        this.totalTabs = totalTabs;
    }

    @Override
    public Fragment getItem(int position) {

        switch (position) {
            case 0:
                TodayOrderFragment todayOrderFragment = new TodayOrderFragment();
                return todayOrderFragment;

            case 1:
                AllOrderFragment allOrderFragment = new AllOrderFragment();
                return allOrderFragment;

            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return totalTabs;
    }
}
