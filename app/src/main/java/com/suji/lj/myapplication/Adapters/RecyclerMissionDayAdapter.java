package com.suji.lj.myapplication.Adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.api.client.util.DateTime;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import com.suji.lj.myapplication.Items.ItemForFriendByDay;
import com.suji.lj.myapplication.Items.ItemForMissionByDay;
import com.suji.lj.myapplication.MissionDetailActivity;
import com.suji.lj.myapplication.MissionDetailMultiActivity;
import com.suji.lj.myapplication.R;
import com.suji.lj.myapplication.Utils.Account;
import com.suji.lj.myapplication.Utils.DateTimeFormatter;
import com.suji.lj.myapplication.Utils.DateTimeUtils;
import com.suji.lj.myapplication.Utils.OnSingleClickListener;
import com.suji.lj.myapplication.Utils.PreciseCountdown;
import com.suji.lj.myapplication.Utils.Utils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class RecyclerMissionDayAdapter extends RecyclerView.Adapter<RecyclerMissionDayAdapter.ItemViewHolder> {

    List<ItemForMissionByDay> list;
    Activity context;
    private boolean isTimerRunning = false;
    private PreciseCountdown timer;
    Calendar calendar = Calendar.getInstance();
    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
    String user_id;
    OnLoadPreviewBottomSheetListener listener;
    AlertDialog alertDialog;


    public RecyclerMissionDayAdapter(Activity context, List<ItemForMissionByDay> list, OnLoadPreviewBottomSheetListener listener, AlertDialog alertDialog) {
        this.list = list;
        this.context = context;
        this.listener = listener;
        this.alertDialog = alertDialog;


    }


    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        view = inflater.inflate(R.layout.main_recycler_view_item, parent, false);

        RecyclerMissionDayAdapter.ItemViewHolder viewHolder = new RecyclerMissionDayAdapter.ItemViewHolder(view);
        user_id = Account.getUserId(context);


        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {

        String title = list.get(position).getTitle();
        String address = list.get(position).getAddress();
        String date = list.get(position).getDate();
        String time = list.get(position).getTime();
        boolean isSingle = list.get(position).isSingle_mode();
        String mission_id = list.get(position).getMission_id();
        String date_time = list.get(position).getDate_time();

        holder.tv_missionTitle.setText(title);
        holder.tv_address.setText(address);
        holder.tv_date.setText(DateTimeUtils.makeDateForHuman(date));
        holder.tv_time.setText(DateTimeUtils.makeTimeForHuman(time, "HHmm"));
        if (!isSingle) {
            holder.tv_mode.setText("멀티");

        } else {
            holder.tv_mode.setText("싱글");
        }

        if (position == 0) {


            holder.tvTimeLeft.setVisibility(View.VISIBLE);

            Date endDate = DateTimeFormatter.dateTimeParser(date_time);
            Date curDate = new Date(System.currentTimeMillis());

            if (!isSingle) {

                FriendAdapter adapter = new FriendAdapter(list.get(position).getFriendByDayList());
                holder.rv_friend_image.setLayoutManager(new LinearLayoutManager(context, RecyclerView.HORIZONTAL, false));
                holder.rv_friend_image.setAdapter(adapter);


            }


            long diff = endDate.getTime() - curDate.getTime();
            if (isTimerRunning) {
                timer.cancel();
            }


            Log.d("싱글", diff + "");

            if (diff >= 0) {

                timer = new PreciseCountdown(diff, 1000) {
                    @Override
                    public void onTick(long l) {
                        isTimerRunning = true;
                        long days = TimeUnit.MILLISECONDS.toDays(l);
                        long remainingHoursInMillis = l - TimeUnit.DAYS.toMillis(days);
                        long hours = TimeUnit.MILLISECONDS.toHours(remainingHoursInMillis);
                        long remainingMinutesInMillis = remainingHoursInMillis - TimeUnit.HOURS.toMillis(hours);
                        long minutes = TimeUnit.MILLISECONDS.toMinutes(remainingMinutesInMillis);
                        long remainingSecondsInMillis = remainingMinutesInMillis - TimeUnit.MINUTES.toMillis(minutes);
                        long seconds = TimeUnit.MILLISECONDS.toSeconds(remainingSecondsInMillis);
                        //Log.d("타이머", "position" + position + "   " + holder.getAdapterPosition() + "time:" + l + " " + timer_switch);
                        context.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (days < 2) {
                                    if (l < 10 * 60 * 1000) { //10분
                                        String time2 = minutes + "분 " + seconds + "초 이후 종료";
                                        holder.tvTimeLeft.setText(time2);
                                    } else {
                                        String time = hours + "시간 " + minutes + "분 이후 종료";

                                        holder.tvTimeLeft.setText(time);
                                    }


                                } else {
                                    String time = days + "일 " + hours + "시간 이후 종료";

                                    holder.tvTimeLeft.setText(time);


                                }
                            }
                        });


                    }

                    @Override
                    public void onFinished() {
                        isTimerRunning = false;

                        /** 시간이 지나면 mission_display 삭제**/
                        databaseReference.child("user_data").child(user_id).child("mission_display").orderByChild("date_time").equalTo(date_time).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if (dataSnapshot.exists()) {
                                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                        ItemForMissionByDay item = snapshot.getValue(ItemForMissionByDay.class);
                                        if (item != null) {
                                            if (mission_id.equals(item.getMission_id())) {
                                                snapshot.getRef().removeValue();

                                            }


                                        }


                                    }


                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });


                    }
                };


                timer.start();


            }


        } else {
            Log.d("싱글", "뒤포지션");
            holder.tvTimeLeft.setVisibility(View.GONE);
        }
        holder.view_container.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {

                alertDialog.show();
                if (isSingle) {


                    Intent intent = new Intent(context, MissionDetailActivity.class);
                    intent.putExtra("mission_id", mission_id);
                    intent.putExtra("mission_mode", isSingle);
                    context.startActivity(intent);

                } else {
                    listener.onLoadPreviewBottomSheet(list.get(0));


                }


            }
        });


    }

    @Override
    public int getItemCount() {
        if (list == null) {
            return 0;

        }
        return list.size();
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    public static class ItemViewHolder extends RecyclerView.ViewHolder {

        TextView tv_missionTitle;
        TextView tv_date;
        TextView tv_time;
        TextView tv_address;
        TextView tvTimeLeft;
        RecyclerView rv_friend_image;

        LinearLayout view_container;
        TextView tv_mode;


        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);

            view_container = itemView.findViewById(R.id.main_recycler_view_container);
            tv_missionTitle = itemView.findViewById(R.id.tv_mission_title);
            tv_date = itemView.findViewById(R.id.date_display);
            tv_time = itemView.findViewById(R.id.time_display);
            tv_address = itemView.findViewById(R.id.address_display);
            tvTimeLeft = itemView.findViewById(R.id.tvTimeLeft);
            rv_friend_image = itemView.findViewById(R.id.rv_friend_image);
            tv_mode = itemView.findViewById(R.id.tv_mode);


        }

    }


    private class FriendAdapter extends RecyclerView.Adapter<FriendAdapter.ItemViewHolder> {

        List<ItemForFriendByDay> list;

        private FriendAdapter(List<ItemForFriendByDay> list) {
            this.list = list;

        }

        @NonNull
        @Override
        public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            View view = inflater.inflate(R.layout.item_friend_image, parent, false);
            FriendAdapter.ItemViewHolder viewHolder = new FriendAdapter.ItemViewHolder(view);

            return viewHolder;
        }

        @Override
        public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {

            String image = list.get(position).getFriend_image();
            boolean isSuccess = list.get(position).isSuccess();
            String name = list.get(position).getFriend_name();

            holder.iv_friend_image.setBackground(new ShapeDrawable(new OvalShape()));
            holder.iv_friend_image.setClipToOutline(true);
            holder.tv_friend_name.setText(name);

            if (image != null && !image.isEmpty()) {

                Picasso.with(context)
                        .load(image)
                        .fit()
                        .into(holder.iv_friend_image);
            }

            if (isSuccess) {
                holder.iv_check_state.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_success));

            } else {
                holder.iv_check_state.setImageDrawable(context.getResources().getDrawable(R.drawable.unknown_icon));

            }


        }

        @Override
        public int getItemCount() {

            return list.size();
        }

        private class ItemViewHolder extends RecyclerView.ViewHolder {

            ImageView iv_friend_image;
            ImageView iv_check_state;
            TextView tv_friend_name;


            private ItemViewHolder(@NonNull View itemView) {
                super(itemView);

                iv_friend_image = itemView.findViewById(R.id.iv_friend_image);
                iv_check_state = itemView.findViewById(R.id.iv_check_state);
                tv_friend_name = itemView.findViewById(R.id.tv_friend_name);

            }

        }


    }

    public interface OnLoadPreviewBottomSheetListener {
        void onLoadPreviewBottomSheet(ItemForMissionByDay itemForMissionByDay);


    }

}
