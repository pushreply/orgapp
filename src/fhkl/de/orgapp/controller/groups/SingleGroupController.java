package fhkl.de.orgapp.controller.groups;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import fhkl.de.orgapp.R;
import fhkl.de.orgapp.controller.event.EventController;
import fhkl.de.orgapp.util.IMessages;
import fhkl.de.orgapp.util.IUniformResourceLocator;
import fhkl.de.orgapp.util.JSONParser;
import fhkl.de.orgapp.util.MenuActivity;
import fhkl.de.orgapp.util.check.UserJoinEventChecker;
import fhkl.de.orgapp.util.data.EventData;
import fhkl.de.orgapp.util.data.EventSettingsData;
import fhkl.de.orgapp.util.data.GroupData;
import fhkl.de.orgapp.util.data.ListModel;
import fhkl.de.orgapp.util.data.UserData;

/**
 * SingleGroupController - Handles the single group activity.
 * 
 * Displays group information and group events. Quick sign-in/sign-off via click
 * on icon.
 * 
 * @author Jochen Jung
 * @version 3.9
 */
public class SingleGroupController extends MenuActivity {
	private ProgressDialog pDialog;

	JSONParser jsonParser = new JSONParser();

	public ArrayList<ListModel> customAdapterValues = new ArrayList<ListModel>();

	private static final String TAG_SUCCESS = "success";

	String eventId = null;
	JSONArray calendar = null;
	JSONArray event = null;

	Boolean toggleButtonChecked;
	TextView tv_eventId, tvGroupInfo, groupInfo;
	LinearLayout hr;

	/**
	 * Initializes the view. Calls async class that loads the group events in a
	 * ListView.
	 * 
	 * @param savedInstanceState Bundle
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.singlegroup);
		checkOnNewNotificationsAndNotifyUser();
		this.setTitle(GroupData.getGROUPNAME());

		EventData.setBACK(false);

		tvGroupInfo = (TextView) findViewById(R.id.TV_GROUP_INFO);
		groupInfo = (TextView) findViewById(R.id.GROUP_INFO);

		// hr = (LinearLayout) findViewById(R.id.HR);
		new GetGroupCalendar().execute();
	}

	/**
	 * Async class that loads the group events in a ListView
	 * 
	 */
	class GetGroupCalendar extends AsyncTask<String, String, String> {

		/**
		 * Creates ProgressDialog
		 */
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pDialog = new ProgressDialog(SingleGroupController.this);
			pDialog.setMessage(IMessages.Status.LOADING_CALENDAR);
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(true);
			pDialog.show();
		}

		/**
		 * 
		 * 
		 * @param args String...
		 * @return String result
		 */
		protected String doInBackground(String... args) {
			List<NameValuePair> params = new ArrayList<NameValuePair>();

			// Required params
			params.add(new BasicNameValuePair("do", "readGroupEvents"));
			params.add(new BasicNameValuePair("groupId", GroupData.getGROUPID()));

			// Show limited events. Adjustable in event settings
			if (!EventSettingsData.getSHOWN_EVENT_ENTRIES().equals(""))
				params.add(new BasicNameValuePair("shownEventEntries", EventSettingsData.getSHOWN_EVENT_ENTRIES()));

			// Fetch the events of a selected group
			JSONObject json = jsonParser.makeHttpsRequest(IUniformResourceLocator.URL.URL_EVENT, "GET", params,
							SingleGroupController.this);

			try {
				int success = json.getInt(TAG_SUCCESS);
				if (success == 1) {
					calendar = json.getJSONArray("event");

					for (int i = 0; i < calendar.length(); i++) {
						JSONObject c = calendar.getJSONObject(i);

						String eventId = c.getString("eventId");
						String eventName = c.getString("name");
						String eventDate = c.getString("eventDate");
						String eventTime = c.getString("eventTime");

						eventTime = eventTime.substring(0, 5);
						final ListModel listModel = new ListModel();

						// Save values in custom model
						listModel.setEventId(eventId);
						listModel.setEventName(eventName);
						listModel.setEventDate(eventDate);
						listModel.setEventTime(eventTime);

						// Check if member is signed on/off event
						UserJoinEventChecker joinChecker = new UserJoinEventChecker();

						if (joinChecker.isMemberJoinedEvent(eventId)) {
							listModel.setAttending(R.drawable.ic_action_good);
						} else {
							listModel.setAttending(R.drawable.ic_action_bad);
						}
						// Save model to custom adapter
						customAdapterValues.add(listModel);

					}
				}
			} catch (Exception e) {
				e.printStackTrace();
				pDialog.dismiss();
				logout();
			}

			return null;
		}

		/**
		 * Removes ProgressDialog. Initializes and loads ListView. Loads group
		 * information and separator.
		 */
		protected void onPostExecute(String file_url) {
			pDialog.dismiss();
			runOnUiThread(new Runnable() {
				public void run() {

					CustomAdapter adapter = new CustomAdapter(SingleGroupController.this, customAdapterValues, getResources());

					ListView calenderList = (ListView) findViewById(android.R.id.list);
					calenderList.setAdapter(adapter);
					tvGroupInfo.setVisibility(View.VISIBLE);
					groupInfo.setText(GroupData.getGROUPINFO());
					//hr.setVisibility(View.VISIBLE);
				}
			});
		}
	}

	/**
	 * Async class that saves event values in EventData.
	 * 
	 */
	class GetEvent extends AsyncTask<String, String, String> {

		/**
		 * Creates ProgressDialog.
		 */
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pDialog = new ProgressDialog(SingleGroupController.this);
			pDialog.setMessage(IMessages.Status.LOADING_EVENT);
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(true);
			pDialog.show();
		}

		/**
		 * Saves event values in EventData.
		 * 
		 * @param args String...
		 * @return String result
		 */
		protected String doInBackground(String... args) {
			List<NameValuePair> params = new ArrayList<NameValuePair>();

			// Required parameters
			params.add(new BasicNameValuePair("do", "readEvent"));
			params.add(new BasicNameValuePair("eventId", eventId));

			// Fetch the selected event
			JSONObject json = jsonParser.makeHttpsRequest(IUniformResourceLocator.URL.URL_EVENT, "GET", params,
							SingleGroupController.this);

			try {
				int success = json.getInt(TAG_SUCCESS);
				if (success == 1) {
					event = json.getJSONArray("event");

					for (int i = 0; i < event.length(); i++) {
						JSONObject c = event.getJSONObject(i);

						// Save event values
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
			} catch (Exception e) {
				e.printStackTrace();
				pDialog.dismiss();
				logout();
			}

			return null;
		}

		/**
		 * Removes ProgressDialog. Starts new activity.
		 */
		protected void onPostExecute(String result) {
			pDialog.dismiss();

			Intent intent = new Intent(SingleGroupController.this, EventController.class);
			startActivity(intent);
		}
	}

	/**
	 * Custom adapter for four TextViews and one ImageView
	 * 
	 */
	private class CustomAdapter extends BaseAdapter {

		private ArrayList<ListModel> data;
		@SuppressWarnings("unused")
		private Resources res;
		@SuppressWarnings("unused")
		private final Activity context;
		private LayoutInflater inflater = null;

		/**
		 * Initializes Customadapter.
		 * 
		 * @param context
		 * @param data
		 * @param res
		 */
		public CustomAdapter(Activity context, ArrayList<ListModel> data, Resources res) {

			this.context = context;
			this.data = data;
			this.res = res;

			inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		}

		/**
		 * ViewHolder that temporary holds view values.
		 * 
		 */
		public class ViewHolder {

			public TextView eventId;
			public TextView eventName;
			public TextView eventDate;
			public TextView eventTime;
			public ImageView attending;
		}

		/**
		 * Sets values. Registers onClickListener.
		 * 
		 * @param position int
		 * @param view View
		 * @param parent ViewGroup
		 * @return View view
		 */
		@SuppressLint({ "InflateParams", "ViewHolder" })
		@Override
		public View getView(int position, View view, ViewGroup parent) {
			ViewHolder holder = new ViewHolder();

			view = inflater.inflate(R.layout.singlegroup_item, null);

			holder.eventId = (TextView) view.findViewById(R.id.SINGLEGROUP_EVENTID);
			holder.eventName = (TextView) view.findViewById(R.id.SINGLEGROUP_EVENTNAME);
			holder.eventDate = (TextView) view.findViewById(R.id.SINGLEGROUP_EVENTDATE);
			holder.eventTime = (TextView) view.findViewById(R.id.SINGLEGROUP_EVENTTIME);
			holder.attending = (ImageView) view.findViewById(R.id.SINGLEGROUP_ATTENDING);

			view.setTag(holder);

			if (data.size() > 0) {

				ListModel tempValues = null;
				tempValues = (ListModel) data.get(position);

				holder.eventId.setText(tempValues.getEventId());
				holder.eventName.setText(tempValues.getEventName());
				holder.eventName.setTag(tempValues.getEventId());

				holder.eventDate.setText(tempValues.getEventDate());
				holder.eventDate.setTag(tempValues.getEventId());

				holder.eventTime.setText(tempValues.getEventTime());
				holder.eventTime.setTag(tempValues.getEventId());

				holder.attending.setImageResource(tempValues.getAttending());
				String[] tagAttending = new String[2];
				tagAttending[0] = tempValues.getEventId();
				tagAttending[1] = tempValues.getAttending().toString();
				holder.attending.setTag(tagAttending);

				// Set onClickListener
				holder.eventName.setOnClickListener(onEventListener);
				holder.eventDate.setOnClickListener(onEventListener);
				holder.eventTime.setOnClickListener(onEventListener);
				holder.attending.setOnClickListener(onAttendingListener);
			}
			return view;
		}

		// Open the selected event in new activity
		private OnClickListener onEventListener = new OnClickListener() {

			@Override
			public void onClick(View view) {

				eventId = view.getTag().toString();
				new GetEvent().execute();
			}
		};

		// Change attending status in current activity
		private OnClickListener onAttendingListener = new OnClickListener() {

			@Override
			public void onClick(View view) {

				ImageView iv = (ImageView) view.findViewById(R.id.SINGLEGROUP_ATTENDING);

				String[] tag = (String[]) iv.getTag();
				eventId = tag[0];

				Integer ivId = Integer.valueOf(tag[1]);
				if (ivId.equals(R.drawable.ic_action_good)) {
					toggleButtonChecked = true;
				} else {
					toggleButtonChecked = false;
				}

				new ChangeAttendingStatus().execute();
			}
		};

		/**
		 * Gets the data count.
		 * 
		 * @return int Count
		 */
		@Override
		public int getCount() {
			if (data.size() <= 0)
				return 1;
			return data.size();
		}

		/**
		 * Gets the data at given position
		 * 
		 * @param position int
		 * @return Object item
		 */
		@Override
		public Object getItem(int position) {
			return data.get(position);
		}

		/**
		 * Get the itemId at given position
		 * 
		 * @param position int
		 * @return long itemId
		 */
		@Override
		public long getItemId(int position) {
			return position;
		}
	}

	/**
	 * Async class that changes attending status depending on current status.
	 * 
	 */
	class ChangeAttendingStatus extends AsyncTask<String, String, String> {

		/**
		 * Creates ProgressDialog.
		 */
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pDialog = new ProgressDialog(SingleGroupController.this);
			pDialog.setMessage(IMessages.Status.CHANGING_STATUS);
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(true);
			pDialog.show();
		}

		/**
		 * Changes attending status depending on current status.
		 * 
		 * @param args String...
		 * @return String result
		 */
		protected String doInBackground(String... args) {
			List<NameValuePair> params = new ArrayList<NameValuePair>();

			// Required parameters
			params.add(new BasicNameValuePair("personId", UserData.getPERSONID()));
			params.add(new BasicNameValuePair("eventId", eventId));

			JSONObject json;

			// Going to event -> Not going to event
			if (toggleButtonChecked)
				params.add(new BasicNameValuePair("do", "deletePersonInEvent"));
			// Not going to event -> Going to event
			else
				params.add(new BasicNameValuePair("do", "createPersonInEvent"));

			// Change attending status
			json = jsonParser.makeHttpsRequest(IUniformResourceLocator.URL.URL_EVENTPERSON, "GET", params,
							SingleGroupController.this);

			try {
				int success = json.getInt(TAG_SUCCESS);
				if (success == 1) {
				}
			} catch (Exception e) {
				e.printStackTrace();
				pDialog.dismiss();
				logout();
			}
			return null;
		}

		/**
		 * Removes ProgressDialog. Restarts refreshed activity.
		 */
		protected void onPostExecute(String message) {
			pDialog.dismiss();

			finish();
			Intent intent = new Intent(SingleGroupController.this, SingleGroupController.class);
			startActivity(intent);
		}
	}
}