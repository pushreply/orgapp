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
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import fhkl.de.orgapp.R;
import fhkl.de.orgapp.util.IMessages;
import fhkl.de.orgapp.util.JSONParser;
import fhkl.de.orgapp.util.MenuActivity;

public class EventHistoryController extends MenuActivity
{
	private static String URL_SELECT_EVENT_HISTORY = "http://pushrply.com/select_person_event_history.php";
	
	private ProgressDialog pDialog;
	JSONParser jsonParser = new JSONParser();
	List<HashMap<String, String>> eventHistoryList;
	
	// JSON nodes
	private static final String TAG_SUCCESS = "success";
	private static final String TAG_EVENT_NAME = "EVENT_NAME";
	private static final String TAG_EVENT_DATE = "EVENT_DATE";
	
	JSONArray events = null;
	
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.event_history);
		checkNewNotificationAndCreateIcon();
		
		eventHistoryList = new ArrayList<HashMap<String, String>>();
		
		new EventHistoryGetter().execute();
	}
	
	class EventHistoryGetter extends AsyncTask<String, String, String>
	{
		@Override
		protected void onPreExecute()
		{
			super.onPreExecute();
			pDialog = new ProgressDialog(EventHistoryController.this);
			
			pDialog.setMessage(IMessages.LOADING_EVENT_HISTORY);

			pDialog.setIndeterminate(false);
			pDialog.setCancelable(true);
			pDialog.show();
		}

		@Override
		protected String doInBackground(String... params)
		{
			List<NameValuePair> requestParams = new ArrayList<NameValuePair>();
			requestParams.add(new BasicNameValuePair("personId", getIntent().getStringExtra("UserId")));

			JSONObject json = jsonParser.makeHttpRequest(URL_SELECT_EVENT_HISTORY, "GET", requestParams);

			try
			{
				int success = json.getInt(TAG_SUCCESS);
				
				if (success == 1)
				{
					events = json.getJSONArray("previousEvents");
					int e;
					JSONObject event;
					String name, date;
					HashMap<String, String> eventMap;
					
					for (e = 0; e < events.length(); e++)
					{
						event = events.getJSONObject(e);

						name = event.getString("name");
						date = event.getString("eventDate");

						eventMap = new HashMap<String, String>();
						eventMap.put(TAG_EVENT_NAME, name);
						eventMap.put(TAG_EVENT_DATE, date);

						eventHistoryList.add(eventMap);
					}
				}
				else
				{
					//TODO error message
				}
			}
			catch (JSONException e)
			{
				System.out.println("Error in EventHistoryGetter.doInBackground(String...): " + e.getMessage());
				e.printStackTrace();
			}

			return null;
		}

		@Override
		protected void onPostExecute(String result)
		{
			super.onPostExecute(result);
			
			pDialog.dismiss();
			
			runOnUiThread(new Runnable()
			{
				public void run()
				{
					ListAdapter adapter = new SimpleAdapter
											(
											   EventHistoryController.this,
											   eventHistoryList,
											   R.layout.event_history_item,
											   new String[] {TAG_EVENT_NAME, TAG_EVENT_DATE},
											   new int[] {R.id.PREVIOUS_EVENT_NAME, R.id.PREVIOUS_EVENT_DATE}
											);

					// update listview
					final ListView eventList = (ListView) findViewById(android.R.id.list);

					eventList.setAdapter(adapter);
				}
			});
		}
	}
}