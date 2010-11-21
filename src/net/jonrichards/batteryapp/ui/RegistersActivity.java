package net.jonrichards.batteryapp.ui;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.teslacoilsw.quicksshd.ShellCommand;
import com.teslacoilsw.quicksshd.ShellCommand.CommandResult;

public class RegistersActivity extends Activity {
	
	private static final String TAG = "NOW";
	private TextView textview;
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
    

}