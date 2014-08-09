package fhkl.de.orgapp.util.check;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import android.os.AsyncTask;
import fhkl.de.orgapp.util.IUniformResourceLocator;
import fhkl.de.orgapp.util.JSONParser;
import fhkl.de.orgapp.util.data.NotificationSettingsData;
import fhkl.de.orgapp.util.data.UserData;

/**
 * NewNotificationsChecker - Handles the data to check for new notifications
 * 
 * @author Oliver Neubauer
 * @version 3.5
 * 
 */

public class NewNotificationsChecker {
	// Required tags
	private static final String TAG_SUCCESS = "success";
	private static final String TAG_HAS_NEW_NOTIFICATIONS = "hasNewNotifications";
	private static final String TAG_NEW_NOTIFICATIONS = "new notifications";
	private static final String TAG_NO_NEW_NOTIFICATIONS = "no new notifications";
	private static final String TAG_NUMBER_NEW_NOTIFICATIONS = "numberNewNotifications";

	// For json issues
	JSONParser jsonParser = new JSONParser();
	JSONObject json = null;

	// Member attributes
	private boolean hasNewNotifications;
	private String numberNewNotifications;

	// Constructor
	public NewNotificationsChecker() {
		// Execute the request
		// Object for getting the respone within the background thread
		AsyncTask<String, String, String> notificationChecker = new NotificationChecker().execute();

		try {
			// In case of new notifications
			if (notificationChecker.get() != null && notificationChecker.get().equals(TAG_NEW_NOTIFICATIONS)) {
				hasNewNotifications = true;

				// Execute the request
				// Object for getting the respone within the background thread
				AsyncTask<String, String, String> newNotificationsCounter = new NumberNewNotificationsGetter().execute();

				// Get the number of new notifications, if available
				if (newNotificationsCounter.get() != null)
					numberNewNotifications = newNotificationsCounter.get();
				// Otherwise
				else
					numberNewNotifications = "0";
			}
			// In case of no new notifications
			else {
				hasNewNotifications = false;
				numberNewNotifications = "0";
			}
		}
		// In case of error
		catch (Exception e) {
			hasNewNotifications = false;
			numberNewNotifications = "0";
		}
	}

	/**
	 * Get the logical value for new notifications
	 * 
	 * @return true, in case of new notifications, false otherwise
	 */
	public boolean hasNewNotifications() {
		return hasNewNotifications;
	}

	/**
	 * Get the number of new notifications
	 * 
	 * @return the number of new notifications
	 */

	public String getNumberNewNotifications() {
		return numberNewNotifications;
	}

	/**
	 * NotificationChecker - Checks for new notifications
	 * 
	 * 
	 */

	class NotificationChecker extends AsyncTask<String, String, String> {
		/**
		 * Makes the request to check for new notifications
		 * 
		 * @param arg the arguments as array
		 * @return the logical value of new notifications or null in case of error
		 */

		@Override
		protected String doInBackground(String... arg) {
			// Define and initialize params
			List<NameValuePair> params = initializeParams(new ArrayList<NameValuePair>());
			params.add(new BasicNameValuePair("do", "checkunread"));

			// Make the request to check for new notifications
			// HTTP request, because the HTTPS request made some problems about the
			// context
			json = new JSONParser().makeHttpRequest(IUniformResourceLocator.URL.URL_NOTIFICATION_HTTP, "GET", params);

			try {
				int success = json.getInt(TAG_SUCCESS);

				// In case of success
				if (success == 1) {
					int hasNewNotifications = json.getInt(TAG_HAS_NEW_NOTIFICATIONS);

					// In case of new notifications
					if (hasNewNotifications == 1)
						return TAG_NEW_NOTIFICATIONS;

					// In case of no new notifications
					return TAG_NO_NEW_NOTIFICATIONS;
				}
				// In case of no success
				else {
					return null;
				}
			}
			// In case of error
			catch (Exception e) {
				return null;
			}
		}
	}

	/**
	 * NumberNewNotificationsGetter - Fetches the number of new notifications
	 * 
	 * 
	 */

	class NumberNewNotificationsGetter extends AsyncTask<String, String, String> {
		/**
		 * Makes the request to fetch the number of new notifications
		 * 
		 * @param arg the arguments as array
		 * @return the number of new notifications or null in case of error
		 */

		@Override
		protected String doInBackground(String... arg) {
			// Define and initialize params
			List<NameValuePair> params = initializeParams(new ArrayList<NameValuePair>());
			params.add(new BasicNameValuePair("do", "checknumberunread"));

			// Make the request to fetch the number of new notifications
			// HTTP request, because the HTTPS request made some problems about the
			// context
			json = new JSONParser().makeHttpRequest(IUniformResourceLocator.URL.URL_NOTIFICATION_HTTP, "GET", params);

			try {
				int success = json.getInt(TAG_SUCCESS);

				// In case of success
				if (success == 1) {
					return json.getString(TAG_NUMBER_NEW_NOTIFICATIONS);
				}
				// In case of no success
				else {
					return null;
				}
			}
			// In case of error
			catch (Exception e) {
				return null;
			}
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