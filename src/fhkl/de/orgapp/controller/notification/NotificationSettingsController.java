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
import fhkl.de.orgapp.util.data.NotificationSettingsData;
import fhkl.de.orgapp.util.data.UserData;

public class NotificationSettingsController extends MenuActivity
{
	JSONParser jsonParser = new JSONParser();
	List<NameValuePair> params;
	
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
	Integer shownEntries;

	JSONArray notification = null;
	
	// tags for data access
	private final String TAG_SHOWN_ENTRIES = "shownEntries";
	private final String TAG_GROUP_INVITES = "groupInvites";
	private final String TAG_GROUP_EDITED = "groupEdited";
	private final String TAG_GROUP_REMOVED = "groupRemoved";
	private final String TAG_EVENTS_ADDED = "eventsAdded";
	private final String TAG_EVENTS_EDITED = "eventsEdited";
	private final String TAG_EVENTS_REMOVED = "eventsRemoved";
	private final String TAG_COMMENTS_ADDED = "commentsAdded";
	private final String TAG_COMMENTS_EDITED = "commentsEdited";
	private final String TAG_COMMENTS_REMOVED = "commentsRemoved";
	private final String TAG_PRIVILEGE_GIVEN = "privilegeGiven";
	private final String TAG_VIBRATION = "vibration";
	private final String TAG_TRUE = "true";
	private final String TAG_FALSE = "false";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.notification_settings);
		checkOnNewNotificationsAndNotifyUser();

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

	class SaveSettings extends AsyncTask<String, String, String>
	{
		@Override
		protected void onPreExecute()
		{
			super.onPreExecute();
			pDialog = new ProgressDialog(NotificationSettingsController.this);
			pDialog.setMessage(IMessages.Status.SAVING_SETTINGS);
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(false);
			pDialog.show();
		}

		protected String doInBackground(String... args)
		{
			params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair("personId", UserData.getPERSONID()));

			if (received_entries.isChecked())
			{
				try
				{
					shownEntries = Integer.valueOf(numberEntries.getText().toString());
					if (shownEntries > Integer.MAX_VALUE)
					{
						return IMessages.Error.INVALID_NUMBER;
					}
				}
				catch (NumberFormatException e)
				{
					return IMessages.Error.INVALID_NUMBER;
				}
				params.add(new BasicNameValuePair(TAG_SHOWN_ENTRIES, numberEntries.getText().toString()));
			}

			params.add(new BasicNameValuePair(TAG_GROUP_INVITES, groupInvites.isChecked() ? "1" : "0"));
			params.add(new BasicNameValuePair(TAG_GROUP_EDITED, groupEdited.isChecked() ? "1" : "0"));
			params.add(new BasicNameValuePair(TAG_GROUP_REMOVED, groupRemoved.isChecked() ? "1" : "0"));
			params.add(new BasicNameValuePair(TAG_EVENTS_ADDED, eventsAdded.isChecked() ? "1" : "0"));
			params.add(new BasicNameValuePair(TAG_EVENTS_EDITED, eventsEdited.isChecked() ? "1" : "0"));
			params.add(new BasicNameValuePair(TAG_EVENTS_REMOVED, eventsRemoved.isChecked() ? "1" : "0"));
			params.add(new BasicNameValuePair(TAG_COMMENTS_ADDED, commentsAdded.isChecked() ? "1" : "0"));
			params.add(new BasicNameValuePair(TAG_COMMENTS_EDITED, commentsEdited.isChecked() ? "1" : "0"));
			params.add(new BasicNameValuePair(TAG_COMMENTS_REMOVED, commentsRemoved.isChecked() ? "1" : "0"));
			params.add(new BasicNameValuePair(TAG_PRIVILEGE_GIVEN, privilegeGiven.isChecked() ? "1" : "0"));
			params.add(new BasicNameValuePair(TAG_VIBRATION, vibration ? "1" : "0"));
			
			try
			{
				JSONObject json = new JSONParser().makeHttpRequest(URL_UPDATE_NOTIFICATION_SETTINGS, "GET", params);
				int success = json.getInt(TAG_SUCCESS);

				if (success == 1)
				{
					updateNotificationSettingsData();
					Intent intent = new Intent(NotificationSettingsController.this, NotificationController.class);
					startActivity(intent);
				}
				else
				{
					// unknown error
				}
			}
			catch (JSONException e)
			{
				System.out.println("Error in SaveSettings.doInBackground(String... args): " + e.getMessage());
				e.printStackTrace();
			}

			return null;
		}

		protected void onPostExecute(String message)
		{
			pDialog.dismiss();

			if (message != null)
			{
				Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
			}
		}
		
		/**
		 * updates the notification settings of the user
		 */
		private void updateNotificationSettingsData()
		{
			NotificationSettingsData.setSHOW_ENTRIES(received_entries.isChecked() ? numberEntries.getText().toString() : "");
			NotificationSettingsData.setGROUP_INVITES(groupInvites.isChecked() ? TAG_TRUE : TAG_FALSE);
			NotificationSettingsData.setGROUP_EDITED(groupEdited.isChecked() ? TAG_TRUE : TAG_FALSE);
			NotificationSettingsData.setGROUP_REMOVED(groupRemoved.isChecked() ? TAG_TRUE : TAG_FALSE);
			NotificationSettingsData.setEVENTS_ADDED(eventsAdded.isChecked() ? TAG_TRUE : TAG_FALSE);
			NotificationSettingsData.setEVENTS_EDITED(eventsEdited.isChecked() ? TAG_TRUE : TAG_FALSE);
			NotificationSettingsData.setEVENTS_REMOVED(eventsRemoved.isChecked() ? TAG_TRUE : TAG_FALSE);
			NotificationSettingsData.setCOMMENTS_ADDED(commentsAdded.isChecked() ? TAG_TRUE : TAG_FALSE);
			NotificationSettingsData.setCOMMENTS_EDITED(commentsEdited.isChecked() ? TAG_TRUE : TAG_FALSE);
			NotificationSettingsData.setCOMMENTS_REMOVED(commentsRemoved.isChecked() ? TAG_TRUE : TAG_FALSE);
			NotificationSettingsData.setPRIVILEGE_GIVEN(privilegeGiven.isChecked() ? TAG_TRUE : TAG_FALSE);
			NotificationSettingsData.setVIBRATION(vibration ? TAG_TRUE : TAG_FALSE);
		}
	}
}