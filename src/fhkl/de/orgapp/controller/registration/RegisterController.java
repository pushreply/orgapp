package fhkl.de.orgapp.controller.registration;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import fhkl.de.orgapp.R;
import fhkl.de.orgapp.controller.login.LoginController;
import fhkl.de.orgapp.controller.start.StartController;
import fhkl.de.orgapp.util.IMessages;
import fhkl.de.orgapp.util.IUniformResourceLocator;
import fhkl.de.orgapp.util.JSONParser;
import fhkl.de.orgapp.util.validator.InputValidator;

/**
 * RegisterController - Handles the data to register an user
 * 
 * @author Oliver Neubauer
 * @version 1.0
 *
 */

public class RegisterController extends Activity
{
	// Progress Dialog
	private ProgressDialog pDialog;

	// Variables for the input fields
	EditText inputEMail;
	EditText inputPassword;
	EditText inputPasswordConfirm;
	EditText inputFirstName;
	EditText inputLastName;

	// For json issues
	JSONParser jsonParser = new JSONParser();
	JSONObject json;
	private static final String TAG_SUCCESS = "success";
	List<NameValuePair> params;
	Integer success, newPersonId;
	
	/**
	 * Sets the content view.
	 * Fetches the views by id.
	 * Sets onClickListener
	 * 
	 * @param savedInstanceState contains the data
	 */
	
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		
		// Set the content view
		setContentView(R.layout.register);

		// Fetch the views by id
		inputEMail = (EditText) findViewById(R.id.EMAIL);
		inputPassword = (EditText) findViewById(R.id.PASSWORD);
		inputPasswordConfirm = (EditText) findViewById(R.id.PASSWORD_CONFIRMATION);
		inputFirstName = (EditText) findViewById(R.id.FIRSTNAME);
		inputLastName = (EditText) findViewById(R.id.LASTNAME);
		Button bSubmit = (Button) findViewById(R.id.SAVE);
		Button bCancel = (Button) findViewById(R.id.CANCEL);

		// Set onClickListener for submit
		bSubmit.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View view)
			{
				// Call the person creator
				new CreateNewPerson().execute();
			}
		});

		// Set onClickListener for cancel
		bCancel.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View view)
			{
				// Call the StartController
				Intent i = new Intent(RegisterController.this, StartController.class);
				startActivity(i);
			}
		});
	}

	/**
	 * CreateNewPerson - Registers a new user
	 * 
	 * @author Oliver Neubauer
	 * @version 1.0
	 *
	 */
	
	class CreateNewPerson extends AsyncTask<String, String, String>
	{
		/**
		 * Displays a progress dialog
		 */
		
		@Override
		protected void onPreExecute()
		{
			super.onPreExecute();
			pDialog = new ProgressDialog(RegisterController.this);
			pDialog.setMessage(IMessages.Status.CREATING_PERSON);
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(true);
			pDialog.show();
		}

		/**
		 * Validates the user inputs.
		 * Create a new user.
		 * Create new notification settings.
		 * Create new event settings
		 * 
		 * @param args the parameters as array
		 * @return an error message or null in case of success
		 */
		
		protected String doInBackground(String... args)
		{
			// Fetch the user input
			String eMail = inputEMail.getText().toString();
			String password = inputPassword.getText().toString();
			String passwordConfirm = inputPasswordConfirm.getText().toString();
			String firstName = inputFirstName.getText().toString();
			String lastName = inputLastName.getText().toString();

			// Validate email
			if (!InputValidator.isEmailValid(eMail))
				return IMessages.Error.INVALID_EMAIL;

			// Validate password (only the length of them)
			if (!InputValidator.isStringLengthInRange(password, 0, 255))
				return IMessages.Error.INVALID_PASSWORD;

			// Validate the equality of password and confirm password
			if (!password.equals(passwordConfirm))
				return IMessages.Error.PASSWORDS_DO_NOT_MATCH;

			// Validate the first name (only the length of them)
			if (!InputValidator.isStringLengthInRange(firstName, 0, 255))
				return IMessages.Error.INVALID_FIRSTNAME;

			// Validate the last name  (only the length of them)
			if (!InputValidator.isStringLengthInRange(lastName, 0, 255))
				return IMessages.Error.INVALID_LASTNAME;

			params = new ArrayList<NameValuePair>();
			
			// The required parameters
			params.add(new BasicNameValuePair("do", "read"));
			params.add(new BasicNameValuePair("eMail", eMail));
			
			// Make the request to check, whether person already exists
			json = jsonParser.makeHttpsRequest(IUniformResourceLocator.URL.URL_PERSON, "GET", params, RegisterController.this);

			try
			{
				int success = json.getInt(TAG_SUCCESS);

				// Person already exists
				if (success == 1)
				{
					return IMessages.Error.DUPLICATE_PERSON;
				}
			}
			// In case of error
			catch(Exception e)
			{
				// Back to StartController
				Intent i = new Intent(RegisterController.this, StartController.class);
				startActivity(i);
			}

			// The current date
			DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
			Date date = new Date();
			
			// The required parameters
			params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair("do", "create"));
			params.add(new BasicNameValuePair("eMail", eMail));
			params.add(new BasicNameValuePair("password", password));
			params.add(new BasicNameValuePair("firstName", firstName));
			params.add(new BasicNameValuePair("lastName", lastName));
			params.add(new BasicNameValuePair("created", dateFormat.format(date).toString()));

			// Make the request to create the person
			json = jsonParser.makeHttpsRequest(IUniformResourceLocator.URL.URL_PERSON, "GET", params, RegisterController.this);

			try
			{
				// Fetch the id of new person
				newPersonId = json.getInt(TAG_SUCCESS);
				
				// In case of success
				if (newPersonId.intValue() != 0)
				{
					// The required parameters
					params = new ArrayList<NameValuePair>();
					params.add(new BasicNameValuePair("do", "create"));
					params.add(new BasicNameValuePair("personId", newPersonId.toString()));
					
					// Make the request to create the notification settings
					json = jsonParser.makeHttpsRequest(IUniformResourceLocator.URL.URL_NOTIFICATIONSETTINGS, "GET", params, RegisterController.this);
					
					success = json.getInt(TAG_SUCCESS);
					
					// In case of success
					if (success == 1)
					{
						// The required parameters
						params = new ArrayList<NameValuePair>();
						params.add(new BasicNameValuePair("do", "create"));
						params.add(new BasicNameValuePair("personId", newPersonId.toString()));
						
						// Make the request to create event settings
						json = jsonParser.makeHttpsRequest(IUniformResourceLocator.URL.URL_EVENTSETTINGS, "GET", params, RegisterController.this);
						
						success = json.getInt(TAG_SUCCESS);
						
						// In case of success
						if(success == 1)
						{
							return null;
						}
						// In case of no success
						else
						{
							// Back to StartController
							Intent i = new Intent(RegisterController.this, StartController.class);
							startActivity(i);
						}
					}
					// In case of no success
					else
					{
						// Back to StartController
						Intent i = new Intent(RegisterController.this, StartController.class);
						startActivity(i);
					}
				}
				// In case of no success
				else
				{
					return IMessages.Error.PERSON_NOT_CREATED;
				}
			}
			// In case of error
			catch(Exception e)
			{
				// Back to StartController
				Intent i = new Intent(RegisterController.this, StartController.class);
				startActivity(i);
			}

			return null;
		}

		/**
		 * Dismisses the progress dialog.
		 * Displays a negative message in case of error.
		 * Starts the LoginController and displays a positive message in case of success
		 * 
		 * @param message the error message or null in case of success
		 */
		
		protected void onPostExecute(String message)
		{
			super.onPostExecute(message);
			
			// Dismiss the dialog once done
			pDialog.dismiss();

			// Error Message
			if (message != null)
			{
				Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
			}
			// Success
			else
			{
				Toast.makeText(getApplicationContext(), IMessages.Success.PERSON_SUCCESSFUL_CREATED, Toast.LENGTH_LONG).show();
				
				// Close screen
				finish();
				
				// Start LoginController
				Intent i = new Intent(getApplicationContext(), LoginController.class);
				startActivity(i);
			}
		}
	}
}