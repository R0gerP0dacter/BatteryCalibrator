package net.jonrichards.batteryapp.ui;

import net.jonrichards.battery.R;
import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

public class EventsActivity extends Activity {

	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.eventslayout);

        //TextView textview = new TextView(this);
        //textview.setText("This is the Events tab");
        //setContentView(textview);
    }
}
