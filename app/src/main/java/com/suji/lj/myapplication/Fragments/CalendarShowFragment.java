package com.suji.lj.myapplication.Fragments;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.format.TitleFormatter;
import com.suji.lj.myapplication.Adapters.RecyclerInvitationDateTimeAdapter;
import com.suji.lj.myapplication.Items.ItemForDateTime;
import com.suji.lj.myapplication.R;

import java.util.List;

public class CalendarShowFragment extends Fragment {
    MaterialCalendarView materialCalendarView;
    RecyclerView recycler_date_time;
    List<ItemForDateTime> dateTimeList;
    Context context;
    public CalendarShowFragment(Context context, List<ItemForDateTime> dateTimeList) {
        this.dateTimeList = dateTimeList;
        this.context = context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_calendar_show, container, false);


        materialCalendarView = view.findViewById(R.id.material_calendarView);
        recycler_date_time = view.findViewById(R.id.recycler_date_time);

        materialCalendarView.state().edit()
                .setMinimumDate(CalendarDay.today())
                .commit();


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

        displayCalendar();


        return view;
    }

    private void displayCalendar(){




        setupRecyclerDateTime(dateTimeList);

        if (dateTimeList.size() != 0) {
            int year = dateTimeList.get(0).getYear();
            int month = dateTimeList.get(0).getMonth();
            int day = dateTimeList.get(0).getDay();
            materialCalendarView.state().edit().setMinimumDate(CalendarDay.from(year, month, day)).commit();
            materialCalendarView.setSelectionMode(MaterialCalendarView.SELECTION_MODE_NONE);
            //materialCalendarView.setShowOtherDates(MaterialCalendarView.SHOW_ALL);

        }


        for (int i = 0; i < dateTimeList.size(); i++) {

            int year = dateTimeList.get(i).getYear();
            int month = dateTimeList.get(i).getMonth();
            int day = dateTimeList.get(i).getDay();
            Log.d("번들 날", year + " " + month + " " + day);

            materialCalendarView.setDateSelected(CalendarDay.from(year, month, day), true);


        }

    }
    private void setupRecyclerDateTime(List<ItemForDateTime> list) {

        RecyclerInvitationDateTimeAdapter adapter = new RecyclerInvitationDateTimeAdapter(context, list);
        recycler_date_time.setLayoutManager(new LinearLayoutManager(context));
        recycler_date_time.setAdapter(adapter);


    }

}
