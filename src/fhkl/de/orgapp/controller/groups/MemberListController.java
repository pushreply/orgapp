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

public class MemberListController extends MenuActivity {

	private ProgressDialog pDialog;
	private String personIdLoggedPerson;

	JSONParser jsonParser = new JSONParser();
	ArrayList<HashMap<String, String>> memberList;

	private static String URL_GET_MEMBER_LIST = "http://pushrply.com/get_member_list.php";

	private static final String TAG_SUCCESS = "success";
	private static final String TAG_PERSON_NAME = "PERSONNAME";

	JSONArray member = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.member_list);

		memberList = new ArrayList<HashMap<String, String>>();
		new MemberList().execute();
	}

	class MemberList extends AsyncTask<String, String, String> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pDialog = new ProgressDialog(MemberListController.this);
			pDialog.setMessage(IMessages.LOADING_MEMBER_LIST);
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(true);
			pDialog.show();
		}

		protected String doInBackground(String... args) {
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair("groupId", getIntent().getStringExtra(
					"GroupId")));

			JSONObject json = jsonParser.makeHttpRequest(URL_GET_MEMBER_LIST, "GET",
					params);

			Log.d("Memberlist: ", json.toString());

			try {
				int success = json.getInt(TAG_SUCCESS);
				if (success == 1) {
					member = json.getJSONArray("member");
					System.out.println(member);

					for (int i = 0; i < member.length(); i++) {
						JSONObject c = member.getJSONObject(i);

						String personId = c.getString("personId");
						String firstName = c.getString("firstName");
						String lastName = c.getString("lastName");

						HashMap<String, String> map = new HashMap<String, String>();
						map.put(TAG_PERSON_NAME, firstName + " " + lastName);

						memberList.add(map);
					}
				} else {

				}
			} catch (JSONException e) {
				System.out
						.println("Error in MemberListData.doInBackground(String... args): "
								+ e.getMessage());
				e.printStackTrace();
			}

			return null;
		}

		protected void onPostExecute(String file_url) {
			pDialog.dismiss();
			runOnUiThread(new Runnable() {
				public void run() {
					ListAdapter adapter = new SimpleAdapter(MemberListController.this,
							memberList, R.layout.member_list_item,
							new String[] { TAG_PERSON_NAME }, new int[] { R.id.PERSONNAME });

					// update listview
					final ListView memberListView = (ListView) findViewById(android.R.id.list);
					//
					// groupList
					// .setOnItemClickListener(new AdapterView.OnItemClickListener() {
					//
					// @Override
					// public void onItemClick(AdapterView<?> parent, View view,
					// int position, long id) {
					// Intent intent = new Intent(GroupsController.this,
					// SingleGroupController.class);
					// personIdLoggedPerson = getIntent().getStringExtra("UserId");
					// TextView tv = (TextView) view.findViewById(R.id.GROUPID);
					// String groupId = tv.getText().toString();
					// TextView tv2 = (TextView) view.findViewById(R.id.GROUPNAME);
					// String groupName = tv2.getText().toString();
					// System.out.println(groupName);
					// intent.putExtra("UserId", personIdLoggedPerson);
					// intent.putExtra("GroupId", groupId);
					// intent.putExtra("GroupName", groupName);
					// startActivity(intent);
					//
					// }
					//
					// });
					memberListView.setAdapter(adapter);
				}
			});
		}

	}

}
