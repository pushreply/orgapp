package fhkl.de.orgapp.controller.comment;

import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;

import fhkl.de.orgapp.R;
import fhkl.de.orgapp.R.id;
import fhkl.de.orgapp.R.layout;
import fhkl.de.orgapp.R.menu;
import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;

public class CommentController extends Activity {
	
	/**
	 * CommentController: 
	 * 
	 * CREATE/INSERT 
	 * @param do = "addcomment"
	 * @param eventId 
	 * @param personId
	 * @param message
	 * 
	 *  
	 * EDIT/UPDATE
	 * @param do = "updatecomment"
	 * @param commentId
	 * @param eventId
	 * @param personId
	 * @param message
	 * 
	 * DELETE
	 * @param do = "deletecomment"
	 * @param commentId
	 * @param eventId
	 * @param personId
	 * 
	 */
	private static String URL_COMMENTCONTROL = "http://pushrply.com/pdo_commentcontrol.php";

	private static final String TAG_SUCCESS = "success";
	
	private static final String TAG_COMMENT_ID = "COMMENTID";
	private static final String TAG_PERSON_ID = "PERSONID";
	private static final String TAG_FIRSTNAME = "FIRSTNAME";
	private static final String TAG_LASTNAME = "LASTNAME";
	private static final String TAG_MESSAGE = "MESSAGE";
	private static final String TAG_COMMENTDATETIME = "COMMENTDATETIME";
	
	ArrayList<HashMap<String, String>> commentList;
	JSONArray comment = null;
	
	//Form
	EditText inputMessage;

	//Confirmation 
	TextView messageContent;
	TextView messageDateTime;

	
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
		setContentView(R.layout.insertcomment);
		
		
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
