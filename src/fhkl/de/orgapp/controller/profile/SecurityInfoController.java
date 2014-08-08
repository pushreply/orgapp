package fhkl.de.orgapp.controller.profile;

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
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import fhkl.de.orgapp.R;
import fhkl.de.orgapp.util.IMessages;
import fhkl.de.orgapp.util.IUniformResourceLocator;
import fhkl.de.orgapp.util.JSONParser;
import fhkl.de.orgapp.util.MenuActivity;
import fhkl.de.orgapp.util.data.UserData;
import fhkl.de.orgapp.util.validator.InputValidator;

/**
 * SecurityInfoController - Handles the data for edit the security information of the user
 * 
 * @author Oliver Neubauer
 * @version ?
 *
 */

public class SecurityInfoController extends MenuActivity
{
	// For progress dialog
	private ProgressDialog pDialog;
	
	// Required variables for layout fields
	TextView textEmail, textEmailConfirm;
	EditText emailNew, emailConfirmNew;
	Button changeButton, cancelButton;
	
	/**
	 * Calls the required methods
	 * 
	 * @param savedInstanceState
	 */
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		
		// Set the layout
		setContentView(R.layout.security_information);
		
		// Check for new notifications and signal the user
		checkOnNewNotificationsAndNotifyUser();
		
		// Fetch the views
		getViews();
		
		// Set the texts of the views
		setTexts();
		
		// Set the text sizes
		setTextSizes();
	}
	
	/**
	 * Fetches the views by id
	 */
	
	private void getViews()
	{
		textEmail = (TextView) findViewById(R.id.SECURITY_INFO_TEXT_EMAIL);
		emailNew = (EditText) findViewById(R.id.SECURITY_INFO_USER_EMAIL);
		textEmailConfirm = (TextView) findViewById(R.id.SECURITY_INFO_TEXT_EMAIL_CONFIRM);
		emailConfirmNew = (EditText) findViewById(R.id.SECURITY_INFO_USER_EMAIL_CONFIRM);
		changeButton = (Button) findViewById(R.id.CHANGE_SECURITY_INFO_BUTTON);
		cancelButton = (Button) findViewById(R.id.CANCEL_SECURITY_INFO_VIEW);
	}
	
	/**
	 * Sets the texts of the views
	 */
	
	private void setTexts()
	{
		textEmail.setText(getString(R.string.EMAIL_MUST_HAVE) + ":");
		textEmailConfirm.setText(getString(R.string.EMAIL_CONFIRM_MUST_HAVE) + ":");
		emailNew.setText(UserData.getEMAIL());
		emailConfirmNew.setHint(UserData.getEMAIL());
		changeButton.setText(getString(R.string.CHANGE_INFO));
		cancelButton.setText(getString(R.string.CANCEL));
	}
	
	/**
	 * Sets the text sizes
	 */
	
	private void setTextSizes()
	{
		// Android specific sizes
		textEmail.setTextAppearance(this, android.R.style.TextAppearance_Large);
		emailNew.setTextAppearance(this, android.R.style.TextAppearance_Medium);
		textEmailConfirm.setTextAppearance(this, android.R.style.TextAppearance_Large);
		emailConfirmNew.setTextAppearance(this, android.R.style.TextAppearance_Medium);
	}
	
	/**
	 * Checks the data and calls the updater
	 * 
	 * @param view the associated view
	 */
	
	public void changeSecurityInfo(View view)
	{
		// Error message in case of incomplete fields
		if(!isSecurityInfoComplete())
		{
			Toast.makeText(getApplicationContext(), IMessages.Error.REQUIRED_FIELDS_NOT_COMPLETE, Toast.LENGTH_LONG).show();
			return;
		}
		
		// Error message in case of invalid email
		if(!isEmailValid())
		{
			Toast.makeText(getApplicationContext(), IMessages.Error.INVALID_EMAIL, Toast.LENGTH_LONG).show();
			return;
		}
		
		// Error message in case of no match of the two emails
		if(!emailNew.getText().toString().equals(emailConfirmNew.getText().toString()))
		{
			Toast.makeText(getApplicationContext(), IMessages.Error.EMAIL_ADDRESSES_DO_NOT_MATCH, Toast.LENGTH_LONG).show();
			return;
		}
		
		// Error message in case of undone changes
		if(!hasSecurityInfoChanged())
		{
			Toast.makeText(getApplicationContext(), IMessages.Error.SECURITY_INFO_NOT_UPDATED, Toast.LENGTH_LONG).show();
			return;
		}
		
		// Update the security information
		new SecurityInfoUpdater().execute();
	}
	
	/**
	 * Goes back to profile
	 * 
	 * @param view
	 */
	
	public void cancelSecurityInfoView(View view)
	{
		backToProfile();
	}
	
	/**
	 * Starts the ProfileController
	 */
	
	private void backToProfile()
	{
		// Start the ProfileController
		Intent intent = new Intent(this, ProfileController.class);
		startActivity(intent);
	}
	
	/**
	 * Checks, whether all required fields are complete
	 * 
	 * @return false, if one field is missing. True otherwise
	 */
	
	private boolean isSecurityInfoComplete()
	{
		if(
		     emailNew.getText().toString().equals("")
			 || emailConfirmNew.getText().toString().equals("")
		  )
				return false;
			
			return true;
	}
	
	/**
	 * Checks, whether email is valid
	 * 
	 * @return false, if email is invalid. True, otherwise
	 */
	
	private boolean isEmailValid()
	{
		if(!InputValidator.isEmailValid(emailNew.getText().toString()))
			return false;
		
		return true;
	}
	
	/**
	 * Checks, whether changes were done
	 *
	 * @return false, if the new email contains the same data as previous. True otherwise
	 */
	
	private boolean hasSecurityInfoChanged()
	{
		if(emailNew.getText().toString().equals(UserData.getEMAIL()))
			return false;
		
		return true;
	}
	
	/**
	 * SecurityInfoUpdater - Updates the security information
	 * 
	 * @author Oliver Neubauer
	 * @version ?
	 *
	 */
	
	class SecurityInfoUpdater extends AsyncTask<String, String, String>
	{
		/**
		 * Defines a progress dialog within the main thread
		 */
		
		@Override
		protected void onPreExecute()
		{
			super.onPreExecute();
			
			// Display a progress dialog
			pDialog = new ProgressDialog(SecurityInfoController.this);
			pDialog.setMessage(IMessages.Status.UPDATING_SECURITY_INFO);
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(true);
			pDialog.show();
		}

		/**
		 * Prepares and makes a http-request within the background thread
		 * 
		 * @param arg0 the arguments as String array
		 */
		
		@Override
		protected String doInBackground(String... arg0)
		{
			// Required parameters for the request
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair("do", "update"));
			params.add(new BasicNameValuePair("personId", UserData.getPERSONID()));
			
			// The new value
			params.add(new BasicNameValuePair("eMail", emailNew.getText().toString()));
			
			// This values are not changeable in this layout, but required for the update
			params.add(new BasicNameValuePair("firstName", UserData.getFIRST_NAME()));
			params.add(new BasicNameValuePair("lastName", UserData.getLAST_NAME()));
			params.add(new BasicNameValuePair("birthday", UserData.getBIRTHDAY()));
			params.add(new BasicNameValuePair("gender", UserData.getGENDER()));
			
			// Make the request
			JSONObject json = new JSONParser().makeHttpsRequest(IUniformResourceLocator.URL.URL_PERSON, "GET", params, SecurityInfoController.this);
			
			try
			{
				int success = json.getInt("success");
				
				// In case of success
				if(success == 1)
				{
					return IMessages.Success.UPDATE_WAS_SUCCESSFUL;
				}
				// In case of no success
				else
				{
					return IMessages.Error.UPDATE_WAS_NOT_SUCCESSFUL;
				}
			}
			// In case of Error
			catch(Exception e)
			{
				e.getStackTrace();
				pDialog.dismiss();
				// Logout the user
				logout();
			}
			
			return null;
		}

		/**
		 * Displays an user message within main thread.
		 * Sets the updated user data
		 *
		 * @param message the message to be displayed
		 */
		
		@Override
		protected void onPostExecute(String message)
		{
			super.onPostExecute(message);
			
			// Hide the progress dialog
			pDialog.dismiss();
			
			// Display a message if available
			if(message != null)
				Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
			
			// Set the updated data to the POJO
			UserData.setEMAIL(emailNew.getText().toString());
			
			// Back to the profile of the user
			backToProfile();
		}
	}
}