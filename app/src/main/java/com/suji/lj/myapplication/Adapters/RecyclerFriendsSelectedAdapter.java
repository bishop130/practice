package com.suji.lj.myapplication.Adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.suji.lj.myapplication.ContactActivity;
import com.suji.lj.myapplication.FriendsActivity;
import com.suji.lj.myapplication.Items.ContactItem;
import com.suji.lj.myapplication.Items.ItemForFriends;
import com.suji.lj.myapplication.R;

import java.util.List;

import io.realm.Realm;

public class RecyclerFriendsSelectedAdapter extends RecyclerView.Adapter<RecyclerFriendsSelectedAdapter.ContactResponseHolder> {
    Context context;
    private RecyclerFriendsSelectedAdapter.ContactResponseHolder contactResponseHolder;
    RecyclerViewContactAdapter adapter;
    List<ItemForFriends> itemList;
    OnFriendsCountListener onFriendsCountListener;
    Realm realm;
    OnRefreshSelectListener onRefreshSelectListener;

    public RecyclerFriendsSelectedAdapter(Context context, List<ItemForFriends> itemList, Realm realm, OnRefreshSelectListener onRefreshSelectListener) {

        this.context = context;
        this.itemList = itemList;
        this.realm = realm;
        this.onRefreshSelectListener = onRefreshSelectListener;


    }

    @NonNull
    @Override
    public ContactResponseHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view;
        LayoutInflater inflater = LayoutInflater.from(context);
        view = inflater.inflate(R.layout.item_flexbox, parent, false);

        return new RecyclerFriendsSelectedAdapter.ContactResponseHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ContactResponseHolder holder, int position) {
        //List<ContactItem> ctList = ((RecyclerViewContactAdapter) adapter).getSelected_contact();

        holder.title.setText("#" + itemList.get(position).getName());
        //holder.phone_number.setText(itemList.get(position).getPhoneNumbers());
        //holder.first_name.setText(String.valueOf(itemList.get(position).getDisplayName().charAt(0)));
        Log.d("라니스터", "onBindViewHolder_contactResponse" + position);

        holder.title.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //((FriendsActivity) context).refreshSelection(position);
                onRefreshSelectListener.onRefreshSelect(position);

                //notifyDataSetChanged();


               // onFriendsCountListener.onFriendsCount(itemList.size());


            }
        });


    }

    @Override
    public int getItemCount() {
        return this.itemList.size();
    }

    public class ContactResponseHolder extends RecyclerView.ViewHolder {
        TextView name;
        TextView phone_number;
        LinearLayout selected_container;
        TextView first_name;
        TextView title;

        public ContactResponseHolder(@NonNull View itemView) {
            super(itemView);
            phone_number = itemView.findViewById(R.id.contact_num);
            first_name = itemView.findViewById(R.id.first_name);
            title = itemView.findViewById(R.id.tvTitle);

        }

    }

    public interface OnFriendsCountListener {
        void onFriendsCount(int num);


    }

    public interface OnRefreshSelectListener {
        void onRefreshSelect(int position);
    }


}

