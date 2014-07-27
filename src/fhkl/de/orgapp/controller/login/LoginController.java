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
import fhkl.de.orgapp.util.data.NotificationSettingsData;
import fhkl.de.orgapp.util.data.UserData;
import fhkl.de.orgapp.util.validator.OutputValidator;

public class LoginController extends Activity
{
	// Progress Dialog
	private ProgressDialog pDialog;
	
	private Button bSubmit;
	private Button bCancel;

	EditText inputEMail;
	EditText inputPassword;
	List<NameValuePair> params;
	JSONObject json, e, notificationSettings;
	JSONArray person = null, notificationSettingsArray;
	int success;

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
			pDialog.setMessage(IMessages.Status.CHECKING_DATA);
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(true);
			pDialog.show();
		}
		
		@Override
		protected String doInBackground(String... arg0)
		{
			// url to select a person
			String urlSelectPerson = "http://pushrply.com/select_person_by_email.php";
			params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair("eMail", inputEMail.getText().toString()));
			
			json = new JSONParser().makeHttpRequest(urlSelectPerson, "GET", params);

			try
			{
				success = json.getInt("success");
				
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
							//fetch notification settings of the user
							String urlSelectNotificationSettings = "http://pushrply.com/get_notification_settings.php";
							params = new ArrayList<NameValuePair>();
							params.add(new BasicNameValuePair("personId", e.getString("personId")));

							json = new JSONParser().makeHttpRequest(urlSelectNotificationSettings, "GET", params);
							
							success = json.getInt("success");
							
							if(success == 1)
							{
								notificationSettingsArray = json.getJSONArray("notificationSettings");
								notificationSettings = notificationSettingsArray.getJSONObject(0);
							}
							else
							{
								System.out.println("No notificationSettings loaded");
							}
							
							//invokes onPostExecute(String)
							return null;
						}
						else
						{
							//invokes onPostExecute(String)
							return IMessages.Error.INVALID_PASSWORD;
						}
					}
					else
					{
						//invokes onPostExecute(String)
						return IMessages.Error.INVALID_USER;
					}
				}				
				else
				{
					//invokes onPostExecute(String)
					return IMessages.Error.INVALID_USER;
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
					
					// set user data
					UserData.setPERSONID(e.getString("personId"));
					UserData.setFIRST_NAME(e.getString("firstName"));
					UserData.setLAST_NAME(e.getString("lastName"));

					if (OutputValidator.isUserBirthdaySet(e.getString("birthday")))
						UserData.setBIRTHDAY(e.getString("birthday"));
					else
						UserData.setBIRTHDAY("");

					UserData.setGENDER(e.getString("gender"));
					UserData.setEMAIL(e.getString("eMail"));
					UserData.setMEMBER_SINCE(e.getString("created"));
					
					// set notification settings of the user
					NotificationSettingsData.setNOTIFICATION_SETTINGS_ID(notificationSettings.getString("notificationSettingsId"));
					NotificationSettingsData.setSHOW_ENTRIES(OutputValidator.isNotificationSettingsShownEntriesSet(notificationSettings.getString("shownEntries")) ? notificationSettings.getString("shownEntries") : "");
					NotificationSettingsData.setGROUP_INVITES(notificationSettings.getInt("groupInvites") == 1 ? "true": "false");
					NotificationSettingsData.setGROUP_EDITED(notificationSettings.getInt("groupEdited") == 1 ? "true": "false");
					NotificationSettingsData.setGROUP_REMOVED(notificationSettings.getInt("groupRemoved") == 1 ? "true": "false");
					NotificationSettingsData.setEVENTS_ADDED(notificationSettings.getInt("eventsAdded") == 1 ? "true": "false");
					NotificationSettingsData.setEVENTS_EDITED(notificationSettings.getInt("eventsEdited") == 1 ? "true": "false");
					NotificationSettingsData.setEVENTS_REMOVED(notificationSettings.getInt("eventsRemoved") == 1 ? "true": "false");
					NotificationSettingsData.setCOMMENTS_ADDED(notificationSettings.getInt("commentsAdded") == 1 ? "true": "false");
					NotificationSettingsData.setCOMMENTS_EDITED(notificationSettings.getInt("commentsEdited") == 1 ? "true": "false");
					NotificationSettingsData.setCOMMENTS_REMOVED(notificationSettings.getInt("commentsRemoved") == 1 ? "true": "false");
					NotificationSettingsData.setPRIVILEGE_GIVEN(notificationSettings.getInt("privilegeGiven") == 1 ? "true": "false");
					NotificationSettingsData.setVIBRATION(notificationSettings.getInt("vibration") == 1 ? "true": "false");
					
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