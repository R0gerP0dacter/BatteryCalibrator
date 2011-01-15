/* This program is free software. It comes without any warranty, to
 * the extent permitted by applicable law. You can redistribute it
 * and/or modify it under the terms of the Do What The Fuck You Want
 * To Public License, Version 2, as published by Sam Hocevar. See
 * http://sam.zoy.org/wtfpl/COPYING for more details. */ 
package net.jonrichards.batterycalibrator.ui;

import java.io.FileInputStream;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.widget.TextView;

/**
 * A class for displaying information from the log.
 * @author Jon Richards
 * @author Roger Podacter
 */
public class LogActivity extends Activity {
	
	//Instance Variables
	
	//Handler for updating the UI
	private final Handler my_handler = new Handler();
	
	private TextView my_log_contents;
	private PowerManager my_power_manager;
	private WakeLock my_wake_lock;
	
	/**
	 * The polling frequency in milliseconds.
	 */
	private int my_sample_poll = 30000;

	/**
	 * Called when the activity is first created, initializations happen here.
	 * @param savedInstanceState 
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.loglayout);
        setTitle("Battery Calibrator Log");
        
        my_power_manager = (PowerManager)getBaseContext().getSystemService(Context.POWER_SERVICE);
        my_wake_lock = my_power_manager.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK | PowerManager.ON_AFTER_RELEASE, "LearnModeActivity");
        
        //Added raw dumpreg to registers tab
        my_log_contents = (TextView)findViewById(R.id.widget1);
        
        //Initial poll when activity is first created
        my_handler.postDelayed(mUpdateUITimerTask, my_sample_poll);
        if (savedInstanceState == null) {
            setUIText();
        }
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
		my_handler.postDelayed(mUpdateUITimerTask, 2000);
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
		my_handler.removeCallbacks(mUpdateUITimerTask);
        super.onPause();
    }
	
	//Private Methods
	
	/**
	 * Sets the UI text.
	 */
	private void setUIText() {
		my_log_contents.setText(readLog());
	}
	
	/**
	 * Our runnable to continuously update the UI.
	 */
	private final Runnable mUpdateUITimerTask = new Runnable() {
	    public void run() {
    		setUIText();
	        my_handler.postDelayed(mUpdateUITimerTask, my_sample_poll);
	    }
	};
	
	/**
	 * Returns the contents of the log file.
	 * @return The contents of the log file.
	 */
	private String readLog() {
		String log_contents = "";
		
		try {
			StringBuffer string_buffer = new StringBuffer();
			int character;
			FileInputStream file_input_stream = openFileInput(BatteryApp.LOG_FILE);
			while( (character = file_input_stream.read()) != -1) {
				string_buffer.append((char)character);
			}
			file_input_stream.close();
			
			log_contents = string_buffer.toString();
		} catch(Exception e) {
			
		}
		
	
		
		return log_contents;
	}
}
//End of class Registers