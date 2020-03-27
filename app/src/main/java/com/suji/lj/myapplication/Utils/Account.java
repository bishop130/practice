package com.suji.lj.myapplication.Utils;

import android.content.Context;
import android.content.Intent;

import com.suji.lj.myapplication.SampleLoginActivity;

import static android.content.Context.MODE_PRIVATE;

public class Account {

    public Account() {
    }

    public static String getUserId(Context context){

        String user_id = context.getSharedPreferences("Kakao", MODE_PRIVATE).getString("token", "");
        if(user_id!=null){
            return user_id;
        }else{
            context.startActivity(new Intent(context, SampleLoginActivity.class));
            return "null";

        }


    }



}
