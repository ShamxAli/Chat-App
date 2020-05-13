package com.startup.chatapp.adapters;

import android.content.Context;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import java.util.ArrayList;
import java.util.List;

public class ViewPagerAdapter extends FragmentPagerAdapter {


    Context context;

    private ArrayList<Fragment> fragmentArrayList = new ArrayList<Fragment>();
    private ArrayList<String> fragmentTitle = new ArrayList<String>();


    public ViewPagerAdapter(Context context, FragmentManager fm) {
        super(fm);
        this.context = context;
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        return fragmentArrayList.get(position);
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return fragmentTitle.get(position);
    }

    @Override
    public int getCount() {

        return fragmentArrayList.size();

    }

    // Added Laterd
    public void addFragment(Fragment fragment, String string) {
        fragmentArrayList.add(fragment);
        fragmentTitle.add(string);
    }
}