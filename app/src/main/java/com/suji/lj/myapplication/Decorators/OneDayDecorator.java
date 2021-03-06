package com.suji.lj.myapplication.Decorators;

import android.graphics.Typeface;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;

import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.DayViewDecorator;
import com.prolificinteractive.materialcalendarview.DayViewFacade;
import com.prolificinteractive.materialcalendarview.spans.DotSpan;

import org.threeten.bp.LocalDate;


public class OneDayDecorator implements DayViewDecorator {

    private CalendarDay date;
    private int today_color;




    public OneDayDecorator(int today_color) {
        date = CalendarDay.today();
        this.today_color = today_color;


    }

    @Override
    public boolean shouldDecorate(CalendarDay day) {
        return date != null && day.equals(date);
    }

    @Override
    public void decorate(DayViewFacade view) {

        view.addSpan(new DotSpan(10,today_color));

        //view.addSpan(new StyleSpan(Typeface.BOLD));
        //view.addSpan(new RelativeSizeSpan(1.4f));
        //view.addSpan(new ForegroundColorSpan(today_color));
    }


    public void setDate(CalendarDay date) {
        this.date = date;
    }
}
