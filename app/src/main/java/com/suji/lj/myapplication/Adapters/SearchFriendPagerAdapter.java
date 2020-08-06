package com.suji.lj.myapplication.Adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.suji.lj.myapplication.Fragments.FriendFragment;
import com.suji.lj.myapplication.Fragments.MultiRequestFragment;

public class SearchFriendPagerAdapter extends FragmentStatePagerAdapter {

    int tab_size;


    public SearchFriendPagerAdapter(@NonNull FragmentManager fm, int behavior) {
        super(fm, behavior);
        this.tab_size = behavior;
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                FriendFragment tab2 = new FriendFragment();
                return tab2;


            case 1:
                MultiRequestFragment tab1 = new MultiRequestFragment();
                return tab1;


            default:
                FriendFragment tab3 = new FriendFragment();
                return tab3;
        }

    }

    @Override
    public int getCount() {
        return tab_size;
    }
}