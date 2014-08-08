package fhkl.de.orgapp.controller.groups;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;
import fhkl.de.orgapp.R;
import fhkl.de.orgapp.util.IMessages;
import fhkl.de.orgapp.util.IUniformResourceLocator;
import fhkl.de.orgapp.util.JSONParser;
import fhkl.de.orgapp.util.MenuActivity;
import fhkl.de.orgapp.util.data.GroupData;
import fhkl.de.orgapp.util.data.UserData;

/**
 * ListInviteMemberController - Handles the list invite member activity.
 * 
 * Invites new group members via a ListView. Sends Notifications to other group
 * members.
 * 
 * @author Jochen Jung
 * @version 1.0
 */
public class ListInviteMemberController extends MenuActivity {

	Button inviteButton, cancelButton;
	View horizontalLine;
	TextView userInfo;
	private ProgressDialog pDialog;
	AlertDialog.Builder alertDialogBuilder;
	AlertDialog alertDialog;
	List<HashMap<String, Object>> personList;

	private static final String TAG_SUCCESS = "success";
	private static final String TAG_PERSON_ID = "PERSON_ID";
	private static final String TAG_TEXT_FIRST_NAME = "TEXT_FIRST_NAME";
	private static final String TAG_FIRST_NAME = "FIRST_NAME";
	private static final String TAG_TEXT_LAST_NAME = "TEXT_LAST_NAME";
	private static final String TAG_LAST_NAME = "LAST_NAME";
	private static final String TAG_TEXT_EMAIL = "TEXT_EMAIL";
	private static final String TAG_EMAIL = "EMAIL";
	private static final String TAG_IS_SELECTED = "IS_SELECTED";

	JSONArray persons = null;

	JSONParser jsonParser = new JSONParser();

	/**
	 * Calls async class that loads the member list.
	 * 
	 * @param savedInstanceState Bundle
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.invite_member_list);
		checkOnNewNotificationsAndNotifyUser();
		personList = new ArrayList<HashMap<String, Object>>();

		new PersonListGetter().execute();
	}

	/**
	 * Returns a warning in case no member selection was made. Calls async class
	 * that invites the selected users into the group if no errors present.
	 * 
	 * @param view View
	 */
	public void invitePersons(View view) {
		if (!isAtLeastOnePersonSelected()) {
			Toast.makeText(getApplicationContext(), IMessages.Error.NO_MEMBER_SELECTED, Toast.LENGTH_LONG).show();
			return;
		}

		new PersonListInvite().execute();
	}

	/**
	 * Returns to previous activity.
	 */
	public void backToSingleGroupView() {
		Intent intent = new Intent(ListInviteMemberController.this, SingleGroupController.class);
		startActivity(intent);
	}

	/**
	 * Validates member selection. At least one member has to be selected.
	 * 
	 * @return boolean AtLeastOneMemberSelected
	 */
	private boolean isAtLeastOnePersonSelected() {
		int p;

		for (p = 0; p < personList.size(); p++) {
			if (((Boolean) personList.get(p).get(TAG_IS_SELECTED)).booleanValue())
				return true;
		}

		return false;
	}

	/**
	 * 
	 * @author Jochen Jung
	 * @version 1.0
	 */
	class PersonListGetter extends AsyncTask<String, String, String> {

		/**
		 * Creates ProcessDialog
		 */
		@Override
		protected void onPreExecute() {
			super.onPreExecute();

			pDialog = new ProgressDialog(ListInviteMemberController.this);
			pDialog.setMessage(IMessages.Status.LOADING_LIST);
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(true);
			pDialog.show();
		}

		/**
		 * Gets user data.
		 * 
		 * @param args String...
		 * @return String result
		 */
		protected String doInBackground(String... args) {
			List<NameValuePair> params = new ArrayList<NameValuePair>();

			params.add(new BasicNameValuePair("do", "readInviteMemberList"));
			params.add(new BasicNameValuePair("groupId", GroupData.getGROUPID()));
			params.add(new BasicNameValuePair("personId", UserData.getPERSONID()));

			// Fetch all users, who are in the groups of the logged user, but not in
			// the selected group
			JSONObject json = jsonParser.makeHttpsRequest(IUniformResourceLocator.URL.URL_GROUPS, "GET", params,
							ListInviteMemberController.this);
			int success;

			try {
				success = json.getInt(TAG_SUCCESS);

				if (success == 1) {
					persons = json.getJSONArray("memberList");
					int p;
					JSONObject person;
					String personId, firstName, lastName, email;
					HashMap<String, Object> personMap;

					for (p = 0; p < persons.length(); p++) {
						person = persons.getJSONObject(p);

						personId = person.getString("personId");
						firstName = person.getString("firstName");
						lastName = person.getString("lastName");
						email = person.getString("eMail");

						// Load selected member into HashMap
						personMap = new HashMap<String, Object>();

						personMap.put(TAG_PERSON_ID, personId);
						personMap.put(TAG_TEXT_FIRST_NAME, getString(R.string.TEXT_FIRST_NAME) + ": ");
						personMap.put(TAG_FIRST_NAME, firstName);
						personMap.put(TAG_TEXT_LAST_NAME, getString(R.string.TEXT_LAST_NAME) + ": ");
						personMap.put(TAG_LAST_NAME, lastName);
						personMap.put(TAG_TEXT_EMAIL, getString(R.string.TEXT_EMAIL) + ": ");
						personMap.put(TAG_EMAIL, email);
						personMap.put(TAG_IS_SELECTED, false);

						// Add selected member to ArrayList
						personList.add(personMap);
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
				pDialog.dismiss();
				logout();
			}

			return null;
		}

		/**
		 * Removes ProcessDialog. Initializes and loads ListView. Updates chosen
		 * member list when ListItem is clicked.
		 * 
		 * @param message String
		 */
		protected void onPostExecute(String message) {
			super.onPostExecute(message);

			pDialog.dismiss();

			loadLayout();

			runOnUiThread(new Runnable() {
				public void run() {
					ListAdapter adapter = new SimpleAdapter(ListInviteMemberController.this, personList,
									R.layout.invite_member_list_item, new String[] { TAG_TEXT_FIRST_NAME, TAG_FIRST_NAME,
													TAG_TEXT_LAST_NAME, TAG_LAST_NAME, TAG_TEXT_EMAIL, TAG_EMAIL }, new int[] {
													R.id.INVITE_MEMBER_LIST_TEXT_FIRST_NAME, R.id.INVITE_MEMBER_LIST_USER_FIRST_NAME,
													R.id.INVITE_MEMBER_LIST_TEXT_LAST_NAME, R.id.INVITE_MEMBER_LIST_USER_LAST_NAME,
													R.id.INVITE_MEMBER_LIST_TEXT_EMAIL, R.id.INVITE_MEMBER_LIST_USER_EMAIL });

					final ListView personViewList = (ListView) findViewById(android.R.id.list);

					personViewList.setAdapter(adapter);

					personViewList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
						@Override
						public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
							ImageView image = (ImageView) view.findViewById(R.id.INVITE_MEMBER_LIST_IMAGE_ACCEPT);
							HashMap<String, Object> selectedPerson = getSelectedPerson((TextView) view
											.findViewById(R.id.INVITE_MEMBER_LIST_USER_EMAIL));

							// Unselect member
							if (image.getVisibility() == View.VISIBLE) {
								image.setVisibility(View.INVISIBLE);

								selectedPerson.put(TAG_IS_SELECTED, false);
								personList.set(personList.indexOf(selectedPerson), selectedPerson);
							}
							// Select member
							else if (image.getVisibility() == View.INVISIBLE) {
								image.setVisibility(View.VISIBLE);

								selectedPerson.put(TAG_IS_SELECTED, true);
								personList.set(personList.indexOf(selectedPerson), selectedPerson);
							}
						}

						private HashMap<String, Object> getSelectedPerson(TextView emailOfSelectedPerson) {
							HashMap<String, Object> selectedPerson = new HashMap<String, Object>();
							int p;

							for (p = 0; p < personList.size(); p++)
								if (personList.get(p).get(TAG_EMAIL).equals(emailOfSelectedPerson.getText().toString()))
									selectedPerson = personList.get(p);

							return selectedPerson;
						}
					});
				}
			});
		}

		/**
		 * Initializes, loads and formats the view.
		 */
		private void loadLayout() {
			getViews();
			setTexts();
			setBackgrounds();
		}

		/**
		 * Initializes the view
		 */
		private void getViews() {
			inviteButton = (Button) findViewById(R.id.INVITE_MEMBER_LIST_INVITE_BUTTON);
			cancelButton = (Button) findViewById(R.id.INVITE_MEMBER_LIST_CANCEL_BUTTON);
			userInfo = (TextView) findViewById(R.id.INVITE_MEMBER_LIST_USER_INFO);
			horizontalLine = findViewById(R.id.INVITE_MEMBER_LIST_HORIZONTAL_LINE);
		}

		/**
		 * Loads values to fill view.
		 */
		private void setTexts() {
			inviteButton.setText(getString(R.string.LIST_INVITE));
			cancelButton.setText(getString(R.string.LIST_CANCEL));
			userInfo.setText(getString(R.string.INVITE_MEMBER_LIST_USER_INFO));
		}

		/**
		 * Sets background color for the view.
		 */
		private void setBackgrounds() {
			inviteButton.setBackgroundColor(getResources().getColor(android.R.color.transparent));
			cancelButton.setBackgroundColor(getResources().getColor(android.R.color.transparent));
			horizontalLine.setBackgroundColor(getResources().getColor(android.R.color.black));
		}
	}

	/**
	 * 
	 * 
	 * @author Jochen Jung
	 * @version 1.0
	 */
	class PersonListInvite extends AsyncTask<String, String, String> {

		/**
		 * Creates ProcessDialog
		 */
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pDialog = new ProgressDialog(ListInviteMemberController.this);
			pDialog.setMessage(IMessages.Status.INVITING_MEMBERS);
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(true);
			pDialog.show();
		}

		/**
		 * Invites selected persons into group. Sends Notification to selected
		 * persons.
		 * 
		 * @param params String...
		 * @return String result
		 */
		protected String doInBackground(String... params) {
			JSONObject json;
			List<NameValuePair> paramsInvite, paramsNotification;
			String notification;
			DateFormat dateFormat;
			Date date;
			int p, success;

			for (p = 0; p < personList.size(); p++) {
				if (((Boolean) personList.get(p).get(TAG_IS_SELECTED)).booleanValue()) {
					// invite person to group
					paramsInvite = new ArrayList<NameValuePair>();
					dateFormat = new SimpleDateFormat("yyyy/MM/dd", Locale.GERMANY);
					date = new Date();

					// Required parameters
					paramsInvite.add(new BasicNameValuePair("do", "createPrivilegeMember"));
					paramsInvite.add(new BasicNameValuePair("groupId", GroupData.getGROUPID()));
					paramsInvite.add(new BasicNameValuePair("memberSince", dateFormat.format(date).toString()));
					paramsInvite.add(new BasicNameValuePair("personId", personList.get(p).get(TAG_PERSON_ID).toString()));

					// Create new member in group
					json = jsonParser.makeHttpsRequest(IUniformResourceLocator.URL.URL_PRIVILEGE, "GET", paramsInvite,
									ListInviteMemberController.this);

					try {
						success = json.getInt(TAG_SUCCESS);

						if (success != 1)
							return null;

						paramsNotification = new ArrayList<NameValuePair>();
						paramsNotification.add(new BasicNameValuePair("do", "create"));
						notification = IMessages.Notification.MESSAGE_INVITE + GroupData.getGROUPNAME();
						paramsNotification.add(new BasicNameValuePair("eMail", personList.get(p).get(TAG_EMAIL).toString()));
						paramsNotification.add(new BasicNameValuePair("message", notification));
						paramsNotification.add(new BasicNameValuePair("classification", "1"));
						paramsNotification.add(new BasicNameValuePair("syncInterval", null));
						// Send notification
						json = jsonParser.makeHttpsRequest(IUniformResourceLocator.URL.URL_NOTIFICATION, "GET", paramsNotification,
										ListInviteMemberController.this);

						success = json.getInt(TAG_SUCCESS);

						if (success != 1)
							return null;
					} catch (Exception e) {
						e.printStackTrace();
						pDialog.dismiss();
						logout();
					}
				}
			}

			return "Successful";
		}

		/**
		 * Removes ProcessDialog. Calls method that opens a AlertDialog.
		 * 
		 * @param message String
		 */
		protected void onPostExecute(String message) {
			pDialog.dismiss();
			if (message != null)
				showInvertedPersonsDialog();
		}

		/**
		 * Creates AlertDialog and returns every new invited member.
		 */
		private void showInvertedPersonsDialog() {
			alertDialogBuilder = new AlertDialog.Builder(ListInviteMemberController.this);
			alertDialogBuilder.setTitle(IMessages.Status.MESSAGE_INVITED_PERSON);
			CharSequence[] items = getSelectedPersonsAsArray();
			alertDialogBuilder.setItems(items, null);

			alertDialogBuilder.setPositiveButton(IMessages.DialogButton.OK, new OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					backToSingleGroupView();
				}
			});

			alertDialog = alertDialogBuilder.create();

			alertDialog.show();
		}

		/**
		 * Converts the selected person from HaspMap to CharSequence Array
		 * 
		 * @return CharSequence[] persons
		 */
		private CharSequence[] getSelectedPersonsAsArray() {
			int p;
			List<String> selectedPersons = new ArrayList<String>();
			String person;
			CharSequence[] result;

			for (p = 0; p < personList.size(); p++) {
				if (((Boolean) personList.get(p).get(TAG_IS_SELECTED)).booleanValue()) {
					person = getString(R.string.FIRSTNAME) + ": " + personList.get(p).get(TAG_FIRST_NAME).toString() + "\n"
									+ getString(R.string.LASTNAME) + ": " + personList.get(p).get(TAG_LAST_NAME).toString() + "\n"
									+ getString(R.string.EMAIL) + ": " + personList.get(p).get(TAG_EMAIL).toString();

					selectedPersons.add(person);
				}
			}

			result = new CharSequence[selectedPersons.size()];

			for (p = 0; p < result.length; p++)
				result[p] = selectedPersons.get(p);

			return result;
		}
	}
}