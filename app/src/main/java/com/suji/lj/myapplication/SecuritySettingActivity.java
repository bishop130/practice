package com.suji.lj.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class SecuritySettingActivity extends AppCompatActivity implements View.OnClickListener {


    TextView key_1, key_2, key_3, key_4, key_5, key_6, key_7, key_8, key_9, key_0, backspace;
    ImageView dot_1, dot_2, dot_3, dot_4, dot_5, dot_6;

    StringBuffer stringBuffer = new StringBuffer();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_security_setting);


        key_1 = findViewById(R.id.key_1);
        key_2 = findViewById(R.id.key_2);
        key_3 = findViewById(R.id.key_3);
        key_4 = findViewById(R.id.key_4);
        key_5 = findViewById(R.id.key_5);
        key_6 = findViewById(R.id.key_6);
        key_7 = findViewById(R.id.key_7);
        key_8 = findViewById(R.id.key_8);
        key_9 = findViewById(R.id.key_9);
        key_0 = findViewById(R.id.key_0);
        backspace = findViewById(R.id.backspace);

        dot_1 = findViewById(R.id.dot_1);
        dot_2 = findViewById(R.id.dot_2);
        dot_3 = findViewById(R.id.dot_3);
        dot_4 = findViewById(R.id.dot_4);
        dot_5 = findViewById(R.id.dot_5);
        dot_6 = findViewById(R.id.dot_6);


        key_1.setOnClickListener(this);
        key_2.setOnClickListener(this);
        key_3.setOnClickListener(this);
        key_4.setOnClickListener(this);
        key_5.setOnClickListener(this);
        key_6.setOnClickListener(this);
        key_7.setOnClickListener(this);
        key_8.setOnClickListener(this);
        key_9.setOnClickListener(this);
        key_0.setOnClickListener(this);
        backspace.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {

    }
}
