package com.suji.lj.myapplication.StepperForm;

import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.suji.lj.myapplication.DaumMapActivity;

import com.suji.lj.myapplication.R;

import ernestoyaquello.com.verticalstepperform.Step;

import static android.content.Context.MODE_PRIVATE;

public class MakeLocationStep extends Step<String> {

    private Button open_map;
    private TextView resultView;


    public MakeLocationStep(String title) {
        this(title, "유효지점은 선택한 장소 기준 반경 100m이내 입니다");
    }

    public MakeLocationStep(String title, String subtitle) {
        super(title, subtitle);
    }


    @Override
    public String getStepData() {

        Log.d("반복", "getStepData");
        SharedPreferences sf = getContext().getSharedPreferences("sFile", MODE_PRIVATE);
        String result = sf.getString("address", "");
        Log.d("반복DATA", result);
        return result;
    }

    @Override
    public String getStepDataAsHumanReadableString() {
        Log.d("반복", "getStepDataAsHumanReadableString");
        SharedPreferences sf = getContext().getSharedPreferences("sFile", MODE_PRIVATE);
        String result = sf.getString("address", "");

        return result;

    }


    @Override
    public void restoreStepData(String data) {
        Log.d("반복", "restoreStepData");


        data = getStepDataAsHumanReadableString();

        resultView.setText(data);


    }

    @Override
    public IsDataValid isStepDataValid(String stepData) {
        SharedPreferences sf = getContext().getSharedPreferences("sFile", MODE_PRIVATE);
        int code = sf.getInt("validCode", 0);
        Log.d("코드좀 보자", String.valueOf(code));
        if (code == 777) {
            Log.d("성공", String.valueOf(code));
            markAsCompleted(true);
            return new IsDataValid(true);
        } else {
            Log.d("실패", String.valueOf(code));
            return new IsDataValid(false);
        }


    }

    @Override
    protected View createStepContentLayout() {
        Log.d("반복", "createStepContentLayout");
        LayoutInflater inflater = LayoutInflater.from(getContext());
        final View locationStepContent = inflater.inflate(R.layout.make_location_step, null, false);
        resultView = (TextView) locationStepContent.findViewById(R.id.location_text);
        open_map = (Button) locationStepContent.findViewById(R.id.open_map);
        open_map.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), DaumMapActivity.class);
                getContext().startActivity(intent);
            }
        });

        return locationStepContent;
    }


    @Override
    protected void onStepOpened(boolean animated) {
        Log.d("반복", "onStepOpened");
    }

    @Override
    protected void onStepClosed(boolean animated) {
        Log.d("반복", "onStepClosed");
    }

    @Override
    protected void onStepMarkedAsCompleted(boolean animated) {
        Log.d("반복", "onStepMarkedAsCompleted");
    }

    @Override
    protected void onStepMarkedAsUncompleted(boolean animated) {
        Log.d("반복", "onStepMarkedAsUncompleted");
    }


}
