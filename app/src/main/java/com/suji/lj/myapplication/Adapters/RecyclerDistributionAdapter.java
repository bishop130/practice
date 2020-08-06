package com.suji.lj.myapplication.Adapters;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.suji.lj.myapplication.Fragments.MultiModeFragment;
import com.suji.lj.myapplication.Items.ItemForFriends;
import com.suji.lj.myapplication.MultiModeActivity;
import com.suji.lj.myapplication.R;
import com.suji.lj.myapplication.Utils.Utils;

import java.util.ArrayList;
import java.util.List;

public class RecyclerDistributionAdapter extends RecyclerView.Adapter<RecyclerDistributionAdapter.ViewHolder> {


    Context context;
    OnCheckPortionListener listener;
    int portion_id;
    List<Integer> list_portion;
    int numOfPeople;

    public RecyclerDistributionAdapter(Context context, int numOfPeople, OnCheckPortionListener listener, List<Integer> list_portion) {

        this.numOfPeople = numOfPeople;
        this.listener = listener;
        this.list_portion = list_portion;
        this.context = context;

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.item_distribution, parent, false);


        return new RecyclerDistributionAdapter.ViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.ranking.setText("#" + (position + 1));
        Log.d("에디트", list_portion.size() + "리스트포션 사이즈");
        Log.d("에디트", list_portion.get(position) + "리스트포션 내용");
        holder.portion.setText(String.valueOf(list_portion.get(position)));


        holder.portion.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    Log.d("에디트", v.getText().toString());
                    int portion = Integer.valueOf(v.getText().toString());
                    list_portion.set(position, portion);
                    InputMethodManager imm = (InputMethodManager) v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);


                    if (position > 0) {
                        if (portion > list_portion.get(position - 1)) {
                            //warning
                            Log.d("에디트", "warning");

                            holder.portion_warning.setVisibility(View.VISIBLE);
                            listener.onCheckPortion(list_portion);

                        } else {
                            Log.d("에디트", "good");
                            listener.onCheckPortion(list_portion);
                            holder.portion_warning.setVisibility(View.GONE);

                        }

                    } else {
                        listener.onCheckPortion(list_portion);

                    }
                    return true;
                }
                return false;
            }
        });
        //listener.onCheckPortion(integerList, position);

    }

    @Override
    public int getItemCount() {
        return numOfPeople;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView ranking;
        EditText portion;

        LinearLayout portion_warning;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            ranking = itemView.findViewById(R.id.ranking);
            portion = itemView.findViewById(R.id.edit_portion);
            portion_warning = itemView.findViewById(R.id.portion_warning);


        }

    }

    public interface OnCheckPortionListener {
        void onCheckPortion(List<Integer> list);

    }
}
