package fhkl.de.orgapp.controller.groups;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import fhkl.de.orgapp.R;
import fhkl.de.orgapp.controller.event.AttendingMemberController;
import fhkl.de.orgapp.util.IMessages;
import fhkl.de.orgapp.util.IUniformResourceLocator;
import fhkl.de.orgapp.util.JSONParser;
import fhkl.de.orgapp.util.MenuActivity;
import fhkl.de.orgapp.util.data.EventData;
import fhkl.de.orgapp.util.data.GroupData;
import fhkl.de.orgapp.util.data.MemberData;
import fhkl.de.orgapp.util.data.UserData;

/**
 * MemberPrivilegeInfoController - Handles the member privilege info activity.
 * 
 * Privileges Management handling. Additional member information.
 * 
 * @author Jochen Jung
 * @version 3.9
 */
public class MemberPrivilegeInfoController extends MenuActivity {
	private ProgressDialog pDialog;

	JSONParser jsonParser = new JSONParser();

	private static final String TAG_SUCCESS = "success";

	JSONArray member = null;

	TextView tv_eMail, tv_firstName, tv_lastName, tv_birthday_text_view, tv_birthday, tv_gender_text_view, tv_gender,
					tv_memberSince;
	CheckBox privilegeInvitation, privilegeMemberlistEditing, privilegeEventCreating, privilegeEventEditing,
					privilegeEventDeleting, privilegeCommentEditing, privilegeCommentDeleting, privilegeManagement;
	Button bSave, bCancel;

	/**
	 * Initializes Views. Loads member information. Loads privilege information
	 * when user is authorized to handle privilege management.
	 * 
	 * @param savedInstanceState Bundle
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.member_privilege_info);
		checkOnNewNotificationsAndNotifyUser();

		bSave = (Button) findViewById(R.id.SAVE_PRIVILEGES);
		bCancel = (Button) findViewById(R.id.CANCEL);

		// Only group admins and member with privilege management rights are allowed
		// to edit privileges
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

			privilegeInvitation.setChecked(Boolean.parseBoolean(MemberData.getPRIVILEGE_INVITE_MEMBER()));
			privilegeMemberlistEditing.setChecked(Boolean.parseBoolean(MemberData.getPRIVILEGE_EDIT_MEMBERLIST()));
			privilegeEventCreating.setChecked(Boolean.parseBoolean(MemberData.getPRIVILEGE_CREATE_EVENT()));
			privilegeEventEditing.setChecked(Boolean.parseBoolean(MemberData.getPRIVILEGE_EDIT_EVENT()));
			privilegeEventDeleting.setChecked(Boolean.parseBoolean(MemberData.getPRIVILEGE_DELETE_EVENT()));
			privilegeCommentEditing.setChecked(Boolean.parseBoolean(MemberData.getPRIVILEGE_EDIT_COMMENT()));
			privilegeCommentDeleting.setChecked(Boolean.parseBoolean(MemberData.getPRIVILEGE_DELETE_COMMENT()));
			privilegeManagement.setChecked(Boolean.parseBoolean(MemberData.getPRIVILEGE_MANAGEMENT()));

		} else {
			// Hide save button
			bSave.setVisibility(View.GONE);
		}

		tv_eMail = (TextView) findViewById(R.id.MEMBER_EMAIL);
		tv_firstName = (TextView) findViewById(R.id.MEMBER_FIRSTNAME);
		tv_lastName = (TextView) findViewById(R.id.MEMBER_LASTNAME);
		tv_birthday_text_view = (TextView) findViewById(R.id.MEMBER_BIRTHDAY_TEXT_VIEW);
		tv_birthday = (TextView) findViewById(R.id.MEMBER_BIRTHDAY);
		tv_gender_text_view = (TextView) findViewById(R.id.MEMBER_GENDER_TEXT_VIEW);
		tv_gender = (TextView) findViewById(R.id.MEMBER_GENDER);

		tv_memberSince = (TextView) findViewById(R.id.MEMBER_SINCE);

		tv_eMail.setText(MemberData.getEMAIL());
		tv_firstName.setText(MemberData.getFIRST_NAME());
		tv_lastName.setText(MemberData.getLAST_NAME());
		if (!MemberData.getBIRTHDAY().equals("null")) {
			tv_birthday.setText(MemberData.getBIRTHDAY());
		} else {
			tv_birthday_text_view.setVisibility(View.GONE);
			tv_birthday.setVisibility(View.GONE);
		}
		if (!MemberData.getGENDER().equals("null")) {
			tv_gender.setText(MemberData.getGENDER());
		} else {
			tv_gender_text_view.setVisibility(View.GONE);
			tv_gender.setVisibility(View.GONE);
		}
		tv_memberSince.setText(MemberData.getMEMBER_SINCE());

		bSave.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {

				new SavePrivileges().execute();
			}
		});

		bCancel.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				Intent intent;
				if (EventData.isBACK()) {
					EventData.setBACK(false);
					intent = new Intent(MemberPrivilegeInfoController.this, AttendingMemberController.class);
					startActivity(intent);
				} else {
					intent = new Intent(MemberPrivilegeInfoController.this, MemberListController.class);
					startActivity(intent);
				}
			}
		});
	}

	/**
	 * Async class that saves the selected privileges.
	 * 
	 */
	class SavePrivileges extends AsyncTask<String, String, String> {

		/**
		 * Creates ProgressDialog.
		 */
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pDialog = new ProgressDialog(MemberPrivilegeInfoController.this);
			pDialog.setMessage(IMessages.Status.SAVING_PRIVILEGES);
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(true);
			pDialog.show();
		}

		/**
		 * Saves selected privileges. Sends notification to member.
		 * 
		 * @param args String...
		 * @return String result
		 */
		protected String doInBackground(String... args) {
			// Can not edit admin privileges
			if (GroupData.getPERSONID().equals(MemberData.getPERSONID())) {
				return IMessages.Error.PRIVILEGE_ADMIN;
			}
			// Check the checkboxes
			String afterMemberInvitation = privilegeInvitation.isChecked() == true ? "1" : "0";
			String afterMemberlistEditing = privilegeMemberlistEditing.isChecked() == true ? "1" : "0";
			String afterEventCreating = privilegeEventCreating.isChecked() == true ? "1" : "0";
			String afterEventEditing = privilegeEventEditing.isChecked() == true ? "1" : "0";
			String afterEventDeleting = privilegeEventDeleting.isChecked() == true ? "1" : "0";
			String afterCommentEditing = privilegeCommentEditing.isChecked() == true ? "1" : "0";
			String afterCommentDeleting = privilegeCommentDeleting.isChecked() == true ? "1" : "0";
			String afterPrivilegeManagement = privilegeManagement.isChecked() == true ? "1" : "0";

			List<NameValuePair> params = new ArrayList<NameValuePair>();

			// Required parameters
			params.add(new BasicNameValuePair("do", "readUserInGroup"));
			params.add(new BasicNameValuePair("groupId", GroupData.getGROUPID()));
			params.add(new BasicNameValuePair("personId", MemberData.getPERSONID()));

			// Fetch the selected member
			JSONObject json = jsonParser.makeHttpsRequest(IUniformResourceLocator.URL.URL_GROUPS, "GET", params,
							MemberPrivilegeInfoController.this);

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

						// Compares current and selected privileges
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
							return IMessages.Error.NO_CHANGES_MADE;
						}
					}

					List<NameValuePair> paramsUpdate = new ArrayList<NameValuePair>();

					// Required parameters
					paramsUpdate.add(new BasicNameValuePair("do", "updatePrivilege"));
					paramsUpdate.add(new BasicNameValuePair("personId", MemberData.getPERSONID()));
					paramsUpdate.add(new BasicNameValuePair("groupId", GroupData.getGROUPID()));
					paramsUpdate.add(new BasicNameValuePair("memberInvitation", afterMemberInvitation));
					paramsUpdate.add(new BasicNameValuePair("memberlistEditing", afterMemberlistEditing));
					paramsUpdate.add(new BasicNameValuePair("eventCreating", afterEventCreating));
					paramsUpdate.add(new BasicNameValuePair("eventEditing", afterEventEditing));
					paramsUpdate.add(new BasicNameValuePair("eventDeleting", afterEventDeleting));
					paramsUpdate.add(new BasicNameValuePair("commentEditing", afterCommentEditing));
					paramsUpdate.add(new BasicNameValuePair("commentDeleting", afterCommentDeleting));
					paramsUpdate.add(new BasicNameValuePair("privilegeManagement", afterPrivilegeManagement));

					// Update the privileges
					json = jsonParser.makeHttpsRequest(IUniformResourceLocator.URL.URL_PRIVILEGE, "GET", paramsUpdate,
									MemberPrivilegeInfoController.this);

					success = json.getInt(TAG_SUCCESS);
					if (success == 1) {
						List<NameValuePair> paramsNotification = new ArrayList<NameValuePair>();

						// Required parameters
						paramsNotification.add(new BasicNameValuePair("do", "create"));
						paramsNotification.add(new BasicNameValuePair("eMail", tv_eMail.getText().toString()));
						paramsNotification.add(new BasicNameValuePair("classification", "10"));
						String message = new String();
						boolean firstEntry = true;

						// Message which describes granted and revoked user rights
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

						// Send notification to member
						json = jsonParser.makeHttpsRequest(IUniformResourceLocator.URL.URL_NOTIFICATION, "GET", paramsNotification,
										MemberPrivilegeInfoController.this);
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
				pDialog.dismiss();
				logout();
			}
			return null;
		}

		/**
		 * Removes ProgressDialog. Starts new activity.
		 * 
		 * @param message String
		 */
		protected void onPostExecute(String message) {
			pDialog.dismiss();

			if (message != null) {
				Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
			} else {
				Intent intent;
				if (EventData.isBACK()) {
					EventData.setBACK(false);
					intent = new Intent(MemberPrivilegeInfoController.this, AttendingMemberController.class);
					startActivity(intent);
				} else {
					intent = new Intent(MemberPrivilegeInfoController.this, MemberListController.class);
					startActivity(intent);
				}
			}
		}
	}
}