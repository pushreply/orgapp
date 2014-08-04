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
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import fhkl.de.orgapp.R;
import fhkl.de.orgapp.util.IMessages;
import fhkl.de.orgapp.util.JSONParser;
import fhkl.de.orgapp.util.MenuActivity;
import fhkl.de.orgapp.util.data.GroupData;
import fhkl.de.orgapp.util.validator.InputValidator;

/**
 * EditGroupController - Handles the edit group activity
 * 
 * Loads chosen group. Checks if user changed group name or info. Edites Group.
 * Sends Notifications to all group member.
 * 
 * @author Jochen Jung
 * @version 1.0
 */
public class EditGroupController extends MenuActivity {

	private ProgressDialog pDialog;
	EditText inputName;
	EditText inputInfo;
	String beforeName;
	String beforeInfo;

	JSONParser jsonParser = new JSONParser();
	private static String url_check_group = "http://pushrply.com/get_group.php";
	private static String url_update_group = "http://pushrply.com/update_group.php";
	private static String url_get_all_user_in_group = "http://pushrply.com/get_all_user_in_group.php";
	private static String URL_NOTIFICATION = "http://pushrply.com/pdo_notificationcontrol.php";

	private static final String TAG_SUCCESS = "success";

	JSONArray member = null;
	JSONArray groups = null;

	/**
	 * Initializes view elements. Registers ClickListener.
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

		Button bSave = (Button) findViewById(R.id.SAVE);
		Button bCancel = (Button) findViewById(R.id.CANCEL);

		// Calls the Async class to load view elements
		new GetGroup().execute();

		bSave.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				// Calls the Async class to edit a group
				new EditGroup().execute();
			}
		});

		bCancel.setOnClickListener(new View.OnClickListener() {
			@Override
			// Returns to previous activity
			public void onClick(View view) {
				Intent intent = new Intent(EditGroupController.this, SingleGroupController.class);
				startActivity(intent);
			}
		});
	}

	/**
	 * Async class that loads view elements
	 * 
	 * @author Jochen Jung
	 * @version 1.0
	 */
	class GetGroup extends AsyncTask<String, String, String> {

		/**
		 * Creates ProcessDialog
		 */
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pDialog = new ProgressDialog(EditGroupController.this);
			pDialog.setMessage(IMessages.Status.LOADING_GROUP);
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(true);
			pDialog.show();
		}

		/**
		 * Gets current group name and info.
		 * 
		 * @param args String...
		 * @return String result
		 */
		protected String doInBackground(String... args) {
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair("groupId", GroupData.getGROUPID()));
			// Get group
			JSONObject json = jsonParser.makeHttpRequest(url_check_group, "GET", params);

			Log.d("Response", json.toString());

			try {
				int success = json.getInt(TAG_SUCCESS);
				if (success == 1) {
					groups = json.getJSONArray("groups");

					for (int i = 0; i < groups.length();) {
						JSONObject c = groups.getJSONObject(i);

						String result = new String();
						result += c.getString("name");
						// Saving current name to compare with new
						beforeName = c.getString("name");
						result += ", " + c.getString("info");
						// Saving current info to compare with new
						beforeInfo = c.getString("info");

						return result;
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
				logout();
			}

			return null;
		}

		/**
		 * Removes ProcessDialog. Processes result. Calls method to set view
		 * elements.
		 */
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			pDialog.dismiss();

			if (result == null)
				return;

			String[] datas = result.split(", ");

			if (datas.length != 2)
				return;

			setTexts(datas);
		}
	}

	/**
	 * Sets view elements.
	 * 
	 * @param datas String[]
	 */
	private void setTexts(String[] datas) {

		inputName.setText(datas[0]);
		inputInfo.setText(datas[1]);
	}

	class EditGroup extends AsyncTask<String, String, String> {

		/**
		 * Creates ProcessDialog
		 */
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pDialog = new ProgressDialog(EditGroupController.this);
			pDialog.setMessage(IMessages.Status.SAVING_GROUP);
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(true);
			pDialog.show();
		}

		/**
		 * Compares current name and info with new name and info. Returns warning
		 * when no changes were made. Updates group otherwise. Sends Notifications
		 * to all group member.
		 * 
		 * @param args String...
		 * @return String result
		 */
		protected String doInBackground(String... args) {
			String name = inputName.getText().toString();
			String info = inputInfo.getText().toString();

			// Validates name
			if (!InputValidator.isStringLengthInRange(name, 0, 255)) {
				return IMessages.Error.INVALID_NAME;
			}
			// Validates info
			if (!InputValidator.isStringLengthInRange(info, 0, 1024)) {
				return IMessages.Error.INVALID_INFO;
			}

			// Checks for changes
			if (beforeName.equals(name) && beforeInfo.equals(info)) {
				return IMessages.Error.NO_CHANGES_MADE;
			}

			List<NameValuePair> paramsUpdateGroup = new ArrayList<NameValuePair>();

			paramsUpdateGroup.add(new BasicNameValuePair("groupId", GroupData.getGROUPID()));
			paramsUpdateGroup.add(new BasicNameValuePair("name", name));
			paramsUpdateGroup.add(new BasicNameValuePair("info", info));

			// Update group
			JSONObject json = jsonParser.makeHttpRequest(url_update_group, "GET", paramsUpdateGroup);

			Log.d("Create Response", json.toString());

			try {
				Integer success = json.getInt(TAG_SUCCESS);
				if (success != 0) {
					List<NameValuePair> paramsGetUserInGroup = new ArrayList<NameValuePair>();
					paramsGetUserInGroup.add(new BasicNameValuePair("groupId", GroupData.getGROUPID()));

					// Get all group member
					json = jsonParser.makeHttpRequest(url_get_all_user_in_group, "GET", paramsGetUserInGroup);

					Log.d("Response", json.toString());
					success = json.getInt(TAG_SUCCESS);
					if (success == 1) {

						member = json.getJSONArray("member");

						for (int i = 0; i < member.length(); i++) {
							JSONObject c = member.getJSONObject(i);

							List<NameValuePair> paramsCreateNotification = new ArrayList<NameValuePair>();
							paramsCreateNotification.add(new BasicNameValuePair("do", "create"));
							paramsCreateNotification.add(new BasicNameValuePair("eMail", c.getString("eMail")));
							paramsCreateNotification.add(new BasicNameValuePair("classification", "2"));

							// Notification message. Depends on changes.
							String message = new String();

							if (!beforeName.equals(name)) {
								message += "Group \"" + beforeName + "\" was renamed to \"" + name + "\"";
							}

							if (!beforeInfo.equals(info)) {
								if (beforeName.equals(name)) {
									message += "The info of the Group \"" + name + "\" was changed from \"" + beforeInfo + "\" to \""
													+ info + "\"";
								} else {
									message += " and info was changed from \"" + beforeInfo + "\" to \"" + info + "\"";
								}
							}

							paramsCreateNotification.add(new BasicNameValuePair("message", message));

							paramsCreateNotification.add(new BasicNameValuePair("syncInterval", "null"));

							// Send notifications
							json = jsonParser.makeHttpRequest(URL_NOTIFICATION, "GET", paramsCreateNotification);

							Intent intent = new Intent(EditGroupController.this, GroupsController.class);
							startActivity(intent);
						}
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
				logout();
			}
			return null;
		}

		/**
		 * Removes ProcessDialog. Show Toast if group not edited.
		 */
		protected void onPostExecute(String message) {
			super.onPostExecute(message);
			pDialog.dismiss();

			if (message != null) {
				Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
			}
		}
	}

}
