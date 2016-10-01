package com.rivastecnologia.graduei.controller.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.rivastecnologia.graduei.R;

public class Utility {

    private static final String SHARED_PREFERENCES_PREFIX = "com.rivastecnologia.graduei";

    /**
     * Verifies if the app is launching for the first time, so the app can show the settings screen
     *
     * @param context app/activity context
     * @return whether is the first launch or not
     */
    public static boolean isFirstLaunch(Context context) {

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        final String first_launch_key = context.getString(R.string.pref_first_launch_key);
        final boolean aBoolean = sharedPreferences.getBoolean(first_launch_key, true);

        if (aBoolean) {
            sharedPreferences.edit().putBoolean(first_launch_key, false).apply();
        }

        return aBoolean;
    }
}