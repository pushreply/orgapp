package fhkl.de.orgapp.controller.groups;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.InputType;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.GridLayout.LayoutParams;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;
import fhkl.de.orgapp.R;
import fhkl.de.orgapp.controller.calendar.CalendarController;
import fhkl.de.orgapp.controller.notification.NotificationController;
import fhkl.de.orgapp.controller.profile.ProfileController;
import fhkl.de.orgapp.controller.start.StartController;
import fhkl.de.orgapp.util.IMessages;
import fhkl.de.orgapp.util.JSONParser;

public class ManualInviteMemberController extends Activity {

	private ProgressDialog pDialog;

	JSONParser jsonParser = new JSONParser();
	ArrayList<HashMap<String, String>> groupList;

	private static String URL_EXIST_USER = "http://pushrply.com/select_person_by_email.php";
	private static String URL_USER_INVITED = "http://pushrply.com/get_user_in_group_by_eMail.php";
	private static String URL_INVITE_PERSON = "http://pushrply.com/create_user_in_group_by_eMail.php";
	private static String URL_SEND_NOTIFICATIONS = "http://pushrply.com/create_notification.php";

	private static final String TAG_SUCCESS = "success";

	LinearLayout containerLayout;
	GridLayout textLayout;
	private String personIdLoggedPerson;

	private Button bInvite;
	private Button bCancel;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.invite_member_manual);

		getIntent().putExtra("cnt", "0");

		personIdLoggedPerson = getIntent().getStringExtra("UserId");
		containerLayout = (LinearLayout) findViewById(R.id.LinearLayout);
		textLayout = new GridLayout(ManualInviteMemberController.this);
		LayoutParams params = new LayoutParams();
		params.width = LayoutParams.MATCH_PARENT;
		params.height = LayoutParams.WRAP_CONTENT;
		textLayout.setLayoutParams(params);
		textLayout.setOrientation(GridLayout.HORIZONTAL);
		textLayout.setColumnCount(4);

		containerLayout.addView(textLayout);
		bInvite = (Button) findViewById(R.id.INVITE);
		bCancel = (Button) findViewById(R.id.CANCEL);

		bInvite.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				new InviteMembers().execute();
			}
		});

		bCancel.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				Intent intent = new Intent(ManualInviteMemberController.this,
						SingleGroupController.class);
				intent.putExtra("UserId", personIdLoggedPerson);
				intent.putExtra("GroupId", getIntent().getStringExtra("GroupId"));
				intent.putExtra("GroupName", getIntent().getStringExtra("GroupName"));
				startActivity(intent);
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.main_menu, menu);
		menu.findItem(R.id.ADD_EMAIL_FIELD).setVisible(true);
		menu.findItem(R.id.CALENDAR).setVisible(true);
		menu.findItem(R.id.GROUPS).setVisible(true);
		menu.findItem(R.id.NOTIFICATIONS).setVisible(true);
		menu.findItem(R.id.PROFILE).setVisible(true);
		menu.findItem(R.id.LOGOUT).setVisible(true);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Intent intent;

		switch (item.getItemId()) {
		case R.id.CALENDAR:
			intent = new Intent(ManualInviteMemberController.this,
					CalendarController.class);
			intent.putExtra("UserId", personIdLoggedPerson);
			startActivity(intent);
			return true;
		case R.id.GROUPS:
			intent = new Intent(ManualInviteMemberController.this,
					GroupsController.class);
			intent.putExtra("UserId", personIdLoggedPerson);
			startActivity(intent);
			return true;
		case R.id.NOTIFICATIONS:
			intent = new Intent(ManualInviteMemberController.this,
					NotificationController.class);
			intent.putExtra("UserId", personIdLoggedPerson);
			startActivity(intent);
			return true;
		case R.id.PROFILE:
			intent = new Intent(ManualInviteMemberController.this,
					ProfileController.class);
			intent.putExtra("UserId", personIdLoggedPerson);
			startActivity(intent);
			return true;
		case R.id.LOGOUT:
			intent = new Intent(ManualInviteMemberController.this,
					StartController.class);
			intent.putExtra("UserId", personIdLoggedPerson);
			startActivity(intent);
			return true;
		case R.id.ADD_EMAIL_FIELD:
			Integer tmpCnt = Integer.valueOf(getIntent().getStringExtra("cnt")
					.toString());

			EditText editText = new EditText(ManualInviteMemberController.this);
			editText.setId(tmpCnt);
			editText.setInputType(InputType.TYPE_CLASS_TEXT
					| InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
			editText.setHint(R.string.EMAIL);

			tmpCnt++;

			ImageButton imageButton = new ImageButton(
					ManualInviteMemberController.this);
			imageButton.setId(tmpCnt);
			imageButton.setImageResource(R.drawable.ic_action_remove);

			tmpCnt++;
			getIntent().putExtra("cnt", tmpCnt.toString());

			textLayout.addView(editText);
			LayoutParams layoutParams = (GridLayout.LayoutParams) editText
					.getLayoutParams();
			layoutParams.columnSpec = GridLayout.spec(0, 3);
			layoutParams.setGravity(Gravity.FILL);
			editText.setLayoutParams(layoutParams);

			textLayout.addView(imageButton);
			layoutParams = (GridLayout.LayoutParams) imageButton.getLayoutParams();
			layoutParams.columnSpec = GridLayout.spec(3);
			layoutParams.width = LayoutParams.WRAP_CONTENT;
			layoutParams.height = LayoutParams.WRAP_CONTENT;
			imageButton.setLayoutParams(layoutParams);

			imageButton.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					textLayout.removeView(findViewById(v.getId() - 1));
					textLayout.removeView(v);
				}
			});
			return true;
		default:
			return true;
		}
	}

	class InviteMembers extends AsyncTask<String, String, String> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pDialog = new ProgressDialog(ManualInviteMemberController.this);

			pDialog.setMessage(IMessages.INVITING_MEMBER);

			pDialog.setIndeterminate(false);
			pDialog.setCancelable(false);
			pDialog.show();
		}

		protected String doInBackground(String... params) {
			int editTextLength = textLayout.getChildCount();
			if (editTextLength == 0) {
				return IMessages.MISSING_EMAIL;
			}
			String[] editTextArray = new String[editTextLength / 2];
			for (int i = 0; i < editTextLength; i++) {
				if (i % 2 == 0) {
					EditText tmp = (EditText) textLayout.getChildAt(i);
					if (i != 0) {
						System.out.println("I ist:" + i);
						System.out.println(tmp.getText().toString());

						if (Arrays.asList(editTextArray).contains(tmp.getText().toString())) {
							// Duplicate Input
							System.out.println("duplicate");
							return IMessages.DUPLICATE_EMAIL;
						} else {
							editTextArray[i / 2] = tmp.getText().toString();
						}
					} else {

						editTextArray[i / 2] = tmp.getText().toString();
					}
				}
			}
			System.out.println("check 1 done");
			for (int i = 0; i < editTextArray.length; i++) {
				if (!editTextArray[i]
						.matches("[A-Z0-9a-z._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,4}")) {
					// Wrong Email format
					return IMessages.INVALID_EMAIL;
				}
			}
			System.out.println("check 2 done");
			for (int i = 0; i < editTextArray.length; i++) {
				List<NameValuePair> paramsCheck = new ArrayList<NameValuePair>();
				paramsCheck.add(new BasicNameValuePair("eMail", editTextArray[i]));
				JSONObject json = jsonParser.makeHttpRequest(URL_EXIST_USER, "GET",
						paramsCheck);
				int success;
				try {
					success = json.getInt(TAG_SUCCESS);
					if (success == 0) {
						// User is not registered
						return IMessages.EXIST_USER;
					}
				} catch (JSONException e) {
					System.out
							.println("Error in InviteMembers.doInBackground(String... arg0): "
									+ e.getMessage());
					e.printStackTrace();
				}
			}
			System.out.println("check 3 done");
			for (int i = 0; i < editTextArray.length; i++) {
				List<NameValuePair> paramsCheck = new ArrayList<NameValuePair>();
				paramsCheck.add(new BasicNameValuePair("groupId", getIntent()
						.getStringExtra("GroupId")));
				paramsCheck.add(new BasicNameValuePair("eMail", editTextArray[i]));
				JSONObject json = jsonParser.makeHttpRequest(URL_USER_INVITED, "GET",
						paramsCheck);
				int success;
				try {
					success = json.getInt(TAG_SUCCESS);
					if (success == 1) {
						// User already invited
						return IMessages.USER_INVITED;
					}
				} catch (JSONException e) {
					System.out
							.println("Error in InviteMembers.doInBackground(String... arg0): "
									+ e.getMessage());
					e.printStackTrace();
				}
			}
			System.out.println("check 4 done");
			// Everything okay
			List<NameValuePair> paramsInvite = new ArrayList<NameValuePair>();
			paramsInvite.add(new BasicNameValuePair("groupId", getIntent()
					.getStringExtra("GroupId")));
			DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
			Date date = new Date();
			paramsInvite.add(new BasicNameValuePair("memberSince", dateFormat.format(
					date).toString()));
			for (int i = 0; i < editTextArray.length; i++) {
				paramsInvite.add(new BasicNameValuePair("eMail", editTextArray[i]));
				JSONObject json = jsonParser.makeHttpRequest(URL_INVITE_PERSON, "GET",
						paramsInvite);
				int success;
				try {
					success = json.getInt(TAG_SUCCESS);
					if (success == 1) {
						// Send Notifications
						List<NameValuePair> paramsNotification = new ArrayList<NameValuePair>();
						paramsNotification.add(new BasicNameValuePair("eMail",
								editTextArray[i]));
						paramsNotification
								.add(new BasicNameValuePair("classification", "0"));
						String message = IMessages.MESSAGE_INVITE
								+ getIntent().getStringExtra("GroupName");
						paramsNotification.add(new BasicNameValuePair("message", message));
						paramsNotification
								.add(new BasicNameValuePair("syncInterval", null));
						json = jsonParser.makeHttpRequest(URL_SEND_NOTIFICATIONS, "GET",
								paramsNotification);
						json.getInt(TAG_SUCCESS);
						if (success != 1) {
							// unknown error
						}
					} else {
						// unknown error
					}
				} catch (JSONException e) {
					System.out
							.println("Error in InviteMembers.doInBackground(String... arg0): "
									+ e.getMessage());
					e.printStackTrace();
				}
			}
			Intent intent = new Intent(ManualInviteMemberController.this,
					SingleGroupController.class);
			intent.putExtra("UserId", personIdLoggedPerson);
			intent.putExtra("GroupId", getIntent().getStringExtra("GroupId"));
			intent.putExtra("GroupName", getIntent().getStringExtra("GroupName"));
			startActivity(intent);
			startActivity(intent);
			return null;
		}

		protected void onPostExecute(String message) {
			pDialog.dismiss();
			if (message != null) {
				Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG)
						.show();

			}
		}
	}
}
