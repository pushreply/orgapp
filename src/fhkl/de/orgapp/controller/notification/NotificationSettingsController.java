package fhkl.de.orgapp.controller.notification;

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
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;
import fhkl.de.orgapp.R;
import fhkl.de.orgapp.util.IMessages;
import fhkl.de.orgapp.util.JSONParser;
import fhkl.de.orgapp.util.MenuActivity;
import fhkl.de.orgapp.util.NotificationSettingsData;
import fhkl.de.orgapp.util.UserData;

public class NotificationSettingsController extends MenuActivity {

	JSONParser jsonParser = new JSONParser();

	private ProgressDialog pDialog;

	private static String URL_UPDATE_NOTIFICATION_SETTINGS = "http://pushrply.com/update_notification_settings.php";
	private static final String TAG_SUCCESS = "success";

	CheckBox groupInvites, groupEdited, groupRemoved, eventsAdded, eventsEdited,
			eventsRemoved, commentsAdded, commentsEdited, commentsRemoved,
			privilegeGiven, received_entries;
	EditText numberEntries;
	Button bSave, bCancel;
	RadioButton textVibrationYes, textVibrationNo;
	boolean vibration;

	JSONArray notification = null;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.notification_settings);
		checkNewNotificationAndCreateIcon();

		groupInvites = (CheckBox) findViewById(R.id.GROUP_INVITES);
		groupEdited = (CheckBox) findViewById(R.id.GROUP_EDITED);
		groupRemoved = (CheckBox) findViewById(R.id.GROUP_REMOVED);
		eventsAdded = (CheckBox) findViewById(R.id.EVENTS_ADDED);
		eventsEdited = (CheckBox) findViewById(R.id.EVENTS_EDITED);
		eventsRemoved = (CheckBox) findViewById(R.id.EVENTS_REMOVED);
		commentsAdded = (CheckBox) findViewById(R.id.COMMENTS_ADDED);
		commentsEdited = (CheckBox) findViewById(R.id.COMMENTS_EDITED);
		commentsRemoved = (CheckBox) findViewById(R.id.COMMENTS_REMOVED);
		privilegeGiven = (CheckBox) findViewById(R.id.PRIVILEGES_GIVEN);
		received_entries = (CheckBox) findViewById(R.id.RECEIVED_ENTRIES);

		numberEntries = (EditText) findViewById(R.id.NUMBER_ENTRIES);

		bSave = (Button) findViewById(R.id.NOTIFICATION_SETTINGS_SAVE);
		bCancel = (Button) findViewById(R.id.NOTIFICATION_SETTINGS_CANCEL);
		
		textVibrationYes = (RadioButton) findViewById(R.id.NOTIFICATION_SETTINGS_VIBRATION_AT_NEW_NOTIFICATIONS_YES);
		textVibrationNo = (RadioButton) findViewById(R.id.NOTIFICATION_SETTINGS_VIBRATION_AT_NEW_NOTIFICATIONS_NO);

		setTexts();
		
		received_entries
				.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

					@Override
					public void onCheckedChanged(CompoundButton buttonView,
							boolean isChecked) {

						if (isChecked) {
							numberEntries.setVisibility(View.VISIBLE);
						} else {
							numberEntries.setVisibility(View.GONE);
						}
					}
				});

		bSave.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {

				new SaveSettings().execute();
			}
		});

		bCancel.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				Intent intent = new Intent(NotificationSettingsController.this,
						NotificationController.class);
				startActivity(intent);
			}
		});
	}
	
	private void setTexts()
	{
		groupInvites.setChecked(Boolean.parseBoolean(NotificationSettingsData.getGROUP_INVITES()));
		groupEdited.setChecked(Boolean.parseBoolean(NotificationSettingsData.getGROUP_EDITED()));
		groupRemoved.setChecked(Boolean.parseBoolean(NotificationSettingsData.getGROUP_REMOVED()));
		eventsAdded.setChecked(Boolean.parseBoolean(NotificationSettingsData.getEVENTS_ADDED()));
		eventsEdited.setChecked(Boolean.parseBoolean(NotificationSettingsData.getEVENTS_EDITED()));
		eventsRemoved.setChecked(Boolean.parseBoolean(NotificationSettingsData.getEVENTS_REMOVED()));
		commentsAdded.setChecked(Boolean.parseBoolean(NotificationSettingsData.getCOMMENTS_ADDED()));
		commentsEdited.setChecked(Boolean.parseBoolean(NotificationSettingsData.getCOMMENTS_EDITED()));
		commentsRemoved.setChecked(Boolean.parseBoolean(NotificationSettingsData.getCOMMENTS_REMOVED()));
		privilegeGiven.setChecked(Boolean.parseBoolean(NotificationSettingsData.getPRIVILEGE_GIVEN()));

		if(Boolean.parseBoolean(NotificationSettingsData.getVIBRATION()))
		{
			textVibrationYes.setChecked(true);
			vibration = true;
		}
		else
		{
			textVibrationNo.setChecked(true);
			vibration = false;
		}
		
		if (NotificationSettingsData.getSHOW_ENTRIES() != null && !NotificationSettingsData.getSHOW_ENTRIES().equals(""))
		{
			received_entries.setChecked(true);
			numberEntries.setText(NotificationSettingsData.getSHOW_ENTRIES());
			numberEntries.setVisibility(View.VISIBLE);
		}
		else
		{
			numberEntries.setVisibility(View.GONE);
		}
	}
	
	public void selectVibrationAtNewNotifications(View view)
	{
		switch (view.getId())
		{
			case R.id.NOTIFICATION_SETTINGS_VIBRATION_AT_NEW_NOTIFICATIONS_YES:
				vibration =	true;
				break;
	
			case R.id.NOTIFICATION_SETTINGS_VIBRATION_AT_NEW_NOTIFICATIONS_NO:
				vibration = false;
				break;
		}
	}

	class SaveSettings extends AsyncTask<String, String, String> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pDialog = new ProgressDialog(NotificationSettingsController.this);
			pDialog.setMessage(IMessages.SAVING_SETTINGS);
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(false);
			pDialog.show();
		}

		protected String doInBackground(String... args) {
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair("personId", UserData.getPERSONID()));

			if (received_entries.isChecked() == true) {
				Integer shownEntries;
				try {
					shownEntries = Integer.valueOf(numberEntries.getText().toString());
					if (shownEntries > Integer.MAX_VALUE) {
						return IMessages.INVALID_NUMBER;
					}
				} catch (NumberFormatException e) {
					return IMessages.INVALID_NUMBER;
				}
				params.add(new BasicNameValuePair("shownEntries", numberEntries
						.getText().toString()));
			}

			params.add(new BasicNameValuePair("groupInvites", groupInvites
					.isChecked() == true ? "1" : "0"));
			params.add(new BasicNameValuePair("groupEdited",
					groupEdited.isChecked() == true ? "1" : "0"));
			params.add(new BasicNameValuePair("groupRemoved", groupRemoved
					.isChecked() == true ? "1" : "0"));

			params.add(new BasicNameValuePair("eventsAdded",
					eventsAdded.isChecked() == true ? "1" : "0"));
			params.add(new BasicNameValuePair("eventsEdited", eventsEdited
					.isChecked() == true ? "1" : "0"));
			params.add(new BasicNameValuePair("eventsRemoved", eventsRemoved
					.isChecked() == true ? "1" : "0"));

			params.add(new BasicNameValuePair("commentsAdded", commentsAdded
					.isChecked() == true ? "1" : "0"));
			params.add(new BasicNameValuePair("commentsEdited", commentsEdited
					.isChecked() == true ? "1" : "0"));
			params.add(new BasicNameValuePair("commentsRemoved", commentsRemoved
					.isChecked() == true ? "1" : "0"));

			params.add(new BasicNameValuePair("privilegeGiven", privilegeGiven
					.isChecked() == true ? "1" : "0"));

			params.add(new BasicNameValuePair("vibration", vibration ? "1" : "0"));
			
			try {
				JSONObject json = new JSONParser().makeHttpRequest(
						URL_UPDATE_NOTIFICATION_SETTINGS, "GET", params);
				int success = json.getInt(TAG_SUCCESS);

				if (success == 1) {
					Intent intent = new Intent(NotificationSettingsController.this,
							NotificationController.class);
					startActivity(intent);
				} else {
					// unknown error
				}
			} catch (JSONException e) {
				System.out
						.println("Error in SaveSettings.doInBackground(String... args): "
								+ e.getMessage());
				e.printStackTrace();
			}

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