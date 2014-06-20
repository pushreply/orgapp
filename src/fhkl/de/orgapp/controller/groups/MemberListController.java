package fhkl.de.orgapp.controller.groups;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
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
import fhkl.de.orgapp.util.GroupData;
import fhkl.de.orgapp.util.IMessages;
import fhkl.de.orgapp.util.JSONParser;
import fhkl.de.orgapp.util.MenuActivity;
import fhkl.de.orgapp.util.UserData;

public class MemberListController extends MenuActivity {

	private ProgressDialog pDialog;

	JSONParser jsonParser = new JSONParser();
	ArrayList<HashMap<String, String>> memberList;

	private static String URL_GET_MEMBER_LIST = "http://pushrply.com/get_member_list.php";
	private static String URL_DELETE_MEMBER = "http://pushrply.com/delete_member.php";

	private static final String TAG_SUCCESS = "success";
	private static final String TAG_MEMBER_ID = "MEMBERID";
	private static final String TAG_MEMBER_NAME = "MEMBERNAME";

	TextView tv_memberId;

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
			} catch (JSONException e) {
				System.out.println("Error in MemberListData.doInBackground(String... args): " + e.getMessage());
				e.printStackTrace();
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
							Intent intent = new Intent(MemberListController.this, MemberPrivilegeInfoController.class);
							tv_memberId = (TextView) view.findViewById(R.id.MEMBERID);
							intent.putExtra("MemberId", tv_memberId.getText().toString());
							startActivity(intent);

						}

					});

					memberListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
						@Override
						public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
							if (GroupData.getPERSONID().equals(UserData.getPERSONID())
											|| GroupData.getPRIVILEGE_EDIT_MEMBERLIST().equals("1")) {
								tv_memberId = (TextView) view.findViewById(R.id.MEMBERID);
								AlertDialog.Builder builder = new AlertDialog.Builder(MemberListController.this);
								builder.setMessage(IMessages.QUESTION_DELETE_MEMBER);
								builder.setPositiveButton(IMessages.YES, new OnClickListener() {

									@Override
									public void onClick(DialogInterface dialog, int which) {
										new DeleteMember().execute();
									}

								});
								builder.setNegativeButton(IMessages.NO, new OnClickListener() {

									@Override
									public void onClick(DialogInterface dialog, int which) {
									}
								});
								builder.create().show();
								return true;

							} else {
								Toast.makeText(getApplicationContext(), IMessages.INSUFFICIENT_PRIVILEGES, Toast.LENGTH_LONG).show();
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
			pDialog.setMessage(IMessages.REMOVING_MEMBER);
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(true);
			pDialog.show();
		}

		protected String doInBackground(String... args) {
			if (GroupData.getPERSONID().equals(tv_memberId.getText().toString())) {
				return IMessages.REMOVING_ADMIN;
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
			} catch (JSONException e) {
				System.out.println("Error in DeleteMember.doInBackground(String... args): " + e.getMessage());
				e.printStackTrace();
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
}