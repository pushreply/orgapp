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
import fhkl.de.orgapp.util.JSONParser;
import fhkl.de.orgapp.util.MenuActivity;
import fhkl.de.orgapp.util.data.EventData;
import fhkl.de.orgapp.util.data.GroupData;
import fhkl.de.orgapp.util.data.UserData;

public class GroupsController extends MenuActivity {

	// Progress Dialog
	private ProgressDialog pDialog;

	JSONParser jsonParser = new JSONParser();
	ArrayList<HashMap<String, String>> groupList;

	private static String URL_SELECT_MY_GROUP = "http://pushrply.com/select_my_group.php";
	private static String URL_GET_USER_IN_GROUP = "http://pushrply.com/get_user_in_group_by_eMail.php";

	// JSON nodes
	private static final String TAG_SUCCESS = "success";
	private static final String TAG_GROUP_ID = "GROUPID";
	private static final String TAG_PERSON_ID = "PERSONID";
	private static final String TAG_GROUP_NAME = "GROUPNAME";
	private static final String TAG_GROUP_INFO = "GROUPINFO";

	JSONArray groups = null;
	JSONArray member = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.groups);
		checkOnNewNotificationsAndNotifyUser();
		groupList = new ArrayList<HashMap<String, String>>();
		new Groups().execute();
	}

	class Groups extends AsyncTask<String, String, String> {

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

		protected String doInBackground(String... params) {
			List<NameValuePair> vp = new ArrayList<NameValuePair>();
			vp.add(new BasicNameValuePair("personId", UserData.getPERSONID()));

			JSONObject json = jsonParser.makeHttpRequest(URL_SELECT_MY_GROUP, "GET", vp);

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

						HashMap<String, String> map = new HashMap<String, String>();
						map.put(TAG_GROUP_ID, groupId);
						map.put(TAG_PERSON_ID, personId);
						map.put(TAG_GROUP_NAME, name);
						map.put(TAG_GROUP_INFO, info);

						groupList.add(map);
					}
				} else {

				}
			} catch (Exception e) {
				e.printStackTrace();
				logout();
			}

			return null;
		}

		protected void onPostExecute(String file_url) {
			pDialog.dismiss();
			runOnUiThread(new Runnable() {
				public void run() {
					ListAdapter adapter = new SimpleAdapter(GroupsController.this, groupList, R.layout.groups_item, new String[] {
									TAG_GROUP_ID, TAG_PERSON_ID, TAG_GROUP_NAME, TAG_GROUP_INFO }, new int[] { R.id.GROUPID,
									R.id.PERSONID, R.id.GROUPNAME, R.id.GROUPINFO });

					// update listview
					final ListView groupList = (ListView) findViewById(android.R.id.list);

					groupList.setOnItemClickListener(new AdapterView.OnItemClickListener() {

						@Override
						public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

							setGroupData(view);
							new SetPrivileges().execute();
						}

					});

					groupList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

						@Override
						public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

							setGroupData(view);
							new SetPrivileges().execute("menu");

							return true;
						}
					});
					groupList.setAdapter(adapter);
				}
			});
		}

	}

	class SetPrivileges extends AsyncTask<String, String, String> {

		protected String doInBackground(String... args) {
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair("groupId", GroupData.getGROUPID()));
			params.add(new BasicNameValuePair("eMail", UserData.getEMAIL()));
			JSONObject json = jsonParser.makeHttpRequest(URL_GET_USER_IN_GROUP, "GET", params);

			Log.d("Member: ", json.toString());

			try {
				int success = json.getInt(TAG_SUCCESS);
				if (success == 1) {
					member = json.getJSONArray("member");

					for (int i = 0; i < member.length(); i++) {
						JSONObject c = member.getJSONObject(i);

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

			if (args.length != 0) {
				return args[0];
			} else {
				return null;
			}
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			System.out.println("result:" + result);
			if (result != null) {

				EventData.setBACK(false);

				LinearLayout ll = new LinearLayout(GroupsController.this);
				ll.setOrientation(LinearLayout.VERTICAL);

				if (GroupData.getPERSONID().equals(UserData.getPERSONID()) || GroupData.getPRIVILEGE_CREATE_EVENT().equals("1")) {

					Button createEvent = new Button(GroupsController.this);
					createEvent.setText(getResources().getString(R.string.CREATE_EVENT));

					createEvent.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View v) {

							Intent intent = new Intent(GroupsController.this, CreateEventController.class);
							startActivity(intent);
						}
					});

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

				AlertDialog.Builder builder = new AlertDialog.Builder(GroupsController.this);
				builder.setTitle(getResources().getString(R.string.GROUP_SETTINGS));
				builder.setView(ll);

				builder.create().show();

			} else {
				Intent intent = new Intent(GroupsController.this, SingleGroupController.class);
				startActivity(intent);
			}
		}
	}

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