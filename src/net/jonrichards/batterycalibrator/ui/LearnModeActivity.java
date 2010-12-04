package net.jonrichards.batterycalibrator.ui;

import net.jonrichards.batterycalibrator.system.DS2784Battery;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
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
import android.widget.Toast;
import android.widget.ToggleButton;

/**
 * A class for displaying informtation to assist the user to reach learn mode for their battery.
 * @author Jon Richards
 * @author Roger Podacter
 */
public class LearnModeActivity extends Activity {
	
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
	
	//Used to turn learn mode on or off
	private boolean my_learn_mode = false;
	
	//Sample rate used for learn mode on or off
	private int my_sample_poll = 30000;
	
	//Handler for updating the UI
	private final Handler my_handler = new Handler();
	
	//Declare the text views for this tab
	private TextView my_status_reg;
	private TextView my_live_voltage;
	private TextView my_current;
	private TextView my_capacity;
	private TextView my_message_1;
	private TextView my_message_2;
	
	private int my_empty_converted;
	
	/**
	 * A boolean for whether the alert has been displayed already.
	 */
	private boolean my_alert_displayed = false;
	
	private PowerManager power_manager;
	private WakeLock wake_lock;
	private LocationManager my_location_manager;
	private LocationListener my_location_listener;
	
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
        
        my_location_manager = (LocationManager)this.getSystemService(Context.LOCATION_SERVICE);
		my_location_listener = new LocationListener() {
		    public void onLocationChanged(Location location) {}
		    public void onStatusChanged(String provider, int status, Bundle extras) {}
		    public void onProviderEnabled(String provider) {}
		    public void onProviderDisabled(String provider) {}
		  };
        
        power_manager = (PowerManager)getBaseContext().getSystemService(Context.POWER_SERVICE);
        wake_lock = power_manager.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK | PowerManager.ON_AFTER_RELEASE, "LearnModeActivity");
 
        DS2784Battery battery_info = new DS2784Battery();
        String empty_volt_text = battery_info.getDumpRegister(54);
        my_empty_converted = (Integer.parseInt(empty_volt_text,16))*1952/100;
        
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
	        	my_learn_mode = true;
	        	setUIText();
	            learnPrepHelp();
	        } else if(v.getId() == R.id.btnLearnOff){
	        	my_learn_mode = false;
	        	setUIText();
	            learnPrepHelp();
	        }
	    }
	};
	
	/**
	 * Called when this activity is paused.
	 */
	@Override
    public void onPause() {
		my_handler.removeCallbacks(mUpdateUITimerTask);
	    super.onPause();
    }
	
	/**
	 * Called when this activity is resumed.
	 */
	@Override
    public void onResume() {
		my_handler.postDelayed(mUpdateUITimerTask, 2000);
	    super.onResume();
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
	    switch(item.getItemId()) {
	        case R.id.about:     
	        	Intent myIntent = new Intent();
                myIntent.setClass(this, AboutActivity.class);
                startActivity(myIntent);
                break;
	        case R.id.tech_help:     
	        	String text = getResources().getText(R.string.status_register).toString();
				AlertDialog.Builder builder = new AlertDialog.Builder(this);
				builder.setTitle(R.string.status_title);
				builder.setPositiveButton(R.string.ok, null);
		        builder.setMessage(text).create().show();
	        	break;
	        case R.id.settings: 
	        	startActivity(new Intent(this, SettingsActivity.class));                
	            break;
	        case R.id.instructions: 
	        	Toast.makeText(this, "Add directions for the app", Toast.LENGTH_LONG).show();
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
		String statustext = my_battery_info.getDumpRegister(01);
		my_status_reg.setText("(0x" + statustext + ")");
		
		//Populate voltage
		String voltagetext = my_battery_info.getVoltage();
		int volt = (Integer.parseInt(voltagetext));
		my_live_voltage.setText(Integer.toString(volt));
		
		//Populate current
		String current_text = my_battery_info.getCurrent();
		double curr = (Double.parseDouble(current_text));
		my_current.setText(Double.toString(curr/1000));
		
		//Populate mAh capacity
		String capacity_text = my_battery_info.getMAh();
		int mAh = (Integer.parseInt(capacity_text))/1000;
		my_capacity.setText(Integer.toString(mAh));
		
		//Added volt check and learn mode check to update sample poll
		if(my_learn_mode == true && volt <= 3500000){
			my_sample_poll = 3000;
		}else if(my_learn_mode == true && volt > 3500000){
			my_sample_poll = 20000;
		}else if(my_learn_mode == false){
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
		if (my_learn_mode == true && !intToBoolean(my_battery_info.getStatusRegister(4))) {
			my_alert_displayed = false;
			String text = getResources().getText(R.string.waiting_message1).toString();
			String text1 = getResources().getText(R.string.waiting_message2).toString();
			my_message_1.setText(text + my_empty_converted + text1);
			
		//Message output when learn mode is inactive
		} else if (my_learn_mode == false) {
			my_alert_displayed = false;
			String text = getResources().getText(R.string.recalibrate_message).toString();
			my_message_1.setText(text);
			
		//Message output when learn mode active and learn flag is on
		} else if (my_learn_mode == true && intToBoolean(my_battery_info.getStatusRegister(4))) {
			
			//If the alert has not just been displayed
			if(!my_alert_displayed) {
				my_alert_displayed = true;
				
				//Play alert beep three times
				ToneGenerator tone_generator = new ToneGenerator(AudioManager.STREAM_NOTIFICATION, ToneGenerator.MAX_VOLUME);
				tone_generator.startTone(ToneGenerator.TONE_PROP_BEEP);
				tone_generator.startTone(ToneGenerator.TONE_PROP_BEEP);
				tone_generator.startTone(ToneGenerator.TONE_PROP_BEEP);
				
				//Set learn detect button back to off
				my_learn_off.setChecked(true);
				//Disable learn mode
				my_learn_mode = false;
				//Clear out any remaining learn prep helpers
				learnPrepHelp();
				
				//Display alert popup message with title and OK button
				String text1 = getResources().getText(R.string.learn_popup).toString();
				AlertDialog.Builder builder = new AlertDialog.Builder(this);
				builder.setTitle(R.string.learn_popup_title);
				builder.setPositiveButton(R.string.ok, null);
		        builder.setMessage(text1).create().show();
				
		        //Display message inside the activity
		        String text = getResources().getText(R.string.in_progress_message).toString();
				my_message_1.setText(text);				
			}			
		}
		
		//Message when full charge is on
		if (intToBoolean(my_battery_info.getStatusRegister(7))) {
			String text = getResources().getText(R.string.chgtf_message).toString();
			my_message_2.setText(text);			
		}
	}
	
	/**
	 * Our runnable to continuously update the UI.
	 */
	private final Runnable mUpdateUITimerTask = new Runnable() {
	    public void run() {
			learnPrepHelp();
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
		if(my_learn_mode) {
			if(SettingsActivity.getEnableScreenOn(getBaseContext())) {
				if(!wake_lock.isHeld()) {
					wake_lock.acquire();
				}
			}
			if(SettingsActivity.getEnableGPSPolling(getBaseContext()) && Double.parseDouble(my_battery_info.getVoltage()) > 3500) {
				// Register the listener with the Location Manager to receive location updates
				my_location_manager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, my_location_listener);
			} 
		} else {
			if(wake_lock.isHeld()) {
				wake_lock.release();
			}
			my_location_manager.removeUpdates(my_location_listener);
		}
	}
}
//End of class LearnModeActivity