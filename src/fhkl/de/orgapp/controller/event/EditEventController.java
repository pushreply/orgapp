package fhkl.de.orgapp.controller.event;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
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
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;
import fhkl.de.orgapp.R;
import fhkl.de.orgapp.controller.groups.SingleGroupController;
import fhkl.de.orgapp.util.IMessages;
import fhkl.de.orgapp.util.IUniformResourceLocator;
import fhkl.de.orgapp.util.JSONParser;
import fhkl.de.orgapp.util.MenuActivity;
import fhkl.de.orgapp.util.data.EventData;
import fhkl.de.orgapp.util.data.GroupData;
import fhkl.de.orgapp.util.data.UserData;
import fhkl.de.orgapp.util.validator.InputValidator;

public class EditEventController extends MenuActivity {

	private ProgressDialog pDialog;

	JSONParser jsonParser = new JSONParser();
	JSONArray member;

	private static final String TAG_SUCCESS = "success";
	int yearNew, monthNew, dayNew, hourNew, minuteNew, cntDate = 0, cntTime = 0;

	Calendar calendar;
	EditText name, eventLocation, eventDate, eventTime;
	CheckBox regularityDate;

	private Button bSave, bCancel;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.new_edit_event);
		checkOnNewNotificationsAndNotifyUser();
		calendar = Calendar.getInstance();

		name = (EditText) findViewById(R.id.EVENTNAME);
		eventLocation = (EditText) findViewById(R.id.EVENTLOCATION);
		eventDate = (EditText) findViewById(R.id.EVENTDATE);

		eventDate.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				DatePickerDialog dateDialog = new DatePickerDialog(EditEventController.this, dateEvent, calendar
								.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
				DatePicker datePicker = dateDialog.getDatePicker();

				if (cntDate == 0) {
					datePicker.updateDate(yearNew, monthNew, dayNew);
					cntDate++;
				}
				dateDialog.show();
			}
		});

		eventTime = (EditText) findViewById(R.id.EVENTTIME);

		eventTime.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				TimePickerDialog timeDialog = new TimePickerDialog(EditEventController.this, timeEvent, calendar
								.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true);
				if (cntTime == 0) {
					timeDialog.updateTime(hourNew, minuteNew);
					cntTime++;
				}
				timeDialog.show();
			}
		});

		regularityDate = (CheckBox) findViewById(R.id.REGULARITYDATE);
		regularityDate.setVisibility(View.GONE);

		bSave = (Button) findViewById(R.id.SAVE);
		bCancel = (Button) findViewById(R.id.CANCEL);

		bSave.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				new EditEvent().execute();
			}
		});

		bCancel.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
				Intent i = new Intent(EditEventController.this, SingleGroupController.class);
				startActivity(i);
			}
		});

		setText();
	}

	class EditEvent extends AsyncTask<String, String, String> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pDialog = new ProgressDialog(EditEventController.this);
			pDialog.setMessage(IMessages.Status.SAVING_EVENT);
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(true);
			pDialog.show();
		}

		protected String doInBackground(String... args) {
			List<NameValuePair> params = new ArrayList<NameValuePair>();

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

			if (eventDate.getText().toString().isEmpty()) {
				return IMessages.Error.INVALID_EVENTDATE;
			} else {
				if (eventDate.getText().toString().isEmpty()) {
					return IMessages.Error.INVALID_EVENTDATE;
				} else {
					SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.GERMANY);
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
			}

			if (eventTime.getText().toString().isEmpty()) {
				return IMessages.Error.INVALID_EVENTTIME;
			} else {
				params.add(new BasicNameValuePair("eventTime", eventTime.getText().toString()));
			}

			String message = new String();
			boolean eventChanged = false;
			if (!name.getText().toString().equals(EventData.getNAME())) {
				message += "Event name was changed from \"" + EventData.getNAME() + "\" to \"" + name.getText().toString()
								+ "\"";
				eventChanged = true;
			}

			if (!eventLocation.getText().toString().equals(EventData.getEVENTLOCATION())) {
				if (eventChanged) {
					message += "and event location was changed from \"" + EventData.getEVENTLOCATION() + "\" to \""
									+ eventLocation.getText().toString() + "\"";
				} else {
					message += "Event \"" + EventData.getNAME() + "\" location was changed from \""
									+ EventData.getEVENTLOCATION() + "\" to \"" + eventLocation.getText().toString() + "\"";
					eventChanged = true;
				}
			}

			if (!eventDate.getText().toString().equals(EventData.getEVENTDATE())) {
				if (eventChanged) {
					message += "and event date was changed from \"" + EventData.getEVENTDATE() + "\" to \""
									+ eventDate.getText().toString() + "\"";
				} else {
					message += "Event \"" + EventData.getNAME() + "\" date was changed from \"" + EventData.getEVENTDATE()
									+ "\" to \"" + eventDate.getText().toString() + "\"";
					eventChanged = true;
				}
			}
			String tmp = eventTime.getText().toString();
			tmp += ":00";
			if (!tmp.equals(EventData.getEVENTTIME())) {
				if (eventChanged) {
					message += "and event time was changed from \"" + EventData.getEVENTTIME().substring(0, 5) + "\" to \""
									+ eventTime.getText().toString() + "\"";
				} else {
					message += "Event \"" + EventData.getNAME() + "\" time was changed from \""
									+ EventData.getEVENTTIME().substring(0, 5) + "\" to \"" + eventTime.getText().toString() + "\"";
					eventChanged = true;
				}
			}

			if (!eventChanged) {
				return IMessages.Error.NO_CHANGES_MADE;
			}

			params.add(new BasicNameValuePair("do", "updateEvent"));
			params.add(new BasicNameValuePair("eventId", EventData.getEVENTID()));
			JSONObject json = new JSONParser().makeHttpsRequest(IUniformResourceLocator.URL.URL_EVENT, "GET", params,
							EditEventController.this);

			try {
				int success = json.getInt(TAG_SUCCESS);

				if (success != 0) {

					List<NameValuePair> paramsGetMemberList = new ArrayList<NameValuePair>();
					paramsGetMemberList.add(new BasicNameValuePair("do", "readAllAttendingMember"));
					paramsGetMemberList.add(new BasicNameValuePair("personId", UserData.getPERSONID()));
					paramsGetMemberList.add(new BasicNameValuePair("groupId", GroupData.getGROUPID()));
					json = new JSONParser().makeHttpsRequest(IUniformResourceLocator.URL.URL_EVENTPERSON, "GET",
									paramsGetMemberList, EditEventController.this);

					success = json.getInt(TAG_SUCCESS);
					if (success == 1) {

						member = json.getJSONArray("member");

						for (int i = 0; i < member.length(); i++) {
							JSONObject c = member.getJSONObject(i);

							List<NameValuePair> paramsCreateNotification = new ArrayList<NameValuePair>();
							paramsCreateNotification.add(new BasicNameValuePair("do", "create"));
							paramsCreateNotification.add(new BasicNameValuePair("eMail", c.getString("eMail")));
							paramsCreateNotification.add(new BasicNameValuePair("classification", "5"));
							paramsCreateNotification.add(new BasicNameValuePair("message", message));

							json = jsonParser.makeHttpsRequest(IUniformResourceLocator.URL.URL_NOTIFICATION, "GET",
											paramsCreateNotification, EditEventController.this);
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

			if (message != null) {
				Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
			} else {
				showDialogAndGoToSingleGroupController();
			}
		}

		private void showDialogAndGoToSingleGroupController() {
			AlertDialog.Builder builder = new AlertDialog.Builder(EditEventController.this);

			builder.setMessage(IMessages.SecurityIssue.SHARE_EDITED_EVENT);
			builder.setNeutralButton(IMessages.DialogButton.NO, new android.content.DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
					Intent intent = new Intent(EditEventController.this, SingleGroupController.class);
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
									Intent intent = new Intent(EditEventController.this, SingleGroupController.class);
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
									Intent intent = new Intent(EditEventController.this, SingleGroupController.class);
									finish();
									startActivity(intent);
								}
							});

			builder.create().show();
		}
	}

	class SocialNetworkSharer extends AsyncTask<String, String, String> {
		@Override
		protected String doInBackground(String... socialNetworkName) {
			return socialNetworkName[0];
		}

		@Override
		protected void onPostExecute(String socialNetworkName) {
			super.onPostExecute(socialNetworkName);
			String sharingMessage = "Event " + name.getText().toString() + " " + "was edited by " + UserData.getFIRST_NAME()
							+ " " + UserData.getLAST_NAME() + ": " + ", new Date -> " + eventDate.getText().toString()
							+ ", new Time -> " + eventTime.getText().toString() + ", new Location -> "
							+ eventLocation.getText().toString();

			shareToSocialNetwork(Intent.ACTION_SEND, socialNetworkName, sharingMessage);
		}
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

	private void setText() {
		name.setText(EventData.getNAME());
		eventLocation.setText(EventData.getEVENTLOCATION());
		eventDate.setText(EventData.getEVENTDATE());

		String formatTime = "kk:mm";
		String formatDate = "yyyy-MM-dd";
		SimpleDateFormat sdfTime = new SimpleDateFormat(formatTime, Locale.US);
		SimpleDateFormat sdfDate = new SimpleDateFormat(formatDate, Locale.US);
		Date time = null;
		Date date = null;
		try {
			time = sdfTime.parse(EventData.getEVENTTIME());
			date = sdfDate.parse(EventData.getEVENTDATE());
		} catch (ParseException e) {
			e.printStackTrace();
			logout();
		}
		eventTime.setText(sdfTime.format(time));

		String[] result = new String[3];

		result = sdfTime.format(time).split(":");
		hourNew = Integer.parseInt(result[0]);
		minuteNew = Integer.parseInt(result[1]);

		result = sdfDate.format(date).split("-");
		yearNew = Integer.parseInt(result[0]);
		monthNew = Integer.parseInt(result[1]);
		dayNew = Integer.parseInt(result[2]);

	}
}