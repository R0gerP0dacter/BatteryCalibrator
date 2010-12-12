/* This program is free software. It comes without any warranty, to
 * the extent permitted by applicable law. You can redistribute it
 * and/or modify it under the terms of the Do What The Fuck You Want
 * To Public License, Version 2, as published by Sam Hocevar. See
 * http://sam.zoy.org/wtfpl/COPYING for more details. */ 
package net.jonrichards.batterycalibrator.ui;

import net.jonrichards.batterycalibrator.ui.R;
import android.content.Context;
import android.os.Bundle;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;

/**
 * A class to set settings for this application.
 * @author Jon Richards
 * @author Roger Podacter
 */
public class SettingsActivity extends PreferenceActivity {

	private static final String OPTION_ACR = "ACR_Adjustment_Values";
    private static final String OPTION_ACR_DEFAULT = "0";
    
	//Instance Variables
	
	private PowerManager my_power_manager;
	private WakeLock my_wake_lock;
	
	//Protected Methods
	
	/**
	 * Called when the activity is first created, initializations happen here.
	 * @param savedInstanceState 
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    addPreferencesFromResource(R.xml.settings);
	    
	    my_power_manager = (PowerManager)getBaseContext().getSystemService(Context.POWER_SERVICE);
        my_wake_lock = my_power_manager.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK | PowerManager.ON_AFTER_RELEASE, "LearnModeActivity");
	}
	
	//Public Methods
	
	/**
	 * Returns whether GPS polling should be enabled or not during learn prep mode.
	 * @param context
	 * @return Whether GPS polling should be enabled or not during learn prep mode.
	 */
	public static boolean getEnableGPSPolling(Context context) {
	    return PreferenceManager.getDefaultSharedPreferences(context).getBoolean("gps_polling", false);
	}
	
	/**
	 * Returns whether screen always on should be enabled or not during learn prep mode.
	 * @param context
	 * @return Whether screen always on should be enabled or not during learn prep mode.
	 */
	public static boolean getEnableScreenOn(Context context) {
	    return PreferenceManager.getDefaultSharedPreferences(context).getBoolean("wake_lock", true);
	}
	
	/**
	 * Returns whether automatic ACR adjustment GREATER THAN 0.2 should be enabled or not during learn prep mode.
	 * @param context
	 * @return Whether automatic ACR adjustment should be enabled or not during learn prep mode.
	 */
	public static boolean getEnableACRAdjustmentGreaterThan(Context context) {
		return PreferenceManager.getDefaultSharedPreferences(context).getBoolean("ACR_adjustment_greater", true);
	}
	
	/**
	 * Returns whether automatic ACR adjustment LESS THAN 0.2 should be enabled or not during learn prep mode.
	 * @param context
	 * @return Whether automatic ACR adjustment should be enabled or not during learn prep mode.
	 */
	public static boolean getEnableACRAdjustmentLessThan(Context context) {
		return PreferenceManager.getDefaultSharedPreferences(context).getBoolean("ACR_adjustment_less", true);
	}
	
	/**
	 * Returns whether automatic ACR adjustment is Off, < 0.2volts, > 0.2volts, or both, during learn prep mode.
	 * @param context
	 * @return Four possible options, 0: Off, 1: < 0.2, 2: > 0.2, 3: Both less and greater than with pop up for testing.
	 */
	public static String getACRVariable(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context).
            getString(OPTION_ACR, OPTION_ACR_DEFAULT);
	}
	
	/**
	 * Called when this activity is resumed.
	 */
	@Override
    public void onResume() {
		if(LearnModeActivity.LEARN_MODE && SettingsActivity.getEnableScreenOn(getBaseContext())) {
			if(!my_wake_lock.isHeld()) {
				my_wake_lock.acquire();
			}
		}
        super.onResume();
    }

	/**
	 * Called when this activity is paused.
	 */
	@Override
    public void onPause() {
		if(my_wake_lock.isHeld()) {
			my_wake_lock.release();
		}
        super.onPause();
    }
}
//End of class SettingsActivity