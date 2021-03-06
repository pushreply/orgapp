package fhkl.de.orgapp.controller.start;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;
import fhkl.de.orgapp.R;
import fhkl.de.orgapp.controller.calendar.CalendarController;
import fhkl.de.orgapp.controller.login.ForgotPasswordController;
import fhkl.de.orgapp.controller.login.LoginController;
import fhkl.de.orgapp.controller.registration.RegisterController;
import fhkl.de.orgapp.util.IMessages;
import fhkl.de.orgapp.util.data.EventSettingsData;
import fhkl.de.orgapp.util.data.NotificationSettingsData;
import fhkl.de.orgapp.util.data.UserData;

/**
 * StartController - Handles the data for the splash screen
 * 
 * @author Ronaldo Hasiholan, Jochen Jung
 * @version 3.9
 * 
 */

public class StartController extends Activity {
	// For the displayed button
	private Button bLogin;
	private Button bRegister;
	private Button bForgotPassword;

	// For the check on internet connection
	boolean isConnected;

	/**
	 * Splits the splash screen. Sets the content view. Calls method to define
	 * onClickListener
	 * 
	 * @param savedInstanceState contains the data
	 */

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Go full screen, but show status bar
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN,
						WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);

		SharedPreferences prefs = getSharedPreferences("fhkl.de.orgapp", Context.MODE_PRIVATE);
		if (prefs.contains("eMail")) {

			UserData.setPERSONID(prefs.getString("personId", ""));
			UserData.setFIRST_NAME(prefs.getString("firstName", ""));
			UserData.setLAST_NAME(prefs.getString("lastName", ""));
			UserData.setBIRTHDAY(prefs.getString("birthday", ""));
			UserData.setGENDER(prefs.getString("gender", ""));
			UserData.setEMAIL(prefs.getString("eMail", ""));
			UserData.setMEMBER_SINCE(prefs.getString("memberSince", ""));

			NotificationSettingsData.setNOTIFICATION_SETTINGS_ID(prefs.getString("notificationSettingsId", ""));
			NotificationSettingsData.setSHOW_ENTRIES(prefs.getString("shownEntries", ""));
			NotificationSettingsData.setGROUP_INVITES(prefs.getString("groupInvites", ""));
			NotificationSettingsData.setGROUP_EDITED(prefs.getString("groupEdited", ""));
			NotificationSettingsData.setGROUP_REMOVED(prefs.getString("groupRemoved", ""));
			NotificationSettingsData.setEVENTS_ADDED(prefs.getString("eventsAdded", ""));
			NotificationSettingsData.setEVENTS_EDITED(prefs.getString("eventsEdited", ""));
			NotificationSettingsData.setEVENTS_REMOVED(prefs.getString("eventsRemoved", ""));
			NotificationSettingsData.setCOMMENTS_ADDED(prefs.getString("commentsAdded", ""));
			NotificationSettingsData.setCOMMENTS_EDITED(prefs.getString("commentsEdited", ""));
			NotificationSettingsData.setCOMMENTS_REMOVED(prefs.getString("commentsRemoved", ""));
			NotificationSettingsData.setPRIVILEGE_GIVEN(prefs.getString("privilegeGiven", ""));
			NotificationSettingsData.setVIBRATION(prefs.getString("vibration", ""));

			EventSettingsData.setEVENT_SETTINGS_ID(prefs.getString("eventSettingsId", ""));
			EventSettingsData.setSHOWN_EVENT_ENTRIES(prefs.getString("shownEventEntries", ""));

			// Call the CalendarController
			Intent intent = new Intent(getApplicationContext(), CalendarController.class);
			startActivity(intent);

		} else {
			setContentView(R.layout.start);

			addListenerOnButton();
		}

	}

	/**
	 * Checks the internet connection. Fetches the views by id. Defines
	 * onClickListener for the buttons
	 */

	public void addListenerOnButton() {
		// Check the internet connection
		Context ctx = this;
		ConnectivityManager cm = (ConnectivityManager) ctx.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
		isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();

		// Fetch the views by id
		bLogin = (Button) findViewById(R.id.LOGIN);
		bRegister = (Button) findViewById(R.id.REGISTER);
		bForgotPassword = (Button) findViewById(R.id.FORGOTPASSWORD);

		// Set onClickListener for login
		bLogin.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// If internet connection is active
				if (isConnected) {
					Intent i = new Intent(StartController.this, LoginController.class);
					startActivity(i);
				}
				// If internet connection is not active
				else {
					Toast.makeText(getApplicationContext(), IMessages.Error.NO_INTERNET_CONNECTION, Toast.LENGTH_LONG).show();
				}
			}
		});

		// Set onClickListener for register
		bRegister.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// If internet connection is active
				if (isConnected) {
					Intent i = new Intent(StartController.this, RegisterController.class);
					startActivity(i);
				}
				// If internet connection is not active
				else {
					Toast.makeText(getApplicationContext(), IMessages.Error.NO_INTERNET_CONNECTION, Toast.LENGTH_LONG).show();
				}
			}
		});

		// Set onClickListener for forgot password
		bForgotPassword.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// Go to ForgotControllerController
				Intent i = new Intent(StartController.this, ForgotPasswordController.class);
				startActivity(i);
			}
		});
	}
}