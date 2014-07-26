package fhkl.de.orgapp.controller.notification;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import fhkl.de.orgapp.R;
import fhkl.de.orgapp.util.IMessages;
import fhkl.de.orgapp.util.JSONParser;
import fhkl.de.orgapp.util.MenuActivity;
import fhkl.de.orgapp.util.NotificationSettingsData;
import fhkl.de.orgapp.util.UserData;

public class NotificationController extends MenuActivity
{
	private ProgressDialog pDialog;
	JSONParser jsonParser = new JSONParser();
	ArrayList<HashMap<String, String>> notificationList;

	private static String url_get_notification = "http://pushrply.com/get_notifications.php";
	private static String url_update_notification_read_status = "http://pushrply.com/update_notification_read_status.php";

	private static final String TAG_SUCCESS = "success";
	private static final String TAG_ID = "ID";
	private static final String TAG_MESSAGE = "MESSAGE";
	private static final String TAG_IS_READ = "IS READ";

	JSONArray notification = null;
	JSONArray notificationSettings = null;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		deleteIcon();
		
		setContentView(R.layout.notification);
		notificationList = new ArrayList<HashMap<String, String>>();
		new Notification().execute();

		LayoutInflater inflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View notificationItemView = inflater.inflate(R.layout.notification_item, null);
		TextView message = (TextView) notificationItemView.findViewById(R.id.MESSAGE);

		if (Integer.valueOf(android.os.Build.VERSION.SDK_INT) < 16) {
			message.setMaxLines(Integer.MAX_VALUE);
		}
	}

	class Notification extends AsyncTask<String, String, String> {
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pDialog = new ProgressDialog(NotificationController.this);

			if (getIntent().getStringExtra("Refresh") != null)
				pDialog.setMessage(IMessages.UPDATING);
			else
				pDialog.setMessage(IMessages.LOADING_NOTIFICATIONS);

			pDialog.setIndeterminate(false);
			pDialog.setCancelable(true);
			pDialog.show();
		}

		protected String doInBackground(String... args)
		{
			try
			{
				List<NameValuePair> paramsNotifications = new ArrayList<NameValuePair>();
				paramsNotifications.add(new BasicNameValuePair("personId", UserData.getPERSONID()));
				
				String groupInvites = Boolean.parseBoolean(NotificationSettingsData.getGROUP_INVITES()) ? "1" : null;
				if (groupInvites != null)
				{
					paramsNotifications.add(new BasicNameValuePair("groupInvites", groupInvites));
				}
				String groupEdited = Boolean.parseBoolean(NotificationSettingsData.getGROUP_EDITED()) ? "2" : null;
				if (groupEdited != null)
				{
					paramsNotifications.add(new BasicNameValuePair("groupEdited", groupEdited));
				}
				String groupRemoved = Boolean.parseBoolean(NotificationSettingsData.getGROUP_REMOVED()) ? "3" : null;
				if (groupRemoved != null)
				{
					paramsNotifications.add(new BasicNameValuePair("groupRemoved", groupRemoved));
				}
				String eventsAdded = Boolean.parseBoolean(NotificationSettingsData.getEVENTS_ADDED()) ? "4" : null;
				if (eventsAdded != null)
				{
					paramsNotifications.add(new BasicNameValuePair("eventsAdded", eventsAdded));
				}
				String eventsEdited = Boolean.parseBoolean(NotificationSettingsData.getEVENTS_EDITED()) ? "5" : null;
				if (eventsEdited != null)
				{
					paramsNotifications.add(new BasicNameValuePair("eventsEdited", eventsEdited));
				}
				String eventsRemoved = Boolean.parseBoolean(NotificationSettingsData.getEVENTS_REMOVED()) ? "6" : null;
				if (eventsRemoved != null)
				{
					paramsNotifications.add(new BasicNameValuePair("eventsRemoved", eventsRemoved));
				}
				String commentsAdded = Boolean.parseBoolean(NotificationSettingsData.getCOMMENTS_ADDED()) ? "7" : null;
				if (commentsAdded != null)
				{
					paramsNotifications.add(new BasicNameValuePair("commentsAdded", commentsAdded));
				}
				String commentsEdited = Boolean.parseBoolean(NotificationSettingsData.getCOMMENTS_EDITED()) ? "8" : null;
				if (commentsEdited != null)
				{
					paramsNotifications.add(new BasicNameValuePair("commentsEdited", commentsEdited));
				}
				String commentsRemoved = Boolean.parseBoolean(NotificationSettingsData.getCOMMENTS_REMOVED()) ? "9" : null;
				if (commentsRemoved != null)
				{
					paramsNotifications.add(new BasicNameValuePair("commentsRemoved", commentsRemoved));
				}
				String privilegeGiven = Boolean.parseBoolean(NotificationSettingsData.getPRIVILEGE_GIVEN()) ? "10" : null;
				if (privilegeGiven != null)
				{
					paramsNotifications.add(new BasicNameValuePair("privilegeGiven", privilegeGiven));
				}
				
				if(NotificationSettingsData.getSHOW_ENTRIES() != null && !NotificationSettingsData.getSHOW_ENTRIES().equals(""))
					paramsNotifications.add(new BasicNameValuePair("shownEntries", NotificationSettingsData.getSHOW_ENTRIES()));
				
				JSONObject json = jsonParser.makeHttpRequest(url_get_notification, "GET", paramsNotifications);
				
				int success = json.getInt(TAG_SUCCESS);
				
				if (success == 1)
				{
					notification = json.getJSONArray("notification");
					
					for (int i = 0; i < notification.length(); i++)
					{
						JSONObject c = notification.getJSONObject(i);
						
						String id = c.getString("notificationsId");
						String message = c.getString("message");
						String isRead = c.getString("isRead");
						
						HashMap<String, String> map = new HashMap<String, String>();
						map.put(TAG_ID, id);
						map.put(TAG_MESSAGE, message);
						map.put(TAG_IS_READ, isRead);

						notificationList.add(map);
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

		protected void onPostExecute(String file_url) {
			pDialog.dismiss();
			runOnUiThread(new Runnable() {
				public void run() {
					
					ListView notificationListView = (ListView) findViewById(android.R.id.list);
					
					class NotificationListAdapter extends BaseAdapter
					{
				        private ArrayList<HashMap<String, String>> notificationList;
				        
				        public NotificationListAdapter(ArrayList<HashMap<String, String>> notificationList)
				        {
				            this.notificationList = notificationList;
				        }

				        public int getCount()
				        {
				            return notificationList.size();
				        }

				        public Object getItem(int arg0)
				        {
				            return null;
				        }

				        public long getItemId(int position)
				        {
				            return position;
				        }

				        public View getView(int position, View convertView, ViewGroup parent)
				        {
				            LayoutInflater inflater = getLayoutInflater();
				            View row;
				            row = inflater.inflate(R.layout.notification_item, parent, false);
				            TextView message;
				            message = (TextView) row.findViewById(R.id.MESSAGE);
				            message.setText(notificationList.get(position).get(TAG_MESSAGE).toString());
				            
				            if(notificationList.get(position).get(TAG_IS_READ).equals("0"))
				            	message.setTypeface(Typeface.DEFAULT_BOLD);
				            
				            return (row);
				        }
				    }
					
					notificationListView.setAdapter(new NotificationListAdapter(notificationList));
					notificationListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

						@SuppressLint("NewApi")
						@Override
						public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

							final TextView message = (TextView) view.findViewById(R.id.MESSAGE);
							
							if(message.getTypeface() != null && message.getTypeface().isBold())
							{
								message.setTypeface(Typeface.DEFAULT);
								new NotificationReadStatusUpdater().execute(position);
							}
							
							Animation slideDown = AnimationUtils.loadAnimation(NotificationController.this, R.anim.slide_down);
							Animation slideUp = AnimationUtils.loadAnimation(NotificationController.this, R.anim.slide_up);

							slideDown.setAnimationListener(new AnimationListener() {

								@Override
								public void onAnimationStart(Animation animation) {

									message.setMaxLines(Integer.MAX_VALUE);
								}

								@Override
								public void onAnimationRepeat(Animation animation) {
								}

								@Override
								public void onAnimationEnd(Animation animation) {
								}
							});

							slideUp.setAnimationListener(new AnimationListener() {

								@Override
								public void onAnimationStart(Animation animation) {
								}

								@Override
								public void onAnimationRepeat(Animation animation) {
								}

								@Override
								public void onAnimationEnd(Animation animation) {
									message.setMaxLines(2);
								}

							});

							if (Integer.valueOf(android.os.Build.VERSION.SDK_INT) >= 16) {
								if (message.getMaxLines() == 2) {
									message.startAnimation(slideDown);
								} else {
									message.startAnimation(slideUp);
								}
							}
						}
						
						class NotificationReadStatusUpdater extends AsyncTask<Integer, String, String>
						{
							@Override
							protected String doInBackground(Integer... arg)
							{
								List<NameValuePair> params = new ArrayList<NameValuePair>();
								params.add(new BasicNameValuePair("notificationsId", notificationList.get(arg[0]).get(TAG_ID)));
								jsonParser.makeHttpRequest(url_update_notification_read_status, "GET", params);
								
								return null;
							}
						}
					});
				}
			});
		}
	}
}