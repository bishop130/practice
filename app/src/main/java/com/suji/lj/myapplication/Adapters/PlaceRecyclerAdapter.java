package com.suji.lj.myapplication.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.suji.lj.myapplication.DaumMapActivity;
import com.suji.lj.myapplication.Items.PlaceItem;
import com.suji.lj.myapplication.R;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class PlaceRecyclerAdapter extends RecyclerView.Adapter<PlaceRecyclerAdapter.ItemViewHolder>{

    private List<PlaceItem> placeList;
    private Context context;

    public PlaceRecyclerAdapter(List<PlaceItem> placeList, Context context) {
        this.placeList = placeList;
        this.context = context;
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        LayoutInflater inflater = LayoutInflater.from(context);
        view = inflater.inflate(R.layout.place_item, parent, false);
        final PlaceRecyclerAdapter.ItemViewHolder place = new PlaceRecyclerAdapter.ItemViewHolder(view);

        return place;
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, final int position) {

        holder.place_name.setText(placeList.get(position).getPlaceName());
        holder.road_address.setText(placeList.get(position).getRoadAddress());
        holder.old_address.setText(placeList.get(position).getOldAddress());
        holder.view_container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ((DaumMapActivity)DaumMapActivity.mContext).moveSelectedPlace(placeList.get(position).getLatitude(),placeList.get(position).getLongitude());

            }
        });


    }

    @Override
    public int getItemCount() {
        return this.placeList.size();
    }

    public static class ItemViewHolder extends RecyclerView.ViewHolder {
        TextView place_name;
        TextView road_address;
        TextView old_address;
        RelativeLayout view_container;

        public ItemViewHolder(View itemView) {
            super(itemView);
            view_container = itemView.findViewById(R.id.place_container);
            place_name= (TextView) itemView.findViewById(R.id.place_name);
            road_address= (TextView) itemView.findViewById(R.id.road_address);
            old_address = (TextView)itemView.findViewById(R.id.old_address);
        }
    }

}
