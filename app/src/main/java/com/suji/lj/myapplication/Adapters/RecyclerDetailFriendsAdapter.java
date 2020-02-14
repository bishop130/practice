package com.suji.lj.myapplication.Adapters;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.suji.lj.myapplication.Items.ContactItemForServer;
import com.suji.lj.myapplication.R;

import java.text.DecimalFormat;
import java.util.List;

public class RecyclerDetailFriendsAdapter extends RecyclerView.Adapter<RecyclerDetailFriendsAdapter.ItemViewHolder>{

    private List<ContactItemForServer> friendsLists;
    public RecyclerDetailFriendsAdapter(List<ContactItemForServer> friendsLists){
        this.friendsLists = friendsLists;
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
        holder.friend_num.setText(friendsLists.get(position).getPhone_num());
        Log.d("친구",friendsLists.get(position).getFriend_name());
        Log.d("친구",friendsLists.get(position).getPhone_num());
        Log.d("친구",String.valueOf(friendsLists.get(position).getAmount()));
        DecimalFormat decimalFormat = new DecimalFormat("###,###");
        String amount = decimalFormat.format(friendsLists.get(position).getAmount())+"원";
        holder.amount.setText(amount);

    }

    @Override
    public int getItemCount() {
        return friendsLists.size();
    }

    public static class ItemViewHolder extends RecyclerView.ViewHolder {
        TextView friend_name;
        TextView friend_num;
        TextView amount;

        public ItemViewHolder(View itemView) {
            super(itemView);

            friend_name = itemView.findViewById(R.id.friend_name);
            friend_num = itemView.findViewById(R.id.friend_num);
            amount = itemView.findViewById(R.id.amount);


        }
    }
}
