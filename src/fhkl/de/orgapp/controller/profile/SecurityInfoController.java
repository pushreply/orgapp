package fhkl.de.orgapp.controller.profile;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import android.app.ProgressDialog;
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

public class SecurityInfoController extends MenuActivity
{
	private static String URL_UPDATE_PERSON_SECURITY_INFO = "http://pushrply.com/update_person_security_info.php";
	
	private ProgressDialog pDialog;
	
	TextView textEmail, textEmailConfirm;
	EditText emailNew, emailConfirmNew;
	Button changeButton, cancelButton;
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.security_information);
		
		getViews();
		setTexts();
		setTextSizes();
	}
	
	private void getViews()
	{
		textEmail = (TextView) findViewById(R.id.SECURITY_INFO_TEXT_EMAIL);
		emailNew = (EditText) findViewById(R.id.SECURITY_INFO_USER_EMAIL);
		textEmailConfirm = (TextView) findViewById(R.id.SECURITY_INFO_TEXT_EMAIL_CONFIRM);
		emailConfirmNew = (EditText) findViewById(R.id.SECURITY_INFO_USER_EMAIL_CONFIRM);
		changeButton = (Button) findViewById(R.id.CHANGE_SECURITY_INFO_BUTTON);
		cancelButton = (Button) findViewById(R.id.CANCEL_SECURITY_INFO_VIEW);
	}
	
	private void setTexts()
	{
		textEmail.setText(getString(R.string.EMAIL_MUST_HAVE) + ":");
		textEmailConfirm.setText(getString(R.string.EMAIL_CONFIRM_MUST_HAVE) + ":");
		emailNew.setText(getIntent().getStringExtra("Email"));
		emailConfirmNew.setHint(getIntent().getStringExtra("Email"));
		changeButton.setText(getString(R.string.CHANGE_INFO));
		cancelButton.setText(getString(R.string.CANCEL));
	}
	
	private void setTextSizes()
	{
		int userTextSize = (int) getResources().getDimension(R.dimen.PROFIL_USER_TEXT_SIZE);
		
		textEmail.setTextSize(userTextSize);
		emailNew.setTextSize(userTextSize);
		textEmailConfirm.setTextSize(userTextSize);
		emailConfirmNew.setTextSize(userTextSize);
	}
	
	public void changeSecurityInfo(View view)
	{
		if(!isSecurityInfoComplete())
		{
			Toast.makeText(getApplicationContext(), IMessages.REQUIRED_FIELDS_NOT_COMPLETE, Toast.LENGTH_LONG).show();
			return;
		}
		
		if(!isEmailValid())
		{
			Toast.makeText(getApplicationContext(), IMessages.INVALID_EMAIL, Toast.LENGTH_LONG).show();
			return;
		}
		
		if(!emailNew.getText().toString().equals(emailConfirmNew.getText().toString()))
		{
			Toast.makeText(getApplicationContext(), IMessages.EMAIL_ADDRESSES_DO_NOT_MATCH, Toast.LENGTH_LONG).show();
			return;
		}
		
		if(!hasSecurityInfoChanged())
		{
			Toast.makeText(getApplicationContext(), IMessages.SECURITY_INFO_NOT_UPDATED, Toast.LENGTH_LONG).show();
			return;
		}
		
		new SecurityInfoUpdater().execute();
	}
	
	public void cancelSecurityInfoView(View view)
	{
		finish();
	}
	
	private boolean isSecurityInfoComplete()
	{
		if(
		     emailNew.getText().toString().equals("")
			 || emailConfirmNew.getText().toString().equals("")
		  )
				return false;
			
			return true;
	}
	
	//TODO in eine util-Klasse auslagern
	private boolean isEmailValid()
	{
		if(!emailNew.getText().toString().matches("[A-Z0-9a-z._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,4}"))
			return false;
		
		return true;
	}
	
	private boolean hasSecurityInfoChanged()
	{
		if(emailNew.getText().toString().equals(getIntent().getStringExtra("Email")))
			return false;
		
		return true;
	}
	
	class SecurityInfoUpdater extends AsyncTask<String, String, String>
	{
		@Override
		protected void onPreExecute()
		{
			super.onPreExecute();
			
			pDialog = new ProgressDialog(SecurityInfoController.this);
			pDialog.setMessage(IMessages.UPDATING_SECURITY_INFO);
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(false);
			pDialog.show();
		}

		@Override
		protected String doInBackground(String... arg0)
		{
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair("personId", getIntent().getStringExtra("UserId")));
			params.add(new BasicNameValuePair("eMail", emailNew.getText().toString()));
			
			JSONObject json = new JSONParser().makeHttpRequest(URL_UPDATE_PERSON_SECURITY_INFO, "GET", params);
			
			try
			{
				int success = json.getInt("success");
				
				if(success == 1)
				{
					return IMessages.UPDATE_WAS_SUCCESSFUL;
				}
				else
				{
					return IMessages.UPDATE_WAS_NOT_SUCCESSFUL;
				}
			}
			catch(Exception e)
			{
				System.out.println("Error in SecurityInfoUpdater.doInBackground(String... arg0): " + e.getMessage());
				e.getStackTrace();
			}
			
			return null;
		}

		@Override
		protected void onPostExecute(String message)
		{
			super.onPostExecute(message);
			pDialog.dismiss();
			
			if(message != null)
				Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
			
			logout();
		}
	}
}