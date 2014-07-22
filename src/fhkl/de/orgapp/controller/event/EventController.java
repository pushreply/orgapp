package fhkl.de.orgapp.controller.event;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import android.widget.ToggleButton;
import fhkl.de.orgapp.R;
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
}