package net.jonrichards.batteryapp.ui;

import android.content.Context;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;

public class SettingsActivity extends PreferenceActivity
{

        @Override
        protected void onCreate(Bundle savedInstanceState)
        {
                super.onCreate(savedInstanceState);
                addPreferencesFromResource(R.xml.settings);
        }
        
        public static boolean getEnableGPSPolling(Context context)
        {
                return PreferenceManager.getDefaultSharedPreferences(context).getBoolean("gps_polling", false);
        }
        
        public static boolean getEnableScreenOn(Context context)
        {
                return PreferenceManager.getDefaultSharedPreferences(context).getBoolean("screen_brightness", true);
        }
        
        public static boolean getEnableACRAdjustment(Context context)
        {
                return PreferenceManager.getDefaultSharedPreferences(context).getBoolean("ACR_adjustment", true);
        }
}