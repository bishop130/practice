package com.suji.lj.myapplication.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.suji.lj.myapplication.AccountActivity;
import com.suji.lj.myapplication.Items.UserAccountItem;
import com.suji.lj.myapplication.R;

import java.util.List;
import java.util.zip.Inflater;

public class RecyclerAccountManagementAdapter extends RecyclerView.Adapter<RecyclerAccountManagementAdapter.ItemViewHolder>{

    List<UserAccountItem> list;
    Context context;


    public RecyclerAccountManagementAdapter(Context context, List<UserAccountItem> list){
        this.list = list;
        this.context = context;



    }


    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.item_recycler_account,parent,false);
        RecyclerAccountManagementAdapter.ItemViewHolder itemViewHolder = new RecyclerAccountManagementAdapter.ItemViewHolder(view);

        return itemViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {
        holder.bank_name.setText(list.get(position).getBank_name());
        holder.account_num.setText(list.get(position).getAccount_num_masked());
        holder.ly_account.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String bank_name = list.get(position).getBank_name();
                String account_num = list.get(position).getAccount_num_masked();
                ((AccountActivity)context).accountManagement(bank_name,account_num);

            }
        });

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class ItemViewHolder extends RecyclerView.ViewHolder {

        TextView bank_name;
        TextView account_num;
        LinearLayout ly_account;


        public ItemViewHolder(View itemView) {
            super(itemView);

            bank_name = itemView.findViewById(R.id.bank_name);
            account_num = itemView.findViewById(R.id.account_num);
            ly_account = itemView.findViewById(R.id.ly_account);


        }
    }
}
