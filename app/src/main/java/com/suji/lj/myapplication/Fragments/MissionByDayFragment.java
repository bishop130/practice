package com.suji.lj.myapplication.Fragments;


import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.UiThread;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.paging.PagedList;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.paging.DatabasePagingOptions;
import com.firebase.ui.database.paging.FirebaseRecyclerPagingAdapter;
import com.firebase.ui.database.paging.LoadingState;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import com.suji.lj.myapplication.Adapters.RecyclerBankSelectionAdapter;
import com.suji.lj.myapplication.Adapters.RecyclerMissionDayAdapter;
import com.suji.lj.myapplication.Adapters.RecyclerViewDivider;
import com.suji.lj.myapplication.Items.ItemForBank;
import com.suji.lj.myapplication.Items.ItemForFriendByDay;
import com.suji.lj.myapplication.Items.ItemForMissionByDay;
import com.suji.lj.myapplication.Items.ItemForMultiKey;
import com.suji.lj.myapplication.Items.MissionInfoList;
import com.suji.lj.myapplication.MainActivity;
import com.suji.lj.myapplication.MissionDetailActivity;
import com.suji.lj.myapplication.MissionDetailMultiActivity;
import com.suji.lj.myapplication.R;
import com.suji.lj.myapplication.SingleModeActivity;
import com.suji.lj.myapplication.Utils.Account;
import com.suji.lj.myapplication.Utils.DateTimeUtils;
import com.suji.lj.myapplication.Utils.Utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;

/**
 * A simple {@link Fragment} subclass.
 */
public class MissionByDayFragment extends Fragment implements RecyclerMissionDayAdapter.OnLoadPreviewBottomSheetListener {

    private RecyclerView recyclerView;
    private SwipeRefreshLayout ly_swipe_refresh;
    private ProgressBar loading_panel;
    private String user_id;
    List<ItemForMissionByDay> singleList = new ArrayList<>();
    RecyclerMissionDayAdapter adapter;
    TextView tv_add_mission;
    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
    Activity activity;
    RelativeLayout rl_is_empty;

    int numOfData = 0;
    boolean isFetched = false;
    ValueEventListener listener = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            Log.d("멀티", "day 콜");
            if (dataSnapshot.exists()) {
                if(!isFetched) {
                    isFetched = true;
                    singleList = new ArrayList<>();
                    numOfData = (int) dataSnapshot.getChildrenCount();
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        Boolean isSingle = snapshot.child("singleMode").getValue(Boolean.class);
                        if (!isSingle) {
                            String missionId = snapshot.child("missionId").getValue(String.class);
                            String dateTime = snapshot.child("dateTime").getValue(String.class);
                            databaseReference.child("multi_data").orderByChild("missionId").equalTo(missionId).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    if (snapshot.exists()) {
                                        for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                                            snapshot1.getRef().child("missionDisplay").orderByChild("dateTime").equalTo(dateTime).addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                    if (snapshot.exists()) {
                                                        for (DataSnapshot shot : snapshot.getChildren()) {
                                                            ItemForMissionByDay day = shot.getValue(ItemForMissionByDay.class);

                                                            if (day != null) {
                                                                Log.d("멀티", day.getTitle() + " 밸류3");
                                                                singleList.add(day);


                                                            }


                                                        }
                                                    }

                                                    update();
                                                    //rl_is_empty.setVisibility(View.GONE);

                                                }

                                                @Override
                                                public void onCancelled(@NonNull DatabaseError error) {

                                                }
                                            });


                                        }
                                    }

                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });


                        } else {
                            ItemForMissionByDay item = snapshot.getValue(ItemForMissionByDay.class);
                            if (item != null) {
                                singleList.add(item);

                            }
                        }


                    }

                    update();
                    rl_is_empty.setVisibility(View.GONE);
                }else{
                    Log.d("멀티", "추가호출막기");
                    //rl_is_empty.setVisibility(View.GONE);
                    //query.removeEventListener(this);
                }


            } else {
                isFetched = true;
                numOfData = 0;
                singleList = new ArrayList<>();
                /** 데이터 없음**/
                update();
                rl_is_empty.setVisibility(View.VISIBLE);


            }

        }

        @Override
        public void onCancelled(@NonNull DatabaseError error) {
            loading_panel.setVisibility(View.GONE);
            Toast.makeText(activity, "데이터를 불러오는데 실패했습니다.", Toast.LENGTH_SHORT).show();
        }
    };
    Query query;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_mission_by_day, container, false);
        recyclerView = view.findViewById(R.id.rv_day);
        ly_swipe_refresh = view.findViewById(R.id.ly_swipe_refresh);
        loading_panel = view.findViewById(R.id.loadingPanel);
        rl_is_empty = view.findViewById(R.id.rl_is_empty);
        tv_add_mission = view.findViewById(R.id.tv_add_mission);

        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setCancelable(false); // if you want user to wait for some process to finish,
        builder.setView(R.layout.layout_progress);

        user_id = Account.getUserId(activity);
        //recyclerView.addItemDecoration(new RecyclerViewDivider(28));
        Utils.drawRecyclerViewDivider(activity, recyclerView);
        ly_swipe_refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                isFetched = false;
                querySingleData();
                //fa.refresh();
                //displayData();

                Log.d("들어와", "리프레시해줘");
                ly_swipe_refresh.setRefreshing(false);
                //recyclerView.setVisibility(View.VISIBLE);
                loading_panel.setVisibility(View.GONE);


            }
        });

        tv_add_mission.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                int isLocationGranted = ContextCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION);
                if (isLocationGranted == PackageManager.PERMISSION_GRANTED) {
                    ((MainActivity) activity).showLoadingDialog();
                    Intent intent = new Intent(activity, SingleModeActivity.class);
                    activity.startActivity(intent);


                } else {
                    Toast.makeText(activity, "위치권한설정이 필요합니다.", Toast.LENGTH_SHORT).show();


                }


            }
        });


        loading_panel.setVisibility(View.VISIBLE);


        //querySingleData();

        Log.d("데이", "oncreateview");


        return view;
    }


    private void querySingleData() {
        query =databaseReference.child("user_data").child(user_id).child("missionDisplay");
        query.addValueEventListener(listener);
        //String key = DateTimeUtils.getCurrentDateTimeForKey();

    }


    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof Activity) {
            this.activity = (Activity) context;
            Log.d("데이", "onAtacched");
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        isFetched = false;
        querySingleData();
        Log.d("차일드", "onResume");
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d("데이", "onPause");
    }

    private void update() {
        Log.d("멀티", "업데이트");

        if (singleList.size() == numOfData) {
            loading_panel.setVisibility(View.GONE);

            //query.removeEventListener(listener);

            Log.d("멀티", singleList.size() + " 리스트사이즈");
            //singleList.addAll(multiList);
            Collections.sort(singleList, new Comparator<ItemForMissionByDay>() {
                @Override
                public int compare(ItemForMissionByDay o1, ItemForMissionByDay o2) {
                    return o1.getDateTime().compareTo(o2.getDateTime());
                }
            });

            isFetched = false;
            Log.d("멀티", singleList.size() + " 필터완료");
            if (singleList.size() == 0) {
                rl_is_empty.setVisibility(View.VISIBLE);

            }


            adapter = new RecyclerMissionDayAdapter(activity, singleList, MissionByDayFragment.this);
            recyclerView.setLayoutManager(new LinearLayoutManager(activity));
            recyclerView.setAdapter(adapter);
            adapter.notifyDataSetChanged();
            // Stuff that updates the UI


        }


    }

    @Override
    public void onLoadPreviewBottomSheet(ItemForMissionByDay itemForMissionByDay) {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(activity);


        View view = LayoutInflater.from(activity).inflate(R.layout.dialog_day_preview, null);
        RecyclerView rv_day_preview = view.findViewById(R.id.rv_day_preview);
        TextView tvDetail = view.findViewById(R.id.tvDetail);
        // List<ItemForBank> bankList = new ArrayList<>();

        String missionId = itemForMissionByDay.getMissionId();
        String date = itemForMissionByDay.getDate();
        String dateTime = itemForMissionByDay.getDateTime();
        Log.d("바텀", missionId + "missionId");
        Log.d("바텀", date + "date");
        Log.d("바텀", dateTime + "dateTime");

        ((MainActivity) activity).dismissLoadingDialog();

        databaseReference.child("multi_data").orderByChild("missionId").equalTo(missionId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {

                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        snapshot.getRef().child("missionDisplay").orderByChild("dateTime").equalTo(dateTime).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if (snapshot.exists()) {
                                    for (DataSnapshot shot : snapshot.getChildren()) {
                                        GenericTypeIndicator<List<ItemForFriendByDay>> t = new GenericTypeIndicator<List<ItemForFriendByDay>>() {
                                        };
                                        List<ItemForFriendByDay> friendByDayList = shot.child("friendByDayList").getValue(t);


                                        FriendByDayAdapter adapter = new FriendByDayAdapter(friendByDayList);
                                        rv_day_preview.setLayoutManager(new LinearLayoutManager(activity));
                                        rv_day_preview.setAdapter(adapter);


                                        bottomSheetDialog.setContentView(view);
                                        bottomSheetDialog.show();


                                    }

                                }

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });


                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        tvDetail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                ((MainActivity) activity).showLoadingDialog();


                Intent intent = new Intent(activity, MissionDetailMultiActivity.class);
                intent.putExtra("missionId", missionId);
                startActivity(intent);
                bottomSheetDialog.dismiss();
            }
        });
        Utils.drawRecyclerViewDivider(activity, rv_day_preview);


    }

    private class FriendByDayAdapter extends RecyclerView.Adapter<FriendByDayAdapter.ViewHolder> {


        List<ItemForFriendByDay> friendByDayList;

        public FriendByDayAdapter(List<ItemForFriendByDay> friendByDayList) {
            this.friendByDayList = friendByDayList;


        }

        @NonNull
        @Override
        public FriendByDayAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            View view = inflater.inflate(R.layout.item_friend_by_day, parent, false);


            return new FriendByDayAdapter.ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull FriendByDayAdapter.ViewHolder holder, int position) {
            String friend_name = friendByDayList.get(position).getFriendName();
            String friend_image = friendByDayList.get(position).getFriendImage();
            String dateTime = friendByDayList.get(position).getTimeStamp();

            // holder.tv_time.setText(DateTimeUtils.makeTimeForHumanInt(hour, min));
            holder.tv_friend_name.setText(friend_name);
            Log.d("멀티", friend_image);

            if (dateTime != null && !dateTime.isEmpty()) {
                String time = DateTimeUtils.makeTimeForHuman(dateTime, "yyyyMMddHHmmss");
                holder.tv_time.setText(time);

            } else {
                holder.tv_time.setText("도착정보없음");
            }


            holder.iv_friend_image.setBackground(new ShapeDrawable(new OvalShape()));
            holder.iv_friend_image.setClipToOutline(true);

            if (friend_image != null && !friend_image.isEmpty()) {
                Picasso.with(activity)
                        .load(friend_image)
                        .fit()
                        .into(holder.iv_friend_image);
            }


        }

        @Override
        public int getItemCount() {
            return friendByDayList.size();
        }

        private class ViewHolder extends RecyclerView.ViewHolder {

            TextView tv_friend_name;
            ImageView iv_friend_image;
            TextView tv_time;


            private ViewHolder(@NonNull View itemView) {
                super(itemView);

                tv_friend_name = itemView.findViewById(R.id.tv_friend_name);
                iv_friend_image = itemView.findViewById(R.id.iv_friend_image);
                tv_time = itemView.findViewById(R.id.tv_time);

            }
        }
    }


}
