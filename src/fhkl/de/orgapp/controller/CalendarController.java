package fhkl.de.orgapp.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.NameValuePair;
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

import com.example.orgapp.R;

import fhkl.de.orgapp.util.IMessages;
import fhkl.de.orgapp.util.JSONParser;
import fhkl.de.orgapp.util.MenuActivity;

public class CalendarController extends MenuActivity
{
	private ProgressDialog pDialog;

	JSONParser jsonParser = new JSONParser();
	ArrayList<HashMap<String, String>> eventList;

	private static String url_get_calendar = "http://pushrply.com/get_events.php";

	private static final String TAG_SUCCESS = "success";
	private static final String TAG_EVENTID = "EVENTID";
	private static final String TAG_EVENTDATE = "EVENTDATE";
	private static final String TAG_EVENTTIME = "EVENTTIME";
	private static final String TAG_EVENT = "EVENT";

	JSONArray calendar = null;

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.calendar);
		
		eventList = new ArrayList<HashMap<String, String>>();
		new Calendar().execute();

	}

	class Calendar extends AsyncTask<String, String, String>
	{
		@Override
		protected void onPreExecute()
		{
			super.onPreExecute();
			pDialog = new ProgressDialog(CalendarController.this);
			pDialog.setMessage(IMessages.LOADING_CALENDAR);
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(false);
			pDialog.show();
		}

		protected String doInBackground(String... args)
		{
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			JSONObject json = jsonParser.makeHttpRequest(url_get_calendar,
					"GET", params);

			Log.d("Calendar: ", json.toString());

			try
			{
				int success = json.getInt(TAG_SUCCESS);
				if (success == 1)
				{
					calendar = json.getJSONArray("event");

					for (int i = 0; i < calendar.length(); i++)
					{
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
				}
				else
				{

				}
			}
			catch (JSONException e)
			{
				e.printStackTrace();
			}

			return null;
		}

		protected void onPostExecute(String file_url)
		{
			pDialog.dismiss();
			runOnUiThread(new Runnable()
			{
				public void run()
				{
					ListAdapter adapter = new SimpleAdapter(
							CalendarController.this, eventList,
							R.layout.calendar_item, new String[] { TAG_EVENTID,
									TAG_EVENTDATE, TAG_EVENTTIME, TAG_EVENT },
							new int[] { R.id.EVENTID, R.id.EVENTDATE,
									R.id.EVENTTIME, R.id.EVENT });
					// updating listview
					ListView calenderList = (ListView) findViewById(android.R.id.list);
					calenderList.setAdapter(adapter);
				}
			});
		}
	}
}