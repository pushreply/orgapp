package fhkl.de.orgapp.controller.groups;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;
import fhkl.de.orgapp.R;
import fhkl.de.orgapp.util.GroupData;
import fhkl.de.orgapp.util.IMessages;
import fhkl.de.orgapp.util.JSONParser;
import fhkl.de.orgapp.util.MenuActivity;
//import fhkl.de.orgapp.controller.groups.GroupsController;

public class LeaveGroupController extends MenuActivity
{
	private static String URL_LEAVE_GROUP = "http://pushrply.com/leave_group.php";
	private static String URL_SEND_NOTIFICATION = "http://pushrply.com/create_notification.php";

	private static final String TAG_SUCCESS = "success";


	TextView tv_memberId;
	private ProgressDialog pDialog;

	JSONParser jsonParser = new JSONParser();
	ArrayList<HashMap<String, String>> memberList;
	JSONArray member = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.member_list);
		memberList = new ArrayList<HashMap<String, String>>();

		new LeaveGroup().execute();
	}

	class LeaveGroup extends AsyncTask<String, String, String> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pDialog = new ProgressDialog(LeaveGroupController.this);
			pDialog.setMessage(IMessages.LEAVING_GROUP);
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(true);
			pDialog.show();
		}

		protected String doInBackground(String... args) {
			if (GroupData.getPERSONID().equals(tv_memberId.getText().toString())) {
				return IMessages.REMOVING_ADMIN;
			}
			List<NameValuePair> params = new ArrayList<NameValuePair>();

			params.add(new BasicNameValuePair("personId", tv_memberId.getText().toString()));
			params.add(new BasicNameValuePair("groupId", GroupData.getGROUPID()));

			JSONObject json = jsonParser.makeHttpRequest(URL_LEAVE_GROUP, "GET", params);

			Log.d("Response: ", json.toString());

			try {
				int success = json.getInt(TAG_SUCCESS);
				if (success == 1) {
					return null;
				}
			} catch (JSONException e) {
				System.out.println("Error in LeaveGroup.doInBackground(String... args): " + e.getMessage());
				e.printStackTrace();
			}

			return null;
		}

		protected void onPostExecute(String message) {
			pDialog.dismiss();

			if (message != null) {
				Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
				Intent intent = new Intent(LeaveGroupController.this, GroupsController.class);
				tv_memberId =  (TextView) findViewById(R.id.MEMBERID);
				intent.putExtra("MemberId", tv_memberId.getText().toString());
				startActivity(intent);
			}
		}
	}
}
