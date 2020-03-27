package com.suji.lj.myapplication.Adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.suji.lj.myapplication.Fragments.FriendsListFragment;
import com.suji.lj.myapplication.Fragments.MultiRequestFragment;

public class FriendPagerAdapter extends FragmentStatePagerAdapter {

    int tab_size;


    public FriendPagerAdapter(@NonNull FragmentManager fm, int behavior) {
        super(fm, behavior);
        this.tab_size = behavior;
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                MultiRequestFragment tab1 = new MultiRequestFragment();
                return tab1;
            case 1:
                FriendsListFragment tab2 = new FriendsListFragment();
                return tab2;
            default:
                MultiRequestFragment tab3 = new MultiRequestFragment();
                return tab3;
        }

    }

    @Override
    public int getCount() {
        return tab_size;
    }
}
