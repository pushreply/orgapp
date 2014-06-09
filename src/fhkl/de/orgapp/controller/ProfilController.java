package fhkl.de.orgapp.controller;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.TextView;

import com.example.orgapp.R;

import fhkl.de.orgapp.util.IMessages;
import fhkl.de.orgapp.util.JSONParser;
import fhkl.de.orgapp.util.MenuActivity;

public class ProfilController extends MenuActivity
{
	// Progress Dialog
	private ProgressDialog pDialog;
	
	private static String URL_SELECT_PERSON = "http://pushrply.com/select_person_by_personId.php";
	private JSONObject person = null;
	
	TextView textFirstName, textLastName, textBirthday, textGender, textMemberSince, textPassword;
	TextView firstName, lastName, birthday, gender, memberSince, password;
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.profil);
		
		textFirstName = (TextView) findViewById(R.id.TEXT_FIRST_NAME);
		textLastName = (TextView) findViewById(R.id.TEXT_LAST_NAME);
		textBirthday = (TextView) findViewById(R.id.TEXT_BIRTHDAY);
		textGender = (TextView) findViewById(R.id.TEXT_GENDER);
		textMemberSince = (TextView) findViewById(R.id.TEXT_MEMBER_SINCE);
		textPassword = (TextView) findViewById(R.id.TEXT_PASSWORD_PROFIL);
		
		firstName = (TextView) findViewById(R.id.USER_FIRST_NAME);
		lastName = (TextView) findViewById(R.id.USER_LAST_NAME);
		birthday = (TextView) findViewById(R.id.USER_BIRTHDAY);
		gender = (TextView) findViewById(R.id.USER_GENDER);
		memberSince = (TextView) findViewById(R.id.USER_MEMBER_SINCE);
		password = (TextView) findViewById(R.id.USER_PASSWORD_PROFIL);
		
		new UserData().execute();
	}
	
	class UserData extends AsyncTask<String, String, String>
	{
		@Override
		protected void onPreExecute()
		{
			super.onPreExecute();
			
			pDialog = new ProgressDialog(ProfilController.this);
			pDialog.setMessage(IMessages.LOADING_PROFIL);
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
					
					result = person.getString("firstName");
					result += ", " + person.getString("lastName");
					result += ", " + person.getString("birthday");
					result += ", " + person.getString("gender");
					result += ", "; //member sice, TODO wie in der Datenbank speichern?
					result += ", " + person.getString("password");
					
					return result;
				}
			}
			catch(Exception e)
			{
				System.out.println("Error in UserData.doInBackground(String... arg0): " + e.getMessage());
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
			
			if(datas.length != 6)
				return;
			
			textFirstName.setText(getString(R.string.FIRSTNAME) + ":");
			textLastName.setText(getString(R.string.LASTNAME) + ":");
			textBirthday.setText(getString(R.string.BIRTHDAY) + ":");
			textGender.setText(getString(R.string.GENDER) + ":");
			textMemberSince.setText(getString(R.string.MEMBER_SINCE) + ":");
			textPassword.setText(getString(R.string.PASSWORD) + ":");
			
			firstName.setText(datas[0]);
			lastName.setText(datas[1]);
			birthday.setText(datas[2]);
			gender.setText(datas[3]);
			memberSince.setText(datas[4]);
			password.setText(datas[5]);
		}
	}
}