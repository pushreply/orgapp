package fhkl.de.orgapp.controller.profile;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import fhkl.de.orgapp.R;
import fhkl.de.orgapp.controller.event.OldEventController;
import fhkl.de.orgapp.util.IMessages;
import fhkl.de.orgapp.util.IUniformResourceLocator;
import fhkl.de.orgapp.util.JSONParser;
import fhkl.de.orgapp.util.MenuActivity;
import fhkl.de.orgapp.util.data.EventData;
import fhkl.de.orgapp.util.data.UserData;

/**
 * EventHistoryController - Handles the data for display the previous events the user joined
 * 
 * @author Jochen Jung, Oliver Neubauer
 * @version ?
 *
 */

public class EventHistoryController extends MenuActivity
{
	// For json issues
	private static final String TAG_SUCCESS = "success";
	private static final String TAG_EVENT_ID = "EVENT_ID";
	private static final String TAG_EVENT_NAME = "EVENT_NAME";
	private static final String TAG_EVENT_DATE = "EVENT_DATE";
	JSONParser jsonParser = new JSONParser();
	JSONArray events = null;
	JSONArray event = null;
	
	// Required variables for progress dialog, event history and text view
	private ProgressDialog pDialog;
	List<HashMap<String, String>> eventHistoryList;
	TextView tv_eventId;

	/**
	 * Initializes all necessary variables.
	 * Calls the required methods
	 * 
	 * @param savedInstanceState contains the data
	 */
	
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		// Set the layout
		setContentView(R.layout.event_history);
		
		// check for new notifications and signal the user
		checkOnNewNotificationsAndNotifyUser();

		eventHistoryList = new ArrayList<HashMap<String, String>>();

		// Object for getting the event history
		new EventHistoryGetter().execute();
	}

	/**
	 * EventHistoryGetter - Fetches the previous events of the user from the database
	 * 
	 * @author Oliver Neubauer
	 * @version ?
	 *
	 */
	
	class EventHistoryGetter extends AsyncTask<String, String, String>
	{
		/**
		 * Defines a progress dialog within the main thread
		 */
		
		@Override
		protected void onPreExecute()
		{
			super.onPreExecute();
			
			// Display a progress dialog
			pDialog = new ProgressDialog(EventHistoryController.this);

			pDialog.setMessage(IMessages.Status.LOADING_EVENT_HISTORY);

			pDialog.setIndeterminate(false);
			pDialog.setCancelable(true);
			pDialog.show();
		}

		/**
		 * Prepares and makes a http-request within the background thread to fetches the previous events.
		 * Put the events in a list
		 * 
		 * @param params the arguments as String array
		 */
		
		@Override
		protected String doInBackground(String... params)
		{
			// Required parameters for the request
			List<NameValuePair> requestParams = new ArrayList<NameValuePair>();
			requestParams.add(new BasicNameValuePair("do", "readOldEvents"));
			requestParams.add(new BasicNameValuePair("personId", UserData.getPERSONID()));

			// Make the request
			JSONObject json = jsonParser.makeHttpsRequest(IUniformResourceLocator.URL.URL_EVENT, "GET", requestParams, EventHistoryController.this);

			try
			{
				int success = json.getInt(TAG_SUCCESS);

				// In case of success
				if (success == 1)
				{
					// Fetch the previous events of the user
					events = json.getJSONArray("previousEvents");
					int e;
					JSONObject event;
					String id, name, date;
					HashMap<String, String> eventMap;

					// Put the previous events with necessary attributes in a list
					for (e = 0; e < events.length(); e++)
					{
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
				}
			}
			// Error case
			catch (Exception e)
			{
				e.printStackTrace();
				pDialog.dismiss();
				// Logout user
				logout();
			}

			return null;
		}

		/**
		 * Prepares the data for the display with help of an adapter
		 *
		 * @param result null or an error message
		 */
		
		@Override
		protected void onPostExecute(String result)
		{
			super.onPostExecute(result);

			// Hide the progress dialog
			pDialog.dismiss();

			// Prepare the data
			runOnUiThread(new Runnable() {
				
				// Put the data in an adapter
				public void run() {
					
					// Initialize the adapter and assign the fetched data to the layout fields
					ListAdapter adapter = new SimpleAdapter(EventHistoryController.this, eventHistoryList,
									R.layout.event_history_item,
									new String[] {TAG_EVENT_ID, TAG_EVENT_DATE, TAG_EVENT_NAME},
									new int[] {R.id.EVENTID, R.id.PREVIOUS_EVENT_DATE,  R.id.PREVIOUS_EVENT_NAME});

					// Update listview
					final ListView eventList = (ListView) findViewById(android.R.id.list);

					// Make the list clickable
					eventList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
						
						// Define the action in case of an item click
						@Override
						public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
							// Fetch the id of the selected event
							tv_eventId = (TextView) view.findViewById(R.id.EVENTID);
							// Object for getting details of the selected event
							new GetEvent().execute();

						}
					});
					
					// Set the adapter
					eventList.setAdapter(adapter);
				}
			});
		}
	}

	/**
	 * GetEvent - Fetches the details of the selected, previous event
	 * 
	 * @author Jochen Jung
	 * @version ?
	 *
	 */
	
	class GetEvent extends AsyncTask<String, String, String> {

		/**
		 * Defines a progress dialog within the main thread 
		 */
		
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			// Display a progress dialog
			pDialog = new ProgressDialog(EventHistoryController.this);
			pDialog.setMessage(IMessages.Status.LOADING_EVENT);
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(true);
			pDialog.show();
		}

		/**
		 * Prepares and makes a http-request within the background thread
		 * 
		 * @param args the arguments as String array
		 */
		
		protected String doInBackground(String... args) {
			// Required parameters for the request
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair("do", "readEvent"));
			params.add(new BasicNameValuePair("eventId", tv_eventId.getText().toString()));

			// Make the request
			JSONObject json = jsonParser.makeHttpsRequest(IUniformResourceLocator.URL.URL_EVENT, "GET", params, EventHistoryController.this);

			try
			{
				int success = json.getInt(TAG_SUCCESS);
				
				// In case of success
				if (success == 1)
				{
					// Fetch the event
					event = json.getJSONArray("event");

					// Set the event attributes
					for (int i = 0; i < event.length(); i++)
					{
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
			}
			// Error case
			catch (Exception e)
			{
				e.printStackTrace();
				pDialog.dismiss();
				// Logout user
				logout();
			}

			return null;
		}

		/**
		 * Calls the OldEventController
		 *
		 * @param result is null
		 */
		
		protected void onPostExecute(String result)
		{
			// Hide the progress dialog
			pDialog.dismiss();

			// Call OldEventController
			Intent intent = new Intent(EventHistoryController.this, OldEventController.class);
			startActivity(intent);
		}
	}
}