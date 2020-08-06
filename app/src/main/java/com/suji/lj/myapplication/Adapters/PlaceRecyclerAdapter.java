package com.suji.lj.myapplication.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.suji.lj.myapplication.Items.PlaceItem;
import com.suji.lj.myapplication.R;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class PlaceRecyclerAdapter extends RecyclerView.Adapter<PlaceRecyclerAdapter.ItemViewHolder>{

    private List<PlaceItem> placeList;
    private Context context;
    OnMoveSelectedPlaceListener listener;


    public PlaceRecyclerAdapter(List<PlaceItem> placeList, Context context,OnMoveSelectedPlaceListener listener) {
        this.placeList = placeList;
        this.context = context;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        LayoutInflater inflater = LayoutInflater.from(context);
        view = inflater.inflate(R.layout.item_address, parent, false);
        final PlaceRecyclerAdapter.ItemViewHolder place = new PlaceRecyclerAdapter.ItemViewHolder(view);

        return place;
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, final int position) {

        holder.place_name.setText(placeList.get(position).getPlaceName());
        if(placeList.get(position).getRoadAddress().isEmpty()){

            holder.ly_road_address.setVisibility(View.GONE);
            holder.ly_old_address.setVisibility(View.VISIBLE);
            holder.old_address.setText(placeList.get(position).getOldAddress());

        }else{
            holder.ly_old_address.setVisibility(View.GONE);
            holder.ly_road_address.setVisibility(View.VISIBLE);
            holder.road_address.setText(placeList.get(position).getRoadAddress());

        }


        holder.view_container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               // ((SingleModeActivity) context).moveSelectedPlace(placeList.get(position).getLatitude(),placeList.get(position).getLongitude());
                listener.onMoveSelectedPlace(placeList.get(position).getLatitude(),placeList.get(position).getLongitude());

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
        LinearLayout view_container;
        LinearLayout ly_old_address;
        LinearLayout ly_road_address;

        public ItemViewHolder(View itemView) {
            super(itemView);
            view_container = itemView.findViewById(R.id.place_container);
            place_name= (TextView) itemView.findViewById(R.id.place_name);
            road_address= (TextView) itemView.findViewById(R.id.road_address);
            old_address = (TextView)itemView.findViewById(R.id.old_address);
            ly_old_address = itemView.findViewById(R.id.ly_old_address);
            ly_road_address = itemView.findViewById(R.id.ly_road_address);
        }
    }

    public interface OnMoveSelectedPlaceListener {
        void onMoveSelectedPlace(double lat,double lng);

    }

}
