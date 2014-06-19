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
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import fhkl.de.orgapp.R;
import fhkl.de.orgapp.util.GroupData;
import fhkl.de.orgapp.util.IMessages;
import fhkl.de.orgapp.util.JSONParser;
import fhkl.de.orgapp.util.MenuActivity;
import fhkl.de.orgapp.util.UserData;

public class GroupsController extends MenuActivity {

	// Progress Dialog
	private ProgressDialog pDialog;
	private String personIdLoggedPerson;

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
	private static final String TAG_GROUP_PICTURE = "GROUPPICTURE";

	JSONArray groups = null;
	JSONArray member = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.groups);

		groupList = new ArrayList<HashMap<String, String>>();
		new Groups().execute();
	}

	class Groups extends AsyncTask<String, String, String> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pDialog = new ProgressDialog(GroupsController.this);

			if (getIntent().getStringExtra("Refresh") != null)
				pDialog.setMessage(IMessages.UPDATING);
			else
				pDialog.setMessage(IMessages.LOADING_GROUPS);

			pDialog.setIndeterminate(false);
			pDialog.setCancelable(true);
			pDialog.show();
		}

		protected String doInBackground(String... params) {
			List<NameValuePair> vp = new ArrayList<NameValuePair>();
			vp.add(new BasicNameValuePair("personId", getIntent().getStringExtra(
					"UserId")));

			JSONObject json = jsonParser.makeHttpRequest(URL_SELECT_MY_GROUP, "GET",
					vp);

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
						String pic = c.getString("picture");

						HashMap<String, String> map = new HashMap<String, String>();
						map.put(TAG_GROUP_ID, groupId);
						map.put(TAG_PERSON_ID, personId);
						map.put(TAG_GROUP_NAME, name);
						map.put(TAG_GROUP_INFO, info);
						map.put(TAG_GROUP_PICTURE, pic);

						groupList.add(map);
					}
				} else {

				}
			} catch (JSONException e) {
				System.out
						.println("Error in GroupData.doInBackground(String... arg0): "
								+ e.getMessage());
				e.printStackTrace();
			}

			return null;
		}

		protected void onPostExecute(String file_url) {
			pDialog.dismiss();
			runOnUiThread(new Runnable() {
				public void run() {
					ListAdapter adapter = new SimpleAdapter(GroupsController.this,
							groupList, R.layout.groups_item, new String[] { TAG_GROUP_ID,
									TAG_PERSON_ID, TAG_GROUP_NAME, TAG_GROUP_INFO,
									TAG_GROUP_PICTURE }, new int[] { R.id.GROUPID, R.id.PERSONID,
									R.id.GROUPNAME, R.id.GROUPINFO, R.id.GROUPPICTURE });

					// update listview
					final ListView groupList = (ListView) findViewById(android.R.id.list);

					groupList
							.setOnItemClickListener(new AdapterView.OnItemClickListener() {

								@Override
								public void onItemClick(AdapterView<?> parent, View view,
										int position, long id) {
									Intent intent = new Intent(GroupsController.this,
											SingleGroupController.class);

									personIdLoggedPerson = UserData.getPERSONID();
									TextView tv_groupId = (TextView) view
											.findViewById(R.id.GROUPID);
									TextView tv_personId = (TextView) view
											.findViewById(R.id.PERSONID);
									TextView tv_groupName = (TextView) view
											.findViewById(R.id.GROUPNAME);
									TextView tv_groupInfo = (TextView) view
											.findViewById(R.id.GROUPINFO);
									TextView tv_groupPicture = (TextView) view
											.findViewById(R.id.GROUPPICTURE);

									GroupData.setGROUPID(tv_groupId.getText().toString());
									GroupData.setPERSONID(tv_personId.getText().toString());
									GroupData.setGROUPNAME(tv_groupName.getText().toString());
									GroupData.setGROUPINFO(tv_groupInfo.getText().toString());
									GroupData.setPICTURE(tv_groupPicture.getText().toString());

									new Privileges().execute();

									intent.putExtra("UserId", personIdLoggedPerson);
									intent.putExtra("GroupId", tv_groupId.getText().toString());
									intent.putExtra("GroupName", tv_groupName.getText()
											.toString());
									startActivity(intent);

								}

							});
					groupList.setAdapter(adapter);
				}
			});
		}

	}

	class Privileges extends AsyncTask<String, String, String> {

		protected String doInBackground(String... args) {
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair("groupId", GroupData.getGROUPID()));
			params.add(new BasicNameValuePair("eMail", UserData.getEMAIL()));
			JSONObject json = jsonParser.makeHttpRequest(URL_GET_USER_IN_GROUP,
					"GET", params);

			Log.d("Member: ", json.toString());

			try {
				int success = json.getInt(TAG_SUCCESS);
				if (success == 1) {
					member = json.getJSONArray("member");

					for (int i = 0; i < member.length(); i++) {
						JSONObject c = member.getJSONObject(i);

						GroupData.setPRIVILEGE_MANAGEMENT(c
								.getString("privilegeManagement"));
						GroupData.setPRIVILEGE_INVITE_MEMBER(c
								.getString("memberInvitation"));
						GroupData.setPRIVILEGE_EDIT_MEMBERLIST(c
								.getString("memberlistEditing"));
						GroupData.setPRIVILEGE_CREATE_EVENT(c.getString("eventCreating"));
						GroupData.setPRIVILEGE_EDIT_EVENT(c.getString("eventEditing"));
						GroupData.setPRIVILEGE_DELETE_EVENT(c.getString("eventDeleting"));
						GroupData.setPRIVILEGE_EDIT_COMMENT(c.getString("commentEditing"));
						GroupData.setPRIVILEGE_DELETE_COMMENT(c
								.getString("commentDeleting"));
					}
				}
			} catch (JSONException e) {
				System.out
						.println("Error in GroupData.doInBackground(String... arg0): "
								+ e.getMessage());
				e.printStackTrace();
			}

			return null;
		}
	}
}