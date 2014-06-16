package fhkl.de.orgapp.controller.notification;

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
import android.util.Log;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import fhkl.de.orgapp.R;
import fhkl.de.orgapp.util.IMessages;
import fhkl.de.orgapp.util.JSONParser;
import fhkl.de.orgapp.util.MenuActivity;

public class NotificationController extends MenuActivity {

	private ProgressDialog pDialog;
	JSONParser jsonParser = new JSONParser();
	ArrayList<HashMap<String, String>> notificationList;

	private static String url_get_notification_settings = "http://pushrply.com/get_notification_settings.php";
	private static String url_get_notification = "http://pushrply.com/get_notifications.php";

	private static final String TAG_SUCCESS = "success";
	private static final String TAG_MESSAGE = "MESSAGE";

	JSONArray notification = null;
	JSONArray notificationSettings = null;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.notification);
		notificationList = new ArrayList<HashMap<String, String>>();
		new Notification().execute();
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
			pDialog.setCancelable(false);
			pDialog.show();
		}

		protected String doInBackground(String... args) {
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair("personId", getIntent().getStringExtra(
					"UserId")));

			JSONObject json = jsonParser.makeHttpRequest(
					url_get_notification_settings, "GET", params);

			Log.d("NotificationSettings: ", json.toString());

			try {
				int success = json.getInt(TAG_SUCCESS);
				if (success == 1) {
					List<NameValuePair> paramsNotifications = new ArrayList<NameValuePair>();
					paramsNotifications.add(new BasicNameValuePair("personId",
							getIntent().getStringExtra("UserId")));

					notificationSettings = json.getJSONArray("notificationSettings");
					for (int i = 0; i < notificationSettings.length(); i++) {
						JSONObject c = notificationSettings.getJSONObject(i);
						String groupInvites = c.getInt("groupInvites") == 1 ? "1" : null;
						if (groupInvites != null) {
							paramsNotifications.add(new BasicNameValuePair("groupInvites",
									groupInvites));
						}
						String groupEdited = c.getInt("groupEdited") == 1 ? "2" : null;
						if (groupEdited != null) {
							paramsNotifications.add(new BasicNameValuePair("groupEdited",
									groupEdited));
						}
						String groupRemoved = c.getInt("groupRemoved") == 1 ? "3" : null;
						if (groupRemoved != null) {
							paramsNotifications.add(new BasicNameValuePair("groupRemoved",
									groupRemoved));
						}
						String eventsAdded = c.getInt("eventsAdded") == 1 ? "4" : null;
						if (eventsAdded != null) {
							paramsNotifications.add(new BasicNameValuePair("eventsAdded",
									eventsAdded));
						}
						String eventsEdited = c.getInt("eventsEdited") == 1 ? "5" : null;
						if (eventsEdited != null) {
							paramsNotifications.add(new BasicNameValuePair("eventsEdited",
									eventsEdited));
						}
						String eventsRemoved = c.getInt("eventsRemoved") == 1 ? "6" : null;
						if (eventsRemoved != null) {
							paramsNotifications.add(new BasicNameValuePair("eventsRemoved",
									eventsRemoved));
						}
						String commentsAdded = c.getInt("commentsAdded") == 1 ? "7" : null;
						if (commentsAdded != null) {
							paramsNotifications.add(new BasicNameValuePair("commentsAdded",
									commentsAdded));
						}
						String commentsEdited = c.getInt("commentsEdited") == 1 ? "8"
								: null;
						if (commentsEdited != null) {
							paramsNotifications.add(new BasicNameValuePair("commentsEdited",
									commentsEdited));
						}
						String commentsRemoved = c.getInt("commentsRemoved") == 1 ? "9"
								: null;
						if (commentsRemoved != null) {
							paramsNotifications.add(new BasicNameValuePair("commentsRemoved",
									commentsRemoved));
						}
						String privilegeGiven = c.getInt("privilegeGiven") == 1 ? "10"
								: null;
						System.out.println("privi" + privilegeGiven);
						if (privilegeGiven != null) {
							paramsNotifications.add(new BasicNameValuePair("privilegeGiven",
									privilegeGiven));
						}

						paramsNotifications.add(new BasicNameValuePair("shownEntries", c
								.getString("shownEntries")));

					}

					json = jsonParser.makeHttpRequest(url_get_notification, "GET",
							paramsNotifications);
					if (success == 1) {
						notification = json.getJSONArray("notification");

						for (int i = 0; i < notification.length(); i++) {
							JSONObject c = notification.getJSONObject(i);

							String message = c.getString("message");

							HashMap<String, String> map = new HashMap<String, String>();
							map.put(TAG_MESSAGE, message);

							notificationList.add(map);
						}
					} else {

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
					ListAdapter adapter = new SimpleAdapter(NotificationController.this,
							notificationList, R.layout.notification_item,
							new String[] { TAG_MESSAGE }, new int[] { R.id.MESSAGE });
					ListView notificationList = (ListView) findViewById(android.R.id.list);
					notificationList.setAdapter(adapter);
				}
			});
		}
	}
}