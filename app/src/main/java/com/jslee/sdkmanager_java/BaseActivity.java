package com.jslee.sdkmanager_java;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.DisplayMetrics;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

@SuppressLint("Registered")
public class BaseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public float getConvertDpByRes(float dpSize) {
        float weight;

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int width = dm.widthPixels;
        double wi = (double) width / (double) dm.xdpi;
        double standardWi = (double)2.86817851;
        weight = (float)(wi/standardWi);
        return dpSize * weight;
    }

    @Override
    public void onBackPressed() {
//        super.onBackPressed();
    }
}