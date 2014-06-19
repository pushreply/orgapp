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

public class RegisterController extends Activity {
	// Progress Dialog
	private ProgressDialog pDialog;

	JSONParser jsonParser = new JSONParser();
	EditText inputEMail;
	EditText inputPassword;
	EditText inputPasswordConfirm;
	EditText inputFirstName;
	EditText inputLastName;

	// url to check existing person and create new person
	private static String url_check_person = "http://pushrply.com/select_person_by_email.php";
	private static String url_create_person = "http://pushrply.com/create_person.php";
	private static String url_create_notification_settings = "http://pushrply.com/create_notification_settings.php";

	// JSON Node names
	private static final String TAG_SUCCESS = "success";

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
		Button bSubmit = (Button) findViewById(R.id.SUBMIT);
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
			pDialog.setMessage(IMessages.CREATING_PERSON);
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

			if (!eMail.matches("[A-Z0-9a-z._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,4}")) {
				return IMessages.INVALID_EMAIL;
			}
			if (password.length() == 0 || password.length() > 255) {
				return IMessages.INVALID_PASSWORD;
			}
			if(!password.equals(passwordConfirm))
			{
				return IMessages.PASSWORDS_DO_NOT_MATCH;
			}
			if (firstName.length() == 0 || firstName.length() > 255) {
				return IMessages.INVALID_FIRSTNAME;
			}
			if (lastName.length() == 0 || lastName.length() > 255) {
				return IMessages.INVALID_LASTNAME;
			}

			List<NameValuePair> paramsCheck = new ArrayList<NameValuePair>();
			paramsCheck.add(new BasicNameValuePair("eMail", eMail));
			JSONObject jsonCheck = jsonParser.makeHttpRequest(url_check_person,
					"GET", paramsCheck);

			Log.d("Create Response", jsonCheck.toString());

			// check for success tag
			try {
				int success = jsonCheck.getInt(TAG_SUCCESS);

				if (success == 1) {
					return IMessages.DUPLICATE_PERSON;
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}

			// Building Parameters
			List<NameValuePair> paramsCreate = new ArrayList<NameValuePair>();
			paramsCreate.add(new BasicNameValuePair("eMail", eMail));

			// verschlüsselung

			paramsCreate.add(new BasicNameValuePair("password", password));
			paramsCreate.add(new BasicNameValuePair("firstName", firstName));
			paramsCreate.add(new BasicNameValuePair("lastName", lastName));
			DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
			Date date = new Date();
			paramsCreate.add(new BasicNameValuePair("created", dateFormat
					.format(date).toString()));

			// getting JSON Object
			// Note that create person url accepts GET method
			JSONObject json = jsonParser.makeHttpRequest(url_create_person, "GET",
					paramsCreate);

			// check log cat fro response
			Log.d("Create Response", json.toString());

			// check for success tag
			try {
				Integer success = json.getInt(TAG_SUCCESS);

				if (success != 0) {
					// successfully created person
					List<NameValuePair> paramsSettings = new ArrayList<NameValuePair>();
					paramsSettings.add(new BasicNameValuePair("personId", success
							.toString()));
					json = jsonParser.makeHttpRequest(url_create_notification_settings,
							"GET", paramsSettings);
					json.getInt(TAG_SUCCESS);
					if (success == 1) {
						Intent i = new Intent(getApplicationContext(),
								LoginController.class);
						startActivity(i);
					} else {
						// failed to create notification settings
					}

					// closing this screen
					finish();
				} else {
					// failed to create person
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
			}
		}
	}
}