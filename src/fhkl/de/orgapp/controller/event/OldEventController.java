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
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.ToggleButton;
import fhkl.de.orgapp.R;
import fhkl.de.orgapp.util.IMessages;
import fhkl.de.orgapp.util.JSONParser;
import fhkl.de.orgapp.util.MenuActivity;
import fhkl.de.orgapp.util.data.CommentData;
import fhkl.de.orgapp.util.data.EventData;

public class OldEventController extends MenuActivity {

	private ProgressDialog pDialog;
	JSONParser jsonParser = new JSONParser();

	private static String URL_COMMENTCONTROL = "http://pushrply.com/pdo_commentcontrol.php";

	private static final String TAG_SUCCESS = "success";

	String message, changedMessage, commentId;

	TextView eventTime;
	TextView eventDate;
	TextView eventLocation;

	ToggleButton buttonAttendance;
	boolean toggleButtonChecked;

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
	JSONArray member = null;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.old_event);
		checkOnNewNotificationsAndNotifyUser();
		this.setTitle(EventData.getNAME());
		commentList = new ArrayList<HashMap<String, String>>();

		eventTime = (TextView) findViewById(R.id.EVENTTIME);
		eventDate = (TextView) findViewById(R.id.EVENTDATE);
		eventLocation = (TextView) findViewById(R.id.EVENTLOCATION);

		eventTime.setText("Time: " + EventData.getEVENTTIME());
		eventDate.setText("Date: " + EventData.getEVENTDATE());
		eventLocation.setText("Location: " + EventData.getEVENTLOCATION());

		new ShowComments().execute();
	}

	class ShowComments extends AsyncTask<String, String, String> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pDialog = new ProgressDialog(OldEventController.this);
			pDialog.setMessage(IMessages.Status.LOADING_EVENT);
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(true);
			pDialog.show();
		}

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
			pDialog.dismiss();
			runOnUiThread(new Runnable() {
				public void run() {
					ListAdapter adapter = new SimpleAdapter(OldEventController.this, commentList, R.layout.comment_item,
									new String[] { TAG_COMMENT_ID, TAG_MESSAGE, TAG_FIRSTNAME, TAG_LASTNAME, TAG_COMMENTDATETIME },
									new int[] { R.id.COMMENTID, R.id.MESSAGE, R.id.FIRSTNAME, R.id.LASTNAME, R.id.COMMENTDATETIME });

					ListView commentList = (ListView) findViewById(android.R.id.list);
					commentList.setAdapter(adapter);
				}
			});
		}
	}
}
