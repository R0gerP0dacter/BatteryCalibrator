package net.jonrichards.batterycalibrator.ui;

import net.jonrichards.batterycalibrator.ui.R;
import net.jonrichards.batterycalibrator.system.DS2784Battery;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

/**
 * A class for displaying information from the dump register.
 * @author Jon Richards
 * @author Roger Podacter
 */
public class RegistersActivity extends Activity {
	
	//Instance Variables
	
	private TextView my_register_dump;
	private Button my_update_button;

	/**
	 * Called when the activity is first created, initializations happen here.
	 * @param savedInstanceState 
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.registerslayout);
        
        //Added raw dumpreg to registers tab
        my_register_dump = (TextView)findViewById(R.id.widget1);
        my_update_button = (Button)findViewById(R.id.btnUpdate);
        setUIText();
		
		//button click to manually update dumpreg
        my_update_button.setOnClickListener(new OnClickListener() {
        	public void onClick(View v) {        		
        		//Populate raw dumpreg for now
        		setUIText();       		
        	}
        });
    }
	
	/**
	 * Called when this activity is paused.
	 */
	@Override
    public void onPause() {
        super.onPause();
    }
	
	/**
	 * Called when this activity is resumed.
	 */
	@Override
    public void onResume() {
        super.onResume();
    }
	
	/**
	 * Creates an options menu.
	 * @param menu The options menu to place the menu items in.
	 * @return Returns boolean true.
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
	    MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.menu, menu);
	    return true;
	}
	
	/**
	 * Called when an item in the options menu is selected.
	 * @param item The MenuItem selected.
	 * @return Returns a boolean true.
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    switch (item.getItemId()) {
	        case R.id.about:     
	        	Intent myIntent = new Intent();
                myIntent.setClass(this, AboutActivity.class);
                startActivity(myIntent);
                break;
	        case R.id.tech_help:     
	        	String text = getResources().getText(R.string.status_register).toString();
				AlertDialog.Builder builder = new AlertDialog.Builder(this);
				builder.setTitle(R.string.status_title);
				builder.setPositiveButton(R.string.ok, null);
		        builder.setMessage(text).create().show();
		        break;
	        case R.id.settings: 
	        	startActivity(new Intent(this, SettingsActivity.class));                
	            break;
	        case R.id.instructions: 
	        	Toast.makeText(this, "Add directions for the app", Toast.LENGTH_LONG).show();
	            break;
	        case R.id.exit: 
	        	finish();
            break;
	    }
	    return true;
	}
	
	//Private Methods
	
	/**
	 * Sets the UI text.
	 */
	private void setUIText() {
		DS2784Battery battery_info = new DS2784Battery();
		my_register_dump.setText(battery_info.getDumpRegister());
	}
}
//End of class Registers