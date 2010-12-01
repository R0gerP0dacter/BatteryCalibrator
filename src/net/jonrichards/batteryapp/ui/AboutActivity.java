package net.jonrichards.batteryapp.ui;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

public class AboutActivity extends Activity
{
        
        /**
         * Called when the activity is first created.
         */
        @Override
        public void onCreate(Bundle savedInstanceState)
        {
                super.onCreate(savedInstanceState);
                setContentView(R.layout.about);
                TextView my_about_title = (TextView) findViewById(R.id.txtTitle);
                TextView my_about_name = (TextView) findViewById(R.id.txtAppName);
                TextView my_about_version = (TextView) findViewById(R.id.txtAppVersion);
                TextView my_about_author = (TextView) findViewById(R.id.txtAuthor);
                TextView my_about_text = (TextView) findViewById(R.id.txtAbout);

                String text = this.getResources().getText(R.string.about_title).toString();
                String text1 = this.getResources().getText(R.string.app_name).toString();
                String text2 = this.getResources().getText(R.string.app_version).toString();

                String text3 = this.getResources().getText(R.string.about_author).toString();
                String text4 = this.getResources().getText(R.string.about_text).toString();

                my_about_title.setText(text);
                my_about_name.setText(text1);
                my_about_version.setText(text2);
                my_about_author.setText(text3);
                my_about_text.setText(text4);

                
        }
        
}
