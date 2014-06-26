package fhkl.de.orgapp.controller.event;

import android.os.Bundle;
import android.widget.TextView;
import fhkl.de.orgapp.R;
import fhkl.de.orgapp.util.EventData;
import fhkl.de.orgapp.util.MenuActivity;

public class EventController extends MenuActivity
{
	TextView eventTime;
	TextView eventDate;
	TextView eventLocation;

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.event);
		
		this.setTitle(EventData.getNAME());
		eventTime = (TextView)findViewById(R.id.EVENTTIME);
		eventDate = (TextView)findViewById(R.id.EVENTDATE);
		eventLocation = (TextView)findViewById(R.id.EVENTLOCATION);
		
		eventTime.setText("Time: " + EventData.getEVENTTIME());
		eventDate.setText("Date: " + EventData.getEVENTDATE());
		eventLocation.setText("Location: " + EventData.getEVENTLOCATION());
	}
}