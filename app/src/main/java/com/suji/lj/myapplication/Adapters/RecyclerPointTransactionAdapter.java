package com.suji.lj.myapplication.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.suji.lj.myapplication.Items.ItemForPointTransaction;
import com.suji.lj.myapplication.R;
import com.suji.lj.myapplication.Utils.Utils;

import java.util.List;

public class RecyclerPointTransactionAdapter extends RecyclerView.Adapter<RecyclerPointTransactionAdapter.ViewHolder> {


    List<ItemForPointTransaction> list;
    Context context;

    public RecyclerPointTransactionAdapter(List<ItemForPointTransaction> list, Context context) {
        this.list = list;

        this.context = context;

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.item_transaction, parent, false);


        return new RecyclerPointTransactionAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {


        holder.tv_title.setText(list.get(position).getTitle());
        holder.tv_date_time.setText(list.get(position).getDate_time());

        if (list.get(position).isDeposit()) {//

            holder.tv_withdraw_deposit.setText("입금액");
            String amount_text = "+" + Utils.makeNumberCommaWon(Integer.valueOf(list.get(position).getAmount())) + " P";
            holder.tv_amount.setText(amount_text);
            //holder.tv_amount.setTextColor(context.getResources().getColor(R.color.errorColor));


        } else {

            holder.tv_withdraw_deposit.setText("출금금액");
            String amount_text = "-" + Utils.makeNumberCommaWon(Integer.valueOf(list.get(position).getAmount())) + " P";
            holder.tv_amount.setText(amount_text);

        }


    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView tv_title;
        TextView tv_date_time;
        TextView tv_amount;
        TextView tv_withdraw_deposit;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);


            tv_title = itemView.findViewById(R.id.tv_title);
            tv_date_time = itemView.findViewById(R.id.tv_date_time);
            tv_amount = itemView.findViewById(R.id.tv_amount);
            tv_withdraw_deposit = itemView.findViewById(R.id.tv_withdraw_transfer);

        }

    }
}
