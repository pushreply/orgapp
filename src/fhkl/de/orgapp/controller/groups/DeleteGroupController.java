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
import fhkl.de.orgapp.util.IUniformResourceLocator;
import fhkl.de.orgapp.util.JSONParser;
import fhkl.de.orgapp.util.MenuActivity;
import fhkl.de.orgapp.util.data.GroupData;

/**
 * DeleteGroupController - Handles the delete group activity
 * 
 * Deletes the chosen group. Deletes all group users. Sends Notifications to
 * members.
 * 
 * @author Jochen Jung
 * @version 1.0
 */
public class DeleteGroupController extends MenuActivity
{
	private static final String TAG_SUCCESS = "success";
	private ProgressDialog pDialog;
	JSONParser jsonParser = new JSONParser();
	JSONObject json;
	JSONArray memberList;
	List<NameValuePair> params;
	int m;
	String notification, TAG_EMAIL = "eMail";

	/**
	 * Calls the async class to delete a group.
	 * 
	 * @param savedInstanceState Bundle
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		new GroupDelete().execute();
	}

	/**
	 * Async class that deletes the group and users and sends notifications to
	 * users.
	 * 
	 * @author Jochen Jung
	 * @version 1.0
	 */
	class GroupDelete extends AsyncTask<String, String, String> {
		/**
		 * Creates ProcessDialog
		 */
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pDialog = new ProgressDialog(DeleteGroupController.this);
			pDialog.setMessage(IMessages.Status.DELETING_GROUP);
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(true);
			pDialog.show();
		}

		/**
		 * Deletes all group members, deletes group and sends notifications to all
		 * members.
		 * 
		 * @param args String...
		 * @return String result
		 */
		@Override
		protected String doInBackground(String... args)
		{
			params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair("do", "readAllUserInGroup"));
			params.add(new BasicNameValuePair("groupId", GroupData.getGROUPID()));

			// Get all group members
			json = jsonParser.makeHttpRequest(IUniformResourceLocator.URL.URL_GROUPS, "GET", params);

			try {
				if (json.getInt(TAG_SUCCESS) != 1)
					return null;

				memberList = json.getJSONArray("member");

				// Delete privilege entries
				params = new ArrayList<NameValuePair>();
				params.add(new BasicNameValuePair("do", "deletePrivilegeGroup"));
				params.add(new BasicNameValuePair("groupId", GroupData.getGROUPID()));
				
				json = jsonParser.makeHttpRequest(IUniformResourceLocator.URL.URL_PRIVILEGE, "GET", params);

				if (json.getInt(TAG_SUCCESS) != 1)
					return null;

				// Delete group
				params = new ArrayList<NameValuePair>();
				params.add(new BasicNameValuePair("do", "deleteGroup"));
				params.add(new BasicNameValuePair("groupId", GroupData.getGROUPID()));
				
				json = jsonParser.makeHttpRequest(IUniformResourceLocator.URL.URL_GROUPS, "GET", params);

				if (json.getInt(TAG_SUCCESS) != 1)
					return null;

				// Send notifications
				params = new ArrayList<NameValuePair>();
				params.add(new BasicNameValuePair("do", "create"));
				notification = IMessages.Notification.DELETE_GROUP_NOTIFICATION_1 + GroupData.getGROUPNAME()
								+ IMessages.Notification.DELETE_GROUP_NOTIFICATION_2;
				params.add(new BasicNameValuePair("message", notification));
				params.add(new BasicNameValuePair("classification", "3"));
				params.add(new BasicNameValuePair("syncInterval", null));

				for (m = 0; m < memberList.length(); m++) {
					params.add(new BasicNameValuePair("eMail", memberList.getJSONObject(m).getString(TAG_EMAIL)
									.toString()));
					// Send notification
					json = jsonParser.makeHttpRequest(IUniformResourceLocator.URL.URL_NOTIFICATION, "GET", params);

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

		/**
		 * Removes ProcessDialog. Shows Toasts. Starts new Activity on successful
		 * delete.
		 */
		@Override
		protected void onPostExecute(String message) {
			super.onPostExecute(message);
			pDialog.dismiss();

			if (message != null) {
				Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
				startActivity(new Intent(DeleteGroupController.this, GroupsController.class));
			} else {
				Toast.makeText(getApplicationContext(), IMessages.Error.GROUP_NOT_DELETED, Toast.LENGTH_LONG).show();
			}
		}
	}
}