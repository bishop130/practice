package com.suji.lj.myapplication.Adapters;

import android.content.Context;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;
import com.suji.lj.myapplication.Items.ContactItem;
import com.suji.lj.myapplication.Items.ContactItemForServer;
import com.suji.lj.myapplication.Items.ItemForFriendByDay;
import com.suji.lj.myapplication.R;

import java.text.DecimalFormat;
import java.util.List;

public class RecyclerDetailFriendsAdapter extends RecyclerView.Adapter<RecyclerDetailFriendsAdapter.ItemViewHolder> {

    private List<ItemForFriendByDay> friendsLists;
    Context context;

    public RecyclerDetailFriendsAdapter(List<ItemForFriendByDay> friendsLists, Context context) {
        this.friendsLists = friendsLists;
        this.context = context;
    }


    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        view = inflater.inflate(R.layout.item_recycler_detail_friends, parent, false);
        return new RecyclerDetailFriendsAdapter.ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {

        holder.friend_name.setText(friendsLists.get(position).getFriend_name());
        String thumbnail = friendsLists.get(position).getFriend_image();

        holder.friend_image.setBackground(new ShapeDrawable(new OvalShape()));
        holder.friend_image.setClipToOutline(true);

        if (thumbnail != null && !thumbnail.isEmpty()) {
            Picasso.with(context)
                    .load(thumbnail)
                    .fit()
                    .into(holder.friend_image);
            Log.d("이미지", "이미지있음");
        } else {
            Log.d("이미지", "이미지없음");
            holder.friend_image.setImageDrawable(context.getResources().getDrawable(R.drawable.default_profile));


        }


    }

    @Override
    public int getItemCount() {
        return friendsLists.size();
    }

    public static class ItemViewHolder extends RecyclerView.ViewHolder {
        TextView friend_name;
        ImageView friend_image;

        public ItemViewHolder(View itemView) {
            super(itemView);

            friend_name = itemView.findViewById(R.id.friend_name);
            friend_image = itemView.findViewById(R.id.friend_image);


        }
    }
}
