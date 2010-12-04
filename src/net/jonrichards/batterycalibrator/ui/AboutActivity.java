package net.jonrichards.batterycalibrator.ui;

import net.jonrichards.batterycalibrator.ui.R;
import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

/**
 * A class for showing information about this application.
 * @author Jon Richards
 * @author Roger Podacter
 */
public class AboutActivity extends Activity {
	
	/**
	 * Called when the activity is first created.
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.about);
		
		TextView my_about_title = (TextView)findViewById(R.id.txtTitle);
		TextView my_about_name = (TextView)findViewById(R.id.txtAppName);
		TextView my_about_version = (TextView)findViewById(R.id.txtAppVersion);
		TextView my_about_author = (TextView)findViewById(R.id.txtAuthor);
		TextView my_about_author_name = (TextView)findViewById(R.id.txtAuthorName);
		TextView my_about_text = (TextView)findViewById(R.id.txtAbout);
		TextView my_support_title = (TextView)findViewById(R.id.txtSupport);
		TextView my_support_text = (TextView)findViewById(R.id.txtSupportText);

		my_about_title.setText(getResources().getText(R.string.about_title).toString());
		my_about_name.setText(getResources().getText(R.string.app_name).toString());
		my_about_version.setText(getResources().getText(R.string.app_version).toString());
		my_about_author.setText(getResources().getText(R.string.about_author).toString());
		my_about_author_name.setText(getResources().getText(R.string.about_author_name).toString());
		my_about_text.setText(getResources().getText(R.string.about_text).toString());
		my_support_title.setText(getResources().getText(R.string.support_title).toString());
		my_support_text.setText(getResources().getText(R.string.support_text).toString());
	}
}
//End of class AboutActivity