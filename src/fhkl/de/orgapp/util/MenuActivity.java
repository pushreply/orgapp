package fhkl.de.orgapp.util;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import fhkl.de.orgapp.R;
import fhkl.de.orgapp.controller.calendar.CalendarController;
import fhkl.de.orgapp.controller.groups.EditGroupController;
import fhkl.de.orgapp.controller.groups.GroupsController;
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
			if (GroupData.getPERSONID().equals(UserData.getPERSONID())) {
				menu.findItem(R.id.EDIT_GROUP).setVisible(true);
			}
			menu.findItem(R.id.DELETE_GROUP).setVisible(true);
			menu.findItem(R.id.LEAVE_GROUP).setVisible(true);
			menu.findItem(R.id.SHOW_MEMBER_LIST).setVisible(true);
			menu.findItem(R.id.INVITE_MEMBER).setVisible(true);
		}

		if (nameCurrentController.equals(MemberListController.class.getName())) {
			menu.findItem(R.id.BACK_TO_GROUP).setVisible(true);
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

		case R.id.EDIT_GROUP:
			intent = new Intent(MenuActivity.this, EditGroupController.class);
			startActivity(intent);
			return true;

		case R.id.SHOW_MEMBER_LIST:
			intent = new Intent(MenuActivity.this, MemberListController.class);
			startActivity(intent);
			return true;

		case R.id.BACK_TO_GROUP:
			intent = new Intent(MenuActivity.this, SingleGroupController.class);
			startActivity(intent);
			return true;

		case R.id.INVITE_MEMBER:
			AlertDialog.Builder builder = new AlertDialog.Builder(MenuActivity.this);
			builder.setMessage(IMessages.QUESTION_MEMBER);
			builder.setPositiveButton(IMessages.LIST, new OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {
					Intent intent = new Intent(MenuActivity.this, ListInviteMemberController.class);
					startActivity(intent);
				}

			});
			builder.setNegativeButton(IMessages.MANUALLY, new OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {
					Intent intent = new Intent(MenuActivity.this, ManualInviteMemberController.class);
					startActivity(intent);
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
			intent.putExtra("UserId", UserData.getPERSONID());
			intent.putExtra("FirstName", UserData.getFIRST_NAME());
			intent.putExtra("LastName", UserData.getLAST_NAME());
			intent.putExtra("Birthday", UserData.getBIRTHDAY());
			intent.putExtra("Gender", UserData.getGENDER());
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
		CalendarController.resetSTART_ACTIVITY_COUNTER();

		UserData.setPERSONID("");
		UserData.setFIRST_NAME("");
		UserData.setLAST_NAME("");
		UserData.setBIRTHDAY("");
		UserData.setGENDER("");
		UserData.setEMAIL("");
		UserData.setMEMBER_SINCE("");

		Intent intent = new Intent(MenuActivity.this, StartController.class);
		startActivity(intent);
	}
}