package fhkl.de.orgapp.controller.registration;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
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
import fhkl.de.orgapp.controller.login.LoginController;
import fhkl.de.orgapp.controller.start.StartController;
import fhkl.de.orgapp.util.IMessages;
import fhkl.de.orgapp.util.JSONParser;
import fhkl.de.orgapp.util.validator.InputValidator;

public class RegisterController extends Activity
{
	// Progress Dialog
	private ProgressDialog pDialog;

	EditText inputEMail;
	EditText inputPassword;
	EditText inputPasswordConfirm;
	EditText inputFirstName;
	EditText inputLastName;

	// For http request
	private static String URL_PERSON = "http://pushrply.com/pdo_personcontrol.php";
	private static String URL_NOTIFICATION_SETTINGS = "http://pushrply.com/pdo_notificationsettingscontrol.php";
	private static String URL_EVENT_SETTINGS = "http://pushrply.com/pdo_eventsettingscontrol.php";

	// For json issues
	JSONParser jsonParser = new JSONParser();
	JSONObject json;
	private static final String TAG_SUCCESS = "success";
	List<NameValuePair> params;
	Integer success, newPersonId;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.register);

		// Edit Text
		inputEMail = (EditText) findViewById(R.id.EMAIL);
		inputPassword = (EditText) findViewById(R.id.PASSWORD);
		inputPasswordConfirm = (EditText) findViewById(R.id.PASSWORD_CONFIRMATION);
		inputFirstName = (EditText) findViewById(R.id.FIRSTNAME);
		inputLastName = (EditText) findViewById(R.id.LASTNAME);

		// Create button
		Button bSubmit = (Button) findViewById(R.id.SAVE);
		Button bCancel = (Button) findViewById(R.id.CANCEL);

		// button click event
		bSubmit.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				// creating new person in background thread
				new CreateNewPerson().execute();
			}
		});

		bCancel.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				Intent i = new Intent(RegisterController.this, StartController.class);
				startActivity(i);
			}
		});
	}

	/**
	 * Background Async Task to Create new person
	 * */
	class CreateNewPerson extends AsyncTask<String, String, String> {

		public CreateNewPerson() {
		}

		/**
		 * Before starting background thread Show Progress Dialog
		 * */
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pDialog = new ProgressDialog(RegisterController.this);
			pDialog.setMessage(IMessages.Status.CREATING_PERSON);
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(true);
			pDialog.show();
		}

		/**
		 * Creating person
		 * */
		protected String doInBackground(String... args) {
			String eMail = inputEMail.getText().toString();
			String password = inputPassword.getText().toString();
			String passwordConfirm = inputPasswordConfirm.getText().toString();
			String firstName = inputFirstName.getText().toString();
			String lastName = inputLastName.getText().toString();

			if (!InputValidator.isEmailValid(eMail))
				return IMessages.Error.INVALID_EMAIL;

			if (!InputValidator.isStringLengthInRange(password, 0, 255))
				return IMessages.Error.INVALID_PASSWORD;

			if (!password.equals(passwordConfirm))
				return IMessages.Error.PASSWORDS_DO_NOT_MATCH;

			if (!InputValidator.isStringLengthInRange(firstName, 0, 255))
				return IMessages.Error.INVALID_FIRSTNAME;

			if (!InputValidator.isStringLengthInRange(lastName, 0, 255))
				return IMessages.Error.INVALID_LASTNAME;

			params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair("do", "read"));
			params.add(new BasicNameValuePair("eMail", eMail));
			json = jsonParser.makeHttpRequest(URL_PERSON, "GET", params);

			Log.d("Create Response", json.toString());

			// check for success tag
			try {
				int success = json.getInt(TAG_SUCCESS);

				if (success == 1) {
					return IMessages.Error.DUPLICATE_PERSON;
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}

			// Building Parameters
			params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair("do", "create"));
			params.add(new BasicNameValuePair("eMail", eMail));
			// TODO Verschluesselung
			params.add(new BasicNameValuePair("password", password));
			params.add(new BasicNameValuePair("firstName", firstName));
			params.add(new BasicNameValuePair("lastName", lastName));
			DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
			Date date = new Date();
			params.add(new BasicNameValuePair("created", dateFormat.format(date).toString()));

			// getting JSON Object
			json = jsonParser.makeHttpRequest(URL_PERSON, "GET", params);

			// check log cat for response
			Log.d("Create Response", json.toString());

			// check for success tag
			try
			{
				// Fetch id of new person
				newPersonId = json.getInt(TAG_SUCCESS);
				
				if (newPersonId.intValue() != 0)
				{
					// Create notification settings
					params = new ArrayList<NameValuePair>();
					params.add(new BasicNameValuePair("do", "create"));
					params.add(new BasicNameValuePair("personId", newPersonId.toString()));
					json = jsonParser.makeHttpRequest(URL_NOTIFICATION_SETTINGS, "GET", params);
					success = json.getInt(TAG_SUCCESS);
					
					if (success == 1)
					{
						// Create event settings
						params = new ArrayList<NameValuePair>();
						params.add(new BasicNameValuePair("do", "create"));
						params.add(new BasicNameValuePair("personId", newPersonId.toString()));
						
						json = jsonParser.makeHttpRequest(URL_EVENT_SETTINGS, "GET", params);
						
						success = json.getInt(TAG_SUCCESS);
						
						if(success == 1)
						{
							return IMessages.Success.PERSON_SUCCESSFUL_CREATED;
						}
						else
						{
							// TODO rollback?
							System.out.println("No event settings created");
						}
					}
					else
					{
						// TODO rollback?
						System.out.println("No notification settings created");
					}
				}
				else
				{
					return IMessages.Error.PERSON_NOT_CREATED;
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
				Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
			}
			
			// Close screen
			finish();
			
			// Start LoginController
			Intent i = new Intent(getApplicationContext(), LoginController.class);
			startActivity(i);
		}
	}
}