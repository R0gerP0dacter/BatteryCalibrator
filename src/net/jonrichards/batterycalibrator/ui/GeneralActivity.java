/* This program is free software. It comes without any warranty, to
 * the extent permitted by applicable law. You can redistribute it
 * and/or modify it under the terms of the Do What The Fuck You Want
 * To Public License, Version 2, as published by Sam Hocevar. See
 * http://sam.zoy.org/wtfpl/COPYING for more details. */ 
package net.jonrichards.batterycalibrator.ui;

import java.math.BigDecimal;

import net.jonrichards.batterycalibrator.system.DS2784Battery;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;

/**
 * A class for displaying general information about the current status of the battery.
 * @author Jon Richards
 * @author Roger Podacter
 */
public class GeneralActivity extends Activity {
	
	//Instance Variables
	
	//Handler for updating the UI
	private final Handler my_handler = new Handler();
	
	//Declaring all text views needed for general tab
	private TextView my_voltage;
	private TextView my_current;
	private TextView my_temperature;
	private TextView my_percent;
	private TextView my_age;
	private TextView my_full_40;
	private TextView my_charge_current;
	private TextView my_charge_volt;
	private TextView my_empty_current;
	private TextView my_empty_volt;
	private TextView my_status_reg;
	private TextView my_capacity;
	private TextView my_aged_capacity;
	
	private PowerManager my_power_manager;
	private WakeLock my_wake_lock;

	/**
	 * The polling frequency in milliseconds.
	 */
	private int my_sample_poll = 60000;
	
	//Public Methods
	
	/**
	 * Called when the activity is first created, initializations happen here.
	 * @param savedInstanceState 
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.generallayout);
        
        my_power_manager = (PowerManager)getBaseContext().getSystemService(Context.POWER_SERVICE);
        my_wake_lock = my_power_manager.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK | PowerManager.ON_AFTER_RELEASE, "LearnModeActivity");
        
        /**
         * Ignore this savedInstanceState code below, was just experimenting
         */
        /*if (savedInstanceState != null)
        {
          String strValue = savedInstanceState.getString("vol");
          if (strValue != null)
          {
            TextView oControl = (TextView)findViewById(R.id.txtVoltage);
            oControl.setText(strValue);
          }          
        }*/
        
        //Defining all text views and linking to UI elements
        my_voltage = (TextView)findViewById(R.id.txtVoltage);
        my_current = (TextView)findViewById(R.id.txtCurrent);
        my_temperature = (TextView)findViewById(R.id.txtTemperature);
        my_percent = (TextView)findViewById(R.id.txtPercent);
        my_age = (TextView)findViewById(R.id.txtAge);
        my_full_40 = (TextView)findViewById(R.id.txtFull40);
        my_charge_current = (TextView)findViewById(R.id.txtMinChgCurrent);
        my_charge_volt = (TextView)findViewById(R.id.txtMinChgVolt);       
        my_empty_volt = (TextView)findViewById(R.id.txtAEvolt);
        my_empty_current = (TextView)findViewById(R.id.txtAEcurrent);
        my_status_reg = (TextView)findViewById(R.id.txtStatusReg);
        my_capacity = (TextView)findViewById(R.id.txtCapacity);
        my_aged_capacity = (TextView)findViewById(R.id.txtAgedCapacity);

        
        //Initial poll when activity is first created
        my_handler.postDelayed(mUpdateUITimerTask, my_sample_poll);
        if (savedInstanceState == null) {
            setUIText();
        }

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
/*	        case R.id.tech_help:     
	        	String text = getResources().getText(R.string.status_register).toString();
				AlertDialog.Builder builder = new AlertDialog.Builder(this);
				builder.setTitle(R.string.status_title);
				builder.setPositiveButton(R.string.ok, null);
		        builder.setMessage(text).create().show();
	        	break;*/
	        case R.id.settings: 
	        	startActivity(new Intent(this, SettingsActivity.class));                
	            break;
/*	        case R.id.instructions: 
	        	Toast.makeText(this, "Add directions for the app", Toast.LENGTH_LONG).show();
	            break;*/
	        case R.id.exit: 
	        	//Toast.makeText(this, "Exit to stop the app.", Toast.LENGTH_LONG).show();
	        	finish();
	        	break;
	        default:
	        	break;
	    }
	    return true;
	}
	
	/**
	 * Ignore this Instance code, just experimenting.
	 *
	@Override
	public void onSaveInstanceState(Bundle savedInstanceState) 
	{
	  // Store UI state to the savedInstanceState.
	  // This bundle will be passed to onCreate on next call.

	  TextView txtName1 = (TextView)findViewById(R.id.txtVoltage);
	  String strName1 = txtName1.getText().toString();	  
	  
	  savedInstanceState.putString("vol", strName1);
	  
	  super.onSaveInstanceState(savedInstanceState);
	}*/
	
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
            
		/**Should update the UI onResume; for now we won't.  We see a delay
		 *when calling the detUIText function onResume.  We need a way to
		 *only poll the dynamic variables regularly, and only poll the static
		 *variables when they change, or less often.
		 */
        //setUIText();
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
		DS2784Battery battery_info = new DS2784Battery();
		
		//Populate voltage
		String voltage_text = battery_info.getVoltage();
		BigDecimal big_decimal = new BigDecimal(Double.parseDouble(voltage_text) / 1000);
		big_decimal = big_decimal.setScale(2,BigDecimal.ROUND_UP);
		voltage_text = big_decimal.toString();
		my_voltage.setText(voltage_text);
		
		//Populate current
		String current_text = battery_info.getCurrent();
		double curr = (Double.parseDouble(current_text));
		my_current.setText(Double.toString(curr/1000));
		
		//Populate full40
		String full_40_text = battery_info.getFull40();
		my_full_40.setText(full_40_text);
		
		//Populate temperature - not outputting correctly (my fault, conversion issue)
		String temperature_text = battery_info.getTemperature();
		int temp = Integer.parseInt(temperature_text);
		my_temperature.setText(Integer.toString(temp/10) + "." + Integer.toString(temp%10));
		
		//Populate age
		String age_text = battery_info.getDumpRegister(20);
		int age_converted = (Integer.parseInt(age_text,16))*100/128;
		my_age.setText(Integer.toString(age_converted));
		//test
		//Populate percent
		String percent_text = battery_info.getDumpRegister(6);
		my_percent.setText(Integer.toString(Integer.parseInt(percent_text,16)));
		
		//Populate min charge current
		String charge_current_text = battery_info.getDumpRegister(53);
		int charge_current_converted = (Integer.parseInt(charge_current_text,16))*50/15;
		my_charge_current.setText(Integer.toString(charge_current_converted));
		
		//Populate min charge volt
		String charge_volt_text = battery_info.getDumpRegister(52);
		int charge_volt_converted = (Integer.parseInt(charge_volt_text,16))*1952/100;
		my_charge_volt.setText(Integer.toString(charge_volt_converted));
		
		//Populate AEvolt (for some reason parseDouble would not accept hex conversion)
		String empty_volt_text = battery_info.getDumpRegister(54);
		int empty_converted = (Integer.parseInt(empty_volt_text,16))*1952/100;
		double empty = empty_converted;
		my_empty_volt.setText(Double.toString(empty/1000));
		
		//Populate AEcurrent
		String empty_current_text = battery_info.getDumpRegister(55);
		int empty_current_converted = (Integer.parseInt(empty_current_text,16))*200/15;
		double empty_current = empty_current_converted;
		my_empty_current.setText(Double.toString(empty_current));
		
		//Populate mAh capacity
		String capacity_text = battery_info.getMAh();
		int mAh = (Integer.parseInt(capacity_text))/1000;
		my_capacity.setText(Integer.toString(mAh));
		
		//Populate aged mAh capacity 
		String aged_capacity_text_MSB = battery_info.getDumpRegister(50);
		String aged_capacity_text_LSB = battery_info.getDumpRegister(51);
		int MSB = (Integer.parseInt(aged_capacity_text_MSB, 16));
		int LSB = (Integer.parseInt(aged_capacity_text_LSB, 16));
	    int aged = ((MSB<<8) | LSB)*625/1500;

		my_aged_capacity.setText(Integer.toString(aged));
		
		//Populate status register
		String status_text = battery_info.getDumpRegister(01);
		my_status_reg.setText("0x" + status_text);
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
}
//End of class GeneralActivity
