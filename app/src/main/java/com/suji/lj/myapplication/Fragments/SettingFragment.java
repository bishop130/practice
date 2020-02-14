package com.suji.lj.myapplication.Fragments;

import android.Manifest;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.suji.lj.myapplication.AccountActivity;
import com.suji.lj.myapplication.Adapters.NewLocationService;
import com.suji.lj.myapplication.AutoLocationSetting;
import com.suji.lj.myapplication.FAQActivity;
import com.suji.lj.myapplication.R;
import com.suji.lj.myapplication.SampleSignupActivity;
import com.squareup.picasso.Picasso;
import com.suji.lj.myapplication.Utils.Utils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import static android.content.Context.MODE_PRIVATE;

public class SettingFragment extends Fragment implements View.OnClickListener {

    private TextView kakao_name_setting;
    private ImageView profile_image_setting;
    private Context mContext;
    private LinearLayout alarm_setting_go;
    private LinearLayout auto_register_location;
    private LinearLayout go_to_my_profile;
    private LinearLayout faq;
    private LinearLayout manage_account;
    private Switch auto_location_switch;
    private static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {


        View view = inflater.inflate(R.layout.fragment_setting, container, false);
        kakao_name_setting = view.findViewById(R.id.kakao_name_setting);
        profile_image_setting = view.findViewById(R.id.profile_image_setting);
        alarm_setting_go = view.findViewById(R.id.alarm_setting_go);
        auto_register_location = view.findViewById(R.id.auto_register_location);
        go_to_my_profile = view.findViewById(R.id.go_to_my_profile);
        faq = view.findViewById(R.id.faq);
        manage_account = view.findViewById(R.id.manage_account);
        auto_location_switch = view.findViewById(R.id.auto_location_switch);


        auto_register_location.setOnClickListener(this);
        go_to_my_profile.setOnClickListener(this);
        alarm_setting_go.setOnClickListener(this);
        profile_image_setting.setBackground(new ShapeDrawable(new OvalShape()));
        profile_image_setting.setClipToOutline(true);
        manage_account.setOnClickListener(this);
        requestProfile();
        autoLocationSetting();
        return view;
    }


    private void requestProfile() {

        SharedPreferences sharedPreferences = mContext.getSharedPreferences("kakao_profile", MODE_PRIVATE);
        String name = sharedPreferences.getString("name", "");
        String profile_image = sharedPreferences.getString("profile_image", null);
        if(!Utils.isEmpty(profile_image)) {

            kakao_name_setting.setText(name);
            Picasso.with(getActivity())
                    .load(profile_image)
                    .fit()
                    .into(profile_image_setting);
        }

    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.auto_register_location:
                startActivity(new Intent(getActivity(), AutoLocationSetting.class));
                break;
            case R.id.faq:
                startActivity(new Intent(getActivity(), FAQActivity.class));
                break;
            case R.id.go_to_my_profile:
                startActivity(new Intent(getActivity(), SampleSignupActivity.class));
                break;
            case R.id.manage_account:
                startActivity(new Intent(getActivity(), AccountActivity.class));


        }
    }

    private void autoLocationSetting() {
        if (!checkLocationPermission()) {
            auto_location_switch.setChecked(false);
            SharedPreferences.Editor editor = mContext.getSharedPreferences("settings", MODE_PRIVATE).edit();
            editor.putBoolean("auto_register_location", false);
            editor.apply();
        }

        SharedPreferences sharedPreferences = mContext.getSharedPreferences("settings", MODE_PRIVATE);
        auto_location_switch.setChecked(sharedPreferences.getBoolean("auto_register_location", false));
        if (Utils.isServiceRunningInForeground(mContext, NewLocationService.class)) {

            auto_location_switch.setChecked(true);
        } else {
            auto_location_switch.setChecked(false);
        }


        auto_location_switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    if (Utils.isLocationEnabled(mContext)) {
                        if (checkLocationPermission()) {
                            Intent service = new Intent(mContext, NewLocationService.class);
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                ContextCompat.startForegroundService(mContext, service);
                            } else {
                                mContext.startService(service);
                            }


                            SharedPreferences.Editor editor = mContext.getSharedPreferences("settings", MODE_PRIVATE).edit();
                            editor.putBoolean("auto_register_location", isChecked);
                            editor.apply();
                        } else {
                            requestPermission();
                        }
                    }else{
                        auto_location_switch.setChecked(false);
                        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);

                        builder.setTitle("위치설정꺼짐").setMessage("위치설정을 확인해주세요");

                        AlertDialog alertDialog = builder.create();

                        alertDialog.show();

                    }

                    // The toggle is enabled
                } else {
                    Intent serviceIntent = new Intent(mContext, NewLocationService.class);
                    mContext.stopService(serviceIntent);
                    cancelAlarm();
                    SharedPreferences.Editor editor = mContext.getSharedPreferences("settings", MODE_PRIVATE).edit();
                    editor.putBoolean("auto_register_location", isChecked);
                    editor.apply();

                    // The toggle is disabled
                }

            }
        });


    }

    private void cancelAlarm() {
        AlarmManager am = (AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(mContext, NewLocationService.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Log.d("서비스", "설정전" + (PendingIntent.getForegroundService(mContext, 123, intent, PendingIntent.FLAG_NO_CREATE) != null));
            PendingIntent pendingIntent = PendingIntent.getForegroundService(mContext, 123, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            am.cancel(pendingIntent);
            Log.d("서비스", "설정후" + (PendingIntent.getForegroundService(mContext, 123, intent, PendingIntent.FLAG_NO_CREATE) != null));
        }


    }

    private Boolean checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            Log.d("퍼미션", "이미 승인받음");
            return true;
        } else {
            // Should we show an explanation?
            return false;
        }
    }

    private void requestPermission() {


        if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)) {

            // Show an explanation to the user *asynchronously* -- don't block
            // this thread waiting for the user's response! After the user
            // sees the explanation, try again to request the permission.
            new AlertDialog.Builder(mContext)
                    .setTitle("위치권한이 필요합니다.")
                    .setMessage("This app needs the Location permission, please accept to use location functionality")
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            //Prompt the user once explanation has been shown
                            ActivityCompat.requestPermissions(getActivity(),
                                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                    MY_PERMISSIONS_REQUEST_LOCATION);
                        }
                    })
                    .create()
                    .show();


        } else {
            // No explanation needed, we can request the permission.
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSIONS_REQUEST_LOCATION);
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay! Do the
                    // location-related task you need to do.
                    if (ContextCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                        Intent service = new Intent(mContext, NewLocationService.class);
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            ContextCompat.startForegroundService(mContext, service);
                        } else {
                            mContext.startService(service);
                        }
                        Toast.makeText(mContext, "위치권한이 허가되었습니다.", Toast.LENGTH_LONG).show();
                    }

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Toast.makeText(mContext, "위치권한이 거부되었습니다.", Toast.LENGTH_LONG).show();
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }
}
