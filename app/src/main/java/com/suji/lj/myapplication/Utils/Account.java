package com.suji.lj.myapplication.Utils;

import android.content.Context;
import android.content.Intent;

import com.suji.lj.myapplication.SampleLoginActivity;

import static android.content.Context.MODE_PRIVATE;

public class Account {

    public Account() {
    }

    public static String getUserId(Context context) {

        String user_id = context.getSharedPreferences("Kakao", MODE_PRIVATE).getString("token", "");
        if (user_id != null) {
            return user_id;
        } else {
            //context.startActivity(new Intent(context, SampleLoginActivity.class));
            return "";

        }


    }

    public static String getUserName(Context context) {

        String user_id = context.getSharedPreferences("Kakao", MODE_PRIVATE).getString("user_name", "");
        if (user_id != null) {
            return user_id;
        } else {
            context.startActivity(new Intent(context, SampleLoginActivity.class));
            return "null";

        }


    }

    public static String getUserThumbnail(Context context) {

        String thumbnail = context.getSharedPreferences("Kakao", MODE_PRIVATE).getString("thumbnail", "");
        if (thumbnail != null) {
            return thumbnail;
        } else {
            context.startActivity(new Intent(context, SampleLoginActivity.class));
            return "null";

        }


    }

    public static String getFcmToken(Context context) {

        String thumbnail = context.getSharedPreferences("FCM", MODE_PRIVATE).getString("fcm_token", "");
        if (thumbnail != null) {
            return thumbnail;
        } else {
            context.startActivity(new Intent(context, SampleLoginActivity.class));
            return "null";

        }


    }

    public static String getUserEmail(Context context) {

        String user_id = context.getSharedPreferences("Kakao", MODE_PRIVATE).getString("email", "");
        if (user_id != null) {
            return user_id;
        } else {
            //context.startActivity(new Intent(context, SampleLoginActivity.class));
            return "";

        }


    }



}
