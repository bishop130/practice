package com.suji.lj.myapplication.Adapters;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.suji.lj.myapplication.Items.ContactItem;
import com.suji.lj.myapplication.Items.ContactItemForServer;
import com.suji.lj.myapplication.R;

import java.text.DecimalFormat;
import java.util.List;

public class RecyclerDetailFriendsAdapter extends RecyclerView.Adapter<RecyclerDetailFriendsAdapter.ItemViewHolder>{

    private List<ContactItem> friendsLists;
    public RecyclerDetailFriendsAdapter(List<ContactItem> friendsLists){
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

        holder.friend_name.setText("#"+friendsLists.get(position).getDisplayName());
        Log.d("친구",friendsLists.get(position).getDisplayName());

        if(friendsLists.get(position).getContact_or_friend()==0){
            holder.friend_num.setText("("+friendsLists.get(position).getPhoneNumbers()+")");
        }else{
            holder.friend_num.setText("");
        }






    }

    @Override
    public int getItemCount() {
        return friendsLists.size();
    }

    public static class ItemViewHolder extends RecyclerView.ViewHolder {
        TextView friend_name;
        TextView friend_num;

        public ItemViewHolder(View itemView) {
            super(itemView);

            friend_name = itemView.findViewById(R.id.friend_name);
            friend_num = itemView.findViewById(R.id.friend_num);


        }
    }
}
