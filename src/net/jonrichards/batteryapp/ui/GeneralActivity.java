package net.jonrichards.batteryapp.ui;

import android.app.Activity;
import android.os.Bundle;
import net.jonrichards.batteryapp.ui.R;

public class GeneralActivity extends Activity {

	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.generallayout);

        //TextView textview = new TextView(this);
        //textview.setText("This is the Events tab");
        //setContentView(textview);
    }
}
