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
import android.widget.TextView;
import fhkl.de.orgapp.R;
import fhkl.de.orgapp.util.IMessages;
import fhkl.de.orgapp.util.IUniformResourceLocator;
import fhkl.de.orgapp.util.JSONParser;
import fhkl.de.orgapp.util.MenuActivity;
import fhkl.de.orgapp.util.data.GroupData;
import fhkl.de.orgapp.util.data.UserData;

/**
 * LeaveGroupController - Handles the leave group activity.
 * 
 * Removes a group member from a group.
 * 
 * @author Ronaldo Hasiholan
 * @version 3.9
 */

public class LeaveGroupController extends MenuActivity {
	private static final String TAG_SUCCESS = "success";

	List<NameValuePair> notificationParams;
	String notification;
	TextView tv_memberId;
	private ProgressDialog pDialog;

	JSONParser jsonParser = new JSONParser();
	ArrayList<HashMap<String, String>> memberList;
	JSONArray member = null;

	/**
	 * Calls Async class that removes user from group.
	 * 
	 * @param savedInstanceState Bundle
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.member_list);
		checkOnNewNotificationsAndNotifyUser();
		memberList = new ArrayList<HashMap<String, String>>();

		new LeaveGroup().execute();
	}

	/**
	 * Asnyc class that removes user from group.
	 * 
	 */
	class LeaveGroup extends AsyncTask<String, String, String> {
		/**
		 * Creates ProgressDialog
		 */
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pDialog = new ProgressDialog(LeaveGroupController.this);
			pDialog.setMessage(IMessages.Status.LEAVING_GROUP);
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(true);
			pDialog.show();
		}

		/**
		 * Removes user from group.
		 * 
		 * @param args String...
		 * @return String result
		 */
		protected String doInBackground(String... args) {
			tv_memberId = (TextView) findViewById(R.id.MEMBERID);
			List<NameValuePair> params = new ArrayList<NameValuePair>();

			// Required parameters
			params.add(new BasicNameValuePair("do", "deletePrivilege"));
			params.add(new BasicNameValuePair("personId", UserData.getPERSONID()));
			params.add(new BasicNameValuePair("groupId", GroupData.getGROUPID()));

			// Leave group
			JSONObject json = jsonParser.makeHttpsRequest(IUniformResourceLocator.URL.URL_PRIVILEGE, "GET", params,
							LeaveGroupController.this);

			try {
				int success = json.getInt(TAG_SUCCESS);

				if (success == 1) {
					return null;
				}

			} catch (Exception e) {
				e.printStackTrace();
				pDialog.dismiss();
				logout();
			}

			return null;
		}

		/**
		 * Removes ProgressDialog. Returns to groups activity.
		 * 
		 * @param message String
		 */
		protected void onPostExecute(String message) {
			super.onPostExecute(message);
			pDialog.dismiss();
			startActivity(new Intent(LeaveGroupController.this, GroupsController.class));
		}
	}
}