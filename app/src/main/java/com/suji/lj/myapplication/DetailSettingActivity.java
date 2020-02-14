package com.suji.lj.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;

public class DetailSettingActivity extends AppCompatActivity {

    SeekBar seekBar;
    SeekBar seekBar_absence;
    View thumbView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_setting);


        seekBar = findViewById(R.id.seek_bar);
        seekBar_absence = findViewById(R.id.seek_bar_absence);
        thumbView = LayoutInflater.from(this).inflate(R.layout.layout_seeker_thumb, null, false);


        seekBar.setMax(30);
        seekBar.setProgress(15);
        seekBar.setThumb(getThumb(15));
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                Log.d("시크바", seekBar.getProgress() + "onProgresschanged");
                seekBar.setThumb(getThumb(progress));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                Log.d("시크바", seekBar.getProgress() + "onStart");

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                Log.d("시크바", seekBar.getProgress() + "onStop");

            }
        });
        seekBar_absence.setMax(7);
        seekBar_absence.setProgress(3);
        seekBar_absence.setThumb(getThumb(3));
        seekBar_absence.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                int min = 3;
                if (progress < min) {
                    seekBar.setProgress(min);
                    seekBar.setThumb(getThumb(min));
                } else {
                    seekBar.setThumb(getThumb(progress));
                }

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {


            }
        });
    }
    public Drawable getThumb(int progress) {
        ((TextView) thumbView.findViewById(R.id.tvProgress)).setText(progress + "");

        thumbView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        Bitmap bitmap = Bitmap.createBitmap(thumbView.getMeasuredWidth(), thumbView.getMeasuredHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        thumbView.layout(0, 0, thumbView.getMeasuredWidth(), thumbView.getMeasuredHeight());
        thumbView.draw(canvas);

        return new BitmapDrawable(getResources(), bitmap);
    }
}
