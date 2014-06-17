package fhkl.de.orgapp.controller.profile;

import android.graphics.Paint;
import android.os.Bundle;
import android.widget.TextView;
import fhkl.de.orgapp.R;
import fhkl.de.orgapp.util.MenuActivity;

public class ProfileController extends MenuActivity
{
	TextView textFirstName, textLastName, textBirthday, textGender, textEmail, textMemberSince;
	TextView textPrivateInformation, textSecurityInformation, textGeneralInformation;
	TextView firstName, lastName, birthday, gender, email, memberSince;
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.profil);
		
		getViews();
		setTexts();
		setTextSizes();
	}
	
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
		
		firstName.setText(getIntent().getStringExtra("FirstName"));
		lastName.setText(getIntent().getStringExtra("LastName"));
		birthday.setText(getIntent().getStringExtra("Birthday"));
		gender.setText(getIntent().getStringExtra("Gender"));
		email.setText(getIntent().getStringExtra("Email"));
		memberSince.setText(getIntent().getStringExtra("MemberSince"));
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