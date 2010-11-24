package net.jonrichards.batteryapp.ui;

import net.jonrichards.batteryapp.system.DS2784Battery;
import android.app.Activity;
import android.app.AlertDialog;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
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
	private int SAMPLE_POLL = 30;
	//Handler for updating the UI
	private final Handler mHandler = new Handler();
	//Declare the text views for this tab
	private TextView statusreg;
	private TextView livevoltage;
	private TextView message1;
	private TextView message2;

	/** 
	 * Used to initially populate empty volt.  Is there better way? i.e. pull
	 * existing param from the other activity?
	 */
	DS2784Battery battery_info = new DS2784Battery();
    String emptyvolttext = battery_info.getDumpRegister(54);
	int emptyconverted = (Integer.parseInt(emptyvolttext,16))*1952/100;

	
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.learnmodelayout);
        
        statusreg = (TextView)findViewById(R.id.txtStatusNumber);
        livevoltage = (TextView)findViewById(R.id.txtRealTimeVoltage);
        message1 = (TextView)findViewById(R.id.txtMessage1);
        message2 = (TextView)findViewById(R.id.txtMessage2);

        
        learngroup = (RadioGroup) findViewById(R.id.learnmodegroup);
        learnon=(RadioButton)findViewById(R.id.btnLearnOn);
		learnoff=(RadioButton)findViewById(R.id.btnLearnOff);
		learnoff.setChecked(true);
		String text = this.getResources().getText(R.string.recalibrate_message).toString();
		message1.setText(text);

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
	        	getUIText();				
	        }
	        else if (v.getId() == R.id.btnLearnOff){
	        	learnmode = false;
	        	getUIText();	    		
	        }
	    }
	};
	
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
            //getUIText();
            super.onResume();
    }
	
	//Our runnable to update the UI, polled every SAMPLE_POLL*500 seconds
	private final Runnable mUpdateUITimerTask = new Runnable() {
	    public void run() {
	    	getUIText();	    	
	        mHandler.postDelayed(mUpdateUITimerTask, SAMPLE_POLL * 500);
	    }
	};
	
	//Function for retrieving the UI text
	public void getUIText() {
		DS2784Battery battery_info = new DS2784Battery();
		
		//Populate status register
		String statustext = battery_info.getDumpRegister(01);
		statusreg.setText("(" + "0x" + statustext + ")");
		
		//Populate voltage
		String voltagetext = battery_info.getVoltage();
		int volt = (Integer.parseInt(voltagetext));
		livevoltage.setText(Integer.toString(volt));
		
		//Added volt check and learn mode check to update sample poll
		if(learnmode == true && volt <= 3500000){
			SAMPLE_POLL = 3;
		}else if(learnmode == true && volt > 3500000){
			SAMPLE_POLL = 20;
		}else if(learnmode == false){
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
		//Message output when learn mode is active and learn flag is off
		if (learnmode == true && LEARNF <= 0) {
			String text = this.getResources().getText(R.string.waiting_message1).toString();
			String text1 = this.getResources().getText(R.string.waiting_message2).toString();

			message1.setText(text + emptyconverted + text1);			
		}
		//Message output when learn mode is inactive
		else if (learnmode == false) {
			String text = this.getResources().getText(R.string.recalibrate_message).toString();
			message1.setText(text);
		}
		//Message output when learn mode active and learn flag is on
		else if (learnmode == true && LEARNF > 0) {
			String text1 = this.getResources().getText(R.string.learn_popup).toString();
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
	        builder.setMessage(text1).create().show();
			String text = this.getResources().getText(R.string.in_progress_message).toString();
			message1.setText(text);
			
			//need to add controls to only display popup dialog one time initially,
			//then remove it on subsequent iterations.
			//String text1 = this.getResources().getText(R.string.learn_popup).toString();
			//AlertDialog.Builder builder = new AlertDialog.Builder(this);
	        //builder.setMessage(text1).create().show();
		}
		//Message when full charge is on
		if (CHGTF > 0) {
			String text = this.getResources().getText(R.string.chgtf_message).toString();
			message2.setText(text);
			
		}
	}
	//Add options menu
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
	    MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.menu, menu);
	    return true;
	}
	//Add options menu
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    switch (item.getItemId()) {
	        case R.id.about:     
	        	Toast.makeText(this, "Write an ABOUT section here?", Toast.LENGTH_LONG).show();
	            break;
	        case R.id.tech_help:     
	        	Toast.makeText(this, "Add advnced technical info/help section here?", Toast.LENGTH_LONG).show();
	        	/*setContentView(R.layout.techinfo);
	        	TextView tv = (TextView) findViewById(R.id.techtext);
	        	String text = this.getResources().getText(R.string.tech_text).toString();
                tv.setText(text);*/
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
	
	
		
}
