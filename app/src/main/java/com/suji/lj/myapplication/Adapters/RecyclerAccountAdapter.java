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

import com.suji.lj.myapplication.Items.UserAccountItem;
import com.suji.lj.myapplication.R;
import com.suji.lj.myapplication.SingleModeActivity;

import java.util.List;

public class RecyclerAccountAdapter extends RecyclerView.Adapter<RecyclerAccountAdapter.ItemViewHolder>{

    List<UserAccountItem> userAccountItemList;
    private  View.OnClickListener positiveListener;

    Context context;
    public RecyclerAccountAdapter(Context context, List<UserAccountItem>userAccountItemList){
        this.userAccountItemList = userAccountItemList;
        this.context = context;




    }


    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.item_recycler_account,parent,false);
        RecyclerAccountAdapter.ItemViewHolder itemViewHolder = new RecyclerAccountAdapter.ItemViewHolder(view);

        return itemViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {

        Log.d("계좌",userAccountItemList.get(position).getAccount_num_masked());
        holder.bank_name.setText(userAccountItemList.get(position).getBank_name());
        holder.account_num.setText(userAccountItemList.get(position).getAccount_num_masked());
        holder.ly_account.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String fintech_num = userAccountItemList.get(position).getFintech_use_num();
                ((SingleModeActivity)context).changeAccount(fintech_num);
            }
        });

    }

    @Override
    public int getItemCount() {
        return userAccountItemList.size();
    }

    public static class ItemViewHolder extends RecyclerView.ViewHolder {

        TextView bank_name,account_num;
        LinearLayout ly_account;



        public ItemViewHolder(View itemView) {
            super(itemView);

            ly_account = itemView.findViewById(R.id.ly_account);
            bank_name = itemView.findViewById(R.id.bank_name);
            account_num = itemView.findViewById(R.id.account_num);


        }
    }
}
