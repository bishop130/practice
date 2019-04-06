package com.example.lj.myapplication.Adapters;

import android.animation.ObjectAnimator;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.os.CountDownTimer;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import android.text.format.DateUtils;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.lj.myapplication.MainActivity;
import com.example.lj.myapplication.R;
import com.example.lj.myapplication.Items.RecyclerItem;
import com.example.lj.myapplication.RecyclerResultActivity;
import com.github.aakira.expandablelayout.ExpandableLayout;
import com.github.aakira.expandablelayout.ExpandableLayoutListenerAdapter;
import com.github.aakira.expandablelayout.ExpandableLinearLayout;
import com.github.aakira.expandablelayout.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import static android.content.Context.MODE_PRIVATE;
import static java.lang.Long.parseLong;

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ItemViewHolder> {


    private Context mContext;
    private List<RecyclerItem> mData;
    private SparseBooleanArray expandState = new SparseBooleanArray();
    private SimpleDateFormat date_time = new SimpleDateFormat("yyyy-M-dd HH:mm:ss", Locale.KOREA);
    private int requestID = 0;
    private SimpleDateFormat date_time_db = new SimpleDateFormat("yyyy-MM-dd", Locale.KOREA);
    private DateTimeFormatter dtf = new DateTimeFormatter();


    public RecyclerAdapter(Context mContext, List<RecyclerItem> mData) {
        this.mContext = mContext;
        this.mData = mData;
        for (int i = 0; i < mData.size(); i++) {
            expandState.append(i, false);
        }
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, final int viewType) {

        mContext = parent.getContext();
        View view;
        LayoutInflater inflater = LayoutInflater.from(mContext);
        view = inflater.inflate(R.layout.item_recycler_view, parent, false);
        final ItemViewHolder viewHolder = new ItemViewHolder(view);
        viewHolder.expanded_button_map.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(mContext, RecyclerResultActivity.class);
                intent.putExtra("MissionTitle", mData.get(viewHolder.getAdapterPosition()).getMissionTitle());
                intent.putExtra("MissionID", mData.get(viewHolder.getAdapterPosition()).getMissionID());
                intent.putExtra("Latitude", mData.get(viewHolder.getAdapterPosition()).getLatitude());
                intent.putExtra("Longitude", mData.get(viewHolder.getAdapterPosition()).getLongitude());
                intent.putExtra("mission_time", mData.get(viewHolder.getAdapterPosition()).getMissionTime());
                intent.putExtra("mission_date", mData.get(viewHolder.getAdapterPosition()).getDate());
                intent.putExtra("mission_success", mData.get(viewHolder.getAdapterPosition()).getSuccess());
                intent.putExtra("mission_date_time", mData.get(viewHolder.getAdapterPosition()).getDate_time());
                mContext.startActivity(intent);
            }
        });

        return viewHolder;

    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {

        holder.tv_missionTitle.setText(mData.get(position).getMissionTitle());

        expandableRecyclerView(holder, position);
        setMinMaxDate(holder, position);
        calculateRestTime(holder, position);
        setMissionTime(holder, position);
        displayRestDate(holder, position);
        displayContacts(holder, position);
        //Alarm(holder,position);
        checkAlarm(position);


    }

    private void expandableRecyclerView(final ItemViewHolder holder, final int position) {


        holder.setIsRecyclable(false);
        holder.expandableLayout.setInRecyclerView(true);
        holder.expandableLayout.setExpanded(expandState.get(position));
        holder.expandableLayout.setListener(new ExpandableLayoutListenerAdapter() {
            @Override
            public void onPreOpen() {
                holder.main_wrap.setBackground(ContextCompat.getDrawable(mContext, R.drawable.toggle_rounded));
                createRotateAnimator(holder.buttonLayout, 0f, 180f).start();
                expandState.put(position, true);

                /*
                if (android.os.Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN)
                    holder.main_wrap.setBackground(mContext.getResources().getDrawable(R.drawable.toggle_rounded));
                else if(android.os.Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP_MR1)
                    holder.main_wrap.setBackground(mContext.getResources().getDrawable(R.drawable.toggle_rounded));
                else
                    holder.main_wrap.setBackground(ContextCompat.getDrawable(mContext, R.drawable.toggle_rounded));
                    */

                Log.d("이거열림", "열림: " + String.valueOf(expandState.get(position)) + "\n포지션" + position);
            }

            @Override
            public void onPreClose() {

                createRotateAnimator(holder.buttonLayout, 180f, 0f).start();
                expandState.put(position, false);
                Thread timer = new Thread() {
                    public void run() {
                        try {
                            sleep(200);
                            holder.main_wrap.setBackground(ContextCompat.getDrawable(mContext, R.drawable.rounded));
/*
                            if (android.os.Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN)
                                holder.main_wrap.setBackground(mContext.getResources().getDrawable(R.drawable.rounded));
                            else if(android.os.Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP_MR1)
                                holder.main_wrap.setBackground(mContext.getResources().getDrawable(R.drawable.rounded));
                            else
                                holder.main_wrap.setBackground(ContextCompat.getDrawable(mContext, R.drawable.rounded));
                            */

                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                    }
                };
                timer.start();


            }
        });

        holder.buttonLayout.setRotation(expandState.get(position) ? 180f : 0f);
        holder.main_wrap.setBackground(expandState.get(position) ? ContextCompat.getDrawable(mContext, R.drawable.toggle_rounded) : ContextCompat.getDrawable(mContext, R.drawable.rounded));


        holder.buttonLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                onClickButton(holder.expandableLayout);
                // holder.main_wrap.setBackground(expandState.get(position) ? ContextCompat.getDrawable(mContext, R.drawable.toggle_rounded) :ContextCompat.getDrawable(mContext, R.drawable.rounded) );

            }
        });
    }


    private void setMinMaxDate(final ItemViewHolder holder, final int position) {

        String array = mData.get(position).getDate_array();


        List<String> date_array = Arrays.asList(array.split("\\s*,\\s*"));
        int date_count = 0;
        Date max_date = dtf.dateParser(date_array.get(0));
        Date min_date = dtf.dateParser(date_array.get(0));

        for (int i = 0; i < date_array.size(); i++) {
            String date = date_array.get(i);
            Log.d("date_check", date);
            date_count++;


            Date date_arr = dtf.dateParser(date);
            if (date_arr.getTime() >= max_date.getTime()) {
                max_date = date_arr;
            }
            if (date_arr.getTime() <= min_date.getTime()) {
                min_date = date_arr;
            }
        }

        Log.d("date_check_count", String.valueOf(date_count));
        //holder.range_date.setText(sdf_array.format(min_date) + "~" + sdf_array.format(max_date));


    }

    private void displayContacts(final ItemViewHolder holder, final int position) {

        StringBuffer stringBuffer = new StringBuffer();
        Log.d("이게 왜 이렇게나와", mData.get(position).getContact_json());
        try {
            JSONArray jsonArray = new JSONArray(mData.get(position).getContact_json());

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);

                stringBuffer.append(jsonObject.getString("name"));
                stringBuffer.append(",");

                Log.d("이게 왜 이렇게나와", jsonObject.getString("num"));

            }
            String result = stringBuffer.toString();
            int last = result.length() - 1;
            if (last > 0 && result.charAt(last) == ',') {
                result = result.substring(0, last);
            }
            holder.display_contact_list.setText(result);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    private void setMissionTime(final ItemViewHolder holder, final int position) {

        Date set_mission_date = dtf.dateTimeParser(mData.get(position).getDate() + " " + mData.get(position).getMissionTime());
        if (DateUtils.isToday(set_mission_date.getTime())) {
            holder.is_today.setText("오늘");


        } else if (isTomorrow(set_mission_date)) {
            holder.is_today.setText("내일");

        } else {
            holder.is_today.setText("");
            holder.is_today.setVisibility(View.GONE);
        }


        String date = mData.get(position).getMissionTime();
        String minutes;
        String[] date_array = date.split(":");
        int hour = Integer.valueOf(date_array[0]);
        int min = Integer.valueOf(date_array[1]);
        if (min < 10) {
            minutes = "0" + String.valueOf(min);
        } else {
            minutes = String.valueOf(min);
        }

        if (hour == 12) {
            String time = "오후\n" + hour + ":" + minutes;
            holder.tv_mission_time.setText(time);
        } else if (hour == 0) {
            String time = "오전\n 12:" + minutes;
            holder.tv_mission_time.setText(time);
        } else {
            String time = ((hour >= 12) ? "오후\n" : "오전\n") + " " + hour % 12 + ":" + minutes;
            holder.tv_mission_time.setText(time);
        }


    }


    private void displayRestDate(final ItemViewHolder holder, final int position) {
        RelativeLayout.LayoutParams relativeParams = (RelativeLayout.LayoutParams) holder.progress_bar_date.getLayoutParams();
        final float scale = mContext.getResources().getDisplayMetrics().density;
        // convert the DP into pixel
        int pixel;

        if (Integer.valueOf(mData.get(position).getCompleted_dates()) == 0) {
            pixel = (int) (2 * scale + 0.5f);
        } else {
            pixel = (int) ((200 * Integer.valueOf(mData.get(position).getCompleted_dates()) / Integer.valueOf(mData.get(position).getTotal_dates())) * scale + 0.5f);
        }


        relativeParams.width = pixel;
        holder.progress_bar_date.setLayoutParams(relativeParams);
        String rest_dates = mData.get(position).getCompleted_dates() + "/" + mData.get(position).getTotal_dates() + "일";

        holder.range_date.setText(rest_dates);


    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    private void onClickButton(final ExpandableLayout expandableLayout) {

        expandableLayout.toggle();
    }


    public static class ItemViewHolder extends RecyclerView.ViewHolder {
        TextView tv_missionTitle;
        TextView tv_missionID;
        TextView tv_mission_time;
        LinearLayout view_container;
        CountDownTimer countDownTimer;
        ExpandableLinearLayout expandableLayout;
        RelativeLayout buttonLayout;
        Button expanded_button_map;
        RelativeLayout progress_bar_date;
        TextView range_date;
        TextView display_contact_list;
        RelativeLayout main_wrap;
        TextView is_today;
        ImageView is_status;


        public ItemViewHolder(View itemView) {
            super(itemView);
            view_container = itemView.findViewById(R.id.container);
            tv_missionTitle = itemView.findViewById(R.id.tv_missionTitle);
            tv_missionID = itemView.findViewById(R.id.tv_missionID);
            expandableLayout = itemView.findViewById(R.id.expandableLayout);
            buttonLayout = itemView.findViewById(R.id.button);
            tv_mission_time = itemView.findViewById(R.id.mission_time);
            expanded_button_map = itemView.findViewById(R.id.expand_button_map);
            progress_bar_date = itemView.findViewById(R.id.progress_bar_date);
            range_date = itemView.findViewById(R.id.range_date);
            display_contact_list = itemView.findViewById(R.id.display_contact_list);
            main_wrap = itemView.findViewById(R.id.main_wrap);
            is_today = itemView.findViewById(R.id.is_today);
            is_status = itemView.findViewById(R.id.is_status);
        }
    }

    private ObjectAnimator createRotateAnimator(final View target, final float from, final float to) {
        ObjectAnimator animator = ObjectAnimator.ofFloat(target, "rotation", from, to);
        animator.setDuration(300);
        animator.setInterpolator(Utils.createInterpolator(Utils.LINEAR_INTERPOLATOR));
        return animator;
    }

    private void calculateRestTime(final ItemViewHolder holder, final int position) {

        Date mission_time = dtf.dateTimeParser(mData.get(position).getDate_time());
        Date current_time = new Date(System.currentTimeMillis());
        if(mission_time.getTime()>current_time.getTime() && mData.get(position).isIs_failed()){
            holder.tv_missionID.setText("종료");
            holder.is_status.setImageResource(R.drawable.fail);
        }
        else {


            if (!mData.get(position).getSuccess()) {

                Date endDate = dtf.dateTimeParser(mData.get(position).getDate() + " " + mData.get(position).getMissionTime());
                Date curDate = new Date(System.currentTimeMillis());

                long diff = endDate.getTime() - curDate.getTime();
                if (diff >= 0) {

                    holder.countDownTimer = new CountDownTimer(diff, 1000) {
                        @Override
                        public void onTick(long l) {
                            long days = TimeUnit.MILLISECONDS.toDays(l);
                            long remainingHoursInMillis = l - TimeUnit.DAYS.toMillis(days);
                            long hours = TimeUnit.MILLISECONDS.toHours(remainingHoursInMillis);
                            long remainingMinutesInMillis = remainingHoursInMillis - TimeUnit.HOURS.toMillis(hours);
                            long minutes = TimeUnit.MILLISECONDS.toMinutes(remainingMinutesInMillis);
                            long remainingSecondsInMillis = remainingMinutesInMillis - TimeUnit.MINUTES.toMillis(minutes);
                            long seconds = TimeUnit.MILLISECONDS.toSeconds(remainingSecondsInMillis);
                            if (days == 0) {
                                String time = "+ " + hours + "시간 " + minutes + "분";

                                holder.tv_missionID.setText(time);
                                if (l < 2 * 60 * 60 * 1000) { //2시간
                                    holder.is_status.setImageResource(R.drawable.ready_to_check);
                                }
                            } else {
                                String time = "+ " + days + "일 " + hours + "시간 " + minutes + "분";
                                holder.tv_missionID.setText(time);
                            }
                        }

                        @Override
                        public void onFinish() {
                            holder.tv_missionID.setText("종료");
                            holder.is_status.setImageResource(R.drawable.fail);
                        }
                    }.start();
                } else {
                    holder.tv_missionID.setText("종료");
                    holder.is_status.setImageResource(R.drawable.fail);
                }
            } else {
                holder.tv_missionID.setText("성공");
                holder.is_status.setImageResource(R.drawable.checked_icon);

            }
        }
    }

    private static boolean isTomorrow(Date d) {
        return DateUtils.isToday(d.getTime() - DateUtils.DAY_IN_MILLIS);
    }

    private void checkAlarm(int position) {
        try {
            AlarmManager alarmManager = (AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE);
            long[] interval = {1000 * 60 * 5, 1000 * 60 * 4, 1000 * 60 * 3};
            long time = 1000 * 60 * 3;
            String date = date_time_db.format(date_time_db.parse(mData.get(position).getDate()));
            requestID = String.valueOf(mData.get(position).getMissionID() + date + time).hashCode();
            Log.d("열일", String.valueOf(mData.get(position).getMissionID() + date + time));

            Date cur_time = date_time.parse(date_time.format(new Date(System.currentTimeMillis())));
            Date mission_time = date_time.parse(mData.get(position).getDate() + " " + mData.get(position).getMissionTime());
            Intent intent = new Intent(mContext, BroadCastService.class);
            intent.putExtra("mission_name", mData.get(position).getMissionTitle());
            long days = TimeUnit.MILLISECONDS.toDays(time);
            long remainingHoursInMillis = time - TimeUnit.DAYS.toMillis(days);
            long hours = TimeUnit.MILLISECONDS.toHours(remainingHoursInMillis);
            long remainingMinutesInMillis = remainingHoursInMillis - TimeUnit.HOURS.toMillis(hours);
            long minutes = TimeUnit.MILLISECONDS.toMinutes(remainingMinutesInMillis);


            intent.putExtra("hour", hours);
            intent.putExtra("min", minutes);

            //alarmManager.cancel(pendingIntent);//important
            //pintent.cancel();
            //pendingIntent.cancel();//important
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                boolean isWorking = (PendingIntent.getBroadcast(mContext, requestID, intent, PendingIntent.FLAG_NO_CREATE) != null);
                Log.d("열일", requestID + " // " + date_time.format(mission_time.getTime() - time) + "alarm is " + (isWorking ? "" : "not") + " working...");

            }
            // Log.d("열일2", date_time.format(mission_time.getTime() - interval[i])+"alarm is " + (isWorking ? "" : "not") + " working...");


        } catch (ParseException e) {
            e.printStackTrace();
        }


    }
}
