package fhkl.de.orgapp.controller.login;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import fhkl.de.orgapp.R;
import fhkl.de.orgapp.controller.calendar.CalendarController;
import fhkl.de.orgapp.controller.start.StartController;
import fhkl.de.orgapp.util.IMessages;
import fhkl.de.orgapp.util.IUniformResourceLocator;
import fhkl.de.orgapp.util.JSONParser;
import fhkl.de.orgapp.util.data.EventSettingsData;
import fhkl.de.orgapp.util.data.NotificationSettingsData;
import fhkl.de.orgapp.util.data.UserData;
import fhkl.de.orgapp.util.validator.OutputValidator;

/**
 * LoginController - Handles the data for login of the user
 * 
 * @author Oliver Neubauer
 * @version 1.0
 * 
 */

public class LoginController extends Activity {
	// Progress Dialog
	private ProgressDialog pDialog;

	// For json issues
	JSONObject json, e, notificationSettings, eventSettings;
	JSONArray person = null, notificationSettingsArray, eventSettingsArray;
	List<NameValuePair> params;
	int success;

	// For the displayed button
	private Button bSubmit;
	private Button bCancel;

	// For the user input
	EditText inputEMail;
	EditText inputPassword;

	/**
	 * Sets the content view. Fetches the views by id. Sets onClickListener
	 * 
	 * @param savedInstanceState contains the data
	 */

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Set the content view
		setContentView(R.layout.login);

		// Fetch the views by id
		bSubmit = (Button) findViewById(R.id.SAVE);
		bCancel = (Button) findViewById(R.id.CANCEL);
		inputEMail = (EditText) findViewById(R.id.EMAIL);
		inputPassword = (EditText) findViewById(R.id.PASSWORD);

		// Set OnclickListener for submit
		bSubmit.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// Call the data validator
				new Validator().execute();
			}
		});

		// Set onClickListener for cancel
		bCancel.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// Back to StartController
				Intent i = new Intent(LoginController.this, StartController.class);
				startActivity(i);
			}
		});
	}

	/**
	 * Validator - Checks the data entered by the user
	 * 
	 * @author Oliver Neubauer
	 * @version 1.0
	 * 
	 */

	class Validator extends AsyncTask<String, String, String> {
		/**
		 * Displays a progress dialog
		 */

		@Override
		protected void onPreExecute() {
			super.onPreExecute();

			pDialog = new ProgressDialog(LoginController.this);
			pDialog.setMessage(IMessages.Status.CHECKING_DATA);
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(true);
			pDialog.show();
		}

		/**
		 * Executes the request for check the user. Fetches the notification
		 * settings of the user. Fetches the event settings of the user.
		 * 
		 * @param arg0 the parameters as array
		 * @return an error message or null in case of success
		 */

		@Override
		protected String doInBackground(String... arg0) {
			// The required parameters for the request
			params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair("do", "read"));
			params.add(new BasicNameValuePair("eMail", inputEMail.getText().toString()));
			params.add(new BasicNameValuePair("password", inputPassword.getText().toString()));
			
			// Set password
			// Reset password in case of no login
			UserData.setPASSWORD(inputPassword.getText().toString());
			
			// Make the request to fetch the person
			json = new JSONParser().makeHttpsRequest(IUniformResourceLocator.URL.URL_PERSON, "GET", params,
							LoginController.this);
			try
			{
				success = json.getInt("success");

				// If the password correct, client receives success = 1
				if (success == 1)
				{
					// Fetch the person array
					person = json.getJSONArray("person");

					// Get the person (the only one)
					e = person.getJSONObject(0);

					// Get the email
					String eMail = e.getString("eMail");

					// Check the email on correctness
					if (eMail.equals(inputEMail.getText().toString())) {

						// The required parameters for the request
						params = new ArrayList<NameValuePair>();
						params.add(new BasicNameValuePair("do", "read"));
						params.add(new BasicNameValuePair("personId", e.getString("personId")));

						// Make the request to fetch the notification settings
						json = new JSONParser().makeHttpsRequest(IUniformResourceLocator.URL.URL_NOTIFICATIONSETTINGS, "GET",
										params, LoginController.this);

						success = json.getInt("success");

						// In case of success
						if (success == 1) {
							notificationSettingsArray = json.getJSONArray("notificationSettings");
							notificationSettings = notificationSettingsArray.getJSONObject(0);
						}
						// In case of no success
						else {
							pDialog.dismiss();
							logout();
						}

						// The required parameters for the request
						params = new ArrayList<NameValuePair>();
						params.add(new BasicNameValuePair("do", "read"));
						params.add(new BasicNameValuePair("personId", e.getString("personId")));

						// Make the request to fetch the event settings
						json = new JSONParser().makeHttpsRequest(IUniformResourceLocator.URL.URL_EVENTSETTINGS, "GET", params,
										LoginController.this);

						success = json.getInt("success");

						// In case of success
						if (success == 1) {
							eventSettingsArray = json.getJSONArray("eventSettings");
							eventSettings = eventSettingsArray.getJSONObject(0);
						}
						// In case of no success
						else {
							// Close the progress dialog
							pDialog.dismiss();
							// Logout the user
							logout();
						}

						return null;

					}
					// In case of no success
					else {
						return IMessages.Error.INVALID_USER;
					}
				}
				// If the password incorrect, client receives success = 2
				else if(success==2)
				{
					// Reset password in UserData
					UserData.setPASSWORD("");
					return IMessages.Error.INVALID_PASSWORD;
				}
				// In case of no success
				else
				{
					// Reset password in UserData
					UserData.setPASSWORD("");
					return IMessages.Error.INVALID_USER;
				}
			}
			// In case of error
			catch (Exception e) {
				// Close the progress dialog
				pDialog.dismiss();
				// Logout the user
				logout();
			}

			return null;
		}

		/**
		 * Dismisses the progress dialog Displays the error message, if available.
		 * Sets the user data, notification settings and event settings. Calls the
		 * CalendarController
		 * 
		 * @param message the error message or null in case of success
		 */

		@Override
		protected void onPostExecute(String message) {
			// Close the progress dialog
			pDialog.dismiss();

			// Display an error message
			if (message != null) {
				Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
			}
			// In case of success
			else {
				try {
					// Set the user data
					UserData.setPERSONID(e.getString("personId"));
					UserData.setFIRST_NAME(e.getString("firstName"));
					UserData.setLAST_NAME(e.getString("lastName"));
					UserData.setBIRTHDAY(OutputValidator.isUserBirthdaySet(e.getString("birthday")) ? e.getString("birthday")
									: "");
					UserData.setGENDER(OutputValidator.isUserGenderSet(e.getString("gender")) ? e.getString("gender") : "");
					UserData.setEMAIL(e.getString("eMail"));
					UserData.setMEMBER_SINCE(e.getString("created"));

					// Set the notification settings of the user
					NotificationSettingsData
									.setNOTIFICATION_SETTINGS_ID(notificationSettings.getString("notificationSettingsId"));
					NotificationSettingsData
									.setSHOW_ENTRIES(OutputValidator.isNotificationSettingsShownEntriesSet(notificationSettings
													.getString("shownEntries")) ? notificationSettings.getString("shownEntries") : "");
					NotificationSettingsData
									.setGROUP_INVITES(notificationSettings.getInt("groupInvites") == 1 ? "true" : "false");
					NotificationSettingsData.setGROUP_EDITED(notificationSettings.getInt("groupEdited") == 1 ? "true" : "false");
					NotificationSettingsData
									.setGROUP_REMOVED(notificationSettings.getInt("groupRemoved") == 1 ? "true" : "false");
					NotificationSettingsData.setEVENTS_ADDED(notificationSettings.getInt("eventsAdded") == 1 ? "true" : "false");
					NotificationSettingsData
									.setEVENTS_EDITED(notificationSettings.getInt("eventsEdited") == 1 ? "true" : "false");
					NotificationSettingsData.setEVENTS_REMOVED(notificationSettings.getInt("eventsRemoved") == 1 ? "true"
									: "false");
					NotificationSettingsData.setCOMMENTS_ADDED(notificationSettings.getInt("commentsAdded") == 1 ? "true"
									: "false");
					NotificationSettingsData.setCOMMENTS_EDITED(notificationSettings.getInt("commentsEdited") == 1 ? "true"
									: "false");
					NotificationSettingsData.setCOMMENTS_REMOVED(notificationSettings.getInt("commentsRemoved") == 1 ? "true"
									: "false");
					NotificationSettingsData.setPRIVILEGE_GIVEN(notificationSettings.getInt("privilegeGiven") == 1 ? "true"
									: "false");
					NotificationSettingsData.setVIBRATION(notificationSettings.getInt("vibration") == 1 ? "true" : "false");

					// Set the event settings of the user
					EventSettingsData.setEVENT_SETTINGS_ID(eventSettings.getString("eventSettingsId"));
					EventSettingsData.setSHOWN_EVENT_ENTRIES(OutputValidator.isEventSettingsShownEntriesSet(eventSettings
									.getString("shownEntries")) ? eventSettings.getString("shownEntries") : "");

					// Call the CalendarController
					Intent intent = new Intent(getApplicationContext(), CalendarController.class);
					startActivity(intent);
				}
				// In case of error
				catch (Exception e) {
					// Logout the user
					logout();
				}
			}
		}
	}

	/**
	 * Calls methods for reset the user data, notification settings and event
	 * settings. Calls the StartController
	 */

	private void logout() {
		resetUserData();
		resetNotificationSettingsData();
		resetEventSettingsData();

		Intent intent = new Intent(LoginController.this, StartController.class);
		startActivity(intent);
	}

	/**
	 * Resets the user data
	 */

	private void resetUserData() {
		UserData.setPERSONID("");
		UserData.setFIRST_NAME("");
		UserData.setLAST_NAME("");
		UserData.setBIRTHDAY("");
		UserData.setGENDER("");
		UserData.setEMAIL("");
		UserData.setPASSWORD("");
		UserData.setMEMBER_SINCE("");
	}

	/**
	 * Resets the notification settings
	 */

	private void resetNotificationSettingsData() {
		NotificationSettingsData.setNOTIFICATION_SETTINGS_ID("");
		NotificationSettingsData.setSHOW_ENTRIES("");
		NotificationSettingsData.setGROUP_INVITES("");
		NotificationSettingsData.setGROUP_EDITED("");
		NotificationSettingsData.setGROUP_REMOVED("");
		NotificationSettingsData.setEVENTS_ADDED("");
		NotificationSettingsData.setEVENTS_EDITED("");
		NotificationSettingsData.setEVENTS_REMOVED("");
		NotificationSettingsData.setCOMMENTS_ADDED("");
		NotificationSettingsData.setCOMMENTS_EDITED("");
		NotificationSettingsData.setCOMMENTS_REMOVED("");
		NotificationSettingsData.setPRIVILEGE_GIVEN("");
		NotificationSettingsData.setVIBRATION("");
	}

	/**
	 * Resets the event settings
	 */

	private void resetEventSettingsData() {
		EventSettingsData.setEVENT_SETTINGS_ID("");
		EventSettingsData.setSHOWN_EVENT_ENTRIES("");
	}
}