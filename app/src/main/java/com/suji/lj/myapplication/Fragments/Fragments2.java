package com.suji.lj.myapplication.Fragments;

import android.graphics.Color;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.os.Bundle;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.suji.lj.myapplication.R;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.DayViewDecorator;
import com.prolificinteractive.materialcalendarview.DayViewFacade;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;
import com.prolificinteractive.materialcalendarview.format.ArrayWeekDayFormatter;
import com.prolificinteractive.materialcalendarview.format.TitleFormatter;

import org.threeten.bp.DayOfWeek;
import org.threeten.bp.format.DateTimeFormatter;

import java.util.Calendar;
import java.util.List;


public class Fragments2 extends Fragment implements OnDateSelectedListener {

    MaterialCalendarView materialCalendarView;
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("EEE, d MMM yyyy");


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_fragments2, container, false);
        materialCalendarView = (MaterialCalendarView)view.findViewById(R.id.material_calendarView);

        materialCalendarView.setDateSelected(CalendarDay.today(),true);
        Calendar min = Calendar.getInstance();

        materialCalendarView.state().edit()
                .setMinimumDate(CalendarDay.today())
                .commit();




        materialCalendarView.addDecorator(new SundayDecorator());



        //SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy MM");
        //DateTimeFormatter.ofPattern(String.valueOf(simpleDateFormat));
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
        //materialCalendarView.setTitleFormatter(new MonthArrayTitleFormatter(getResources().getTextArray(R.array.custom_months)));
        materialCalendarView.setWeekDayFormatter(new ArrayWeekDayFormatter(getResources().getTextArray(R.array.custom_weekdays)));
        materialCalendarView.setSelectionMode(MaterialCalendarView.SELECTION_MODE_MULTIPLE);
        materialCalendarView.setOnDateChangedListener(this);




        materialCalendarView.setOnDateChangedListener(new OnDateSelectedListener() {
            @Override
            public void onDateSelected(@NonNull MaterialCalendarView materialCalendarView, @NonNull CalendarDay calendarDay, boolean b) {

                Toast.makeText(getActivity(),calendarDay.toString(),Toast.LENGTH_LONG).show();


            }
        });

        return view;
    }

    @Override
    public void onDateSelected(@NonNull MaterialCalendarView materialCalendarView, @NonNull CalendarDay calendarDay, boolean selected) {
        Toast.makeText(getActivity(),"시간"+calendarDay.getDate(),Toast.LENGTH_LONG).show();
    }

    public class SundayDecorator implements DayViewDecorator {

        public SundayDecorator(){

        }
        @Override
        public boolean shouldDecorate(final CalendarDay day) {
            final DayOfWeek weekDay = day.getDate().getDayOfWeek();
            return weekDay == DayOfWeek.SUNDAY;
        }

        @Override
        public void decorate(final DayViewFacade view) {
            view.addSpan(new ForegroundColorSpan(Color.RED));
        }

        public void getSelectedDateClick(final View v) {
            final List<CalendarDay> selectedDates = materialCalendarView.getSelectedDates();
            if (!selectedDates.isEmpty()) {
                Toast.makeText(getActivity(), selectedDates.toString(), Toast.LENGTH_SHORT).show();
                Log.e("GettersActivity", selectedDates.toString());
            } else {
                Toast.makeText(getActivity(), "No Selection", Toast.LENGTH_SHORT).show();
            }}
    }



}
