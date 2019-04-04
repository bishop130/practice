package com.example.lj.myapplication.StepperForm;

import ernestoyaquello.com.verticalstepperform.Step;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.lj.myapplication.ContactActivity;
import com.example.lj.myapplication.R;

public class MakeContactStep extends Step<String> {

    private TextView textView;
    private Button open_contact;


    public MakeContactStep(String title) {
        this(title, " ");

    }

    public MakeContactStep(String title, String subtitle) {
        super(title, subtitle);
    }

    protected MakeContactStep(String title, String subtitle, String nextButtonText) {
        super(title, subtitle, nextButtonText);
    }

    @Override
    public String getStepData() {//데이터 저장
        SharedPreferences sharedPreferences = getContext().getSharedPreferences("sFile",Context.MODE_PRIVATE);
        String result = sharedPreferences.getString("ContactInfo","");

        return result;
    }

    @Override//넘길때 데이터 표시
    public String getStepDataAsHumanReadableString() {

        SharedPreferences sharedPreferences = getContext().getSharedPreferences("sFile", Context.MODE_PRIVATE);
        String result = sharedPreferences.getString("ContactInfo","");

        return result;

    }

    @Override//해당 섹터에 저장 표시
    public void restoreStepData(String data) {

        textView.setText(getStepDataAsHumanReadableString());

    }

    @Override
    public IsDataValid isStepDataValid(String stepData) {
        SharedPreferences sharedPreferences = getContext().getSharedPreferences("sFile",Context.MODE_PRIVATE);
        int code = sharedPreferences.getInt("Code",0);
        if(code ==777){
            markAsCompleted(true);
            return new IsDataValid(true);

        }
        else{

            return new IsDataValid(false);
        }

    }

    @Override
    protected View createStepContentLayout() {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View view = inflater.inflate(R.layout.activity_make_contact, null, false);
        textView=(TextView)view.findViewById(R.id.contact);
        open_contact=(Button)view.findViewById(R.id.open_contact);
        open_contact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), ContactActivity.class);
                getContext().startActivity(intent);
            }
        });


        return view;
    }

    @Override
    protected void onStepOpened(boolean animated) {

    }

    @Override
    protected void onStepClosed(boolean animated) {

    }

    @Override
    protected void onStepMarkedAsCompleted(boolean animated) {

    }

    @Override
    protected void onStepMarkedAsUncompleted(boolean animated) {

    }


}
