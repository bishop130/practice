package com.suji.lj.myapplication.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.suji.lj.myapplication.Items.ContactItem;

import com.suji.lj.myapplication.R;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class RecyclerViewContactAdapter extends RecyclerView.Adapter<RecyclerViewContactAdapter.RecyclerViewContactHolder> implements Filterable {

    private List<ContactItem> itemList;
    private List<ContactItem> exampleListFull;
    private Context context;



    public RecyclerViewContactAdapter(Context context, List<ContactItem> itemList) {
        this.itemList = itemList;
        this.context = context;
        this.exampleListFull = new ArrayList<>(itemList);
    }

    @NonNull
    @Override
    public RecyclerViewContactHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view;
        LayoutInflater inflater = LayoutInflater.from(context);
        view = inflater.inflate(R.layout.contact_item, parent, false);
        final RecyclerViewContactHolder recyclerViewContactHolder = new RecyclerViewContactHolder(view);

        return recyclerViewContactHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerViewContactHolder holder, final int position) {

        final int pos = position;

        holder.name.setText(itemList.get(position).getDisplayName());
        holder.phone_number.setText(itemList.get(position).getPhoneNumbers());

        holder.checkBox.setChecked(itemList.get(position).isSelected());
        holder.checkBox.setTag(itemList.get(position));

        holder.checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CheckBox cb = (CheckBox) v;
                ContactItem contactItem = (ContactItem)cb.getTag();

                contactItem.setSelected(cb.isChecked());
                itemList.get(pos).setSelected(cb.isChecked());

                Toast.makeText(v.getContext(),"Clicked on Checkbox: " + cb.getText() + " is "
                        + cb.isChecked(), Toast.LENGTH_LONG).show();
            }
        });

    }
    @Override
    public void onViewRecycled(RecyclerViewContactHolder holder) {
        super.onViewRecycled(holder);

        holder.checkBox.setOnCheckedChangeListener(null);
    }

    @Override
    public int getItemCount() {
        return this.itemList.size();
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


    public static class RecyclerViewContactHolder extends RecyclerView.ViewHolder {

        TextView name;
        TextView phone_number;
        RelativeLayout view_container;
        CheckBox checkBox;

        public RecyclerViewContactHolder(@NonNull View itemView) {
            super(itemView);
            view_container = itemView.findViewById(R.id.contact_container);
            name = (TextView) itemView.findViewById(R.id.contact_name);
            phone_number = (TextView) itemView.findViewById(R.id.contact_num);
            checkBox = (CheckBox) itemView.findViewById(R.id.check_box);



        }

    }
    public List<ContactItem> getStudentist() {
        return itemList;
    }



}
