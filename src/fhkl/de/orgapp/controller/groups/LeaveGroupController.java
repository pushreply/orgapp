package fhkl.de.orgapp.controller.groups;

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
import android.util.Log;
import android.widget.TextView;
import fhkl.de.orgapp.R;
import fhkl.de.orgapp.util.IMessages;
import fhkl.de.orgapp.util.JSONParser;
import fhkl.de.orgapp.util.MenuActivity;
import fhkl.de.orgapp.util.data.GroupData;
import fhkl.de.orgapp.util.data.UserData;

//import fhkl.de.orgapp.controller.groups.GroupsController;

public class LeaveGroupController extends MenuActivity {
	private static String URL_LEAVE_GROUP = "http://pushrply.com/leave_group.php";
	private static String URL_SEND_NOTIFICATION = "http://pushrply.com/create_notification.php";

	private static final String TAG_SUCCESS = "success";

	List<NameValuePair> notificationParams;
	String notification;
	TextView tv_memberId;
	private ProgressDialog pDialog;

	JSONParser jsonParser = new JSONParser();
	ArrayList<HashMap<String, String>> memberList;
	JSONArray member = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.member_list);
		checkOnNewNotificationsAndNotifyUser();
		memberList = new ArrayList<HashMap<String, String>>();

		new LeaveGroup().execute();
	}

	class LeaveGroup extends AsyncTask<String, String, String> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pDialog = new ProgressDialog(LeaveGroupController.this);
			pDialog.setMessage(IMessages.Status.LEAVING_GROUP);
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(true);
			pDialog.show();
		}

		protected String doInBackground(String... args) {
			// need parameters personId and groupId
			tv_memberId = (TextView) findViewById(R.id.MEMBERID);
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair("personId", UserData.getPERSONID()));
			params.add(new BasicNameValuePair("groupId", GroupData.getGROUPID()));

			// send parameters to jsonParser
			JSONObject json = jsonParser.makeHttpRequest(URL_LEAVE_GROUP, "GET", params);

			Log.d("Response: ", json.toString());

			try {
				int success = json.getInt(TAG_SUCCESS);
				if (success == 1) {
					return null;
				}

				notificationParams = new ArrayList<NameValuePair>();
				notification = IMessages.Notification.NOTIFICATION_LEAVING_GROUP + GroupData.getGROUPNAME();
				notificationParams.add(new BasicNameValuePair("message", notification));
				notificationParams.add(new BasicNameValuePair("classification", "3"));
				notificationParams.add(new BasicNameValuePair("syncInterval", null));

				json = jsonParser.makeHttpRequest(URL_SEND_NOTIFICATION, "GET", notificationParams);

				if (json.getInt(TAG_SUCCESS) != 1)
					return null;

			} catch (Exception e) {
				e.printStackTrace();
				logout();
			}

			return null;
		}

		protected void onPostExecute(String message) {
			super.onPostExecute(message);
			pDialog.dismiss();
			startActivity(new Intent(LeaveGroupController.this, GroupsController.class));

		}
	}
}
