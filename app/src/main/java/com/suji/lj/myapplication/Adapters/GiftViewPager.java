package com.suji.lj.myapplication.Adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.suji.lj.myapplication.Fragments.ShopFragment;
import com.suji.lj.myapplication.Fragments.MyGiftFragment;

public class GiftViewPager extends FragmentStatePagerAdapter {


    int tab_size;


    public GiftViewPager(@NonNull FragmentManager fm, int behavior) {
        super(fm, behavior);
        this.tab_size = behavior;
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                ShopFragment tab1 = new ShopFragment();
                return tab1;

            case 1:
                MyGiftFragment tab2 = new MyGiftFragment();
                return tab2;


            default:
                ShopFragment tab = new ShopFragment();
                return tab;
        }

    }

    @Override
    public int getCount() {
        return tab_size;
    }
}
