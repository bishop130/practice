package com.suji.lj.myapplication.Adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;


import com.suji.lj.myapplication.Fragments.RecentWithDrawFragment;
import com.suji.lj.myapplication.Fragments.WithDrawFragment;

public class WithDrawPagerAdapter extends FragmentStatePagerAdapter {

    int tab_size;


    public WithDrawPagerAdapter(@NonNull FragmentManager fm, int behavior) {
        super(fm, behavior);
        this.tab_size = behavior;
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new WithDrawFragment();
            case 1:
                return new RecentWithDrawFragment();
            default:
                return new WithDrawFragment();
        }

    }

    @Override
    public int getCount() {
        return tab_size;
    }
}
