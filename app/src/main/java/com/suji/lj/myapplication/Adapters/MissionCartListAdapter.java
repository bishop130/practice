package com.suji.lj.myapplication.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.suji.lj.myapplication.Items.ContactItem;
import com.suji.lj.myapplication.Items.MissionCartItem;
import com.suji.lj.myapplication.R;

import java.util.List;

public class MissionCartListAdapter extends RecyclerView.Adapter<MissionCartListAdapter.RecyclerViewContactHolder>  {
    Context context;
    List<MissionCartItem> missionCartItemList;

    public MissionCartListAdapter(Context context , List<MissionCartItem> missionCartItemList){

        this.context = context;
        this.missionCartItemList = missionCartItemList;

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

        holder.title.setText(missionCartItemList.get(position).getTitle());


    }

    @Override
    public int getItemCount() {
        return missionCartItemList.size();
    }

    public static class RecyclerViewContactHolder extends RecyclerView.ViewHolder {

        CheckBox checkBox;
        TextView title;


        public RecyclerViewContactHolder(@NonNull View itemView) {
            super(itemView);

            checkBox = itemView.findViewById(R.id.mission_cart_list_check_box);
            title = itemView.findViewById(R.id.mission_cart_list_title);



        }

    }
}
