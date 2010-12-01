package net.jonrichards.batteryapp.ui;

import java.math.BigDecimal;

import net.jonrichards.batteryapp.system.DS2784Battery;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

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
	private TextView my_empty_volt;
	private TextView my_status_reg;
	private TextView my_capacity;	

	private int SAMPLE_POLL = 40;


	/**
	 * The polling frequency in milliseconds.
	 */
	private int my_sample_poll = 60000;
	
	//Public Methods
	
	/**
	 * Called when the activity is first created, initializations happen here.
	 * @param savedInstanceState 
	 */

	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.generallayout);
        
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
        my_empty_volt = (TextView)findViewById(R.id.txtAEvolt);       
        my_status_reg = (TextView)findViewById(R.id.txtStatusReg);
        my_capacity = (TextView)findViewById(R.id.txtCapacity);
        
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
	        case R.id.tech_help:     
	        	String text = this.getResources().getText(R.string.about_test).toString();
				AlertDialog.Builder builder = new AlertDialog.Builder(this);
				builder.setTitle(R.string.about);
				builder.setPositiveButton(R.string.ok, null);
		        builder.setMessage(text).create().show();
	        	break;
	        case R.id.settings: 
	        	Toast.makeText(this, "Any possible settings?", Toast.LENGTH_LONG).show();
	            break;
	        case R.id.exit: 
	        	Toast.makeText(this, "Exit to stop the app.", Toast.LENGTH_LONG).show();
	        	finish();
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
	 * Pauses this activity.
	 */
	@Override
    public void onPause() {
		my_handler.removeCallbacks(mUpdateUITimerTask);
        super.onPause();
    }
	
	/**
	 * Resumes this activity.
	 */
	@Override
    public void onResume() {
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
	 * Sets the UI text.
	 */
	public void setUIText() {
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
		
		//Populate percent
		String percent_text = battery_info.getDumpRegister(6);
		my_percent.setText(Integer.toString(Integer.parseInt(percent_text,16)));
		
		//Populate min charge current
		String charge_current_text = battery_info.getDumpRegister(53);
		int charge_current_converted = (Integer.parseInt(charge_current_text,16))*50/15;
		my_charge_current.setText(Integer.toString(charge_current_converted));
		
		//Populate AEvolt (for some reason parseDouble would not accept hex conversion)
		String empty_volt_text = battery_info.getDumpRegister(54);
		int empty_converted = (Integer.parseInt(empty_volt_text,16))*1952/100;
		double empty = empty_converted;
		my_empty_volt.setText(Double.toString(empty/1000));
		
		//Populate mAh capacity
		String capacity_text = battery_info.getMAh();
		int mAh = (Integer.parseInt(capacity_text))/1000;
		my_capacity.setText(Integer.toString(mAh));
		
		//Populate status register
		String status_text = battery_info.getDumpRegister(01);
		my_status_reg.setText("0x" + status_text);
	}
	
	//Private Methods

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
