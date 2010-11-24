package net.jonrichards.batteryapp.ui;

import java.text.SimpleDateFormat;
import java.util.Date;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.teslacoilsw.quicksshd.ShellCommand;
import com.teslacoilsw.quicksshd.ShellCommand.CommandResult;

public class LearnPrepActivity extends Activity {

	private static final String TAG = "LearnPrepActivity";
	private TextView textview;
	private final Handler mHandler = new Handler();
	
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.learnpreplayout);

        //textview = new TextView(this);
        //textview.setText(getText());
        //textview.append("Last updated: " + getDateTime());
        //setContentView(textview);
                
        //mHandler.postDelayed(mUpdateUITimerTask, 10 * 1000);
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
	        	Toast.makeText(this, "Write an ABOUT section here?", Toast.LENGTH_LONG).show();
	            break;
	        case R.id.tech_help:     
	        	Toast.makeText(this, "Add advnced technical info/help section here?", Toast.LENGTH_LONG).show();
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
            mHandler.removeCallbacks(mUpdateUITimerTask);
            super.onPause();
    }
	
	@Override
    public void onResume()
    {
            mHandler.postDelayed(mUpdateUITimerTask, 10 * 1000);
            super.onResume();
    }
	
	private final Runnable mUpdateUITimerTask = new Runnable() {
	    public void run() {
	    	textview.setText(getText());
	        textview.append("Last updated: " + getDateTime());
	        mHandler.postDelayed(mUpdateUITimerTask, 10 * 1000);
	    }
	};
	
	private String getText() {
		String result = "";
		
		ShellCommand cmd = new ShellCommand();
		CommandResult r = cmd.sh.runWaitFor("cat /sys/devices/platform/ds2784-battery/power_supply/battery/uevent");

		if (!r.success()) {
		  Log.v(TAG, "Error " + r.stderr);
		} else {
		  result = r.stdout;
		}
		
		return result + "\n\n";
	}
	
	private String getDateTime() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Date date = new Date();
        return dateFormat.format(date);
    }	
	
	
}
