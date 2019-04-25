package com.suji.lj.myapplication.Decorators;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.text.style.ForegroundColorSpan;

import com.suji.lj.myapplication.R;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.DayViewDecorator;
import com.prolificinteractive.materialcalendarview.DayViewFacade;

import java.util.Collection;
import java.util.HashSet;

public class EventDecoratorForDetail implements DayViewDecorator {

    private int color;
    private HashSet<CalendarDay> dates;
    private final Drawable drawable;

    public EventDecoratorForDetail(int color, Collection<CalendarDay> dates, Activity context) {
        this.color = color;
        this.dates = new HashSet<>(dates);
        drawable = context.getResources().getDrawable(R.drawable.ring);
        drawable.setBounds(0,0,50,50);
    }

    @Override
    public boolean shouldDecorate(CalendarDay day) {
        return dates.contains(day);
    }

    @Override
    public void decorate(DayViewFacade view) {
        //view.addSpan(new DotSpan(5, color));
        view.setSelectionDrawable(drawable);
        view.addSpan(new ForegroundColorSpan(Color.BLACK));
    }
}