package com.suji.lj.myapplication.Adapters;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.squareup.picasso.Picasso;
import com.suji.lj.myapplication.Fragments.FriendFragment;
import com.suji.lj.myapplication.Items.ItemForFriends;
import com.suji.lj.myapplication.Items.ItemForFriendsList;
import com.suji.lj.myapplication.R;

import java.util.List;

public class RecyclerFriendAdapter extends RecyclerView.Adapter<RecyclerFriendAdapter.ViewHolder> {


    List<ItemForFriendsList> list;
    Activity activity;
    OnEditFriendListener listener;


    public RecyclerFriendAdapter(Activity activity, List<ItemForFriendsList> list,OnEditFriendListener listener) {
        this.list = list;
        this.activity = activity;
        this.listener = listener;
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


        String friendId = list.get(position).getFriendId();
        String friend_name = list.get(position).getFriendName();
        Log.d("친구목록", friend_name);
        String friend_image = list.get(position).getFriendImage();

        holder.tvFriendName.setText(friend_name);
        holder.ivFriendImage.setBackground(new ShapeDrawable(new OvalShape()));
        holder.ivFriendImage.setClipToOutline(true);


        if (friend_image != null && !friend_image.isEmpty()) {


            Picasso.with(activity).load(friend_image).fit().into(holder.ivFriendImage);


        } else {

            holder.ivFriendImage.setImageDrawable(activity.getResources().getDrawable(R.drawable.default_profile));


        }


        holder.lyContainer.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                listener.onEditFriend(friendId);


                return false;
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

    public interface OnEditFriendListener{
        void onEditFriend(String friendId);
    }
}
