package fhkl.de.orgapp.controller.groups;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
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
import android.widget.Toast;
import fhkl.de.orgapp.R;
import fhkl.de.orgapp.util.IMessages;
import fhkl.de.orgapp.util.JSONParser;
import fhkl.de.orgapp.util.MenuActivity;
import fhkl.de.orgapp.util.data.GroupData;
import fhkl.de.orgapp.util.data.MemberData;
import fhkl.de.orgapp.util.data.UserData;

public class MemberListController extends MenuActivity {

	private ProgressDialog pDialog;

	JSONParser jsonParser = new JSONParser();
	ArrayList<HashMap<String, String>> memberList;

	private static String URL_GET_MEMBER_LIST = "http://pushrply.com/get_member_list.php";
	private static String URL_DELETE_MEMBER = "http://pushrply.com/delete_member.php";
	private static String URL_PERSON = "http://pushrply.com/pdo_personcontrol.php";
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

		new GetMemberList().execute();
	}

	class GetMemberList extends AsyncTask<String, String, String> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pDialog = new ProgressDialog(MemberListController.this);
			pDialog.setMessage(IMessages.Status.LOADING_MEMBER_LIST);
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(true);
			pDialog.show();
		}

		protected String doInBackground(String... args) {
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair("personId", UserData.getPERSONID()));
			params.add(new BasicNameValuePair("groupId", GroupData.getGROUPID()));

			JSONObject json = jsonParser.makeHttpRequest(URL_GET_MEMBER_LIST, "GET", params);

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
			} catch (Exception e) {
				e.printStackTrace();
				logout();
			}

			return null;
		}

		protected void onPostExecute(String result) {
			pDialog.dismiss();
			runOnUiThread(new Runnable() {
				public void run() {
					ListAdapter adapter = new SimpleAdapter(MemberListController.this, memberList, R.layout.member_list_item,
									new String[] { TAG_MEMBER_ID, TAG_MEMBER_NAME }, new int[] { R.id.MEMBERID, R.id.MEMBERNAME });

					final ListView memberListView = (ListView) findViewById(android.R.id.list);

					memberListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

						@Override
						public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
							tv_memberId = (TextView) view.findViewById(R.id.MEMBERID);
							new GetPrivilegesInfo().execute();
						}
					});

					memberListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
						@Override
						public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
							if (GroupData.getPERSONID().equals(UserData.getPERSONID())
											|| GroupData.getPRIVILEGE_EDIT_MEMBERLIST().equals("1")) {
								tv_memberId = (TextView) view.findViewById(R.id.MEMBERID);
								AlertDialog.Builder builder = new AlertDialog.Builder(MemberListController.this);
								builder.setMessage(IMessages.SecurityIssue.QUESTION_DELETE_MEMBER);
								builder.setPositiveButton(IMessages.DialogButton.YES, new OnClickListener() {

									@Override
									public void onClick(DialogInterface dialog, int which) {
										new DeleteMember().execute();
									}

								});
								builder.setNegativeButton(IMessages.DialogButton.NO, new OnClickListener() {

									@Override
									public void onClick(DialogInterface dialog, int which) {
									}
								});
								builder.create().show();
								return true;

							} else {
								Toast.makeText(getApplicationContext(), IMessages.Error.INSUFFICIENT_PRIVILEGES, Toast.LENGTH_LONG)
												.show();
								return false;
							}
						}
					});

					memberListView.setAdapter(adapter);
				}
			});
		}
	}

	class DeleteMember extends AsyncTask<String, String, String> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pDialog = new ProgressDialog(MemberListController.this);
			pDialog.setMessage(IMessages.Status.REMOVING_MEMBER);
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(true);
			pDialog.show();
		}

		protected String doInBackground(String... args) {
			if (GroupData.getPERSONID().equals(tv_memberId.getText().toString())) {
				return IMessages.Error.REMOVING_ADMIN;
			}
			List<NameValuePair> params = new ArrayList<NameValuePair>();

			params.add(new BasicNameValuePair("personId", tv_memberId.getText().toString()));
			params.add(new BasicNameValuePair("groupId", GroupData.getGROUPID()));

			JSONObject json = jsonParser.makeHttpRequest(URL_DELETE_MEMBER, "GET", params);

			Log.d("Response: ", json.toString());

			try {
				int success = json.getInt(TAG_SUCCESS);
				if (success == 1) {
					Intent intent = new Intent(MemberListController.this, MemberListController.class);
					finish();
					startActivity(intent);
				}
			} catch (Exception e) {
				e.printStackTrace();
				logout();
			}

			return null;
		}

		protected void onPostExecute(String message) {
			pDialog.dismiss();

			if (message != null) {
				Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();

			}
		}
	}

	class GetPrivilegesInfo extends AsyncTask<String, String, String> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pDialog = new ProgressDialog(MemberListController.this);
			pDialog.setMessage(IMessages.Status.LOADING_INFO);
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(true);
			pDialog.show();
		}

		protected String doInBackground(String... args) {
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair("do", "read"));
			params.add(new BasicNameValuePair("personId", tv_memberId.getText().toString()));
			JSONObject json = jsonParser.makeHttpRequest(URL_PERSON, "GET", params);

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

							finish();
							Intent intent = new Intent(MemberListController.this, MemberPrivilegeInfoController.class);
							startActivity(intent);
						}
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
				logout();
			}
			return null;
		}

		protected void onPostExecute(String message) {
			pDialog.dismiss();
		}

	}
}