package com.suji.lj.myapplication.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.suji.lj.myapplication.Items.ItemForTransaction;
import com.suji.lj.myapplication.R;
import com.suji.lj.myapplication.Utils.Utils;

import java.util.ArrayList;
import java.util.List;

public class RecyclerTransactionAdapter extends RecyclerView.Adapter<RecyclerTransactionAdapter.ViewHolder> {


    List<ItemForTransaction> list;
    Context context;

    public RecyclerTransactionAdapter(Context context, List<ItemForTransaction> list) {
        this.list = list;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.item_recycler_transaction, parent, false);


        return new RecyclerTransactionAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        String title = list.get(position).getTitle();
        String date_time = list.get(position).getDate_time();
        String method = list.get(position).getPayment_method();
        int point = list.get(position).getPoint();
        int pointTransactionCode = list.get(position).getTransactionCode();
        holder.rlMethod.setVisibility(View.GONE);
        holder.rlWithdraw.setVisibility(View.GONE);
        holder.rlPoint.setVisibility(View.GONE);
        holder.rlMission.setVisibility(View.GONE);

        switch (pointTransactionCode){
            case 0:
                /** 포인트 충전 **/
                holder.tv_title.setText("포인트 충전");
                holder.tv_date_time.setText(date_time);
                holder.tv_payment.setText(method);
                holder.tv_point.setText(Utils.makeNumberComma(point));
                holder.tv_point.setTextColor(context.getResources().getColor(R.color.colorSuccess));

                holder.rlMethod.setVisibility(View.VISIBLE);
                holder.rlWithdraw.setVisibility(View.GONE);
                holder.rlPoint.setVisibility(View.VISIBLE);
                holder.rlMission.setVisibility(View.GONE);
                break;

            case 1:
                /** 포인트 분배금 수령 **/

                holder.tv_title.setText("분배금");
                holder.tv_date_time.setText(date_time);
                holder.tv_payment.setText(method);
                holder.tv_point.setText(Utils.makeNumberComma(point));
                holder.tv_point.setTextColor(context.getResources().getColor(R.color.colorSuccess));
                holder.tvTitle.setText(title);

                holder.rlMethod.setVisibility(View.GONE);
                holder.rlWithdraw.setVisibility(View.GONE);
                holder.rlPoint.setVisibility(View.VISIBLE);
                holder.rlMission.setVisibility(View.VISIBLE);

                break;

            case 2:
                /** 포인트 인출**/
                holder.tv_title.setText("포인트 계좌인출");
                holder.tv_date_time.setText(date_time);

                holder.tvAccountNum.setText("계좌번호");
                holder.tv_point.setText(Utils.makeNumberComma(point));
                holder.tv_point.setTextColor(context.getResources().getColor(R.color.colorError));

                holder.rlMethod.setVisibility(View.GONE);
                holder.rlWithdraw.setVisibility(View.VISIBLE);
                holder.rlPoint.setVisibility(View.VISIBLE);
                holder.rlMission.setVisibility(View.GONE);

            case 3:
                /** 약속 보증금 납입**/

                holder.tv_title.setText(title);
                holder.tvTitle.setText(title);
                holder.tv_point.setText(Utils.makeNumberComma(point));


                holder.rlMethod.setVisibility(View.GONE);
                holder.rlWithdraw.setVisibility(View.GONE);
                holder.rlPoint.setVisibility(View.VISIBLE);
                holder.rlMission.setVisibility(View.VISIBLE);



                break;

            case 4:
                /** 보증금 취소 반환**/
                holder.tv_title.setText("약속 취소");
                holder.tvTitle.setText(title);
                holder.tv_point.setText(Utils.makeNumberComma(point));


                holder.rlMethod.setVisibility(View.GONE);
                holder.rlWithdraw.setVisibility(View.GONE);
                holder.rlPoint.setVisibility(View.VISIBLE);
                holder.rlMission.setVisibility(View.VISIBLE);



                break;

            case 5:
                /** 상품 구매 **/
                /** 상품 구매 철회**/

                break;






        }




    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView tv_title;
        TextView tv_date_time;
        TextView tv_payment;
        TextView tv_point;

        RelativeLayout rlWithdraw;
        RelativeLayout rlMethod;
        RelativeLayout rlPoint;
        RelativeLayout rlMission;
        TextView tvAccountNum;
        TextView tvTitle;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);


            tv_title = itemView.findViewById(R.id.tv_title);
            tv_date_time = itemView.findViewById(R.id.tv_date_time);
            tv_payment = itemView.findViewById(R.id.tv_payment);
            tv_point = itemView.findViewById(R.id.tv_point);
            rlWithdraw = itemView.findViewById(R.id.rlWithdraw);
            rlMethod = itemView.findViewById(R.id.rlCashMethod);
            rlPoint = itemView.findViewById(R.id.rlPoint);
            rlMission = itemView.findViewById(R.id.rlMission);
            tvTitle = itemView.findViewById(R.id.tvTitle);

            tvAccountNum = itemView.findViewById(R.id.tvAccountNum);

        }

    }

}
