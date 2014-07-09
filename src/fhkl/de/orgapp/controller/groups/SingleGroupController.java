package fhkl.de.orgapp.controller.groups;

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
import fhkl.de.orgapp.controller.event.EventController;
import fhkl.de.orgapp.util.EventData;
import fhkl.de.orgapp.util.GroupData;
import fhkl.de.orgapp.util.IMessages;
import fhkl.de.orgapp.util.JSONParser;
import fhkl.de.orgapp.util.MenuActivity;

public class SingleGroupController extends MenuActivity {

	private ProgressDialog pDialog;

	JSONParser jsonParser = new JSONParser();
	ArrayList<HashMap<String, String>> eventList;

	private static String url_get_calendar = "http://pushrply.com/get_group_events.php";
	private static String url_get_event = "http://pushrply.com/get_event.php";

	private static final String TAG_SUCCESS = "success";
	private static final String TAG_EVENTID = "SINGLEGROUP_EVENTID";
	private static final String TAG_EVENTDATE = "SINGLEGROUP_EVENTDATE";
	private static final String TAG_EVENTTIME = "SINGLEGROUP_EVENTTIME";
	private static final String TAG_EVENT = "SINGLEGROUP_EVENTNAME";

	JSONArray calendar = null;
	JSONArray event = null;

	TextView tv_eventId, groupInfo;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.singlegroup);
		this.setTitle(GroupData.getGROUPNAME());

		EventData.setBACK(false);

		groupInfo = (TextView) findViewById(R.id.GROUP_INFO);
		groupInfo.setText(GroupData.getGROUPINFO());

		eventList = new ArrayList<HashMap<String, String>>();
		new GetGroupCalendar().execute();
	}

	class GetGroupCalendar extends AsyncTask<String, String, String> {
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pDialog = new ProgressDialog(SingleGroupController.this);
			pDialog.setMessage(IMessages.LOADING_CALENDAR);
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(true);
			pDialog.show();
		}

		protected String doInBackground(String... args) {
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair("groupId", GroupData.getGROUPID()));
			JSONObject json = jsonParser.makeHttpRequest(url_get_calendar, "GET", params);

			Log.d("Calendar: ", json.toString());

			try {
				int success = json.getInt(TAG_SUCCESS);
				if (success == 1) {
					calendar = json.getJSONArray("event");

					for (int i = 0; i < calendar.length(); i++) {
						JSONObject c = calendar.getJSONObject(i);

						String eventId = c.getString("eventId");
						String event = c.getString("name");
						String date = c.getString("eventDate");
						String time = c.getString("eventTime");

						HashMap<String, String> map = new HashMap<String, String>();
						map.put(TAG_EVENTID, eventId);
						map.put(TAG_EVENTDATE, date);
						map.put(TAG_EVENTTIME, time);
						map.put(TAG_EVENT, event);

						eventList.add(map);
					}
				} else {

				}
			} catch (JSONException e) {
				e.printStackTrace();
			}

			return null;
		}

		protected void onPostExecute(String file_url) {
			pDialog.dismiss();
			runOnUiThread(new Runnable() {
				public void run() {
					ListAdapter adapter = new SimpleAdapter(SingleGroupController.this, eventList, R.layout.singlegroup_item,
									new String[] { TAG_EVENTID, TAG_EVENTDATE, TAG_EVENTTIME, TAG_EVENT }, new int[] {
													R.id.SINGLEGROUP_EVENTID, R.id.SINGLEGROUP_EVENTDATE, R.id.SINGLEGROUP_EVENTTIME,
													R.id.SINGLEGROUP_EVENTNAME });
					ListView calenderList = (ListView) findViewById(android.R.id.list);

					calenderList.setOnItemClickListener(new AdapterView.OnItemClickListener() {

						@Override
						public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

							tv_eventId = (TextView) view.findViewById(R.id.SINGLEGROUP_EVENTID);
							new GetEvent().execute();
						}
					});
					calenderList.setAdapter(adapter);
				}
			});
		}
	}

	class GetEvent extends AsyncTask<String, String, String> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pDialog = new ProgressDialog(SingleGroupController.this);
			pDialog.setMessage(IMessages.LOADING_EVENT);
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

			finish();
			Intent intent = new Intent(SingleGroupController.this, EventController.class);
			startActivity(intent);
		}
	}

}
