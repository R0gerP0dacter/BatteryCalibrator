package net.jonrichards.batterycalibrator.system;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;

import net.jonrichards.batterycalibrator.ui.BatteryApp;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;

/**
 * A logging service.
 * @author Jon Richards
 * @author Roger Podacter
 */
public class LoggingService extends Service {
	
	//Static Variables
	
	private static final int INTERVAL = 30000;
	private static final int LOG_LENGTH = 100;
	
	//Instance Variables
	
	private Timer my_timer;
	private DS2784Battery my_battery_info;
	
	/**
	 * Called when the service is first created, initializations happen here.
	 */
	@Override
	public void onCreate() {
		super.onCreate();
		
		my_timer = new Timer();
		my_battery_info = new DS2784Battery();
	}
	
	/**
	 * Called when the service is destroyed.
	 */
	@Override
	public void onDestroy() {
		stopService();
		super.onDestroy();
	}
	
	/**
	 * Called when the service is started.
	 */
	@Override
	public void onStart(Intent intent, int startid) {
		startService();
	}

	@Override
	public IBinder onBind(Intent arg0) {

		return null;
	}
	
	//Private Methods
	
	/**
	 * Starts the timer to write to the log.
	 */
	private void startService() {
		my_timer.scheduleAtFixedRate(new TimerTask() {
			public void run() {
				appendToLog();
			}
		}, 0, INTERVAL);
	}

	/**
	 * Stops the timer.
	 */
	private void stopService() {
		if (my_timer != null){
			my_timer.cancel();
		}
	}
	

	/**
	 * Returns the contents of the log file.
	 * @return The contents of the log file.
	 */
	private String readLog() {
		String log_contents = "";
		
		try {
			StringBuffer string_buffer = new StringBuffer();
			int character;
			FileInputStream file_input_stream = openFileInput(BatteryApp.LOG_FILE);
			while( (character = file_input_stream.read()) != -1) {
				string_buffer.append((char)character);
			}
			file_input_stream.close();
			
			log_contents = string_buffer.toString();
		} catch(Exception e) {
			
		}
		
		
		return log_contents;
	}
	
	/**
	 * Writes a string to a log file. This method overwrites an existing log file, or creates a new one if it does not already exist.
	 * @param the_string_to_write The string to write to the log file.
	 */
	private void writeToLog(String the_string_to_write) {
		try {
			FileOutputStream fos = openFileOutput(BatteryApp.LOG_FILE, Context.MODE_PRIVATE);
			fos.write(the_string_to_write.getBytes());
			fos.close();
		} catch(Exception e) {
			
		}
	}
	
	/**
	 * Writes a string to a log file. This method appends to an existing log file, or creates a new one if it does not already exist.
	 */
	private void appendToLog() {
		try {
			trimLog();
			FileOutputStream fos = openFileOutput(BatteryApp.LOG_FILE, Context.MODE_APPEND);
			fos.write(logMessage().getBytes());
			fos.close();
		} catch(Exception e) {
			e.toString();
		}
	}
	
	/**
	 * Returns the message to be written in the log.
	 * @return The message to be written in the log.
	 */
	private String logMessage() {
		String log_message = "";
		
		Calendar calendar = Calendar.getInstance();
		String date = calendar.getTime().toString();
		
		//Populate mAh capacity
		String capacity_text = my_battery_info.getMAh();
		try {
			int mAh = (Integer.parseInt(capacity_text))/1000;
			capacity_text = Integer.toString(mAh);
		} catch(Exception e) {
			
		}
		
		//Populate voltage
		String voltage_text = my_battery_info.getVoltage();
		try {
			BigDecimal big_decimal = new BigDecimal(Double.parseDouble(voltage_text) / 1000);
			big_decimal = big_decimal.setScale(2,BigDecimal.ROUND_UP);
			voltage_text = big_decimal.toString();
		} catch(Exception e) {
			
		}
		
		//Populate current
		String current_text = my_battery_info.getCurrent();
		try {
			double curr = (Double.parseDouble(current_text));
			current_text = Double.toString(curr/1000);
		} catch(Exception e) {
			
		}
		
		//Populate age
		String age_text = my_battery_info.getDumpRegister(20);
		try {
			int age_converted = (Integer.parseInt(age_text,16))*100/128;
			age_text = Integer.toString(age_converted);
		} catch(Exception e) {
			
		}
		
		//Populate battery percent
		String percent_text = my_battery_info.getDumpRegister(6);
		try {
			percent_text = Integer.toString(Integer.parseInt(percent_text,16));
		} catch(Exception e) {
			
		}
		
		log_message = date + "\n";
		log_message += "Capacity: " + capacity_text + "mAh\n";
		log_message += "Voltage: " + voltage_text + "mV\n";
		log_message += "Current: " + current_text + "mA\n";
		log_message += "age: " + age_text + "\n";
		log_message += "battery : " + percent_text + "%\n";
		log_message += "\n";
		
		return log_message;
	}
	
	/**
	 * Trims the log to last 100 entries so that it doesn't become too large.
	 */
	private void trimLog() {
		String log = readLog();
		String[] log_split = log.split("\n\n");
		String[] new_log = new String[LOG_LENGTH];
		int log_split_index = log_split.length - 1;
		if(log_split.length > LOG_LENGTH) {
			for(int index=LOG_LENGTH - 1; index>=0; index--) {
				new_log[index] = log_split[log_split_index] + "\n\n";
				log_split_index--;
			}
			
			log = "";
			for(int index=0; index<new_log.length; index++) {
				if(new_log[index] != null) {
					log += new_log[index];
				}
			}
		}
		
		writeToLog(log);
	}


}
//End of class LoggingService