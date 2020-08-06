package com.suji.lj.myapplication.Adapters;

import android.app.TimePickerDialog;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.suji.lj.myapplication.Items.ItemForDateTime;
import com.suji.lj.myapplication.R;
import com.suji.lj.myapplication.SingleModeActivity;
import com.suji.lj.myapplication.Utils.DateTimeUtils;

import java.util.List;

import io.realm.Realm;

public class RecyclerDateTimeAdapter extends RecyclerView.Adapter<RecyclerDateTimeAdapter.ItemViewHolder> {

    List<ItemForDateTime> calendarDayList;
    Realm realm;
    Context context;
    OnTimeSetListener listener;





    public RecyclerDateTimeAdapter(Context context, List<ItemForDateTime> calendarDayList,Realm realm, OnTimeSetListener listener) {


        this.context = context;
        this.calendarDayList = calendarDayList;
        this.realm = realm;
        this.listener = listener;

    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        view = inflater.inflate(R.layout.item_recycler_date_time, parent, false);
        final RecyclerDateTimeAdapter.ItemViewHolder viewHolder = new RecyclerDateTimeAdapter.ItemViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {
        Log.d("담쟁이2",calendarDayList.size()+"어댑");


        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                calendarDayList.get(position).setPosition(position);
            }
        });



        int year = calendarDayList.get(position).getYear();
        int month = calendarDayList.get(position).getMonth();
        int day = calendarDayList.get(position).getDay();
        Log.d("솔약국",calendarDayList.get(position).getPosition()+"데이트");
        Log.d("솔약국",position+"포지션");

        String time = calendarDayList.get(position).getTime();
        Log.d("담쟁이",year+""+month+""+day+time);
        String date = DateTimeUtils.makeDateForServer(year,month,day);
        boolean is30min = DateTimeUtils.compareIsFuture30min(date+time);
        if(is30min){
            holder.ly_time_error.setVisibility(View.GONE);
        }else{
            holder.ly_time_error.setVisibility(View.VISIBLE);
        }



            holder.date.setText(DateTimeUtils.makeDateForHumanNoYear(date));
            holder.time.setText(DateTimeUtils.makeTimeForHuman(calendarDayList.get(position).getTime(),"HHmm"));
            holder.time.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //Log.d("솔약국",calendarDayList.get(position).getDate()+"데이트");
                    //Log.d("솔약국",calendarDayList.get(position).getPosition()+"포지션");
                    TimePickerDialog timePicker = new TimePickerDialog(context, new TimePickerDialog.OnTimeSetListener() {
                        @Override
                        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                            holder.time.setText(DateTimeUtils.makeTimeForHumanInt(hourOfDay,minute));
                            listener.onTimeSet(hourOfDay,minute,position);
                            //((SingleModeActivity)context).timeSet(hourOfDay,minute,position);
                            boolean is30min = DateTimeUtils.compareIsFuture30min(year+""+month+""+day+time);
                            if(is30min){
                                holder.ly_time_error.setVisibility(View.GONE);
                            }else{
                                holder.ly_time_error.setVisibility(View.VISIBLE);
                            }


                        }
                    }, calendarDayList.get(position).getHour(), calendarDayList.get(position).getMin(), false);
                    timePicker.show();
                }
            });




    }

    @Override
    public int getItemCount() {
        return calendarDayList.size();
    }

    public static class ItemViewHolder extends RecyclerView.ViewHolder {

        TextView date;
        TextView time;
        LinearLayout ly_time_error;


        public ItemViewHolder(View itemView) {
            super(itemView);

            date = itemView.findViewById(R.id.date);
            time = itemView.findViewById(R.id.time);
            ly_time_error = itemView.findViewById(R.id.ly_time_error);


        }
    }

    public interface OnTimeSetListener{
        void onTimeSet(int hour, int min, int position);

    }
}
