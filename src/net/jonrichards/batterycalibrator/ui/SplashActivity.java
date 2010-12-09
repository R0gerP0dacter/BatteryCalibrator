/* This program is free software. It comes without any warranty, to
 * the extent permitted by applicable law. You can redistribute it
 * and/or modify it under the terms of the Do What The Fuck You Want
 * To Public License, Version 2, as published by Sam Hocevar. See
 * http://sam.zoy.org/wtfpl/COPYING for more details. */ 
package net.jonrichards.batterycalibrator.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
/**
 * A class for showing information about this application.
 * @author Jon Richards
 * @author Roger Podacter
 */
public class SplashActivity extends Activity {
	
	//Instance Variables
    private final int SPLASH_DISPLAY_LENGHT = 2000;

	
	
	//Public Methods
	
	/**
	 * Called when the activity is first created.
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.splash);
		
		new Handler().postDelayed(new Runnable(){
			 
            //@Override

            public void run() {

                    /* Create an Intent that will start the Main-Activity. */

                    Intent mainIntent = new Intent(SplashActivity.this,BatteryApp.class);

                    SplashActivity.this.startActivity(mainIntent);

                    SplashActivity.this.finish();

            }

    }, SPLASH_DISPLAY_LENGHT);
	}	
	
}
//End of class AboutActivity