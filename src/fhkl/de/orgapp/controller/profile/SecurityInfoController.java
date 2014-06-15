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
	private static String URL_SELECT_PERSON = "http://pushrply.com/select_person_by_personId.php";
	private static String URL_UPDATE_PERSON_SECURITY_INFO = "http://pushrply.com/update_person_security_info.php";
	
	private ProgressDialog pDialog;
	private JSONObject person = null;
	
	TextView textEmail;
	EditText emailNew;
	Button changeButton, cancelButton;
	
	String emailOld;
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.security_information);
		
		textEmail = (TextView) findViewById(R.id.SECURITY_INFO_TEXT_EMAIL);
		
		emailNew = (EditText) findViewById(R.id.SECURITY_INFO_USER_EMAIL);
		
		changeButton = (Button) findViewById(R.id.CHANGE_SECURITY_INFO_BUTTON);
		cancelButton = (Button) findViewById(R.id.CANCEL_SECURITY_INFO_VIEW);
		
		emailNew.setVisibility(View.INVISIBLE);
		
		new SecurityInfoGetter().execute();
	}
	
	public void changeSecurityInfo(View view)
	{
		if(!isEmailValid())
		{
			Toast.makeText(getApplicationContext(), IMessages.INVALID_EMAIL, Toast.LENGTH_LONG).show();
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
	
	//TODO in eine util-Klasse auslagern
	private boolean isEmailValid()
	{
		if(!emailNew.getText().toString().matches("[A-Z0-9a-z._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,4}"))
			return false;
		
		return true;
	}
	
	private boolean hasSecurityInfoChanged()
	{
		if(emailOld.equals(emailNew.getText().toString()))
			return false;
		
		return true;
	}
	
	// TODO security Informationen aus dem ProfilController holen anstatt erneut aus der Datenbank -> performance
	class SecurityInfoGetter extends AsyncTask<String, String, String>
	{
		@Override
		protected void onPreExecute()
		{
			super.onPreExecute();
			
			pDialog = new ProgressDialog(SecurityInfoController.this);
			pDialog.setMessage(IMessages.LOADING_SECURITY_INFO);
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(false);
			pDialog.show();
		}
		
		@Override
		protected String doInBackground(String... arg0)
		{
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair("personId", getIntent().getStringExtra("UserId")));
			
			JSONObject json = new JSONParser().makeHttpRequest(URL_SELECT_PERSON, "GET", params);
			
			try
			{
				int success = json.getInt("success");
				
				if(success == 1)
				{
					person = json.getJSONArray("person").getJSONObject(0);
					String result;
					
					emailOld = person.getString("eMail");
					
					result = person.getString("eMail");
					
					return result;
				}
			}
			catch(Exception e)
			{
				System.out.println("Error in SecurityInfoGetter.doInBackground(String... arg0): " + e.getMessage());
				e.printStackTrace();
			}
			
			return null;
		}
		
		@Override
		protected void onPostExecute(String result)
		{
			super.onPostExecute(result);
			
			pDialog.dismiss();
			
			if(result == null)
				return;
			
			setText(result);
			setTextSizes();
			doEditTextVisible();
		}
		
		private void setText(String data)
		{
			textEmail.setText(getString(R.string.EMAIL) + ":");
			
			emailNew.setText(data);
			
			changeButton.setText(getString(R.string.CHANGE_INFO));
			cancelButton.setText(getString(R.string.CANCEL));
		}
		
		private void setTextSizes()
		{
			int userTextSize = (int) getResources().getDimension(R.dimen.PROFIL_USER_TEXT_SIZE);
			
			textEmail.setTextSize(userTextSize);
			
			emailNew.setTextSize(userTextSize);
		}
		
		private void doEditTextVisible()
		{
			emailNew.setVisibility(View.VISIBLE);
		}
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
			
			cancelButton.setVisibility(View.INVISIBLE);
		}
	}
}