package com.suji.lj.myapplication.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.ValueEventListener;
import com.suji.lj.myapplication.Items.ItemForMissionByDay;
import com.suji.lj.myapplication.MissionDetailActivity;
import com.suji.lj.myapplication.R;
import com.suji.lj.myapplication.Utils.DateTimeUtils;

import java.util.List;

public class RecyclerPastMissionAdapter extends RecyclerView.Adapter<RecyclerPastMissionAdapter.ViewHolder>{

    List<ItemForMissionByDay> list;
    Context context;
    OnLoadMissionFromPastListener onLoadMissionFromPastListener;
    public RecyclerPastMissionAdapter(Context context, List<ItemForMissionByDay> list, OnLoadMissionFromPastListener onLoadMissionFromPastListener){
        this.context = context;
        this.list = list;
        this.onLoadMissionFromPastListener = onLoadMissionFromPastListener;


    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.item_recycler_past, parent, false);



        return new RecyclerPastMissionAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.tv_missionTitle.setText(list.get(position).getTitle());
        holder.tv_address.setText(list.get(position).getAddress());
        holder.tv_date.setText(DateTimeUtils.makeDateForHuman(list.get(position).getDate()));
        holder.tv_time.setText(DateTimeUtils.makeTimeForHuman(list.get(position).getTime()));
        if(list.get(position).isSuccess()){
            //holder.ly_is_success.setBackgroundColor(context.getResources().getColor(R.color.colorSuccess));
            holder.tv_missionTitle.setTextColor(context.getResources().getColor(R.color.colorSuccess));
            holder.ic_is_success.setImageDrawable(context.getDrawable(R.drawable.ic_success));

        }else{
            //holder.ly_is_success.setBackgroundColor(context.getResources().getColor(R.color.colorError));
            holder.tv_missionTitle.setTextColor(context.getResources().getColor(R.color.colorError));
            holder.ic_is_success.setImageDrawable(context.getDrawable(R.drawable.ic_fail));

        }
        holder.view_container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onLoadMissionFromPastListener.onLoadMissionFromPast(list.get(position).getMother_id());

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
        ImageView ic_is_success;

        LinearLayout view_container;



        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            view_container = itemView.findViewById(R.id.main_recycler_view_container);
            tv_missionTitle = itemView.findViewById(R.id.tv_mission_title);
            tv_date = itemView.findViewById(R.id.date_display);
            tv_time = itemView.findViewById(R.id.time_display);
            tv_address = itemView.findViewById(R.id.address_display);
            ic_is_success = itemView.findViewById(R.id.ic_is_success);




        }

    }
    public interface OnLoadMissionFromPastListener{
        void onLoadMissionFromPast(String mission_id);

    }
}
