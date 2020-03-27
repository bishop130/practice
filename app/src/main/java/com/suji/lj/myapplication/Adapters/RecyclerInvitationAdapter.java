package com.suji.lj.myapplication.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.suji.lj.myapplication.InvitationInfoActivity;
import com.suji.lj.myapplication.Items.ItemForMultiModeRequest;
import com.suji.lj.myapplication.MultiModeActivity;
import com.suji.lj.myapplication.R;

import java.util.List;

public class RecyclerInvitationAdapter extends RecyclerView.Adapter<RecyclerInvitationAdapter.ViewHolder>{



    List<ItemForMultiModeRequest> list;
    Context context;

    public RecyclerInvitationAdapter(Context context, List<ItemForMultiModeRequest> list){

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

        holder.address.setText(list.get(i).getAddress());
        holder.title.setText(list.get(i).getTitle());
        holder.manager_name.setText(list.get(i).getManager_name());
        holder.container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                context.startActivity(new Intent(context, InvitationInfoActivity.class));
            }
        });

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView title;
        TextView manager_name;
        TextView address;

        LinearLayout container;




        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            container = itemView.findViewById(R.id.container);
            title = itemView.findViewById(R.id.title);
            manager_name = itemView.findViewById(R.id.manager_name);
            address = itemView.findViewById(R.id.address);



        }

    }
}
