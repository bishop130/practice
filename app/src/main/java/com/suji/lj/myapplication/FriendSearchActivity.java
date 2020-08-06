package com.suji.lj.myapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.tabs.TabLayout;
import com.suji.lj.myapplication.Adapters.FriendPagerAdapter;
import com.suji.lj.myapplication.Adapters.MissionPagerAdapter;

public class FriendSearchActivity extends AppCompatActivity {


    Toolbar toolbar;
    TabLayout tab_ly;
    ViewPager pager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_search);


        toolbar = findViewById(R.id.toolbar);
        tab_ly = findViewById(R.id.ly_tab);
        pager = findViewById(R.id.fragment_pager);


        toolbar.setTitle("친구추가");
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {

            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }


        tab_ly.addTab(tab_ly.newTab().setText("친구찾기"));
        tab_ly.addTab(tab_ly.newTab().setText("받은요청"));
        tab_ly.addTab(tab_ly.newTab().setText("보낸요청"));


        FriendPagerAdapter adapter = new FriendPagerAdapter
                (getSupportFragmentManager(), tab_ly.getTabCount());
        pager.setOffscreenPageLimit(3);
        pager.setAdapter(adapter);
        pager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tab_ly));


        toolbar.setTitle("친구추가");


        tab_ly.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                switch (tab.getPosition()) {
                    case 0:
                        pager.setCurrentItem(tab.getPosition());

                        break;
                    case 1:
                        pager.setCurrentItem(tab.getPosition());
                        break;

                    case 2:
                        pager.setCurrentItem(tab.getPosition());
                        break;
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {


            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: { //toolbar의 back키 눌렀을 때 동작
                finish();
                return true;
            }


        }
        return super.onOptionsItemSelected(item);


    }


}
