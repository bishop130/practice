package com.suji.lj.myapplication.Adapters;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.suji.lj.myapplication.ContactActivity;
import com.suji.lj.myapplication.Fragments.ContactSearchFragment;
import com.suji.lj.myapplication.Fragments.FriendSearchFragment;
import com.suji.lj.myapplication.Fragments.FriendsListFragment;
import com.suji.lj.myapplication.Fragments.MultiRequestFragment;

import io.realm.Realm;

public class ContactPagerAdapter extends FragmentStatePagerAdapter {

    int tab_size;
    Context context;
    Realm realm;
    ContactActivity.OnUnCheckFromSelectToFriendListener onUnCheckFromSelectToFriendListener;
    ContactActivity.OnUnCheckFromSelectToContactListener onUnCheckFromSelectToContactListener;



    public ContactPagerAdapter(@NonNull FragmentManager fm, int behavior, Context context, Realm realm, ContactActivity.OnUnCheckFromSelectToContactListener onUnCheckFromSelectToContactListener, ContactActivity.OnUnCheckFromSelectToFriendListener onUnCheckFromSelectToFriendListener) {
        super(fm, behavior);
        this.tab_size = behavior;
        this.context = context;
        this.realm = realm;
        this.onUnCheckFromSelectToContactListener = onUnCheckFromSelectToContactListener;
        this.onUnCheckFromSelectToFriendListener = onUnCheckFromSelectToFriendListener;

    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                ContactSearchFragment tab1 = new ContactSearchFragment(context,realm,onUnCheckFromSelectToContactListener);
                return tab1;
            case 1:
                FriendSearchFragment tab2 = new FriendSearchFragment(context,realm,onUnCheckFromSelectToFriendListener);
                return tab2;
            default:
                ContactSearchFragment tab3 = new ContactSearchFragment(context,realm,onUnCheckFromSelectToContactListener);
                return tab3;
        }

    }

    @Override
    public int getCount() {
        return tab_size;
    }
}
