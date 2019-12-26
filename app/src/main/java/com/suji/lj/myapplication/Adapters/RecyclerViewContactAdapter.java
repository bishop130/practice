package com.suji.lj.myapplication.Adapters;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.suji.lj.myapplication.ContactActivity;
import com.suji.lj.myapplication.Items.ContactItem;

import com.suji.lj.myapplication.MissionCartActivity;
import com.suji.lj.myapplication.R;
import com.suji.lj.myapplication.Utils.HangulUtils;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class RecyclerViewContactAdapter extends RecyclerView.Adapter<RecyclerViewContactAdapter.RecyclerViewContactHolder> implements Filterable {

    private List<ContactItem> itemList;
    private List<ContactItem> exampleListFull;
    private List<ContactItem> selected_contact = new ArrayList<>();
    private Context context;
    private int count = 0;
    private static final int MAX_CONTACTS = 10;
    OnFriendsCountFromContactListener onFriendsCountFromContactListener;



    public RecyclerViewContactAdapter(Context context, List<ContactItem> itemList, OnFriendsCountFromContactListener onFriendsCountFromContactListener) {
        this.itemList = itemList;
        this.context = context;
        this.exampleListFull = new ArrayList<>(itemList);

        this.onFriendsCountFromContactListener = onFriendsCountFromContactListener;
    }

    @NonNull
    @Override
    public RecyclerViewContactHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view;
        LayoutInflater inflater = LayoutInflater.from(context);
        view = inflater.inflate(R.layout.contact_item, parent, false);
        RecyclerViewContactHolder recyclerViewContactHolder = new RecyclerViewContactHolder(view);

        return recyclerViewContactHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerViewContactHolder holder, final int position) {

        holder.name.setText(itemList.get(position).getDisplayName());
        holder.phone_number.setText(itemList.get(position).getPhoneNumbers());
        holder.first_name.setText(String.valueOf(itemList.get(position).getDisplayName().charAt(0)));
        holder.contact_container.setBackgroundColor(itemList.get(position).isSelected() ? Color.WHITE : Color.WHITE);
        holder.first_name.setBackgroundResource(itemList.get(position).isSelected() ? R.drawable.checked_contact : R.drawable.contact_circle);
        holder.first_name.setText(itemList.get(position).isSelected() ? "" : String.valueOf(itemList.get(position).getDisplayName().charAt(0)));


        holder.contact_container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                itemList.get(position).setSelected(!itemList.get(position).isSelected()); //스위치
                itemList.get(position).setPosition(position);
                //Log.d("라니스터","position  "+position+"\n"+"isture"+itemList.get(position).isSelected());

                if(itemList.get(position).isSelected()) {

                    ContactItem contactItem = new ContactItem();
                    contactItem.setDisplayName(itemList.get(position).getDisplayName());
                    contactItem.setPhoneNumbers(itemList.get(position).getPhoneNumbers());
                    contactItem.setPosition(position);
                    contactItem.setSelected(true);
                    //onFriendsCountFromContactListener.onFriendsCountFromContact(itemList.size());


                    ((ContactActivity) context).selectedList(contactItem);

                   // notifyDataSetChanged();
                }
                else{

                    //onFriendsCountFromContactListener.onFriendsCountFromContact(itemList.size());

                    ((ContactActivity) context).testDelete(position);
                    //notifyDataSetChanged();

                }


            }
        });


        //((ContactActivity) context).refreshThumb(selected_contact);
/*
        holder.name.setText(itemList.get(position).getDisplayName());
        holder.phone_number.setText(itemList.get(position).getPhoneNumbers());
        holder.first_name.setText(String.valueOf(itemList.get(position).getDisplayName().charAt(0)));

        holder.contact_container.setBackgroundColor(itemList.get(position).isSelected() ? Color.WHITE : Color.WHITE);
        holder.first_name.setBackgroundResource(itemList.get(position).isSelected() ? R.drawable.checked_contact : R.drawable.contact_circle);
        holder.first_name.setText(itemList.get(position).isSelected() ? "" : String.valueOf(itemList.get(position).getDisplayName().charAt(0)));
        //holder.first_name.setVisibility(itemList.get(position).isSelected() ? View.INVISIBLE : View.VISIBLE);
        //holder.checked_contact.setVisibility(itemList.get(position).isSelected() ? View.GONE : View.VISIBLE);
        //holder.first_name.setImageResource(R.drawable.account);
        Log.d("라니스터","onBindViewHolder_recyclerViewContact");
        holder.contact_container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (count < MAX_CONTACTS) {

                    //((ContactActivity) context).refreshSelection(position);
                    itemList.get(position).setSelected(!itemList.get(position).isSelected());


                    if (itemList.get(position).isSelected()) {
                        Log.d("라니스터","포지션 선택"+ position+"   "+itemList.get(position).isSelected());
                        count = count + 1;
                        ContactItem contactItem = new ContactItem();
                        contactItem.setDisplayName(itemList.get(position).getDisplayName());
                        contactItem.setPhoneNumbers(itemList.get(position).getPhoneNumbers());
                        contactItem.setPosition(position);

                        selected_contact.add(contactItem);
                        //contactActivity.recyclerViewConfirmed(selected_contact);
                        //((ContactActivity) context).recyclerViewConfirmed(selected_contact);

                        ((ContactActivity) context).refreshThumb(selected_contact, itemList);
                        //notifyDataSetChanged();

                        holder.contact_container.setBackgroundColor(itemList.get(position).isSelected() ? Color.WHITE : Color.WHITE);
                        holder.first_name.setBackgroundResource(itemList.get(position).isSelected() ? R.drawable.checked_contact : R.drawable.contact_circle);
                        holder.first_name.setText(itemList.get(position).isSelected() ? "" : String.valueOf(itemList.get(position).getDisplayName().charAt(0)));


                        checkcontact();

                    } else {
                        Log.d("라니스터","포지션 선택해제"+ position+"   "+itemList.get(position).isSelected());
                        ContactItem contactItem = new ContactItem();
                        contactItem.setDisplayName(itemList.get(position).getDisplayName());
                        contactItem.setPhoneNumbers(itemList.get(position).getPhoneNumbers());
                        contactItem.setPosition(position);

                        count = count - 1;
                        iterator(position);

                        ((ContactActivity) context).refreshThumb(selected_contact,itemList);

                        holder.contact_container.setBackgroundColor(itemList.get(position).isSelected() ? Color.WHITE : Color.WHITE);
                        holder.first_name.setBackgroundResource(itemList.get(position).isSelected() ? R.drawable.checked_contact : R.drawable.contact_circle);
                        holder.first_name.setText(itemList.get(position).isSelected() ? "" : String.valueOf(itemList.get(position).getDisplayName().charAt(0)));


                        checkcontact();
                    }
                } else {
                    Toast.makeText(context, "전송 최대 인원은 10명입니다.", Toast.LENGTH_LONG).show();
                    if(itemList.get(position).isSelected()){
                        itemList.get(position).setSelected(!itemList.get(position).isSelected());
                        holder.contact_container.setBackgroundColor(itemList.get(position).isSelected() ? Color.WHITE : Color.WHITE);
                        holder.first_name.setBackgroundResource(itemList.get(position).isSelected() ? R.drawable.checked_contact : R.drawable.contact_circle);
                        holder.first_name.setText(itemList.get(position).isSelected() ? "" : String.valueOf(itemList.get(position).getDisplayName().charAt(0)));

                        ContactItem contactItem = new ContactItem();
                        contactItem.setDisplayName(itemList.get(position).getDisplayName());
                        contactItem.setPhoneNumbers(itemList.get(position).getPhoneNumbers());
                        contactItem.setPosition(position);
                        count = count-1;
                        iterator(position);

                        checkcontact();
                    }

                }


                Log.d("숫자", count + "");
                //itemList.get(holder.getAdapterPosition()).setContact_count(itemList.get(holder.getAdapterPosition()).getContact_count() + 1);


            }
        });
        */


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
            List<ContactItem> filteredList = new ArrayList<>();

            if (constraint == null || constraint.length() == 0) {
                filteredList.addAll(exampleListFull);
            } else {
                String filterPattern = constraint.toString().toLowerCase().trim();
                for (ContactItem item : exampleListFull) {
                    String miniName = HangulUtils.getHangulInitialSound(item.getDisplayName(), constraint.toString());
                    if (miniName.indexOf(constraint.toString()) >= 0) {
                        filteredList.add(item);
                    } else if (item.getDisplayName().toLowerCase().contains(filterPattern)) {
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
            ContactItem ct = selected_contact.get(i);
            String name = ct.getDisplayName();
            //Log.d("라니스터",name);
        }


    }

    private void iterator(int position) {
        for (int i = 0; i < selected_contact.size(); i++) {
            ContactItem ct = selected_contact.get(i);
            if (ct.getPosition() == position) {
                int idx = i;
                selected_contact.remove(idx);


            }


        }
        //((ContactActivity) context).recyclerViewConfirmed(selected_contact);
    }


    public static class RecyclerViewContactHolder extends RecyclerView.ViewHolder {

        TextView name;
        TextView phone_number;
        RelativeLayout contact_container;
        TextView first_name;
        ImageView checked_contact;

        public RecyclerViewContactHolder(@NonNull View itemView) {
            super(itemView);
            contact_container = itemView.findViewById(R.id.contact_container);
            name = itemView.findViewById(R.id.contact_name);
            phone_number = itemView.findViewById(R.id.contact_num);
            first_name = itemView.findViewById(R.id.first_name);
            checked_contact = itemView.findViewById(R.id.checked_contact);


        }

    }

    public List<ContactItem> getStudentist() {
        return itemList;
    }

    public List<ContactItem> getSelected_contact() {
        return selected_contact;
    }

    public interface OnFriendsCountFromContactListener{
        void onFriendsCountFromContact(int num);

    }


}
