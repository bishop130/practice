package com.suji.lj.myapplication.Adapters;

import android.content.Context;
import android.content.Intent;
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

import java.util.List;

public class RecyclerPastMissionAdapter extends RecyclerView.Adapter<RecyclerPastMissionAdapter.ViewHolder>{

    List<ItemForMissionByDay> list;
    Context context;
    public RecyclerPastMissionAdapter(Context context,List<ItemForMissionByDay> list){
        this.context = context;
        this.list = list;

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.main_recycler_view_item, parent, false);



        return new RecyclerPastMissionAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.tv_missionTitle.setText(list.get(position).getTitle());
        holder.tv_address.setText(list.get(position).getAddress());
        holder.tv_date.setText(DateTimeUtils.makeDateForHuman(list.get(position).getDate()));
        holder.tv_time.setText(DateTimeUtils.makeTimeForHuman(list.get(position).getTime()));
        if(list.get(position).isSuccess()){



        }else{

        }
        holder.view_container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, MissionDetailActivity.class);
                intent.putExtra("mission_id", list.get(position).getMother_id());
                context.startActivity(intent);
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
}
