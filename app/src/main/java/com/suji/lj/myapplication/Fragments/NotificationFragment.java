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
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.ChildEventListener;
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
import com.suji.lj.myapplication.Utils.Code;
import com.suji.lj.myapplication.Utils.DateTimeFormatter;
import com.suji.lj.myapplication.Utils.Utils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import static com.suji.lj.myapplication.Utils.Code.ACCEPT_INVITATION;
import static com.suji.lj.myapplication.Utils.Code.ARRIVE_SUCCESS;
import static com.suji.lj.myapplication.Utils.Code.INVITE_FRIEND;
import static com.suji.lj.myapplication.Utils.Code.INVITE_MISSION_CANCEL;
import static com.suji.lj.myapplication.Utils.Code.INVITE_MISSION_START;
import static com.suji.lj.myapplication.Utils.Code.MISSION_MADE;
import static com.suji.lj.myapplication.Utils.Code.MISSION_OVER;
import static com.suji.lj.myapplication.Utils.Code.MISSION_START;
import static com.suji.lj.myapplication.Utils.Code.POINT_REFUND;
import static com.suji.lj.myapplication.Utils.Code.POINT_REWARD_DISTRIBUTION;
import static com.suji.lj.myapplication.Utils.Code.POINT_TO_CASH;
import static com.suji.lj.myapplication.Utils.Code.RECEIVED_INVITATION;
import static com.suji.lj.myapplication.Utils.Code.SEND_INVITATION;

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


    //200 약속생성알림
    //201 약속초대알림
    //202 초대약속취소
    //203 초대약속시작


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
            list.remove(position);
            adapter.notifyItemRemoved(position);


            databaseReference.child("user_data").child(user_id).child("notification").child(key).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {

                }
            });
            //adapter.notifyDataSetChanged();

        }

    };

    boolean isFetched = false;
    HashMap<String,Object> hashMap;

    ValueEventListener singleEventListener = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot snapshot) {

            hashMap =  new HashMap<>();
            list = new ArrayList<>();
            if (snapshot.exists()) {
                for(DataSnapshot shot : snapshot.getChildren()) {
                    String key = shot.getKey();

                    ItemForNotification item = shot.getValue(ItemForNotification.class);
                    if (item != null) {
                        item.setKey(shot.getKey());
                        item.setRead(true);
                        Log.d("알림", item.getMissionId());
                        hashMap.put(key,item);
                        list.add(item);
                        //shot.getRef().child("read").setValue(true);
                    }
                }
                snapshot.getRef().updateChildren(hashMap);

                rl_is_empty.setVisibility(View.GONE);
                progressBar.setVisibility(View.GONE);
                //loading_dialog.dismiss();
                setRecyclerView(list);
            } else {
                Log.d("차일드", "none");
                //loading_dialog.dismiss();
                rl_is_empty.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.GONE);
            }
            //databaseReference.child("user_data").child(user_id).child("notification").addValueEventListener(valueEventListener);
        }

        @Override
        public void onCancelled(@NonNull DatabaseError error) {

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
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);

        user_id = Account.getUserId(activity);
        Log.d("차일드", "data");
        databaseReference.child("user_data").child(user_id).child("notification").addListenerForSingleValueEvent(singleEventListener);




        toolbar.setTitle("알림");




        return view;
    }

    private void setRecyclerView(List<ItemForNotification> list) {


        progressBar.setVisibility(View.GONE);

        adapter = new NotificationAdapter(list);
        recyclerView.setLayoutManager(new LinearLayoutManager(activity));
        recyclerView.setAdapter(adapter);


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

            int code = list.get(position).getCode();
            String friend_name = list.get(position).getFriendName();
            String title = list.get(position).getTitle();
            String friend_image = list.get(position).getFriendImage();
            String date_time = list.get(position).getDateTime();
            String mission_id = list.get(position).getMissionId();
            boolean is_single = list.get(position).isSingle();
            String content = list.get(position).getContent();
            Date date = DateTimeFormatter.dateParser(date_time, "yyyyMMddHHmmss");
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM월 dd일 a hh시 mm분", Locale.KOREA);
            String timeformat = simpleDateFormat.format(date);


            Log.d("차일드", code + "");
            holder.iv_notification.setBackground(new ShapeDrawable(new OvalShape()));
            holder.iv_notification.setClipToOutline(true);


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


                    switch (code) {
                        case ARRIVE_SUCCESS: /** 친구도착 알림**/
                            loading_dialog.show();
                            if (is_single) {
                                Log.d("알림", "싱글" + is_single);
                                Intent intent = new Intent(activity, MissionDetailActivity.class);
                                intent.putExtra("missionId", mission_id);
                                intent.putExtra("missionMode", true);
                                startActivity(intent);
                            } else {
                                Log.d("알림", "멀티" + is_single);
                                Intent intent = new Intent(activity, MissionDetailMultiActivity.class);
                                intent.putExtra("missionId", mission_id);
                                intent.putExtra("missionMode", false);
                                startActivity(intent);


                            }



                            break;
                        case INVITE_FRIEND:/** 친구초대 알림**/


                            break;
                        case MISSION_MADE:
                            /** 약속 등록 알림**/


                            break;
                        case POINT_REFUND:
                            /** 환불**/

                            break;
                        case POINT_TO_CASH:
                            break;
                        case SEND_INVITATION:

                            break;
                        case RECEIVED_INVITATION:

                            break;

                        case ACCEPT_INVITATION:

                            break;
                        case INVITE_MISSION_START:

                            break;
                        case INVITE_MISSION_CANCEL:

                            break;
                        case MISSION_START:

                            break;
                        case MISSION_OVER:

                            break;

                        case POINT_REWARD_DISTRIBUTION:

                            break;
                    }



                }
            });


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
