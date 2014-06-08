package fhkl.de.orgapp.controller;

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

import com.example.orgapp.R;

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

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login);

		bSubmit = (Button) findViewById(R.id.SUBMIT);
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
			pDialog.setCancelable(false);
			pDialog.show();
		}
		
		@Override
		protected String doInBackground(String... arg0)
		{
			// url to select a person
			String urlSelectPerson = "http://pushrply.com/select_person.php";
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair("eMail", inputEMail.getText().toString()));
			
			JSONObject json = new JSONParser().makeHttpRequest(urlSelectPerson, "GET", params);

			try
			{
				int success = json.getInt("success");
				
				if (success == 1)
				{
					person = json.getJSONArray("person");
					
						JSONObject e = person.getJSONObject(0);
						
						String eMail = e.getString("eMail");
						
						if (eMail.equals(inputEMail.getText().toString()))
						{
							String password = e.getString("password");
							
							if (password.equals(inputPassword.getText().toString()))
							{
								Intent intent = new Intent(getApplicationContext(), CalendarController.class);
								intent.putExtra("UserId", e.getString("personId"));
								startActivity(intent);
								finish();
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
			super.onPostExecute(message);
			pDialog.dismiss();
			
			if(message != null)
				Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
		}
	}
}