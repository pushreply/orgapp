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
import fhkl.de.orgapp.util.UserData;

public class NotificationSettingsController extends MenuActivity {

	JSONParser jsonParser = new JSONParser();
	private JSONObject notificationSettings = null;

	private ProgressDialog pDialog;

	private static String URL_SELECT_NOTIFICATION_SETTINGS = "http://pushrply.com/get_notification_settings.php";
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

		received_entries
				.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

					@Override
					public void onCheckedChanged(CompoundButton buttonView,
							boolean isChecked) {

						if (isChecked == true) {
							numberEntries.setVisibility(View.VISIBLE);
						} else {
							numberEntries.setVisibility(View.GONE);
						}
					}
				});

		new GetSettings().execute();

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

	class GetSettings extends AsyncTask<String, String, String> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pDialog = new ProgressDialog(NotificationSettingsController.this);
			pDialog.setMessage(IMessages.LOADING_NOTIFICATION_SETTINGS);
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(true);
			pDialog.show();
		}

		protected String doInBackground(String... args) {
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair("personId", UserData.getPERSONID()));

			JSONObject json = new JSONParser().makeHttpRequest(
					URL_SELECT_NOTIFICATION_SETTINGS, "GET", params);

			try {
				int success = json.getInt(TAG_SUCCESS);

				if (success == 1) {

					String result = new String();
					notificationSettings = json.getJSONArray("notificationSettings")
							.getJSONObject(0);

					result += notificationSettings.getInt("groupInvites") == 1 ? "true"
							+ ", " : "false" + ", ";
					result += notificationSettings.getInt("groupEdited") == 1 ? "true"
							+ ", " : "false" + ", ";
					result += notificationSettings.getInt("groupRemoved") == 1 ? "true"
							+ ", " : "false" + ", ";
					result += notificationSettings.getInt("eventsAdded") == 1 ? "true"
							+ ", " : "false" + ", ";
					result += notificationSettings.getInt("eventsEdited") == 1 ? "true"
							+ ", " : "false" + ", ";
					result += notificationSettings.getInt("eventsRemoved") == 1 ? "true"
							+ ", " : "false" + ", ";
					result += notificationSettings.getInt("commentsAdded") == 1 ? "true"
							+ ", " : "false" + ", ";
					result += notificationSettings.getInt("commentsEdited") == 1 ? "true"
							+ ", " : "false" + ", ";
					result += notificationSettings.getInt("commentsRemoved") == 1 ? "true"
							+ ", "
							: "false" + ", ";
					result += notificationSettings.getInt("privilegeGiven") == 1 ? "true"
							+ ", " : "false" + ", ";

					result += notificationSettings.getInt("vibration") == 1 ? "true"
							+ ", " : "false" + ", ";
					
					try {
						result += notificationSettings.getInt("shownEntries");
					} catch (Exception e) {
						result += "null";
					}

					return result;
				} else {
					// unknown error
				}
			} catch (Exception e) {
				System.out
						.println("Error in SaveSettings.doInBackground(String... args): "
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

			if (datas.length != 12)
				return;

			setTexts(datas);

		}

		private void setTexts(String[] datas) {
			
			groupInvites.setChecked(Boolean.parseBoolean(datas[0]));
			groupEdited.setChecked(Boolean.parseBoolean(datas[1]));
			groupRemoved.setChecked(Boolean.parseBoolean(datas[2]));
			eventsAdded.setChecked(Boolean.parseBoolean(datas[3]));
			eventsEdited.setChecked(Boolean.parseBoolean(datas[4]));
			eventsRemoved.setChecked(Boolean.parseBoolean(datas[5]));
			commentsAdded.setChecked(Boolean.parseBoolean(datas[6]));
			commentsEdited.setChecked(Boolean.parseBoolean(datas[7]));
			commentsRemoved.setChecked(Boolean.parseBoolean(datas[8]));
			privilegeGiven.setChecked(Boolean.parseBoolean(datas[9]));

			if(Boolean.parseBoolean(datas[10]))
			{
				textVibrationYes.setChecked(true);
				vibration = true;
			}
			else
			{
				textVibrationNo.setChecked(true);
				vibration = false;
			}
			
			if (!datas[11].equals("null")) {
				received_entries.setChecked(true);
				numberEntries.setText(datas[11]);
				numberEntries.setVisibility(View.VISIBLE);
			} else {
				numberEntries.setVisibility(View.GONE);
			}

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
