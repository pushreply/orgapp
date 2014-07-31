package fhkl.de.orgapp.controller.profile;

import android.graphics.Paint;
import android.os.Bundle;
import android.widget.TextView;
import fhkl.de.orgapp.R;
import fhkl.de.orgapp.util.MenuActivity;
import fhkl.de.orgapp.util.data.UserData;

/**
 * ProfileController - Handles the data for display the profile of the user
 * 
 * @author Oliver Neubauer
 * @version ?
 *
 */

public class ProfileController extends MenuActivity
{
	// Required variables for layout fields
	TextView textFirstName, textLastName, textBirthday, textGender, textEmail, textMemberSince;
	TextView textPrivateInformation, textSecurityInformation, textGeneralInformation;
	TextView firstName, lastName, birthday, gender, email, memberSince;
	
	/**
	 * Sets the content view.
	 * Calls the required methods
	 *
	 * @param savedInstanceState contains the data
	 */
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		// Set the layout
		setContentView(R.layout.profil);
		
		// Check for new notifications and signal the user
		checkOnNewNotificationsAndNotifyUser();
		
		// Fetch the views
		getViews();
		// Set the texts of the views
		setTexts();
		// Set the text sizes
		setTextSizes();
	}
	
	/**
	 * Fetches the views by id
	 */
	
	private void getViews()
	{
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
	}
	
	/**
	 * Sets the texts of views
	 */
	
	private void setTexts()
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
		
		firstName.setText(UserData.getFIRST_NAME());
		lastName.setText(UserData.getLAST_NAME());
		birthday.setText(UserData.getBIRTHDAY());
		
		// User set no gender
		if(!UserData.getGENDER().equalsIgnoreCase("m") && !UserData.getGENDER().equalsIgnoreCase("w"))
			gender.setText("");
		// User set a gender
		else
			gender.setText(UserData.getGENDER());
		
		email.setText(UserData.getEMAIL());
		memberSince.setText(UserData.getMEMBER_SINCE());
	}
	
	/**
	 * Sets text sizes
	 */
	
	private void setTextSizes()
	{
		// Android specific sizes
		textPrivateInformation.setTextAppearance(this, android.R.style.TextAppearance_Large);
		textSecurityInformation.setTextAppearance(this, android.R.style.TextAppearance_Large);
		textGeneralInformation.setTextAppearance(this, android.R.style.TextAppearance_Large);
		
		textFirstName.setTextAppearance(this, android.R.style.TextAppearance_Large);
		textLastName.setTextAppearance(this, android.R.style.TextAppearance_Large);
		textBirthday.setTextAppearance(this, android.R.style.TextAppearance_Large);
		textGender.setTextAppearance(this, android.R.style.TextAppearance_Large);
		textEmail.setTextAppearance(this, android.R.style.TextAppearance_Large);
		textMemberSince.setTextAppearance(this, android.R.style.TextAppearance_Large);
		
		firstName.setTextAppearance(this, android.R.style.TextAppearance_Medium);
		lastName.setTextAppearance(this, android.R.style.TextAppearance_Medium);
		birthday.setTextAppearance(this, android.R.style.TextAppearance_Medium);
		gender.setTextAppearance(this, android.R.style.TextAppearance_Medium);
		email.setTextAppearance(this, android.R.style.TextAppearance_Medium);
		memberSince.setTextAppearance(this, android.R.style.TextAppearance_Medium);
	}
}