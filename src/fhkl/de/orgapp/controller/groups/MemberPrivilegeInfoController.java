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
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;
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

	TextView tv_eMail, tv_firstName, tv_lastName, tv_birthday, tv_gender,
			tv_memberSince;
	CheckBox privilegeInvitation, privilegeMemberlistEditing,
			privilegeEventCreating, privilegeEventEditing, privilegeEventDeleting,
			privilegeCommentEditing, privilegeCommentDeleting, privilegeManagement;
	Button bSave, bCancel;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.member_privilege_info);

		if (GroupData.getPERSONID().equals(UserData.getPERSONID())) {
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
				Intent i = new Intent(MemberPrivilegeInfoController.this,
						MemberListController.class);
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
			params.add(new BasicNameValuePair("personId", getIntent().getStringExtra(
					"MemberId")));

			JSONObject json = jsonParser.makeHttpRequest(URL_GET_PERSON, "GET",
					params);

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
					paramsPrivileges.add(new BasicNameValuePair("groupId", GroupData
							.getGROUPID()));
					paramsPrivileges.add(new BasicNameValuePair("eMail", eMail));

					json = jsonParser.makeHttpRequest(URL_GET_USER_IN_GROUP, "GET",
							paramsPrivileges);

					Log.d("Member: ", json.toString());
					success = json.getInt(TAG_SUCCESS);
					if (success == 1) {
						member = json.getJSONArray("member");

						for (int i = 0; i < member.length(); i++) {
							JSONObject c = member.getJSONObject(i);

							result += c.getString("memberSince") + ", ";
							result += c.getInt("memberInvitation") == 1 ? "true" + ", "
									: "false" + ", ";
							result += c.getInt("memberlistEditing") == 1 ? "true" + ", "
									: "false" + ", ";
							result += c.getInt("eventCreating") == 1 ? "true" + ", "
									: "false" + ", ";
							result += c.getInt("eventEditing") == 1 ? "true" + ", " : "false"
									+ ", ";
							result += c.getInt("eventDeleting") == 1 ? "true" + ", "
									: "false" + ", ";
							result += c.getInt("commentEditing") == 1 ? "true" + ", "
									: "false" + ", ";
							result += c.getInt("commentDeleting") == 1 ? "true" + ", "
									: "false" + ", ";
							result += c.getInt("privilegeManagement") == 1 ? "true" : "false";
						}
					}
				}
				return result;
			} catch (JSONException e) {
				System.out
						.println("Error in GetPrivilegesInfo.doInBackground(String... args): "
								+ e.getMessage());
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

		if (GroupData.getPERSONID().equals(UserData.getPERSONID())) {
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
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair("personId", getIntent().getStringExtra(
					"MemberId")));
			params.add(new BasicNameValuePair("groupId", GroupData.getGROUPID()));
			params.add(new BasicNameValuePair("memberInvitation", privilegeInvitation
					.isChecked() == true ? "1" : "0"));
			params.add(new BasicNameValuePair("memberlistEditing",
					privilegeMemberlistEditing.isChecked() == true ? "1" : "0"));
			params.add(new BasicNameValuePair("eventCreating", privilegeEventCreating
					.isChecked() == true ? "1" : "0"));
			params.add(new BasicNameValuePair("eventEditing", privilegeEventEditing
					.isChecked() == true ? "1" : "0"));
			params.add(new BasicNameValuePair("eventDeleting", privilegeEventDeleting
					.isChecked() == true ? "1" : "0"));
			params.add(new BasicNameValuePair("commentEditing",
					privilegeCommentEditing.isChecked() == true ? "1" : "0"));
			params.add(new BasicNameValuePair("commentDeleting",
					privilegeCommentDeleting.isChecked() == true ? "1" : "0"));
			params.add(new BasicNameValuePair("privilegeManagement",
					privilegeManagement.isChecked() == true ? "1" : "0"));

			JSONObject json = jsonParser.makeHttpRequest(URL_UPDATE_PRIVILEGES,
					"GET", params);

			Log.d("Member: ", json.toString());

			try {
				int success = json.getInt(TAG_SUCCESS);
				if (success == 1) {
					Resources res = getResources();
					List<NameValuePair> paramsNotification = new ArrayList<NameValuePair>();
					paramsNotification.add(new BasicNameValuePair("eMail", tv_eMail
							.getText().toString()));
					paramsNotification.add(new BasicNameValuePair("classification", ""));
					String message = new String();
					boolean firstEntry = false;
					message += "You were granted the following rights in "
							+ GroupData.getGROUPNAME() + ": ";

					if (privilegeInvitation.isChecked() == true) {
						message += res.getString(R.string.PRIVILEGE_INVITATION);
						firstEntry = true;
					}

					if (privilegeMemberlistEditing.isChecked() == true) {
						if (firstEntry == true) {
							message += ", "
									+ res.getString(R.string.PRIVILEGE_MEMBERLIST_Editing);
						} else {
							message += res.getString(R.string.PRIVILEGE_MEMBERLIST_Editing);
							firstEntry = true;
						}
					}

					if (privilegeEventCreating.isChecked() == true) {
						if (firstEntry == true) {
							message += ", "
									+ res.getString(R.string.PRIVILEGE_EVENT_CREATING);
						} else {
							message += res.getString(R.string.PRIVILEGE_EVENT_CREATING);
							firstEntry = true;
						}
					}

					if (privilegeEventEditing.isChecked() == true) {
						if (firstEntry == true) {
							message += ", " + res.getString(R.string.PRIVILEGE_EVENT_EDITING);
						} else {
							message += res.getString(R.string.PRIVILEGE_EVENT_EDITING);
							firstEntry = true;
						}
					}

					if (privilegeEventDeleting.isChecked() == true) {
						if (firstEntry == true) {
							message += ", "
									+ res.getString(R.string.PRIVILEGE_EVENT_DELETING);
						} else {
							message += res.getString(R.string.PRIVILEGE_EVENT_DELETING);
							firstEntry = true;
						}
					}

					if (privilegeCommentEditing.isChecked() == true) {
						if (firstEntry == true) {
							message += ", "
									+ res.getString(R.string.PRIVILEGE_COMMENT_EDITING);
						} else {
							message += res.getString(R.string.PRIVILEGE_COMMENT_EDITING);
							firstEntry = true;
						}
					}

					if (privilegeCommentDeleting.isChecked() == true) {
						if (firstEntry == true) {
							message += ", "
									+ res.getString(R.string.PRIVILEGE_COMMENT_DELETING);
						} else {
							message += res.getString(R.string.PRIVILEGE_COMMENT_DELETING);
							firstEntry = true;
						}
					}

					if (privilegeManagement.isChecked() == true) {
						if (firstEntry == true) {
							message += ", " + res.getString(R.string.PRIVILEGE_MANAGEMENT);
						} else {
							message += res.getString(R.string.PRIVILEGE_MANAGEMENT);
						}
					}

					paramsNotification.add(new BasicNameValuePair("message", message));
					paramsNotification.add(new BasicNameValuePair("syncInterval", null));
					json = jsonParser.makeHttpRequest(URL_CREATE_NOTIFICATION, "GET",
							paramsNotification);

					success = json.getInt(TAG_SUCCESS);
					if (success == 1) {

						Intent intent = new Intent(MemberPrivilegeInfoController.this,
								MemberPrivilegeInfoController.class);
						intent.putExtra("MemberId", getIntent().getStringExtra("MemberId"));
						finish();
						startActivity(intent);
					}
				}
			} catch (JSONException e) {
				System.out
						.println("Error in SavePrivileges.doInBackground(String... args): "
								+ e.getMessage());
				e.printStackTrace();
			}

			return null;
		}

		protected void onPostExecute(String result) {
			pDialog.dismiss();
		}
	}
}