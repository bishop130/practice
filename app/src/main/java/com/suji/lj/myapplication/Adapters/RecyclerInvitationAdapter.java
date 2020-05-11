package com.suji.lj.myapplication.Adapters;

import android.content.Context;
import android.content.Intent;
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
import com.suji.lj.myapplication.InvitationInfoActivity;
import com.suji.lj.myapplication.Items.ItemForMultiModeRequest;
import com.suji.lj.myapplication.MultiModeActivity;
import com.suji.lj.myapplication.R;

import java.util.ArrayList;
import java.util.List;

public class RecyclerInvitationAdapter extends RecyclerView.Adapter<RecyclerInvitationAdapter.ViewHolder> {


    ArrayList<ItemForMultiModeRequest> list;
    Context context;

    public RecyclerInvitationAdapter(Context context, ArrayList<ItemForMultiModeRequest> list) {

        this.list = list;
        this.context = context;

    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.item_recycler_invitation, parent, false);


        return new RecyclerInvitationAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int i) {


        String title = list.get(i).getTitle();
        String manager_name = list.get(i).getManager_name();
        int friends_num = list.get(i).getFriendRequestList().size();
        String thumbnail = list.get(i).getManager_thumbnail();


        Log.d("멀티모드", title + "title");
        Log.d("멀티모드", manager_name + "manager_name");
        Log.d("멀티모드", thumbnail + "thumbnail");
        Log.d("멀티모드", friends_num + "");

        if (!thumbnail.equals("")) {
            Picasso.with(context).load(list.get(i).getManager_thumbnail()).fit().into(holder.thumbnail);
        }


        holder.text.setText(manager_name + "님이 " + title + "미션에 초대하셨습니다.");
        holder.ly_multi_request.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, InvitationInfoActivity.class);
                intent.putExtra("item",list.get(i));
                context.startActivity(intent);

            }
        });

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView text;

        ImageView thumbnail;
        LinearLayout ly_multi_request;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            ly_multi_request = itemView.findViewById(R.id.ly_multi_request);
            text = itemView.findViewById(R.id.text);
            thumbnail = itemView.findViewById(R.id.thumbnail);


        }

    }
}
