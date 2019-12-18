package com.suji.lj.myapplication.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.suji.lj.myapplication.Items.ContactItem;
import com.suji.lj.myapplication.MissionCartActivity;
import com.suji.lj.myapplication.R;

import java.util.ArrayList;

public class FlexBoxAdapter extends RecyclerView.Adapter<FlexBoxAdapter.ViewHolder> {

    Context context;
    ArrayList<ContactItem> arrayList = new ArrayList<>();
    OnFriendsNumListener onFriendsNumListener;

    public FlexBoxAdapter(Context context, ArrayList<ContactItem> arrayList,OnFriendsNumListener onFriendsNumListener) {
        this.context = context;
        this.arrayList = arrayList;
        this.onFriendsNumListener = onFriendsNumListener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_flexbox, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        holder.title.setText("#" + arrayList.get(position).getDisplayName());
        holder.title.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                arrayList.remove(position);
                notifyItemRemoved(position);
                notifyItemRangeChanged(position, arrayList.size());
                onFriendsNumListener.onFriendsNum(arrayList.size());

            }
        });


    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView title;


        public ViewHolder(View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.tvTitle);

        }
    }


    public interface OnFriendsNumListener {
        void onFriendsNum(int friends_num);


    }


}