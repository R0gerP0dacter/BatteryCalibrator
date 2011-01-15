/* This program is free software. It comes without any warranty, to
 * the extent permitted by applicable law. You can redistribute it
 * and/or modify it under the terms of the Do What The Fuck You Want
 * To Public License, Version 2, as published by Sam Hocevar. See
 * http://sam.zoy.org/wtfpl/COPYING for more details. */ 
package net.jonrichards.batterycalibrator.ui;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;

import com.teslacoilsw.quicksshd.ShellCommand;

/**
 * A class to set settings for this application.
 * @author Jon Richards
 * @author Roger Podacter
 */
public class SettingsActivity extends PreferenceActivity {
    
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
	    
	    //Set the background color to dark gray since we can't do it in the XML file
	    getListView().setBackgroundColor(Color.TRANSPARENT);
        getListView().setCacheColorHint(Color.TRANSPARENT);
        getListView().setBackgroundColor(Color.rgb(25, 25, 25));
	    
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
	    return PreferenceManager.getDefaultSharedPreferences(context).getBoolean("wake_lock", false);
	}
	
	/**
	 * Returns whether automatic ACR adjustment should be enabled or not during learn prep mode.
	 * @param context
	 * @return Whether automatic ACR adjustment should be enabled or not during learn prep mode.
	 */
	public static boolean getEnableACRAdjustment(Context context) {
		return PreferenceManager.getDefaultSharedPreferences(context).getBoolean("ACR_adjustment", false);
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
	
	/**
	 * Called when a preference item is selected.
	 */
	@Override
	public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {
		//If the item select is for ACR adjustment
		if(preference.getKey().equals("ACR_adjustment")) {
			//Request SU ability if ACR adjustment is being turned on
			if(PreferenceManager.getDefaultSharedPreferences(getBaseContext()).getBoolean("ACR_adjustment", true)) {
				ShellCommand shell_command = new ShellCommand();
				shell_command.canSU();
			}
		}

		return super.onPreferenceTreeClick(preferenceScreen, preference);
	}
	
}
//End of class SettingsActivity