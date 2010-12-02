package net.jonrichards.batteryapp.ui;

import net.jonrichards.batteryapp.system.DS2784Battery;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class LearnPrepActivity extends Activity {

	private static final String TAG = "LearnPrepActivity";
	private TextView textview;
	private final Handler mHandler = new Handler();
	
	private TextView my_age;
	private Button my_age_button;
	
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.learnpreplayout);

        //Added raw dumpreg to registers tab
        my_age = (TextView)findViewById(R.id.txtAge);
		my_age_button = (Button)findViewById(R.id.btnAge);
		//my_age.setText(setUIText());
		
		//button click to manually update dumpreg
        my_age_button.setOnClickListener(new OnClickListener() {
        	public void onClick(View v) {        		
        		//Populate age text view
        		setUIText();        		
        	}
        });        
    }
	//Options menu
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
	    MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.menu, menu);
	    return true;
	}
	//Options menu
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    switch (item.getItemId()) {
	        case R.id.about:     
	        	Intent myIntent = new Intent();
                myIntent.setClass(this, AboutActivity.class);
                startActivity(myIntent);
                break;
	        case R.id.tech_help:     
	        	String text = this.getResources().getText(R.string.about_test).toString();
				AlertDialog.Builder builder = new AlertDialog.Builder(this);
				builder.setTitle(R.string.about);
				builder.setPositiveButton(R.string.ok, null);
		        builder.setMessage(text).create().show();
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
	
	@Override
    public void onPause()
    {
            //mHandler.removeCallbacks(mUpdateUITimerTask);
            super.onPause();
    }
	
	@Override
    public void onResume()
    {
            //mHandler.postDelayed(mUpdateUITimerTask, 10 * 1000);
            super.onResume();
    }
	
	/*private final Runnable mUpdateUITimerTask = new Runnable() {
	    public void run() {
	    	textview.setText(getText());
	        //textview.append("Last updated: " + getDateTime());
	        mHandler.postDelayed(mUpdateUITimerTask, 10 * 1000);
	    }
	};*/
	
	public void setUIText() {
		DS2784Battery battery_info = new DS2784Battery();		
		
		//Populate age
		String age_text = battery_info.getDumpRegister(20);
		int age_converted = (Integer.parseInt(age_text,16))*100/128;
		my_age.setText(Integer.toString(age_converted));
	}	
	
}
