package fhkl.de.orgapp.controller.calendar;

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
import fhkl.de.orgapp.util.NewNotifications;
import fhkl.de.orgapp.util.UserData;
import fhkl.de.orgapp.util.validator.OutputValidator;

public class CalendarController extends MenuActivity {
	private ProgressDialog pDialog;

	JSONParser jsonParser = new JSONParser();
	ArrayList<HashMap<String, String>> eventList;

	private static String url_get_calendar = "http://pushrply.com/get_person_events.php";
	private static String url_get_event = "http://pushrply.com/get_event.php";
	private static String url_get_group = "http://pushrply.com/get_group.php";
	private static String URL_GET_USER_IN_GROUP = "http://pushrply.com/get_user_in_group_by_eMail.php";

	private static int START_ACTIVITY_COUNTER = 0;

	TextView tv_eventId;
	private static final String TAG_SUCCESS = "success";
	private static final String TAG_EVENTID = "EVENTID";
	private static final String TAG_EVENTDATE = "EVENTDATE";
	private static final String TAG_EVENTTIME = "EVENTTIME";
	private static final String TAG_EVENT = "EVENT";

	JSONArray calendar = null;
	JSONArray event = null;
	JSONArray group = null;
	JSONArray member = null;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.calendar);
		
		START_ACTIVITY_COUNTER++;

		// set user data after login
		if (START_ACTIVITY_COUNTER == 1) {
			UserData.setPERSONID(getIntent().getStringExtra("UserId"));
			UserData.setFIRST_NAME(getIntent().getStringExtra("UserFirstName"));
			UserData.setLAST_NAME(getIntent().getStringExtra("UserLastName"));
			
			if(OutputValidator.isUserBirthdaySet(getIntent().getStringExtra("UserBirthday")))
				UserData.setBIRTHDAY(getIntent().getStringExtra("UserBirthday"));
			else
				UserData.setBIRTHDAY("");
			
			UserData.setGENDER(getIntent().getStringExtra("UserGender"));
			UserData.setEMAIL(getIntent().getStringExtra("UserEmail"));
			UserData.setMEMBER_SINCE(getIntent().getStringExtra("UserMemberSince"));
		}
					
		checkNewNotificationAndCreateIcon();

		eventList = new ArrayList<HashMap<String, String>>();
		new Calendar().execute();
	}
	
	@Override
	public void onBackPressed()
	{
		super.onBackPressed();
		logout();
	}

	public static void resetSTART_ACTIVITY_COUNTER() {
		START_ACTIVITY_COUNTER = 0;
	}

	class Calendar extends AsyncTask<String, String, String> {
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pDialog = new ProgressDialog(CalendarController.this);

			if (getIntent().getStringExtra("Refresh") != null)
				pDialog.setMessage(IMessages.UPDATING);
			else
				pDialog.setMessage(IMessages.LOADING_CALENDAR);

			pDialog.setIndeterminate(false);
			pDialog.setCancelable(true);
			pDialog.show();
		}

		protected String doInBackground(String... args) {
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair("personId", UserData.getPERSONID()));
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
					ListAdapter adapter = new SimpleAdapter(CalendarController.this, eventList, R.layout.calendar_item,
									new String[] { TAG_EVENTID, TAG_EVENTDATE, TAG_EVENTTIME, TAG_EVENT }, new int[] { R.id.EVENTID,
													R.id.EVENTDATE, R.id.EVENTTIME, R.id.EVENT });
					// updating listview
					ListView calenderList = (ListView) findViewById(android.R.id.list);

					calenderList.setOnItemClickListener(new AdapterView.OnItemClickListener() {

						@Override
						public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
							tv_eventId = (TextView) view.findViewById(R.id.EVENTID);
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
			pDialog = new ProgressDialog(CalendarController.this);
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

					List<NameValuePair> paramsGetGroup = new ArrayList<NameValuePair>();
					paramsGetGroup.add(new BasicNameValuePair("groupId", EventData.getGROUPID()));
					json = jsonParser.makeHttpRequest(url_get_group, "GET", paramsGetGroup);

					success = json.getInt(TAG_SUCCESS);
					if (success == 1) {
						group = json.getJSONArray("groups");
						for (int i = 0; i < group.length(); i++) {
							JSONObject c = group.getJSONObject(i);

							GroupData.setGROUPID(c.getString("groupId"));
							GroupData.setPERSONID(c.getString("personId"));
							GroupData.setGROUPNAME(c.getString("name"));
							GroupData.setGROUPINFO(c.getString("info"));
							GroupData.setPICTURE(c.getString("picture"));
						}
					}

					List<NameValuePair> paramsGetMember = new ArrayList<NameValuePair>();
					paramsGetMember.add(new BasicNameValuePair("groupId", GroupData.getGROUPID()));
					paramsGetMember.add(new BasicNameValuePair("eMail", UserData.getEMAIL()));
					json = jsonParser.makeHttpRequest(URL_GET_USER_IN_GROUP, "GET", paramsGetMember);

					Log.d("Member: ", json.toString());

					success = json.getInt(TAG_SUCCESS);
					if (success == 1) {
						member = json.getJSONArray("member");

						for (int i = 0; i < member.length(); i++) {
							JSONObject c = member.getJSONObject(i);

							GroupData.setPRIVILEGE_MANAGEMENT(c.getString("privilegeManagement"));
							GroupData.setPRIVILEGE_INVITE_MEMBER(c.getString("memberInvitation"));
							GroupData.setPRIVILEGE_EDIT_MEMBERLIST(c.getString("memberlistEditing"));
							GroupData.setPRIVILEGE_CREATE_EVENT(c.getString("eventCreating"));
							GroupData.setPRIVILEGE_EDIT_EVENT(c.getString("eventEditing"));
							GroupData.setPRIVILEGE_DELETE_EVENT(c.getString("eventDeleting"));
							GroupData.setPRIVILEGE_EDIT_COMMENT(c.getString("commentEditing"));
							GroupData.setPRIVILEGE_DELETE_COMMENT(c.getString("commentDeleting"));
						}
					}
				} else {

				}
			} catch (JSONException e) {
				e.printStackTrace();
			}

			return null;
		}

		protected void onPostExecute(String result) {
			pDialog.dismiss();

			finish();
			Intent intent = new Intent(CalendarController.this, EventController.class);
			startActivity(intent);
		}
	}
}