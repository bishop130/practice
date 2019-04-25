package com.suji.lj.myapplication.Adapters;

import android.content.Context;
import android.content.Intent;
import android.os.CountDownTimer;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.suji.lj.myapplication.Items.RecyclerItem;
import com.suji.lj.myapplication.MissionDetailActivity;
import com.suji.lj.myapplication.R;
import com.github.aakira.expandablelayout.ExpandableLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class RecyclerForMissionAdapter extends RecyclerView.Adapter<RecyclerForMissionAdapter.ItemViewHolder> {


    private Context mContext;
    private List<RecyclerItem> mData;
    String CDT;
    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-M-d", Locale.KOREA);


    public RecyclerForMissionAdapter(Context mContext, List<RecyclerItem> mData) {
        this.mContext = mContext;
        this.mData = mData;
    }


    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, final int viewType) {

        mContext = parent.getContext();
        View view;
        LayoutInflater inflater = LayoutInflater.from(mContext);
        view = inflater.inflate(R.layout.list_item, parent, false);
        final ItemViewHolder viewHolder = new ItemViewHolder(view);

        viewHolder.button_to_detail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(mContext, MissionDetailActivity.class);
                intent.putExtra("MissionTitle", mData.get(viewHolder.getAdapterPosition()).getMissionTitle());
                intent.putExtra("MissionID", mData.get(viewHolder.getAdapterPosition()).getMissionID());
                intent.putExtra("Latitude", mData.get(viewHolder.getAdapterPosition()).getLatitude());
                intent.putExtra("Longitude", mData.get(viewHolder.getAdapterPosition()).getLongitude());
                intent.putExtra("mission_time", mData.get(viewHolder.getAdapterPosition()).getMissionTime());
                intent.putExtra("mission_date_array", mData.get(viewHolder.getAdapterPosition()).getDate_array());
                intent.putExtra("address",mData.get(viewHolder.getAdapterPosition()).getAddress());
                intent.putExtra("contact_list",mData.get(viewHolder.getAdapterPosition()).getContact_json());
                intent.putExtra("completed_dates",mData.get(viewHolder.getAdapterPosition()).getCompleted_dates());
                intent.putExtra("is_failed",mData.get(viewHolder.getAdapterPosition()).getIs_failed());
                intent.putExtra("completed",mData.get(viewHolder.getAdapterPosition()).getCompleted());

                mContext.startActivity(intent);
            }
        });

        return viewHolder;

    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {

        holder.mission_title_total.setText(mData.get(position).getMissionTitle());

        setMinMaxDate(holder, position);
        //calculateRestTime(holder, position);
        //setMissionTime(holder, position);
        //displayRestDate(holder, position);
        //displayContacts(holder, position);
        displayRegisteredDate(holder, position);
        displayState(holder,position);


    }
    private void displayState(ItemViewHolder holder, int position){
        int total_dates = Integer.valueOf(mData.get(position).getTotal_dates());
      int completed_dates = Integer.valueOf(mData.get(position).getCompleted());
        if(total_dates==completed_dates){
            holder.mission_state.setImageResource(R.drawable.checked_icon);
        }else if(mData.get(position).getIs_failed().equals("1")){
            holder.mission_state.setImageResource(R.drawable.fail);

        }

    }



    private void displayRegisteredDate(ItemViewHolder holder, int position) {

        try {


            Date date = simpleDateFormat.parse(mData.get(position).getRegister_date());


            String day = (String) DateFormat.format("d", date); // 20
            String monthNumber = (String) DateFormat.format("M", date); // 6
            String year = (String) DateFormat.format("yyyy", date); // 2013


        } catch (ParseException e) {
            e.printStackTrace();
        }


    }

    private void setMinMaxDate(final ItemViewHolder holder, final int position) {
        String array = mData.get(position).getDate_array();


        List<String> date_array = Arrays.asList(array.split("\\s*,\\s*"));

        SimpleDateFormat sdf_array = new SimpleDateFormat("yyyy-MM-dd", Locale.KOREA);
        int date_count = 0;
        long now = System.currentTimeMillis();
        Date max_date = new Date(now);
        String str = sdf_array.format(max_date);
        Date min_date = new Date(now);
        try {
            max_date = sdf_array.parse(str);
            min_date = sdf_array.parse(str);
            for (int i = 0; i < date_array.size(); i++) {
                String date = date_array.get(i);
                Log.d("date_check", date);
                date_count++;

                Date date_arr = sdf_array.parse(date);
                if (date_arr.getTime() >= max_date.getTime()) {
                    max_date = date_arr;
                }
                if (date_arr.getTime() <= min_date.getTime()) {
                    min_date = date_arr;
                }
            }
        } catch (Exception e) {

        }
        String min_day = (String) DateFormat.format("d", min_date); // 20
        String min_month = (String) DateFormat.format("M", min_date); // 6
        String min_year = (String) DateFormat.format("yyyy", min_date); // 2013
        String max_day = (String) DateFormat.format("d", max_date); // 20
        String max_month = (String) DateFormat.format("M", max_date); // 6
        String max_year = (String) DateFormat.format("yyyy", max_date); // 2013

        holder.range_date_total.setText(min_month+"."+min_day + " ~ " + max_month+"."+max_day);


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
        }
    }


    private void setMissionTime(final ItemViewHolder holder, final int position) {
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


        Log.d("CDTrr", date_array[0] + "   " + date_array[1]);
        if (hour == 12) {
            holder.tv_mission_time.setText("오후\n" + hour + ":" + minutes);
        } else if (hour == 0) {
            holder.tv_mission_time.setText("오전\n 12:" + minutes);
        } else {

            holder.tv_mission_time.setText(((hour >= 12) ? "오후\n" : "오전\n") + " " + hour % 12 + ":" + minutes);
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
        String rest_dates = mData.get(position).getCompleted_dates() + "/" + mData.get(position).getTotal_dates();
       // holder.range_date.setText(rest_dates);


    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    private void onClickButton(final ExpandableLayout expandableLayout) {

        expandableLayout.toggle();
    }


    public static class ItemViewHolder extends RecyclerView.ViewHolder {
        TextView mission_title_total;
        TextView tv_missionID;
        TextView tv_mission_time;
        LinearLayout view_container;
        CountDownTimer countDownTimer;
        public RelativeLayout buttonLayout;
        RelativeLayout button_to_detail;
        RelativeLayout progress_bar_date;
        TextView range_date_total;
        TextView display_contact_list;
        RelativeLayout main_wrap;
        ImageView mission_state;

        public ItemViewHolder(View itemView) {
            super(itemView);
            view_container = itemView.findViewById(R.id.container_total);
            mission_title_total = itemView.findViewById(R.id.mission_title_total);
            tv_missionID =itemView.findViewById(R.id.tv_missionID);
            buttonLayout = itemView.findViewById(R.id.button);
            tv_mission_time = itemView.findViewById(R.id.mission_time);
            button_to_detail =  itemView.findViewById(R.id.button_to_detail);
            progress_bar_date =  itemView.findViewById(R.id.progress_bar_date);
            range_date_total =  itemView.findViewById(R.id.range_date_total);
            main_wrap =  itemView.findViewById(R.id.main_wrap);
            mission_state = itemView.findViewById(R.id.mission_state);


        }
    }

    private void calculateRestTime(final ItemViewHolder holder, final int position) {
        long now = System.currentTimeMillis();
        String date = mData.get(position).getDate() + " " + mData.get(position).getMissionTime();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.KOREA);
        try {
            Date endDate = sdf.parse(date);
            Date startDate = new Date(now);
            String strNow = sdf.format(startDate);
            Date d2 = sdf.parse(strNow);

            long diff = endDate.getTime() - d2.getTime();
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

                        holder.tv_missionID.setText("남은시간: " + days + "일 " + hours + "시간 " + minutes + "분");
                    }

                    @Override
                    public void onFinish() {
                        holder.tv_missionID.setText("종료");
                    }
                }.start();
            } else {
                holder.tv_missionID.setText("종료");
            }

        } catch (Exception e) {
            e.printStackTrace();

        }

    }

}

