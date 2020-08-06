package com.suji.lj.myapplication.Adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.suji.lj.myapplication.Items.ItemForBank;
import com.suji.lj.myapplication.R;

import java.util.List;

public class RecyclerBankSelectionAdapter extends RecyclerView.Adapter<RecyclerBankSelectionAdapter.ItemViewHolder> {


    List<ItemForBank> bankList;
    BottomSheetDialog bottomSheetDialog;
    OnSetupBankListener onSetupBankListener;
    Context context;

    public RecyclerBankSelectionAdapter(Context context, List<ItemForBank> bankList, BottomSheetDialog bottomSheetDialog, OnSetupBankListener onSetupBankListener) {
        this.bankList = bankList;
        this.bottomSheetDialog = bottomSheetDialog;
        this.onSetupBankListener = onSetupBankListener;

        this.context = context;

    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        view = inflater.inflate(R.layout.item_bank, parent, false);

        return new RecyclerBankSelectionAdapter.ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {

        String bank_name = bankList.get(position).getBank_name();
        String bank_code = bankList.get(position).getBank_id();

        holder.bank_name.setText(bankList.get(position).getBank_name());

        //holder.ivBankImage.setBackground(new ShapeDrawable(new OvalShape()));
       // holder.ivBankImage.setClipToOutline(true);


        //holder.ivBankImage.setImageResource(R.drawable.kb_bank);

        switch (Integer.valueOf(bank_code)) {
            case 2:
                Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.kdb_bank);

                holder.ivBankImage.setImageBitmap(bitmap);
                break;
            case 3:
                holder.ivBankImage.setImageResource(R.drawable.ibk_bank);
                break;
            case 4:
                holder.ivBankImage.setImageResource(R.drawable.kb_bank);
                break;

            case 7:
                holder.ivBankImage.setImageResource(R.drawable.soohyup_bank);
                break;
            case 11:
                holder.ivBankImage.setImageResource(R.drawable.nh_bank);
                break;
            case 20:
                holder.ivBankImage.setImageResource(R.drawable.woori_bank);
                break;
            case 23:
                holder.ivBankImage.setImageResource(R.drawable.sc_bank);
                break;
            case 27:
                holder.ivBankImage.setImageResource(R.drawable.city_bank);
                break;
            case 31:
                holder.ivBankImage.setImageResource(R.drawable.daegu_bank);
                break;
            case 32:
                holder.ivBankImage.setImageResource(R.drawable.busan_bank);
                break;
            case 34:
                holder.ivBankImage.setImageResource(R.drawable.gwangju_bank);
                break;
            case 35:
                holder.ivBankImage.setImageResource(R.drawable.jeju_bank);
                break;
            case 37:
                holder.ivBankImage.setImageResource(R.drawable.junbook_bank);
                break;
            case 39:
                holder.ivBankImage.setImageResource(R.drawable.kyungnam_bank);
                break;
            case 81:
                holder.ivBankImage.setImageResource(R.drawable.keb_hana_bank);
                break;
            case 88:
                holder.ivBankImage.setImageResource(R.drawable.shinhan_bank);
                break;
            case 89:
                holder.ivBankImage.setImageResource(R.drawable.k_bank);
                break;
            case 90:
                holder.ivBankImage.setImageResource(R.drawable.kakao_bank);
                break;


        }


        holder.ly_select_bank.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                onSetupBankListener.onSetupBank(bank_name, bank_code);

                bottomSheetDialog.dismiss();
            }
        });

    }

    @Override
    public int getItemCount() {
        return bankList.size();
    }

    public static class ItemViewHolder extends RecyclerView.ViewHolder {

        TextView bank_name;
        ImageView ivBankImage;
        LinearLayout ly_select_bank;


        public ItemViewHolder(View itemView) {
            super(itemView);

            bank_name = itemView.findViewById(R.id.bank_name);
            ivBankImage = itemView.findViewById(R.id.ivBankImage);

            ly_select_bank = itemView.findViewById(R.id.ly_select_bank);

        }
    }

    public interface OnSetupBankListener {
        void onSetupBank(String bank_name, String bank_code);


    }
}
