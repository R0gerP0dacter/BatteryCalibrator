package net.jonrichards.batteryapp.ui;

import net.jonrichards.batteryapp.system.DS2784Battery;
import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class GeneralActivity extends Activity {
	
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
        mHandler.postDelayed(mUpdateUITimerTask, 60 * 1000);
        getUIText();

    }
	//Our runnable to continuously update the UI, polled every 60 seconds
	private final Runnable mUpdateUITimerTask = new Runnable() {
	    public void run() {
	    	getUIText();
	        mHandler.postDelayed(mUpdateUITimerTask, 60 * 1000);
	    }
	};
	//Function for retrieving the UI text
	protected void getUIText() {
		DS2784Battery battery_info = new DS2784Battery();
		
		//Populate voltage
		String voltagetext = battery_info.getVoltage();
		voltage.setText(voltagetext);
		
		//Populate current
		String currenttext = battery_info.getCurrent();
		current.setText(currenttext);
		
		//Populate full40
		String full40text = battery_info.getFull40();
		full40.setText(full40text);
		
		//Populate temperature - not outputting correctly (my fault, conversion issue)
		//String temperaturetext = battery_info.getTemperature();
		//temperature.setText(temperaturetext);
		
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
		
		//Populate AEvolt
		String emptyvolttext = battery_info.getDumpRegister(54);
		int emptyconverted = (Integer.parseInt(emptyvolttext,16))*1952/100;
		emptyvolt.setText(Integer.toString(emptyconverted));
		
		//Populate mAh capacity
		String capacitytext = battery_info.getMAh();
		capacity.setText(capacitytext);
	}
}
