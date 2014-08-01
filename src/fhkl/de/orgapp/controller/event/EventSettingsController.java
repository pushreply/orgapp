package fhkl.de.orgapp.controller.event;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
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
import android.widget.Toast;
import fhkl.de.orgapp.R;
import fhkl.de.orgapp.util.IMessages;
import fhkl.de.orgapp.util.JSONParser;
import fhkl.de.orgapp.util.MenuActivity;
import fhkl.de.orgapp.util.data.EventSettingsData;
import fhkl.de.orgapp.util.data.UserData;

/**
 * EventSettingsController - Handles the data for display the event settings
 * 
 * @author Oliver Neubauer
 * @version 1.0
 * 
 */

public class EventSettingsController extends MenuActivity {
	private ProgressDialog pDialog;
	JSONParser jsonParser = new JSONParser();

	private static String URL_EVENT_SETTINGS = "http://pushrply.com/pdo_eventsettingscontrol.php";
	private static final String TAG_SUCCESS = "success";

	CheckBox receivedEntries;
	EditText numberEntries;
	Button save, cancel;
	Integer shownEntries;

	/**
	 * 
	 * 
	 * @param savedInstanceState contains the data
	 */

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.event_settings);

		receivedEntries = (CheckBox) findViewById(R.id.EVENT_SETTINGS_RECEIVED_EVENT_ENTRIES);
		numberEntries = (EditText) findViewById(R.id.EVENT_SETTINGS_NUMBER_EVENT_ENTRIES);
		save = (Button) findViewById(R.id.EVENT_SETTINGS_BUTTON_SAVE);
		cancel = (Button) findViewById(R.id.EVENT_SETTINGS_BUTTON_CANCEL);

		if (EventSettingsData.getSHOWN_EVENT_ENTRIES() != null && !EventSettingsData.getSHOWN_EVENT_ENTRIES().equals("")) {
			receivedEntries.setChecked(true);
			numberEntries.setText(EventSettingsData.getSHOWN_EVENT_ENTRIES());
			numberEntries.setVisibility(View.VISIBLE);
		} else {
			numberEntries.setVisibility(View.GONE);
		}

		receivedEntries.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if (isChecked) {
					numberEntries.setVisibility(View.VISIBLE);
				} else {
					numberEntries.setVisibility(View.GONE);
					numberEntries.setText("");
				}
			}
		});
	}

	public void saveEventSettings(View view) {
		if (isNumberOfEntriesEmpty()) {
			Toast.makeText(getApplicationContext(), IMessages.Error.NO_NUMBER_ENTERED, Toast.LENGTH_LONG).show();
			return;
		}

		if (isNumberZero()) {
			Toast.makeText(getApplicationContext(), IMessages.Error.NUMBER_ZERO_NOT_ALLOW, Toast.LENGTH_LONG).show();
			return;
		}

		if (!hasEventSettingsChanged()) {
			Toast.makeText(getApplicationContext(), IMessages.Error.NO_CHANGES_MADE, Toast.LENGTH_LONG).show();
			return;
		}

		new EventSettingsSaver().execute();
	}

	public void cancelEventSettings(View view) {
		Intent intent = new Intent(EventSettingsController.this, EventController.class);
		startActivity(intent);
	}

	private boolean isNumberOfEntriesEmpty() {
		if (receivedEntries.isChecked() && numberEntries.getText().toString().equals(""))
			return true;

		return false;
	}

	private boolean isNumberZero() {
		if (numberEntries.getText().toString().equals("0"))
			return true;

		return false;
	}

	private boolean hasEventSettingsChanged() {
		if (numberEntries.getText().toString().equals(EventSettingsData.getSHOWN_EVENT_ENTRIES()))
			return false;

		return true;
	}

	/**
	 * EventSettingsSaver - Saves the event settings
	 * 
	 * @author Oliver Neubauer
	 * @version 1.0
	 */

	class EventSettingsSaver extends AsyncTask<String, String, String> {
		/**
		 * Defines a progress dialog within the main thread
		 */

		@Override
		protected void onPreExecute() {
			super.onPreExecute();

			pDialog = new ProgressDialog(EventSettingsController.this);
			pDialog.setMessage(IMessages.Status.SAVING_SETTINGS);
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(false);
			pDialog.show();
		}

		/**
		 * Prepares and makes a http-request within the background thread
		 * 
		 * @param arg0 the arguments as String array
		 */

		@Override
		protected String doInBackground(String... arg0) {
			List<NameValuePair> params = new ArrayList<NameValuePair>();

			params.add(new BasicNameValuePair("do", "update"));
			params.add(new BasicNameValuePair("personId", UserData.getPERSONID()));

			if (receivedEntries.isChecked()) {
				try {
					shownEntries = Integer.valueOf(numberEntries.getText().toString());

					if (shownEntries > Integer.MAX_VALUE) {
						return IMessages.Error.INVALID_NUMBER;
					}
				} catch (NumberFormatException e) {
					return IMessages.Error.INVALID_NUMBER;
				}

				params.add(new BasicNameValuePair("shownEntries", numberEntries.getText().toString()));
			}

			JSONObject json = jsonParser.makeHttpRequest(URL_EVENT_SETTINGS, "GET", params);

			try {
				int success = json.getInt(TAG_SUCCESS);

				if (success == 1)
				{
					updateEventSettings();

					return IMessages.Success.UPDATE_WAS_SUCCESSFUL;
				}
			} catch (Exception e) {
				e.printStackTrace();
				logout();
			}

			return null;
		}

		/**
		 * Displays an user message within main thread. Goes to EventController
		 * 
		 * @param message the message to be displayed
		 */

		protected void onPostExecute(String message) {
			pDialog.dismiss();

			if (message != null) {
				Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();

				Intent intent = new Intent(EventSettingsController.this, EventController.class);
				startActivity(intent);
			}
		}

		private void updateEventSettings() {
			EventSettingsData.setSHOWN_EVENT_ENTRIES(receivedEntries.isChecked() ? numberEntries.getText().toString() : "");
		}
	}
}