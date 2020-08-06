package com.suji.lj.myapplication.Adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.suji.lj.myapplication.Fragments.AddFriendFragment;
import com.suji.lj.myapplication.Fragments.ReceivedFriendRequestFragment;
import com.suji.lj.myapplication.Fragments.SendFriendRequestFragment;

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
                AddFriendFragment tab1 = new AddFriendFragment();
                return tab1;

            case 1:
                ReceivedFriendRequestFragment tab2 = new ReceivedFriendRequestFragment();
                return tab2;

            case 2:
                SendFriendRequestFragment tab3 = new SendFriendRequestFragment();
                return tab3;


            default:
                AddFriendFragment tab = new AddFriendFragment();
                return tab;
        }

    }

    @Override
    public int getCount() {
        return tab_size;
    }
}
