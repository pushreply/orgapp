package fhkl.de.orgapp.controller;

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

import com.example.orgapp.R;

import fhkl.de.orgapp.util.IMessages;
import fhkl.de.orgapp.util.JSONParser;
import fhkl.de.orgapp.util.MenuActivity;

public class NotificationController extends MenuActivity {

	private ProgressDialog pDialog;
	JSONParser jsonParser = new JSONParser();
	ArrayList<HashMap<String, String>> notificationList;

	private static String url_Notifications = "http://pushrply.com/Notifications.php";

	private static final String TAG_SUCCESS = "success";
	private static final String TAG_MESSAGE = "MESSAGE";

	JSONArray notification = null;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		if (getIntent().getStringExtra("Notification_Settings") != null) {
			setContentView(R.layout.notification_settings);
		} else {
			setContentView(R.layout.notification);
			notificationList = new ArrayList<HashMap<String, String>>();
			new Notification().execute();
		}		
	}

	class Notification extends AsyncTask<String, String, String> {
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pDialog = new ProgressDialog(NotificationController.this);
			pDialog.setMessage(IMessages.LOADING_NOTIFICATIONS);
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(false);
			pDialog.show();
		}

		protected String doInBackground(String... args) {
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair("personId", getIntent().getStringExtra(
					"UserId")));

			JSONObject json = jsonParser.makeHttpRequest(url_Notifications, "GET",
					params);

			Log.d("Notification: ", json.toString());

			try {
				int success = json.getInt(TAG_SUCCESS);
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