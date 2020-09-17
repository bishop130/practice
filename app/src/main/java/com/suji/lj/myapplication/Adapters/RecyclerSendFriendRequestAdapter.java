package com.suji.lj.myapplication.Adapters;

import android.content.Context;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;
import com.suji.lj.myapplication.Items.ItemForFriendsList;
import com.suji.lj.myapplication.R;

import java.util.List;

public class RecyclerSendFriendRequestAdapter extends RecyclerView.Adapter<RecyclerSendFriendRequestAdapter.ViewHolder> {


    List<ItemForFriendsList> list;
    OnSendRequestListener onSendRequestListener;
    Context context;

    public RecyclerSendFriendRequestAdapter(Context context, List<ItemForFriendsList> list, OnSendRequestListener onSendRequestListener) {
        this.list = list;
        this.onSendRequestListener = onSendRequestListener;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.item_send_friend_request, parent, false);


        return new RecyclerSendFriendRequestAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String friend_name = list.get(position).getFriendName();
        String friend_image = list.get(position).getFriendImage();

        holder.tv_friend_name.setText(friend_name);

        holder.iv_friend_image.setBackground(new ShapeDrawable(new OvalShape()));
        holder.iv_friend_image.setClipToOutline(true);

        if (friend_image != null && !friend_image.isEmpty()) {
            Picasso.with(context)
                    .load(friend_image)
                    .error(R.drawable.default_profile)
                    .fit()
                    .into(holder.iv_friend_image);
        }


        holder.tv_cancel_request.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                onSendRequestListener.onSendRequest(list.get(position));


            }
        });


    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView tv_friend_name;
        TextView tv_cancel_request;
        ImageView iv_friend_image;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);


            tv_friend_name = itemView.findViewById(R.id.tv_friend_name);
            iv_friend_image = itemView.findViewById(R.id.iv_friends_image);
            tv_cancel_request = itemView.findViewById(R.id.tv_cancel_request);


        }

    }


    public interface OnSendRequestListener {
        void onSendRequest(ItemForFriendsList item);


    }
}
