package com.suji.lj.myapplication.Adapters;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.suji.lj.myapplication.Items.ItemPortion;
import com.suji.lj.myapplication.R;

import java.util.List;

public class RecyclerPortionInvitationAdapter extends RecyclerView.Adapter<RecyclerPortionInvitationAdapter.ViewHolder>  {


    List<ItemPortion> itemPortionList;

    public RecyclerPortionInvitationAdapter(List<ItemPortion> itemPortionList) {
        this.itemPortionList = itemPortionList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.item_distribution_invitation, parent, false);



        return new RecyclerPortionInvitationAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        int rank = position+1;
        holder.ranking.setText("# "+rank);
        Log.d("비율",itemPortionList.get(position).getPortion()+"");
        int portion = itemPortionList.get(position).getPortion();
        holder.portion.setText(portion+" %");




    }

    @Override
    public int getItemCount() {
        return itemPortionList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView ranking;
        TextView portion;



        public ViewHolder(@NonNull View itemView) {
            super(itemView);



            ranking = itemView.findViewById(R.id.ranking);
            portion = itemView.findViewById(R.id.portion);



        }

    }
}
