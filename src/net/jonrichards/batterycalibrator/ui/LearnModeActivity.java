/* This program is free software. It comes without any warranty, to
 * the extent permitted by applicable law. You can redistribute it
 * and/or modify it under the terms of the Do What The Fuck You Want
 * To Public License, Version 2, as published by Sam Hocevar. See
 * http://sam.zoy.org/wtfpl/COPYING for more details. */ 
package net.jonrichards.batterycalibrator.ui;

import net.jonrichards.batterycalibrator.system.DS2784Battery;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.ToggleButton;

/**
 * A class for displaying informtation to assist the user to reach learn mode for their battery.
 * @author Jon Richards
 * @author Roger Podacter
 */
public class LearnModeActivity extends Activity {
			
	//Static Variables
	
	public static boolean LEARN_MODE = false;
	
	//Instance Variables
	
	//Declare the status reg flag toggle buttons
	private ToggleButton my_CHGTF_light;
	private ToggleButton my_AEF_light;
	private ToggleButton my_SEF_light;
	private ToggleButton my_LEARNF_light;
	private ToggleButton my_UVF_light;
	private ToggleButton my_PORF_light;
	
	//Declare the learn detect on/off radio buttons
	private RadioGroup my_learn_group;
	private RadioButton my_learn_on;
	private RadioButton my_learn_off;
	
	//Sample rate used for learn mode on or off
	private int my_sample_poll = 30000;
	
	//Handler for updating the UI
	private final Handler my_handler = new Handler();
	
	//Context for ACR write settings
	private Context my_context;
	
	//Declare the text views for this tab
	private TextView my_status_reg;
	private TextView my_live_voltage;
	private TextView my_current;
	private TextView my_capacity;
	private TextView my_message_1;
	private TextView my_message_2;
	private TextView my_learn_failed_message;

	private int my_empty_converted;
	
	/**
	 * A boolean for whether the alert has been displayed already.
	 */
	private boolean my_alert_displayed = false;
	
	private boolean my_learn_mode_tracker = false;

	private PowerManager my_power_manager;
	private WakeLock my_wake_lock;
	
	private DS2784Battery my_battery_info;

	//Public Methods
	
	/**
	 * Called when the activity is first created, initializations happen here.
	 * @param savedInstanceState 
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		
        super.onCreate(savedInstanceState);
        setContentView(R.layout.learnmodelayout);
        
        my_battery_info = new DS2784Battery();
        
        my_power_manager = (PowerManager)getBaseContext().getSystemService(Context.POWER_SERVICE);
        my_wake_lock = my_power_manager.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK | PowerManager.ON_AFTER_RELEASE, "LearnModeActivity");
 
        DS2784Battery battery_info = new DS2784Battery();
        String empty_volt_text = battery_info.getDumpRegister(54);
        try {
        	my_empty_converted = (Integer.parseInt(empty_volt_text,16))*1952/100;
        } catch(Exception e) {
        	my_empty_converted = 0;
        }
        
        my_status_reg = (TextView)findViewById(R.id.txtStatusNumber);
        my_live_voltage = (TextView)findViewById(R.id.txtRealTimeVoltage);
        my_current = (TextView)findViewById(R.id.txtRealTimeCurrent);
        my_capacity = (TextView)findViewById(R.id.txtRealTimeCapacity);
        my_message_1 = (TextView)findViewById(R.id.txtMessage1);
        my_message_2 = (TextView)findViewById(R.id.txtMessage2);

        my_learn_group = (RadioGroup)findViewById(R.id.learnmodegroup);
        my_learn_on = (RadioButton)findViewById(R.id.btnLearnOn);
        my_learn_off = (RadioButton)findViewById(R.id.btnLearnOff);
        my_learn_off.setChecked(true);
        my_learn_on.setOnClickListener(radio_listener);
        my_learn_off.setOnClickListener(radio_listener);
		
		String text = getResources().getText(R.string.recalibrate_message).toString();
		my_message_1.setText(text);

		my_CHGTF_light = (ToggleButton)findViewById(R.id.CHGTFlight);
		my_AEF_light = (ToggleButton)findViewById(R.id.AEFlight);
		my_SEF_light = (ToggleButton)findViewById(R.id.SEFlight);
		my_LEARNF_light = (ToggleButton)findViewById(R.id.LEARNFlight);
		my_UVF_light = (ToggleButton)findViewById(R.id.UVFlight);
		my_PORF_light = (ToggleButton)findViewById(R.id.PORFlight);
        
		my_handler.postDelayed(mUpdateUITimerTask, my_sample_poll);
		setUIText();
    }
	
	/**
	 * Called when the view is clicked.
	 */
	public OnClickListener radio_listener = new OnClickListener() {
	    public void onClick(View v) {
	        //Perform action on clicks
	        if(v.getId() == R.id.btnLearnOn){
	        	LEARN_MODE = true;
	        	setUIText();
	            learnPrepHelp();
	        } else if(v.getId() == R.id.btnLearnOff){
	        	LEARN_MODE = false;
	        	setUIText();
	            learnPrepHelp();
	        }
	    }
	};
	
	/**
	 * Called when this activity is resumed.
	 */
	@Override
    public void onResume() {
		if(LEARN_MODE && SettingsActivity.getEnableScreenOn(getBaseContext())) {
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
	
	/**
	 * Creates an options menu.
	 * @param menu The options menu to place the menu items in.
	 * @return Returns boolean true.
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
	    MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.menu, menu);
	    return true;
	}

	/**
	 * Called when an item in the options menu is selected.
	 * @param item The MenuItem selected.
	 * @return Returns a boolean true.
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    switch (item.getItemId()) {
	        case R.id.about:
	        	Intent myIntent = new Intent();
                myIntent.setClass(this, AboutActivity.class);
                startActivity(myIntent);
	            break;
	        case R.id.settings:
	        	startActivity(new Intent(this, SettingsActivity.class));                
	            break;
	        case R.id.log:
	        	startActivity(new Intent(this, LogActivity.class));
	        	break;
	        case R.id.exit:
	        	finish();
	        	break;
	        default:
	        	break;
	    }
	    return true;
	}
	
	//Private Methods
	
	/**
	 * Sets the UI text.
	 */
	private void setUIText() {
		
		//Populate status register
		String status_text = my_battery_info.getDumpRegister(01);
		my_status_reg.setText("(0x" + status_text + ")");
		
		//Populate voltage
		String voltage_text = my_battery_info.getVoltage();
		int volt;
		try {
			volt = (Integer.parseInt(voltage_text));
			voltage_text = Integer.toString(volt);
		} catch(Exception e) {
			//Set to trigger highest sample poll
			volt = 3500001;
		}
		my_live_voltage.setText(voltage_text);
		
		//Populate current
		String current_text = my_battery_info.getCurrent();
		try {
			double curr = (Double.parseDouble(current_text));
			current_text = Double.toString(curr/1000);
		} catch(Exception e) {
			
		}
		my_current.setText(current_text);
		
		//Populate mAh capacity
		String capacity_text = my_battery_info.getMAh();
		try {
			int mAh = (Integer.parseInt(capacity_text))/1000;
			capacity_text = Integer.toString(mAh);
		} catch(Exception e) {
			
		}
		my_capacity.setText(capacity_text);
		
		//Added volt check and learn mode check to update sample poll
		if(LEARN_MODE == true && volt <= 3500000){
			my_sample_poll = 3000;
		}else if(LEARN_MODE == true && volt > 3500000){
			my_sample_poll = 20000;
		}else if(LEARN_MODE == false){
			my_sample_poll = 30000;
		}
		
		//Populate status register flags
		my_CHGTF_light.setChecked(intToBoolean(my_battery_info.getStatusRegister(7)));
		my_AEF_light.setChecked(intToBoolean(my_battery_info.getStatusRegister(6)));
		my_SEF_light.setChecked(intToBoolean(my_battery_info.getStatusRegister(5)));
		my_LEARNF_light.setChecked(intToBoolean(my_battery_info.getStatusRegister(4)));
		my_UVF_light.setChecked(intToBoolean(my_battery_info.getStatusRegister(2)));
		my_PORF_light.setChecked(intToBoolean(my_battery_info.getStatusRegister(1)));
		
		//Message output when learn mode is active and learn flag is off
		if (LEARN_MODE && !intToBoolean(my_battery_info.getStatusRegister(4))) {
			my_alert_displayed = false;
			String text = getResources().getText(R.string.waiting_message1).toString();
			String text1 = getResources().getText(R.string.waiting_message2).toString();
			my_message_1.setText(text + my_empty_converted + text1);
			
		//Message output when learn mode is inactive
		} else if (!LEARN_MODE) {
			my_alert_displayed = false;
			String text = getResources().getText(R.string.recalibrate_message).toString();
			my_message_1.setText(text);
			
		//Message output when learn mode active and learn flag is on
		} else if (LEARN_MODE && intToBoolean(my_battery_info.getStatusRegister(4))) {
			
			//If the alert has not just been displayed
			if(!my_alert_displayed) {
				my_alert_displayed = true;
				
				//Play alert beep three times
				ToneGenerator tone_generator = new ToneGenerator(AudioManager.STREAM_NOTIFICATION, ToneGenerator.MAX_VOLUME);
				tone_generator.startTone(ToneGenerator.TONE_PROP_BEEP);
				tone_generator.startTone(ToneGenerator.TONE_PROP_BEEP);
				tone_generator.startTone(ToneGenerator.TONE_PROP_BEEP);
				
				//Display alert popup message with title and OK button
				String text1 = getResources().getText(R.string.learn_popup).toString();
				AlertDialog.Builder builder = new AlertDialog.Builder(this);
				builder.setTitle(R.string.learn_popup_title);
				builder.setPositiveButton(R.string.ok, null);
		        builder.setMessage(text1).create().show();
				
		        //Display message inside the activity
		        String text = getResources().getText(R.string.in_progress_message).toString();
				my_message_1.setText(text);
				
				//Set learn detect button back to off
				my_learn_off.setChecked(true);
				//Disable learn mode
				LEARN_MODE = false;
				//Clear out any remaining learn prep helpers
				learnPrepHelp();
				//Set learn flag tracking boolean
				my_learn_mode_tracker = true;
			}
			//Learn flag tracking to display pop up when failure before 4.15 volt
			if(my_learn_mode_tracker) {				
				if(!intToBoolean(my_battery_info.getStatusRegister(4)) && volt <= 4150000) {
					String text2 = getResources().getText(R.string.learn_failed_popup).toString();
					AlertDialog.Builder builder = new AlertDialog.Builder(this);
					builder.setTitle(R.string.learn_failed_title);
					builder.setPositiveButton(R.string.ok, null);
			        builder.setMessage(text2).create().show();
			        
					my_learn_mode_tracker = false;

				}
			}
		}
		
		//Message when full charge is on
		if(intToBoolean(my_battery_info.getStatusRegister(7))) {
			String text = getResources().getText(R.string.chgtf_message).toString();
			my_message_2.setText(text);			
		}
	}
	
	/**
	 * Our runnable to continuously update the UI.
	 */
	private final Runnable mUpdateUITimerTask = new Runnable() {
	    public void run() {
	    	//If the user has selected to have the application handle ACR adjustments and learn mode detect is on
	    	if(SettingsActivity.getEnableACRAdjustment(getBaseContext()) && LEARN_MODE) {
	    		checkACR();
	    	}
	    	setUIText();	    	
	        my_handler.postDelayed(mUpdateUITimerTask, my_sample_poll);
	    }
	};
	
	/**
	 * Returns false if the int == 0, true otherwise
	 * @param the_int_to_evaluate The int value to evaluate as a boolean.
	 * @return False if the int == 0, true otherwise.
	 */
	private boolean intToBoolean(int the_int_to_evaluate) {
		return (the_int_to_evaluate != 0);
	}
	
	/**
	 * If learn mode is active, helps drain the battery faster.
	 */
	private void learnPrepHelp() {
		//If learn detect mode is active
		if(LEARN_MODE) {
			//Keep the screen from locking if the user preference is selected
			if(SettingsActivity.getEnableScreenOn(getBaseContext())) {
				if(!my_wake_lock.isHeld()) {
					my_wake_lock.acquire();
				}
			}
		} else {
			if(my_wake_lock.isHeld()) {
				my_wake_lock.release();
			}
		}
	}
	
	/**
	 * Checks the remaining capacity and remaining voltage, and bumps voltage up if needed.
	 */
	private void checkACR() {
		try {
			int capacity = Integer.parseInt(my_battery_info.getMAh()) / 1000;		
			double realtime_volt = (Integer.parseInt(my_battery_info.getVoltage())) / 1000000.00;
			double empty_volt = ((Integer.parseInt(my_battery_info.getDumpRegister(54),16)) * 1952) / 100000.00;
			
			//If remaining capacity is low, but remaining voltage is not low, and learn mode has not come on, bump capacity
			if(capacity < 70 && realtime_volt - empty_volt >= 0.3 && !intToBoolean(my_battery_info.getStatusRegister(4))) {
				my_battery_info.setACR();
			}
		} catch(Exception e) {
			
		}
		
	}
	
}
//End of class LearnModeActivity