package net.jonrichards.batteryapp.ui;

import java.text.SimpleDateFormat;
import java.util.Date;

import net.jonrichards.batteryapp.ui.R;

import com.teslacoilsw.quicksshd.ShellCommand;
import com.teslacoilsw.quicksshd.ShellCommand.CommandResult;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.TextView;

public class RealTimeActivity extends Activity {

	private static final String TAG = "NOW";
	private TextView textview;
	private final Handler mHandler = new Handler();
	
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.realtimelayout);

        //textview = new TextView(this);
        //textview.setText(getText());
        //textview.append("Last updated: " + getDateTime());
        //setContentView(textview);
                
        //mHandler.postDelayed(mUpdateUITimerTask, 10 * 1000);
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
