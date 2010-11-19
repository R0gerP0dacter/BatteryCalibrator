package net.jonrichards.batteryapp.ui;

import net.jonrichards.batteryapp.system.DS2784Battery;
import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

public class LearnModeActivity extends Activity {
	
	//Declare the status reg flag toggle buttons
	private ToggleButton CHGTFlight;
	private ToggleButton AEFlight;
	private ToggleButton SEFlight;
	private ToggleButton LEARNFlight;
	private ToggleButton UVFlight;
	private ToggleButton PORFlight;
	//Declare the learn detect on/off radio buttons
	RadioGroup learngroup;
	private RadioButton learnon;
	private RadioButton learnoff;
	
	//Used to turn learn mode on or off
	private boolean learnmode = false;
	
	//Sample rate used for learn mode on or off
	private int SAMPLE_POLL = 3;

	private final Handler mHandler = new Handler();

	private TextView livevoltage;
	

	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.learnmodelayout);
        
        learngroup = (RadioGroup) findViewById(R.id.learnmodegroup);
        learnon=(RadioButton)findViewById(R.id.btnLearnOn);
		learnoff=(RadioButton)findViewById(R.id.btnLearnOff);
		learnoff.setChecked(true);
		
        livevoltage = (TextView)findViewById(R.id.txtRealTimeVoltage);
        
        CHGTFlight = (ToggleButton) findViewById(R.id.CHGTFlight);
        AEFlight = (ToggleButton) findViewById(R.id.AEFlight);
        SEFlight = (ToggleButton) findViewById(R.id.SEFlight);
        LEARNFlight = (ToggleButton) findViewById(R.id.LEARNFlight);
        UVFlight = (ToggleButton) findViewById(R.id.UVFlight);
        PORFlight = (ToggleButton) findViewById(R.id.PORFlight);
        
        mHandler.postDelayed(mUpdateUITimerTask, SAMPLE_POLL * 500);
        getUIText();
        
        final RadioButton learnon = (RadioButton) findViewById(R.id.btnLearnOn);
        final RadioButton learnoff = (RadioButton) findViewById(R.id.btnLearnOff);
        learnon.setOnClickListener(radio_listener);
        learnoff.setOnClickListener(radio_listener);
    }
	//Radio Button listener to set learn detect on or off
	OnClickListener radio_listener = new OnClickListener() {
	    public void onClick(View v) {
	        // Perform action on clicks
	        RadioButton rb = (RadioButton) v;
	        if(v.getId() == R.id.btnLearnOn){
	        	learnmode = true;
	        }
	        else if (v.getId() == R.id.btnLearnOff){
	        	learnmode = false;
	        }
	    }
	};

	
	//Our runnable to update the UI, polled every 1.5 seconds
	private final Runnable mUpdateUITimerTask = new Runnable() {
	    public void run() {
	    	getUIText();	    	
	        mHandler.postDelayed(mUpdateUITimerTask, SAMPLE_POLL * 500);
	    }
	};
	
	//Function for retrieving the UI text
	protected void getUIText() {
		DS2784Battery battery_info = new DS2784Battery();
		
		//Populate voltage
		String voltagetext = battery_info.getVoltage();
		int volt = Integer.parseInt(voltagetext);
		livevoltage.setText(voltagetext);
		
		//Added volt check and learn mode check to update sample poll
		if(learnmode = true && volt <= 3600000){
			SAMPLE_POLL = 3;
		}else if(learnmode = true && volt > 360000){
			SAMPLE_POLL = 10;
		}else if(learnmode = false){
			SAMPLE_POLL = 30;
		}
		
		//Populate status register flags
		int CHGTF = battery_info.getStatusRegister(7);
		if (CHGTF > 0) {
			CHGTFlight.setChecked(true);		
		}
		else {
			CHGTFlight.setChecked(false);
		}
		
		int AEF = battery_info.getStatusRegister(6);
		if (AEF > 0) {
			AEFlight.setChecked(true);		
		}
		else {
			AEFlight.setChecked(false);
		}
		int SEF = battery_info.getStatusRegister(5);
		if (SEF > 0) {
			SEFlight.setChecked(true);		
		}
		else {
			SEFlight.setChecked(false);
		}		
		int LEARNF = battery_info.getStatusRegister(4);
		if (LEARNF > 0) {
			LEARNFlight.setChecked(true);		
		}
		else {
			LEARNFlight.setChecked(false);
		}		
		int UVF = battery_info.getStatusRegister(2);
		if (UVF > 0) {
			UVFlight.setChecked(true);		
		}
		else {
			UVFlight.setChecked(false);
		}		
		int PORF = battery_info.getStatusRegister(1);
		if (PORF > 0) {
			PORFlight.setChecked(true);		
		}
		else {
			PORFlight.setChecked(false);
		}		
		
	}	
		
}
