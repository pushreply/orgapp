package fhkl.de.orgapp.util;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import fhkl.de.orgapp.R;
import fhkl.de.orgapp.controller.calendar.CalendarController;
import fhkl.de.orgapp.controller.groups.GroupsController;
import fhkl.de.orgapp.controller.groups.ListInviteMemberController;
import fhkl.de.orgapp.controller.groups.ManualInviteMemberController;
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
	
	//counter for starting this activity during user is logged in
	private static int START_ACTIVITY_COUNTER = 0;
	
	private String personIdLoggedPerson;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		
		START_ACTIVITY_COUNTER++;
		
		//set user data after login
		if(START_ACTIVITY_COUNTER == 1)
		{
			UserData.setID(getIntent().getStringExtra("UserId"));
			UserData.setFIRST_NAME(getIntent().getStringExtra("UserFirstName"));
			UserData.setLAST_NAME(getIntent().getStringExtra("UserLastName"));
			UserData.setBIRTHDAY(getIntent().getStringExtra("UserBirthday"));
			UserData.setGENDER(getIntent().getStringExtra("UserGender"));
			UserData.setEMAIL(getIntent().getStringExtra("UserEmail"));
			UserData.setMEMBER_SINCE(getIntent().getStringExtra("UserMemberSince"));
		}
	}

	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.main_menu, menu);
		String nameCurrentController = getIntent().getComponent().getClassName();

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

		if (nameCurrentController.equals(GroupsController.class.getName())) {
			menu.findItem(R.id.GROUPS).setVisible(false);
		}

		if (nameCurrentController.equals(SingleGroupController.class.getName())) {
			menu.findItem(R.id.GROUP_SETTINGS).setVisible(true);
			menu.findItem(R.id.CREATE_EVENT).setVisible(true);
			menu.findItem(R.id.EDIT_GROUP).setVisible(true);
			menu.findItem(R.id.DELETE_GROUP).setVisible(true);
			menu.findItem(R.id.LEAVE_GROUP).setVisible(true);
			menu.findItem(R.id.MEMBER_LIST).setVisible(true);
			menu.findItem(R.id.INVITE_MEMBER).setVisible(true);
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
		
		personIdLoggedPerson = getIntent().getStringExtra("UserId");
		
		switch (item.getItemId()) {
		case R.id.CALENDAR:
			intent = new Intent(MenuActivity.this, CalendarController.class);
			intent.putExtra("UserId", personIdLoggedPerson);
			startActivity(intent);
			return true;

		case R.id.GROUPS:
			intent = new Intent(MenuActivity.this, GroupsController.class);
			intent.putExtra("UserId", personIdLoggedPerson);
			startActivity(intent);
			return true;

		case R.id.NEW_GROUP:
			intent = new Intent(MenuActivity.this, NewGroupController.class);
			intent.putExtra("UserId", personIdLoggedPerson);
			startActivity(intent);
			return true;

		case R.id.INVITE_MEMBER:
			AlertDialog.Builder builder = new AlertDialog.Builder(MenuActivity.this);
			builder.setMessage(IMessages.MEMBER_QUESTION);
			builder.setPositiveButton(IMessages.LIST, new OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {
					Intent intent = new Intent(MenuActivity.this,
							ListInviteMemberController.class);
					intent.putExtra("UserId", personIdLoggedPerson);
					intent.putExtra("GroupId", getIntent().getStringExtra("GroupId"));
					System.out.println(getIntent().getStringExtra("GroupName"));
					intent.putExtra("GroupName", getIntent().getStringExtra("GroupName"));
					startActivity(intent);
				}

			});
			builder.setNegativeButton(IMessages.MANUALLY, new OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {
					Intent intent = new Intent(MenuActivity.this,
							ManualInviteMemberController.class);
					intent.putExtra("UserId", personIdLoggedPerson);
					intent.putExtra("GroupId", getIntent().getStringExtra("GroupId"));
					System.out.println(getIntent().getStringExtra("GroupName"));
					intent.putExtra("GroupName", getIntent().getStringExtra("GroupName"));
					startActivity(intent);
				}
			});
			builder.create().show();
			return true;

		case R.id.NOTIFICATIONS:
			intent = new Intent(MenuActivity.this, NotificationController.class);
			intent.putExtra("UserId", personIdLoggedPerson);
			startActivity(intent);
			return true;

		case R.id.PROFILE:
			intent = new Intent(MenuActivity.this, ProfileController.class);
			intent.putExtra("FirstName", UserData.getFIRST_NAME());
			intent.putExtra("LastName", UserData.getLAST_NAME());
			intent.putExtra("Birthday", UserData.getBIRTHDAY());
			intent.putExtra("Gender", UserData.getGENDER());
			intent.putExtra("Email", UserData.getEMAIL());
			intent.putExtra("MemberSince", UserData.getMEMBER_SINCE());
			startActivity(intent);
			return true;

		case R.id.NOTIFICATION_SETTINGS:
			intent = new Intent(MenuActivity.this,
					NotificationSettingsController.class);
			intent.putExtra("UserId", personIdLoggedPerson);
			startActivity(intent);
			return true;

		case R.id.CHANGE_PRIVATE_INFORMATION:
			intent = new Intent(MenuActivity.this, PrivateInfoController.class);
			intent.putExtra("UserId", UserData.getID());
			intent.putExtra("FirstName", UserData.getFIRST_NAME());
			intent.putExtra("LastName", UserData.getLAST_NAME());
			intent.putExtra("Birthday", UserData.getBIRTHDAY());
			intent.putExtra("Gender", UserData.getGENDER());
			startActivity(intent);
			return true;

		case R.id.LIST_EVENT_HISTORY:
			intent = new Intent(MenuActivity.this, EventHistoryController.class);
			intent.putExtra("UserId", personIdLoggedPerson);
			startActivity(intent);
			return true;

		case R.id.CHANGE_SECURITY_INFORMATION:
			intent = new Intent(MenuActivity.this, SecurityInfoController.class);
			intent.putExtra("UserId", UserData.getID());
			intent.putExtra("Email", UserData.getEMAIL());
			startActivity(intent);
			return true;

		case R.id.LOGOUT:
			//reset counter and user data at logout
			START_ACTIVITY_COUNTER = 0;
			
			UserData.setID("");
			UserData.setFIRST_NAME("");
			UserData.setLAST_NAME("");
			UserData.setBIRTHDAY("");
			UserData.setGENDER("");
			UserData.setEMAIL("");
			UserData.setMEMBER_SINCE("");
			
			intent = new Intent(MenuActivity.this, StartController.class);
			startActivity(intent);
			return true;

		case R.id.REFRESH:
			finish();
			startActivity(getIntent().putExtra("Refresh", "Refresh"));

		default:
			return false;
		}
	}
}