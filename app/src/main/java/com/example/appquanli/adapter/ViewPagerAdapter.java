package com.example.appquanli.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.example.appquanli.fragment.ChatFragment;
import com.example.appquanli.fragment.DonhangFragment;
import com.example.appquanli.fragment.QuanLiFragment;
import com.example.appquanli.fragment.ThongKeFragment;

public class ViewPagerAdapter extends FragmentStatePagerAdapter {
    public ViewPagerAdapter(@NonNull FragmentManager fm, int behavior) {
        super(fm, behavior);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0:return new DonhangFragment();
            case 1:return new QuanLiFragment();
            case 2:return new ChatFragment();
            case 3:return new ThongKeFragment();
            default:return new DonhangFragment();
        }
    }

    @Override
    public int getCount() {
        return 4;
    }
}
