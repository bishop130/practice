package com.suji.lj.myapplication.StepperForm;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TextView;

import com.suji.lj.myapplication.R;
import com.google.android.material.textfield.TextInputEditText;

import ernestoyaquello.com.verticalstepperform.Step;

public class MakeTitle extends Step<String> {

    private TextInputEditText textInputEditText;
    private String unformattedErrorString;
    private static final int MIN_CHARACTERS_NAME = 1;


    public MakeTitle(String title) {
        this(title, "");
    }

    public MakeTitle(String title, String subtitle) {
        super(title, subtitle);
    }

    protected MakeTitle(String title, String subtitle, String nextButtonText) {
        super(title, subtitle, nextButtonText);
    }

    @Override
    protected View createStepContentLayout() {

        textInputEditText = new TextInputEditText(getContext());
        textInputEditText.setHint(R.string.form_hint_title);
        textInputEditText.setSingleLine(true);
        textInputEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Log.d("타이틀 생명주기","onTextChange");
                markAsCompletedOrUncompleted(true);
            }

            @Override
            public void afterTextChanged(Editable s) {
                textInputEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                    @Override
                    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                        getFormView().goToNextStep(true);

                        return false;
                    }
                });


            }
        });

        unformattedErrorString = getContext().getResources().getString(R.string.error_alarm_name_min_characters);
        return textInputEditText;
    }

    @Override
    public String getStepData() {
        Log.d("타이틀 생명주기","getStepData");
        Editable text = textInputEditText.getText();
        if(text != null){
        return text.toString();
        }
        return "";
    }

    @Override
    public String getStepDataAsHumanReadableString() {
        Log.d("타이틀 생명주기","getStepDataAsHumanReadableString");
        String name = getStepData();
        return name == null || name.isEmpty()
                ? getContext().getString(R.string.form_empty_field)
                : name;
    }

    @Override
    public void restoreStepData(String data) {
        Log.d("타이틀 생명주기","restoreStepData");
        if (textInputEditText != null) {
            textInputEditText.setText(data);
        }

    }

    @Override
    protected IsDataValid isStepDataValid(String stepData) {
        Log.d("타이틀 생명주기","isStepDataValid");
        if (stepData.length() < MIN_CHARACTERS_NAME) {
            String titleError = String.format(unformattedErrorString, MIN_CHARACTERS_NAME);
            return new IsDataValid(false, titleError);
        } else {
            return new IsDataValid(true);
        }
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
