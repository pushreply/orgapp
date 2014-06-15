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

public class PrivateInfoController extends MenuActivity
{
	private static String URL_SELECT_PERSON = "http://pushrply.com/select_person_by_personId.php";
	private static String URL_UPDATE_PERSON_PRIVATE_INFO = "http://pushrply.com/update_person_private_info.php";
	
	private ProgressDialog pDialog;
	private JSONObject person = null;
	
	TextView textFirstName, textLastName, textBirthday, textGender;
	EditText firstNameNew, lastNameNew, birthdayNew, genderNew;
	Button changeButton, cancelButton;
	
	String firstNameOld, lastNameOld, birthdayOld, genderOld;
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.private_information);
		
		textFirstName = (TextView) findViewById(R.id.PRIVATE_INFO_TEXT_FIRST_NAME);
		textLastName = (TextView) findViewById(R.id.PRIVATE_INFO_TEXT_LAST_NAME);
		textBirthday = (TextView) findViewById(R.id.PRIVATE_INFO_TEXT_BIRTHDAY);
		textGender = (TextView) findViewById(R.id.PRIVATE_INFO_TEXT_GENDER);
		
		firstNameNew = (EditText) findViewById(R.id.PRIVATE_INFO_USER_FIRST_NAME);
		lastNameNew = (EditText) findViewById(R.id.PRIVATE_INFO_USER_LAST_NAME);
		birthdayNew = (EditText) findViewById(R.id.PRIVATE_INFO_USER_BIRTHDAY);
		genderNew = (EditText) findViewById(R.id.PRIVATE_INFO_USER_GENDER);
		
		changeButton = (Button) findViewById(R.id.CHANGE_PRIVATE_INFO_BUTTON);
		cancelButton = (Button) findViewById(R.id.CANCEL_PRIVATE_INFO_VIEW);
		
		firstNameNew.setVisibility(View.INVISIBLE);
		lastNameNew.setVisibility(View.INVISIBLE);
		birthdayNew.setVisibility(View.INVISIBLE);
		genderNew.setVisibility(View.INVISIBLE);
		
		new PrivateInfoGetter().execute();
	}
	
	public void changePrivateInfo(View view)
	{
		if(!hasPrivateInfoChanged())
		{
			Toast.makeText(getApplicationContext(), IMessages.PRIVATE_INFO_NOT_UPDATED, Toast.LENGTH_LONG).show();
			return;
		}
		
		if(!isPrivateInfoComplete())
		{
			Toast.makeText(getApplicationContext(), IMessages.REQUIRED_FIELDS_NOT_COMPLETE, Toast.LENGTH_LONG).show();
			return;
		}
		
		new PrivateInfoUpdater().execute();
	}
	
	public void cancelPrivateInfoView(View view)
	{
		finish();
	}
	
	private boolean hasPrivateInfoChanged()
	{
		if(
		     firstNameOld.equals(firstNameNew.getText().toString())
		     && lastNameOld.equals(lastNameNew.getText().toString())
		     && birthdayOld.equals(birthdayNew.getText().toString())
		     && genderOld.equals(genderNew.getText().toString())
		  )
			return false;
		
		return true;
	}
	
	private boolean isPrivateInfoComplete()
	{
		if(
		     firstNameNew.getText().toString().equals("")
		     || lastNameNew.getText().toString().equals("")
		     || birthdayNew.getText().toString().equals("")
		     || genderNew.getText().toString().equals("")
		  )
			return false;
		
		return true;
	}
	
	// TODO private Informationen aus dem ProfilController holen anstatt erneut aus der Datenbank -> performance
	class PrivateInfoGetter extends AsyncTask<String, String, String>
	{
		@Override
		protected void onPreExecute()
		{
			super.onPreExecute();
			
			pDialog = new ProgressDialog(PrivateInfoController.this);
			pDialog.setMessage(IMessages.LOADING_PRIVATE_INFO);
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
					
					firstNameOld = person.getString("firstName");
					lastNameOld = person.getString("lastName");
					birthdayOld = person.getString("birthday");
					genderOld = person.getString("gender");
					
					result = person.getString("firstName");
					result += ", " + person.getString("lastName");
					result += ", " + person.getString("birthday");
					result += ", " + person.getString("gender");
					
					return result;
				}
			}
			catch(Exception e)
			{
				System.out.println("Error in PrivateInfoGetter.doInBackground(String... arg0): " + e.getMessage());
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
			
			//seperate result by ", "
			String[] datas = result.split(", ");
			
			if(datas.length != 4)
				return;
			
			setTexts(datas);
			setTextSizes();
			doEditTextVisible();
		}
		
		private void setTexts(String[] datas)
		{
			textFirstName.setText(getString(R.string.FIRSTNAME) + ":");
			textLastName.setText(getString(R.string.LASTNAME) + ":");
			textBirthday.setText(getString(R.string.BIRTHDAY) + ":");
			textGender.setText(getString(R.string.GENDER) + ":");
			
			firstNameNew.setText(datas[0]);
			lastNameNew.setText(datas[1]);
			birthdayNew.setText(datas[2]);
			genderNew.setText(datas[3]);
			
			changeButton.setText(getString(R.string.CHANGE_INFO));
			cancelButton.setText(getString(R.string.CANCEL));
		}
		
		private void setTextSizes()
		{
			int userTextSize = (int) getResources().getDimension(R.dimen.PROFIL_USER_TEXT_SIZE);
			
			textFirstName.setTextSize(userTextSize);
			textLastName.setTextSize(userTextSize);
			textBirthday.setTextSize(userTextSize);
			textGender.setTextSize(userTextSize);
			
			firstNameNew.setTextSize(userTextSize);
			lastNameNew.setTextSize(userTextSize);
			birthdayNew.setTextSize(userTextSize);
			genderNew.setTextSize(userTextSize);
		}
		
		private void doEditTextVisible()
		{
			firstNameNew.setVisibility(View.VISIBLE);
			lastNameNew.setVisibility(View.VISIBLE);
			birthdayNew.setVisibility(View.VISIBLE);
			genderNew.setVisibility(View.VISIBLE);
		}
	}
	
	class PrivateInfoUpdater extends AsyncTask<String, String, String>
	{
		@Override
		protected void onPreExecute()
		{
			super.onPreExecute();
			
			pDialog = new ProgressDialog(PrivateInfoController.this);
			pDialog.setMessage(IMessages.UPDATING_PRIVATE_INFO);
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(false);
			pDialog.show();
		}

		@Override
		protected String doInBackground(String... arg0)
		{
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair("personId", getIntent().getStringExtra("UserId")));
			params.add(new BasicNameValuePair("firstName", firstNameNew.getText().toString()));
			params.add(new BasicNameValuePair("lastName", lastNameNew.getText().toString()));
			params.add(new BasicNameValuePair("birthday", birthdayNew.getText().toString()));
			params.add(new BasicNameValuePair("gender", genderNew.getText().toString()));
			
			JSONObject json = new JSONParser().makeHttpRequest(URL_UPDATE_PERSON_PRIVATE_INFO, "GET", params);
			
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
				System.out.println("Error in PrivateInfoUpdater.doInBackground(String... arg0): " + e.getMessage());
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