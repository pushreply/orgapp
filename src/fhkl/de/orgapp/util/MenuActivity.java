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
	private String personIdLoggedPerson;

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
			intent.putExtra("UserId", personIdLoggedPerson);
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
			intent.putExtra("UserId", personIdLoggedPerson);
			startActivity(intent);
			return true;

		case R.id.LIST_EVENT_HISTORY:
			intent = new Intent(MenuActivity.this, EventHistoryController.class);
			intent.putExtra("UserId", personIdLoggedPerson);
			startActivity(intent);
			return true;

		case R.id.CHANGE_SECURITY_INFORMATION:
			intent = new Intent(MenuActivity.this, SecurityInfoController.class);
			intent.putExtra("UserId", personIdLoggedPerson);
			startActivity(intent);
			return true;

		case R.id.LOGOUT:
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