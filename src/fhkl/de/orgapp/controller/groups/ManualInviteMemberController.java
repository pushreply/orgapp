package fhkl.de.orgapp.controller.groups;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.text.InputType;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.GridLayout.LayoutParams;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;
import fhkl.de.orgapp.R;
import fhkl.de.orgapp.controller.calendar.CalendarController;
import fhkl.de.orgapp.controller.notification.NotificationController;
import fhkl.de.orgapp.controller.profile.ProfileController;
import fhkl.de.orgapp.controller.start.StartController;
import fhkl.de.orgapp.util.IMessages;
import fhkl.de.orgapp.util.IUniformResourceLocator;
import fhkl.de.orgapp.util.JSONParser;
import fhkl.de.orgapp.util.check.NewNotificationsChecker;
import fhkl.de.orgapp.util.data.EventSettingsData;
import fhkl.de.orgapp.util.data.GroupData;
import fhkl.de.orgapp.util.data.NotificationSettingsData;
import fhkl.de.orgapp.util.data.UserData;
import fhkl.de.orgapp.util.validator.InputValidator;

/**
 * ManualInviteMemberController - Handles the manual invite member activity.
 * 
 * Invites new group members via their respective E-Mail address.
 * 
 * @author Jochen Jung
 * @version 1.0
 */
public class ManualInviteMemberController extends Activity {

	private ProgressDialog pDialog;

	// To identify the notification icon
	private int newNotificationNotificationId = 1;

	// For json issues
	JSONParser jsonParser = new JSONParser();
	JSONArray persons;

	// To store the persons with email and personId
	List<HashMap<String, Object>> existPersons;

	private static final String TAG_SUCCESS = "success";
	private static final String TAG_PERSON_ID = "personId";
	private static final String TAG_EMAIL = "email";

	LinearLayout containerLayout;
	GridLayout textLayout;
	private String personIdLoggedPerson;

	private Button bInvite;
	private Button bCancel;

	/**
	 * Initializes and loads the view. Defines save and cancel Button.
	 * 
	 * @param savedInstanceState Bundle
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.invite_member_manual);
		checkNewNotificationAndCreateIcon();
		// Set initial E-Mail field count
		getIntent().putExtra("cnt", "0");

		personIdLoggedPerson = UserData.getPERSONID();
		containerLayout = (LinearLayout) findViewById(R.id.LinearLayout);
		textLayout = new GridLayout(ManualInviteMemberController.this);
		LayoutParams params = new LayoutParams();
		params.width = LayoutParams.MATCH_PARENT;
		params.height = LayoutParams.WRAP_CONTENT;
		textLayout.setLayoutParams(params);
		textLayout.setOrientation(GridLayout.HORIZONTAL);
		textLayout.setColumnCount(4);

		containerLayout.addView(textLayout);
		bInvite = (Button) findViewById(R.id.INVITE);
		bCancel = (Button) findViewById(R.id.CANCEL);

		bInvite.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				// Async class that invites new members
				new InviteMembers().execute();
			}
		});

		bCancel.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				Intent intent = new Intent(ManualInviteMemberController.this, SingleGroupController.class);
				startActivity(intent);
			}
		});
	}

	/**
	 * Can not implement MenuActivity because the created E-Mail address field has
	 * to be accessed.
	 * 
	 * Renders the menu visible.
	 * 
	 * @param menu the menu
	 * @return boolean OptionMenuCreated
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.main_menu, menu);
		menu.findItem(R.id.ADD_EMAIL_FIELD).setVisible(true);
		menu.findItem(R.id.CALENDAR).setVisible(true);
		menu.findItem(R.id.GROUPS).setVisible(true);
		menu.findItem(R.id.NOTIFICATIONS).setVisible(true);
		menu.findItem(R.id.PROFILE).setVisible(true);
		menu.findItem(R.id.LOGOUT).setTitle("Logout ( " + UserData.getEMAIL() + " )");
		menu.findItem(R.id.LOGOUT).setVisible(true);
		return true;
	}

	/**
	 * Can not implement MenuActivity because the created E-Mail address field has
	 * to be accessed.
	 * 
	 * Defines selected menu item functionality. Adds new formated E-Mail address
	 * field.
	 * 
	 * @param item MenuItem
	 * @return boolean OptionItemSelected
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Intent intent;

		switch (item.getItemId()) {
		case R.id.CALENDAR:
			intent = new Intent(ManualInviteMemberController.this, CalendarController.class);
			intent.putExtra("UserId", personIdLoggedPerson);
			startActivity(intent);
			return true;
		case R.id.GROUPS:
			intent = new Intent(ManualInviteMemberController.this, GroupsController.class);
			intent.putExtra("UserId", personIdLoggedPerson);
			startActivity(intent);
			return true;
		case R.id.NOTIFICATIONS:
			intent = new Intent(ManualInviteMemberController.this, NotificationController.class);
			intent.putExtra("UserId", personIdLoggedPerson);
			startActivity(intent);
			return true;
		case R.id.PROFILE:
			intent = new Intent(ManualInviteMemberController.this, ProfileController.class);
			intent.putExtra("UserId", personIdLoggedPerson);
			startActivity(intent);
			return true;
		case R.id.LOGOUT:
			intent = new Intent(ManualInviteMemberController.this, StartController.class);
			intent.putExtra("UserId", personIdLoggedPerson);
			startActivity(intent);
			return true;
		case R.id.ADD_EMAIL_FIELD:
			// Current E-Mail address field count
			Integer tmpCnt = Integer.valueOf(getIntent().getStringExtra("cnt").toString());

			EditText editText = new EditText(ManualInviteMemberController.this);
			editText.setId(tmpCnt);
			editText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
			editText.setHint(R.string.EMAIL);

			// Current E-Mail address field count + 1
			tmpCnt++;

			// Add remove E-Mail address field Button
			ImageButton imageButton = new ImageButton(ManualInviteMemberController.this);
			imageButton.setId(tmpCnt);
			imageButton.setImageResource(R.drawable.ic_action_remove);

			tmpCnt++;
			// Sets new E-Mail address field count
			getIntent().putExtra("cnt", tmpCnt.toString());

			textLayout.addView(editText);
			LayoutParams layoutParams = (GridLayout.LayoutParams) editText.getLayoutParams();
			layoutParams.columnSpec = GridLayout.spec(0, 3);
			layoutParams.setGravity(Gravity.FILL);
			editText.setLayoutParams(layoutParams);

			textLayout.addView(imageButton);
			layoutParams = (GridLayout.LayoutParams) imageButton.getLayoutParams();
			layoutParams.columnSpec = GridLayout.spec(3);
			layoutParams.width = LayoutParams.WRAP_CONTENT;
			layoutParams.height = LayoutParams.WRAP_CONTENT;
			imageButton.setLayoutParams(layoutParams);

			imageButton.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// Remove E-Mail address field
					textLayout.removeView(findViewById(v.getId() - 1));
					// Remove ImageView
					textLayout.removeView(v);
				}
			});
			return true;
		default:
			return true;
		}
	}

	/**
	 * 
	 * Asnyc class that invites selected person into group.
	 * 
	 * @author Jochen Jung
	 * @version 1.0
	 */
	class InviteMembers extends AsyncTask<String, String, String> {
		/**
		 * Creates ProcessDialog
		 */
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pDialog = new ProgressDialog(ManualInviteMemberController.this);

			pDialog.setMessage(IMessages.Status.INVITING_MEMBERS);

			pDialog.setIndeterminate(false);
			pDialog.setCancelable(true);
			pDialog.show();
		}

		/**
		 * Validates user input. Invites selected persons into group and sends
		 * notification on correct input.
		 * 
		 * @param params String...
		 * @return String result
		 */
		protected String doInBackground(String... params) {
			int editTextLength = textLayout.getChildCount();

			if (editTextLength == 0) {
				return IMessages.Error.MISSING_EMAIL;
			}

			String[] editTextArray = new String[editTextLength / 2];
			for (int i = 0; i < editTextLength; i++) {
				if (i % 2 == 0) {
					EditText tmp = (EditText) textLayout.getChildAt(i);
					if (i != 0) {
						if (Arrays.asList(editTextArray).contains(tmp.getText().toString())) {
							// Duplicate E-Mail address
							return IMessages.Error.DUPLICATE_EMAIL;
						} else {
							editTextArray[i / 2] = tmp.getText().toString();
						}
					} else {

						editTextArray[i / 2] = tmp.getText().toString();
					}
				}
			}
			for (int i = 0; i < editTextArray.length; i++) {
				if (!InputValidator.isEmailValid(editTextArray[i])) {
					// Wrong E-Mail address format
					return IMessages.Error.INVALID_EMAIL;
				}
			}

			// To store the persons with email and personId
			existPersons = new ArrayList<HashMap<String, Object>>();
			HashMap<String, Object> person;

			for (int e = 0; e < editTextArray.length; e++) {
				List<NameValuePair> paramsCheck = new ArrayList<NameValuePair>();

				// Required parameters
				paramsCheck.add(new BasicNameValuePair("do", "read"));
				paramsCheck.add(new BasicNameValuePair("eMail", editTextArray[e]));

				// Fetch person by email
				JSONObject json = jsonParser.makeHttpRequest(IUniformResourceLocator.URL.URL_PERSON, "GET", paramsCheck,
								ManualInviteMemberController.this);

				int success;

				try {
					success = json.getInt(TAG_SUCCESS);

					if (success == 0) {
						// User is not registered
						return IMessages.Error.EXIST_USER;
					}

					// User exists
					person = new HashMap<String, Object>();

					person.put(TAG_EMAIL, editTextArray[e]);
					person.put(TAG_PERSON_ID, json.getJSONArray("person").getJSONObject(0).getString(TAG_PERSON_ID));

					existPersons.add(person);
				} catch (Exception exception) {
					exception.printStackTrace();
					pDialog.dismiss();
					logout();
				}
			}

			for (int p = 0; p < existPersons.size(); p++) {
				List<NameValuePair> paramsCheck = new ArrayList<NameValuePair>();
				paramsCheck.add(new BasicNameValuePair("do", "readUserInGroup"));
				paramsCheck.add(new BasicNameValuePair("groupId", GroupData.getGROUPID()));
				paramsCheck.add(new BasicNameValuePair("personId", existPersons.get(p).get(TAG_PERSON_ID).toString()));

				JSONObject json = jsonParser.makeHttpRequest(IUniformResourceLocator.URL.URL_GROUPS, "GET", paramsCheck,
								ManualInviteMemberController.this);

				int success;

				try {
					success = json.getInt(TAG_SUCCESS);

					if (success == 1) {
						// User already invited
						return IMessages.Error.USER_INVITED;
					}
				} catch (Exception e) {
					pDialog.dismiss();
					e.printStackTrace();
					logout();
				}
			}

			// Everything validated

			DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd", Locale.GERMANY);
			Date date = new Date();
			List<NameValuePair> paramsInvite = new ArrayList<NameValuePair>();

			// Required parameters
			paramsInvite.add(new BasicNameValuePair("do", "createPrivilegeMember"));
			paramsInvite.add(new BasicNameValuePair("groupId", GroupData.getGROUPID()));
			paramsInvite.add(new BasicNameValuePair("memberSince", dateFormat.format(date).toString()));

			for (int p = 0; p < existPersons.size(); p++) {
				paramsInvite.add(new BasicNameValuePair("personId", existPersons.get(p).get(TAG_PERSON_ID).toString()));

				// Invite person
				JSONObject json = jsonParser.makeHttpRequest(IUniformResourceLocator.URL.URL_PRIVILEGE, "GET", paramsInvite,
								ManualInviteMemberController.this);

				int success;
				try {
					success = json.getInt(TAG_SUCCESS);

					if (success == 1) {
						List<NameValuePair> paramsNotification = new ArrayList<NameValuePair>();
						paramsNotification.add(new BasicNameValuePair("do", "create"));
						paramsNotification.add(new BasicNameValuePair("eMail", existPersons.get(p).get(TAG_EMAIL).toString()));
						paramsNotification.add(new BasicNameValuePair("classification", "1"));
						String message = IMessages.Notification.MESSAGE_INVITE + GroupData.getGROUPNAME();
						paramsNotification.add(new BasicNameValuePair("message", message));
						paramsNotification.add(new BasicNameValuePair("syncInterval", null));

						// Send Notifications
						json = jsonParser.makeHttpRequest(IUniformResourceLocator.URL.URL_NOTIFICATION, "GET", paramsNotification,
										ManualInviteMemberController.this);
					}
				} catch (Exception e) {
					pDialog.dismiss();
					e.printStackTrace();
					logout();
				}
			}

			Intent intent = new Intent(ManualInviteMemberController.this, SingleGroupController.class);
			startActivity(intent);
			return null;
		}

		/**
		 * Removes ProcessDialog. Returns warning on wrong input.
		 * 
		 * @param message String
		 */
		protected void onPostExecute(String message) {
			pDialog.dismiss();
			if (message != null) {
				Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
			}
		}
	}

	/**
	 * Checks if user received new notification. Displays notification icon.
	 */
	private void checkNewNotificationAndCreateIcon() {
		NewNotificationsChecker newNotifications = new NewNotificationsChecker();

		if (!newNotifications.hasNewNotifications())
			return;

		String numberNewNotifications = newNotifications.getNumberNewNotifications();

		String title = IMessages.Notification.NEW_NOTIFICATION;
		title += numberNewNotifications.equals("1") ? "" : "s";

		String text = IMessages.Notification.YOU_HAVE_UNREAD_NOTIFICATION_1;
		text += numberNewNotifications;
		text += IMessages.Notification.YOU_HAVE_UNREAD_NOTIFICATION_2;
		text += numberNewNotifications.equals("1") ? "" : "s";

		NotificationCompat.Builder builder = new NotificationCompat.Builder(this).setSmallIcon(R.drawable.ic_action_unread)
						.setContentTitle(title).setContentText(text).setAutoCancel(true);

		Intent resultIntent = new Intent(this, NotificationController.class);

		PendingIntent resultPendingIntent = PendingIntent.getActivity(this, 0, resultIntent,
						PendingIntent.FLAG_UPDATE_CURRENT);

		builder.setContentIntent(resultPendingIntent);

		NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

		notificationManager.notify(newNotificationNotificationId, builder.build());
	}

	/**
	 * Saves the preferences when app is closed.
	 */
	@Override
	protected void onDestroy() {
		super.onDestroy();

		SharedPreferences prefs = getSharedPreferences("fhkl.de.orgapp", Context.MODE_PRIVATE);

		SharedPreferences.Editor editor = prefs.edit();

		// Save the data to SharedPreferences
		editor.putString("personId", UserData.getPERSONID());
		editor.putString("firstName", UserData.getFIRST_NAME());
		editor.putString("lastName", UserData.getLAST_NAME());
		editor.putString("birthday", UserData.getBIRTHDAY());
		editor.putString("gender", UserData.getGENDER());
		editor.putString("eMail", UserData.getEMAIL());
		editor.putString("memberSince", UserData.getMEMBER_SINCE());

		editor.putString("notificationSettingsId", NotificationSettingsData.getNOTIFICATION_SETTINGS_ID());
		editor.putString("shownEntries", NotificationSettingsData.getSHOW_ENTRIES());
		editor.putString("groupInvites", NotificationSettingsData.getGROUP_INVITES());
		editor.putString("groupEdited", NotificationSettingsData.getGROUP_EDITED());
		editor.putString("eventsAdded", NotificationSettingsData.getEVENTS_ADDED());
		editor.putString("eventsRemoved", NotificationSettingsData.getEVENTS_REMOVED());
		editor.putString("commentsAdded", NotificationSettingsData.getCOMMENTS_ADDED());
		editor.putString("commentsEdited", NotificationSettingsData.getCOMMENTS_EDITED());
		editor.putString("commentsRemoved", NotificationSettingsData.getCOMMENTS_REMOVED());
		editor.putString("privilegeGiven", NotificationSettingsData.getPRIVILEGE_GIVEN());
		editor.putString("vibration", NotificationSettingsData.getVIBRATION());

		editor.putString("eventSettingsId", EventSettingsData.getEVENT_SETTINGS_ID());
		editor.putString("shownEventEntries", EventSettingsData.getSHOWN_EVENT_ENTRIES());

		editor.commit();
	}

	/**
	 * Resets user data, notification settings, event settings. Deletes the icon,
	 * which signal user for new notifications. Calles the StartController
	 */
	protected void logout() {
		// Delete SharedPreferences
		SharedPreferences prefs = getSharedPreferences("fhkl.de.orgapp", Context.MODE_PRIVATE);
		prefs.edit().clear().commit();

		resetUserData();
		resetNotificationSettingsData();
		resetEventSettingsData();
		deleteIcon();

		Intent intent = new Intent(ManualInviteMemberController.this, StartController.class);
		startActivity(intent);
	}

	/**
	 * Resets user data.
	 */
	private void resetUserData() {
		UserData.setPERSONID("");
		UserData.setFIRST_NAME("");
		UserData.setLAST_NAME("");
		UserData.setBIRTHDAY("");
		UserData.setGENDER("");
		UserData.setEMAIL("");
		UserData.setMEMBER_SINCE("");
	}

	/**
	 * Resets notification settings data.
	 */
	private void resetNotificationSettingsData() {
		NotificationSettingsData.setNOTIFICATION_SETTINGS_ID("");
		NotificationSettingsData.setSHOW_ENTRIES("");
		NotificationSettingsData.setGROUP_INVITES("");
		NotificationSettingsData.setGROUP_EDITED("");
		NotificationSettingsData.setGROUP_REMOVED("");
		NotificationSettingsData.setEVENTS_ADDED("");
		NotificationSettingsData.setEVENTS_EDITED("");
		NotificationSettingsData.setEVENTS_REMOVED("");
		NotificationSettingsData.setCOMMENTS_ADDED("");
		NotificationSettingsData.setCOMMENTS_EDITED("");
		NotificationSettingsData.setCOMMENTS_REMOVED("");
		NotificationSettingsData.setPRIVILEGE_GIVEN("");
		NotificationSettingsData.setVIBRATION("");
	}

	/**
	 * Resets event settings data
	 */
	private void resetEventSettingsData() {
		EventSettingsData.setEVENT_SETTINGS_ID("");
		EventSettingsData.setSHOWN_EVENT_ENTRIES("");
	}

	/**
	 * Deletes the notification icon.
	 */
	protected void deleteIcon() {
		((NotificationManager) getSystemService(NOTIFICATION_SERVICE)).cancel(newNotificationNotificationId);
	}
}
