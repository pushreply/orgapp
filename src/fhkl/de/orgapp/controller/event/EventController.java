package fhkl.de.orgapp.controller.event;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.ToggleButton;
import fhkl.de.orgapp.R;
import fhkl.de.orgapp.util.CommentData;
import fhkl.de.orgapp.util.EventData;
import fhkl.de.orgapp.util.IMessages;
import fhkl.de.orgapp.util.JSONParser;
import fhkl.de.orgapp.util.MenuActivity;
import fhkl.de.orgapp.util.UserData;

public class EventController extends MenuActivity {

	private ProgressDialog pDialog;
	JSONParser jsonParser = new JSONParser();

	private static String URL_GET_PERSON_IN_EVENT = "http://pushrply.com/get_person_in_event.php";
	private static String URL_CREATE_PERSON_IN_EVENT = "http://pushrply.com/create_person_in_event.php";
	private static String URL_DELETE_PERSON_IN_EVENT = "http://pushrply.com/delete_person_in_event.php";

	private static final String TAG_SUCCESS = "success";

	TextView eventTime;
	TextView eventDate;
	TextView eventLocation;

	ToggleButton buttonAttendance;
	boolean toggleButtonChecked;

	// commentcontrol
	private static String URL_COMMENTCONTROL = "http://pushrply.com/pdo_commentcontrol.php";

	private static final String TAG_COMMENT_ID = "COMMENTID";
	private static final String TAG_PERSON_ID = "PERSONID";
	private static final String TAG_FIRSTNAME = "FIRSTNAME";
	private static final String TAG_LASTNAME = "LASTNAME";
	private static final String TAG_MESSAGE = "MESSAGE";
	private static final String TAG_COMMENTDATETIME = "COMMENTDATETIME";

	TextView messageContent;
	TextView messageDateTime;
	TextView firstname;
	TextView lastname;

	ArrayList<HashMap<String, String>> commentList;
	JSONArray comment = null;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.event);
		checkNewNotificationAndCreateIcon();
		this.setTitle(EventData.getNAME());
		eventTime = (TextView) findViewById(R.id.EVENTTIME);
		eventDate = (TextView) findViewById(R.id.EVENTDATE);
		eventLocation = (TextView) findViewById(R.id.EVENTLOCATION);

		eventTime.setText("Time: " + EventData.getEVENTTIME());
		eventDate.setText("Date: " + EventData.getEVENTDATE());
		eventLocation.setText("Location: " + EventData.getEVENTLOCATION());

		buttonAttendance = (ToggleButton) findViewById(R.id.BUTTONATTENDANCE);
		buttonAttendance.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View view) {

				toggleButtonChecked = ((ToggleButton) view).isChecked();
				new ChangeAttendingStatus().execute();
			}
		});
		new GetEvent().execute();

		commentList = new ArrayList<HashMap<String, String>>();
		new ShowComments().execute();
	}

	class GetEvent extends AsyncTask<String, String, String> {
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pDialog = new ProgressDialog(EventController.this);
			pDialog.setMessage(IMessages.LOADING_EVENT);
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(true);
			pDialog.show();
		}

		protected String doInBackground(String... args) {
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair("personId", UserData.getPERSONID()));
			params.add(new BasicNameValuePair("eventId", EventData.getEVENTID()));
			JSONObject json = jsonParser.makeHttpRequest(URL_GET_PERSON_IN_EVENT, "GET", params);

			Log.d("EventPerson: ", json.toString());

			try {
				int success = json.getInt(TAG_SUCCESS);
				if (success == 1) {
					toggleButtonChecked = true;
				} else {
					toggleButtonChecked = false;
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
			return null;
		}

		protected void onPostExecute(String message) {
			pDialog.dismiss();

			setText();
		}
	}

	private void setText() {

		buttonAttendance.setChecked(toggleButtonChecked);
	}

	class ChangeAttendingStatus extends AsyncTask<String, String, String> {
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pDialog = new ProgressDialog(EventController.this);
			pDialog.setMessage(IMessages.CHANGING_STATUS);
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(true);
			pDialog.show();
		}

		protected String doInBackground(String... args) {
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair("personId", UserData.getPERSONID()));
			params.add(new BasicNameValuePair("eventId", EventData.getEVENTID()));
			JSONObject json;
			if (toggleButtonChecked) {
				json = jsonParser.makeHttpRequest(URL_CREATE_PERSON_IN_EVENT, "GET", params);
			} else {
				json = jsonParser.makeHttpRequest(URL_DELETE_PERSON_IN_EVENT, "GET", params);
			}

			Log.d("EventPerson: ", json.toString());

			try {
				int success = json.getInt(TAG_SUCCESS);
				if (success == 1) {
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
			return null;
		}

		protected void onPostExecute(String message) {
			pDialog.dismiss();
		}
	}

	// Show comments
	class ShowComments extends AsyncTask<String, String, String> {

		@Override
		protected String doInBackground(String... params) {
			List<NameValuePair> vp = new ArrayList<NameValuePair>();

			CommentData.setACTION("showcomment");
			vp.add(new BasicNameValuePair("do", CommentData.getACTION()));
			vp.add(new BasicNameValuePair("eventId", EventData.getEVENTID()));

			System.out.println("EventData.getEVENTID() : " + EventData.getEVENTID());

			JSONObject json = jsonParser.makeHttpRequest(URL_COMMENTCONTROL, "GET", vp);

			Log.d("Comments: ", json.toString());

			try {
				int success = json.getInt(TAG_SUCCESS);
				if (success == 1) {
					comment = json.getJSONArray("comment");

					for (int i = 0; i < comment.length(); i++) {

						JSONObject c = comment.getJSONObject(i);

						String commentId = c.getString("commentId");
						String personId = c.getString("personId");
						String firstname = c.getString("firstName");
						String lastname = c.getString("lastName");
						String commentdatetime = c.getString("commentDateTime");
						String message = c.getString("message");

						HashMap<String, String> map = new HashMap<String, String>();
						map.put(TAG_COMMENT_ID, commentId);
						map.put(TAG_PERSON_ID, personId);
						map.put(TAG_MESSAGE, message);
						map.put(TAG_FIRSTNAME, firstname);
						map.put(TAG_LASTNAME, lastname);
						map.put(TAG_COMMENTDATETIME, commentdatetime);

						commentList.add(map);
					}
				} else {

				}
			} catch (JSONException e) {
				System.out.println("Error in CommentData.doInBackground(String... arg0): " + e.getMessage());
				e.printStackTrace();
			}

			return null;
		}

		protected void onPostExecute(String file_url) {
			runOnUiThread(new Runnable() {
				public void run() {
					ListAdapter adapter = new SimpleAdapter(EventController.this, commentList, R.layout.comment_item,
									new String[] { TAG_MESSAGE, TAG_FIRSTNAME, TAG_LASTNAME, TAG_COMMENTDATETIME }, new int[] {
													R.id.MESSAGE, R.id.FIRSTNAME, R.id.LASTNAME, R.id.COMMENTDATETIME });
					// updating listview
					ListView commentList = (ListView) findViewById(android.R.id.list);
					commentList.setAdapter(adapter);
				}
			});
		}
	}
}