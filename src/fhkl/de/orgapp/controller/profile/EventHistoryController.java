package fhkl.de.orgapp.controller.profile;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import fhkl.de.orgapp.R;
import fhkl.de.orgapp.controller.event.OldEventController;
import fhkl.de.orgapp.util.IMessages;
import fhkl.de.orgapp.util.JSONParser;
import fhkl.de.orgapp.util.MenuActivity;
import fhkl.de.orgapp.util.data.EventData;

public class EventHistoryController extends MenuActivity {
	private static String URL_SELECT_EVENT_HISTORY = "http://pushrply.com/select_person_event_history.php";
	private static String url_get_event = "http://pushrply.com/get_event.php";

	private ProgressDialog pDialog;
	JSONParser jsonParser = new JSONParser();
	List<HashMap<String, String>> eventHistoryList;

	// JSON nodes
	private static final String TAG_SUCCESS = "success";
	private static final String TAG_EVENT_ID = "EVENT_ID";
	private static final String TAG_EVENT_NAME = "EVENT_NAME";
	private static final String TAG_EVENT_DATE = "EVENT_DATE";

	JSONArray events = null;
	JSONArray event = null;

	TextView tv_eventId;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.event_history);
		checkOnNewNotificationsAndNotifyUser();

		eventHistoryList = new ArrayList<HashMap<String, String>>();

		new EventHistoryGetter().execute();
	}

	class EventHistoryGetter extends AsyncTask<String, String, String> {
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pDialog = new ProgressDialog(EventHistoryController.this);

			pDialog.setMessage(IMessages.Status.LOADING_EVENT_HISTORY);

			pDialog.setIndeterminate(false);
			pDialog.setCancelable(true);
			pDialog.show();
		}

		@Override
		protected String doInBackground(String... params) {
			List<NameValuePair> requestParams = new ArrayList<NameValuePair>();
			requestParams.add(new BasicNameValuePair("personId", getIntent().getStringExtra("UserId")));

			JSONObject json = jsonParser.makeHttpRequest(URL_SELECT_EVENT_HISTORY, "GET", requestParams);

			try {
				int success = json.getInt(TAG_SUCCESS);

				if (success == 1) {
					events = json.getJSONArray("previousEvents");
					int e;
					JSONObject event;
					String id, name, date;
					HashMap<String, String> eventMap;

					for (e = 0; e < events.length(); e++) {
						event = events.getJSONObject(e);

						id = event.getString("eventId");
						name = event.getString("name");
						date = event.getString("eventDate");

						eventMap = new HashMap<String, String>();
						eventMap.put(TAG_EVENT_ID, id);
						eventMap.put(TAG_EVENT_NAME, name);
						eventMap.put(TAG_EVENT_DATE, date);

						eventHistoryList.add(eventMap);
					}
				} else {
					// TODO error message
				}
			} catch (JSONException e) {
				System.out.println("Error in EventHistoryGetter.doInBackground(String...): " + e.getMessage());
				e.printStackTrace();
			}

			return null;
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);

			pDialog.dismiss();

			runOnUiThread(new Runnable() {
				public void run() {
					ListAdapter adapter = new SimpleAdapter(EventHistoryController.this, eventHistoryList,
									R.layout.event_history_item, new String[] { TAG_EVENT_ID, TAG_EVENT_NAME, TAG_EVENT_DATE }, new int[] {
													R.id.EVENTID, R.id.PREVIOUS_EVENT_NAME, R.id.PREVIOUS_EVENT_DATE });

					// update listview
					final ListView eventList = (ListView) findViewById(android.R.id.list);

					eventList.setOnItemClickListener(new AdapterView.OnItemClickListener() {

						@Override
						public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
							tv_eventId = (TextView) view.findViewById(R.id.EVENTID);
							new GetEvent().execute();

						}
					});
					eventList.setAdapter(adapter);
				}
			});
		}
	}

	class GetEvent extends AsyncTask<String, String, String> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pDialog = new ProgressDialog(EventHistoryController.this);
			pDialog.setMessage(IMessages.Status.LOADING_EVENT);
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(true);
			pDialog.show();
		}

		protected String doInBackground(String... args) {
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair("eventId", tv_eventId.getText().toString()));
			JSONObject json = jsonParser.makeHttpRequest(url_get_event, "GET", params);

			Log.d("Event: ", json.toString());

			System.out.println("eventId: " + tv_eventId.getText().toString());

			try {
				int success = json.getInt(TAG_SUCCESS);
				if (success == 1) {
					event = json.getJSONArray("event");

					for (int i = 0; i < event.length(); i++) {
						JSONObject c = event.getJSONObject(i);

						EventData.setEVENTID(c.getString("eventId"));
						EventData.setEVENTDATE(c.getString("eventDate"));
						EventData.setEVENTTIME(c.getString("eventTime"));
						EventData.setEVENTLOCATION(c.getString("eventLocation"));
						EventData.setGROUPID(c.getString("groupId"));
						EventData.setNAME(c.getString("name"));
						EventData.setPERSONID(c.getString("personId"));
						EventData.setREGULARITY("regularity");
					}
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}

			return null;
		}

		protected void onPostExecute(String result) {
			pDialog.dismiss();

			Intent intent = new Intent(EventHistoryController.this, OldEventController.class);
			startActivity(intent);
		}
	}
}