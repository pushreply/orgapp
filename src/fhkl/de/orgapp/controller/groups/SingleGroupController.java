package fhkl.de.orgapp.controller.groups;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import fhkl.de.orgapp.R;
import fhkl.de.orgapp.controller.event.EventController;
import fhkl.de.orgapp.util.EventData;
import fhkl.de.orgapp.util.GroupData;
import fhkl.de.orgapp.util.IMessages;
import fhkl.de.orgapp.util.JSONParser;
import fhkl.de.orgapp.util.ListModel;
import fhkl.de.orgapp.util.MenuActivity;
import fhkl.de.orgapp.util.UserData;
import fhkl.de.orgapp.util.check.Check;

public class SingleGroupController extends MenuActivity {

	private ProgressDialog pDialog;

	JSONParser jsonParser = new JSONParser();

	public ArrayList<ListModel> customAdapterValues = new ArrayList<ListModel>();

	private static String url_get_calendar = "http://pushrply.com/get_group_events.php";
	private static String url_get_event = "http://pushrply.com/get_event.php";
	private static String URL_CREATE_PERSON_IN_EVENT = "http://pushrply.com/create_person_in_event.php";
	private static String URL_DELETE_PERSON_IN_EVENT = "http://pushrply.com/delete_person_in_event.php";

	private static final String TAG_SUCCESS = "success";

	String eventId = null;
	JSONArray calendar = null;
	JSONArray event = null;

	Boolean toggleButtonChecked;
	TextView tv_eventId, groupInfo;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.singlegroup);
		this.setTitle(GroupData.getGROUPNAME());

		EventData.setBACK(false);

		groupInfo = (TextView) findViewById(R.id.GROUP_INFO);
		groupInfo.setText(GroupData.getGROUPINFO());

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
						String eventName = c.getString("name");
						String eventDate = c.getString("eventDate");
						String eventTime = c.getString("eventTime");

						final ListModel listModel = new ListModel();

						listModel.setEventId(eventId);
						listModel.setEventName(eventName);
						listModel.setEventDate(eventDate);
						listModel.setEventTime(eventTime);
						if (Check.attendingMember(eventId)) {
							listModel.setAttending(R.drawable.ic_action_good);
						} else {
							listModel.setAttending(R.drawable.ic_action_bad);
						}
						customAdapterValues.add(listModel);

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

					CustomAdapter adapter = new CustomAdapter(SingleGroupController.this, customAdapterValues, getResources());

					ListView calenderList = (ListView) findViewById(android.R.id.list);

					calenderList.setOnItemClickListener(new OnItemClickListener() {

						@Override
						public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

							TextView tv = (TextView) view.findViewById(R.id.SINGLEGROUP_EVENTID);
							eventId = tv.getText().toString();
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

	@SuppressWarnings("unused")
	private class CustomAdapter extends BaseAdapter {

		private ArrayList<ListModel> data;
		private Resources res;
		private final Activity context;
		private LayoutInflater inflater = null;

		public CustomAdapter(Activity context, ArrayList<ListModel> data, Resources res) {

			this.context = context;
			this.data = data;
			this.res = res;

			inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		}

		public class ViewHolder {

			public TextView eventId;
			public TextView eventName;
			public TextView eventDate;
			public TextView eventTime;
			public ImageView attending;

		}

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
				holder.eventDate.setText(tempValues.getEventDate());
				holder.eventTime.setText(tempValues.getEventTime());
				holder.attending.setImageResource(tempValues.getAttending());

				holder.eventName.setOnClickListener(onEventListener);
				holder.eventDate.setOnClickListener(onEventListener);
				holder.eventTime.setOnClickListener(onEventListener);
				holder.attending.setOnClickListener(onAttendingListener);
			}
			return view;
		}

		private OnClickListener onEventListener = new OnClickListener() {

			@Override
			public void onClick(View view) {

				tv_eventId = (TextView) view.findViewById(R.id.SINGLEGROUP_EVENTID);
				new GetEvent().execute();
			}
		};

		private OnClickListener onAttendingListener = new OnClickListener() {

			@Override
			public void onClick(View view) {

				ImageView iv = (ImageView) view.findViewById(R.id.SINGLEGROUP_ATTENDING);

				if (iv.getDrawable().equals((R.drawable.ic_action_good))) {
					toggleButtonChecked = true;
				} else {
					toggleButtonChecked = false;
				}

				System.out.println(toggleButtonChecked);
				new ChangeAttendingStatus().execute();
			}
		};

		@Override
		public int getCount() {
			if (data.size() <= 0)
				return 1;
			return data.size();
		}

		@Override
		public Object getItem(int position) {
			return data.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}
	}

	class ChangeAttendingStatus extends AsyncTask<String, String, String> {
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pDialog = new ProgressDialog(SingleGroupController.this);
			pDialog.setMessage(IMessages.CHANGING_STATUS);
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(true);
			pDialog.show();
		}

		protected String doInBackground(String... args) {
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair("personId", UserData.getPERSONID()));
			params.add(new BasicNameValuePair("eventId", eventId));
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