package com.example.lj.myapplication.Decorators;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.text.style.BackgroundColorSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;

import com.example.lj.myapplication.R;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.DayViewDecorator;
import com.prolificinteractive.materialcalendarview.DayViewFacade;

import org.threeten.bp.LocalDate;

import java.util.Date;


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

        view.addSpan(new StyleSpan(Typeface.BOLD));
        view.addSpan(new RelativeSizeSpan(1.4f));
        view.addSpan(new ForegroundColorSpan(today_color));
    }


    public void setDate(LocalDate date) {
        this.date = CalendarDay.from(date);
    }
}
