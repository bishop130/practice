package com.suji.lj.myapplication.Fragments;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.suji.lj.myapplication.Adapters.MissionRecyclerAdapter;
import com.suji.lj.myapplication.Adapters.RecyclerFriendsSelectedAdapter;
import com.suji.lj.myapplication.Adapters.RecyclerMissionDayAdapter;
import com.suji.lj.myapplication.Adapters.RecyclerPastMissionAdapter;
import com.suji.lj.myapplication.Adapters.RecyclerViewDivider;
import com.suji.lj.myapplication.Items.ItemForMissionByDay;
import com.suji.lj.myapplication.Items.MissionInfoList;
import com.suji.lj.myapplication.MissionDetailActivity;
import com.suji.lj.myapplication.R;
import com.suji.lj.myapplication.Utils.DateTimeUtils;
import com.suji.lj.myapplication.Utils.Utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;

/**
 * A simple {@link Fragment} subclass.
 */
public class MissionByDayFragment extends Fragment {

    private RecyclerView recyclerView;
    private SwipeRefreshLayout ly_swipe_refresh;
    private Context mContext;
    private ProgressBar loading_panel;
    private String user_id;
    String key;
    public MissionByDayFragment(String key) {
        // Required empty public constructor
        this.key = key;
    }
    public MissionByDayFragment(){}


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_mission_by_day, container, false);
        recyclerView = view.findViewById(R.id.recyclerView);
        ly_swipe_refresh = view.findViewById(R.id.ly_swipe_refresh);
        loading_panel = view.findViewById(R.id.loadingPanel);
        user_id = mContext.getSharedPreferences("Kakao", MODE_PRIVATE).getString("token", "");
        recyclerView.addItemDecoration(new RecyclerViewDivider(28));
        ly_swipe_refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                if(!TextUtils.isEmpty(key)) {
                    getDatabase();
                    ly_swipe_refresh.setRefreshing(false);
                }else{
                    recyclerView.setVisibility(View.VISIBLE);
                    loading_panel.setVisibility(View.GONE);
                    ly_swipe_refresh.setRefreshing(false);
                }

            }
        });
        if(!TextUtils.isEmpty(key)) {
            getDatabase();
        }else{
            recyclerView.setVisibility(View.VISIBLE);
            loading_panel.setVisibility(View.GONE);
        }



        return view;
    }



    private void getDatabase(){
        FirebaseDatabase.getInstance().getReference().child("user_data").child(user_id).child("mission_display").orderByKey().startAt(key).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<ItemForMissionByDay> list = new ArrayList<>();
                for (DataSnapshot data : dataSnapshot.getChildren()) {
                    ItemForMissionByDay item = data.getValue(ItemForMissionByDay.class);
                    list.add(item);
                    Log.d("아이템바이", data.getKey());
                }
                RecyclerMissionDayAdapter adapter = new RecyclerMissionDayAdapter(mContext,list);
                recyclerView.setLayoutManager(new LinearLayoutManager(mContext));
                recyclerView.setAdapter(adapter);
                Log.d("에러","여기요");
                recyclerView.setVisibility(View.VISIBLE);
                loading_panel.setVisibility(View.GONE);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

                Log.d("에러","에런가");
            }
        });




    }



    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        Log.d("홈프레그", "onAttach_SearchFragment");
        mContext = context;

    }


}
