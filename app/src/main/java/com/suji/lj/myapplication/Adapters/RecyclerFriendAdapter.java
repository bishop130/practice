package com.suji.lj.myapplication.Adapters;

import android.app.Activity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;
import com.suji.lj.myapplication.Items.ItemForFriends;
import com.suji.lj.myapplication.Items.ItemForFriendsList;
import com.suji.lj.myapplication.R;

import java.util.List;

public class RecyclerFriendAdapter extends RecyclerView.Adapter<RecyclerFriendAdapter.ViewHolder> {


    List<ItemForFriendsList> list;
    Activity activity;


    public RecyclerFriendAdapter(Activity activity, List<ItemForFriendsList> list) {
        this.list = list;
        this.activity = activity;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.item_friend, parent, false);


        return new RecyclerFriendAdapter.ViewHolder(view);


    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {


        String friend_name = list.get(position).getFriends_name();
        Log.d("친구목록", friend_name);
        String friend_image = list.get(position).getFriends_image();

        holder.tvFriendName.setText(friend_name);
        if (friend_image != null && !friend_image.isEmpty()) {


            Picasso.with(activity).load(friend_image).fit().into(holder.ivFriendImage);


        } else {

            holder.ivFriendImage.setImageDrawable(activity.getResources().getDrawable(R.drawable.default_profile));


        }


        holder.lyContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


            }
        });


    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView tvFriendName;
        ImageView ivFriendImage;
        LinearLayout lyContainer;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tvFriendName = itemView.findViewById(R.id.tvFriendName);
            ivFriendImage = itemView.findViewById(R.id.ivFriendImage);
            lyContainer = itemView.findViewById(R.id.lyContainer);


        }

    }
}
