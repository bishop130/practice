package com.suji.lj.myapplication.Adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.suji.lj.myapplication.ContactActivity;
import com.suji.lj.myapplication.Items.ContactItem;
import com.suji.lj.myapplication.R;

import java.util.List;

import io.realm.Realm;

public class RecyclerContactFriendSelectAdapter extends RecyclerView.Adapter<RecyclerContactFriendSelectAdapter.ContactResponseHolder>{
    Context context;
    private RecyclerContactFriendSelectAdapter.ContactResponseHolder contactResponseHolder;
    RecyclerViewContactAdapter adapter;
    List<ContactItem> itemList;
    OnFriendsCountListener onFriendsCountListener;
    OnRemoveSelectedListener onRemoveSelectedListener;
    Realm realm;

    public RecyclerContactFriendSelectAdapter(Context context, List<ContactItem> itemList, OnFriendsCountListener onFriendsCountListener, Realm realm, OnRemoveSelectedListener onRemoveSelectedListener) {

        this.context = context;
        this.itemList=itemList;
        this.onFriendsCountListener = onFriendsCountListener;
        this.realm = realm;
        this.onRemoveSelectedListener = onRemoveSelectedListener;


    }

    @NonNull
    @Override
    public ContactResponseHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view;
        LayoutInflater inflater = LayoutInflater.from(context);
        view = inflater.inflate(R.layout.item_flexbox, parent, false);
        contactResponseHolder = new RecyclerContactFriendSelectAdapter.ContactResponseHolder(view);

        return contactResponseHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ContactResponseHolder holder, int position) {
        //List<ContactItem> ctList = ((RecyclerViewContactAdapter) adapter).getSelected_contact();

        if(itemList.get(position).getContact_or_friend()==1){
            holder.title.setTextColor(context.getResources().getColor(R.color.colorPrimary));
        }else{
            holder.title.setTextColor(context.getResources().getColor(R.color.black));
        }
        holder.title.setText("#"+itemList.get(position).getDisplayName());
        //holder.phone_number.setText(itemList.get(position).getPhoneNumbers());
        //holder.first_name.setText(String.valueOf(itemList.get(position).getDisplayName().charAt(0)));
        Log.d("라니스터","onBindViewHolder_contactResponse"+ position);

        holder.title.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(itemList.get(position).getContact_or_friend()==0) {
                    int p = itemList.get(position).getPosition();
                    onRemoveSelectedListener.onRemoveSelected(p);
                    ((ContactActivity) context).removeFromSelectToContact(position);

                    //notifyDataSetChanged();


                    onFriendsCountListener.onFriendsCount(itemList.size());
                }else{

                    int p = itemList.get(position).getPosition();

                    ((ContactActivity) context).removeFromSelectToFriend(position);
                    onFriendsCountListener.onFriendsCount(itemList.size());

                }






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

    public interface OnRemoveSelectedListener{
        void onRemoveSelected(int position);

    }


}
