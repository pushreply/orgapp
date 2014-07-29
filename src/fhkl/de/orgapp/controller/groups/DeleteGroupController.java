package fhkl.de.orgapp.controller.groups;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.Toast;
import fhkl.de.orgapp.util.IMessages;
import fhkl.de.orgapp.util.JSONParser;
import fhkl.de.orgapp.util.MenuActivity;
import fhkl.de.orgapp.util.data.GroupData;

public class DeleteGroupController extends MenuActivity {
	private static String URL_GET_ALL_USER_IN_GROUP = "http://pushrply.com/get_all_user_in_group.php";
	private static String URL_DELETE_PRIVILEGE_ENTRIES_BY_GROUP_ID = "http://pushrply.com/delete_privilege_entries_by_group_id.php";
	private static String URL_DELETE_GROUP_BY_ID = "http://pushrply.com/delete_group_by_id.php";
	private static String URL_SEND_NOTIFICATION = "http://pushrply.com/create_notification.php";

	private static final String TAG_SUCCESS = "success";
	private ProgressDialog pDialog;
	JSONParser jsonParser = new JSONParser();
	JSONObject json;
	JSONArray memberList;
	List<NameValuePair> urlParams, notificationParams;
	int m;
	String notification, TAG_EMAIL = "eMail";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		new GroupDelete().execute();
	}

	class GroupDelete extends AsyncTask<String, String, String> {
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pDialog = new ProgressDialog(DeleteGroupController.this);
			pDialog.setMessage(IMessages.Status.DELETING_GROUP);
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(true);
			pDialog.show();
		}

		@Override
		protected String doInBackground(String... params) {
			urlParams = new ArrayList<NameValuePair>();
			urlParams.add(new BasicNameValuePair("groupId", GroupData.getGROUPID()));

			// get all group members
			json = jsonParser.makeHttpRequest(URL_GET_ALL_USER_IN_GROUP, "GET", urlParams);

			try {
				if (json.getInt(TAG_SUCCESS) != 1)
					return null;

				memberList = json.getJSONArray("member");

				// delete privilege entries
				json = jsonParser.makeHttpRequest(URL_DELETE_PRIVILEGE_ENTRIES_BY_GROUP_ID, "GET", urlParams);

				if (json.getInt(TAG_SUCCESS) != 1)
					return null;

				// delete group
				json = jsonParser.makeHttpRequest(URL_DELETE_GROUP_BY_ID, "GET", urlParams);

				if (json.getInt(TAG_SUCCESS) != 1)
					return null;

				notificationParams = new ArrayList<NameValuePair>();

				notification = IMessages.Notification.DELETE_GROUP_NOTIFICATION_1 + GroupData.getGROUPNAME()
								+ IMessages.Notification.DELETE_GROUP_NOTIFICATION_2;

				notificationParams.add(new BasicNameValuePair("message", notification));
				notificationParams.add(new BasicNameValuePair("classification", "3"));
				notificationParams.add(new BasicNameValuePair("syncInterval", null));

				for (m = 0; m < memberList.length(); m++) {
					notificationParams.add(new BasicNameValuePair("eMail", memberList.getJSONObject(m).getString(TAG_EMAIL)
									.toString()));
					// send notification
					json = jsonParser.makeHttpRequest(URL_SEND_NOTIFICATION, "GET", notificationParams);

					if (json.getInt(TAG_SUCCESS) != 1)
						return null;
				}

				return (IMessages.Success.GROUP_SUCCESSFUL_DELETED + GroupData.getGROUPNAME());
			} catch (Exception e) {
				e.printStackTrace();
				logout();
			}

			return null;
		}

		@Override
		protected void onPostExecute(String message) {
			super.onPostExecute(message);

			if (message != null) {
				Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
				startActivity(new Intent(DeleteGroupController.this, GroupsController.class));
			} else {
				Toast.makeText(getApplicationContext(), IMessages.Error.GROUP_NOT_DELETED, Toast.LENGTH_LONG).show();
			}
		}
	}
}