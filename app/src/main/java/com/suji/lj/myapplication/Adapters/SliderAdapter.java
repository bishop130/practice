package com.suji.lj.myapplication.Adapters;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.suji.lj.myapplication.Fragments.Fragments1;
import com.suji.lj.myapplication.Fragments.Fragments2;
import com.suji.lj.myapplication.Fragments.Fragments3;

public class SliderAdapter extends FragmentStatePagerAdapter {
    int mNumOfTabs;

    public SliderAdapter(FragmentManager fm, int NumOfTabs) {
        super(fm);
        this.mNumOfTabs = NumOfTabs;
    }

    @Override
    public Fragment getItem(int position) {

        switch (position) {
            case 0:
                Fragments1 tab1 = new Fragments1();
                return tab1;
            case 1:
                Fragments2 tab2 = new Fragments2();
                return tab2;
            case 2:
                Fragments3 tab3 = new Fragments3();
                return tab3;
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return mNumOfTabs;
    }
}
