package fhkl.de.orgapp.controller.groups;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
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
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import fhkl.de.orgapp.R;
import fhkl.de.orgapp.util.IMessages;
import fhkl.de.orgapp.util.IUniformResourceLocator;
import fhkl.de.orgapp.util.JSONParser;
import fhkl.de.orgapp.util.MenuActivity;
import fhkl.de.orgapp.util.data.GroupData;
import fhkl.de.orgapp.util.data.UserData;
import fhkl.de.orgapp.util.validator.InputValidator;

/**
 * NewGroupController - Handles new group activity.
 * 
 * Adds new group. Gives member admin privileges.
 * 
 * @author Jochen Jung
 * @version 1.0
 */
public class NewGroupController extends MenuActivity {
	AlertDialog member_question;
	private ProgressDialog pDialog;
	EditText inputName;
	EditText inputInfo;

	JSONParser jsonParser = new JSONParser();

	private static final String TAG_SUCCESS = "success";

	/**
	 * Initializes view. Defines save and cancel Button functionality.
	 * 
	 * @param savedInstanceState Bundle
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.new_edit_group);
		checkOnNewNotificationsAndNotifyUser();

		inputName = (EditText) findViewById(R.id.NAME);
		inputInfo = (EditText) findViewById(R.id.INFO);

		Button bSubmit = (Button) findViewById(R.id.SAVE);
		Button bCancel = (Button) findViewById(R.id.CANCEL);

		bSubmit.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				new CreateNewGroup().execute();
			}
		});

		bCancel.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				Intent intent = new Intent(NewGroupController.this, GroupsController.class);
				startActivity(intent);
			}
		});
	}

	/**
	 * Async class that creates new group. Gives member admin privileges.
	 * 
	 * @author Jochen Jung
	 * @version 1.0
	 */
	class CreateNewGroup extends AsyncTask<String, String, String> {

		String groupId;

		/**
		 * Creates ProcessDialog
		 */
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pDialog = new ProgressDialog(NewGroupController.this);
			pDialog.setMessage(IMessages.Status.CREATING_GROUP);
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(true);
			pDialog.show();
		}

		/**
		 * Validates User input. Creates new group and gives admin privilege to
		 * member when user input validated.
		 * 
		 * @param args String...
		 * @return String result
		 */
		protected String doInBackground(String... args) {
			String personId = UserData.getPERSONID();
			String name = inputName.getText().toString();
			String info = inputInfo.getText().toString();

			if (!InputValidator.isStringLengthInRange(name, 0, 255)) {
				// Wrong name format
				return IMessages.Error.INVALID_NAME;
			}
			if (!InputValidator.isStringLengthInRange(info, 0, 1024)) {
				// Wrong info format
				return IMessages.Error.INVALID_INFO;
			}

			List<NameValuePair> paramsCreateGroup = new ArrayList<NameValuePair>();

			// Required parameters
			paramsCreateGroup.add(new BasicNameValuePair("do", "createGroup"));
			paramsCreateGroup.add(new BasicNameValuePair("personId", personId));
			paramsCreateGroup.add(new BasicNameValuePair("name", name));
			paramsCreateGroup.add(new BasicNameValuePair("info", info));

			// Create new group
			JSONObject json = jsonParser.makeHttpsRequest(IUniformResourceLocator.URL.URL_GROUPS, "GET", paramsCreateGroup,
							NewGroupController.this);

			Log.d("Create Response", json.toString());

			try {
				Integer success = json.getInt(TAG_SUCCESS);
				if (success != 0) {
					Integer groupId = json.getInt("groupId");
					GroupData.setGROUPID(groupId.toString());
					GroupData.setGROUPNAME(name);
					GroupData.setGROUPINFO(info);
					List<NameValuePair> paramsCreateUserInGroup = new ArrayList<NameValuePair>();
					DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd", Locale.GERMANY);
					Date date = new Date();

					// Required parameters
					paramsCreateUserInGroup.add(new BasicNameValuePair("do", "createPrivilegeAdmin"));
					paramsCreateUserInGroup.add(new BasicNameValuePair("groupId", groupId.toString()));
					paramsCreateUserInGroup.add(new BasicNameValuePair("personId", personId));
					paramsCreateUserInGroup.add(new BasicNameValuePair("memberSince", dateFormat.format(date).toString()));

					// Create new user in group as admin
					json = jsonParser.makeHttpsRequest(IUniformResourceLocator.URL.URL_PRIVILEGE, "GET", paramsCreateUserInGroup,
									NewGroupController.this);
				}
			} catch (Exception e) {
				e.printStackTrace();
				pDialog.dismiss();
				logout();
			}

			return null;
		}

		/**
		 * Removes ProcessDialog. Shows AlertDialog, which gives the option to
		 * invite member via list or manually, when group successfully created.
		 * 
		 * @param message String
		 */
		protected void onPostExecute(String message) {
			super.onPostExecute(message);
			pDialog.dismiss();

			if (message != null) {
				Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();

			} else {
				AlertDialog dialog;
				AlertDialog.Builder builder = new AlertDialog.Builder(NewGroupController.this);
				builder.setMessage(IMessages.SecurityIssue.QUESTION_MEMBER);
				builder.setPositiveButton(IMessages.DialogButton.LIST, new OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						Intent i = new Intent(NewGroupController.this, ListInviteMemberController.class);
						dialog.dismiss();
						startActivity(i);
					}

				});
				builder.setNegativeButton(IMessages.DialogButton.MANUALLY, new OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						Intent intent = new Intent(NewGroupController.this, ManualInviteMemberController.class);
						dialog.dismiss();
						startActivity(intent);
					}
				});
				builder.setNeutralButton(IMessages.DialogButton.NO_MEMBER_INVITE, new OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						Intent intent = new Intent(NewGroupController.this, GroupsController.class);
						dialog.dismiss();
						startActivity(intent);
					}
				});

				dialog = builder.create();
				dialog.show();
			}
		}
	}
}
