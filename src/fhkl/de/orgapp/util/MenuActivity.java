package fhkl.de.orgapp.util;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
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

public class MenuActivity extends Activity {

	JSONParser jsonParser = new JSONParser();

	private static String URL_GET_MEMBER_LIST = "http://pushrply.com/get_member_list.php";

	private static final String TAG_SUCCESS = "success";
	private int newNotificationNotificationId = 1;
	
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.main_menu, menu);
		String nameCurrentController = getIntent().getComponent().getClassName();

		menu.findItem(R.id.LOGOUT).setTitle("Logout ( " + UserData.getEMAIL() + " )");
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
			if (GroupData.getPERSONID().equals(UserData.getPERSONID()) || GroupData.getPRIVILEGE_EDIT_EVENT().equals("1")
							|| EventData.getPERSONID().equals(UserData.getPERSONID())) {
				menu.findItem(R.id.EDIT_EVENT).setVisible(true);
			}
			if (GroupData.getPERSONID().equals(UserData.getPERSONID()) || GroupData.getPRIVILEGE_DELETE_EVENT().equals("1")
							|| EventData.getPERSONID().equals(UserData.getPERSONID())) {
				menu.findItem(R.id.DELETE_EVENT).setVisible(true);
			}
			if (EventData.getPERSONID().equals(UserData.getPERSONID()))
			{
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

			if (GroupData.getPRIVILEGE_INVITE_MEMBER().equals("1"))
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

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Intent intent;
		AlertDialog.Builder builder;
		String sharingMessage;
		
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
			builder = new AlertDialog.Builder(MenuActivity.this);
			builder.setMessage(IMessages.DELETE_EVENT);
			builder.setPositiveButton(IMessages.YES, new OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {
					Intent intent = new Intent(MenuActivity.this, DeleteEventController.class);
					dialog.dismiss();
					finish();
					startActivity(intent);
				}

			});
			builder.setNegativeButton(IMessages.NO, new OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
				}
			});
			builder.create().show();
			return true;

		case R.id.SHARE_EVENT_VIA_FACEBOOK:
			sharingMessage =
				"New Event: " + EventData.getNAME()
				+ ", Date: " + EventData.getEVENTDATE()
				+ ", Time: " + EventData.getEVENTTIME()
				+ ", Location: " + EventData.getEVENTLOCATION();
		
		return shareToSocialNetwork(Intent.ACTION_SEND, "facebook", sharingMessage);

		case R.id.SHARE_EVENT_VIA_TWITTER:
			sharingMessage =
				"New Event: " + EventData.getNAME()
				+ ", Date: " + EventData.getEVENTDATE()
				+ ", Time: " + EventData.getEVENTTIME()
				+ ", Location: " + EventData.getEVENTLOCATION();
			
			return shareToSocialNetwork(Intent.ACTION_SEND, "twitter", sharingMessage);

		case R.id.EDIT_GROUP:
			intent = new Intent(MenuActivity.this, EditGroupController.class);
			startActivity(intent);
			return true;

		case R.id.LEAVE_GROUP:
			builder = new AlertDialog.Builder(MenuActivity.this);
			AlertDialog dialog;
			builder.setMessage(IMessages.CONFIRM_LEAVING_GROUP + GroupData.getGROUPNAME() + IMessages.QUESTION_MARK);

			builder.setPositiveButton(IMessages.YES, new OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
					startActivity(new Intent(MenuActivity.this, LeaveGroupController.class));
				}
			});

			builder.setNegativeButton(IMessages.NO, new OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
				}
			});

			dialog = builder.create();

			dialog.show();

			return true;

		case R.id.DELETE_GROUP:
			builder = new AlertDialog.Builder(MenuActivity.this);
			AlertDialog leavedialog;
			builder.setMessage(IMessages.MESSAGE_DELETE_GROUP + GroupData.getGROUPNAME() + IMessages.QUESTION_MARK);

			builder.setPositiveButton(IMessages.YES, new OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
					startActivity(new Intent(MenuActivity.this, DeleteGroupController.class));
				}
			});

			builder.setNegativeButton(IMessages.NO, new OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
				}
			});

			leavedialog = builder.create();

			leavedialog.show();

			return true;

		case R.id.SHOW_MEMBER_LIST:
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
			builder = new AlertDialog.Builder(MenuActivity.this);
			builder.setMessage(IMessages.QUESTION_MEMBER);
			builder.setPositiveButton(IMessages.LIST, new OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {
					Intent intent = new Intent(MenuActivity.this, ListInviteMemberController.class);
					dialog.dismiss();
					finish();
					startActivity(intent);
				}

			});
			builder.setNegativeButton(IMessages.MANUALLY, new OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {
					Intent intent = new Intent(MenuActivity.this, ManualInviteMemberController.class);
					dialog.dismiss();
					finish();
					startActivity(intent);
				}
			});
			builder.setNeutralButton(IMessages.NO_MEMBER_INVITE, new OnClickListener() {

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
			intent.putExtra("UserId", UserData.getPERSONID());
			startActivity(intent);
			return true;

		case R.id.CHANGE_SECURITY_INFORMATION:
			intent = new Intent(MenuActivity.this, SecurityInfoController.class);
			intent.putExtra("UserId", UserData.getPERSONID());
			intent.putExtra("Email", UserData.getEMAIL());
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

	protected void logout() {

		UserData.setPERSONID("");
		UserData.setFIRST_NAME("");
		UserData.setLAST_NAME("");
		UserData.setBIRTHDAY("");
		UserData.setGENDER("");
		UserData.setEMAIL("");
		UserData.setMEMBER_SINCE("");

		deleteIcon();
		
		Intent intent = new Intent(MenuActivity.this, StartController.class);
		startActivity(intent);
	}

	class MemberList extends AsyncTask<String, String, String> {

		protected String doInBackground(String... args) {
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair("personId", UserData.getPERSONID()));
			params.add(new BasicNameValuePair("groupId", GroupData.getGROUPID()));

			JSONObject json = jsonParser.makeHttpRequest(URL_GET_MEMBER_LIST, "GET", params);

			Log.d("Memberlist: ", json.toString());

			try {
				int success = json.getInt(TAG_SUCCESS);
				if (success == 1) {
					Intent intent = new Intent(MenuActivity.this, MemberListController.class);
					startActivity(intent);
				} else {
					return IMessages.MEMBERLIST_EMPTY;
				}

			} catch (JSONException e) {
				System.out.println("Error in MemberList.doInBackground(String... args): " + e.getMessage());
				e.printStackTrace();
			}

			return null;
		}

		protected void onPostExecute(String message) {

			if (message != null) {
				Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
			}
		}
	}
	
	protected boolean shareToSocialNetwork(String sharedContent, String socialNetworkName, String sharingMessage)
	{
		Intent sharingIntent = new Intent(sharedContent);
		sharingIntent.setType("text/plain");
		ComponentName component = null;

		PackageManager pManager = getPackageManager();
		List<ResolveInfo> activityList = pManager.queryIntentActivities(sharingIntent, 0);
		
		//Search app
		for(int a=0; a<activityList.size(); a++)
			if((activityList.get(a).activityInfo.name).contains(socialNetworkName))
				component = new ComponentName(activityList.get(a).activityInfo.applicationInfo.packageName, activityList.get(a).activityInfo.name);

		// prepare content as string
		sharingIntent.putExtra(Intent.EXTRA_SUBJECT, EventData.getNAME());
		
		//send intent to app
		sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, sharingMessage);

		// send intent to twitter app
		sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, sharingMessage);
		sharingIntent.setComponent(component);
		
		//if no appropriate app found, send to browser and open url
		if (component == null)
		{
			String url;
			
			if(socialNetworkName.equalsIgnoreCase("facebook"))
				url = "https://facebook.com/sharer/sharer.php?text=" + sharingMessage;
			else
				url = "https://twitter.com/intent/tweet?text=" + sharingMessage;
			
			Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
			startActivity(browserIntent);

			return true;
		}
		startActivity(sharingIntent);

		return true;
	}
	
	protected void checkNewNotificationAndCreateIcon()
	{
		NewNotifications newNotifications = new NewNotifications();
		
		if(!newNotifications.hasNewNotifications())
			return;
		
		String numberNewNotifications = newNotifications.getNumberNewNotifications();
			
		String title = IMessages.NEW_NOTIFICATION;
		title += numberNewNotifications.equals("1") ? "" : "s";
			
		String text = IMessages.YOU_HAVE_UNREAD_NOTIFICATION_1;
		text += numberNewNotifications;
		text += IMessages.YOU_HAVE_UNREAD_NOTIFICATION_2;
		text += numberNewNotifications.equals("1") ? "" : "s";
		
		NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
			.setSmallIcon(R.drawable.ic_action_unread)
			.setContentTitle(title)
			.setContentText(text)
			.setAutoCancel(true);
		
		Intent resultIntent = new Intent(this, NotificationController.class);
		
		PendingIntent resultPendingIntent = PendingIntent.getActivity(
												this,
												0,
												resultIntent,
												PendingIntent.FLAG_UPDATE_CURRENT
												);
		
		builder.setContentIntent(resultPendingIntent);
		
		NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

		notificationManager.notify(newNotificationNotificationId, builder.build());
	}
	
	protected void deleteIcon()
	{
		((NotificationManager) getSystemService(NOTIFICATION_SERVICE)).cancel(newNotificationNotificationId);
	}
}