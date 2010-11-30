package net.jonrichards.batteryapp.ui;

import net.jonrichards.batteryapp.system.DS2784Battery;
import android.app.Activity;
import android.app.AlertDialog;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

public class GeneralActivity extends Activity {
	//Handler for updating the UI
	private final Handler mHandler = new Handler();
	
	//Declaring all text views needed for general tab
	private TextView voltage;
	private TextView current;
	private TextView temperature;
	private TextView percent;
	private TextView age;
	private TextView full40;
	private TextView chargecurrent;
	private TextView emptyvolt;
	private TextView statusreg;
	private TextView capacity;
	
	private int SAMPLE_POLL = 40;

	//test
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
        voltage = (TextView)findViewById(R.id.txtVoltage);
        current = (TextView)findViewById(R.id.txtCurrent);
        temperature = (TextView)findViewById(R.id.txtTemperature);
        percent = (TextView)findViewById(R.id.txtPercent);
        age = (TextView)findViewById(R.id.txtAge);
        full40 = (TextView)findViewById(R.id.txtFull40);
        chargecurrent = (TextView)findViewById(R.id.txtMinChgCurrent);
        emptyvolt = (TextView)findViewById(R.id.txtAEvolt);       
        statusreg = (TextView)findViewById(R.id.txtStatusReg);
        capacity = (TextView)findViewById(R.id.txtCapacity);
        
        //Initial poll when activity is first created
        mHandler.postDelayed(mUpdateUITimerTask, SAMPLE_POLL * 1000);
        if (savedInstanceState == null) {
            getUIText();
        }

    }
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
	    MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.menu, menu);
	    return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    switch (item.getItemId()) {
	        case R.id.about:     
	        	Toast.makeText(this, "Write an ABOUT section here?", Toast.LENGTH_LONG).show();
	            break;
	        case R.id.tech_help:     
	        	Toast.makeText(this, "Add advnced technical info/help section here?", Toast.LENGTH_LONG).show();
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
	
	
	
	@Override
    public void onPause()
    {
            mHandler.removeCallbacks(mUpdateUITimerTask);
            super.onPause();
    }
	
	@Override
    public void onResume()
    {
            mHandler.postDelayed(mUpdateUITimerTask, 2 * 1000);
            
            /**Should update the UI onResume; for now we won't.  We see a delay
             *when calling the detUIText function onResume.  We need a way to
             *only poll the dynamic variables regularly, and only poll the static
             *variables when they change, or less often.
             */
            //getUIText();
            super.onResume();
    }
	
	//Our runnable to continuously update the UI, polled every 60 seconds
	private final Runnable mUpdateUITimerTask = new Runnable() {
	    public void run() {
	    	getUIText();
	        mHandler.postDelayed(mUpdateUITimerTask, 60 * 1000);
	    }
	};
	
	//Function for retrieving the UI text
	public void getUIText() {
		DS2784Battery battery_info = new DS2784Battery();
		
		//Populate voltage
		String voltagetext = battery_info.getVoltage();
		//voltagetext = voltagetext.substring(voltagetext.indexOf(".") + 1);
		//double volt = (Double.parseDouble(voltagetext))/1000;
		//BigDecimal big_decimal = new BigDecimal(Double.parseDouble(voltagetext) / 1000);
		//big_decimal = big_decimal.setScale(2,BigDecimal.ROUND_UP);
		//double mV = big_decimal.doubleValue();
		int volt = (Integer.parseInt(voltagetext))/1000;
		voltage.setText(Integer.toString(volt));
		
		//Populate current
		String currenttext = battery_info.getCurrent();
		double curr = (Double.parseDouble(currenttext));
		current.setText(Double.toString(curr/1000));		
		
		//Populate full40
		String full40text = battery_info.getFull40();
		full40.setText(full40text);
		
		//Populate temperature - not outputting correctly (my fault, conversion issue)
		String temperaturetext = battery_info.getTemperature();
		int temp = Integer.parseInt(temperaturetext);
		temperature.setText(Integer.toString(temp/10) + "." + Integer.toString(temp%10));
		
		//Populate age
		String agetext = battery_info.getDumpRegister(20);
		int ageconverted = (Integer.parseInt(agetext,16))*100/128;
		age.setText(Integer.toString(ageconverted));
		
		//Populate percent
		String percenttext = battery_info.getDumpRegister(6);
		percent.setText(Integer.toString(Integer.parseInt(percenttext,16)));
		
		//Populate min charge current
		String chargecurrenttext = battery_info.getDumpRegister(53);
		int chargecurrentconverted = (Integer.parseInt(chargecurrenttext,16))*50/15;
		chargecurrent.setText(Integer.toString(chargecurrentconverted));
		
		//Populate AEvolt (for some reason parseDouble would not accept hex conversion)
		String emptyvolttext = battery_info.getDumpRegister(54);
		int emptyconverted = (Integer.parseInt(emptyvolttext,16))*1952/100;
		double empty = emptyconverted;
		emptyvolt.setText(Double.toString(empty/1000));
		
		//Populate mAh capacity
		String capacitytext = battery_info.getMAh();
		int mAh = (Integer.parseInt(capacitytext))/1000;
		capacity.setText(Integer.toString(mAh));
		
		//Populate status register
		String statustext = battery_info.getDumpRegister(01);
		statusreg.setText("0x" + statustext);
	}
	
	
	
	
}
