package com.suji.lj.myapplication.Adapters;

import android.content.Context;
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
import com.suji.lj.myapplication.Items.MissionCartItem;
import com.suji.lj.myapplication.R;

import java.util.List;

import io.realm.Realm;

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

        holder.title.setText(missionCartItemList.get(position).getTitle());
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


        public RecyclerViewContactHolder(@NonNull View itemView) {
            super(itemView);

            checkBox = itemView.findViewById(R.id.mission_cart_list_check_box);
            title = itemView.findViewById(R.id.mission_cart_list_title);
            delete_item = itemView.findViewById(R.id.delete_item);



        }

    }
}
