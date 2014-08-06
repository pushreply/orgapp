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
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import fhkl.de.orgapp.R;
import fhkl.de.orgapp.controller.event.CreateEventController;
import fhkl.de.orgapp.util.IMessages;
import fhkl.de.orgapp.util.IUniformResourceLocator;
import fhkl.de.orgapp.util.JSONParser;
import fhkl.de.orgapp.util.MenuActivity;
import fhkl.de.orgapp.util.data.EventData;
import fhkl.de.orgapp.util.data.GroupData;
import fhkl.de.orgapp.util.data.UserData;

/**
 * GroupsController - Handles the groups activity
 * 
 * Loads a list of one user's groups. Starts new Activity ''SingleGroup'' on
 * ItemClick. Opens a group menu on ItemLongClick.
 * 
 * @author Jochen Jung
 * @version 1.0
 */
public class GroupsController extends MenuActivity {
	private ProgressDialog pDialog;

	JSONParser jsonParser = new JSONParser();
	ArrayList<HashMap<String, String>> groupList;

	private static final String TAG_SUCCESS = "success";
	private static final String TAG_GROUP_ID = "GROUPID";
	private static final String TAG_PERSON_ID = "PERSONID";
	private static final String TAG_GROUP_NAME = "GROUPNAME";
	private static final String TAG_GROUP_INFO = "GROUPINFO";

	JSONArray groups = null;
	JSONArray member = null;

	/**
	 * Calls the async class to show and manage groups.
	 * 
	 * @param savedInstanceState Bundle
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.groups);
		checkOnNewNotificationsAndNotifyUser();
		groupList = new ArrayList<HashMap<String, String>>();
		new Groups().execute();
	}

	/**
	 * Async class that loads one user's groups. Handles ItemClick and
	 * ItemLongClick.
	 * 
	 * @author Jochen Jung
	 * @version 1.0
	 */
	class Groups extends AsyncTask<String, String, String> {

		/**
		 * Creates ProcessDialog
		 */
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pDialog = new ProgressDialog(GroupsController.this);

			if (getIntent().getStringExtra("Refresh") != null)
				pDialog.setMessage(IMessages.Status.UPDATING);
			else
				pDialog.setMessage(IMessages.Status.LOADING_GROUPS);

			pDialog.setIndeterminate(false);
			pDialog.setCancelable(true);
			pDialog.show();
		}

		/**
		 * Loads one user's groups.
		 * 
		 * @param params String...
		 * @return String result
		 */
		protected String doInBackground(String... params) {
			List<NameValuePair> vp = new ArrayList<NameValuePair>();

			vp.add(new BasicNameValuePair("do", "readUserGroup"));
			vp.add(new BasicNameValuePair("personId", UserData.getPERSONID()));

			// Get groups of the user
			JSONObject json = jsonParser.makeHttpRequest(IUniformResourceLocator.URL.URL_GROUPS, "GET", vp,
							GroupsController.this);

			Log.d("Groups: ", json.toString());

			try {
				int success = json.getInt(TAG_SUCCESS);
				if (success == 1) {
					groups = json.getJSONArray("groups");

					for (int i = 0; i < groups.length(); i++) {
						JSONObject c = groups.getJSONObject(i);

						String groupId = c.getString("groupId");
						String personId = c.getString("personId");
						String name = c.getString("name");
						String info = c.getString("info");

						// Load groups into haspmap
						HashMap<String, String> map = new HashMap<String, String>();
						map.put(TAG_GROUP_ID, groupId);
						map.put(TAG_PERSON_ID, personId);
						map.put(TAG_GROUP_NAME, name);
						map.put(TAG_GROUP_INFO, info);

						// Add groups to arraylist
						groupList.add(map);
					}
				}
			} catch (Exception e) {
				pDialog.dismiss();
				e.printStackTrace();
				logout();
			}

			return null;
		}

		/**
		 * Removes ProcessDialog. Sets ListView. Defines ClickListener.
		 * 
		 * @param result String
		 */
		protected void onPostExecute(String result) {
			pDialog.dismiss();
			runOnUiThread(new Runnable() {
				public void run() {
					ListAdapter adapter = new SimpleAdapter(GroupsController.this, groupList, R.layout.groups_item, new String[] {
									TAG_GROUP_ID, TAG_PERSON_ID, TAG_GROUP_NAME, TAG_GROUP_INFO }, new int[] { R.id.GROUPID,
									R.id.PERSONID, R.id.GROUPNAME, R.id.GROUPINFO });

					final ListView groupList = (ListView) findViewById(android.R.id.list);

					groupList.setOnItemClickListener(new AdapterView.OnItemClickListener() {

						@Override
						public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

							// Set the group data
							setGroupData(view);
							// Call Async class that loads the privileges into GroupData.
							// Open new activity ''SingleGroup''
							new SetPrivileges().execute();
						}

					});

					groupList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

						@Override
						public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

							// Set the group data
							setGroupData(view);
							// Call Async class that loads the privileges into GroupData. Open
							// the group menu.
							new SetPrivileges().execute("menu");

							return true;
						}
					});
					groupList.setAdapter(adapter);
				}
			});
		}

	}

	/**
	 * Loads privileges into GroupData. Open new activity ''SingleGroup''
	 * OnItemClick. Open the group menu OnItemLongClick.
	 * 
	 * @author Jochen Jung
	 * @version 1.0
	 */
	class SetPrivileges extends AsyncTask<String, String, String> {

		/**
		 * Gets privileges. Saves privileges in GroupData.
		 * 
		 * @param args String...
		 * @return String
		 */
		protected String doInBackground(String... args) {
			List<NameValuePair> params = new ArrayList<NameValuePair>();

			params.add(new BasicNameValuePair("do", "readUserInGroup"));
			params.add(new BasicNameValuePair("groupId", GroupData.getGROUPID()));
			params.add(new BasicNameValuePair("personId", UserData.getPERSONID()));

			// Get current user's privileges
			JSONObject json = jsonParser.makeHttpRequest(IUniformResourceLocator.URL.URL_GROUPS, "GET", params,
							GroupsController.this);

			Log.d("Member: ", json.toString());

			try {
				int success = json.getInt(TAG_SUCCESS);
				if (success == 1) {
					member = json.getJSONArray("member");

					for (int i = 0; i < member.length(); i++) {
						JSONObject c = member.getJSONObject(i);

						// Set privileges
						GroupData.setPRIVILEGE_MANAGEMENT(c.getString("privilegeManagement"));
						GroupData.setPRIVILEGE_INVITE_MEMBER(c.getString("memberInvitation"));
						GroupData.setPRIVILEGE_EDIT_MEMBERLIST(c.getString("memberlistEditing"));
						GroupData.setPRIVILEGE_CREATE_EVENT(c.getString("eventCreating"));
						GroupData.setPRIVILEGE_EDIT_EVENT(c.getString("eventEditing"));
						GroupData.setPRIVILEGE_DELETE_EVENT(c.getString("eventDeleting"));
						GroupData.setPRIVILEGE_EDIT_COMMENT(c.getString("commentEditing"));
						GroupData.setPRIVILEGE_DELETE_COMMENT(c.getString("commentDeleting"));
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
				logout();
			}

			// Differentiate OnItemClick and OnItemLongClick
			if (args.length != 0) {
				return args[0];
			} else {
				return null;
			}
		}

		/**
		 * Opens group menu when result not null. Opens new activity ''SingleGroup''
		 * when result null.
		 * 
		 * @param result String
		 */
		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			// Group menu
			if (result != null) {

				EventData.setBACK(false);

				// Define own view for AlertDialog
				LinearLayout ll = new LinearLayout(GroupsController.this);
				ll.setOrientation(LinearLayout.VERTICAL);

				// Check all privileges. Add the group menu button only if privilege
				// available
				if (GroupData.getPERSONID().equals(UserData.getPERSONID()) || GroupData.getPRIVILEGE_CREATE_EVENT().equals("1")) {

					// Create button
					Button createEvent = new Button(GroupsController.this);
					createEvent.setText(getResources().getString(R.string.CREATE_EVENT));

					// Set ClickListener
					createEvent.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View v) {

							Intent intent = new Intent(GroupsController.this, CreateEventController.class);
							startActivity(intent);
						}
					});

					// Add button to view
					ll.addView(createEvent);
				}

				if (GroupData.getPERSONID().equals(UserData.getPERSONID())) {

					Button editGroup = new Button(GroupsController.this);
					editGroup.setText(getResources().getString(R.string.EDIT_GROUP));

					editGroup.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View v) {

							Intent intent = new Intent(GroupsController.this, EditGroupController.class);
							startActivity(intent);
						}
					});

					ll.addView(editGroup);

					Button deleteGroup = new Button(GroupsController.this);
					deleteGroup.setText(getResources().getString(R.string.DELETE_GROUP));

					deleteGroup.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View v) {

							AlertDialog.Builder builder = new AlertDialog.Builder(GroupsController.this);
							AlertDialog leavedialog;
							builder.setMessage(IMessages.SecurityIssue.MESSAGE_DELETE_GROUP + GroupData.getGROUPNAME()
											+ IMessages.SecurityIssue.QUESTION_MARK);

							builder.setPositiveButton(IMessages.DialogButton.YES, new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog, int which) {
									dialog.dismiss();
									startActivity(new Intent(GroupsController.this, DeleteGroupController.class));
								}
							});

							builder.setNegativeButton(IMessages.DialogButton.NO, new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog, int which) {
									dialog.dismiss();
								}
							});

							leavedialog = builder.create();

							leavedialog.show();
						}
					});

					ll.addView(deleteGroup);
				} else {

					Button leaveGroup = new Button(GroupsController.this);
					leaveGroup.setText(getResources().getString(R.string.LEAVE_GROUP));

					leaveGroup.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View v) {

							AlertDialog.Builder builder = new AlertDialog.Builder(GroupsController.this);
							AlertDialog dialog;
							builder.setMessage(IMessages.SecurityIssue.CONFIRM_LEAVING_GROUP + GroupData.getGROUPNAME()
											+ IMessages.SecurityIssue.QUESTION_MARK);

							builder.setPositiveButton(IMessages.DialogButton.YES, new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog, int which) {
									dialog.dismiss();
									startActivity(new Intent(GroupsController.this, LeaveGroupController.class));
								}
							});

							builder.setNegativeButton(IMessages.DialogButton.NO, new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog, int which) {
									dialog.dismiss();
								}
							});

							dialog = builder.create();

							dialog.show();
						}
					});

					ll.addView(leaveGroup);
				}

				Button showMemberList = new Button(GroupsController.this);
				showMemberList.setText(getResources().getString(R.string.SHOW_MEMBER_LIST));

				showMemberList.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {

						new MemberList().execute();
					}
				});

				ll.addView(showMemberList);

				if (GroupData.getPERSONID().equals(UserData.getPERSONID())
								|| GroupData.getPRIVILEGE_INVITE_MEMBER().equals("1")) {

					Button inviteMember = new Button(GroupsController.this);
					inviteMember.setText(getResources().getString(R.string.INVITE_MEMBER));

					inviteMember.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View v) {

							AlertDialog.Builder builder = new AlertDialog.Builder(GroupsController.this);
							builder.setMessage(IMessages.SecurityIssue.QUESTION_MEMBER);
							builder.setPositiveButton(IMessages.DialogButton.LIST, new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog, int which) {
									Intent intent = new Intent(GroupsController.this, ListInviteMemberController.class);
									dialog.dismiss();
									finish();
									startActivity(intent);
								}

							});
							builder.setNegativeButton(IMessages.DialogButton.MANUALLY, new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog, int which) {
									Intent intent = new Intent(GroupsController.this, ManualInviteMemberController.class);
									dialog.dismiss();
									finish();
									startActivity(intent);
								}
							});
							builder.setNeutralButton(IMessages.DialogButton.CANCEL, new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog, int which) {
									dialog.dismiss();
								}
							});
							builder.create().show();
						}
					});

					ll.addView(inviteMember);
				}

				// Create AlertDialog
				AlertDialog.Builder builder = new AlertDialog.Builder(GroupsController.this);
				builder.setTitle(getResources().getString(R.string.GROUP_SETTINGS));
				// Use LinearLayout with added buttons
				builder.setView(ll);

				builder.create().show();

			}
			// Start new activity ''SingleGroup''
			else {
				Intent intent = new Intent(GroupsController.this, SingleGroupController.class);
				startActivity(intent);
			}
		}
	}

	/**
	 * Sets GroupData with clicked ListView item.
	 * 
	 * @param view View
	 */
	private void setGroupData(View view) {

		TextView tv_groupId = (TextView) view.findViewById(R.id.GROUPID);
		TextView tv_personId = (TextView) view.findViewById(R.id.PERSONID);
		TextView tv_groupName = (TextView) view.findViewById(R.id.GROUPNAME);
		TextView tv_groupInfo = (TextView) view.findViewById(R.id.GROUPINFO);

		GroupData.setGROUPID(tv_groupId.getText().toString());
		GroupData.setPERSONID(tv_personId.getText().toString());
		GroupData.setGROUPNAME(tv_groupName.getText().toString());
		GroupData.setGROUPINFO(tv_groupInfo.getText().toString());
	}
}