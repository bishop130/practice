package com.suji.lj.myapplication.Adapters;

import android.content.Context;
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
import androidx.recyclerview.widget.RecyclerView;

import com.github.aakira.expandablelayout.ExpandableLinearLayout;
import com.suji.lj.myapplication.Items.RecyclerItem;
import com.suji.lj.myapplication.R;
import com.suji.lj.myapplication.Utils.DateTimeFormatter;
import com.suji.lj.myapplication.Utils.Utils;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MainRecyclerAdapter extends RecyclerView.Adapter<MainRecyclerAdapter.ItemViewHolder> {
    Context context;
    private List<RecyclerItem> recyclerItemList;
    private DateTimeFormatter dtf = new DateTimeFormatter();
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyMMdd", Locale.KOREA);

    public MainRecyclerAdapter (Context context, List<RecyclerItem> recyclerItemList){
        this.context =context;
        this.recyclerItemList = recyclerItemList;




    }


    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        view = inflater.inflate(R.layout.main_recycler_view_item, parent, false);
        ItemViewHolder viewHolder = new ItemViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {



        holder.tv_missionTitle.setText(recyclerItemList.get(position).getMissionTitle());
        holder.tv_address.setText(recyclerItemList.get(position).getAddress()+" 인근");
        setDisplayDateTime(holder,position);

    }

    @Override
    public int getItemCount() {
        return recyclerItemList.size();
    }

    public static class ItemViewHolder extends RecyclerView.ViewHolder {
        TextView tv_missionTitle;
        TextView tv_date;
        TextView tv_time;
        TextView tv_address;

        LinearLayout view_container;


        public ItemViewHolder(View itemView) {
            super(itemView);
            view_container = itemView.findViewById(R.id.main_recycler_view_container);
            tv_missionTitle = itemView.findViewById(R.id.tv_mission_title);
            tv_date = itemView.findViewById(R.id.date_display);
            tv_time = itemView.findViewById(R.id.time_display);
            tv_address = itemView.findViewById(R.id.address_display);

        }
    }
    private void setDisplayDateTime(ItemViewHolder holder,final int position){
        //String time = Utils.makeDateTimeForHuman(recyclerItemList.get(position).getDate(),recyclerItemList.get(position).getMissionTime());
        //holder.tv_date.setText(time);
        holder.tv_time.setText(recyclerItemList.get(position).getDate_time());


    }

}






