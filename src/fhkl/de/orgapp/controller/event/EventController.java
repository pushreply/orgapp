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
					System.out.println("Duerfte true sein: " + on);

				} else {
					System.out.println("Duerfte false sein: " + on);
				}

			}
		});
		 }
}