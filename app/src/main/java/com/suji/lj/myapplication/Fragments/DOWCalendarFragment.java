package com.suji.lj.myapplication.Fragments;

import android.app.DatePickerDialog;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toast;

import com.suji.lj.myapplication.R;

import java.util.Calendar;
import java.util.Date;


public class DOWCalendarFragment extends Fragment {


    final Calendar cal = Calendar.getInstance();
    TextView date_show;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_dowcalendar, container, false);
        // Inflate the layout for this fragment
        date_show=view.findViewById(R.id.date_show);
        date_show.setText("1234");

        Button button = view.findViewById(R.id.date_picker);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog datePicker = new DatePickerDialog(getActivity(),
                        R.style.DatePicker, datePickerListener,
                        cal.get(Calendar.YEAR),
                        cal.get(Calendar.MONTH),
                        cal.get(Calendar.DAY_OF_MONTH));
                datePicker.setCancelable(false);
                datePicker.show();
            }
        });






        return view;
    }

    // when dialog box is closed, below method will be called.
    private DatePickerDialog.OnDateSetListener datePickerListener = (view, selectedYear, selectedMonth, selectedDay) -> {
        String year1 = String.valueOf(selectedYear);
        String month1 = String.valueOf(selectedMonth + 1);
        String day1 = String.valueOf(selectedDay);

        Toast.makeText(getActivity(),year1+month1+day1,Toast.LENGTH_LONG).show();
        date_show.setText(year1+month1+day1);

    };
}
