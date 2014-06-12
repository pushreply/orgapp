package fhkl.de.orgapp.util;

import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import fhkl.de.orgapp.R;
import fhkl.de.orgapp.controller.calendar.CalendarController;
import fhkl.de.orgapp.controller.groups.GroupController;
import fhkl.de.orgapp.controller.notification.NotificationController;
import fhkl.de.orgapp.controller.notification.NotificationSettingsController;
import fhkl.de.orgapp.controller.profile.EventHistoryController;
import fhkl.de.orgapp.controller.profile.PrivateInfoController;
import fhkl.de.orgapp.controller.profile.ProfileController;
import fhkl.de.orgapp.controller.profile.SecurityInfoController;

public class MenuActivity extends Activity
{
	private String personIdLoggedPerson;

	public boolean onCreateOptionsMenu(Menu menu)
	{
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.main_menu, menu);
		
		if(getIntent().getComponent().getClassName().equals(NotificationController.class.getName()))
		{
			menu.findItem(R.id.NOTIFICATION_SETTINGS).setVisible(true);
		}
		
		if(getIntent().getComponent().getClassName().equals(ProfileController.class.getName()))
		{
			menu.findItem(R.id.PROFIL_SETTINGS).setVisible(true);
		}

		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		Intent intent;
		personIdLoggedPerson = getIntent().getStringExtra("UserId");

		switch(item.getItemId())
		{
			case R.id.CALENDAR:
				intent = new Intent(MenuActivity.this, CalendarController.class);
				intent.putExtra("UserId", personIdLoggedPerson);
				startActivity(intent);
				return true;
	
			case R.id.NOTIFICATIONS:
				intent = new Intent(MenuActivity.this, NotificationController.class);
				intent.putExtra("UserId", personIdLoggedPerson);
				startActivity(intent);
				return true;
	
			case R.id.PROFIL:
				intent = new Intent(MenuActivity.this, ProfileController.class);
				intent.putExtra("UserId", personIdLoggedPerson);
				startActivity(intent);
				return true;
	
			case R.id.NOTIFICATION_SETTINGS:
				intent = new Intent(MenuActivity.this, NotificationSettingsController.class);
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
				
			case R.id.GROUPS:
				intent = new Intent(MenuActivity.this, GroupController.class);
				intent.putExtra("UserId", personIdLoggedPerson);
				startActivity(intent);
				return true;
	
			default:
				return false;
		}
	}
}