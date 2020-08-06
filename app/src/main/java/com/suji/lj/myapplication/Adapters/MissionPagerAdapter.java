package com.suji.lj.myapplication.Adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.suji.lj.myapplication.Fragments.MissionByDayFragment;
import com.suji.lj.myapplication.Fragments.MissionByMissionFragment;

public class MissionPagerAdapter extends FragmentStatePagerAdapter {

    int tab_size;

    public MissionPagerAdapter(@NonNull FragmentManager fm, int behavior) {
        super(fm, behavior);
        this.tab_size = behavior;
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new MissionByDayFragment();
            case 1:
                return new MissionByMissionFragment();
            default:
                return new MissionByDayFragment();
        }
    }

    @Override
    public int getCount() {
        return tab_size;
    }
}
