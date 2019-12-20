package com.suji.lj.myapplication.Fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioGroup;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;
import com.prolificinteractive.materialcalendarview.format.TitleFormatter;
import com.suji.lj.myapplication.Items.DateItem;
import com.suji.lj.myapplication.MainActivity;
import com.suji.lj.myapplication.R;

import org.threeten.bp.LocalDate;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmList;
import io.realm.annotations.RealmClass;

public class NormalCalendarFragment extends Fragment{

    private MaterialCalendarView materialCalendarView;
    private RadioGroup selection_mode_radio_group;
    List<CalendarDay> calendarDayList = new ArrayList<>();
    RealmList<DateItem> dateList = new RealmList<>();
    OnDateChangedListener onDateChangedListener;
    Realm realm;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_normal_calendar, container, false);
        // Inflate the layout for this fragment
        materialCalendarView = view.findViewById(R.id.material_calendarView);
        selection_mode_radio_group = view.findViewById(R.id.normal_calendar_radio_group);


        materialCalendarView.state().edit()
                .setMinimumDate(CalendarDay.today())
                .commit();
        materialCalendarView.setSelectedDate(CalendarDay.today());



        realm = Realm.getDefaultInstance();
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
                dateList.clear();
                calendarDayList = widget.getSelectedDates();

                //String max_date_string = max_date.getYear()+"-"+max_date.getMonth()+"-"+max_date.getDate();
                int maxYear = 0;
                int maxMonth = 0;
                int maxDay = 0;
                int minYear = 2100;
                int minMonth = 13;
                int minDay = 40;

                DateItem dateItem = realm.createObject(DateItem.class);

                    for (int i = 0; i < calendarDayList.size(); i++) {
                        int year = calendarDayList.get(i).getYear();
                        int month = calendarDayList.get(i).getMonth();
                        int day = calendarDayList.get(i).getDay();
                        dateItem.setYear(calendarDayList.get(i).getYear());
                        dateItem.setMonth(calendarDayList.get(i).getMonth());
                        dateItem.setDay(calendarDayList.get(i).getDay());

                        if (minYear == year) {
                            if (minMonth == month) {
                                if (minDay > day) {
                                    minDay = day;
                                }
                            } else if (minMonth > month) {
                                minMonth = month;
                                minDay = day;
                            }
                        } else if (minYear > year) {
                            minYear = year;
                            minMonth = month;
                            minDay = day;
                        }
                        if (maxYear == year) {
                            if (maxMonth == month) {
                                if (maxDay < day) {
                                    maxDay = day;
                                }
                            } else if (maxMonth < month) {
                                maxMonth = month;
                                maxDay = day;
                            }
                        } else if (maxYear < year) {
                            maxYear = year;
                            maxMonth = month;
                            maxDay = day;
                        }
                        String minDate = minYear + "-" + minMonth + "-" + minDay;
                        String maxDate = maxYear + "-" + maxMonth + "-" + maxDay;

                        if(i==calendarDayList.size()-1) {
                            dateItem.setMin_date(minDate);
                            dateItem.setMax_date(maxDate);
                        }


                        dateList.add(dateItem);
                    }

                onDateChangedListener.onDateChanged(dateList);


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





    public interface OnDateChangedListener{

        void onDateChanged(RealmList<DateItem> DateItemArrayList);
    }

    @Override
    public void onAttach(@NonNull Context context){
        super.onAttach(context);
        if(context instanceof OnDateChangedListener){
            onDateChangedListener = (OnDateChangedListener)context;


        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        onDateChangedListener = null;
    }
}
