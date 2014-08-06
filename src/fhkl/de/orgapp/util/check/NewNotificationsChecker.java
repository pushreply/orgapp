package fhkl.de.orgapp.util.check;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.AsyncTask;
import fhkl.de.orgapp.util.IUniformResourceLocator;
import fhkl.de.orgapp.util.JSONParser;
import fhkl.de.orgapp.util.data.NotificationSettingsData;
import fhkl.de.orgapp.util.data.UserData;

public class NewNotificationsChecker {
	private static final String TAG_SUCCESS = "success";
	private static final String TAG_HAS_NEW_NOTIFICATIONS = "hasNewNotifications";
	private static final String TAG_NEW_NOTIFICATIONS = "new notifications";
	private static final String TAG_NO_NEW_NOTIFICATIONS = "no new notifications";
	private static final String TAG_NUMBER_NEW_NOTIFICATIONS = "numberNewNotifications";

	JSONObject json = null;
	JSONParser jsonParser = new JSONParser();
	private boolean hasNewNotifications;
	private String numberNewNotifications;

	public NewNotificationsChecker() {
		AsyncTask<String, String, String> notificationChecker = new NotificationChecker().execute();

		try {
			if (notificationChecker.get() != null && notificationChecker.get().equals(TAG_NEW_NOTIFICATIONS)) {
				hasNewNotifications = true;

				AsyncTask<String, String, String> newNotificationsCounter = new NumberNewNotificationsGetter().execute();

				if (newNotificationsCounter.get() != null)
					numberNewNotifications = newNotificationsCounter.get();
				else
					numberNewNotifications = "0";
			} else {
				hasNewNotifications = false;
				numberNewNotifications = "0";
			}
		} catch (Exception e) {
			System.out.println("Error in NewNotifications.NewNotifications(): " + e.getMessage());
			e.printStackTrace();
		}
	}

	public boolean hasNewNotifications() {
		return hasNewNotifications;
	}

	public String getNumberNewNotifications() {
		return numberNewNotifications;
	}

	class NotificationChecker extends AsyncTask<String, String, String> {
		@Override
		protected String doInBackground(String... arg) {
			// Define and initialize params
			List<NameValuePair> params = initializeParams(new ArrayList<NameValuePair>());
			params.add(new BasicNameValuePair("do", "checkunread"));

			json = new JSONParser().makeHttpRequest(IUniformResourceLocator.URL.URL_NOTIFICATION_HTTP, "GET", params);

			try {
				int success = json.getInt(TAG_SUCCESS);

				if (success == 1) {
					int hasNewNotifications = json.getInt(TAG_HAS_NEW_NOTIFICATIONS);

					if (hasNewNotifications == 1)
						return TAG_NEW_NOTIFICATIONS;

					return TAG_NO_NEW_NOTIFICATIONS;
				} else {
					return null;
				}
			} catch (JSONException e) {
				System.out
								.println("Error in NewNotifications.NotificationChecker.doInBackground(String...): " + e.getMessage());
				e.printStackTrace();
			}

			return null;
		}
	}

	class NumberNewNotificationsGetter extends AsyncTask<String, String, String> {
		@Override
		protected String doInBackground(String... arg) {
			// Define and initialize params
			List<NameValuePair> params = initializeParams(new ArrayList<NameValuePair>());
			params.add(new BasicNameValuePair("do", "checknumberunread"));

			json = new JSONParser().makeHttpRequest(IUniformResourceLocator.URL.URL_NOTIFICATION_HTTP, "GET", params);

			try {
				int success = json.getInt(TAG_SUCCESS);

				if (success == 1) {
					return json.getString(TAG_NUMBER_NEW_NOTIFICATIONS);
				} else {
					return null;
				}
			} catch (JSONException e) {
				System.out.println("Error in NewNotifications.NumberNewNotificationsGetter.doInBackground(String...): "
								+ e.getMessage());
				e.printStackTrace();
			}

			return null;
		}
	}

	/**
	 * Initialize a list of required parameters
	 * 
	 * @param params the empty list
	 * @return the initialized list
	 */
	private List<NameValuePair> initializeParams(List<NameValuePair> params) {
		if (params == null)
			params = new ArrayList<NameValuePair>();

		params.add(new BasicNameValuePair("personId", UserData.getPERSONID()));

		String groupInvites = Boolean.parseBoolean(NotificationSettingsData.getGROUP_INVITES()) ? "1" : null;
		if (groupInvites != null) {
			params.add(new BasicNameValuePair("groupInvites", groupInvites));
		}
		String groupEdited = Boolean.parseBoolean(NotificationSettingsData.getGROUP_EDITED()) ? "2" : null;
		if (groupEdited != null) {
			params.add(new BasicNameValuePair("groupEdited", groupEdited));
		}
		String groupRemoved = Boolean.parseBoolean(NotificationSettingsData.getGROUP_REMOVED()) ? "3" : null;
		if (groupRemoved != null) {
			params.add(new BasicNameValuePair("groupRemoved", groupRemoved));
		}
		String eventsAdded = Boolean.parseBoolean(NotificationSettingsData.getEVENTS_ADDED()) ? "4" : null;
		if (eventsAdded != null) {
			params.add(new BasicNameValuePair("eventsAdded", eventsAdded));
		}
		String eventsEdited = Boolean.parseBoolean(NotificationSettingsData.getEVENTS_EDITED()) ? "5" : null;
		if (eventsEdited != null) {
			params.add(new BasicNameValuePair("eventsEdited", eventsEdited));
		}
		String eventsRemoved = Boolean.parseBoolean(NotificationSettingsData.getEVENTS_REMOVED()) ? "6" : null;
		if (eventsRemoved != null) {
			params.add(new BasicNameValuePair("eventsRemoved", eventsRemoved));
		}
		String commentsAdded = Boolean.parseBoolean(NotificationSettingsData.getCOMMENTS_ADDED()) ? "7" : null;
		if (commentsAdded != null) {
			params.add(new BasicNameValuePair("commentsAdded", commentsAdded));
		}
		String commentsEdited = Boolean.parseBoolean(NotificationSettingsData.getCOMMENTS_EDITED()) ? "8" : null;
		if (commentsEdited != null) {
			params.add(new BasicNameValuePair("commentsEdited", commentsEdited));
		}
		String commentsRemoved = Boolean.parseBoolean(NotificationSettingsData.getCOMMENTS_REMOVED()) ? "9" : null;
		if (commentsRemoved != null) {
			params.add(new BasicNameValuePair("commentsRemoved", commentsRemoved));
		}
		String privilegeGiven = Boolean.parseBoolean(NotificationSettingsData.getPRIVILEGE_GIVEN()) ? "10" : null;
		if (privilegeGiven != null) {
			params.add(new BasicNameValuePair("privilegeGiven", privilegeGiven));
		}

		return params;
	}
}