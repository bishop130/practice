package com.suji.lj.myapplication.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.suji.lj.myapplication.Adapters.GiftViewPager;
import com.suji.lj.myapplication.Adapters.SearchFriendPagerAdapter;
import com.suji.lj.myapplication.R;

public class GiftFragment extends Fragment {

    TabLayout tabLayout;
    FrameLayout frameLayout;
    ViewPager viewPager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_gift, container, false);

        tabLayout = view.findViewById(R.id.tlSelect);
        viewPager = view.findViewById(R.id.viewPager);


        tabLayout.addTab(tabLayout.newTab().setText("포인트교환"));
        tabLayout.addTab(tabLayout.newTab().setText("선물함"));


        GiftViewPager adapter = new GiftViewPager(getChildFragmentManager(), tabLayout.getTabCount());
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


        return view;
    }
}
