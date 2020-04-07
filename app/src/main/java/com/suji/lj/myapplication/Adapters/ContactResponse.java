package com.suji.lj.myapplication.Adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.suji.lj.myapplication.ContactActivity;
import com.suji.lj.myapplication.Items.ContactItem;
import com.suji.lj.myapplication.R;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;

public class ContactResponse extends RecyclerView.Adapter<ContactResponse.ContactResponseHolder>{
    Context context;
    private ContactResponse.ContactResponseHolder contactResponseHolder;
    RecyclerViewContactAdapter adapter;
    List<ContactItem> itemList;
    OnFriendsCountListener onFriendsCountListener;
    Realm realm;

    public ContactResponse(Context context, List<ContactItem> itemList, OnFriendsCountListener onFriendsCountListener, Realm realm) {

        this.context = context;
        this.itemList=itemList;
        this.onFriendsCountListener = onFriendsCountListener;
        this.realm = realm;


    }

    @NonNull
    @Override
    public ContactResponseHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view;
        LayoutInflater inflater = LayoutInflater.from(context);
        view = inflater.inflate(R.layout.item_flexbox, parent, false);
        contactResponseHolder = new ContactResponse.ContactResponseHolder(view);

        return contactResponseHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ContactResponseHolder holder, int position) {
        //List<ContactItem> ctList = ((RecyclerViewContactAdapter) adapter).getSelected_contact();

        holder.title.setText("#"+itemList.get(position).getDisplayName());
        //holder.phone_number.setText(itemList.get(position).getPhoneNumbers());
        //holder.first_name.setText(String.valueOf(itemList.get(position).getDisplayName().charAt(0)));
        Log.d("라니스터","onBindViewHolder_contactResponse"+ position);

        holder.title.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ((ContactActivity) context).refreshSelection(position);

                //notifyDataSetChanged();


                onFriendsCountListener.onFriendsCount(itemList.size());





            }
        });



    }

    @Override
    public int getItemCount() {
        return this.itemList.size();
    }

    public class ContactResponseHolder extends RecyclerView.ViewHolder{
        TextView name;
        TextView phone_number;
        LinearLayout selected_container;
        TextView first_name;
        TextView title;

        public ContactResponseHolder(@NonNull View itemView){
            super(itemView);
            phone_number = itemView.findViewById(R.id.contact_num);
            first_name = itemView.findViewById(R.id.first_name);
            title = itemView.findViewById(R.id.tvTitle);
        }

    }
    public interface OnFriendsCountListener{
        void onFriendsCount(int num);


    }


}
