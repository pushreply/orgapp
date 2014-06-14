package fhkl.de.orgapp.controller.groups;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
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
import fhkl.de.orgapp.util.JSONParser;
import fhkl.de.orgapp.util.MenuActivity;

public class NewGroupController extends MenuActivity {

	AlertDialog member_question;
	private String personIdLoggedPerson;
	private ProgressDialog pDialog;
	EditText inputName;
	EditText inputInfo;

	JSONParser jsonParser = new JSONParser();
	private static String url_check_group = "http://pushrply.com/get_groups.php";
	private static String url_create_group = "http://pushrply.com/create_group.php";

	private static final String TAG_SUCCESS = "success";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.new_group);

		personIdLoggedPerson = getIntent().getStringExtra("UserId");

		inputName = (EditText) findViewById(R.id.NAME);
		inputInfo = (EditText) findViewById(R.id.INFO);

		Button bSubmit = (Button) findViewById(R.id.SUBMIT);
		Button bCancel = (Button) findViewById(R.id.CANCEL);

		// button click event
		bSubmit.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				// creating new group in background thread
				new CreateNewGroup().execute();
			}
		});

		bCancel.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				Intent intent = new Intent(NewGroupController.this,
						GroupsController.class);
				intent.putExtra("UserId", personIdLoggedPerson);
				startActivity(intent);
			}
		});
	}

	/**
	 * Background Async Task to Create new group
	 * */
	class CreateNewGroup extends AsyncTask<String, String, String> {

		/**
		 * Before starting background thread Show Progress Dialog
		 * */
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pDialog = new ProgressDialog(NewGroupController.this);
			pDialog.setMessage(IMessages.CREATING_GROUP);
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(false);
			pDialog.show();
		}

		/**
		 * Creating group
		 * */
		protected String doInBackground(String... args) {
			String personId = getIntent().getStringExtra("UserId");
			String name = inputName.getText().toString();
			String info = inputInfo.getText().toString();

			if (name.length() == 0 || name.length() > 255) {
				return IMessages.INVALID_NAME;
			}
			if (info.length() == 0 || info.length() > 1024) {
				return IMessages.INVALID_INFO;
			}

			List<NameValuePair> paramsCheck = new ArrayList<NameValuePair>();
			paramsCheck.add(new BasicNameValuePair("name", name));
			JSONObject jsonCheck = jsonParser.makeHttpRequest(url_check_group, "GET",
					paramsCheck);

			Log.d("Create Response", jsonCheck.toString());

			// check for success tag
			try {
				int success = jsonCheck.getInt(TAG_SUCCESS);

				if (success == 1) {
					return IMessages.DUPLICATE_GROUP;
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}

			// Building Parameters
			List<NameValuePair> paramsCreate = new ArrayList<NameValuePair>();

			paramsCreate.add(new BasicNameValuePair("personId", personId));
			paramsCreate.add(new BasicNameValuePair("name", name));
			paramsCreate.add(new BasicNameValuePair("info", info));

			// getting JSON Object
			// Note that create group url accepts GET method
			JSONObject json = jsonParser.makeHttpRequest(url_create_group, "GET",
					paramsCreate);

			// check log cat fro response
			Log.d("Create Response", json.toString());

			// check for success tag
			try {
				int success = json.getInt(TAG_SUCCESS);

				if (success == 1) {
					// successfully created group

					// closing this screen
				} else {
					// failed to create group
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}

			return null;
		}

		/**
		 * After completing background task Dismiss the progress dialog
		 * **/
		protected void onPostExecute(String message) {
			super.onPostExecute(message);
			// dismiss the dialog once done
			pDialog.dismiss();

			if (message != null) {
				Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG)
						.show();

			} else {
				AlertDialog.Builder builder = new AlertDialog.Builder(
						NewGroupController.this);
				builder.setMessage(IMessages.MEMBER_QUESTION);
				builder.setPositiveButton(IMessages.LIST, new OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						Intent i = new Intent(NewGroupController.this,
								ListInviteMemberController.class);
						startActivity(i);
					}

				});
				builder.setNegativeButton(IMessages.MANUALLY, new OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						Intent intent = new Intent(NewGroupController.this,
								ManualInviteMemberController.class);
						intent.putExtra("UserId", personIdLoggedPerson);
						startActivity(intent);
					}
				});
				builder.create().show();
			}
		}
	}
}
