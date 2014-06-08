package fhkl.de.orgapp.util;

import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.example.orgapp.R;

import fhkl.de.orgapp.controller.CalendarController;
import fhkl.de.orgapp.controller.NotificationController;
import fhkl.de.orgapp.controller.ProfilController;

public class MenuActivity extends Activity {

	private String personIdLoggedPerson;

	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.main_menu, menu);
		if(getIntent().getComponent().getClassName().equals(NotificationController.class.getName())) {
			menu.findItem(R.id.NOTIFICATION_SETTINGS).setVisible(true);
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

		case R.id.NOTIFICATIONS:
			intent = new Intent(MenuActivity.this, NotificationController.class);
			intent.putExtra("UserId", personIdLoggedPerson);
			startActivity(intent);
			return true;

		case R.id.PROFIL:
			intent = new Intent(MenuActivity.this, ProfilController.class);
			intent.putExtra("UserId", personIdLoggedPerson);
			startActivity(intent);
			return true;

		default:
			return false;

		}
	}
}
