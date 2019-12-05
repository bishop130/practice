package com.suji.lj.myapplication.Decorators;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.text.style.ForegroundColorSpan;
import android.util.Log;

import com.prolificinteractive.materialcalendarview.spans.DotSpan;
import com.suji.lj.myapplication.R;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.DayViewDecorator;
import com.prolificinteractive.materialcalendarview.DayViewFacade;

public class SelectorDecorator implements DayViewDecorator {


    public SelectorDecorator(Activity context) {


    }

    @Override
    public boolean shouldDecorate(CalendarDay day) {
        return true;
    }

    @Override
    public void decorate(DayViewFacade view) {
        Log.d("켈린더","선택");
        view.addSpan(new DotSpan(5, Color.CYAN));
        view.addSpan(new ForegroundColorSpan(Color.BLACK));

    }

}
