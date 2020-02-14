package com.suji.lj.myapplication.Adapters;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.suji.lj.myapplication.Items.ContactItem;
import com.suji.lj.myapplication.Items.DateItem;
import com.suji.lj.myapplication.Items.ItemForDateTime;
import com.suji.lj.myapplication.Items.MissionCartItem;
import com.suji.lj.myapplication.MissionCartActivity;
import com.suji.lj.myapplication.R;
import com.suji.lj.myapplication.SingleModeActivity;
import com.suji.lj.myapplication.Utils.DateTimeUtils;

import java.util.List;

import io.realm.Realm;
import io.realm.RealmList;

public class MissionCartListAdapter extends RecyclerView.Adapter<MissionCartListAdapter.RecyclerViewContactHolder>  {
    Context context;
    List<MissionCartItem> missionCartItemList;
    Realm realm;

    public MissionCartListAdapter(Context context , List<MissionCartItem> missionCartItemList, Realm realm){

        this.context = context;
        this.missionCartItemList = missionCartItemList;
        this.realm = realm;

    }


    @NonNull
    @Override
    public RecyclerViewContactHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        view = inflater.inflate(R.layout.mission_list_item, parent, false);
        MissionCartListAdapter.RecyclerViewContactHolder recyclerViewContactHolder = new MissionCartListAdapter.RecyclerViewContactHolder(view);

        return recyclerViewContactHolder;

    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerViewContactHolder holder, int position) {

        int hour = missionCartItemList.get(position).getHour();
        int min = missionCartItemList.get(position).getMin();
        String start_date = missionCartItemList.get(position).getMin_date();
        String end_date = missionCartItemList.get(position).getMax_date();
        holder.title.setText(missionCartItemList.get(position).getTitle());
        holder.start_date.setText(DateTimeUtils.makeDateForHuman(start_date));
        holder.end_date.setText(DateTimeUtils.makeDateForHuman(end_date));
        holder.time.setText(DateTimeUtils.makeTimeForHumanInt(hour,min));
        holder.ly_past_warning.setVisibility(View.GONE);
        holder.ly_mission_container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, SingleModeActivity.class);
                intent.putExtra("id",missionCartItemList.get(position).getId());
                context.startActivity(intent);
            }
        });

        RealmList<ItemForDateTime> dayList= missionCartItemList.get(position).getCalendarDayList();
        for(int i=0; i<dayList.size();i++){
            int year = dayList.get(i).getYear();
            int month = dayList.get(i).getMonth();
            int day = dayList.get(i).getDay();

            String date_time = DateTimeUtils.makeDateForServer(year,month,day)+DateTimeUtils.makeTimeForServer(hour,min);
            Log.d("과거는삭제",date_time);

            if(!DateTimeUtils.compareIsFuture(date_time)){
                holder.title.setTextColor(context.getResources().getColor(R.color.red));
                holder.ly_past_warning.setVisibility(View.VISIBLE);
                break;
            }

        }



        holder.delete_item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                realm.beginTransaction();
                missionCartItemList.get(position).deleteFromRealm();
                realm.commitTransaction();
                notifyItemRemoved(position);
                notifyItemRangeChanged(position,missionCartItemList.size());
            }
        });


    }

    @Override
    public int getItemCount() {
        return missionCartItemList.size();
    }

    public static class RecyclerViewContactHolder extends RecyclerView.ViewHolder {

        CheckBox checkBox;
        TextView title;
        LinearLayout delete_item;
        TextView start_date;
        TextView end_date;
        TextView time;
        LinearLayout ly_past_warning;
        LinearLayout ly_mission_container;


        public RecyclerViewContactHolder(@NonNull View itemView) {
            super(itemView);

            checkBox = itemView.findViewById(R.id.mission_cart_list_check_box);
            title = itemView.findViewById(R.id.mission_cart_list_title);
            delete_item = itemView.findViewById(R.id.delete_item);
            start_date = itemView.findViewById(R.id.start_date);
            end_date = itemView.findViewById(R.id.end_date);
            time = itemView.findViewById(R.id.time);
            ly_past_warning = itemView.findViewById(R.id.past_warning);
            ly_mission_container = itemView.findViewById(R.id.ly_mission_container);



        }

    }
}
