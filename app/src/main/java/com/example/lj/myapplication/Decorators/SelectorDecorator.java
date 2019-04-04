package com.example.lj.myapplication.Decorators;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;

import com.example.lj.myapplication.R;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.DayViewDecorator;
import com.prolificinteractive.materialcalendarview.DayViewFacade;

public class SelectorDecorator implements DayViewDecorator {

    private final Drawable drawable;

    public SelectorDecorator(Activity context) {
        drawable = context.getResources().getDrawable(R.drawable.selector_ring);
        drawable.setBounds(0,0,50,50);

    }

    @Override
    public boolean shouldDecorate(CalendarDay day) {
        return true;
    }

    @Override
    public void decorate(DayViewFacade view) {
        view.setSelectionDrawable(drawable);

        view.addSpan(new ForegroundColorSpan(Color.BLACK));

    }

}
