package com.example.lj.myapplication.Adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.lj.myapplication.Items.AlarmSettingItem;
import com.example.lj.myapplication.Items.ContactItem;
import com.example.lj.myapplication.R;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class AlarmSettingAdapter extends RecyclerView.Adapter<AlarmSettingAdapter.AlarmSettingHolder> {


    private List<AlarmSettingItem> itemList;
    private Context context;

    public AlarmSettingAdapter(Context context, List<AlarmSettingItem> itemList) {
        this.itemList = itemList;
        this.context = context;
    }


    @NonNull
    @Override
    public AlarmSettingHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        LayoutInflater inflater = LayoutInflater.from(context);
        view = inflater.inflate(R.layout.item_alarm_setting, parent, false);
        final AlarmSettingAdapter.AlarmSettingHolder alarmSettingHolder = new AlarmSettingAdapter.AlarmSettingHolder(view);

        return alarmSettingHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull AlarmSettingHolder holder, final int position) {

        holder.alarm_text.setText(itemList.get(position).getAlarm_time());

        //holder.checkBox.setChecked(itemList.get(position).isSelected());
        holder.checkBox.setTag(itemList.get(position));
        holder.checkBox.setChecked(load(String.valueOf(itemList.get(position).getTime())));

        holder.checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CheckBox cb = (CheckBox) v;
                AlarmSettingItem alarmSettingItem = (AlarmSettingItem) cb.getTag();

                alarmSettingItem.setSelected(cb.isChecked());
                itemList.get(position).setSelected(cb.isChecked());


                save(cb.isChecked(), String.valueOf(itemList.get(position).getTime()));


                Toast.makeText(v.getContext(), "Clicked on Checkbox: " + position + " is " + itemList.get(position).isSelected(), Toast.LENGTH_LONG).show();


                //Toast.makeText(v.getContext(), "Clicked on Checkbox: " + cb.getText() + " is "+ cb.isChecked(), Toast.LENGTH_LONG).show();

            }
        });
    }

    private void save(final boolean isChecked, String key) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("alarm_setting_check", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(key, isChecked);
        editor.apply();
    }

    private boolean load(String key) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("alarm_setting_check", Context.MODE_PRIVATE);
        return sharedPreferences.getBoolean(key, false);
    }

    public List<AlarmSettingItem> getStudentist() {
        return itemList;
    }

    @Override
    public int getItemCount() {
        return this.itemList.size();
    }

    public static class AlarmSettingHolder extends RecyclerView.ViewHolder {

        TextView alarm_text;
        TextView phone_number;
        LinearLayout view_container;
        CheckBox checkBox;

        public AlarmSettingHolder(@NonNull View itemView) {
            super(itemView);
            view_container = itemView.findViewById(R.id.contact_container);
            alarm_text = (TextView) itemView.findViewById(R.id.alarm_setting_text);
            phone_number = (TextView) itemView.findViewById(R.id.contact_num);
            checkBox = (CheckBox) itemView.findViewById(R.id.alarm_setting_check);


        }

    }

}
