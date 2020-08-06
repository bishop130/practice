package com.suji.lj.myapplication.Fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import com.suji.lj.myapplication.Items.ItemForNotification;
import com.suji.lj.myapplication.MainActivity;
import com.suji.lj.myapplication.MissionDetailActivity;
import com.suji.lj.myapplication.MissionDetailMultiActivity;
import com.suji.lj.myapplication.R;
import com.suji.lj.myapplication.Utils.Account;
import com.suji.lj.myapplication.Utils.BadgeManager;
import com.suji.lj.myapplication.Utils.DateTimeFormatter;
import com.suji.lj.myapplication.Utils.Utils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class NotificationFragment extends Fragment {

    /**
     * (싱글,멀티)본인포함 친구 도착 or 실패 알림 = 100  기본 폼
     * (멀티)멀티약속 초대 = 200
     **/

    Toolbar toolbar;
    RecyclerView recyclerView;
    String user_id;
    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
    Activity activity;
    List<ItemForNotification> list;
    NotificationAdapter adapter;
    RelativeLayout rl_is_empty;
    ProgressBar progressBar;
    AlertDialog loading_dialog;


    private ItemTouchHelper.SimpleCallback simpleItemTouchCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {

        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            //showToast("on Move");
            return true;
        }

        @Override
        public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDir) {

            final int position = viewHolder.getAdapterPosition();


            String key = list.get(position).getKey();

            databaseReference.child("user_data").child(user_id).child("notification").child(key).removeValue();

        }
    };


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_notification, container, false);

        toolbar = view.findViewById(R.id.toolbar);
        recyclerView = view.findViewById(R.id.recyclerView);
        rl_is_empty = view.findViewById(R.id.rl_is_empty);
        progressBar = view.findViewById(R.id.loadingPanel);

        progressBar.setVisibility(View.VISIBLE);

        Utils.drawRecyclerViewDivider(activity, recyclerView);

        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setCancelable(false); // if you want user to wait for some process to finish,
        builder.setView(R.layout.layout_progress);
        loading_dialog = builder.create();

        if (loading_dialog.getWindow() != null) {
            loading_dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        }

        user_id = Account.getUserId(activity);
        databaseReference.child("user_data").child(user_id).child("notification").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                list = new ArrayList<>();

                if (dataSnapshot.exists()) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        ItemForNotification item = snapshot.getValue(ItemForNotification.class);
                        if (item != null) {
                            item.setKey(snapshot.getKey());
                            Log.d("알림", item.getMission_id());
                            list.add(item);
                            snapshot.getRef().child("read").setValue(true);
                        }
                    }
                    rl_is_empty.setVisibility(View.GONE);
                    setRecyclerView(list);
                } else {
                    rl_is_empty.setVisibility(View.VISIBLE);
                    setRecyclerView(list);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        toolbar.setTitle("알림");

        BadgeManager.hideBottomNavigationViewBadge(((MainActivity) activity).bottomNavigationView, 2);


        return view;
    }


    private void setRecyclerView(List<ItemForNotification> list) {


        progressBar.setVisibility(View.GONE);

        adapter = new NotificationAdapter(list);
        recyclerView.setLayoutManager(new LinearLayoutManager(activity));
        recyclerView.setAdapter(adapter);

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);

    }


    private class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.ViewHolder> {


        List<ItemForNotification> list;

        private NotificationAdapter(List<ItemForNotification> list) {
            this.list = list;
        }


        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            View view = inflater.inflate(R.layout.item_notification, parent, false);


            return new NotificationAdapter.ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

            int code = list.get(position).getNotification_code();
            String friend_name = list.get(position).getFriend_name();
            String title = list.get(position).getTitle();
            String friend_image = list.get(position).getFriend_image();
            String date_time = list.get(position).getDate_time();
            String mission_id = list.get(position).getMission_id();
            boolean is_single = list.get(position).isSingle();
            Date date = DateTimeFormatter.dateParser(date_time, "yyyyMMddHHmmss");
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM월 dd일 a hh시 mm분", Locale.KOREA);
            String timeformat = simpleDateFormat.format(date);


            Log.d("알림", code + "");
            holder.iv_notification.setBackground(new ShapeDrawable(new OvalShape()));
            holder.iv_notification.setClipToOutline(true);


            switch (code) {
                case 100: /** 친구도착 알림**/
                    String content = friend_name + " 님 도착 성공!";
                    holder.tv_content.setText(content);
                    holder.tv_date_time.setText(timeformat);
                    holder.tv_title.setText(title);
                    if (friend_image != null && !friend_image.isEmpty()) {
                        Log.d("알림", "여기들어와?" + friend_name);
                        Picasso.with(activity)
                                .load(friend_image)
                                .error(R.drawable.default_profile)
                                .fit()
                                .into(holder.iv_notification);
                    } else {
                        Log.d("알림", "여기들어와?" + friend_name);
                        holder.iv_notification.setImageDrawable(activity.getDrawable(R.drawable.default_profile));


                    }
                    holder.ly_container.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            loading_dialog.show();
                            if (is_single) {
                                Log.d("알림", "싱글" + is_single);
                                Intent intent = new Intent(activity, MissionDetailActivity.class);
                                intent.putExtra("mission_id", mission_id);
                                intent.putExtra("mission_mode", true);
                                startActivity(intent);
                            } else {
                                Log.d("알림", "멀티" + is_single);
                                Intent intent = new Intent(activity, MissionDetailMultiActivity.class);
                                intent.putExtra("mission_id", mission_id);
                                intent.putExtra("mission_mode", false);
                                startActivity(intent);


                            }


                        }
                    });


                    break;
                case 200:/** 친구초대 알림**/
                    String invitation_conent = friend_name + "님이 " + title + "에 초대하셨습니다.";
                    holder.tv_content.setText(invitation_conent);
                    holder.tv_date_time.setText(timeformat);
                    holder.tv_title.setText(title);
                    if (friend_image != null && !friend_image.isEmpty()) {
                        Picasso.with(activity)
                                .load(friend_image)
                                .error(R.drawable.default_profile)
                                .fit()
                                .into(holder.iv_notification);
                    }
                    holder.ly_container.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            loading_dialog.show();

                        }
                    });

                    break;
                case 300:

                    break;


            }


        }

        @Override
        public int getItemCount() {
            return list.size();
        }

        private class ViewHolder extends RecyclerView.ViewHolder {

            TextView tv_content;
            TextView tv_date_time;
            ImageView iv_notification;
            LinearLayout ly_container;
            TextView tv_title;
            ImageView iv_menu;

            private ViewHolder(@NonNull View itemView) {
                super(itemView);

                tv_content = itemView.findViewById(R.id.tv_content);
                tv_date_time = itemView.findViewById(R.id.tv_date_time);
                iv_notification = itemView.findViewById(R.id.iv_notification);
                ly_container = itemView.findViewById(R.id.ly_container);
                tv_title = itemView.findViewById(R.id.tv_title);


            }
        }
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof Activity) {
            this.activity = (Activity) context;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (loading_dialog.isShowing()) {
            loading_dialog.dismiss();

        }
    }
}
