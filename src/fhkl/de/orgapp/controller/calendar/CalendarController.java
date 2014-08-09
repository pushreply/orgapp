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
import fhkl.de.orgapp.util.IMessages;
import fhkl.de.orgapp.util.IUniformResourceLocator;
import fhkl.de.orgapp.util.JSONParser;
import fhkl.de.orgapp.util.MenuActivity;
import fhkl.de.orgapp.util.data.EventData;
import fhkl.de.orgapp.util.data.EventSettingsData;
import fhkl.de.orgapp.util.data.GroupData;
import fhkl.de.orgapp.util.data.UserData;

/**
 * CalendarController - handles personal list of events on that user is going to
 * attend. This is what user sees after a successful login has been made.
 * 
 * @author Ronaldo Hasiholan, Oliver Neubauer, Jochen Jung
 * @version 3.7
 */

public class CalendarController extends MenuActivity {

	// Android progress dialog.
	private ProgressDialog pDialog;

	// A json parser and a container for the list of events.
	JSONParser jsonParser = new JSONParser();
	ArrayList<HashMap<String, String>> eventList;

	TextView tv_eventId;
	private static final String TAG_SUCCESS = "success";

	// All elements needed in a single event item of the list.
	private static final String TAG_EVENTID = "EVENTID";
	private static final String TAG_EVENTDATE = "EVENTDATE";
	private static final String TAG_EVENTTIME = "EVENTTIME";
	private static final String TAG_EVENT = "EVENT";

	// JSON array for calendar, event, group and member
	JSONArray calendar = null;
	JSONArray event = null;
	JSONArray group = null;
	JSONArray member = null;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.calendar);

		// Check any notification and inform the user.
		checkOnNewNotificationsAndNotifyUser();
		eventList = new ArrayList<HashMap<String, String>>();

		// Start the calendar
		new Calendar().execute();
	}

	// Each time the back button is pressed from this standpoint (calendar
	// activity),
	// logged the user out.
	@Override
	public void onBackPressed() {
		super.onBackPressed();
		logout();
	}

	/**
	 * Begin the background operation using asynchronous task to get data through
	 * the network. The extended AsyncTask, Calendar class creates a comment in an
	 * event by requesting the commentId using the HTTPS request. The string
	 * "do=readUserEvents" is being used to execute the corresponding PHP file on
	 * the server side. It handles the request, return the result as a list and a
	 * success marker to the client app.
	 * 
	 * 
	 */
	class Calendar extends AsyncTask<String, String, String> {
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pDialog = new ProgressDialog(CalendarController.this);

			// Clicking on the refresh button will inform the user
			// that the list is updating and will be loaded shortly.
			if (getIntent().getStringExtra("Refresh") != null)
				pDialog.setMessage(IMessages.Status.UPDATING);
			else
				pDialog.setMessage(IMessages.Status.LOADING_CALENDAR);

			pDialog.setIndeterminate(false);

			// If the progress is taking too long, let user cancels
			// the progress dialog by hitting the android-back-button.
			pDialog.setCancelable(true);
			pDialog.show();
		}

		protected String doInBackground(String... args) {
			List<NameValuePair> params = new ArrayList<NameValuePair>();

			// Prepare the parameter for the HTTP GET request using the value
			// of 'personId' from the temporary storage 'UserData class',
			// which has been set after the user successfully logged in from
			// LoginController.
			params.add(new BasicNameValuePair("personId", UserData.getPERSONID()));

			// If any of the user event is available, get the quantity of the events,
			// append it and the operation marker "do=readUserEvents"
			// and this class as the context through the HTTPS GET parameter.
			if (!EventSettingsData.getSHOWN_EVENT_ENTRIES().equals(""))
				params.add(new BasicNameValuePair("shownEventEntries", EventSettingsData.getSHOWN_EVENT_ENTRIES()));
			params.add(new BasicNameValuePair("do", "readUserEvents"));
			JSONObject json = jsonParser.makeHttpsRequest(IUniformResourceLocator.URL.URL_EVENT, "GET", params,
							CalendarController.this);

			// Get the result from server using JSON and map the respective key-value.
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

						time = time.substring(0, 5);

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
				// Dismiss the progress dialog before logout.
				pDialog.dismiss();
				logout();
			}

			return null;
		}

		protected void onPostExecute(String file_url) {
			pDialog.dismiss();
			runOnUiThread(new Runnable() {
				public void run() {
					// An adapter to hold information in a single event item.
					ListAdapter adapter = new SimpleAdapter(CalendarController.this, eventList, R.layout.calendar_item,
									new String[] { TAG_EVENTID, TAG_EVENTDATE, TAG_EVENTTIME, TAG_EVENT }, new int[] { R.id.EVENTID,
													R.id.EVENTDATE, R.id.EVENTTIME, R.id.EVENT });

					// Update the listview
					ListView calenderList = (ListView) findViewById(android.R.id.list);

					// Make the list clickable and view the selected event detail
					// information by executing GetEvent().
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

	/**
	 * Begin the background operation using asynchronous task to get event data
	 * through the network. The extended AsyncTask, GetEvent class creates a
	 * comment in an event by requesting the eventId using the HTTPS request. The
	 * string "do=readEvent" is being used to execute the corresponding PHP file
	 * on the server side. It handles the request, return the result as a list and
	 * a success marker to the client app, and save them into the class EventData
	 * and GroupData to be used as long as the user logged in.
	 * 
	 */

	class GetEvent extends AsyncTask<String, String, String> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();

			// Start a progress dialog
			pDialog = new ProgressDialog(CalendarController.this);
			pDialog.setMessage(IMessages.Status.LOADING_EVENT);
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(true);
			pDialog.show();
		}

		protected String doInBackground(String... args) {

			// Prepare HTTPS request for event details using previously selected
			// eventID.
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair("do", "readEvent"));
			params.add(new BasicNameValuePair("eventId", tv_eventId.getText().toString()));
			JSONObject json = jsonParser.makeHttpsRequest(IUniformResourceLocator.URL.URL_EVENT, "GET", params,
							CalendarController.this);

			Log.d("Event: ", json.toString());

			try {
				int success = json.getInt(TAG_SUCCESS);
				if (success == 1) {
					event = json.getJSONArray("event");

					for (int i = 0; i < event.length(); i++) {
						JSONObject c = event.getJSONObject(i);

						// Save the event details to the EventData class
						EventData.setEVENTID(c.getString("eventId"));
						EventData.setEVENTDATE(c.getString("eventDate"));
						EventData.setEVENTTIME(c.getString("eventTime"));
						EventData.setEVENTLOCATION(c.getString("eventLocation"));
						EventData.setGROUPID(c.getString("groupId"));
						EventData.setNAME(c.getString("name"));
						EventData.setPERSONID(c.getString("personId"));
						EventData.setREGULARITY("regularity");
					}

					// Prepare HTTPS Request for the group and corresponding member.
					List<NameValuePair> paramsGetGroup = new ArrayList<NameValuePair>();
					paramsGetGroup.add(new BasicNameValuePair("do", "readGroup"));
					paramsGetGroup.add(new BasicNameValuePair("groupId", EventData.getGROUPID()));
					json = jsonParser.makeHttpsRequest(IUniformResourceLocator.URL.URL_GROUPS, "GET", paramsGetGroup,
									CalendarController.this);

					success = json.getInt(TAG_SUCCESS);
					if (success == 1) {
						group = json.getJSONArray("groups");
						for (int i = 0; i < group.length(); i++) {
							JSONObject c = group.getJSONObject(i);

							// Save the value to the GroupData class.
							GroupData.setGROUPID(c.getString("groupId"));
							GroupData.setPERSONID(c.getString("personId"));
							GroupData.setGROUPNAME(c.getString("name"));
							GroupData.setGROUPINFO(c.getString("info"));
						}
					}

					// Prepare HTTPS Request for list of member in a specific group.
					List<NameValuePair> paramsGetMember = new ArrayList<NameValuePair>();
					paramsGetMember.add(new BasicNameValuePair("do", "readUserInGroup"));
					paramsGetMember.add(new BasicNameValuePair("groupId", GroupData.getGROUPID()));
					paramsGetMember.add(new BasicNameValuePair("personId", UserData.getPERSONID()));
					json = jsonParser.makeHttpsRequest(IUniformResourceLocator.URL.URL_GROUPS, "GET", paramsGetMember,
									CalendarController.this);

					Log.d("Member: ", json.toString());

					success = json.getInt(TAG_SUCCESS);
					if (success == 1) {
						member = json.getJSONArray("member");

						for (int i = 0; i < member.length(); i++) {
							JSONObject c = member.getJSONObject(i);

							// Save the value of member privileges.
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
				pDialog.dismiss();
				logout();
			}

			return null;
		}

		protected void onPostExecute(String result) {
			pDialog.dismiss();

			// Send the user to the eventcontroller after selecting an event from the
			// list.
			Intent intent = new Intent(CalendarController.this, EventController.class);
			startActivity(intent);
		}
	}
}