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
	private static String URL_UPDATE_PERSON_SECURITY_INFO = "http://pushrply.com/update_person_security_info.php";
	
	private ProgressDialog pDialog;
	
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
		setContentView(R.layout.security_information);
		checkOnNewNotificationsAndNotifyUser();
		
		getViews();
		setTexts();
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
		if(!isSecurityInfoComplete())
		{
			Toast.makeText(getApplicationContext(), IMessages.Error.REQUIRED_FIELDS_NOT_COMPLETE, Toast.LENGTH_LONG).show();
			return;
		}
		
		if(!isEmailValid())
		{
			Toast.makeText(getApplicationContext(), IMessages.Error.INVALID_EMAIL, Toast.LENGTH_LONG).show();
			return;
		}
		
		// Check the match of the two email entered by the user
		if(!emailNew.getText().toString().equals(emailConfirmNew.getText().toString()))
		{
			Toast.makeText(getApplicationContext(), IMessages.Error.EMAIL_ADDRESSES_DO_NOT_MATCH, Toast.LENGTH_LONG).show();
			return;
		}
		
		if(!hasSecurityInfoChanged())
		{
			Toast.makeText(getApplicationContext(), IMessages.Error.SECURITY_INFO_NOT_UPDATED, Toast.LENGTH_LONG).show();
			return;
		}
		
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
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair("personId", UserData.getPERSONID()));
			params.add(new BasicNameValuePair("eMail", emailNew.getText().toString()));
			
			JSONObject json = new JSONParser().makeHttpRequest(URL_UPDATE_PERSON_SECURITY_INFO, "GET", params);
			
			try
			{
				int success = json.getInt("success");
				
				if(success == 1)
				{
					return IMessages.Success.UPDATE_WAS_SUCCESSFUL;
				}
				else
				{
					return IMessages.Error.UPDATE_WAS_NOT_SUCCESSFUL;
				}
			}
			catch(Exception e)
			{
				e.getStackTrace();
				// logout in case of error
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
			pDialog.dismiss();
			
			if(message != null)
				Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
			
			UserData.setEMAIL(emailNew.getText().toString());
			
			backToProfile();
		}
	}
}