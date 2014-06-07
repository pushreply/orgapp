package fhkl.de.orgapp.controller;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;

import com.example.orgapp.R;

import fhkl.de.orgapp.util.JSONParser;

public class ProfilController extends Activity
{
	private String eMailLoggedPerson;
	private static String URL_SELECT_PERSON = "http://pushrply.com/select_person.php";
	private JSONObject person = null;
	
	TextView firstName, lastName, birthday, gender, memberSince, password;
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.profil);
		eMailLoggedPerson = getIntent().getStringExtra("UserEmail");
		
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
		protected String doInBackground(String... arg0)
		{
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair("eMail", eMailLoggedPerson));
			
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
			if(result == null)
				return;
			
			super.onPostExecute(result);
			
			//seperate result by ", "
			String[] datas = result.split(", ");
			
			if(datas.length != 6)
				return;
			
			firstName.setText(datas[0]);
			lastName.setText(datas[1]);
			birthday.setText(datas[2]);
			gender.setText(datas[3]);
			memberSince.setText(datas[4]);
			password.setText(datas[5]);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.profil_menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		Intent intent;
		return false;
	}
}