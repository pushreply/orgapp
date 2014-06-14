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
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import fhkl.de.orgapp.R;
import fhkl.de.orgapp.util.IMessages;
import fhkl.de.orgapp.util.JSONParser;
import fhkl.de.orgapp.util.MenuActivity;

public class GroupController extends MenuActivity {

	// Progress Dialog
	private ProgressDialog pDialog;

	JSONParser jsonParser = new JSONParser();
	ArrayList<HashMap<String, String>> groupList;

	private static String URL_SELECT_MY_GROUP = "http://pushrply.com/select_my_group.php";

	// JSON nodes
	private static final String TAG_SUCCESS = "success";
	private static final String GROUP_ID = "GROUPID";
	private static final String PERSON_ID = "PERSONID";
	private static final String GROUP_NAME = "GROUPNAME";
	private static final String GROUP_INFO = "GROUPINFO";
	private static final String GROUP_PICTURE = "GROUP_PICTURE_URL";

	JSONArray groups = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.groups);

		groupList = new ArrayList<HashMap<String, String>>();
		new Group().execute();
	}

	class Group extends AsyncTask<String, String, String> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pDialog = new ProgressDialog(GroupController.this);

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
						String gname = c.getString("gname");
						String ginfo = c.getString("ginfo");
						String gpic = c.getString("gpicture");

						HashMap<String, String> map = new HashMap<String, String>();
						map.put(GROUP_ID, groupId);
						map.put(PERSON_ID, personId);
						map.put(GROUP_NAME, gname);
						map.put(GROUP_INFO, ginfo);
						map.put(GROUP_PICTURE, gpic);

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
					ListAdapter adapter = new SimpleAdapter(GroupController.this,
							groupList, R.layout.group_item, new String[] { GROUP_ID,
									GROUP_NAME }, new int[] { R.id.GROUPID, R.id.GROUPNAME });

					// update listview
					ListView groupList = (ListView) findViewById(android.R.id.list);
					groupList.setAdapter(adapter);
				}
			});
		}

	}

}
