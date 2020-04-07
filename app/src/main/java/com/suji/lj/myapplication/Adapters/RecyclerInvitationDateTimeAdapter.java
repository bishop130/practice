package com.suji.lj.myapplication.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.suji.lj.myapplication.Items.ItemForDateTime;
import com.suji.lj.myapplication.R;
import com.suji.lj.myapplication.Utils.DateTimeUtils;

import java.util.List;

public class RecyclerInvitationDateTimeAdapter extends RecyclerView.Adapter<RecyclerInvitationDateTimeAdapter.ViewHolder>{

    Context context;
    List<ItemForDateTime> list;

    public RecyclerInvitationDateTimeAdapter(Context context,List<ItemForDateTime> list) {
        this.context = context;
        this.list = list;



    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.item_invitation_date_time, parent, false);



        return new RecyclerInvitationDateTimeAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int i) {

        String date = list.get(i).getDate();
        String time = list.get(i).getTime();

        holder.date.setText(DateTimeUtils.makeDateForHuman(date));
        holder.time.setText(DateTimeUtils.makeTimeForHuman(time));

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView date;
        TextView time;





        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            date = itemView.findViewById(R.id.date);
            time = itemView.findViewById(R.id.time);


        }

    }


}
