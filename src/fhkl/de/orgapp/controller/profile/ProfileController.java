package fhkl.de.orgapp.controller.profile;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.graphics.Paint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.TextView;
import fhkl.de.orgapp.R;
import fhkl.de.orgapp.util.IMessages;
import fhkl.de.orgapp.util.JSONParser;
import fhkl.de.orgapp.util.MenuActivity;

public class ProfileController extends MenuActivity
{
	// Progress Dialog
	private ProgressDialog pDialog;
	
	private static String URL_SELECT_PERSON = "http://pushrply.com/select_person_by_personId.php";
	private JSONObject person = null;
	
	TextView textFirstName, textLastName, textBirthday, textGender, textMemberSince, textEmail, textPrivateInformation, textSecurityInformation, textGeneralInformation;
	TextView firstName, lastName, birthday, gender, email, memberSince;
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.profil);
		
		textFirstName = (TextView) findViewById(R.id.TEXT_FIRST_NAME);
		textLastName = (TextView) findViewById(R.id.TEXT_LAST_NAME);
		textBirthday = (TextView) findViewById(R.id.TEXT_BIRTHDAY);
		textGender = (TextView) findViewById(R.id.TEXT_GENDER);
		textEmail = (TextView) findViewById(R.id.TEXT_EMAIL_PROFIL);
		textMemberSince = (TextView) findViewById(R.id.TEXT_MEMBER_SINCE);
		textPrivateInformation = (TextView) findViewById(R.id.TEXT_PRIVATE_INFORMATION);
		textSecurityInformation = (TextView) findViewById(R.id.TEXT_SECURITY_INFORMATION);
		textGeneralInformation = (TextView) findViewById(R.id.TEXT_GENERAL_INFORMATION);
		
		firstName = (TextView) findViewById(R.id.USER_FIRST_NAME);
		lastName = (TextView) findViewById(R.id.USER_LAST_NAME);
		birthday = (TextView) findViewById(R.id.USER_BIRTHDAY);
		gender = (TextView) findViewById(R.id.USER_GENDER);
		email = (TextView) findViewById(R.id.USER_EMAIL_PROFIL);
		memberSince = (TextView) findViewById(R.id.USER_MEMBER_SINCE);
		
		new UserDataGetter().execute();
	}
	
	class UserDataGetter extends AsyncTask<String, String, String>
	{
		@Override
		protected void onPreExecute()
		{
			super.onPreExecute();
			
			pDialog = new ProgressDialog(ProfileController.this);
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
					result += ", " + person.getString("eMail");
					result += ", " + person.getString("created");
					
					return result;
				}
			}
			catch(Exception e)
			{
				System.out.println("Error in UserDataGetter.doInBackground(String... arg0): " + e.getMessage());
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
			
			setTexts(datas);
			setTextSizes();
		}
		
		private void setTexts(String[] datas)
		{
			textPrivateInformation.setText(R.string.PRIVATE_INFORMATION);
			textPrivateInformation.setPaintFlags(textPrivateInformation.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
			textFirstName.setText(getString(R.string.FIRSTNAME) + ":");
			textLastName.setText(getString(R.string.LASTNAME) + ":");
			textBirthday.setText(getString(R.string.BIRTHDAY) + ":");
			textGender.setText(getString(R.string.GENDER) + ":");
			
			textSecurityInformation.setText(R.string.SECURITY_INFORMATION);
			textSecurityInformation.setPaintFlags(textSecurityInformation.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
			textEmail.setText(getString(R.string.EMAIL) + ":");
			
			textGeneralInformation.setText(R.string.GENERAL_INFORMATION);
			textGeneralInformation.setPaintFlags(textGeneralInformation.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
			textMemberSince.setText(getString(R.string.MEMBER_SINCE) + ":");
			
			firstName.setText(datas[0]);
			lastName.setText(datas[1]);
			birthday.setText(datas[2]);
			gender.setText(datas[3]);
			email.setText(datas[4]);
			memberSince.setText(datas[5]);
		}
		
		private void setTextSizes()
		{
			int sectionTextSize = (int) getResources().getDimension(R.dimen.PROFIL_SECTION_TEXT_SIZE);
			int userTextSize = (int) getResources().getDimension(R.dimen.PROFIL_USER_TEXT_SIZE);
			
			textPrivateInformation.setTextSize(sectionTextSize);
			textSecurityInformation.setTextSize(sectionTextSize);
			textGeneralInformation.setTextSize(sectionTextSize);
			
			textFirstName.setTextSize(userTextSize);
			textLastName.setTextSize(userTextSize);
			textBirthday.setTextSize(userTextSize);
			textGender.setTextSize(userTextSize);
			textEmail.setTextSize(userTextSize);
			textMemberSince.setTextSize(userTextSize);
			
			firstName.setTextSize(userTextSize);
			lastName.setTextSize(userTextSize);
			birthday.setTextSize(userTextSize);
			gender.setTextSize(userTextSize);
			email.setTextSize(userTextSize);
			memberSince.setTextSize(userTextSize);
		}
	}
}