/* This program is free software. It comes without any warranty, to
 * the extent permitted by applicable law. You can redistribute it
 * and/or modify it under the terms of the Do What The Fuck You Want
 * To Public License, Version 2, as published by Sam Hocevar. See
 * http://sam.zoy.org/wtfpl/COPYING for more details. */ 
package net.jonrichards.batterycalibrator.ui;

import net.jonrichards.batterycalibrator.ui.R;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.widget.TextView;

/**
 * A class for showing information about this application.
 * @author Jon Richards
 * @author Roger Podacter
 */
public class AboutActivity extends Activity {
	
	//Instance Variables
	
	private PowerManager my_power_manager;
	private WakeLock my_wake_lock;
	
	//Public Methods
	
	/**
	 * Called when the activity is first created.
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.about);
		
		my_power_manager = (PowerManager)getBaseContext().getSystemService(Context.POWER_SERVICE);
        my_wake_lock = my_power_manager.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK | PowerManager.ON_AFTER_RELEASE, "LearnModeActivity");
		
		TextView my_about_title = (TextView)findViewById(R.id.txtTitle);
		TextView my_about_name = (TextView)findViewById(R.id.txtAppName);
		TextView my_about_version = (TextView)findViewById(R.id.txtAppVersion);
		TextView my_about_author = (TextView)findViewById(R.id.txtAuthor);
		TextView my_about_author_name = (TextView)findViewById(R.id.txtAuthorName);
		TextView my_about_text = (TextView)findViewById(R.id.txtAbout);
		TextView my_support_title = (TextView)findViewById(R.id.txtSupport);
		TextView my_support_text = (TextView)findViewById(R.id.txtSupportText);

		my_about_title.setText(getResources().getText(R.string.about_title).toString());
		my_about_name.setText(getResources().getText(R.string.app_name).toString());
		my_about_version.setText(getResources().getText(R.string.app_version).toString());
		my_about_author.setText(getResources().getText(R.string.about_author).toString());
		my_about_author_name.setText(getResources().getText(R.string.about_author_name).toString());
		my_about_text.setText(getResources().getText(R.string.about_text).toString());
		my_support_title.setText(getResources().getText(R.string.support_title).toString());
		my_support_text.setText(getResources().getText(R.string.support_text).toString());
	}
	
	/**
	 * Called when this activity is resumed.
	 */
	@Override
    public void onResume() {
		if(LearnModeActivity.LEARN_MODE && SettingsActivity.getEnableScreenOn(getBaseContext())) {
			if(!my_wake_lock.isHeld()) {
				my_wake_lock.acquire();
			}
		}
        super.onResume();
    }

	/**
	 * Called when this activity is paused.
	 */
	@Override
    public void onPause() {
		if(my_wake_lock.isHeld()) {
			my_wake_lock.release();
		}
        super.onPause();
    }
}
//End of class AboutActivity