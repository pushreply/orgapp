package fhkl.de.orgapp.controller.comment;

import fhkl.de.orgapp.R;
import fhkl.de.orgapp.R.id;
import fhkl.de.orgapp.R.layout;
import fhkl.de.orgapp.R.menu;
import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

public class CommentControl extends Activity {

	
	/**
	 * Insert a comment
	 * 
	 */
	
	/**
	 * Update a comment
	 * 
	 */
	
	/**
	 * Delete a comment
	 * 
	 */
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.comment_control);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.comment_control, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
