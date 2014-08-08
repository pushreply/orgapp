package fhkl.de.orgapp.util;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Vibrator;
import android.support.v4.app.NotificationCompat;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;
import fhkl.de.orgapp.R;
import fhkl.de.orgapp.controller.calendar.CalendarController;
import fhkl.de.orgapp.controller.event.AttendingMemberController;
import fhkl.de.orgapp.controller.event.CreateEventController;
import fhkl.de.orgapp.controller.event.DeleteEventController;
import fhkl.de.orgapp.controller.event.EditEventController;
import fhkl.de.orgapp.controller.event.EventController;
import fhkl.de.orgapp.controller.event.EventSettingsController;
import fhkl.de.orgapp.controller.groups.DeleteGroupController;
import fhkl.de.orgapp.controller.groups.EditGroupController;
import fhkl.de.orgapp.controller.groups.GroupsController;
import fhkl.de.orgapp.controller.groups.LeaveGroupController;
import fhkl.de.orgapp.controller.groups.ListInviteMemberController;
import fhkl.de.orgapp.controller.groups.ManualInviteMemberController;
import fhkl.de.orgapp.controller.groups.MemberListController;
import fhkl.de.orgapp.controller.groups.NewGroupController;
import fhkl.de.orgapp.controller.groups.SingleGroupController;
import fhkl.de.orgapp.controller.notification.NotificationController;
import fhkl.de.orgapp.controller.notification.NotificationSettingsController;
import fhkl.de.orgapp.controller.profile.EventHistoryController;
import fhkl.de.orgapp.controller.profile.PrivateInfoController;
import fhkl.de.orgapp.controller.profile.ProfileController;
import fhkl.de.orgapp.controller.profile.SecurityInfoController;
import fhkl.de.orgapp.controller.start.StartController;
import fhkl.de.orgapp.util.check.NewNotificationsChecker;
import fhkl.de.orgapp.util.data.EventData;
import fhkl.de.orgapp.util.data.EventSettingsData;
import fhkl.de.orgapp.util.data.GroupData;
import fhkl.de.orgapp.util.data.NotificationSettingsData;
import fhkl.de.orgapp.util.data.UserData;

/**
 * MenuActivity - Handles the data to define the user menu
 * 
 * @author Ronaldo Hasiholan, Jochen Jung, Oliver Neubauer
 * @version ?
 * 
 */

public class MenuActivity extends Activity {
	// For json issues
	JSONParser jsonParser = new JSONParser();
	private static final String TAG_SUCCESS = "success";

	// Id of the icon for unread notifications
	private int newNotificationNotificationId = 1;

	/**
	 * Renders the menu visible.
	 * 
	 * @param menu the menu
	 * @return OptionMenuCreated
	 */

	public boolean onCreateOptionsMenu(Menu menu) {
		// Fetch the menu inflater
		MenuInflater inflater = getMenuInflater();

		// Set the menu
		inflater.inflate(R.menu.main_menu, menu);

		// The controller, which calls the menu
		String nameCurrentController = getIntent().getComponent().getClassName();

		// Set the title of logout item
		menu.findItem(R.id.LOGOUT).setTitle("Logout ( " + UserData.getEMAIL() + " )");

		// Make the items visible
		// Dependent on the position in the application

		if (nameCurrentController.equals(GroupsController.class.getName())) {
			menu.findItem(R.id.NEW_GROUP).setVisible(true);
		}

		if (nameCurrentController.equals(NotificationController.class.getName())) {
			menu.findItem(R.id.NOTIFICATION_SETTINGS).setVisible(true);
		}

		if (nameCurrentController.equals(ProfileController.class.getName())
						|| nameCurrentController.equals(PrivateInfoController.class.getName())
						|| nameCurrentController.equals(SecurityInfoController.class.getName())
						|| nameCurrentController.equals(EventHistoryController.class.getName())) {
			menu.findItem(R.id.PROFILE_SETTINGS).setVisible(true);
		}

		if (nameCurrentController.equals(ProfileController.class.getName())
						|| nameCurrentController.equals(EventHistoryController.class.getName())
						|| nameCurrentController.equals(SecurityInfoController.class.getName())) {
			menu.findItem(R.id.CHANGE_PRIVATE_INFORMATION).setVisible(true);
		}

		if (nameCurrentController.equals(ProfileController.class.getName())
						|| nameCurrentController.equals(PrivateInfoController.class.getName())
						|| nameCurrentController.equals(SecurityInfoController.class.getName())) {
			menu.findItem(R.id.LIST_EVENT_HISTORY).setVisible(true);
		}

		if (nameCurrentController.equals(ProfileController.class.getName())
						|| nameCurrentController.equals(PrivateInfoController.class.getName())
						|| nameCurrentController.equals(EventHistoryController.class.getName())) {
			menu.findItem(R.id.CHANGE_SECURITY_INFORMATION).setVisible(true);
		}

		if (nameCurrentController.equals(CalendarController.class.getName())) {
			menu.findItem(R.id.CALENDAR).setVisible(false);
		}

		if (nameCurrentController.equals(EventController.class.getName())) {
			menu.findItem(R.id.EVENT_SETTINGS).setVisible(true);
			menu.findItem(R.id.SHOW_ATTENDING_MEMBER).setVisible(true);
			menu.findItem(R.id.EVENT_SETTINGS_SETTINGS).setVisible(true);
			if (GroupData.getPERSONID().equals(UserData.getPERSONID()) || GroupData.getPRIVILEGE_EDIT_EVENT().equals("1")
							|| EventData.getPERSONID().equals(UserData.getPERSONID())) {
				menu.findItem(R.id.EDIT_EVENT).setVisible(true);
			}
			if (GroupData.getPERSONID().equals(UserData.getPERSONID()) || GroupData.getPRIVILEGE_DELETE_EVENT().equals("1")
							|| EventData.getPERSONID().equals(UserData.getPERSONID())) {
				menu.findItem(R.id.DELETE_EVENT).setVisible(true);
			}
			if (EventData.getPERSONID().equals(UserData.getPERSONID())) {
				menu.findItem(R.id.EVENT_SHARE_SETTINGS).setVisible(true);
				menu.findItem(R.id.SHARE_EVENT_VIA_FACEBOOK).setVisible(true);
				menu.findItem(R.id.SHARE_EVENT_VIA_TWITTER).setVisible(true);
			}
		}

		if (nameCurrentController.equals(GroupsController.class.getName())) {
			menu.findItem(R.id.GROUPS).setVisible(false);
		}

		if (nameCurrentController.equals(SingleGroupController.class.getName())) {
			menu.findItem(R.id.GROUP_SETTINGS).setVisible(true);
			if (GroupData.getPERSONID().equals(UserData.getPERSONID()) || GroupData.getPRIVILEGE_CREATE_EVENT().equals("1")) {
				menu.findItem(R.id.CREATE_EVENT).setVisible(true);
			}
			if (GroupData.getPERSONID().equals(UserData.getPERSONID())) {
				menu.findItem(R.id.EDIT_GROUP).setVisible(true);
			}

			if (GroupData.getPERSONID().equals(UserData.getPERSONID())) {
				menu.findItem(R.id.DELETE_GROUP).setVisible(true);
			}
			if (!GroupData.getPERSONID().equals(UserData.getPERSONID())) {
				menu.findItem(R.id.LEAVE_GROUP).setVisible(true);
			}

			menu.findItem(R.id.SHOW_MEMBER_LIST).setVisible(true);

			if (GroupData.getPERSONID().equals(UserData.getPERSONID()) || GroupData.getPRIVILEGE_INVITE_MEMBER().equals("1"))
				menu.findItem(R.id.INVITE_MEMBER).setVisible(true);
		}

		if (nameCurrentController.equals(MemberListController.class.getName())
						|| nameCurrentController.equals(AttendingMemberController.class.getName())) {
			menu.findItem(R.id.BACK).setVisible(true);
		}

		if (nameCurrentController.equals(NotificationController.class.getName())) {
			menu.findItem(R.id.NOTIFICATIONS).setVisible(false);
		}

		if (nameCurrentController.equals(ProfileController.class.getName())) {
			menu.findItem(R.id.PROFILE).setVisible(false);
		}

		if (nameCurrentController.equals(CalendarController.class.getName())
						|| nameCurrentController.equals(GroupsController.class.getName())
						|| nameCurrentController.equals(NotificationController.class.getName())) {
			menu.findItem(R.id.REFRESH).setVisible(true);
		}

		return true;
	}

	/**
	 * Defines selected menu item functionality.
	 * 
	 * @param item the menu item
	 * @return OptionItemSelected
	 */

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// The intent to be called
		Intent intent;
		// Dialog to be displayed
		AlertDialog.Builder builder;
		// Message to be displayed at sharing
		String sharingMessage;

		// Select appropriate menu item
		switch (item.getItemId()) {
		case R.id.CALENDAR:
			intent = new Intent(MenuActivity.this, CalendarController.class);
			startActivity(intent);
			return true;

		case R.id.GROUPS:
			intent = new Intent(MenuActivity.this, GroupsController.class);
			startActivity(intent);
			return true;

		case R.id.NEW_GROUP:
			intent = new Intent(MenuActivity.this, NewGroupController.class);
			startActivity(intent);
			return true;

		case R.id.CREATE_EVENT:
			intent = new Intent(MenuActivity.this, CreateEventController.class);
			startActivity(intent);
			return true;

		case R.id.SHOW_ATTENDING_MEMBER:
			intent = new Intent(MenuActivity.this, AttendingMemberController.class);
			startActivity(intent);
			return true;

		case R.id.EDIT_EVENT:
			intent = new Intent(MenuActivity.this, EditEventController.class);
			startActivity(intent);
			return true;

		case R.id.DELETE_EVENT:

			// Display a security issue
			builder = new AlertDialog.Builder(MenuActivity.this);
			builder.setMessage(IMessages.SecurityIssue.DELETE_EVENT);
			builder.setPositiveButton(IMessages.DialogButton.YES, new OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {
					Intent intent = new Intent(MenuActivity.this, DeleteEventController.class);
					dialog.dismiss();
					finish();
					startActivity(intent);
				}

			});
			builder.setNegativeButton(IMessages.DialogButton.NO, new OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
				}
			});
			builder.create().show();
			return true;

		case R.id.EVENT_SETTINGS_SETTINGS:
			intent = new Intent(MenuActivity.this, EventSettingsController.class);
			startActivity(intent);
			return true;

		case R.id.SHARE_EVENT_VIA_FACEBOOK:

			// Define a message, which will be displayed at sharing
			sharingMessage = "New Event: " + EventData.getNAME() + ", Date: " + EventData.getEVENTDATE() + ", Time: "
							+ EventData.getEVENTTIME() + ", Location: " + EventData.getEVENTLOCATION();

			// Share message on social network
			return shareToSocialNetwork(Intent.ACTION_SEND, "facebook", sharingMessage);

		case R.id.SHARE_EVENT_VIA_TWITTER:

			// Define a message, which will be displayed at sharing
			sharingMessage = "New Event: " + EventData.getNAME() + ", Date: " + EventData.getEVENTDATE() + ", Time: "
							+ EventData.getEVENTTIME() + ", Location: " + EventData.getEVENTLOCATION();

			// Share message on social network
			return shareToSocialNetwork(Intent.ACTION_SEND, "twitter", sharingMessage);

		case R.id.EDIT_GROUP:
			intent = new Intent(MenuActivity.this, EditGroupController.class);
			startActivity(intent);
			return true;

		case R.id.LEAVE_GROUP:

			// Display a security issue
			builder = new AlertDialog.Builder(MenuActivity.this);
			AlertDialog dialog;
			builder.setMessage(IMessages.SecurityIssue.CONFIRM_LEAVING_GROUP + GroupData.getGROUPNAME()
							+ IMessages.SecurityIssue.QUESTION_MARK);

			builder.setPositiveButton(IMessages.DialogButton.YES, new OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
					startActivity(new Intent(MenuActivity.this, LeaveGroupController.class));
				}
			});

			builder.setNegativeButton(IMessages.DialogButton.NO, new OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
				}
			});

			dialog = builder.create();

			dialog.show();

			return true;

		case R.id.DELETE_GROUP:

			// Display a security issue
			builder = new AlertDialog.Builder(MenuActivity.this);
			AlertDialog leavedialog;
			builder.setMessage(IMessages.SecurityIssue.MESSAGE_DELETE_GROUP + GroupData.getGROUPNAME()
							+ IMessages.SecurityIssue.QUESTION_MARK);

			builder.setPositiveButton(IMessages.DialogButton.YES, new OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
					startActivity(new Intent(MenuActivity.this, DeleteGroupController.class));
				}
			});

			builder.setNegativeButton(IMessages.DialogButton.NO, new OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
				}
			});

			leavedialog = builder.create();

			leavedialog.show();

			return true;

		case R.id.SHOW_MEMBER_LIST:
			// Show the member list
			new MemberList().execute();
			return true;

		case R.id.BACK:
			if (EventData.isBACK()) {
				EventData.setBACK(false);
				intent = new Intent(MenuActivity.this, EventController.class);
				startActivity(intent);
			} else {
				intent = new Intent(MenuActivity.this, SingleGroupController.class);
				startActivity(intent);
			}
			return true;

		case R.id.INVITE_MEMBER:

			// Display a security issue, whether invite via list or manually

			builder = new AlertDialog.Builder(MenuActivity.this);
			builder.setMessage(IMessages.SecurityIssue.QUESTION_MEMBER);
			builder.setPositiveButton(IMessages.DialogButton.LIST, new OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {
					Intent intent = new Intent(MenuActivity.this, ListInviteMemberController.class);
					dialog.dismiss();
					finish();
					startActivity(intent);
				}

			});
			builder.setNegativeButton(IMessages.DialogButton.MANUALLY, new OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {
					Intent intent = new Intent(MenuActivity.this, ManualInviteMemberController.class);
					dialog.dismiss();
					finish();
					startActivity(intent);
				}
			});
			builder.setNeutralButton(IMessages.DialogButton.CANCEL, new OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
				}
			});
			builder.create().show();
			return true;

		case R.id.NOTIFICATIONS:
			intent = new Intent(MenuActivity.this, NotificationController.class);
			startActivity(intent);
			return true;

		case R.id.PROFILE:
			intent = new Intent(MenuActivity.this, ProfileController.class);
			startActivity(intent);
			return true;

		case R.id.NOTIFICATION_SETTINGS:
			intent = new Intent(MenuActivity.this, NotificationSettingsController.class);
			startActivity(intent);
			return true;

		case R.id.CHANGE_PRIVATE_INFORMATION:
			intent = new Intent(MenuActivity.this, PrivateInfoController.class);
			startActivity(intent);
			return true;

		case R.id.LIST_EVENT_HISTORY:
			intent = new Intent(MenuActivity.this, EventHistoryController.class);
			startActivity(intent);
			return true;

		case R.id.CHANGE_SECURITY_INFORMATION:
			intent = new Intent(MenuActivity.this, SecurityInfoController.class);
			startActivity(intent);
			return true;

		case R.id.LOGOUT:
			logout();
			return true;

		case R.id.REFRESH:
			finish();
			startActivity(getIntent().putExtra("Refresh", "Refresh"));

		default:
			return false;
		}
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

		Intent intent = new Intent(MenuActivity.this, StartController.class);
		startActivity(intent);
	}

	/**
	 * Resets the user data
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
	 * Resets the notification settings
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
	 * Resets the event settings
	 */

	private void resetEventSettingsData() {
		EventSettingsData.setEVENT_SETTINGS_ID("");
		EventSettingsData.setSHOWN_EVENT_ENTRIES("");
	}

	/**
	 * MemberList - Fetches the members of a group
	 * 
	 * @author Ronaldo Hasiholan, Jochen Jung, Oliver Neubauer
	 * @version ?
	 * 
	 */

	public class MemberList extends AsyncTask<String, String, String> {
		/**
		 * Makes the request to fetch the members of a group
		 * 
		 * @param args the arguments as array
		 * @return an error message or null in case of success
		 */

		protected String doInBackground(String... args) {
			List<NameValuePair> params = new ArrayList<NameValuePair>();

			// The required parameters
			params.add(new BasicNameValuePair("do", "readMemberList"));
			params.add(new BasicNameValuePair("personId", UserData.getPERSONID()));
			params.add(new BasicNameValuePair("groupId", GroupData.getGROUPID()));

			// Make the request to fetch the members of a group
			JSONObject json = jsonParser.makeHttpsRequest(IUniformResourceLocator.URL.URL_GROUPS, "GET", params, MenuActivity.this);
			
			try {
				int success = json.getInt(TAG_SUCCESS);

				// In case of success
				if (success == 1) {
					return null;
				}
				// In case of no success
				else {
					return IMessages.Status.MEMBERLIST_EMPTY;
				}
			}
			// In case of error
			catch (Exception e) {
				// Logout the user
				logout();
			}

			return null;
		}

		/**
		 * Displays a negative message in case of error. Starts the
		 * MemberListController in case of success
		 * 
		 * @param message the error message or null in case of success
		 */

		protected void onPostExecute(String message) {
			// The error message
			if (message != null) {
				Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
			}
			// In case of success
			else {
				// Start the MemberListController
				Intent intent = new Intent(MenuActivity.this, MemberListController.class);
				startActivity(intent);
			}
		}
	}

	/**
	 * Shares a messages on social network
	 * 
	 * @param sharedContent the type of intent
	 * @param socialNetworkName the name of social network
	 * @param sharingMessage the message to be shared
	 * @return success
	 */

	protected boolean shareToSocialNetwork(String sharedContent, String socialNetworkName, String sharingMessage) {
		// Prepare intent
		Intent sharingIntent = new Intent(sharedContent);
		sharingIntent.setType("text/plain");

		// The component
		ComponentName component = null;

		// Fetch all appropriate applications
		PackageManager pManager = getPackageManager();
		List<ResolveInfo> activityList = pManager.queryIntentActivities(sharingIntent, 0);

		// Search app
		for (int a = 0; a < activityList.size(); a++)
			if ((activityList.get(a).activityInfo.name).contains(socialNetworkName))
				component = new ComponentName(activityList.get(a).activityInfo.applicationInfo.packageName,
								activityList.get(a).activityInfo.name);

		// Prepare content as string
		sharingIntent.putExtra(Intent.EXTRA_SUBJECT, EventData.getNAME());

		// Send intent to app
		sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, sharingMessage);

		// Send intent to twitter app
		sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, sharingMessage);
		sharingIntent.setComponent(component);

		// If no appropriate app found, send to browser and open url
		if (component == null) {
			String url;

			// In case of facebook
			if (socialNetworkName.equalsIgnoreCase("facebook"))
				url = "https://facebook.com/sharer/sharer.php?text=" + sharingMessage;
			// In case of twitter
			else
				url = "https://twitter.com/intent/tweet?text=" + sharingMessage;

			// Start the activity
			Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
			startActivity(browserIntent);

			return true;
		}

		// Start the activity
		startActivity(sharingIntent);

		return true;
	}

	/**
	 * Checks for new notifications
	 */

	protected void checkOnNewNotificationsAndNotifyUser() {
		// Object for check on new notifications
		NewNotificationsChecker newNotifications = new NewNotificationsChecker();

		// In case of no new notifications
		if (!newNotifications.hasNewNotifications())
			return;

		// Create title and text for icon
		String numberNewNotifications = newNotifications.getNumberNewNotifications();

		String title = IMessages.Notification.NEW_NOTIFICATION;
		title += numberNewNotifications.equals("1") ? "" : "s";

		String text = IMessages.Notification.YOU_HAVE_UNREAD_NOTIFICATION_1;
		text += numberNewNotifications;
		text += IMessages.Notification.YOU_HAVE_UNREAD_NOTIFICATION_2;
		text += numberNewNotifications.equals("1") ? "" : "s";

		// Create icon for action bar
		NotificationCompat.Builder builder = new NotificationCompat.Builder(this).setSmallIcon(R.drawable.ic_action_unread)
						.setContentTitle(title).setContentText(text).setAutoCancel(true);

		Intent resultIntent = new Intent(this, NotificationController.class);

		// Pending intent for using own permissions
		PendingIntent resultPendingIntent = PendingIntent.getActivity(this, 0, resultIntent,
						PendingIntent.FLAG_UPDATE_CURRENT);

		builder.setContentIntent(resultPendingIntent);

		NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

		// Notify user by icon
		notificationManager.notify(newNotificationNotificationId, builder.build());

		// Notify user by vibration, if this set
		if (Boolean.parseBoolean(NotificationSettingsData.getVIBRATION()))
			((Vibrator) getSystemService(Context.VIBRATOR_SERVICE)).vibrate(2000);
	}

	/**
	 * Delets the icon for signal the user on new notifications
	 */
	protected void deleteIcon() {
		((NotificationManager) getSystemService(NOTIFICATION_SERVICE)).cancel(newNotificationNotificationId);
	}
}