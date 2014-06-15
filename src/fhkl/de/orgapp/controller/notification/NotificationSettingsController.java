package fhkl.de.orgapp.controller.notification;

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
import android.widget.EditText;
import fhkl.de.orgapp.R;
import fhkl.de.orgapp.util.IMessages;
import fhkl.de.orgapp.util.JSONParser;
import fhkl.de.orgapp.util.MenuActivity;

public class NotificationSettingsController extends MenuActivity {

	JSONParser jsonParser = new JSONParser();
	private JSONObject notificationSettings = null;

	private String personIdLoggedPerson;

	private ProgressDialog pDialog;

	private static String URL_SELECT_NOTIFICATION_SETTINGS = "http://pushrply.com/get_notification_settings.php";
	private static final String TAG_SUCCESS = "success";

	CheckBox groupInvites, groupEdited, groupRemoved, eventsAdded, eventsEdited,
			eventsRemoved, commentsAdded, commentsEdited, commentsRemoved,
			privilegeGiven;
	EditText numberEntries;
	Button bSave, bCancel;

	JSONArray notification = null;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.notification_settings);

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

		numberEntries = (EditText) findViewById(R.id.NUMBER_ENTRIES);

		bSave = (Button) findViewById(R.id.NOTIFICATION_SETTINGS_SAVE);
		bCancel = (Button) findViewById(R.id.NOTIFICATION_SETTINGS_CANCEL);

		new SaveSettings().execute();

		bSave.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {

			}
		});

		bCancel.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				Intent intent = new Intent(NotificationSettingsController.this,
						NotificationController.class);
				intent.putExtra("UserId", personIdLoggedPerson);
				startActivity(intent);
			}
		});
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
			params.add(new BasicNameValuePair("personId", getIntent().getStringExtra(
					"UserId")));

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

					result += notificationSettings.getInt("shownEntries");

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

			if (datas.length != 11)
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

			numberEntries.setText(datas[10]);

		}
	}
}
