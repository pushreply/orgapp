package fhkl.de.orgapp.util;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.AsyncTask;

public class NewNotifications
{
	private static String URL_CHECK_FOR_NEW_NOTIFICATIONS = "http://pushrply.com/check_for_new_notifications.php";
	private static String URL_GET_NUMBER_OF_NEW_NOTIFICATIONS = "http://pushrply.com/get_number_new_notifications.php";
	private static final String TAG_SUCCESS = "success";
	private static final String TAG_HAS_NEW_NOTIFICATIONS = "hasNewNotifications";
	private static final String TAG_NEW_NOTIFICATIONS = "new notifications";
	private static final String TAG_NO_NEW_NOTIFICATIONS = "no new notifications";
	private static final String TAG_NUMBER_NEW_NOTIFICATIONS = "numberNewNotifications";
	
	JSONObject json = null;
	private boolean hasNewNotifications;
	private String numberNewNotifications;
	
	public NewNotifications()
	{
		AsyncTask<String, String, String> notificationChecker = new NotificationChecker().execute();
		
		try
		{
			if(notificationChecker.get() != null && notificationChecker.get().equals(TAG_NEW_NOTIFICATIONS))
			{
				hasNewNotifications = true;
				
				AsyncTask<String, String, String> newNotificationsCounter = new NumberNewNotificationsGetter().execute();
				
				if(newNotificationsCounter.get() != null)
					numberNewNotifications = newNotificationsCounter.get();
				else
					numberNewNotifications = "0";
			}
			else
			{
				hasNewNotifications = false;
				numberNewNotifications = "0";
			}
		}
		catch(Exception e)
		{
			System.out.println("Error in NewNotifications.NewNotifications(): " + e.getMessage());
			e.printStackTrace();
		}
	}
	
	public boolean hasNewNotifications()
	{
		return hasNewNotifications;
	}

	public String getNumberNewNotifications()
	{
		return numberNewNotifications;
	}

	class NotificationChecker extends AsyncTask<String, String, String>
	{
		@Override
		protected String doInBackground(String... arg)
		{
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair("personId", UserData.getPERSONID()));
			
			json = new JSONParser().makeHttpRequest(URL_CHECK_FOR_NEW_NOTIFICATIONS, "GET", params);
			
			try
			{
				int success = json.getInt(TAG_SUCCESS);
				
				if(success == 1)
				{
					int hasNewNotifications = json.getInt(TAG_HAS_NEW_NOTIFICATIONS);
					
					if(hasNewNotifications == 1)
						return TAG_NEW_NOTIFICATIONS;
					
					return TAG_NO_NEW_NOTIFICATIONS;
				}
				else
				{
					return null;
				}
			}
			catch(JSONException e)
			{
				System.out.println("Error in NewNotifications.NotificationChecker.doInBackground(String...): " + e.getMessage());
				e.printStackTrace();
			}
			
			return null;
		}
	}
	
	class NumberNewNotificationsGetter extends AsyncTask<String, String, String>
	{
		@Override
		protected String doInBackground(String... arg)
		{
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair("personId", UserData.getPERSONID()));
			
			json = new JSONParser().makeHttpRequest(URL_GET_NUMBER_OF_NEW_NOTIFICATIONS, "GET", params);
			
			try
			{
				int success = json.getInt(TAG_SUCCESS);
				
				if(success == 1)
				{
					return json.getString(TAG_NUMBER_NEW_NOTIFICATIONS);
				}
				else
				{
					return null;
				}
			}
			catch(JSONException e)
			{
				System.out.println("Error in NewNotifications.NumberNewNotificationsGetter.doInBackground(String...): " + e.getMessage());
				e.printStackTrace();
			}
			
			return null;
		}
	}
}