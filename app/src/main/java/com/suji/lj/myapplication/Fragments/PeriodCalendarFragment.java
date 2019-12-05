package com.suji.lj.myapplication.Fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.NumberPicker;
import android.widget.TextView;

import com.suji.lj.myapplication.R;

public class PeriodCalendarFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER

    int sum_days;
    int sum_count;
    TextView end_date;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_period_calendar, container, false);
        // Inflate the layout for this fragment

        end_date = view.findViewById(R.id.end_date);
        NumberPicker max_days = view.findViewById(R.id.max_days);
        NumberPicker min_days = view.findViewById(R.id.min_days);
        NumberPicker how_many = view.findViewById(R.id.how_many);
        max_days.setMinValue(1);
        max_days.setMaxValue(30);
        how_many.setMinValue(1);
        how_many.setMaxValue(32);
        max_days.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                min_days.setMinValue(1);
                min_days.setMaxValue(newVal);
                sum_days = newVal;

                ;
            }
        });
        min_days.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {

            }
        });
        how_many.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {

                sum_count=newVal;
            }
        });
        sum_date();
        return view;
    }

    void sum_date(){

        end_date.setText(sum_days*sum_count+"");

    }

}
