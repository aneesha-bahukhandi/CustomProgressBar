package com.android.bahukhandi.aneesha.parsing.customprogressbar;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;

import com.android.bahukhandi.aneesha.parsing.horizontal_progress_bar_animation.AnimatedHorizontalProgressBar;

import java.util.ArrayList;
import java.util.List;

public class SampleActivity extends AppCompatActivity {

    private AnimatedHorizontalProgressBar mProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sample);

        mProgressBar = (AnimatedHorizontalProgressBar) findViewById(R.id.progress_bar);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mProgressBar.setProgressWithAnimation(50);
        changeProgress(90);
    }

    private void changeProgress(final int progress){
        new Handler().postDelayed(new Runnable(){
            @Override
            public void run() {
                mProgressBar.setProgressWithAnimation(progress);
                changeMax(250);
            }
        }, 4000);
    }

    private void changeMax(final int max){
        new Handler().postDelayed(new Runnable(){
            @Override
            public void run() {
                mProgressBar.setMaximumWithAnimation(max);
                mProgressBar.setProgressBarRadius(20);
                changeProgress();
            }
        }, 4000);
    }

    private void changeProgress(){
        int[] colors = getResources().getIntArray(R.array.colorsListSecondary);
        int[] intervals = getResources().getIntArray(R.array.intervalSlotsSecondary);
        List<Integer> c = new ArrayList<>();
        List<Integer> in = new ArrayList<>();
        for (int color : colors){
            c.add(color);
        }
        for (int interval : intervals){
            in.add(interval);
        }
        mProgressBar.setColorScheme(c, in);
        mProgressBar.setBackgroundColor(ContextCompat.getColor(this, R.color.colorBackground));
        new Handler().postDelayed(new Runnable(){
            @Override
            public void run() {
                mProgressBar.setProgressWithAnimation(200);
            }
        }, 4000);
    }
}
