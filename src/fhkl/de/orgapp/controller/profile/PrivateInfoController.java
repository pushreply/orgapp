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

public class PrivateInfoController extends MenuActivity {
	private static String URL_UPDATE_PERSON_PRIVATE_INFO = "http://pushrply.com/update_person_private_info.php";

	private ProgressDialog pDialog;
	private Calendar calendar;

	TextView textFirstName, textLastName, textBirthday, textGender;
	CheckBox textClearBirthday, textGenderMale, textGenderFemale;
	EditText firstNameNew, lastNameNew, birthdayNew;
	char genderNew;
	Button changeButton, cancelButton;
	int yearNew, monthNew, dayNew;
	String[] birthdayArray;

	/**
	 * Initializes all necessary variables.
	 * Sets onClickListener for birthday calendar
	 * 
	 * @param savedInstanceState contains the data
	 */
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.private_information);
		
		// check for new notifications and signal the user
		checkOnNewNotificationsAndNotifyUser();
		calendar = Calendar.getInstance();
		getViews();
		setTexts();
		setTextSizes();
		
		birthdayNew.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				DatePickerDialog dateDialog = new DatePickerDialog(PrivateInfoController.this, setDateListener, calendar
								.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
				DatePicker datePicker = dateDialog.getDatePicker();

				datePicker.updateDate(yearNew, monthNew, dayNew);
				dateDialog.setTitle(getString(R.string.SET_NEW_BIRTHDAY));
				dateDialog.setButton(DatePickerDialog.BUTTON_POSITIVE, getString(R.string.SAVE), dateDialog);
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
		
		birthdayArray = splitBirthday(UserData.getBIRTHDAY());
		
		if(birthdayArray != null)
		{
			yearNew = Integer.parseInt(birthdayArray[0]);
			monthNew = Integer.parseInt(birthdayArray[1]) - 1;
			dayNew = Integer.parseInt(birthdayArray[2]);
		}
	}

	/**
	 * Defines the dialog for birthday
	 */
	
	private DatePickerDialog.OnDateSetListener setDateListener = new DatePickerDialog.OnDateSetListener() {
		
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
		public void onDateSet(DatePicker view, int year, int month, int day) {
			calendar.set(Calendar.YEAR, year);
			calendar.set(Calendar.MONTH, month);
			calendar.set(Calendar.DAY_OF_MONTH, day);
			updateBirthday();
			textClearBirthday.setVisibility(View.VISIBLE);
			textClearBirthday.setChecked(false);
		}
	};

	/**
	 * Splits the birthday for preferred format
	 * 
	 * @param birthdayString the birthday
	 * @return birthday as array
	 */
	
	private String[] splitBirthday(String birthdayString) {
		String[] result = new String[3];
		
		if(birthdayString.equals(""))
			return null;

		result = birthdayString.split("-");

		return result;
	}

	/**
	 * Updates the birthday to be displayed
	 */
	
	private void updateBirthday() {
		String format = "yyyy-MM-dd";
		SimpleDateFormat dateFormat = new SimpleDateFormat(format, Locale.US);
		birthdayNew.setText(dateFormat.format(calendar.getTime()));
	}

	/**
	 * Fetches the views by id
	 */
	
	private void getViews() {
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
	
	private void setTexts() {
		textFirstName.setText(getString(R.string.FIRSTNAME_MUST_HAVE) + ":");
		textLastName.setText(getString(R.string.LASTNAME_MUST_HAVE) + ":");
		textBirthday.setText(getString(R.string.BIRTHDAY) + ":");
		textGender.setText(getString(R.string.GENDER) + ":");
		textGenderMale.setText(getString(R.string.MALE));
		textGenderFemale.setText(getString(R.string.FEMALE));
		textClearBirthday.setText(getString(R.string.CLEAR_BIRTHDAY));
		
		firstNameNew.setText(UserData.getFIRST_NAME());
		lastNameNew.setText(UserData.getLAST_NAME());
		
		// If birthday contains data, set the field as hint
		if(!UserData.getBIRTHDAY().equals(""))
			birthdayNew.setHint(UserData.getBIRTHDAY());
		// Otherwise set a text
		else
			birthdayNew.setHint(getString(R.string.SET_NEW_BIRTHDAY));

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
	
	private void setTextSizes() {
		int userTextSize = (int) getResources().getDimension(R.dimen.PROFIL_USER_TEXT_SIZE);

		textFirstName.setTextSize(userTextSize);
		textLastName.setTextSize(userTextSize);
		textBirthday.setTextSize(userTextSize);
		textGender.setTextSize(userTextSize);
		textGenderMale.setTextSize(userTextSize);
		textGenderFemale.setTextSize(userTextSize);
		textClearBirthday.setTextSize(userTextSize);
		
		firstNameNew.setTextSize(userTextSize);
		lastNameNew.setTextSize(userTextSize);
		birthdayNew.setTextSize(userTextSize);
	}

	/**
	 * Select a gender
	 * 
	 * @param view the associated view
	 */
	
	public void selectGender(View view)
	{
		if(!textGenderMale.isChecked() && !textGenderFemale.isChecked())
		{
			genderNew = '-';
			return;
		}
		
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
		birthdayNew.setText("");
		birthdayNew.setHint(getString(R.string.SET_NEW_BIRTHDAY));
		textClearBirthday.setVisibility(View.INVISIBLE);
	}

	/**
	 * Checks the data and calls the updater
	 * 
	 * @param view the associated view
	 */
	
	public void changePrivateInfo(View view) {
		if (!areRequiredFieldsComplete()) {
			Toast.makeText(getApplicationContext(), IMessages.Error.REQUIRED_FIELDS_NOT_COMPLETE, Toast.LENGTH_LONG).show();
			return;
		}

		if (!hasPrivateInfoChanged()) {
			Toast.makeText(getApplicationContext(), IMessages.Error.PRIVATE_INFO_NOT_UPDATED, Toast.LENGTH_LONG).show();
			return;
		}
		
		if(hasTooManyGenera())
		{
			Toast.makeText(getApplicationContext(), IMessages.Error.TOO_MANY_GENERA, Toast.LENGTH_LONG).show();
			return;
		}
		
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
		protected String doInBackground(String... arg0) {
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair("personId", UserData.getPERSONID()));
			params.add(new BasicNameValuePair("firstName", firstNameNew.getText().toString()));
			params.add(new BasicNameValuePair("lastName", lastNameNew.getText().toString()));
			params.add(new BasicNameValuePair("birthday", birthdayNew.getText().toString()));
			params.add(new BasicNameValuePair("gender", String.valueOf(genderNew)));

			JSONObject json = new JSONParser().makeHttpRequest(URL_UPDATE_PERSON_PRIVATE_INFO, "GET", params);

			try {
				int success = json.getInt("success");

				if (success == 1) {
					return IMessages.Success.UPDATE_WAS_SUCCESSFUL;
				} else {
					return IMessages.Error.UPDATE_WAS_NOT_SUCCESSFUL;
				}
			} catch (Exception e) {
				System.out.println("Error in PrivateInfoUpdater.doInBackground(String... arg0): " + e.getMessage());
				e.getStackTrace();
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
		protected void onPostExecute(String message) {
			super.onPostExecute(message);
			pDialog.dismiss();

			if (message != null)
				Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();

			// Set the updated data to the POJO
			UserData.setFIRST_NAME(firstNameNew.getText().toString());
			UserData.setLAST_NAME(lastNameNew.getText().toString());
			UserData.setBIRTHDAY(birthdayNew.getText().toString());
			UserData.setGENDER(String.valueOf(genderNew));

			backToProfile();
		}
	}
}