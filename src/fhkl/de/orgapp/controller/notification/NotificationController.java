package fhkl.de.orgapp.controller.notification;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import fhkl.de.orgapp.R;
import fhkl.de.orgapp.util.IMessages;
import fhkl.de.orgapp.util.JSONParser;
import fhkl.de.orgapp.util.MenuActivity;
import fhkl.de.orgapp.util.data.NotificationSettingsData;
import fhkl.de.orgapp.util.data.UserData;

public class NotificationController extends MenuActivity
{
	// For http request
	private static String url_get_notification = "http://pushrply.com/get_notifications.php";
	private static String url_update_notification_read_status = "http://pushrply.com/update_notification_read_status.php";

	// For json issues
	JSONParser jsonParser = new JSONParser();
	private static final String TAG_SUCCESS = "success";
	private static final String TAG_ID = "ID";
	private static final String TAG_MESSAGE = "MESSAGE";
	private static final String TAG_IS_READ = "IS READ";
	JSONArray notification = null;

	// Required variables for progress dialog and the notifications
	private ProgressDialog pDialog;
	ArrayList<HashMap<String, String>> notificationList;
	
	@SuppressLint("InflateParams")
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		// Delete the icon signaled user for new notifications
		deleteIcon();

		// Set the layout
		setContentView(R.layout.notification);
		
		// New list for notifications
		notificationList = new ArrayList<HashMap<String, String>>();
		
		// Fetch the notifications
		new Notification().execute();

		// Fetch the message field
		LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View notificationItemView = inflater.inflate(R.layout.notification_item, null);
		TextView message = (TextView) notificationItemView.findViewById(R.id.MESSAGE);

		// Increase maximal lines of the field at older android versions
		if (Integer.valueOf(android.os.Build.VERSION.SDK_INT) < 16) {
			message.setMaxLines(Integer.MAX_VALUE);
		}
	}

	class Notification extends AsyncTask<String, String, String>
	{
		@Override
		protected void onPreExecute()
		{
			super.onPreExecute();
			
			// Display a progress dialog
			pDialog = new ProgressDialog(NotificationController.this);

			// In case of press the refresh button set an appropriate message
			if (getIntent().getStringExtra("Refresh") != null)
				pDialog.setMessage(IMessages.Status.UPDATING);
			// Otherwise set another message
			else
				pDialog.setMessage(IMessages.Status.LOADING_NOTIFICATIONS);

			pDialog.setIndeterminate(false);
			pDialog.setCancelable(true);
			pDialog.show();
		}

		protected String doInBackground(String... args)
		{
			try
			{
				// Required parameters for the request
				List<NameValuePair> paramsNotifications = new ArrayList<NameValuePair>();
				paramsNotifications.add(new BasicNameValuePair("personId", UserData.getPERSONID()));

				// Set the parameters according the notification settings
				String groupInvites = Boolean.parseBoolean(NotificationSettingsData.getGROUP_INVITES()) ? "1" : null;
				if (groupInvites != null) {
					paramsNotifications.add(new BasicNameValuePair("groupInvites", groupInvites));
				}
				String groupEdited = Boolean.parseBoolean(NotificationSettingsData.getGROUP_EDITED()) ? "2" : null;
				if (groupEdited != null) {
					paramsNotifications.add(new BasicNameValuePair("groupEdited", groupEdited));
				}
				String groupRemoved = Boolean.parseBoolean(NotificationSettingsData.getGROUP_REMOVED()) ? "3" : null;
				if (groupRemoved != null) {
					paramsNotifications.add(new BasicNameValuePair("groupRemoved", groupRemoved));
				}
				String eventsAdded = Boolean.parseBoolean(NotificationSettingsData.getEVENTS_ADDED()) ? "4" : null;
				if (eventsAdded != null) {
					paramsNotifications.add(new BasicNameValuePair("eventsAdded", eventsAdded));
				}
				String eventsEdited = Boolean.parseBoolean(NotificationSettingsData.getEVENTS_EDITED()) ? "5" : null;
				if (eventsEdited != null) {
					paramsNotifications.add(new BasicNameValuePair("eventsEdited", eventsEdited));
				}
				String eventsRemoved = Boolean.parseBoolean(NotificationSettingsData.getEVENTS_REMOVED()) ? "6" : null;
				if (eventsRemoved != null) {
					paramsNotifications.add(new BasicNameValuePair("eventsRemoved", eventsRemoved));
				}
				String commentsAdded = Boolean.parseBoolean(NotificationSettingsData.getCOMMENTS_ADDED()) ? "7" : null;
				if (commentsAdded != null) {
					paramsNotifications.add(new BasicNameValuePair("commentsAdded", commentsAdded));
				}
				String commentsEdited = Boolean.parseBoolean(NotificationSettingsData.getCOMMENTS_EDITED()) ? "8" : null;
				if (commentsEdited != null) {
					paramsNotifications.add(new BasicNameValuePair("commentsEdited", commentsEdited));
				}
				String commentsRemoved = Boolean.parseBoolean(NotificationSettingsData.getCOMMENTS_REMOVED()) ? "9" : null;
				if (commentsRemoved != null) {
					paramsNotifications.add(new BasicNameValuePair("commentsRemoved", commentsRemoved));
				}
				String privilegeGiven = Boolean.parseBoolean(NotificationSettingsData.getPRIVILEGE_GIVEN()) ? "10" : null;
				if (privilegeGiven != null) {
					paramsNotifications.add(new BasicNameValuePair("privilegeGiven", privilegeGiven));
				}
				if (NotificationSettingsData.getSHOW_ENTRIES() != null && !NotificationSettingsData.getSHOW_ENTRIES().equals(""))
					paramsNotifications.add(new BasicNameValuePair("shownEntries", NotificationSettingsData.getSHOW_ENTRIES()));

				// Make the request
				JSONObject json = jsonParser.makeHttpRequest(url_get_notification, "GET", paramsNotifications);

				int success = json.getInt(TAG_SUCCESS);

				// In case of success
				if(success == 1)
				{
					// Fetch the notifications
					notification = json.getJSONArray("notification");

					// Put the notifications with required attributes in a list
					for (int i = 0; i < notification.length(); i++)
					{
						JSONObject c = notification.getJSONObject(i);

						String id = c.getString("notificationsId");
						String message = c.getString("message");
						String isRead = c.getString("isRead");

						HashMap<String, String> map = new HashMap<String, String>();
						map.put(TAG_ID, id);
						map.put(TAG_MESSAGE, message);
						map.put(TAG_IS_READ, isRead);

						notificationList.add(map);
					}
				}
			}
			// In case of error
			catch(Exception e)
			{
				e.printStackTrace();
				// Logout the user
				logout();
			}

			return null;
		}

		protected void onPostExecute(String file_url)
		{
			// Hide the progress dialog
			pDialog.dismiss();
			
			// Prepare the data
			runOnUiThread(new Runnable()
			{
				// Put the data in an adapter
				public void run()
				{
					// Update listview
					final ListView notificationListView = (ListView) findViewById(android.R.id.list);

					/**
					 * Defines an own adapter
					 * 
					 * @author Oliver Neubauer
					 * @version ?
					 *
					 */
					
					class NotificationListAdapter extends BaseAdapter
					{
						// The list of notifications
						private ArrayList<HashMap<String, String>> notificationList;

						/**
						 * Sets the list of notifications
						 * 
						 * @param notificationList
						 */
						
						public NotificationListAdapter(ArrayList<HashMap<String, String>> notificationList)
						{
							this.notificationList = notificationList;
						}

						/**
						 * Required method
						 */
						
						public int getCount()
						{
							return notificationList.size();
						}

						/**
						 * Required method
						 */
						
						public Object getItem(int arg0)
						{
							return null;
						}

						/**
						 * Required method 
						 */
						
						public long getItemId(int position)
						{
							return position;
						}

						/**
						 * Changes the list items according the read status
						 * 
						 * @param position the position of the item in the list
						 * @param convertView the view to be converted
						 * @param parent the parent of the view
						 */
						
						@SuppressLint("ViewHolder")
						public View getView(int position, View convertView, ViewGroup parent)
						{
							// Fetch the layout
							LayoutInflater inflater = getLayoutInflater();
							
							View row;
							
							// Fetch the item
							row = inflater.inflate(R.layout.notification_item, parent, false);
							TextView message;
							
							// Fetch the message field within the item
							message = (TextView) row.findViewById(R.id.MESSAGE);
							
							// Set the text of message
							message.setText(notificationList.get(position).get(TAG_MESSAGE).toString());

							// In case of message is unread, make them bold
							if (notificationList.get(position).get(TAG_IS_READ).equals("0"))
								message.setTypeface(Typeface.DEFAULT_BOLD);

							return(row);
						}
					}

					// Set the adapter
					notificationListView.setAdapter(new NotificationListAdapter(notificationList));
					
					// Make the list clickable
					notificationListView.setOnItemClickListener(new AdapterView.OnItemClickListener()
					{
						@SuppressLint("NewApi")
						@Override
						public void onItemClick(AdapterView<?> parent, View view, int position, long id)
						{
							// Fetch the id of selected message
							final TextView message = (TextView) view.findViewById(R.id.MESSAGE);

							// If message is bold (unread), declare them as read and update read status
							if (message.getTypeface() != null && message.getTypeface().isBold())
							{
								message.setTypeface(Typeface.DEFAULT);
								
								// Update read status
								new NotificationReadStatusUpdater().execute(position);
							}

							// In case of message takes more than two lines, show a dialog with the message
							if (message.getLineCount() > 2)
							{
								// Build the dialog
								AlertDialog.Builder builder = new AlertDialog.Builder(NotificationController.this);
								
								// Define the title
								builder.setTitle(IMessages.SecurityIssue.NOTIFICATION);
								
								// Define a text view
								TextView tv = new TextView(NotificationController.this);
								
								// Set the text with the message
								tv.setText(message.getText().toString());
								builder.setView(tv);

								// Set a button
								builder.setNeutralButton(IMessages.DialogButton.OK, new DialogInterface.OnClickListener() {

									@Override
									public void onClick(DialogInterface dialog, int which) {
										return;
									}
								});
								
								// Create and show the dialog
								builder.create().show();
							}
						}

						class NotificationReadStatusUpdater extends AsyncTask<Integer, String, String>
						{
							@Override
							protected String doInBackground(Integer... arg)
							{
								// Required parameters for the request
								List<NameValuePair> params = new ArrayList<NameValuePair>();
								params.add(new BasicNameValuePair("notificationsId", notificationList.get(arg[0]).get(TAG_ID)));
								
								// Make the request
								jsonParser.makeHttpRequest(url_update_notification_read_status, "GET", params);

								return null;
							}
						}
					});
				}
			});
		}
	}
}