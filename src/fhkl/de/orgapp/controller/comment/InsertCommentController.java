package fhkl.de.orgapp.controller.comment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import fhkl.de.orgapp.R;
import fhkl.de.orgapp.controller.event.EventController;
import fhkl.de.orgapp.util.JSONParser;
import fhkl.de.orgapp.util.data.EventData;
import fhkl.de.orgapp.util.data.UserData;

public class InsertCommentController extends Activity {
	
	/**
	 * CommentController: 
	 * 
	 * CREATE/INSERT 
	 * @param do = "addcomment"
	 * @param eventId 
	 * @param personId
	 * @param message
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
	
	JSONParser jsonParser = new JSONParser();
	JSONArray comment = null;
	
	private ProgressDialog pDialog;
	
	//Form
	EditText inputMessage;
	Button bSubmit;
	Button bCancel;

	//Confirmation 
	TextView eventName;
	TextView eventTime;
	TextView eventDate;
	TextView eventLocation;
	TextView messageContent;
	TextView messageDateTime;

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.insertcomment);
		
		eventName = (TextView) findViewById(R.id.EVENTNAME);
		eventTime = (TextView) findViewById(R.id.EVENTTIME);
		eventDate = (TextView) findViewById(R.id.EVENTDATE);
		eventLocation = (TextView) findViewById(R.id.EVENTLOCATION);
		
		eventName.setText("Event: " + EventData.getNAME());
		eventTime.setText("Time: " + EventData.getEVENTTIME());
		eventDate.setText("Date: " + EventData.getEVENTDATE());
		eventLocation.setText("Location: " + EventData.getEVENTLOCATION());
		
		inputMessage = (EditText) findViewById(R.id.COMMENTTEXT);
		bSubmit = (Button) findViewById(R.id.SUBMITBUTTON);
		bCancel = (Button) findViewById(R.id.CANCELBUTTON);
		
		bSubmit.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				new InsertComment().execute();
			}
		});
		
		bCancel.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(InsertCommentController.this, EventController.class);
				startActivity(intent);
			}
		});	
	}
	
	class InsertComment extends AsyncTask<String, String, String>{
		
		@Override
		protected void onPreExecute() {
			//pDialog = new ProgressDialog(InsertCommentController.this);
			//No progress dialog needed!
			super.onPreExecute();
		}
		
		@Override
		protected String doInBackground(String... params) {
			String eventId = EventData.getEVENTID();
			String personId = UserData.getPERSONID();
			String commenttxt = inputMessage.getText().toString();
			
			List<NameValuePair> paramsInsertComment = new ArrayList<NameValuePair>();
			
			paramsInsertComment.add(new BasicNameValuePair("do", "addcomment")); //specific identifier for insert/add comment operation
			paramsInsertComment.add(new BasicNameValuePair("eventId", eventId));
			paramsInsertComment.add(new BasicNameValuePair("personId", personId));
			paramsInsertComment.add(new BasicNameValuePair("message", commenttxt));
			
			JSONObject json = jsonParser.makeHttpRequest(URL_COMMENTCONTROL, "GET", paramsInsertComment);
			
			Log.d("Insert comment", json.toString());
			
			return null;
		}
		
		
		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			
			//check if the message not empty
			if(result != null) {
				Intent i = new Intent(InsertCommentController.this, EventController.class);
				startActivity(i);
				finish();
			}
		}
	}

}
