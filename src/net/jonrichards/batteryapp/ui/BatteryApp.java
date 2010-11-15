package net.jonrichards.batteryapp.ui;

import net.jonrichards.battery.R;
import android.app.TabActivity;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.widget.TabHost;

public class BatteryApp extends TabActivity {

	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.main);

	    Resources res = getResources(); // Resource object to get Drawables
	    TabHost tabHost = getTabHost();  // The activity TabHost
	    TabHost.TabSpec spec;  // Resusable TabSpec for each tab
	    Intent intent;  // Reusable Intent for each tab

	    // Create an Intent to launch an Activity for the tab (to be reused)
	    intent = new Intent().setClass(this, RealTimeActivity.class);

	    // Initialize a TabSpec for each tab and add it to the TabHost
	    spec = tabHost.newTabSpec("realtime").setIndicator("RealTime",
	                      res.getDrawable(R.drawable.ic_tab_realtime))
	                  .setContent(intent);
	    tabHost.addTab(spec);

	    // Do the same for the other tabs
	    intent = new Intent().setClass(this, RegistersActivity.class);
	    spec = tabHost.newTabSpec("registers").setIndicator("Registers",
	                      res.getDrawable(R.drawable.ic_tab_registers))
	                  .setContent(intent);
	    tabHost.addTab(spec);

	    intent = new Intent().setClass(this, EventsActivity.class);
	    spec = tabHost.newTabSpec("events").setIndicator("Events",
	                      res.getDrawable(R.drawable.ic_tab_events))
	                  .setContent(intent);
	    tabHost.addTab(spec);

	    tabHost.setCurrentTab(1);
	}
}
