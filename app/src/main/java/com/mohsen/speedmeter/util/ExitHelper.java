package com.mohsen.speedmeter.util;

import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.mohsen.speedmeter.R;

public class ExitHelper {
    private static final int SECOND_BACK_ALLOWED_DELAY = 4000;
    private static long lastBackPressedTime = 0;

    public static void exitOnSecondBackPress(AppCompatActivity activity) {
        if (System.currentTimeMillis() - lastBackPressedTime < SECOND_BACK_ALLOWED_DELAY) {
            activity.finish();

        } else {
            lastBackPressedTime = System.currentTimeMillis();
            Toast.makeText(activity, R.string.back_again_to_exist, Toast.LENGTH_SHORT).show();
        }
    }
}
