package com.suji.lj.myapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.tabs.TabLayout;
import com.suji.lj.myapplication.Adapters.FriendPagerAdapter;
import com.suji.lj.myapplication.Adapters.WithDrawPagerAdapter;
import com.suji.lj.myapplication.Fragments.EventModeFragment;
import com.suji.lj.myapplication.Fragments.MultiModeFragment;
import com.suji.lj.myapplication.Fragments.SingleModeFragment;
import com.suji.lj.myapplication.Items.MissionCartItem;

import io.realm.Realm;

public class WithDrawActivity extends AppCompatActivity {


    TabLayout tabLayout;
    ViewPager view_pager;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_with_draw);



        tabLayout = findViewById(R.id.tab_ly);
        toolbar = findViewById(R.id.toolbar);



        toolbar.setTitle("포인트 인출");
        setSupportActionBar(toolbar);
        if(getSupportActionBar()!=null) {

            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }


        tabLayout.addTab(tabLayout.newTab().setText("인출"));
        tabLayout.addTab(tabLayout.newTab().setText("최근내역"));

        view_pager = findViewById(R.id.view_pager);

        WithDrawPagerAdapter adapter = new WithDrawPagerAdapter
                (getSupportFragmentManager(), tabLayout.getTabCount());
        view_pager.setAdapter(adapter);
        view_pager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));


        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                switch (tab.getPosition()){
                    case 0:
                        view_pager.setCurrentItem(tab.getPosition());

                        break;
                    case 1:
                        view_pager.setCurrentItem(tab.getPosition());
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
