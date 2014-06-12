package fhkl.de.orgapp.controller.notification;

import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;

import android.os.Bundle;
import fhkl.de.orgapp.R;
import fhkl.de.orgapp.util.JSONParser;
import fhkl.de.orgapp.util.MenuActivity;

public class NotificationSettingsController extends MenuActivity {

	JSONParser jsonParser = new JSONParser();
	ArrayList<HashMap<String, String>> notificationList;


	JSONArray notification = null;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.notification_settings);
	}
}