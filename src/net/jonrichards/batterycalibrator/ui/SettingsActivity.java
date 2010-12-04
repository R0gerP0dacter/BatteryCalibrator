package net.jonrichards.batterycalibrator.ui;

import net.jonrichards.batterycalibrator.ui.R;
import android.content.Context;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;

/**
 * A class to set settings for this application.
 * @author Jon Richards
 * @author Roger Podacter
 */
public class SettingsActivity extends PreferenceActivity {

	/**
	 * Called when the activity is first created, initializations happen here.
	 * @param savedInstanceState 
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    addPreferencesFromResource(R.xml.settings);
	}
	
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
	    return PreferenceManager.getDefaultSharedPreferences(context).getBoolean("screen_brightness", true);
	}
	
	/**
	 * Returns whether automatic ACR adjustment should be enabled or not during learn prep mode.
	 * @param context
	 * @return Whether automatic ACR adjustment should be enabled or not during learn prep mode.
	 */
	public static boolean getEnableACRAdjustment(Context context) {
		return PreferenceManager.getDefaultSharedPreferences(context).getBoolean("ACR_adjustment", true);
	}
}
//End of class SettingsActivity