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
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;
import fhkl.de.orgapp.R;
import fhkl.de.orgapp.util.IMessages;
import fhkl.de.orgapp.util.IUniformResourceLocator;
import fhkl.de.orgapp.util.JSONParser;
import fhkl.de.orgapp.util.MenuActivity;
import fhkl.de.orgapp.util.data.GroupData;
import fhkl.de.orgapp.util.data.MemberData;
import fhkl.de.orgapp.util.data.UserData;

/**
 * MemberListController - Handles the member list activity.
 * 
 * Shows current attending members or group members. Gives the option to delete
 * a member onLongItemClicked. Shows user information onItemClicked
 * 
 * @author Jochen Jung
 * @version 3.9
 */
public class MemberListController extends MenuActivity {
	private ProgressDialog pDialog;

	JSONParser jsonParser = new JSONParser();
	ArrayList<HashMap<String, String>> memberList;

	private static final String TAG_SUCCESS = "success";
	private static final String TAG_MEMBER_ID = "MEMBERID";
	private static final String TAG_MEMBER_NAME = "MEMBERNAME";

	TextView tv_memberId;

	JSONArray member = null;

	/**
	 * Initializes and loads view.
	 * 
	 * @param savedInstanceState Bundle
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.member_list);
		checkOnNewNotificationsAndNotifyUser();
		memberList = new ArrayList<HashMap<String, String>>();

		new GetMemberList().execute();
	}

	/**
	 * Async class that returns member data. Defines onItemLongClickedListener.
	 * Defines onItemClickedListener.
	 * 
	 */
	class GetMemberList extends AsyncTask<String, String, String> {

		/**
		 * Creates ProgressDialog
		 */
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pDialog = new ProgressDialog(MemberListController.this);
			pDialog.setMessage(IMessages.Status.LOADING_MEMBER_LIST);
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(true);
			pDialog.show();
		}

		/**
		 * Gets the member data.
		 * 
		 * @param args String...
		 * @return String result
		 */
		protected String doInBackground(String... args) {
			List<NameValuePair> params = new ArrayList<NameValuePair>();

			// Required parameters
			params.add(new BasicNameValuePair("do", "readMemberList"));
			params.add(new BasicNameValuePair("personId", UserData.getPERSONID()));
			params.add(new BasicNameValuePair("groupId", GroupData.getGROUPID()));

			// Get member list
			JSONObject json = jsonParser.makeHttpsRequest(IUniformResourceLocator.URL.URL_GROUPS, "GET", params,
							MemberListController.this);

			try {
				int success = json.getInt(TAG_SUCCESS);
				if (success == 1) {
					member = json.getJSONArray("member");

					for (int i = 0; i < member.length(); i++) {
						JSONObject c = member.getJSONObject(i);

						String personId = c.getString("personId");
						String firstName = c.getString("firstName");
						String lastName = c.getString("lastName");

						// Load member into HashMap
						HashMap<String, String> map = new HashMap<String, String>();
						map.put(TAG_MEMBER_ID, personId);
						map.put(TAG_MEMBER_NAME, firstName + " " + lastName);

						// Add member to ArrayList
						memberList.add(map);
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
				pDialog.dismiss();
				logout();
			}

			return null;
		}

		/**
		 * Removes ProgressDialog. Initializes and loads the ListView. Gives option
		 * to delete user onItemLongClicked. Shows user information onItemClicked in
		 * new activity.
		 * 
		 * @param result String
		 */
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
							// Async class that saves MemberData and starts new activity
							new GetPrivilegesInfo().execute();
						}
					});

					memberListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
						@Override
						public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
							// Open AlertDialog only when user has required privileges
							if (GroupData.getPERSONID().equals(UserData.getPERSONID())
											|| GroupData.getPRIVILEGE_EDIT_MEMBERLIST().equals("1")) {
								tv_memberId = (TextView) view.findViewById(R.id.MEMBERID);
								AlertDialog.Builder builder = new AlertDialog.Builder(MemberListController.this);
								builder.setMessage(IMessages.SecurityIssue.QUESTION_DELETE_MEMBER);
								builder.setPositiveButton(IMessages.DialogButton.YES, new OnClickListener() {

									@Override
									public void onClick(DialogInterface dialog, int which) {
										// Async class that deletes the selected member
										new DeleteMember().execute();
									}

								});
								builder.setNegativeButton(IMessages.DialogButton.NO, new OnClickListener() {

									@Override
									public void onClick(DialogInterface dialog, int which) {
										// Close AlertDialog
									}
								});
								builder.create().show();
								return true;

							} else {
								// Show warning, user has not required privileges
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

	/**
	 * Async class that deletes selected member.
	 * 
	 */
	class DeleteMember extends AsyncTask<String, String, String> {

		/**
		 * Creates ProgressDialog
		 */
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pDialog = new ProgressDialog(MemberListController.this);
			pDialog.setMessage(IMessages.Status.REMOVING_MEMBER);
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(true);
			pDialog.show();
		}

		/**
		 * Deletes selected member from group. Returns to refreshed member list.
		 */
		protected String doInBackground(String... args) {
			// Can not remove an admin
			if (GroupData.getPERSONID().equals(tv_memberId.getText().toString())) {
				return IMessages.Error.REMOVING_ADMIN;
			}
			List<NameValuePair> params = new ArrayList<NameValuePair>();

			// Required parameters
			params.add(new BasicNameValuePair("do", "deletePrivilege"));
			params.add(new BasicNameValuePair("personId", tv_memberId.getText().toString()));
			params.add(new BasicNameValuePair("groupId", GroupData.getGROUPID()));
			// Deletes selected member from group
			JSONObject json = jsonParser.makeHttpsRequest(IUniformResourceLocator.URL.URL_PRIVILEGE, "GET", params,
							MemberListController.this);

			try {
				int success = json.getInt(TAG_SUCCESS);
				if (success == 1) {
					Intent intent = new Intent(MemberListController.this, MemberListController.class);
					finish();
					startActivity(intent);
				}
			} catch (Exception e) {
				e.printStackTrace();
				pDialog.dismiss();
				logout();
			}

			return null;
		}

		/**
		 * Removes ProgressDialog. Shows warning when selected member is admin.
		 * 
		 * @param message String
		 */
		protected void onPostExecute(String message) {
			pDialog.dismiss();

			if (message != null) {
				Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
			}
		}
	}

	/**
	 * Asnyc class that sets MemberData and starts new activity.
	 * 
	 */
	class GetPrivilegesInfo extends AsyncTask<String, String, String> {

		/**
		 * Creates ProgressDialog
		 */
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pDialog = new ProgressDialog(MemberListController.this);
			pDialog.setMessage(IMessages.Status.LOADING_INFO);
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(true);
			pDialog.show();
		}

		/**
		 * Loads member data from database and sets MemberData.
		 * 
		 * @param args String...
		 * @return String result
		 */
		protected String doInBackground(String... args) {
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair("do", "read"));
			params.add(new BasicNameValuePair("personId", tv_memberId.getText().toString()));
			// Get person data
			JSONObject json = jsonParser.makeHttpsRequest(IUniformResourceLocator.URL.URL_PERSON, "GET", params,
							MemberListController.this);

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

					// Required parameters
					paramsPrivileges.add(new BasicNameValuePair("do", "readUserInGroup"));
					paramsPrivileges.add(new BasicNameValuePair("groupId", GroupData.getGROUPID()));
					paramsPrivileges.add(new BasicNameValuePair("personId", MemberData.getPERSONID()));
					// Get group data and privileges
					json = jsonParser.makeHttpsRequest(IUniformResourceLocator.URL.URL_GROUPS, "GET", paramsPrivileges,
									MemberListController.this);

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
				pDialog.dismiss();
				logout();
			}
			return null;
		}

		/**
		 * Removes ProgressDialog
		 * 
		 * @param message String
		 */
		protected void onPostExecute(String message) {
			pDialog.dismiss();
		}

	}
}