package fhkl.de.orgapp.controller.login;

import java.util.ArrayList;
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
import fhkl.de.orgapp.util.IMessages;
import fhkl.de.orgapp.util.IUniformResourceLocator;
import fhkl.de.orgapp.util.JSONParser;
import fhkl.de.orgapp.util.validator.InputValidator;

/**
 * ForgotPasswordController - handles action if the user forgot own password. If
 * a request submitted, the server side will send a new password to the
 * associated email account given by the user.
 * 
 * @author Ronaldo Hasiholan
 * @version 4.0
 * 
 */
public class ForgotPasswordController extends Activity {

	// Progress Dialog
	private ProgressDialog pDialog;

	// Variables for the input fields
	EditText inputEMail;

	// For json issues
	JSONParser jsonParser = new JSONParser();
	JSONObject json;

	List<NameValuePair> params;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.forgotpassword);
		// Fetch the views by id
		inputEMail = (EditText) findViewById(R.id.EMAIL);
		Button bSubmit = (Button) findViewById(R.id.FORGOTPASSWORD);
		// Set onClickListener for submit
		bSubmit.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				// execute the request
				new SendNewPassword().execute();
			}
		});
	}

	/**
	 * Begin the background operation using asynchronous task to send request
	 * through the network. The extended AsyncTask, SendNewPassword class send the
	 * request using the given email account. The string "do=forget" is being used
	 * to execute the corresponding PHP 'send a new password' operation in the
	 * back-end. The PHP files on the server side handle the request by creating
	 * and hashing a new password, and send the new password to the given email.
	 * Only the registered email account receives email with a new password.
	 * 
	 */
	class SendNewPassword extends AsyncTask<String, String, String> {
		/**
		 * Displays a progress dialog
		 */

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pDialog = new ProgressDialog(ForgotPasswordController.this);
			pDialog.setMessage(IMessages.Status.REQUESTING_NEW_PASSWORD);
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(true);
			pDialog.show();
		}

		/**
		 * Send a request for a new password. A request is successful if the email
		 * sent is registered in the Database. Otherwise, there will be no email
		 * sent. For security purpose, email check function is not implemented.
		 * Available email validation is only for user input check.
		 * 
		 * @param args the parameters as array
		 * @return null
		 */

		protected String doInBackground(String... args) {
			// Fetch the user input
			String eMail = inputEMail.getText().toString();

			// Validate email
			if (!InputValidator.isEmailValid(eMail))
				return IMessages.Error.INVALID_EMAIL;

			params = new ArrayList<NameValuePair>();

			// The required parameters
			params.add(new BasicNameValuePair("do", "forget"));
			params.add(new BasicNameValuePair("eMail", eMail));

			// Send HTTPS request for a new password
			json = jsonParser.makeHttpsRequest(IUniformResourceLocator.URL.URL_PERSON, "GET", params,
							ForgotPasswordController.this);

			return null;
		}

		protected void onPostExecute(String message) {
			super.onPostExecute(message);

			// Dismiss the dialog once done
			pDialog.dismiss();

			// Error Message
			if (message != null) {
				Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
			}
			// Success
			else {
				Toast.makeText(getApplicationContext(), IMessages.Success.PASSWORD_RENEW_SEND, Toast.LENGTH_LONG).show();

				// Close screen
				finish();

				// Start LoginController
				Intent i = new Intent(getApplicationContext(), LoginController.class);
				startActivity(i);
			}
		}
	}

}
