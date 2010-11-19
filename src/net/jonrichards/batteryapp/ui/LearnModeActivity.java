package net.jonrichards.batteryapp.ui;

import net.jonrichards.batteryapp.system.DS2784Battery;
import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.ToggleButton;

public class LearnModeActivity extends Activity {
	
	private ToggleButton CHGTFlight;
	private ToggleButton AEFlight;
	private ToggleButton SEFlight;
	private ToggleButton LEARNFlight;
	private ToggleButton UVFlight;
	private ToggleButton PORFlight;
	
	private RadioButton learnon;
	private RadioButton learnoff;
	
	private boolean learnmode = false;
	private int SAMPLE_POLL;

	private final Handler mHandler = new Handler();

	private TextView livevoltage;
	

	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.learnmodelayout);
        
        livevoltage = (TextView)findViewById(R.id.txtRealTimeVoltage);
        
        CHGTFlight = (ToggleButton) findViewById(R.id.CHGTFlight);
        AEFlight = (ToggleButton) findViewById(R.id.AEFlight);
        SEFlight = (ToggleButton) findViewById(R.id.SEFlight);
        LEARNFlight = (ToggleButton) findViewById(R.id.LEARNFlight);
        UVFlight = (ToggleButton) findViewById(R.id.UVFlight);
        PORFlight = (ToggleButton) findViewById(R.id.PORFlight);
        
        learnon=(RadioButton)findViewById(R.id.btnLearnOn);
		learnoff=(RadioButton)findViewById(R.id.btnLearnOff);        
        ;
        

        mHandler.postDelayed(mUpdateUITimerTask, 3 * 500);
        getUIText();        
    }
	
	

	
	//Our runnable to update the UI, polled every 10 seconds
	private final Runnable mUpdateUITimerTask = new Runnable() {
	    public void run() {
	    	getUIText();
	        mHandler.postDelayed(mUpdateUITimerTask, 3 * 500);
	    }
	};
	
	//Function for retrieving the UI text
	protected void getUIText() {
		DS2784Battery battery_info = new DS2784Battery();
		
		//Populate voltage
		String voltagetext = battery_info.getVoltage();
		livevoltage.setText(voltagetext);
		
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
