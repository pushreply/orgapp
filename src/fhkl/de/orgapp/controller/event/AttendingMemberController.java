package fhkl.de.orgapp.controller.event;

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
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import fhkl.de.orgapp.R;
import fhkl.de.orgapp.controller.groups.MemberPrivilegeInfoController;
import fhkl.de.orgapp.util.IMessages;
import fhkl.de.orgapp.util.JSONParser;
import fhkl.de.orgapp.util.MenuActivity;
import fhkl.de.orgapp.util.data.EventData;
import fhkl.de.orgapp.util.data.GroupData;
import fhkl.de.orgapp.util.data.MemberData;
import fhkl.de.orgapp.util.data.UserData;

public class AttendingMemberController extends MenuActivity {

	private ProgressDialog pDialog;

	JSONParser jsonParser = new JSONParser();
	ArrayList<HashMap<String, String>> memberList;

	private static String URL_GET_ATTENDING_MEMBER = "http://pushrply.com/get_attending_member.php";
	private static String URL_GET_PERSON = "http://pushrply.com/get_person_by_personId.php";
	private static String URL_GET_USER_IN_GROUP = "http://pushrply.com/get_user_in_group_by_eMail.php";

	private static final String TAG_SUCCESS = "success";
	private static final String TAG_MEMBER_ID = "MEMBERID";
	private static final String TAG_MEMBER_NAME = "MEMBERNAME";

	TextView tv_memberId;

	JSONArray member = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.member_list);
		checkOnNewNotificationsAndNotifyUser();
		memberList = new ArrayList<HashMap<String, String>>();

		EventData.setBACK(true);

		new GetMemberList().execute();
	}

	class GetMemberList extends AsyncTask<String, String, String> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pDialog = new ProgressDialog(AttendingMemberController.this);
			pDialog.setMessage(IMessages.Status.LOADING_MEMBER_LIST);
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(true);
			pDialog.show();
		}

		protected String doInBackground(String... args) {
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair("personId", UserData.getPERSONID()));
			params.add(new BasicNameValuePair("eventId", EventData.getEVENTID()));

			JSONObject json = jsonParser.makeHttpRequest(URL_GET_ATTENDING_MEMBER, "GET", params);

			Log.d("Memberlist: ", json.toString());

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
			} catch (JSONException e) {
				System.out.println("Error in GetMemberList.doInBackground(String... args): " + e.getMessage());
				e.printStackTrace();
			}

			return null;
		}

		protected void onPostExecute(String result) {
			pDialog.dismiss();
			runOnUiThread(new Runnable() {
				public void run() {
					ListAdapter adapter = new SimpleAdapter(AttendingMemberController.this, memberList,
									R.layout.member_list_item, new String[] { TAG_MEMBER_ID, TAG_MEMBER_NAME }, new int[] {
													R.id.MEMBERID, R.id.MEMBERNAME });

					final ListView memberListView = (ListView) findViewById(android.R.id.list);

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

	class GetPrivilegesInfo extends AsyncTask<String, String, String> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pDialog = new ProgressDialog(AttendingMemberController.this);
			pDialog.setMessage(IMessages.Status.LOADING_INFO);
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(true);
			pDialog.show();
		}

		protected String doInBackground(String... args) {
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair("personId", tv_memberId.getText().toString()));
			JSONObject json = jsonParser.makeHttpRequest(URL_GET_PERSON, "GET", params);

			Log.d("Member: ", json.toString());

			try {
				int success = json.getInt(TAG_SUCCESS);
				if (success == 1) {

					member = json.getJSONArray("person");

					for (int i = 0; i < member.length(); i++) {
						JSONObject c = member.getJSONObject(i);

						MemberData.setPERSONID(c.getString("personId"));
						MemberData.setEMAIL(c.getString("eMail"));
						MemberData.setFIRST_NAME(c.getString("firstName"));
						MemberData.setLAST_NAME(c.getString("lastName"));
						MemberData.setBIRTHDAY(c.getString("birthday"));
						MemberData.setGENDER(c.getString("gender"));
					}

					List<NameValuePair> paramsPrivileges = new ArrayList<NameValuePair>();
					paramsPrivileges.add(new BasicNameValuePair("groupId", GroupData.getGROUPID()));
					paramsPrivileges.add(new BasicNameValuePair("eMail", MemberData.getEMAIL()));

					json = jsonParser.makeHttpRequest(URL_GET_USER_IN_GROUP, "GET", paramsPrivileges);

					Log.d("Member: ", json.toString());
					success = json.getInt(TAG_SUCCESS);
					if (success == 1) {
						member = json.getJSONArray("member");

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
			} catch (JSONException e) {
				System.out.println("Error in GetPrivilegesInfo.doInBackground(String... args): " + e.getMessage());
				e.printStackTrace();
			}
			return null;
		}

		protected void onPostExecute(String message) {
			pDialog.dismiss();

			finish();
			Intent intent = new Intent(AttendingMemberController.this, MemberPrivilegeInfoController.class);
			startActivity(intent);
		}
	}
}