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
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import fhkl.de.orgapp.R;
import fhkl.de.orgapp.util.GroupData;
import fhkl.de.orgapp.util.IMessages;
import fhkl.de.orgapp.util.InputValidator;
import fhkl.de.orgapp.util.JSONParser;
import fhkl.de.orgapp.util.MenuActivity;

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
	private static String url_create_notification = "http://pushrply.com/create_notification.php";

	private static final String TAG_SUCCESS = "success";

	JSONArray member = null;
	JSONArray groups = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.new_edit_group);

		inputName = (EditText) findViewById(R.id.NAME);
		inputInfo = (EditText) findViewById(R.id.INFO);

		Button bSave = (Button) findViewById(R.id.SAVE);
		Button bCancel = (Button) findViewById(R.id.CANCEL);

		new GetGroup().execute();

		bSave.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				// creating new group in background thread
				new EditGroup().execute();
			}
		});

		bCancel.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				Intent intent = new Intent(EditGroupController.this, SingleGroupController.class);
				startActivity(intent);
			}
		});
	}

	class GetGroup extends AsyncTask<String, String, String> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pDialog = new ProgressDialog(EditGroupController.this);
			pDialog.setMessage(IMessages.LOADING_GROUP);
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(true);
			pDialog.show();
		}

		protected String doInBackground(String... args) {
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair("name", GroupData.getGROUPNAME()));
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
						beforeName = c.getString("name");
						result += ", " + c.getString("info");
						beforeInfo = c.getString("info");

						return result;
					}
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}

			return null;
		}

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

	private void setTexts(String[] datas) {

		inputName.setText(datas[0]);
		inputInfo.setText(datas[1]);
	}

	class EditGroup extends AsyncTask<String, String, String> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pDialog = new ProgressDialog(EditGroupController.this);
			pDialog.setMessage(IMessages.SAVING_GROUP);
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(true);
			pDialog.show();
		}

		protected String doInBackground(String... args) {
			String name = inputName.getText().toString();
			String info = inputInfo.getText().toString();

			if (!InputValidator.isStringLengthInRange(name, 0, 255)) {
				return IMessages.INVALID_NAME;
			}
			if (!InputValidator.isStringLengthInRange(info, 0, 1024)) {
				return IMessages.INVALID_INFO;
			}

			List<NameValuePair> paramsCheck = new ArrayList<NameValuePair>();
			paramsCheck.add(new BasicNameValuePair("name", name));
			JSONObject jsonCheck = jsonParser.makeHttpRequest(url_check_group, "GET", paramsCheck);

			Log.d("Create Response", jsonCheck.toString());

			try {
				int success = jsonCheck.getInt(TAG_SUCCESS);

				if (success == 1) {
					return IMessages.DUPLICATE_GROUP;
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}

			List<NameValuePair> paramsUpdateGroup = new ArrayList<NameValuePair>();

			paramsUpdateGroup.add(new BasicNameValuePair("groupId", GroupData.getGROUPID()));
			paramsUpdateGroup.add(new BasicNameValuePair("name", name));
			paramsUpdateGroup.add(new BasicNameValuePair("info", info));

			JSONObject json = jsonParser.makeHttpRequest(url_update_group, "GET", paramsUpdateGroup);

			Log.d("Create Response", json.toString());

			try {
				Integer success = json.getInt(TAG_SUCCESS);
				if (success != 0) {
					List<NameValuePair> paramsGetUserInGroup = new ArrayList<NameValuePair>();
					paramsGetUserInGroup.add(new BasicNameValuePair("groupId", GroupData.getGROUPID()));

					json = jsonParser.makeHttpRequest(url_get_all_user_in_group, "GET", paramsGetUserInGroup);

					Log.d("Response", json.toString());
					success = json.getInt(TAG_SUCCESS);
					if (success == 1) {

						member = json.getJSONArray("member");

						for (int i = 0; i < member.length(); i++) {
							JSONObject c = member.getJSONObject(i);

							List<NameValuePair> paramsCreateNotification = new ArrayList<NameValuePair>();
							paramsCreateNotification.add(new BasicNameValuePair("eMail", c.getString("eMail")));
							paramsCreateNotification.add(new BasicNameValuePair("classification", "2"));
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

							json = jsonParser.makeHttpRequest(url_create_notification, "GET", paramsCreateNotification);

							Intent intent = new Intent(EditGroupController.this, GroupsController.class);
							startActivity(intent);
						}
					}
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
			return null;
		}

		protected void onPostExecute(String message) {
			super.onPostExecute(message);
			pDialog.dismiss();

			if (message != null) {
				Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
			}
		}
	}

}
