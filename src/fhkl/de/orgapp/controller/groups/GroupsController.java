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
import fhkl.de.orgapp.util.IMessages;
import fhkl.de.orgapp.util.JSONParser;
import fhkl.de.orgapp.util.MenuActivity;

public class GroupsController extends MenuActivity {

	// Progress Dialog
	private ProgressDialog pDialog;
	private String personIdLoggedPerson;

	JSONParser jsonParser = new JSONParser();
	ArrayList<HashMap<String, String>> groupList;

	private static String URL_SELECT_MY_GROUP = "http://pushrply.com/select_my_group.php";

	// JSON nodes
	private static final String TAG_SUCCESS = "success";
	private static final String TAG_GROUP_ID = "GROUPID";
	private static final String TAG_PERSON_ID = "PERSONID";
	private static final String TAG_GROUP_NAME = "GROUPNAME";
	private static final String TAG_GROUP_INFO = "GROUPINFO";
	private static final String TAG_GROUP_PICTURE = "GROUP_PICTURE_URL";

	JSONArray groups = null;

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
			pDialog.setCancelable(false);
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
									TAG_GROUP_NAME }, new int[] { R.id.GROUPID, R.id.GROUPNAME });

					// update listview
					final ListView groupList = (ListView) findViewById(android.R.id.list);

					groupList
							.setOnItemClickListener(new AdapterView.OnItemClickListener() {

								@Override
								public void onItemClick(AdapterView<?> parent, View view,
										int position, long id) {
									Intent intent = new Intent(GroupsController.this,
											SingleGroupController.class);
									personIdLoggedPerson = getIntent().getStringExtra("UserId");
									TextView tv = (TextView) view.findViewById(R.id.GROUPID);
									String groupId = tv.getText().toString();
									TextView tv2 = (TextView) view.findViewById(R.id.GROUPNAME);
									String groupName = tv2.getText().toString();
									System.out.println(groupName);
									intent.putExtra("UserId", personIdLoggedPerson);
									intent.putExtra("GroupId", groupId);
									intent.putExtra("GroupName", groupName);
									startActivity(intent);

								}

							});
					groupList.setAdapter(adapter);
				}
			});
		}

	}

}
