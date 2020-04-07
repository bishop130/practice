package com.suji.lj.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ScrollView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.format.TitleFormatter;
import com.suji.lj.myapplication.Adapters.RecyclerInvitationDateTimeAdapter;
import com.suji.lj.myapplication.Adapters.RecyclerInvitationFriendAdapter;
import com.suji.lj.myapplication.Fragments.CalendarSelectFragment;
import com.suji.lj.myapplication.Fragments.CalendarShowFragment;
import com.suji.lj.myapplication.Fragments.MapFragment;
import com.suji.lj.myapplication.Items.ItemForDateTime;
import com.suji.lj.myapplication.Items.ItemForFriendResponseForRequest;
import com.suji.lj.myapplication.Items.ItemForMultiModeRequest;
import com.suji.lj.myapplication.Utils.Account;

import net.daum.mf.map.api.MapCircle;
import net.daum.mf.map.api.MapPoint;

import java.util.List;

public class InvitationInfoActivity extends AppCompatActivity {


    RecyclerView recyclerView;
    Toolbar toolbar;

    NestedScrollView scrollView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invitation_info);
        recyclerView = findViewById(R.id.recycler_friend);
        toolbar = findViewById(R.id.toolbar);

        scrollView = findViewById(R.id.scroll_view);

        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        String user_id = Account.getUserId(this);
        Bundle extras = getIntent().getExtras();

        if (extras != null) {


            String key = extras.getString("key", "");

            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
            databaseReference.child("user_data").child(user_id).child("invitation").child(key).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {


                    ItemForMultiModeRequest item = dataSnapshot.getValue(ItemForMultiModeRequest.class);
                    if (item != null) {
                        String title = item.getTitle();
                        List<ItemForFriendResponseForRequest> list = item.getFriendRequestList();
                        setupRecyclerView(list);
                        toolbar.setTitle(title);
                        addCalendarFragment(new CalendarShowFragment(getApplicationContext(), item.getCalendarDayList()));
                        addFragment(new MapFragment(scrollView, item.getLat(), item.getLng()));



                    }


                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }



    }

    private void setupRecyclerView(List<ItemForFriendResponseForRequest> list) {
        RecyclerInvitationFriendAdapter adapter = new RecyclerInvitationFriendAdapter(this, list);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        recyclerView.setAdapter(adapter);


    }


    private void addFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.map_fragment, fragment).commit();
    }
    private void  addCalendarFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.calendar_layout, fragment).commit();
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
