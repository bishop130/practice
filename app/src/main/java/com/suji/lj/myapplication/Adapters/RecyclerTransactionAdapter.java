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
    private final static int CASH_TO_POINT = 100;
    private final static int POINT_DEPOSIT = 101;
    private final static int POINT_TO_CASH = 102;
    private final static int POINT_REWARD_DISTRIBUTION = 103;
    private final static int POINT_REFUND = 104;



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
        String date_time = list.get(position).getDateTime();
        String method = list.get(position).getPaymentMethod();
        int point = list.get(position).getPoint();
        int cash = list.get(position).getCash();
        int pointTransactionCode = list.get(position).getCode();
        holder.rlMethod.setVisibility(View.GONE);
        holder.rlWithdraw.setVisibility(View.GONE);
        holder.rlPoint.setVisibility(View.GONE);
        holder.rlMission.setVisibility(View.GONE);

        switch (pointTransactionCode) {
            case CASH_TO_POINT:
                /** 포인트 충전 **/
                holder.tv_title.setText("포인트 충전");
                holder.tv_date_time.setText(date_time);
                holder.tv_payment.setText(method);
                holder.tv_point.setText(Utils.makeNumberComma(cash) + " P");
                holder.tv_point.setTextColor(context.getResources().getColor(R.color.colorSuccess));

                holder.rlMethod.setVisibility(View.VISIBLE);
                holder.rlWithdraw.setVisibility(View.GONE);
                holder.rlPoint.setVisibility(View.VISIBLE);
                holder.rlMission.setVisibility(View.GONE);
                break;

            case POINT_REWARD_DISTRIBUTION:
                /** 포인트 분배금 수령 **/

                holder.tv_title.setText("분배금");
                holder.tv_date_time.setText(date_time);
                holder.tv_payment.setText(method);
                holder.tv_point.setText(Utils.makeNumberComma(point) + " P");
                holder.tv_point.setTextColor(context.getResources().getColor(R.color.colorSuccess));
                holder.tvMissionTitle.setText(title);

                holder.rlMethod.setVisibility(View.GONE);
                holder.rlWithdraw.setVisibility(View.GONE);
                holder.rlPoint.setVisibility(View.VISIBLE);
                holder.rlMission.setVisibility(View.VISIBLE);

                break;

            case POINT_TO_CASH:
                /** 포인트 인출**/
                String accountNum = list.get(position).getAccountNum();
                String bankName = list.get(position).getBankName();
                String accountHolderName = list.get(position).getAccountHolderName();
                holder.tv_title.setText("포인트 계좌인출");
                holder.tv_date_time.setText(date_time);

                String textAccount = bankName+" "+accountNum+"\n"+accountHolderName;
                holder.tvAccountNum.setText(textAccount);
                holder.tv_point.setText(Utils.makeNumberComma(point) + " P");
                holder.tv_point.setTextColor(context.getResources().getColor(R.color.colorPrimary));

                holder.rlMethod.setVisibility(View.GONE);
                holder.rlWithdraw.setVisibility(View.VISIBLE);
                holder.rlPoint.setVisibility(View.VISIBLE);
                holder.rlMission.setVisibility(View.GONE);
                break;

            case POINT_DEPOSIT:
                /** 약속 보증금 납입**/

                holder.tv_title.setText("약속 등록");
                holder.tv_date_time.setText(date_time);
                holder.tvMissionTitle.setText(title);
                holder.tv_point.setText(Utils.makeNumberComma(point) + " P");
                holder.tv_point.setTextColor(context.getResources().getColor(R.color.colorPrimary));

                holder.rlMethod.setVisibility(View.GONE);
                holder.rlWithdraw.setVisibility(View.GONE);
                holder.rlPoint.setVisibility(View.VISIBLE);
                holder.rlMission.setVisibility(View.VISIBLE);


                break;

            case POINT_REFUND:
                /** 보증금 취소 반환**/
                holder.tv_title.setText("약속 취소");
                holder.tvMissionTitle.setText(title);
                holder.tv_point.setText(Utils.makeNumberComma(point) + " P");
                holder.tv_point.setTextColor(context.getResources().getColor(R.color.colorSuccess));


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
        TextView tvMissionTitle;


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
            tvMissionTitle = itemView.findViewById(R.id.tvMissionTitle);

            tvAccountNum = itemView.findViewById(R.id.tvAccountNum);

        }

    }

}
