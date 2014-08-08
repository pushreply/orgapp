package fhkl.de.orgapp.controller.event;

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
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import fhkl.de.orgapp.R;
import fhkl.de.orgapp.controller.groups.MemberPrivilegeInfoController;
import fhkl.de.orgapp.util.IMessages;
import fhkl.de.orgapp.util.IUniformResourceLocator;
import fhkl.de.orgapp.util.JSONParser;
import fhkl.de.orgapp.util.MenuActivity;
import fhkl.de.orgapp.util.data.EventData;
import fhkl.de.orgapp.util.data.GroupData;
import fhkl.de.orgapp.util.data.MemberData;
import fhkl.de.orgapp.util.data.UserData;

/**
 * AttendingMemberController - handles list of members who are attending an
 * event. Each member is marked by email.
 * 
 * @author ronaldo.hasiholan
 * @version ?
 */
public class AttendingMemberController extends MenuActivity {

	// Android progress dialog.
	private ProgressDialog pDialog;

	// A json parser and a container for the memberlist.
	JSONParser jsonParser = new JSONParser();
	ArrayList<HashMap<String, String>> memberList;

	// Marker tag to sent from server to client app
	// to inform whether the request is completed or failed.
	private static final String TAG_SUCCESS = "success";

	// Variables for custom item list
	private static final String TAG_MEMBER_ID = "MEMBERID";
	private static final String TAG_MEMBER_NAME = "MEMBERNAME";

	// Hidden container for memberId, to be used for item selection
	TextView tv_memberId;

	// A json array to contain member items
	JSONArray member = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.member_list);

		// Notify user(s) with specific privileges.
		checkOnNewNotificationsAndNotifyUser();
		memberList = new ArrayList<HashMap<String, String>>();

		// Save the android-back-button state
		EventData.setBACK(true);

		// Execute the GetMemberList() to get the members
		new GetMemberList().execute();
	}

	/**
	 * Begin the background operation using asynchronous task to fetch data
	 * through the network. The GetMemberList Class gets all members who are
	 * attending the event by referring the personId and eventId using the GET
	 * request. The PHP files on the server side handle the GET request, return
	 * the result and a success marker to the client app.
	 * 
	 */
	class GetMemberList extends AsyncTask<String, String, String> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();

			// Add a simple progress dialog
			pDialog = new ProgressDialog(AttendingMemberController.this);
			pDialog.setMessage(IMessages.Status.LOADING_MEMBER_LIST);
			pDialog.setIndeterminate(false);

			// If the progress is taking too long, let user cancels
			// the progress dialog by hitting the android-back-button.
			pDialog.setCancelable(true);
			pDialog.show();
		}

		protected String doInBackground(String... args) {

			// Prepare the parameters for HTTP request to the server
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair("do", "readAttendingMember"));
			params.add(new BasicNameValuePair("personId", UserData.getPERSONID()));
			params.add(new BasicNameValuePair("eventId", EventData.getEVENTID()));

			// Send the HTTPS request using GET request
			JSONObject json = jsonParser.makeHttpsRequest(IUniformResourceLocator.URL.URL_EVENTPERSON, "GET", params, AttendingMemberController.this);
			// Log the JSON http request
			Log.d("Memberlist: ", json.toString());

			// If the JSON request is returning a success result,
			// fill the JSON array with the result,
			// iterate each JSON result item to a JSON object,
			// get each properties from the JSON object to a java string
			// set the each string properly to android custom item list,
			// put everything in the arraylist container.
			try {
				int success = json.getInt(TAG_SUCCESS);
				if (success == 1) {
					member = json.getJSONArray("member");

					for (int i = 0; i < member.length(); i++) {
						JSONObject c = member.getJSONObject(i);

						String personId = c.getString("personId");
						String firstName = c.getString("firstName");
						String lastName = c.getString("lastName");

						HashMap<String, String> map = new HashMap<String, String>();
						map.put(TAG_MEMBER_ID, personId);
						map.put(TAG_MEMBER_NAME, firstName + " " + lastName);

						memberList.add(map);
					}
				} else {

				}
			} catch (Exception e) {
				e.printStackTrace();
				pDialog.dismiss();
				logout();
			}

			return null;
		}

		protected void onPostExecute(String result) {

			// Dismiss the progress dialog after background process is finished and
			// ready
			pDialog.dismiss();

			// Set the android list adapter from a simple adapter by using the the
			// member_list_item layout and the prepared variables.
			runOnUiThread(new Runnable() {
				public void run() {
					ListAdapter adapter = new SimpleAdapter(AttendingMemberController.this, memberList,
									R.layout.member_list_item, new String[] { TAG_MEMBER_ID, TAG_MEMBER_NAME }, new int[] {
													R.id.MEMBERID, R.id.MEMBERNAME });

					// Set a list view using native android list
					final ListView memberListView = (ListView) findViewById(android.R.id.list);

					// Set the clickable item on the list
					memberListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

						@Override
						public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
							tv_memberId = (TextView) view.findViewById(R.id.MEMBERID);
							new GetPrivilegesInfo().execute();
						}
					});
					memberListView.setAdapter(adapter);
				}
			});
		}
	}

	/**
	 * Begin the background operation using asynchronous task to fetch data
	 * through the network. The GetPrivilegesInfo Class gets a member information
	 * by referring the personId using the GET request. The PHP files on the
	 * server side handle the GET request, return the result and a success marker
	 * to the client app.
	 * 
	 */
	class GetPrivilegesInfo extends AsyncTask<String, String, String> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pDialog = new ProgressDialog(AttendingMemberController.this);
			pDialog.setMessage(IMessages.Status.LOADING_INFO);
			pDialog.setIndeterminate(false);

			// If the progress is taking too long, let user cancels
			// the progress dialog by hitting the android-back-button.
			pDialog.setCancelable(true);
			pDialog.show();
		}

		protected String doInBackground(String... args) {
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair("do", "read"));
			params.add(new BasicNameValuePair("personId", tv_memberId.getText().toString()));
			JSONObject json = jsonParser.makeHttpsRequest(IUniformResourceLocator.URL.URL_PERSON, "GET", params, AttendingMemberController.this);

			Log.d("Member: ", json.toString());

			try {
				int success = json.getInt(TAG_SUCCESS);
				if (success == 1) {

					member = json.getJSONArray("person");

					// Set the properties of a member using MemberData object from the
					// JSON result
					for (int i = 0; i < member.length(); i++) {
						JSONObject c = member.getJSONObject(i);

						MemberData.setPERSONID(c.getString("personId"));
						MemberData.setEMAIL(c.getString("eMail"));
						MemberData.setFIRST_NAME(c.getString("firstName"));
						MemberData.setLAST_NAME(c.getString("lastName"));
						MemberData.setBIRTHDAY(c.getString("birthday"));
						MemberData.setGENDER(c.getString("gender"));
					}

					// Another GET request to get privileges of a member in a group.
					List<NameValuePair> paramsPrivileges = new ArrayList<NameValuePair>();
					paramsPrivileges.add(new BasicNameValuePair("do", "readUserInGroup"));
					paramsPrivileges.add(new BasicNameValuePair("groupId", GroupData.getGROUPID()));
					paramsPrivileges.add(new BasicNameValuePair("personId", MemberData.getPERSONID()));

					json = jsonParser.makeHttpsRequest(IUniformResourceLocator.URL.URL_GROUPS, "GET", paramsPrivileges, AttendingMemberController.this);

					Log.d("Member: ", json.toString());
					success = json.getInt(TAG_SUCCESS);
					if (success == 1) {
						member = json.getJSONArray("member");

						// Set the privileges of the MemberData object
						for (int i = 0; i < member.length(); i++) {
							JSONObject c = member.getJSONObject(i);

							MemberData.setMEMBER_SINCE(c.getString("memberSince"));
							MemberData.setPRIVILEGE_INVITE_MEMBER(c.getInt("memberInvitation") == 1 ? "true" : "false");
							MemberData.setPRIVILEGE_EDIT_MEMBERLIST(c.getInt("memberlistEditing") == 1 ? "true" : "false");
							MemberData.setPRIVILEGE_CREATE_EVENT(c.getInt("eventCreating") == 1 ? "true" : "false");
							MemberData.setPRIVILEGE_EDIT_EVENT(c.getInt("eventEditing") == 1 ? "true" : "false");
							MemberData.setPRIVILEGE_DELETE_EVENT(c.getInt("eventDeleting") == 1 ? "true" : "false");
							MemberData.setPRIVILEGE_EDIT_COMMENT(c.getInt("commentEditing") == 1 ? "true" : "false");
							MemberData.setPRIVILEGE_DELETE_COMMENT(c.getInt("commentDeleting") == 1 ? "true" : "false");
							MemberData.setPRIVILEGE_MANAGEMENT(c.getInt("privilegeManagement") == 1 ? "true" : "false");

						}
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
				pDialog.dismiss();
				logout();
			}
			return null;
		}

		protected void onPostExecute(String message) {
			pDialog.dismiss();

			// Close the current activity (also if an activity flow came backwards
			// using android-back-button)
			// to run the next intended controller.
			finish();

			// Send an intent from the current cotroller to the other controller class
			// where the information
			// is going to be treated.
			Intent intent = new Intent(AttendingMemberController.this, MemberPrivilegeInfoController.class);
			startActivity(intent);
		}
	}
}