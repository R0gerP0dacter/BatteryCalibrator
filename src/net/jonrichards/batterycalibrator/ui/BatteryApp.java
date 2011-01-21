/* This program is free software. It comes without any warranty, to
 * the extent permitted by applicable law. You can redistribute it
 * and/or modify it under the terms of the Do What The Fuck You Want
 * To Public License, Version 2, as published by Sam Hocevar. See
 * http://sam.zoy.org/wtfpl/COPYING for more details. */ 
package net.jonrichards.batterycalibrator.ui;

import net.jonrichards.batterycalibrator.system.LoggingService;
import android.app.TabActivity;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;

/**
 * The main app activity.
 * @author Jon Richards
 * @author Roger Podacter
 */
public class BatteryApp extends TabActivity {
	
	//Static Variables
	
	/**
	 * A log file of battery information.
	 */
	public static final String LOG_FILE = "battery_log";
	
	//Instance Variables
	
	private Resources my_resources;
    private TabHost my_tab_host;
    private TabSpec my_tab_spec;
    private Intent my_intent;
	
	//Public Methods
	
	/**
	 * Called when the activity is first created, initializations happen here.
	 * @param savedInstanceState 
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		
	    super.onCreate(savedInstanceState);
		startService(new Intent(this, LoggingService.class));
	    setContentView(R.layout.main);
	    
	    my_resources = getResources();
	    my_tab_host = getTabHost();
	    
	    //Create the General tab
	    my_intent = new Intent().setClass(this, GeneralActivity.class);
	    my_tab_spec = my_tab_host.newTabSpec("general").setIndicator("General", my_resources.getDrawable(R.drawable.ic_tab_general)).setContent(my_intent);
    	my_tab_host.addTab(my_tab_spec);
    	
    	//Create the Learn Prep tab
    	my_intent = new Intent().setClass(this, LearnPrepActivity.class);
	    my_tab_spec = my_tab_host.newTabSpec("learnprep").setIndicator("LearnPrep", my_resources.getDrawable(R.drawable.ic_tab_learnprep)).setContent(my_intent);
	    my_tab_host.addTab(my_tab_spec);
	    
	    //Create the Learn Mode tab
	    my_intent = new Intent().setClass(this, LearnModeActivity.class);
	    my_tab_spec = my_tab_host.newTabSpec("learnmode").setIndicator("LearnMode", my_resources.getDrawable(R.drawable.ic_tab_learnmode)).setContent(my_intent);
	    my_tab_host.addTab(my_tab_spec);
	    
	    //Create the Registers tab
//	    my_intent = new Intent().setClass(this, LogActivity.class);
//	    my_tab_spec = my_tab_host.newTabSpec("log").setIndicator("Log", my_resources.getDrawable(R.drawable.ic_tab_registers)).setContent(my_intent);
//	    my_tab_host.addTab(my_tab_spec);
	    
	    //Create the Registers tab
	    my_intent = new Intent().setClass(this, RegistersActivity.class);
	    my_tab_spec = my_tab_host.newTabSpec("registers").setIndicator("Registers", my_resources.getDrawable(R.drawable.ic_tab_registers)).setContent(my_intent);
	    my_tab_host.addTab(my_tab_spec);
	    
	    //Set the default tab when the app is opened to the General tab
	    my_tab_host.setCurrentTab(0);
	}
	
	@Override
	public void onDestroy() {
		stopService(new Intent(this, LoggingService.class));
		super.onDestroy();
	}
	
}
//End of class BatteryApp