package fhkl.de.orgapp.controller.profile;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;
import fhkl.de.orgapp.R;
import fhkl.de.orgapp.util.IMessages;
import fhkl.de.orgapp.util.JSONParser;
import fhkl.de.orgapp.util.MenuActivity;
import fhkl.de.orgapp.util.UserData;

public class PrivateInfoController extends MenuActivity
{
	private static String URL_UPDATE_PERSON_PRIVATE_INFO = "http://pushrply.com/update_person_private_info.php";
	private static final int DATE_DIALOG_ID = 1;
	
	private ProgressDialog pDialog;
	
	TextView textFirstName, textLastName, textBirthday, textGender, birthdayNew;
	RadioButton textGenderMale, textGenderFemale;
	EditText firstNameNew, lastNameNew;
	char genderNew;
	Button selectBirthdayButton, changeButton, cancelButton;
	int yearNew, monthNew, dayNew;
	String[] birthdayArray;
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.private_information);
		
		getViews();
		setTexts();
		setTextSizes();
		
		birthdayArray = splitBirthday(UserData.getBIRTHDAY());
		
		yearNew = Integer.parseInt(birthdayArray[0]);
		monthNew = Integer.parseInt(birthdayArray[1]) - 1;
		dayNew = Integer.parseInt(birthdayArray[2]);
	}
	
	@Override
	protected Dialog onCreateDialog(int id)
	{
		return new DatePickerDialog(this, dateSetListener, yearNew, monthNew, dayNew);
	}

	@Override
	protected void onPrepareDialog(int id, Dialog dialog)
	{
		DatePickerDialog dateDialog = ((DatePickerDialog) dialog);
		
		dateDialog.updateDate(yearNew, monthNew, dayNew);
		dateDialog.setTitle(getString(R.string.SET_NEW_BIRTHDAY));
		Button saveButton = dateDialog.getButton(DatePickerDialog.BUTTON_POSITIVE);
		Button cancelButton = dateDialog.getButton(DatePickerDialog.BUTTON_NEGATIVE);
		saveButton.setText(getString(R.string.SAVE));
		cancelButton.setText(getString(R.string.CANCEL));
		
		//TODO set month to english
	}
	
	private DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener()
	{
		public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth)
        {
			yearNew = year;
            monthNew = monthOfYear;
            dayNew = dayOfMonth;
            updateBirthday();
        }
	};
	
	private String[] splitBirthday(String birthdayString)
	{
		String[] result = new String[3];
		
		if(birthdayString == null)
			return result;
		
		result = birthdayString.split("-");
		
		return result;
	}
	
	private void updateBirthday()
	{
		StringBuilder birthdayString = new StringBuilder();
		birthdayString.append(yearNew);
		birthdayString.append("-");
		birthdayString.append(pad(monthNew + 1));
		birthdayString.append("-");
		birthdayString.append(pad(dayNew));
		
		birthdayNew.setText(birthdayString);
	}
	
	private static String pad(int digit)
	{
		if (digit >= 10)
            return String.valueOf(digit);
        else
            return "0" + String.valueOf(digit);
	}
	
	private void getViews()
	{
		textFirstName = (TextView) findViewById(R.id.PRIVATE_INFO_TEXT_FIRST_NAME);
		textLastName = (TextView) findViewById(R.id.PRIVATE_INFO_TEXT_LAST_NAME);
		textBirthday = (TextView) findViewById(R.id.PRIVATE_INFO_TEXT_BIRTHDAY);
		textGender = (TextView) findViewById(R.id.PRIVATE_INFO_TEXT_GENDER);
		textGenderMale = (RadioButton) findViewById(R.id.PRIVATE_INFO_USER_GENDER_MALE);
		textGenderFemale = (RadioButton) findViewById(R.id.PRIVATE_INFO_USER_GENDER_FEMALE);
		
		firstNameNew = (EditText) findViewById(R.id.PRIVATE_INFO_USER_FIRST_NAME);
		lastNameNew = (EditText) findViewById(R.id.PRIVATE_INFO_USER_LAST_NAME);
		birthdayNew = (TextView) findViewById(R.id.PRIVATE_INFO_USER_BIRTHDAY);
		
		selectBirthdayButton = (Button) findViewById(R.id.PRIVATE_INFO_PICK_BIRTHDAY_BUTTON);
		changeButton = (Button) findViewById(R.id.CHANGE_PRIVATE_INFO_BUTTON);
		cancelButton = (Button) findViewById(R.id.CANCEL_PRIVATE_INFO_VIEW);
	}
	
	private void setTexts()
	{
		textFirstName.setText(getString(R.string.FIRSTNAME_MUST_HAVE) + ":");
		textLastName.setText(getString(R.string.LASTNAME_MUST_HAVE) + ":");
		textBirthday.setText(getString(R.string.BIRTHDAY_MUST_HAVE) + ":");
		textGender.setText(getString(R.string.GENDER_MUST_HAVE) + ":");
		textGenderMale.setText(getString(R.string.MALE));
		textGenderFemale.setText(getString(R.string.FEMALE));
		
		firstNameNew.setText(UserData.getFIRST_NAME());
		lastNameNew.setText(UserData.getLAST_NAME());
		birthdayNew.setText(UserData.getBIRTHDAY());
		
		if(UserData.getGENDER().equals("m"))
			textGenderMale.setChecked(true);
		else
			textGenderFemale.setChecked(true);
		
		selectBirthdayButton.setText(getString(R.string.CHANGE_BIRTHDAY));
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
		textGenderMale.setTextSize(userTextSize);
		textGenderFemale.setTextSize(userTextSize);
		
		firstNameNew.setTextSize(userTextSize);
		lastNameNew.setTextSize(userTextSize);
		birthdayNew.setTextSize(userTextSize);
	}
	
	public void selectBirthday(View v)
	{
		showDialog(DATE_DIALOG_ID);
	}
	
	public void selectGender(View view)
	{
		switch(view.getId())
		{
			case R.id.PRIVATE_INFO_USER_GENDER_MALE:
				genderNew = 'm';
				break;
			
			case R.id.PRIVATE_INFO_USER_GENDER_FEMALE:
				genderNew = 'w';
				break;
		}
	}
	
	public void changePrivateInfo(View view)
	{
		if(!isPrivateInfoComplete())
		{
			Toast.makeText(getApplicationContext(), IMessages.REQUIRED_FIELDS_NOT_COMPLETE, Toast.LENGTH_LONG).show();
			return;
		}
		
		if(!hasPrivateInfoChanged())
		{
			Toast.makeText(getApplicationContext(), IMessages.PRIVATE_INFO_NOT_UPDATED, Toast.LENGTH_LONG).show();
			return;
		}
		
		new PrivateInfoUpdater().execute();
	}
	
	public void cancelPrivateInfoView(View view)
	{
		backToProfile();
	}
	
	private void backToProfile()
	{
		Intent intent = new Intent(this, ProfileController.class);
		startActivity(intent);
	}
	
	private boolean isPrivateInfoComplete()
	{
		if(
		     firstNameNew.getText().toString().equals("")
		     || lastNameNew.getText().toString().equals("")
		  )
			return false;
		
		return true;
	}
	
	private boolean hasPrivateInfoChanged()
	{
		if(
		     firstNameNew.getText().toString().equals(UserData.getFIRST_NAME())
		     && lastNameNew.getText().toString().equals(UserData.getLAST_NAME())
		     && birthdayNew.getText().toString().equals(UserData.getBIRTHDAY())
		     && String.valueOf(genderNew).equals(UserData.getGENDER())
		  )
			return false;
		
		return true;
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
			pDialog.setCancelable(true);
			pDialog.show();
		}

		@Override
		protected String doInBackground(String... arg0)
		{
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair("personId", UserData.getPERSONID()));
			params.add(new BasicNameValuePair("firstName", firstNameNew.getText().toString()));
			params.add(new BasicNameValuePair("lastName", lastNameNew.getText().toString()));
			params.add(new BasicNameValuePair("birthday", birthdayNew.getText().toString()));
			params.add(new BasicNameValuePair("gender", String.valueOf(genderNew)));
			
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
			
			UserData.setFIRST_NAME(firstNameNew.getText().toString());
			UserData.setLAST_NAME(lastNameNew.getText().toString());
			UserData.setBIRTHDAY(birthdayNew.getText().toString());
			UserData.setGENDER(String.valueOf(genderNew));
			
			backToProfile();
		}
	}
}