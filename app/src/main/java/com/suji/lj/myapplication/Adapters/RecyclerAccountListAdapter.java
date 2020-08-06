package com.suji.lj.myapplication.Adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.suji.lj.myapplication.Items.ItemForAccountList;
import com.suji.lj.myapplication.R;

import java.util.List;

public class RecyclerAccountListAdapter extends RecyclerView.Adapter<RecyclerAccountListAdapter.ItemViewHolder> {


    //이름,계좌번호,은행명

    private List<ItemForAccountList> accountLists;

    OnCompleteAccountListener onCompleteAccountListener;
    Context context;

    public RecyclerAccountListAdapter(Context context, List<ItemForAccountList> accountLists, OnCompleteAccountListener onCompleteAccountListener) {

        this.accountLists = accountLists;

        this.onCompleteAccountListener = onCompleteAccountListener;
        this.context = context;

    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        view = inflater.inflate(R.layout.item_account_list, parent, false);

        return new RecyclerAccountListAdapter.ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {

        String bank_name = accountLists.get(position).getBank_name();
        String account_num = accountLists.get(position).getAccount_num();
        String account_holder_name = accountLists.get(position).getAccount_holder_name();
        int bankCode = Integer.valueOf(accountLists.get(position).getBank_code());




        switch (bankCode){
            case 2:
                holder.img_bank.setImageDrawable(context.getResources().getDrawable(R.drawable.ibk_bank));
                break;
            case 3:
                break;
            case 4:
                break;

            case 7:
                break;
            case 11:
                break;
            case 20:
                break;
            case 23:
                break;
            case 27:
                break;
            case 31:
                break;
            case 32:
                break;
            case 34:
                break;
            case 35:
                break;
            case 39:
                break;
            case 81:
                break;
            case 88:
                break;
            case 89:
                break;
            case 90:
                break;




        }








        holder.tv_bank_name_account_num.setText(bank_name + " " + account_num);
        holder.tv_account_holder_name.setText(account_holder_name);


        holder.ly_recent_account.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                onCompleteAccountListener.onCompleteAccount(accountLists.get(position));


            }
        });

    }

    @Override
    public int getItemCount() {
        return accountLists.size();
    }

    public static class ItemViewHolder extends RecyclerView.ViewHolder {

        TextView tv_bank_name_account_num;
        TextView tv_account_holder_name;
        ImageView img_bank;
        LinearLayout ly_recent_account;


        public ItemViewHolder(View itemView) {
            super(itemView);

            tv_bank_name_account_num = itemView.findViewById(R.id.tv_bank_name_account_num);
            tv_account_holder_name = itemView.findViewById(R.id.tv_account_holder_name);
            ly_recent_account = itemView.findViewById(R.id.ly_recent_account);
            img_bank = itemView.findViewById(R.id.ivBankImage);

        }
    }

    public interface OnCompleteAccountListener {
        void onCompleteAccount(ItemForAccountList item);


    }


}
