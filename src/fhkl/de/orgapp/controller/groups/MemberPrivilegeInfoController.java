package fhkl.de.orgapp.controller.groups;

import java.util.ArrayList;
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
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import fhkl.de.orgapp.R;
import fhkl.de.orgapp.util.GroupData;
import fhkl.de.orgapp.util.IMessages;
import fhkl.de.orgapp.util.JSONParser;
import fhkl.de.orgapp.util.MenuActivity;
import fhkl.de.orgapp.util.UserData;

public class MemberPrivilegeInfoController extends MenuActivity {

	private ProgressDialog pDialog;

	JSONParser jsonParser = new JSONParser();

	private static String URL_GET_PERSON = "http://pushrply.com/get_person_by_personId.php";
	private static String URL_GET_USER_IN_GROUP = "http://pushrply.com/get_user_in_group_by_eMail.php";
	private static String URL_UPDATE_PRIVILEGES = "http://pushrply.com/update_privileges.php";
	private static String URL_CREATE_NOTIFICATION = "http://pushrply.com/create_notification.php";

	private static final String TAG_SUCCESS = "success";

	JSONArray member = null;

	TextView tv_eMail, tv_firstName, tv_lastName, tv_birthday, tv_gender, tv_memberSince;
	CheckBox privilegeInvitation, privilegeMemberlistEditing, privilegeEventCreating, privilegeEventEditing,
					privilegeEventDeleting, privilegeCommentEditing, privilegeCommentDeleting, privilegeManagement;
	Button bSave, bCancel;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.member_privilege_info);

		if (GroupData.getPERSONID().equals(UserData.getPERSONID()) || GroupData.getPRIVILEGE_MANAGEMENT().equals("1")) {
			LinearLayout privilege_options = (LinearLayout) findViewById(R.id.PRIVILEGE_OPTIONS);
			privilege_options.setVisibility(View.VISIBLE);

			privilegeInvitation = (CheckBox) findViewById(R.id.PRIVILEGE_INVITATION);
			privilegeMemberlistEditing = (CheckBox) findViewById(R.id.PRIVILEGE_MEMBERLIST_Editing);
			privilegeEventCreating = (CheckBox) findViewById(R.id.PRIVILEGE_EVENT_CREATING);
			privilegeEventEditing = (CheckBox) findViewById(R.id.PRIVILEGE_EVENT_EDITING);
			privilegeEventDeleting = (CheckBox) findViewById(R.id.PRIVILEGE_EVENT_DELETING);
			privilegeCommentEditing = (CheckBox) findViewById(R.id.PRIVILEGE_COMMENT_EDITING);
			privilegeCommentDeleting = (CheckBox) findViewById(R.id.PRIVILEGE_COMMENT_DELETING);
			privilegeManagement = (CheckBox) findViewById(R.id.PRIVILEGE_MANAGEMENT);
		}

		tv_eMail = (TextView) findViewById(R.id.MEMBER_EMAIL);
		tv_firstName = (TextView) findViewById(R.id.MEMBER_FIRSTNAME);
		tv_lastName = (TextView) findViewById(R.id.MEMBER_LASTNAME);
		tv_birthday = (TextView) findViewById(R.id.MEMBER_BIRTHDAY);
		tv_gender = (TextView) findViewById(R.id.MEMBER_GENDER);
		tv_memberSince = (TextView) findViewById(R.id.MEMBER_SINCE);

		bSave = (Button) findViewById(R.id.SAVE_PRIVILEGES);
		bCancel = (Button) findViewById(R.id.CANCEL);
		bSave.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {

				new SavePrivileges().execute();
			}
		});

		bCancel.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				Intent i = new Intent(MemberPrivilegeInfoController.this, MemberListController.class);
				startActivity(i);
			}
		});

		new GetPrivilegesInfo().execute();
	}

	class GetPrivilegesInfo extends AsyncTask<String, String, String> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pDialog = new ProgressDialog(MemberPrivilegeInfoController.this);
			pDialog.setMessage(IMessages.LOADING_INFO);
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(true);
			pDialog.show();
		}

		protected String doInBackground(String... args) {
			String result = null;
			String eMail = null;
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair("personId", getIntent().getStringExtra("MemberId")));

			JSONObject json = jsonParser.makeHttpRequest(URL_GET_PERSON, "GET", params);

			Log.d("Member: ", json.toString());

			try {
				int success = json.getInt(TAG_SUCCESS);
				if (success == 1) {

					member = json.getJSONArray("person");

					for (int i = 0; i < member.length(); i++) {
						JSONObject c = member.getJSONObject(i);

						result += c.getString("personId") + ", ";
						result += c.getString("eMail") + ", ";
						eMail = c.getString("eMail");
						result += c.getString("firstName") + ", ";
						result += c.getString("lastName") + ", ";
						result += c.getString("birthday") + ", ";
						result += c.getString("gender") + ", ";
					}

					List<NameValuePair> paramsPrivileges = new ArrayList<NameValuePair>();
					paramsPrivileges.add(new BasicNameValuePair("groupId", GroupData.getGROUPID()));
					paramsPrivileges.add(new BasicNameValuePair("eMail", eMail));

					json = jsonParser.makeHttpRequest(URL_GET_USER_IN_GROUP, "GET", paramsPrivileges);

					Log.d("Member: ", json.toString());
					success = json.getInt(TAG_SUCCESS);
					if (success == 1) {
						member = json.getJSONArray("member");

						for (int i = 0; i < member.length(); i++) {
							JSONObject c = member.getJSONObject(i);

							result += c.getString("memberSince") + ", ";
							result += c.getInt("memberInvitation") == 1 ? "true" + ", " : "false" + ", ";
							result += c.getInt("memberlistEditing") == 1 ? "true" + ", " : "false" + ", ";
							result += c.getInt("eventCreating") == 1 ? "true" + ", " : "false" + ", ";
							result += c.getInt("eventEditing") == 1 ? "true" + ", " : "false" + ", ";
							result += c.getInt("eventDeleting") == 1 ? "true" + ", " : "false" + ", ";
							result += c.getInt("commentEditing") == 1 ? "true" + ", " : "false" + ", ";
							result += c.getInt("commentDeleting") == 1 ? "true" + ", " : "false" + ", ";
							result += c.getInt("privilegeManagement") == 1 ? "true" : "false";
						}
					}
				}
				return result;
			} catch (JSONException e) {
				System.out.println("Error in GetPrivilegesInfo.doInBackground(String... args): " + e.getMessage());
				e.printStackTrace();
			}

			return null;
		}

		protected void onPostExecute(String result) {
			pDialog.dismiss();

			if (result == null)
				return;

			String[] datas = result.split(", ");

			setTexts(datas);
		}
	}

	private void setTexts(String[] datas) {

		tv_eMail.setText(datas[1]);
		tv_firstName.setText(datas[2]);
		tv_lastName.setText(datas[3]);
		tv_birthday.setText(datas[4]);
		tv_gender.setText(datas[5]);
		tv_memberSince.setText(datas[6]);

		if (GroupData.getPERSONID().equals(UserData.getPERSONID()) || GroupData.getPRIVILEGE_MANAGEMENT().equals("1")) {
			privilegeInvitation.setChecked(Boolean.parseBoolean(datas[7]));
			privilegeMemberlistEditing.setChecked(Boolean.parseBoolean(datas[8]));
			privilegeEventCreating.setChecked(Boolean.parseBoolean(datas[9]));
			privilegeEventEditing.setChecked(Boolean.parseBoolean(datas[10]));
			privilegeEventDeleting.setChecked(Boolean.parseBoolean(datas[11]));
			privilegeCommentEditing.setChecked(Boolean.parseBoolean(datas[12]));
			privilegeCommentDeleting.setChecked(Boolean.parseBoolean(datas[13]));
			privilegeManagement.setChecked(Boolean.parseBoolean(datas[14]));
		}
	}

	class SavePrivileges extends AsyncTask<String, String, String> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pDialog = new ProgressDialog(MemberPrivilegeInfoController.this);
			pDialog.setMessage(IMessages.SAVING_PRIVILEGES);
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(true);
			pDialog.show();
		}

		protected String doInBackground(String... args) {
			if (GroupData.getPERSONID().equals(getIntent().getStringExtra("MemberId"))) {
				return IMessages.PRIVILEGE_ADMIN;
			}
			String afterMemberInvitation = privilegeInvitation.isChecked() == true ? "1" : "0";
			String afterMemberlistEditing = privilegeMemberlistEditing.isChecked() == true ? "1" : "0";
			String afterEventCreating = privilegeEventCreating.isChecked() == true ? "1" : "0";
			String afterEventEditing = privilegeEventEditing.isChecked() == true ? "1" : "0";
			String afterEventDeleting = privilegeEventDeleting.isChecked() == true ? "1" : "0";
			String afterCommentEditing = privilegeCommentEditing.isChecked() == true ? "1" : "0";
			String afterCommentDeleting = privilegeCommentDeleting.isChecked() == true ? "1" : "0";
			String afterPrivilegeManagement = privilegeManagement.isChecked() == true ? "1" : "0";

			List<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair("groupId", GroupData.getGROUPID()));
			params.add(new BasicNameValuePair("eMail", tv_eMail.getText().toString()));

			JSONObject json = jsonParser.makeHttpRequest(URL_GET_USER_IN_GROUP, "GET", params);

			try {
				int success = json.getInt(TAG_SUCCESS);
				if (success == 1) {

					member = json.getJSONArray("member");
					String[] privilegesGiven = new String[8];
					for (int i = 0; i < member.length(); i++) {
						JSONObject c = member.getJSONObject(i);

						String beforeMemberInvitation = c.getString("memberInvitation");
						String beforeMemberlistEditing = c.getString("memberlistEditing");
						String beforeEventCreating = c.getString("eventCreating");
						String beforeEventEditing = c.getString("eventEditing");
						String beforeEventDeleting = c.getString("eventDeleting");
						String beforeCommentEditing = c.getString("commentEditing");
						String beforeCommentDeleting = c.getString("commentDeleting");
						String beforePrivilegeManagement = c.getString("privilegeManagement");
						boolean privilegeChanged = false;

						if (beforeMemberInvitation.equals(afterMemberInvitation)) {
							privilegesGiven[0] = "";
						} else {
							privilegeChanged = true;
							if (beforeMemberInvitation.equals("1") && afterMemberInvitation.equals("0")) {
								privilegesGiven[0] = "Revoked";
							} else {
								privilegesGiven[0] = "Granted";
							}
						}

						if (beforeMemberlistEditing.equals(afterMemberlistEditing)) {
							privilegesGiven[1] = "";
						} else {
							privilegeChanged = true;
							if (beforeMemberlistEditing.equals("1") && afterMemberlistEditing.equals("0")) {
								privilegesGiven[1] = "Revoked";
							} else {
								privilegesGiven[1] = "Granted";
							}
						}

						if (beforeEventCreating.equals(afterEventCreating)) {
							privilegesGiven[2] = "";
						} else {
							privilegeChanged = true;
							if (beforeEventCreating.equals("1") && afterEventCreating.equals("0")) {
								privilegesGiven[2] = "Revoked";
							} else {
								privilegesGiven[2] = "Granted";
							}
						}

						if (beforeEventEditing.equals(afterEventEditing)) {
							privilegesGiven[3] = "";
						} else {
							privilegeChanged = true;
							if (beforeEventEditing.equals("1") && afterEventEditing.equals("0")) {
								privilegesGiven[3] = "Revoked";
							} else {
								privilegesGiven[3] = "Granted";
							}
						}

						if (beforeEventDeleting.equals(afterEventDeleting)) {
							privilegesGiven[4] = "";
						} else {
							privilegeChanged = true;
							if (beforeEventDeleting.equals("1") && afterEventDeleting.equals("0")) {
								privilegesGiven[4] = "Revoked";
							} else {
								privilegesGiven[4] = "Granted";
							}
						}

						if (beforeCommentEditing.equals(afterCommentEditing)) {
							privilegesGiven[5] = "";
						} else {
							privilegeChanged = true;
							if (beforeCommentEditing.equals("1") && afterCommentEditing.equals("0")) {
								privilegesGiven[5] = "Revoked";
							} else {
								privilegesGiven[5] = "Granted";
							}
						}

						if (beforeCommentDeleting.equals(afterCommentDeleting)) {
							privilegesGiven[6] = "";
						} else {
							privilegeChanged = true;
							if (beforeCommentDeleting.equals("1") && afterCommentDeleting.equals("0")) {
								privilegesGiven[6] = "Revoked";
							} else {
								privilegesGiven[6] = "Granted";
							}
						}

						if (beforePrivilegeManagement.equals(afterPrivilegeManagement)) {
							privilegesGiven[7] = "";
						} else {
							privilegeChanged = true;
							if (beforePrivilegeManagement.equals("1") && afterPrivilegeManagement.equals("0")) {
								privilegesGiven[7] = "Revoked";
							} else {
								privilegesGiven[7] = "Granted";
							}
						}

						if (privilegeChanged == false) {
							return IMessages.NO_CHANGES_MADE;
						}
					}

					List<NameValuePair> paramsUpdate = new ArrayList<NameValuePair>();
					paramsUpdate.add(new BasicNameValuePair("personId", getIntent().getStringExtra("MemberId")));
					paramsUpdate.add(new BasicNameValuePair("groupId", GroupData.getGROUPID()));
					paramsUpdate.add(new BasicNameValuePair("memberInvitation", afterMemberInvitation));
					paramsUpdate.add(new BasicNameValuePair("memberlistEditing", afterMemberlistEditing));
					paramsUpdate.add(new BasicNameValuePair("eventCreating", afterEventCreating));
					paramsUpdate.add(new BasicNameValuePair("eventEditing", afterEventEditing));
					paramsUpdate.add(new BasicNameValuePair("eventDeleting", afterEventDeleting));
					paramsUpdate.add(new BasicNameValuePair("commentEditing", afterCommentEditing));
					paramsUpdate.add(new BasicNameValuePair("commentDeleting", afterCommentDeleting));
					paramsUpdate.add(new BasicNameValuePair("privilegeManagement", afterPrivilegeManagement));

					json = jsonParser.makeHttpRequest(URL_UPDATE_PRIVILEGES, "GET", paramsUpdate);

					Log.d("Member: ", json.toString());

					success = json.getInt(TAG_SUCCESS);
					if (success == 1) {
						List<NameValuePair> paramsNotification = new ArrayList<NameValuePair>();
						paramsNotification.add(new BasicNameValuePair("eMail", tv_eMail.getText().toString()));
						paramsNotification.add(new BasicNameValuePair("classification", "10"));
						String message = new String();
						boolean firstEntry = true;
						message += "The following privileges were changed in the group \"" + GroupData.getGROUPNAME() + "\": ";

						if (!privilegesGiven[0].isEmpty()) {
							message += privilegesGiven[0] + " member invitation right";
							firstEntry = false;
						} else {
							firstEntry = true;
						}

						if (!privilegesGiven[1].isEmpty()) {
							if (firstEntry == false) {
								message += ", ";
							}
							message += privilegesGiven[1] + " memberlist editing right";
							firstEntry = false;
						} else {
							firstEntry = true;
						}

						if (!privilegesGiven[2].isEmpty()) {
							if (firstEntry == false) {
								message += ", ";
							}
							message += privilegesGiven[2] + " event creating right";
							firstEntry = false;
						} else {
							firstEntry = true;
						}

						if (!privilegesGiven[3].isEmpty()) {
							if (firstEntry == false) {
								message += ", ";
							}
							message += privilegesGiven[3] + " event editing right";
							firstEntry = false;
						} else {
							firstEntry = true;
						}

						if (!privilegesGiven[4].isEmpty()) {
							if (firstEntry == false) {
								message += ", ";
							}
							message += privilegesGiven[4] + " event deleting right";
							firstEntry = false;
						} else {
							firstEntry = true;
						}

						if (!privilegesGiven[5].isEmpty()) {
							if (firstEntry == false) {
								message += ", ";
							}
							message += privilegesGiven[5] + " comment editing right";
							firstEntry = false;
						} else {
							firstEntry = true;
						}

						if (!privilegesGiven[6].isEmpty()) {
							if (firstEntry == false) {
								message += ", ";
							}
							message += privilegesGiven[6] + " comment deleting right";
							firstEntry = false;
						} else {
							firstEntry = true;
						}

						if (!privilegesGiven[7].isEmpty()) {
							if (firstEntry == false) {
								message += ", ";
							}
							message += privilegesGiven[7] + " privilege management right";
							firstEntry = false;
						}

						paramsNotification.add(new BasicNameValuePair("message", message));
						paramsNotification.add(new BasicNameValuePair("syncInterval", null));
						json = jsonParser.makeHttpRequest(URL_CREATE_NOTIFICATION, "GET", paramsNotification);

						success = json.getInt(TAG_SUCCESS);
						if (success == 1) {

							Intent intent = new Intent(MemberPrivilegeInfoController.this, MemberPrivilegeInfoController.class);
							intent.putExtra("MemberId", getIntent().getStringExtra("MemberId"));
							finish();
							startActivity(intent);
						}
					}
				}
			} catch (JSONException e) {
				System.out.println("Error in SavePrivileges.doInBackground(String... args): " + e.getMessage());
				e.printStackTrace();
			}

			return null;
		}

		protected void onPostExecute(String message) {
			pDialog.dismiss();

			if (message != null)
				Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();

		}
	}
}