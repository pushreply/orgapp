package fhkl.de.orgapp.controller.profile;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import fhkl.de.orgapp.R;
import fhkl.de.orgapp.util.IMessages;
import fhkl.de.orgapp.util.IUniformResourceLocator;
import fhkl.de.orgapp.util.JSONParser;
import fhkl.de.orgapp.util.MenuActivity;
import fhkl.de.orgapp.util.data.UserData;

/**
 * PrivateInfoController - Handles the data for edit the private information of the user
 * 
 * @author Oliver Neubauer
 * @version ?
 *
 */

public class PrivateInfoController extends MenuActivity
{
	// Required variables for progress dialog, calendar, new date fields, birthday and gender
	private ProgressDialog pDialog;
	private Calendar calendar;
	int yearNew, monthNew, dayNew;
	String[] birthdayArray;
	char genderNew;
	
	// Required variables for layout fields
	TextView textFirstName, textLastName, textBirthday, textGender;
	CheckBox textClearBirthday, textGenderMale, textGenderFemale;
	EditText firstNameNew, lastNameNew, birthdayNew;
	Button changeButton, cancelButton;

	/**
	 * Initializes all necessary variables.
	 * Sets onClickListener for birthday calendar
	 * 
	 * @param savedInstanceState contains the data
	 */
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		
		// Set the layout
		setContentView(R.layout.private_information);
		
		// Check for new notifications and signal the user
		checkOnNewNotificationsAndNotifyUser();
		// Fetch the calendar
		calendar = Calendar.getInstance();
		// Fetch the views
		getViews();
		// Set the texts of the views
		setTexts();
		// Set the text sizes
		setTextSizes();
		
		// Set a click listener for the birthday field
		birthdayNew.setOnClickListener(new OnClickListener()
		{
			// Define the action in case of click
			@Override
			public void onClick(View view)
			{
				// Define a date picker dialog
				DatePickerDialog dateDialog = new DatePickerDialog(PrivateInfoController.this, setDateListener, calendar
								.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
				
				// Fetch the date picker
				DatePicker datePicker = dateDialog.getDatePicker();
				
				// Modify some fields
				datePicker.updateDate(yearNew, monthNew, dayNew);
				dateDialog.setTitle(getString(R.string.SET_NEW_BIRTHDAY));
				dateDialog.setButton(DatePickerDialog.BUTTON_POSITIVE, getString(R.string.SAVE), dateDialog);
				// Hide the dialog at cancel
				dateDialog.setButton(DatePickerDialog.BUTTON_NEGATIVE, getString(R.string.CANCEL),
								new DialogInterface.OnClickListener() {
									@Override
									public void onClick(DialogInterface dialog, int which) {
										dialog.dismiss();
									}
								});

				dateDialog.show();
			}
		});
		
		// Split the birthday for appropriate date format
		birthdayArray = splitBirthday(UserData.getBIRTHDAY());
		
		// Set the new fields year, month and day
		if(birthdayArray != null && birthdayArray.length == 3)
		{
			yearNew = Integer.parseInt(birthdayArray[0]);
			monthNew = Integer.parseInt(birthdayArray[1]) - 1;
			dayNew = Integer.parseInt(birthdayArray[2]);
		}
	}

	/**
	 * Defines the dialog for birthday
	 */
	
	private DatePickerDialog.OnDateSetListener setDateListener = new DatePickerDialog.OnDateSetListener()
	{
		
		/**
		 * Sets the birthday in the dialog selected by the user.
		 * Call updateBirthday()
		 *
		 * @param view the view of the dialog
		 * @param year the year to set
		 * @param month the month to set
		 * @param day the day to set
		 */
		
		@Override
		public void onDateSet(DatePicker view, int year, int month, int day)
		{
			// Set the new fields entered by the user in calendar
			calendar.set(Calendar.YEAR, year);
			calendar.set(Calendar.MONTH, month);
			calendar.set(Calendar.DAY_OF_MONTH, day);
			
			// Update content in the birthday field
			updateBirthday();
			
			// User should see the checkbox "clear birthday" after the selection of a birthday
			textClearBirthday.setVisibility(View.VISIBLE);
			textClearBirthday.setChecked(false);
			textClearBirthday.setText(getString(R.string.CLEAR_BIRTHDAY));
		}
	};

	/**
	 * Splits the birthday for a preferred format
	 * 
	 * @param birthdayString the birthday from the database
	 * @return birthday as array
	 */
	
	private String[] splitBirthday(String birthdayString)
	{
		String[] result = new String[3];
		
		// Return null in case of null pointer
		if(birthdayString == null)
			return null;
		
		// Return null in case of empty string
		if(birthdayString.equals(""))
			return null;

		// Split birthday at the delimiter "-"
		result = birthdayString.split("-");

		return result;
	}

	/**
	 * Updates the birthday to be displayed
	 */
	
	private void updateBirthday()
	{
		// Define the format
		String format = "yyyy-MM-dd";
		SimpleDateFormat dateFormat = new SimpleDateFormat(format, Locale.US);
		// Set the new birthday with the specific format
		birthdayNew.setText(dateFormat.format(calendar.getTime()));
	}

	/**
	 * Fetches the views by id
	 */
	
	private void getViews()
	{
		textFirstName = (TextView) findViewById(R.id.PRIVATE_INFO_TEXT_FIRST_NAME);
		textLastName = (TextView) findViewById(R.id.PRIVATE_INFO_TEXT_LAST_NAME);
		textBirthday = (TextView) findViewById(R.id.PRIVATE_INFO_TEXT_BIRTHDAY);
		textGender = (TextView) findViewById(R.id.PRIVATE_INFO_TEXT_GENDER);
		textGenderMale = (CheckBox) findViewById(R.id.PRIVATE_INFO_USER_GENDER_MALE);
		textGenderFemale = (CheckBox) findViewById(R.id.PRIVATE_INFO_USER_GENDER_FEMALE);
		textClearBirthday = (CheckBox) findViewById(R.id.PRIVATE_INFO_CHECKBOX_CLEAR_BIRTHDAY);

		firstNameNew = (EditText) findViewById(R.id.PRIVATE_INFO_USER_FIRST_NAME);
		lastNameNew = (EditText) findViewById(R.id.PRIVATE_INFO_USER_LAST_NAME);
		birthdayNew = (EditText) findViewById(R.id.PRIVATE_INFO_USER_BIRTHDAY);

		changeButton = (Button) findViewById(R.id.CHANGE_PRIVATE_INFO_BUTTON);
		cancelButton = (Button) findViewById(R.id.CANCEL_PRIVATE_INFO_VIEW);
	}

	/**
	 * Sets the texts to be displayed
	 */
	
	private void setTexts()
	{
		textFirstName.setText(getString(R.string.FIRSTNAME_MUST_HAVE) + ":");
		textLastName.setText(getString(R.string.LASTNAME_MUST_HAVE) + ":");
		textBirthday.setText(getString(R.string.BIRTHDAY) + ":");
		textGender.setText(getString(R.string.GENDER) + ":");
		textGenderMale.setText(getString(R.string.MALE));
		textGenderFemale.setText(getString(R.string.FEMALE));
		
		firstNameNew.setText(UserData.getFIRST_NAME());
		lastNameNew.setText(UserData.getLAST_NAME());
		
		// If birthday contains data, set the field as hint
		if(!UserData.getBIRTHDAY().equals(""))
		{
			birthdayNew.setHint(UserData.getBIRTHDAY());
			textClearBirthday.setVisibility(View.VISIBLE);
			textClearBirthday.setChecked(false);
			textClearBirthday.setText(getString(R.string.CLEAR_BIRTHDAY));
		}
		// Otherwise set a text as hint
		else
		{
			birthdayNew.setHint(getString(R.string.SET_NEW_BIRTHDAY));
			textClearBirthday.setVisibility(View.INVISIBLE);
		}

		// Set the checkbox according the gender of user
		if (UserData.getGENDER().equals("m"))
			textGenderMale.setChecked(true);
		else if (UserData.getGENDER().equals("w"))
			textGenderFemale.setChecked(true);
		else
			genderNew = '-';

		changeButton.setText(getString(R.string.CHANGE_INFO));
		cancelButton.setText(getString(R.string.CANCEL));
	}

	/**
	 * Sets the text size
	 */
	
	private void setTextSizes()
	{
		// Android specific sizes
		textFirstName.setTextAppearance(this, android.R.style.TextAppearance_Large);
		textLastName.setTextAppearance(this, android.R.style.TextAppearance_Large);
		textBirthday.setTextAppearance(this, android.R.style.TextAppearance_Large);
		textGender.setTextAppearance(this, android.R.style.TextAppearance_Large);
		textGenderMale.setTextAppearance(this, android.R.style.TextAppearance_Small);
		textGenderFemale.setTextAppearance(this, android.R.style.TextAppearance_Small);
		textClearBirthday.setTextAppearance(this, android.R.style.TextAppearance_Small);
		
		firstNameNew.setTextAppearance(this, android.R.style.TextAppearance_Medium);
		lastNameNew.setTextAppearance(this, android.R.style.TextAppearance_Medium);
		birthdayNew.setTextAppearance(this, android.R.style.TextAppearance_Medium);
	}

	/**
	 * Select a gender
	 * 
	 * @param view the associated view
	 */
	
	public void selectGender(View view)
	{
		// No gender is checked
		if(!textGenderMale.isChecked() && !textGenderFemale.isChecked())
		{
			genderNew = '-';
			return;
		}
		
		// Set gender according the selection
		switch (view.getId())
		{
			case R.id.PRIVATE_INFO_USER_GENDER_MALE:
				if(textGenderMale.isChecked())
					genderNew = 'm';
				break;
	
			case R.id.PRIVATE_INFO_USER_GENDER_FEMALE:
				if(textGenderFemale.isChecked())
					genderNew = 'w';
				break;
		}
	}
	
	/**
	 * Clears the selected birthday
	 * 
	 * @param view the associated view
	 */
	
	public void clearBirthday(View view)
	{
		// Clear content in the birthday field
		birthdayNew.setText("");
		
		// Set a text as hint in the birthday field
		birthdayNew.setHint(getString(R.string.SET_NEW_BIRTHDAY));
		
		// The checkbox "clear birthday" is invisible, now
		textClearBirthday.setVisibility(View.INVISIBLE);
	}

	/**
	 * Checks the data and calls the updater
	 * 
	 * @param view the associated view
	 */
	
	public void changePrivateInfo(View view)
	{
		// Error message in case of incomplete fields
		if (!areRequiredFieldsComplete()) {
			Toast.makeText(getApplicationContext(), IMessages.Error.REQUIRED_FIELDS_NOT_COMPLETE, Toast.LENGTH_LONG).show();
			return;
		}

		// Error message in case of undone changes
		if (!hasPrivateInfoChanged()) {
			Toast.makeText(getApplicationContext(), IMessages.Error.PRIVATE_INFO_NOT_UPDATED, Toast.LENGTH_LONG).show();
			return;
		}
		
		// Error message in case of selection of both genera
		if(hasTooManyGenera())
		{
			Toast.makeText(getApplicationContext(), IMessages.Error.TOO_MANY_GENERA, Toast.LENGTH_LONG).show();
			return;
		}
		
		// Update the private information
		new PrivateInfoUpdater().execute();
	}

	/**
	 * Goes back to the profile layout
	 * 
	 * @param view the associated view
	 */
	
	public void cancelPrivateInfoView(View view) {
		backToProfile();
	}

	/**
	 * Starts the ProfileController 
	 */
	
	private void backToProfile() {
		// Start the ProfileController
		Intent intent = new Intent(this, ProfileController.class);
		startActivity(intent);
	}

	/**
	 * Checks, whether all required fields are complete
	 * 
	 * @return false, if one field is missing. True otherwise
	 */
	
	private boolean areRequiredFieldsComplete() {
		if (firstNameNew.getText().toString().equals("") || lastNameNew.getText().toString().equals(""))
			return false;

		return true;
	}

	/**
	 * Checks, whether changes were done
	 * 
	 * @return false, if one field contains the same data as previous. True otherwise
	 */
	
	private boolean hasPrivateInfoChanged() {
		if (firstNameNew.getText().toString().equals(UserData.getFIRST_NAME())
						&& lastNameNew.getText().toString().equals(UserData.getLAST_NAME())
						&& birthdayNew.getText().toString().equals(UserData.getBIRTHDAY())
						&& String.valueOf(genderNew).equals(UserData.getGENDER()))
			return false;

		return true;
	}
	
	/**
	 * Checks, whether male and female are selected
	 * 
	 * @return true, if male and female selected. False otherwise
	 */
	
	private boolean hasTooManyGenera()
	{
		if(textGenderMale.isChecked() && textGenderFemale.isChecked())
			return true;
		
		return false;
	}

	/**
	 * PrivateInfoUpdater - Updates the private information
	 * 
	 * @author Oliver Neubauer
	 * @version ?
	 *
	 */
	
	class PrivateInfoUpdater extends AsyncTask<String, String, String> {
		
		/**
		 * Defines a progress dialog within the main thread
		 */
		
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			
			// Display a progress dialog
			pDialog = new ProgressDialog(PrivateInfoController.this);
			pDialog.setMessage(IMessages.Status.UPDATING_PRIVATE_INFO);
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(true);
			pDialog.show();
		}

		/**
		 * Prepares and makes a http-request within the background thread
		 * 
		 * @param arg0 the arguments as String array
		 */
		@Override
		protected String doInBackground(String... arg0)
		{
			// Required parameters for the request
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair("do", "update"));
			params.add(new BasicNameValuePair("personId", UserData.getPERSONID()));
			
			// The new values
			params.add(new BasicNameValuePair("firstName", firstNameNew.getText().toString()));
			params.add(new BasicNameValuePair("lastName", lastNameNew.getText().toString()));
			params.add(new BasicNameValuePair("birthday", birthdayNew.getText().toString()));
			params.add(new BasicNameValuePair("gender", String.valueOf(genderNew)));
			
			// This value is not changeable in this layout, but required for the update
			params.add(new BasicNameValuePair("eMail", UserData.getEMAIL()));

			// Make the request
			JSONObject json = new JSONParser().makeHttpRequest(IUniformResourceLocator.URL.URL_PERSON, "GET", params);

			try
			{
				int success = json.getInt("success");

				// In case of success
				if (success == 1)
				{
					return IMessages.Success.UPDATE_WAS_SUCCESSFUL;
				}
				// In case of no success
				else
				{
					return IMessages.Error.UPDATE_WAS_NOT_SUCCESSFUL;
				}
			}
			// In case of Error
			catch (Exception e)
			{
				e.getStackTrace();
				// Logout the user
				logout();
			}

			return null;
		}

		/**
		 * Displays an user message within main thread.
		 * Sets the updated user data
		 *
		 * @param message the message to be displayed
		 */
		
		@Override
		protected void onPostExecute(String message)
		{
			super.onPostExecute(message);
			
			// Hide the progress dialog
			pDialog.dismiss();

			// Display a message if available
			if (message != null)
				Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();

			// Set the updated data to the POJO
			UserData.setFIRST_NAME(firstNameNew.getText().toString());
			UserData.setLAST_NAME(lastNameNew.getText().toString());
			UserData.setBIRTHDAY(birthdayNew.getText().toString());
			UserData.setGENDER(String.valueOf(genderNew));

			// Back to the profile of the user
			backToProfile();
		}
	}
}