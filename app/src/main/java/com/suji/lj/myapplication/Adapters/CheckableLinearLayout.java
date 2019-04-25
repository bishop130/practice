package com.suji.lj.myapplication.Adapters;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.CheckBox;
import android.widget.Checkable;
import android.widget.LinearLayout;

import com.suji.lj.myapplication.R;

public class CheckableLinearLayout extends LinearLayout implements Checkable {

    // 만약 CheckBox가 아닌 View를 추가한다면 아래의 변수 사용 가능.
    // private boolean mIsChecked ;

    public CheckableLinearLayout(Context context, AttributeSet attrs) {
        super(context, attrs);

        // mIsChecked = false ;
    }

    @Override
    public boolean isChecked() {
        CheckBox cb = (CheckBox) findViewById(R.id.alarm_setting_check) ;

        return cb.isChecked() ;
        // return mIsChecked ;
    }

    @Override
    public void toggle() {
        CheckBox cb = (CheckBox) findViewById(R.id.alarm_setting_check) ;

        setChecked(cb.isChecked() ? false : true) ;
        // setChecked(mIsChecked ? false : true) ;
    }

    @Override
    public void setChecked(boolean checked) {
        CheckBox cb = (CheckBox) findViewById(R.id.alarm_setting_check) ;

        if (cb.isChecked() != checked) {
            cb.setChecked(checked) ;
            Log.d("췌크",String.valueOf(checked));

        }
        Log.d("췌크1",String.valueOf(checked));
        // CheckBox 가 아닌 View의 상태 변경.
    }
}