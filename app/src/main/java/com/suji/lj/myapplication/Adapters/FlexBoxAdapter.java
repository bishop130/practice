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
import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;

public class FlexBoxAdapter extends RecyclerView.Adapter<FlexBoxAdapter.ViewHolder> {

    Context context;
    RealmResults<ContactItem> arrayList;
    public OnFriendsNumListener onFriendsNumListener;

    Realm realm;

    public FlexBoxAdapter(){}

    public FlexBoxAdapter(Context context, RealmResults<ContactItem> arrayList, OnFriendsNumListener onFriendsNumListener, Realm realm) {
        this.context = context;
        this.arrayList = arrayList;
        this.onFriendsNumListener = onFriendsNumListener;
        this.realm = realm;
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
                realm.beginTransaction();
                arrayList.deleteFromRealm(position);
                realm.commitTransaction();

                notifyItemRemoved(position);
                notifyItemRangeChanged(position, arrayList.size());
                onFriendsNumListener.onFriendsNum(arrayList.size(),position);
                //onFriendsListListener.onFriendsList(arrayList);

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
        void onFriendsNum(int size, int position);


    }



}