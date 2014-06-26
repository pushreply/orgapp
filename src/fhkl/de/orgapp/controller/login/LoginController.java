package fhkl.de.orgapp.controller.login;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import fhkl.de.orgapp.R;
import fhkl.de.orgapp.controller.calendar.CalendarController;
import fhkl.de.orgapp.controller.start.StartController;
import fhkl.de.orgapp.util.IMessages;
import fhkl.de.orgapp.util.JSONParser;

public class LoginController extends Activity
{
	// Progress Dialog
	private ProgressDialog pDialog;
	
	private Button bSubmit;
	private Button bCancel;

	EditText inputEMail;
	EditText inputPassword;
	JSONArray person = null;
	JSONObject e;

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login);

		bSubmit = (Button) findViewById(R.id.SAVE);
		bCancel = (Button) findViewById(R.id.CANCEL);

		inputEMail = (EditText) findViewById(R.id.EMAIL);
		inputPassword = (EditText) findViewById(R.id.PASSWORD);

		bSubmit.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				new Validator().execute();
			}
		});

		bCancel.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				Intent i = new Intent(LoginController.this, StartController.class);
				startActivity(i);
			}
		});
	}

	class Validator extends AsyncTask<String, String, String>
	{
		final static String TAG = "Validator";
		
		public Validator()
		{}
		
		@Override
		protected void onPreExecute()
		{
			super.onPreExecute();
			
			pDialog = new ProgressDialog(LoginController.this);
			pDialog.setMessage(IMessages.CHECKING_DATA);
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(true);
			pDialog.show();
		}
		
		@Override
		protected String doInBackground(String... arg0)
		{
			// url to select a person
			String urlSelectPerson = "http://pushrply.com/select_person_by_email.php";
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair("eMail", inputEMail.getText().toString()));
			
			JSONObject json = new JSONParser().makeHttpRequest(urlSelectPerson, "GET", params);

			try
			{
				int success = json.getInt("success");
				
				if(success == 1)
				{
					person = json.getJSONArray("person");
					
					e = person.getJSONObject(0);
						
					String eMail = e.getString("eMail");
						
					if (eMail.equals(inputEMail.getText().toString()))
					{
						String password = e.getString("password");
						//verschlüsselung
							
						if (password.equals(inputPassword.getText().toString()))
						{
							//invokes onPostExecute(String)
							return null;
						}
						else
						{
							//invokes onPostExecute(String)
							return IMessages.INVALID_PASSWORD;
						}
					}
					else
					{
						//invokes onPostExecute(String)
						return IMessages.INVALID_USER;
					}
				}				
				else
				{
					//invokes onPostExecute(String)
					return IMessages.INVALID_USER;
				}
			}
			catch (JSONException e)
			{
				System.out.println("Error in Validator.doInBackground(String... arg0): " + e.getMessage());
				e.printStackTrace();
			}
			
			//invokes onPostExecute(String)
			return null;
		}
		
		@Override
		protected void onPostExecute(String message)
		{
			pDialog.dismiss();
			
			//error message
			if(message != null)
			{
				Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
			}
			//everything successful
			else
			{
				try
				{
					Intent intent = new Intent(getApplicationContext(), CalendarController.class);
					
					intent.putExtra("UserId", e.getString("personId"));
					intent.putExtra("UserFirstName", e.getString("firstName"));
					intent.putExtra("UserLastName", e.getString("lastName"));
					intent.putExtra("UserBirthday", e.getString("birthday"));
					intent.putExtra("UserGender", e.getString("gender"));
					intent.putExtra("UserEmail", e.getString("eMail"));
					intent.putExtra("UserMemberSince", e.getString("created"));
					
					startActivity(intent);
				}
				catch(JSONException e)
				{
					System.out.println("Error in Validator.onPostExecute(String message): " + e.getMessage());
					e.printStackTrace();
				}
			}
		}
	}
}