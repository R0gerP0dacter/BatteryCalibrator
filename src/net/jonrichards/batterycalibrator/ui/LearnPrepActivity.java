/* This program is free software. It comes without any warranty, to
 * the extent permitted by applicable law. You can redistribute it
 * and/or modify it under the terms of the Do What The Fuck You Want
 * To Public License, Version 2, as published by Sam Hocevar. See
 * http://sam.zoy.org/wtfpl/COPYING for more details. */ 
package net.jonrichards.batterycalibrator.ui;

import net.jonrichards.batterycalibrator.system.DS2784Battery;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

/**
 * A class for prepping the battery to enter learn mode.
 * @author Jon Richards
 * @author Roger Podacter
 */
public class LearnPrepActivity extends Activity {

	//Instance Variables

	private TextView my_age;
	private EditText my_age_input;
	private Button my_age_save_button;
	private Button my_age_cancel_button;
	
	private TextView my_full_40;
	private Button my_full_40_save_button;
	private Button my_full_40_cancel_button;
	private EditText my_full_40_input;
	
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
        setContentView(R.layout.learnpreplayout);
        
        my_battery_info = new DS2784Battery();
        
        my_power_manager = (PowerManager)getBaseContext().getSystemService(Context.POWER_SERVICE);
        my_wake_lock = my_power_manager.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK | PowerManager.ON_AFTER_RELEASE, "LearnModeActivity");


        //text views and button for the UI for this tab
        my_age = (TextView)findViewById(R.id.txtAge);
        my_age_input = (EditText)findViewById(R.id.etAge);
        my_age_save_button = (Button)findViewById(R.id.btnAgeSave);
        my_age_cancel_button = (Button)findViewById(R.id.btnAgeCancel);
        
        my_full_40 = (TextView)findViewById(R.id.txtFull40);
        my_full_40_save_button = (Button)findViewById(R.id.btnFull40Save);
        my_full_40_cancel_button = (Button)findViewById(R.id.btnFull40Cancel);
        my_full_40_input = (EditText)findViewById(R.id.etFull40);
        
        setUIText();

        //Sets the new age value when pressed
        my_age_save_button.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				DS2784Battery battery = new DS2784Battery();
				try {
					int new_age = Integer.parseInt((my_age_input.getText().toString()));
					battery.setAge(new_age);
					my_age_input.setText("");
				} catch(Exception e) {
					my_age_input.setText("");
				}
        		setUIText();
			}
		});
        
        //Clears the age input field when pressed
        my_age_cancel_button.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				my_age_input.setText("");
        		setUIText();
			}
		});
        
        
        //Sets the new full 40 value when pressed
        my_full_40_save_button.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				try {
					int new_full_40 = Integer.parseInt((my_full_40_input.getText().toString()));
					my_battery_info.setFull40(new_full_40);
					my_full_40_input.setText("");
				} catch(Exception e) {
					my_full_40_input.setText("");
				}
        		setUIText();
			}
		});
        
        //Clears the full 40 input field when pressed
        my_full_40_cancel_button.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				my_full_40_input.setText("");
        		setUIText();
			}
		});
        
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

	//Private Methods
	
	/**
	 * Sets the UI text.
	 */
	private void setUIText() {
		DS2784Battery battery_info = new DS2784Battery();		

		//Populate age
		String age_text = battery_info.getDumpRegister(20);
		try {
			int age_converted = (Integer.parseInt(age_text,16))*100/128;
			age_text = Integer.toString(age_converted);
		} catch(Exception e) {
			
		}
		my_age.setText(age_text + "%");

		//Populate full40
		String full_40_text = battery_info.getFull40();
		my_full_40.setText(full_40_text + " mAh");
	}
}
//End of class LearnPrepActivity