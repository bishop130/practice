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

    }

    @Override
    public int getItemCount() {
        return recyclerItemList.size();
    }

    public static class ItemViewHolder extends RecyclerView.ViewHolder {
        TextView tv_missionTitle;

        LinearLayout view_container;


        public ItemViewHolder(View itemView) {
            super(itemView);
            view_container = itemView.findViewById(R.id.main_recycler_view_container);
            tv_missionTitle = itemView.findViewById(R.id.tv_mission_title);

        }
    }

    private void setMinMaxDate(final RecyclerAdapter.ItemViewHolder holder, final int position) {

        String array = recyclerItemList.get(position).getDate_array();


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
        holder.range_date.setText(simpleDateFormat.format(min_date) + "~" + simpleDateFormat.format(max_date));


    }
}





