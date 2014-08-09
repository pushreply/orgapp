package fhkl.de.orgapp.controller.event;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import fhkl.de.orgapp.R;
import fhkl.de.orgapp.controller.groups.SingleGroupController;
import fhkl.de.orgapp.util.IMessages;
import fhkl.de.orgapp.util.IUniformResourceLocator;
import fhkl.de.orgapp.util.JSONParser;
import fhkl.de.orgapp.util.MenuActivity;
import fhkl.de.orgapp.util.data.GroupData;
import fhkl.de.orgapp.util.data.UserData;
import fhkl.de.orgapp.util.validator.InputValidator;

/**
 * CrateEventController - Handle each event instantiation in a group.
 * All event is created and started from here. 
 * 
 * @author Ronaldo Hasiholan, Oliver Neubauer, Jochen Jung
 * @version 4.0
 */
public class CreateEventController extends MenuActivity {

	// Android progress dialog
	private ProgressDialog pDialog;

	// Json parser and json array container
	JSONParser jsonParser = new JSONParser();
	JSONArray member;

	// Marker tag to sent from server to client app
	// to inform whether the request is completed or failed.
	private static final String TAG_SUCCESS = "success";

	// Preparing UI instances
	Calendar calendar;
	EditText name, eventLocation, eventDate, eventTime, regularityChosen;
	TextView regularityQuestion;
	CheckBox regularityDate;
	Spinner regularityDateChosen;
	RadioGroup radioGroupRegularity;
	RadioButton radioButtonRegularityDate, radioButtonRegularityNumber;

	private Button bSave, bCancel;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.new_edit_event);

		// Notify user(s) with specific privileges.
		checkOnNewNotificationsAndNotifyUser();

		// Prepare a java calendar instance
		calendar = Calendar.getInstance();

		// Bind the Android UI instances to the UI XML layout.
		name = (EditText) findViewById(R.id.EVENTNAME);
		eventLocation = (EditText) findViewById(R.id.EVENTLOCATION);
		eventDate = (EditText) findViewById(R.id.EVENTDATE);

		// Make the "eventDate" field clickable to call a date picker dialog
		eventDate.setOnClickListener(new OnClickListener() {

			// A date picker dialog
			@Override
			public void onClick(View v) {
				new DatePickerDialog(CreateEventController.this, dateEvent, calendar.get(Calendar.YEAR), calendar
								.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
			}
		});

		eventTime = (EditText) findViewById(R.id.EVENTTIME);

		// Make the time field clickable to call the time picker dialog
		eventTime.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				new TimePickerDialog(CreateEventController.this, timeEvent, calendar.get(Calendar.HOUR_OF_DAY), calendar
								.get(Calendar.MINUTE), true).show();
			}
		});

		// Regularity variables to let users create reoccuring events.
		regularityDate = (CheckBox) findViewById(R.id.REGULARITYDATE);
		regularityDateChosen = (Spinner) findViewById(R.id.REGULARITYDATE_CHOSEN);

		regularityQuestion = (TextView) findViewById(R.id.REGULARITY_QUESTION);
		regularityChosen = (EditText) findViewById(R.id.REGULARITY_CHOSEN);

		radioGroupRegularity = (RadioGroup) findViewById(R.id.RADIOGROUP_REGULARITY);

		// Let the user selects whether the reoccurence limited to a date or a
		// specific quantity (max 50 occurence).
		radioButtonRegularityDate = (RadioButton) findViewById(R.id.REGULARITY_DATE);
		radioButtonRegularityNumber = (RadioButton) findViewById(R.id.REGULARITY_NUMBER);

		// Save-Cancel Button
		bSave = (Button) findViewById(R.id.SAVE);
		bCancel = (Button) findViewById(R.id.CANCEL);

		// If the checkbox for reoccurence event is thicked, show more options.
		regularityDate.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

				if (isChecked) {
					// Show the frequent dropdown spinner
					regularityDateChosen.setVisibility(View.VISIBLE);
					ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(CreateEventController.this,
									R.array.SPINNER_REGULARITYDATE, android.R.layout.simple_spinner_item);

					// Set the array adapter using the selected value from the the
					// dropdown spinner.
					adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
					regularityDateChosen.setOnItemSelectedListener(new OnItemSelectedListener() {
						@Override
						public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

							// Hide elements
							if (parent.getSelectedItem().toString().equals("empty")) {
								regularityQuestion.setVisibility(View.GONE);
								radioGroupRegularity.setVisibility(View.GONE);
								radioButtonRegularityDate.setChecked(false);
								radioButtonRegularityNumber.setChecked(false);
								regularityChosen.setVisibility(View.GONE);
							} else {
								regularityQuestion.setVisibility(View.VISIBLE);
								radioGroupRegularity.setVisibility(View.VISIBLE);
							}
						}

						@Override
						public void onNothingSelected(AdapterView<?> parent) {
						}
					});

					radioGroupRegularity.setOnCheckedChangeListener(new OnCheckedChangeListener() {

						@Override
						public void onCheckedChanged(RadioGroup group, int checkedId) {
							regularityChosen.setVisibility(View.VISIBLE);
							if (checkedId == R.id.REGULARITY_DATE) {
								regularityChosen.setText("");
								regularityChosen.setHint(R.string.REGULARITY_DATE);
								regularityChosen.setFocusableInTouchMode(false);
								regularityChosen.setOnClickListener(new OnClickListener() {

									@Override
									public void onClick(View v) {
										new DatePickerDialog(CreateEventController.this, dateRegularityChosen, calendar.get(Calendar.YEAR),
														calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
									}
								});
							} else {
								regularityChosen.setText("");
								regularityChosen.setHint(R.string.REGULARITY_NUMBER);
								regularityChosen.setFocusableInTouchMode(true);
								regularityChosen.setInputType(InputType.TYPE_CLASS_NUMBER);
								regularityChosen.setOnClickListener(new OnClickListener() {

									@Override
									public void onClick(View v) {
									}
								});
							}
						}
					});

					regularityDateChosen.setAdapter(adapter);
				} else {
					// CheckBox unchecked
					regularityDateChosen.setVisibility(View.GONE);
					regularityQuestion.setVisibility(View.GONE);
					radioGroupRegularity.setVisibility(View.GONE);
					radioButtonRegularityDate.setChecked(false);
					radioButtonRegularityNumber.setChecked(false);
					regularityChosen.setVisibility(View.GONE);

				}
			}
		});

		// Set actions to the Save - Cancel Button
		bSave.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				new SaveEvent().execute();
			}
		});

		bCancel.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent i = new Intent(CreateEventController.this, SingleGroupController.class);
				startActivity(i);
			}
		});
	}

	// Background task for save button
	class SaveEvent extends AsyncTask<String, String, String> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pDialog = new ProgressDialog(CreateEventController.this);
			pDialog.setMessage(IMessages.Status.SAVING_EVENT);
			pDialog.setIndeterminate(false);
			// If the progress is taking too long, let user cancels
			// the progress dialog by hitting the android-back-button.
			pDialog.setCancelable(true);
			pDialog.show();
		}

		protected String doInBackground(String... args) {
			List<NameValuePair> params = new ArrayList<NameValuePair>();

			// Doing some input validation. The expected input are maximal 255 chars
			// of string type.
			if (!InputValidator.isStringLengthInRange(name.getText().toString(), 0, 255)) {
				return IMessages.Error.INVALID_NAME;
			} else {
				params.add(new BasicNameValuePair("name", name.getText().toString()));
			}

			if (!InputValidator.isStringLengthInRange(eventLocation.getText().toString(), 0, 255)) {
				return IMessages.Error.INVALID_EVENTLOCATION;
			} else {
				params.add(new BasicNameValuePair("eventLocation", eventLocation.getText().toString()));
			}

			// Time must be selected.
			if (eventTime.getText().toString().isEmpty()) {
				return IMessages.Error.INVALID_EVENTTIME;
			} else {
				params.add(new BasicNameValuePair("eventTime", eventTime.getText().toString()));
			}

			// CheckBox clicked?
			if (regularityDate.isChecked()) {
				// Date RadioButton chosen?
				if (radioGroupRegularity.getCheckedRadioButtonId() == R.id.REGULARITY_DATE) {
					// Date chosen?
					if (regularityChosen.getText().toString().isEmpty()) {
						return IMessages.Error.INVALID_REGULARITY_DATE;
					} else {
						// Date > Current Date?
						SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.GERMAN);
						sdf.setLenient(false);

						Date chosenDate = null;
						Date chosenEventDate = null;
						try {
							chosenDate = sdf.parse(regularityChosen.getText().toString());
							chosenEventDate = sdf.parse(eventDate.getText().toString());
							Date currentDate = new Date();
							sdf.format(currentDate);
							if (chosenDate.before(currentDate)) {
								return IMessages.Error.INVALID_REGULARITY_DATE;
							}
							if (chosenDate.before(chosenEventDate)) {
								return IMessages.Error.INVALID_REGULARITY_DATE_2;
							}
						} catch (ParseException e) {
							pDialog.dismiss();
							logout();
						}

						Calendar tmp = Calendar.getInstance();
						tmp.setTime(chosenEventDate);
						Calendar tmp2 = Calendar.getInstance();
						tmp2.setTime(chosenDate);
						if (regularityDateChosen.getSelectedItem().toString().equals("daily")) {
							tmp.add(Calendar.DATE, 1);
							if (tmp.after(tmp2)) {
								return IMessages.Error.INVALID_REGULARITY_DATE_3;
							}
						} else if (regularityDateChosen.getSelectedItem().toString().equals("weekly")) {
							tmp.add(Calendar.DATE, 7);
							if (tmp.after(tmp2)) {
								return IMessages.Error.INVALID_REGULARITY_DATE_3;
							}
						} else if (regularityDateChosen.getSelectedItem().toString().equals("every 2 weeks")) {
							tmp.add(Calendar.DATE, 14);
							if (tmp.after(tmp2)) {
								return IMessages.Error.INVALID_REGULARITY_DATE_3;
							}
						} else if (regularityDateChosen.getSelectedItem().toString().equals("monthly")) {
							tmp.add(Calendar.DATE, 28);
							if (tmp.after(tmp2)) {
								return IMessages.Error.INVALID_REGULARITY_DATE_3;
							}
						}
					}
					// Number RadioButton chosen?
				} else if (radioGroupRegularity.getCheckedRadioButtonId() == R.id.REGULARITY_NUMBER) {
					// Date chosen?
					if (regularityChosen.getText().toString().isEmpty()) {
						return IMessages.Error.INVALID_REGULARITY_NUMBER;
					} else {
						if (!InputValidator.isStringLengthInRange(regularityChosen.getText().toString(), 0, 2)) {
							return IMessages.Error.INVALID_REGULARITY_NUMBER;
						}
						try {
							Integer chosenNumber = Integer.parseInt(regularityChosen.getText().toString());
							if (chosenNumber > 50 || chosenNumber < 2) {
								return IMessages.Error.INVALID_REGULARITY_NUMBER;
							}
						} catch (NumberFormatException e) {
							pDialog.dismiss();
							logout();
						}
					}
					// No RadioButton checked
				} else {
					return IMessages.Error.INVALID_RADIOGROUP_REGULARITY;
				}
			}

			params.add(new BasicNameValuePair("personId", UserData.getPERSONID()));
			params.add(new BasicNameValuePair("groupId", GroupData.getGROUPID()));

			JSONObject json = null;
			Date notificationDate = null;
			SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd", Locale.GERMAN);
			sdfDate.setLenient(false);

			// Recurring events
			if (regularityDate.isChecked()) {
				Date chosenDate = null;
				List<String> dateList = new ArrayList<String>();

				try {
					chosenDate = sdfDate.parse(eventDate.getText().toString());
				} catch (ParseException e) {
					pDialog.dismiss();
					logout();
				}

				if (radioGroupRegularity.getCheckedRadioButtonId() == R.id.REGULARITY_DATE) {
					Date chosenRegularityDate = null;
					try {
						chosenRegularityDate = sdfDate.parse(regularityChosen.getText().toString());
					} catch (ParseException e) {
						pDialog.dismiss();
						logout();
					}
					int cnt = 0;
					Calendar tmp = Calendar.getInstance();
					Calendar tmpRegularityDate = Calendar.getInstance();
					tmp.setTime(chosenDate);
					tmpRegularityDate.setTime(chosenRegularityDate);
					while (tmp.before(tmpRegularityDate) || tmp.equals(tmpRegularityDate)) {
						if (cnt == 50) {
							break;
						}
						Calendar tmp2 = (Calendar) tmp.clone();
						dateList.add(sdfDate.format(tmp2.getTime()));
						if (regularityDateChosen.getSelectedItem().toString().equals("daily")) {
							tmp.add(Calendar.DATE, 1);
						} else if (regularityDateChosen.getSelectedItem().toString().equals("weekly")) {
							tmp.add(Calendar.DATE, 7);
						} else if (regularityDateChosen.getSelectedItem().toString().equals("every 2 weeks")) {
							tmp.add(Calendar.DATE, 14);
						} else if (regularityDateChosen.getSelectedItem().toString().equals("monthly")) {
							tmp.add(Calendar.DATE, 28);
						}
						notificationDate = tmp2.getTime();
						cnt++;
					}

				} else if (radioGroupRegularity.getCheckedRadioButtonId() == R.id.REGULARITY_NUMBER) {
					Integer chosenNumber = Integer.parseInt(regularityChosen.getText().toString());
					Calendar tmp = Calendar.getInstance();
					tmp.setTime(chosenDate);
					for (int i = 0; i < chosenNumber; i++) {
						Calendar tmp2 = (Calendar) tmp.clone();
						dateList.add(sdfDate.format(tmp2.getTime()));
						if (regularityDateChosen.getSelectedItem().toString().equals("daily")) {
							tmp.add(Calendar.DATE, 1);
						} else if (regularityDateChosen.getSelectedItem().toString().equals("weekly")) {
							tmp.add(Calendar.DATE, 7);
						} else if (regularityDateChosen.getSelectedItem().toString().equals("every 2 weeks")) {
							tmp.add(Calendar.DATE, 14);
						} else if (regularityDateChosen.getSelectedItem().toString().equals("monthly")) {
							tmp.add(Calendar.DATE, 28);
						}
						notificationDate = tmp2.getTime();
					}
				}

				params.add(new BasicNameValuePair("do", "createEvent"));
				Iterator<String> dateListIterator = dateList.iterator();
				for (int i = 0; i < dateList.size(); i++) {
					String tmpDateList = dateListIterator.next();
					params.add(new BasicNameValuePair("eventDate", tmpDateList));
					json = new JSONParser().makeHttpsRequest(IUniformResourceLocator.URL.URL_EVENT, "GET", params,
									CreateEventController.this);
				}
			}
			// Non-recurring events
			else {
				if (eventDate.getText().toString().isEmpty()) {
					return IMessages.Error.INVALID_EVENTDATE;
				} else {
					SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.GERMAN);
					sdf.setLenient(false);

					try {
						Date chosenDate = sdf.parse(eventDate.getText().toString());
						Date currentDate = new Date();
						sdf.format(currentDate);
						if (chosenDate.before(currentDate)) {
							return IMessages.Error.INVALID_EVENTDATE;
						}
					} catch (ParseException e) {
						pDialog.dismiss();
						logout();
					}
					params.add(new BasicNameValuePair("eventDate", eventDate.getText().toString()));
				}

				params.add(new BasicNameValuePair("do", "createEvent"));
				json = new JSONParser().makeHttpsRequest(IUniformResourceLocator.URL.URL_EVENT, "GET", params,
								CreateEventController.this);
			}
			try {
				int success = json.getInt(TAG_SUCCESS);

				if (success != 0) {
					List<NameValuePair> paramsGetMemberList = new ArrayList<NameValuePair>();
					paramsGetMemberList.add(new BasicNameValuePair("do", "readAllUserInGroup"));
					paramsGetMemberList.add(new BasicNameValuePair("personId", UserData.getPERSONID()));
					paramsGetMemberList.add(new BasicNameValuePair("groupId", GroupData.getGROUPID()));
					json = new JSONParser().makeHttpsRequest(IUniformResourceLocator.URL.URL_GROUPS, "GET", paramsGetMemberList,
									CreateEventController.this);

					success = json.getInt(TAG_SUCCESS);
					if (success == 1) {

						member = json.getJSONArray("member");

						for (int i = 0; i < member.length(); i++) {
							JSONObject c = member.getJSONObject(i);

							List<NameValuePair> paramsCreateNotification = new ArrayList<NameValuePair>();
							paramsCreateNotification.add(new BasicNameValuePair("do", "create"));
							paramsCreateNotification.add(new BasicNameValuePair("eMail", c.getString("eMail")));
							paramsCreateNotification.add(new BasicNameValuePair("classification", "4"));

							if (!regularityDate.isChecked()) {
								paramsCreateNotification.add(new BasicNameValuePair("message",
												IMessages.Notification.MESSAGE_CREATE_EVENT_1 + GroupData.getGROUPNAME()
																+ IMessages.Notification.MESSAGE_CREATE_EVENT_2 + name.getText().toString()));
							} else {
								paramsCreateNotification.add(new BasicNameValuePair("message",
												IMessages.Notification.MESSAGE_CREATE_EVENT_1 + GroupData.getGROUPNAME()
																+ IMessages.Notification.MESSAGE_CREATE_EVENT_3 + name.getText().toString()
																+ IMessages.Notification.MESSAGE_CREATE_EVENT_4 + notificationDate.toString()));
							}

							json = jsonParser.makeHttpsRequest(IUniformResourceLocator.URL.URL_NOTIFICATION, "GET",
											paramsCreateNotification, CreateEventController.this);
						}
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
				pDialog.dismiss();
				logout();
			}

			return null;
		}

		protected void onPostExecute(String message) {
			pDialog.dismiss();

			// Prepare a dialog to share the event to social network
			if (message != null) {
				Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
			} else {
				showDialogAndGoToSingleGroupController();
			}
		}
		
		// Dialog to let user share an event to social network
		private void showDialogAndGoToSingleGroupController() {
			AlertDialog.Builder builder = new AlertDialog.Builder(CreateEventController.this);

			builder.setMessage(IMessages.SecurityIssue.SHARE_CREATED_EVENT);
			builder.setNeutralButton(IMessages.DialogButton.NO, new android.content.DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
					Intent intent = new Intent(CreateEventController.this, SingleGroupController.class);
					finish();
					startActivity(intent);
				}
			});

			builder.setPositiveButton(IMessages.DialogButton.SHARE_EVENT_VIA_TWITTER,
							new android.content.DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog, int which) {
									new SocialNetworkSharer().execute("twitter");

									dialog.dismiss();
									Intent intent = new Intent(CreateEventController.this, SingleGroupController.class);
									finish();
									startActivity(intent);
								}
							});
			builder.setNegativeButton(IMessages.DialogButton.SHARE_EVENT_VIA_FACEBOOK,
							new android.content.DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog, int which) {
									new SocialNetworkSharer().execute("facebook");

									dialog.dismiss();
									Intent intent = new Intent(CreateEventController.this, SingleGroupController.class);
									finish();
									startActivity(intent);
								}
							});

			builder.create().show();
		}
	}
	
	// Social network sharing implementation
	class SocialNetworkSharer extends AsyncTask<String, String, String> {
		@Override
		protected String doInBackground(String... socialNetworkName) {
			return socialNetworkName[0];
		}

		@Override
		protected void onPostExecute(String socialNetworkName) {
			super.onPostExecute(socialNetworkName);
			String sharingMessage = "New event " + name.getText().toString() + " " + "was created by "
							+ UserData.getFIRST_NAME() + " " + UserData.getLAST_NAME() + ": " + ", Date -> "
							+ eventDate.getText().toString() + ", Time -> " + eventTime.getText().toString() + ", Location -> "
							+ eventLocation.getText().toString();

			shareToSocialNetwork(Intent.ACTION_SEND, socialNetworkName, sharingMessage);
		}
	}

	DatePickerDialog.OnDateSetListener dateRegularityChosen = new DatePickerDialog.OnDateSetListener() {

		@Override
		public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
			calendar.set(Calendar.YEAR, year);
			calendar.set(Calendar.MONTH, monthOfYear);
			calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
			updateRegularityChosen();
		}
	};

	// Date and time modification helper
	private void updateRegularityChosen() {

		String format = "yyyy-MM-dd";
		SimpleDateFormat sdf = new SimpleDateFormat(format, Locale.US);

		regularityChosen.setText(sdf.format(calendar.getTime()));
	}

	DatePickerDialog.OnDateSetListener dateEvent = new DatePickerDialog.OnDateSetListener() {

		@Override
		public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
			calendar.set(Calendar.YEAR, year);
			calendar.set(Calendar.MONTH, monthOfYear);
			calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
			updateEventDate();
		}
	};

	private void updateEventDate() {

		String format = "yyyy-MM-dd";
		SimpleDateFormat sdf = new SimpleDateFormat(format, Locale.US);

		eventDate.setText(sdf.format(calendar.getTime()));
	}

	TimePickerDialog.OnTimeSetListener timeEvent = new TimePickerDialog.OnTimeSetListener() {

		@Override
		public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
			calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
			calendar.set(Calendar.MINUTE, minute);
			updateEventTime();
		}
	};

	private void updateEventTime() {

		String format = "kk:mm";
		SimpleDateFormat sdf = new SimpleDateFormat(format, Locale.US);
		eventTime.setText(sdf.format(calendar.getTime()));
	}
}