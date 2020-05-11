package com.suji.lj.myapplication.Adapters;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
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
import com.suji.lj.myapplication.ContactActivity;
import com.suji.lj.myapplication.Items.ItemForFriends;
import com.suji.lj.myapplication.Items.ItemFriendsList;
import com.suji.lj.myapplication.R;

import java.util.Collections;
import java.util.List;

public class RecyclerContactFriendAdapter extends RecyclerView.Adapter<RecyclerContactFriendAdapter.ItemViewHolder> {


    Context context;
    List<ItemForFriends> list;
    OnAddSelectedListener onAddSelectedListener;
    int contact_size;

    public RecyclerContactFriendAdapter(Context context, List<ItemForFriends> list, OnAddSelectedListener onAddSelectedListener) {
        this.context = context;
        this.list = list;
        this.onAddSelectedListener = onAddSelectedListener;
        this.contact_size = contact_size;
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        view = inflater.inflate(R.layout.item_friend_select, parent, false);
        final RecyclerContactFriendAdapter.ItemViewHolder viewHolder = new RecyclerContactFriendAdapter.ItemViewHolder(view);
        return viewHolder;

    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int i) {
        holder.friend_name.setText(list.get(i).getName());

        //holder.ly_friend.setBackgroundColor(list.get(i).isSelected() ? Color.WHITE : Color.WHITE);
        holder.friend_select.setImageDrawable(list.get(i).isSelected() ? context.getDrawable(R.drawable.checked_icon) : context.getDrawable(R.drawable.ring));

        holder.thumbnail.setBackground(new ShapeDrawable(new OvalShape()));
        holder.thumbnail.setClipToOutline(true);


        if (!list.get(i).getImage().isEmpty()) {
            Picasso.with(context)
                    .load(list.get(i).getImage())
                    .fit()
                    .into(holder.thumbnail);
            Log.d("이미지", "이미지있음");
        }

        //holder.first_name.setBackgroundResource(itemList.get(position).isSelected() ? R.drawable.checked_icon : R.drawable.contact_circle);
        //holder.first_name.setText(itemList.get(position).isSelected() ? "" : String.valueOf(itemList.get(position).getName().charAt(0)));


        holder.ly_friend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                    list.get(i).setSelected(!list.get(i).isSelected()); //스위치
                    list.get(i).setPosition(i);
                    //Log.d("멀티", itemList.get(position).isSelected() + "선택");
                    //Log.d("라니스터","position  "+position+"\n"+"isture"+itemList.get(position).isSelected());

                    if (list.get(i).isSelected()) {


                        ItemForFriends item = new ItemForFriends();
                        item.setName(list.get(i).getName());
                        item.setUuid(list.get(i).getUuid());
                        item.setId(list.get(i).getId());
                        item.setImage(list.get(i).getImage());
                        item.setFavorite(list.get(i).isFavorite());
                        //item.setAmount(1000);
                        item.setPosition(i);
                        item.setSelected(true);
                        Log.d("멀티", i + "포지션");


                        //onFriendSelectListener.onFriendSelect(item, i);
                        //((MultiModeFragment) context).selectedList(item, position);
                        ((ContactActivity) context).addFromFriendToSelect(item, i);
                        notifyDataSetChanged();

                    }
                    else{

                        ((ContactActivity) context).removeFromFriendToSelect(i);
                        notifyDataSetChanged();
                        //onFriendUnSelectListener.onFriendUnSelect(i);
                    }



            }
        });


    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class ItemViewHolder extends RecyclerView.ViewHolder {

        TextView friend_name;
        ImageView thumbnail;
        ImageView friend_select;
        LinearLayout ly_friend;


        public ItemViewHolder(View itemView) {
            super(itemView);

            friend_name = itemView.findViewById(R.id.friend_name);
            thumbnail = itemView.findViewById(R.id.thumbnail);
            friend_select = itemView.findViewById(R.id.friend_select_box);
            ly_friend = itemView.findViewById(R.id.ly_friend);


        }
    }

    public interface OnAddSelectedListener {
        void onAddSelected();

    }

}
