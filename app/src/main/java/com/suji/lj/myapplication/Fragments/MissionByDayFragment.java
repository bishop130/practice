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
import com.suji.lj.myapplication.ContactActivity;
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
public class MissionByDayFragment extends Fragment implements RecyclerMissionDayAdapter.OnLoadMissionListListener {

    private RecyclerView recyclerView;
    private SwipeRefreshLayout ly_swipe_refresh;
    private Context mContext;
    private ProgressBar loading_panel;
    private String user_id;
    ArrayList<ItemForMissionByDay> list = new ArrayList<>();
    String key;
    RecyclerMissionDayAdapter adapter;


    public MissionByDayFragment() {


    }


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


        //.setOnMissionDayDisplayListener(this);

        if (isAdded() && getActivity() != null) {
            Log.d("favorite", "isAdd Missionday");
        }

        ly_swipe_refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                //getDatabase();

                FavoriteFragment fa = (FavoriteFragment) getChildFragmentManager().findFragmentByTag("day");
                if (fa != null)
                    fa.refresh();
                //displayData();
                Log.d("들어와", "리프레시해줘");
                ly_swipe_refresh.setRefreshing(false);
                recyclerView.setVisibility(View.VISIBLE);
                loading_panel.setVisibility(View.GONE);


            }
        });

        //displayData();
        String key = DateTimeUtils.getCurrentDateTimeForKey();
        newQueryData(key);

        return view;
    }

    private void displayData() {

        adapter = new RecyclerMissionDayAdapter(mContext, list, this);
        recyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        recyclerView.setAdapter(adapter);
        Log.d("들어", "여기요");
        recyclerView.setVisibility(View.VISIBLE);
        loading_panel.setVisibility(View.GONE);


    }
    private void newQueryData(String key) {

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        //Log.d("데이트타임", DateTimeUtils.getCurrentTime());
        if (!Utils.isEmpty(user_id))
            databaseReference.child("user_data").child(user_id).child("mission_display").orderByChild("date_time").startAt(key).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    list = new ArrayList<>();

                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        ItemForMissionByDay item = snapshot.getValue(ItemForMissionByDay.class);
                        if (item != null) {
                            if (!item.isSuccess()) {
                                list.add(item);
                            }
                        }
                    }

                    update(list);

                    //single인지 multi인지 구분하기

                    Log.d("들어와", "파이어베이스");
                    //addFragment(MissionByDayFragment.newInstance(list));
                }


                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    // missionByDayFragment = new MissionByDayFragment();
                    //addFragment(missionByDayFragment, "day");

                }
            });
    }


    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mContext = context;
    }

    public void update(ArrayList<ItemForMissionByDay> list) {
        Log.d("들어와", "업데이트");
        this.list = list;
        adapter = new RecyclerMissionDayAdapter(mContext, list, this);
        recyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        recyclerView.setAdapter(adapter);
        Log.d("들어와", "여기요");
        recyclerView.setVisibility(View.VISIBLE);
        loading_panel.setVisibility(View.GONE);


    }

    @Override
    public void onLoadMissionList(String mission_id) {

        Log.d("어댑터", mission_id);
        Log.d("어댑터", user_id);

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        databaseReference.child("user_data").child(user_id).child("mission_info_list").orderByChild("mission_id").equalTo(mission_id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for(DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    MissionInfoList item = snapshot.getValue(MissionInfoList.class);
                    if (item != null) {

                        Log.d("어댑터", item.getAddress());
                        Log.d("어댑터", item.getMission_title());
                        Intent intent = new Intent(mContext, MissionDetailActivity.class);
                        intent.putExtra("mission_info_list", item);
                        mContext.startActivity(intent);
                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
}
