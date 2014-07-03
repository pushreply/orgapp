package fhkl.de.orgapp.controller.event;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import android.widget.ToggleButton;
import fhkl.de.orgapp.R;
import fhkl.de.orgapp.util.EventData;
import fhkl.de.orgapp.util.MenuActivity;

public class EventController extends MenuActivity {
	TextView eventTime;
	TextView eventDate;
	TextView eventLocation;

	ToggleButton buttonAttendance;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.event);

		this.setTitle(EventData.getNAME());
		eventTime = (TextView) findViewById(R.id.EVENTTIME);
		eventDate = (TextView) findViewById(R.id.EVENTDATE);
		eventLocation = (TextView) findViewById(R.id.EVENTLOCATION);

		eventTime.setText("Time: " + EventData.getEVENTTIME());
		eventDate.setText("Date: " + EventData.getEVENTDATE());
		eventLocation.setText("Location: " + EventData.getEVENTLOCATION());
		buttonAttendance = (ToggleButton) findViewById(R.id.BUTTONATTENDANCE);
		buttonAttendance.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				boolean on = ((ToggleButton) v).isChecked();

				if (on) {
					System.out.println("DŸrfte true sein:" + on);

				} else {
					System.out.println("DŸrfte false sein:" + on);
				}

			}
		});
		// }
		//
		// private ShareActionProvider mShareAction;
		//
		//
		//
		// @Override
		// public boolean onCreateOptionsMenu(Menu menu) {
		// // Inflate menu resource file.
		// getMenuInflater().inflate(R.menu.main_menu, menu);
		//
		// // Locate MenuItem with ShareActionProvider
		// MenuItem item = menu.findItem(R.id.EVENT_SHARE).setVisible(true);
		//
		// // Fetch and store ShareActionProvider
		// mShareAction = (ShareActionProvider) item.getActionProvider();
		//
		// Intent sendIntent = new Intent();
		// sendIntent.setAction(Intent.ACTION_SEND);
		// sendIntent.putExtra(Intent.EXTRA_TEXT, EventData.getNAME() + "; " +
		// EventData.getEVENTDATE() );
		// sendIntent.putExtra(Intent.EXTRA_SUBJECT, EventData.getNAME());
		// String shareBody =
		// "New Event: " + EventData.getNAME()
		// + ", Date: " + EventData.getEVENTDATE()
		// + ", Time: " + EventData.getEVENTTIME()
		// + ", Location: " + EventData.getEVENTLOCATION();
		// sendIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
		// sendIntent.setType("text/plain");
		// startActivity(Intent.createChooser(sendIntent, "Share via"));
		//
		// // Return true to display menu
		// return true;
		// }
		//
		// // Call to update the share intent
		// private void setShareIntent(Intent shareIntent) {
		// if (mShareAction != null) {
		// mShareAction.setShareIntent(shareIntent);
		// }
	}
}