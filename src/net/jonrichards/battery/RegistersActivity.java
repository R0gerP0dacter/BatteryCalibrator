package net.jonrichards.battery;

import java.text.SimpleDateFormat;
import java.util.Date;

import android.app.Activity;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.teslacoilsw.quicksshd.ShellCommand;
import com.teslacoilsw.quicksshd.ShellCommand.CommandResult;

public class RegistersActivity extends Activity {
	
	private static final String TAG = "NOW";
	private TextView textview;
	private final Handler mHandler = new Handler();
	
	private EditText statusregister;
	private Button statusbutton;
	private EditText full40;
	private TextView statustext;
	private EditText voltage;
	private EditText realtimevolt;


	
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.registerslayout);
        
        

        
        //statusregister = (EditText)findViewById(R.id.etStatus);
        full40 = (EditText)findViewById(R.id.widget35);
        statusbutton = (Button)findViewById(R.id.btnStatus);
        //statustext = (TextView)findViewById(R.id.widget56);
        voltage = (EditText)findViewById(R.id.etVoltage);
        realtimevolt = (EditText)findViewById(R.id.etRealTimeVolt);
        
        statusbutton.setOnClickListener(new OnClickListener() {
        	public void onClick(View v) {
        		statusbutton.setText(getStatusText());
        		voltage.setText(getVoltageText());
        		realtimevolt.setText(getVoltageText());
        	}
        });
        //textview = new TextView(this);
        //textview.setText(getText());
        //textview.append("Status register last read at: " + getDateTime());
        //setContentView(textview);
                
        //mHandler.postDelayed(mUpdateUITimerTask, 10 * 1000);
    }
	
	@Override
    public void onAttachedToWindow() {
    	    super.onAttachedToWindow();
    	    Window window = getWindow();
    	    window.setFormat(PixelFormat.RGBA_8888);
    	}

	
	private final Runnable mUpdateUITimerTask = new Runnable() {
	    public void run() {
	    	textview.setText(getStatusText());
	        textview.append("Status register last read at: " + getDateTime());
	        mHandler.postDelayed(mUpdateUITimerTask, 10 * 1000);
	    }
	};
	
	private String getStatusText() {
		String result = "";
		
		ShellCommand cmd = new ShellCommand();
		CommandResult r = cmd.sh.runWaitFor("cat /sys/devices/platform/ds2784-battery/statusreg");

		if (!r.success()) {
		  Log.v(TAG, "Error " + r.stderr);
		} else {
		  result = r.stdout;
		}		
		
		int chgtf = ((Integer.parseInt("80", 16) & Integer.parseInt(result.substring(2), 16)) != 0)?1:0;
		int aef = ((Integer.parseInt("40", 16) & Integer.parseInt(result.substring(2), 16)) != 0)?1:0;
		int sef = ((Integer.parseInt("20", 16) & Integer.parseInt(result.substring(2), 16)) != 0)?1:0;
		int learnf = ((Integer.parseInt("10", 16) & Integer.parseInt(result.substring(2), 16)) != 0)?1:0;
		int uvf = ((Integer.parseInt("04", 16) & Integer.parseInt(result.substring(2), 16)) != 0)?1:0;
		int porf = ((Integer.parseInt("02", 16) & Integer.parseInt(result.substring(2), 16)) != 0)?1:0;
		
		result = "Status Register: " + result + "\n\n";
		result += "Charge-Termination Flag (CHGTF): " + chgtf + "\n";
		result += "Active-Empty Flag (AEF): " + aef + "\n";
		result += "Standby-Empty Flag (SEF): " + sef + "\n";
		result += "Learn Flag (LEARNF): " + learnf + "\n";
		result += "Undervoltage Flag (UVF): " + uvf + "\n";
		result += "Power-On Reset Flag (PORF): " + porf + "\n\n";
		
		return result;
	}
	
	private String getVoltageText() {
		String result = "";
		
		ShellCommand cmd = new ShellCommand();
		CommandResult r = cmd.sh.runWaitFor("cat /sys/devices/platform/ds2784-battery/getvoltage");

		if (!r.success()) {
		  Log.v(TAG, "Error " + r.stderr);
		} else {
		  result = r.stdout;
		}		
		
		//result = "Status Register: " + result + "\n\n";
				
		return result;
	}
	
	private String getDateTime() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Date date = new Date();
        return dateFormat.format(date);
    }
	private void CreateMenu(Menu menu)
    {
        menu.setQwertyMode(true);
        MenuItem mnu1 = menu.add(0, 0, 0, "Item 1");
        {
            mnu1.setAlphabeticShortcut('a');
            mnu1.setIcon(R.drawable.icon);            
        }
        MenuItem mnu2 = menu.add(0, 1, 1, "Item 2");
        {
            mnu2.setAlphabeticShortcut('b');
            mnu2.setIcon(R.drawable.icon);            
        }
        MenuItem mnu3 = menu.add(0, 2, 2, "Item 3");
        {
            mnu3.setAlphabeticShortcut('c');
            mnu3.setIcon(R.drawable.icon);
        }
        MenuItem mnu4 = menu.add(0, 3, 3, "Item 4");
        {
            mnu4.setAlphabeticShortcut('d');                    
        }
        menu.add(0, 3, 3, "Item 5");
        menu.add(0, 3, 3, "Item 6");
        menu.add(0, 3, 3, "Item 7");
    }
 
    private boolean MenuChoice(MenuItem item)
    {        
        switch (item.getItemId()) {
        case 0:
            Toast.makeText(this, "You clicked on Item 1", 
                Toast.LENGTH_LONG).show();
            return true;
        case 1:
            Toast.makeText(this, "You clicked on Item 2", 
                Toast.LENGTH_LONG).show();
            return true;
        case 2:
            Toast.makeText(this, "You clicked on Item 3", 
                Toast.LENGTH_LONG).show();
            return true;
        case 3:
            Toast.makeText(this, "You clicked on Item 4", 
                Toast.LENGTH_LONG).show();
            return true;
        case 4:
            Toast.makeText(this, "You clicked on Item 5", 
                Toast.LENGTH_LONG).show();
            return true;
        case 5:
            Toast.makeText(this, "You clicked on Item 6", 
                Toast.LENGTH_LONG).show();
            return true;
        case 6:
            Toast.makeText(this, "You clicked on Item 7", 
                Toast.LENGTH_LONG).show();
            return true;            
        }
        return false;
    }  
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        CreateMenu(menu);
        return true;
    }
 
    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {    
         return MenuChoice(item);    
    }
    @Override
    public void onCreateContextMenu(ContextMenu menu, View view, 
    ContextMenuInfo menuInfo) 
    {
         super.onCreateContextMenu(menu, view, menuInfo);
         CreateMenu(menu);
    }
 
    @Override
    public boolean onContextItemSelected(MenuItem item)
    {    
         return MenuChoice(item);    
    }    

}