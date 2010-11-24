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
	
	private int SAMPLE_POLL = 60;


	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.generallayout);        
                
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
        getUIText();
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
	
	@Override
    public void onPause()
    {
            mHandler.removeCallbacks(mUpdateUITimerTask);
            super.onPause();
    }
	
	@Override
    public void onResume()
    {
            mHandler.postDelayed(mUpdateUITimerTask, SAMPLE_POLL * 1000);
            //Should update the UI onResume; for now we won't
            getUIText();
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
		double volt = (Double.parseDouble(voltagetext))/1000;
		//BigDecimal big_decimal = new BigDecimal(Double.parseDouble(voltagetext) / 1000);
		//big_decimal = big_decimal.setScale(2,BigDecimal.ROUND_UP);
		//double mV = big_decimal.doubleValue();
		voltage.setText(Double.toString(volt/1000));
		
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
