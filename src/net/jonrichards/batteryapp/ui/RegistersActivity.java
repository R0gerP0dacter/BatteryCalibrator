package net.jonrichards.batteryapp.ui;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.teslacoilsw.quicksshd.ShellCommand;
import com.teslacoilsw.quicksshd.ShellCommand.CommandResult;

public class RegistersActivity extends Activity {
	
	private static final String TAG = "RegistersActivity";

	private final Handler mHandler = new Handler();
	
	private TextView dump01;
	private Button updatebutton;

	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.registerslayout);
        
        //Added raw dumpreg to registers tab
        dump01 = (TextView)findViewById(R.id.widget1);
		updatebutton = (Button)findViewById(R.id.btnUpdate);
		dump01.setText(getUIText());
		
		//button click to manually update dumpreg
        updatebutton.setOnClickListener(new OnClickListener() {
        	public void onClick(View v) {        		
        		//Populate raw dumpreg for now
        		dump01.setText(getUIText());        		
        	}
        });        
    }
	
	private String getUIText() {
		String result = "";
		
		ShellCommand cmd = new ShellCommand();
		CommandResult r = cmd.sh.runWaitFor("cat /sys/devices/platform/ds2784-battery/dumpreg");

		if (!r.success()) {
		  Log.v(TAG, "Error " + r.stderr);
		} else {
		  result = r.stdout;
		}		
		
		return result;
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
            super.onPause();
            //SAMPLE_POLL = 0;
    }
	
	@Override
    public void onResume()
    {
            super.onResume();
            //SAMPLE_POLL = 30;

    }

}