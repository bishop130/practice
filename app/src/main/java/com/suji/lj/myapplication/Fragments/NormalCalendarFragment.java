package com.suji.lj.myapplication.Fragments;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioGroup;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;
import com.prolificinteractive.materialcalendarview.format.TitleFormatter;
import com.suji.lj.myapplication.R;

public class NormalCalendarFragment extends Fragment{

    private MaterialCalendarView materialCalendarView;
    private RadioGroup selection_mode_radio_group;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_normal_calendar, container, false);
        // Inflate the layout for this fragment
        materialCalendarView = view.findViewById(R.id.material_calendarView);
        selection_mode_radio_group = view.findViewById(R.id.normal_calendar_radio_group);
        selection_mode_radio_group.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId){
                    case R.id.single_selection:
                        materialCalendarView.setSelectionMode(MaterialCalendarView.SELECTION_MODE_SINGLE);
                    case R.id.period_selection:
                        materialCalendarView.setSelectionMode(MaterialCalendarView.SELECTION_MODE_RANGE);
                }
            }
        });

        materialCalendarView.setOnDateChangedListener(new OnDateSelectedListener() {
            @Override
            public void onDateSelected(@NonNull MaterialCalendarView widget, @NonNull CalendarDay date, boolean selected) {
                date = materialCalendarView.getMinimumDate();

            }
        });
        materialCalendarView.setTitleFormatter(new TitleFormatter() {
            @Override
            public CharSequence format(CalendarDay calendarDay) {

                StringBuffer buffer = new StringBuffer();
                int yearOne = calendarDay.getYear();
                int monthOne = calendarDay.getMonth();
                buffer.append(yearOne).append("년  ").append(monthOne).append("월");
                return buffer;
            }
        });



        return view;
    }


}
