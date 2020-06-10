package com.suji.lj.myapplication.Adapters;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.suji.lj.myapplication.Items.ItemForMissionByDay;
import com.suji.lj.myapplication.MissionDetailActivity;
import com.suji.lj.myapplication.R;
import com.suji.lj.myapplication.Utils.DateTimeUtils;
import com.suji.lj.myapplication.Utils.OnSingleClickListener;

import java.util.ArrayList;
import java.util.List;

public class RecyclerMissionDayAdapter extends RecyclerView.Adapter<RecyclerMissionDayAdapter.ViewHolder> {

    List<ItemForMissionByDay> list = new ArrayList<>();
    Context context;
    OnLoadMissionListListener onLoadMissionListListener;


    public RecyclerMissionDayAdapter(Context context, List<ItemForMissionByDay> list, OnLoadMissionListListener onLoadMissionListListener) {
        this.list = list;
        this.context = context;
        this.onLoadMissionListListener = onLoadMissionListListener;

    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.main_recycler_view_item, parent, false);


        return new RecyclerMissionDayAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.tv_missionTitle.setText(list.get(position).getTitle());
        holder.tv_address.setText(list.get(position).getAddress());
        holder.tv_date.setText(DateTimeUtils.makeDateForHuman(list.get(position).getDate()));
        holder.tv_time.setText(DateTimeUtils.makeTimeForHuman(list.get(position).getTime()));
        holder.view_container.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                //Intent intent = new Intent(context, MissionDetailActivity.class);
                //intent.putExtra("mission_id", list.get(position).getMother_id());
                //context.startActivity(intent);
                Log.d("어댑트",list.get(position).getMission_id());
                onLoadMissionListListener.onLoadMissionList(list.get(position).getMission_id());
            }
        });


    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView tv_missionTitle;
        TextView tv_date;
        TextView tv_time;
        TextView tv_address;

        LinearLayout view_container;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            view_container = itemView.findViewById(R.id.main_recycler_view_container);
            tv_missionTitle = itemView.findViewById(R.id.tv_mission_title);
            tv_date = itemView.findViewById(R.id.date_display);
            tv_time = itemView.findViewById(R.id.time_display);
            tv_address = itemView.findViewById(R.id.address_display);


        }

    }

    public interface OnLoadMissionListListener {
        void onLoadMissionList(String mission_id);

    }
}
