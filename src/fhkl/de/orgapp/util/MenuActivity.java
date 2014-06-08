package fhkl.de.orgapp.util;

import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.example.orgapp.R;

import fhkl.de.orgapp.controller.ProfilController;
import fhkl.de.orgapp.controller.StartController;

public class MenuActivity extends Activity {

	private String eMailLoggedPerson;

	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.main_menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Intent intent;

		switch (item.getItemId()) {
		case R.id.CALENDAR:
			intent = new Intent(MenuActivity.this, StartController.class);
			intent.putExtra("UserEmail", eMailLoggedPerson);
			startActivity(intent);
			return true;

		case R.id.NOTIFICATIONS:
			intent = new Intent(MenuActivity.this, StartController.class);
			intent.putExtra("UserEmail", eMailLoggedPerson);
			startActivity(intent);
			return true;

		case R.id.PROFIL:
			intent = new Intent(MenuActivity.this, ProfilController.class);
			intent.putExtra("UserEmail", eMailLoggedPerson);
			startActivity(intent);
			return true;

		default:
			return false;

		}
	}
}
