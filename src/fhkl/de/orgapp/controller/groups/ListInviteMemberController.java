package fhkl.de.orgapp.controller.groups;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.Toast;
import fhkl.de.orgapp.R;
import fhkl.de.orgapp.util.IMessages;
import fhkl.de.orgapp.util.JSONParser;
import fhkl.de.orgapp.util.MenuActivity;

public class ListInviteMemberController extends MenuActivity {

	private String personIdLoggedPerson;

	private ProgressDialog pDialog;
	JSONParser jsonParser = new JSONParser();
	JSONArray invite_member_list = null;
	ArrayList<HashMap<String, String>> inviteMemberList;

	private Button bInvite;
	private Button bCancel;
	CheckBox member_list_checkbox;

	LinearLayout invite_member_list_linearlayout, checkbox_layout;

	private static String URL_GET_INVITE_MEMBER_LIST = "http://pushrply.com/get_invite_member_list.php";
	private static final String TAG_SUCCESS = "success";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.invite_member_checkbox);

		personIdLoggedPerson = getIntent().getStringExtra("UserId");
		invite_member_list_linearlayout = (LinearLayout) findViewById(R.id.INVITE_MEMBER_LIST_LINEARLAYOUT);
		checkbox_layout = new LinearLayout(ListInviteMemberController.this);
		LayoutParams params = (LinearLayout.LayoutParams) checkbox_layout
				.getLayoutParams();
		params.width = LayoutParams.MATCH_PARENT;
		params.height = LayoutParams.WRAP_CONTENT;
		checkbox_layout.setLayoutParams(params);
		checkbox_layout.setOrientation(LinearLayout.VERTICAL);

		invite_member_list_linearlayout.addView(checkbox_layout);
		new GetList().execute();
	}

	class GetList extends AsyncTask<String, String, String> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pDialog = new ProgressDialog(ListInviteMemberController.this);
			pDialog.setMessage(IMessages.LOADING_LIST);
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(false);
			pDialog.show();
		}

		protected String doInBackground(String... args) {

			List<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair("personId", personIdLoggedPerson));
			JSONObject json = jsonParser.makeHttpRequest(URL_GET_INVITE_MEMBER_LIST,
					"GET", params);
			int success;
			try {
				success = json.getInt(TAG_SUCCESS);
				if (success == 1) {
					invite_member_list = json.getJSONArray("memberlist");

					for (int i = 0; i < invite_member_list.length(); i++) {
						JSONObject c = invite_member_list.getJSONObject(i);
						System.out.println(i);
						member_list_checkbox = new CheckBox(ListInviteMemberController.this);
						System.out.println(member_list_checkbox);
						LayoutParams par = (LinearLayout.LayoutParams) member_list_checkbox
								.getLayoutParams();
						par.width = LayoutParams.MATCH_PARENT;
						par.height = LayoutParams.WRAP_CONTENT;
						member_list_checkbox.setLayoutParams(par);
						// member_list_checkbox.setChecked(false);
						// member_list_checkbox.setVisibility(View.VISIBLE);
						// System.out.println(c.getString("firstName") + " "
						// + c.getString("lastName") + " " + c.getString("eMail"));
						// member_list_checkbox.setId(c.getInt("personId"));
						// member_list_checkbox.setText(c.getString("firstName") + " "
						// + c.getString("lastName") + " " + c.getString("eMail"));
						// System.out.println("test");
						// System.out.println("id");
						// System.out.println(invite_member_list_linearlayout);
						// System.out.println("layout");
						checkbox_layout.addView(member_list_checkbox);
						System.out.println("added");
					}
				} else {

				}
			} catch (Exception e) {

			}
			return null;
		}

		protected void onPostExecute(String message) {
			pDialog.dismiss();

		}
	}

	class ListInviteMembers extends AsyncTask<String, String, String> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pDialog = new ProgressDialog(ListInviteMemberController.this);
			pDialog.setMessage(IMessages.INVITING_MEMBER);
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(false);
			pDialog.show();
		}

		protected String doInBackground(String... params) {
			return null;
		}

		protected void onPostExecute(String message) {
			pDialog.dismiss();
			if (message != null) {
				Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG)
						.show();

			}
		}
	}

}
