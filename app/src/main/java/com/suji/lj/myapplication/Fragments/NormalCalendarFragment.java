package com.suji.lj.myapplication.Fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.os.Parcelable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioGroup;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;
import com.prolificinteractive.materialcalendarview.format.TitleFormatter;
import com.suji.lj.myapplication.Items.DateItem;
import com.suji.lj.myapplication.Items.MissionCartItem;
import com.suji.lj.myapplication.MainActivity;
import com.suji.lj.myapplication.R;
import com.suji.lj.myapplication.Utils.DateTimeUtils;

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

    List<CalendarDay> calendarDayList = new ArrayList<>();
    RealmList<DateItem> dateList = new RealmList<>();
    OnDateChangedListener onDateChangedListener;
    Realm realm;

    public NormalCalendarFragment(RealmList<DateItem> dateList,Realm realm){
        this.dateList = dateList;
        this.realm = realm;
    }
    public NormalCalendarFragment(){

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_normal_calendar, container, false);
        // Inflate the layout for this fragment
        materialCalendarView = view.findViewById(R.id.material_calendarView);


        //first
/*
        realm = Realm.getDefaultInstance();
        long count = realm.where(MissionCartItem.class).count();
        if(count == 0){
            materialCalendarView.setSelectedDate(CalendarDay.today());

        }else{
            for(int i=0; i<dateList.size(); i++){
                int year = dateList.get(i).getYear();
                int month = dateList.get(i).getMonth();
                int day = dateList.get(i).getDay();
                materialCalendarView.setDateSelected(CalendarDay.from(year,month,day),true);

            }

        }


 */



        materialCalendarView.state().edit()
                .setMinimumDate(CalendarDay.today())
                .commit();




        //calendarDayList = materialCalendarView.getSelectedDates();
        //sortingCalendar(calendarDayList);

        materialCalendarView.setOnDateChangedListener(new OnDateSelectedListener() {
            @Override
            public void onDateSelected(@NonNull MaterialCalendarView widget, @NonNull CalendarDay date, boolean selected) {

                calendarDayList = widget.getSelectedDates();
                onDateChangedListener.onDateChanged(widget,date,selected);
               //sortingCalendar(calendarDayList);


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

        //void onDateChanged(RealmList<DateItem> DateItemArrayList,String minDate,String maxDate,MaterialCalendarView calendarView);
        void onDateChanged(MaterialCalendarView calendarView,CalendarDay date,boolean selected);
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
        dateList.clear();
    }

    private void sortingCalendar(List<CalendarDay> calendarDayList){
        //String max_date_string = max_date.getYear()+"-"+max_date.getMonth()+"-"+max_date.getDate();
        int maxYear = 0;
        int maxMonth = 0;
        int maxDay = 0;
        int minYear = 2100;
        int minMonth = 13;
        int minDay = 40;
        String minDate = "";
        String maxDate = "";

        Log.d("파베", "날짜갯수"+calendarDayList.size()+"");

        for (int i = 0; i < calendarDayList.size(); i++) {

            DateItem dateItem = new DateItem();

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
            minDate = DateTimeUtils.makeDateForServer(minYear,minMonth,minDay);
            maxDate = DateTimeUtils.makeDateForServer(maxYear,maxMonth,maxDay);

/*
            if(i==calendarDayList.size()) {
                dateItem.setMin_date(minDate);
                dateItem.setMax_date(maxDate);
            }

 */
            dateList.add(dateItem);
        }

        for(int j=0; j<dateList.size();j++) {
            Log.d("파베", dateList.get(j).getYear() + "-" + dateList.get(j).getMonth() + "-" + dateList.get(j).getDay());
        }

        //onDateChangedListener.onDateChanged(dateList, minDate,maxDate,materialCalendarView);
        dateList.clear();


    }
}
