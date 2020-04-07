package com.suji.lj.myapplication.Adapters;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;
import com.suji.lj.myapplication.ContactActivity;
import com.suji.lj.myapplication.Fragments.MultiModeFragment;
import com.suji.lj.myapplication.FriendsActivity;
import com.suji.lj.myapplication.Items.ContactItem;
import com.suji.lj.myapplication.Items.ItemForFriends;
import com.suji.lj.myapplication.R;
import com.suji.lj.myapplication.Utils.HangulUtils;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;


public class RecyclerFriendsAdapter extends RecyclerView.Adapter<RecyclerFriendsAdapter.RecyclerViewContactHolder> implements Filterable {

    private List<ItemForFriends> itemList;
    private List<ItemForFriends> exampleListFull;
    private List<ItemForFriends> selected_contact = new ArrayList<>();
    private Context context;
    private int count = 0;
    private static final int MAX_CONTACTS = 10;
    OnFriendSelectListener onFriendSelectListener;
    OnFriendUnSelectListener onFriendUnSelectListener;
    Realm realm;


    public RecyclerFriendsAdapter(Context context, List<ItemForFriends> itemList, Realm realm, OnFriendSelectListener onFriendSelectListener,OnFriendUnSelectListener onFriendUnSelectListener) {
        this.itemList = itemList;
        this.context = context;
        this.exampleListFull = new ArrayList<>(itemList);
        this.realm = realm;
        this.onFriendSelectListener = onFriendSelectListener;
        this.onFriendUnSelectListener = onFriendUnSelectListener;

    }

    @NonNull
    @Override
    public RecyclerViewContactHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view;
        LayoutInflater inflater = LayoutInflater.from(context);
        view = inflater.inflate(R.layout.item_friend_select, parent, false);
        RecyclerViewContactHolder recyclerViewContactHolder = new RecyclerViewContactHolder(view);

        return recyclerViewContactHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerViewContactHolder holder, final int position) {

        holder.name.setText(itemList.get(position).getName());
        //holder.first_name.setText(String.valueOf(itemList.get(position).getName().charAt(0)));
        holder.contact_container.setBackgroundColor(itemList.get(position).isSelected() ? Color.WHITE : Color.WHITE);
        holder.select_box.setImageDrawable(itemList.get(position).isSelected() ? context.getDrawable(R.drawable.checked_icon) : context.getDrawable(R.drawable.ring));

        holder.checked_contact.setBackground(new ShapeDrawable(new OvalShape()));
        holder.checked_contact.setClipToOutline(true);


        if(!itemList.get(position).getImage().isEmpty()){
            Picasso.with(context)
                    .load(itemList.get(position).getImage())
                    .fit()
                    .into(holder.checked_contact);
            Log.d("이미지","이미지있음");
        }

        //holder.first_name.setBackgroundResource(itemList.get(position).isSelected() ? R.drawable.checked_icon : R.drawable.contact_circle);
        //holder.first_name.setText(itemList.get(position).isSelected() ? "" : String.valueOf(itemList.get(position).getName().charAt(0)));


        holder.contact_container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                itemList.get(position).setSelected(!itemList.get(position).isSelected()); //스위치
                itemList.get(position).setPosition(position);
                Log.d("멀티", itemList.get(position).isSelected() + "선택");
                //Log.d("라니스터","position  "+position+"\n"+"isture"+itemList.get(position).isSelected());

                if (itemList.get(position).isSelected()) {


                    ItemForFriends item = new ItemForFriends();
                    item.setName(itemList.get(position).getName());
                    item.setUuid(itemList.get(position).getUuid());
                    item.setId(itemList.get(position).getId());
                    item.setImage(itemList.get(position).getImage());
                    item.setFavorite(itemList.get(position).isFavorite());
                    //item.setAmount(1000);
                    item.setPosition(position);
                    item.setSelected(true);
                    Log.d("멀티", position + "포지션");


                    onFriendSelectListener.onFriendSelect(item, position);
                    //((MultiModeFragment) context).selectedList(item, position);

                } else {


                    onFriendUnSelectListener.onFriendUnSelect(position);
                    //((MultiModeFragment) context).testDelete(position);


                }


            }
        });


    }

    @Override
    public void onViewRecycled(@NonNull RecyclerViewContactHolder holder) {
        super.onViewRecycled(holder);

    }

    @Override
    public int getItemCount() {
        return this.itemList.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public Filter getFilter() {
        return exampleFilter;
    }

    private Filter exampleFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<ItemForFriends> filteredList = new ArrayList<>();

            if (constraint == null || constraint.length() == 0) {
                filteredList.addAll(exampleListFull);
            } else {
                String filterPattern = constraint.toString().toLowerCase().trim();
                for (ItemForFriends item : exampleListFull) {
                    String miniName = HangulUtils.getHangulInitialSound(item.getName(), constraint.toString());
                    if (miniName.indexOf(constraint.toString()) >= 0) {
                        filteredList.add(item);
                    } else if (item.getName().toLowerCase().contains(filterPattern)) {
                        filteredList.add(item);
                    }
                }
            }

            FilterResults results = new FilterResults();
            results.values = filteredList;
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {

            itemList.clear();
            itemList.addAll((List) results.values);
            notifyDataSetChanged();
        }
    };

    private void checkcontact() {
        for (int i = 0; i < selected_contact.size(); i++) {
            ItemForFriends ct = selected_contact.get(i);
            String name = ct.getName();
            //Log.d("라니스터",name);
        }


    }

    private void iterator(int position) {
        for (int i = 0; i < selected_contact.size(); i++) {
            ItemForFriends ct = selected_contact.get(i);
            if (ct.getPosition() == position) {
                int idx = i;
                selected_contact.remove(idx);


            }


        }
        //((ContactActivity) context).recyclerViewConfirmed(selected_contact);
    }


    public static class RecyclerViewContactHolder extends RecyclerView.ViewHolder {

        TextView name;
        RelativeLayout contact_container;
        ImageView checked_contact;
        ImageView select_box;

        public RecyclerViewContactHolder(@NonNull View itemView) {
            super(itemView);
            contact_container = itemView.findViewById(R.id.contact_container);
            name = itemView.findViewById(R.id.contact_name);
            checked_contact = itemView.findViewById(R.id.checked_contact);
            select_box = itemView.findViewById(R.id.friend_select_box);


        }

    }

    public List<ItemForFriends> getStudentist() {
        return itemList;
    }

    public List<ItemForFriends> getSelected_contact() {
        return selected_contact;
    }

    public interface OnFriendsCountFromContactListener {
        void onFriendsCountFromContact(int num);

    }

    public interface OnFriendSelectListener {
        void onFriendSelect(ItemForFriends item, int position);
    }
    public interface OnFriendUnSelectListener{
        void onFriendUnSelect(int position);
    }


}


