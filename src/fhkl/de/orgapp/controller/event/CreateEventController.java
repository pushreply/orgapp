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

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
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
import fhkl.de.orgapp.util.GroupData;
import fhkl.de.orgapp.util.IMessages;
import fhkl.de.orgapp.util.InputValidator;
import fhkl.de.orgapp.util.JSONParser;
import fhkl.de.orgapp.util.MenuActivity;
import fhkl.de.orgapp.util.UserData;

public class CreateEventController extends MenuActivity {

	private static String URL_CREATE_EVENT = "http://pushrply.com/create_event.php";
	private static String URL_GET_MEMBER_LIST = "http://pushrply.com/get_member_list.php";
	private static String URL_CREATE_NOTIFICATION = "http://pushrply.com/create_notification.php";

	private ProgressDialog pDialog;

	JSONParser jsonParser = new JSONParser();
	JSONArray member;

	private static final String TAG_SUCCESS = "success";

	Calendar calendar;
	EditText name, eventLocation, eventDate, eventTime, regularityChosen;
	TextView regularityQuestion;
	CheckBox regularityDate;
	Spinner regularityDateChosen;
	RadioGroup radioGroupRegularity;
	RadioButton radioButtonRegularityDate, radioButtonRegularityNumber;

	private Button bSave;
	private Button bCancel;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.new_edit_event);

		calendar = Calendar.getInstance();

		name = (EditText) findViewById(R.id.EVENTNAME);
		eventLocation = (EditText) findViewById(R.id.EVENTLOCATION);
		eventDate = (EditText) findViewById(R.id.EVENTDATE);

		eventDate.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				new DatePickerDialog(CreateEventController.this, dateEvent, calendar.get(Calendar.YEAR), calendar
								.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
			}
		});

		eventTime = (EditText) findViewById(R.id.EVENTTIME);

		eventTime.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				new TimePickerDialog(CreateEventController.this, timeEvent, calendar.get(Calendar.HOUR_OF_DAY), calendar
								.get(Calendar.MINUTE), true).show();
			}
		});

		regularityDate = (CheckBox) findViewById(R.id.REGULARITYDATE);
		regularityDateChosen = (Spinner) findViewById(R.id.REGULARITYDATE_CHOSEN);

		regularityQuestion = (TextView) findViewById(R.id.REGULARITY_QUESTION);
		regularityChosen = (EditText) findViewById(R.id.REGULARITY_CHOSEN);

		radioGroupRegularity = (RadioGroup) findViewById(R.id.RADIOGROUP_REGULARITY);

		radioButtonRegularityDate = (RadioButton) findViewById(R.id.REGULARITY_DATE);
		radioButtonRegularityNumber = (RadioButton) findViewById(R.id.REGULARITY_NUMBER);

		bSave = (Button) findViewById(R.id.SAVE);
		bCancel = (Button) findViewById(R.id.CANCEL);

		regularityDate.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

				if (isChecked) {
					regularityDateChosen.setVisibility(View.VISIBLE);
					ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(CreateEventController.this,
									R.array.SPINNER_REGULARITYDATE, android.R.layout.simple_spinner_item);

					adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
					regularityDateChosen.setOnItemSelectedListener(new OnItemSelectedListener() {
						@Override
						public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
							System.out.println(parent.getSelectedItem());
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

	class SaveEvent extends AsyncTask<String, String, String> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pDialog = new ProgressDialog(CreateEventController.this);
			pDialog.setMessage(IMessages.SAVING_EVENT);
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(true);
			pDialog.show();
		}

		protected String doInBackground(String... args) {
			List<NameValuePair> params = new ArrayList<NameValuePair>();

			if (!InputValidator.isStringLengthInRange(name.getText().toString(), 0, 255)) {
				return IMessages.INVALID_NAME;
			} else {
				params.add(new BasicNameValuePair("name", name.getText().toString()));
			}

			if (!InputValidator.isStringLengthInRange(eventLocation.getText().toString(), 0, 255)) {
				return IMessages.INVALID_EVENTLOCATION;
			} else {
				params.add(new BasicNameValuePair("eventLocation", eventLocation.getText().toString()));
			}

			if (eventDate.getText().toString().isEmpty()) {
				return IMessages.INVALID_EVENTDATE;
			} else {
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
				sdf.setLenient(false);

				try {
					Date chosenDate = sdf.parse(eventDate.getText().toString());
					Date currentDate = new Date();
					sdf.format(currentDate);
					if (chosenDate.before(currentDate)) {
						return IMessages.INVALID_EVENTDATE;
					}
				} catch (ParseException e) {
				}
				params.add(new BasicNameValuePair("eventDate", eventDate.getText().toString()));
			}

			if (eventTime.getText().toString().isEmpty()) {
				return IMessages.INVALID_EVENTTIME;
			} else {
				params.add(new BasicNameValuePair("eventTime", eventTime.getText().toString()));
			}

			// CheckBox clicked?
			if (regularityDate.isChecked()) {
				// Date RadioButton chosen?
				if (radioGroupRegularity.getCheckedRadioButtonId() == R.id.REGULARITY_DATE) {
					// Date chosen?
					if (regularityChosen.getText().toString().isEmpty()) {
						return IMessages.INVALID_REGULARITY_DATE;
					} else {
						// Date > Current Date?
						SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
						sdf.setLenient(false);

						try {
							Date chosenDate = sdf.parse(regularityChosen.getText().toString());
							Date currentDate = new Date();
							sdf.format(currentDate);
							if (chosenDate.before(currentDate)) {
								return IMessages.INVALID_REGULARITY_DATE;
							}
						} catch (ParseException e) {
						}
						// ToDo: Check SpinnerData versus DateData
						// Checks ok, ToDo: add params

					}
					// Number RadioButton chosen?
				} else if (radioGroupRegularity.getCheckedRadioButtonId() == R.id.REGULARITY_NUMBER) {
					// Date chosen?
					if (regularityChosen.getText().toString().isEmpty()) {
						return IMessages.INVALID_REGULARITY_NUMBER;
					} else {
						if (!InputValidator.isStringLengthInRange(regularityChosen.getText().toString(), 0, 2)) {
							return IMessages.INVALID_REGULARITY_NUMBER;
						}
						try {
							Integer chosenNumber = Integer.parseInt(regularityChosen.getText().toString());
							if (chosenNumber > 50) {
								return IMessages.INVALID_REGULARITY_NUMBER;
							}
						} catch (NumberFormatException e) {
						}
						// ToDo: Check SpinnerData versus DateData
						// Checks ok, ToDo: add params
					}
					// No RadioButton checked
				} else {
					return IMessages.INVALID_RADIOGROUP_REGULARITY;
				}
			}

			params.add(new BasicNameValuePair("personId", UserData.getPERSONID()));
			params.add(new BasicNameValuePair("groupId", GroupData.getGROUPID()));

			JSONObject json = new JSONParser().makeHttpRequest(URL_CREATE_EVENT, "GET", params);

			try {
				int success = json.getInt(TAG_SUCCESS);

				if (success != 0) {
					List<NameValuePair> paramsGetMemberList = new ArrayList<NameValuePair>();
					paramsGetMemberList.add(new BasicNameValuePair("personId", UserData.getPERSONID()));
					paramsGetMemberList.add(new BasicNameValuePair("groupId", GroupData.getGROUPID()));
					json = new JSONParser().makeHttpRequest(URL_GET_MEMBER_LIST, "GET", paramsGetMemberList);
					success = json.getInt(TAG_SUCCESS);
					if (success == 1) {

						member = json.getJSONArray("member");

						for (int i = 0; i < member.length(); i++) {
							JSONObject c = member.getJSONObject(i);

							List<NameValuePair> paramsCreateNotification = new ArrayList<NameValuePair>();
							paramsCreateNotification.add(new BasicNameValuePair("eMail", c.getString("eMail")));
							paramsCreateNotification.add(new BasicNameValuePair("classification", "4"));
							paramsCreateNotification.add(new BasicNameValuePair("syncInterval", "0"));
							paramsCreateNotification.add(new BasicNameValuePair("message", IMessages.MESSAGE_CREATE_EVENT_1
											+ GroupData.getGROUPNAME() + IMessages.MESSAGE_CREATE_EVENT_2 + name.getText().toString()));

							json = jsonParser.makeHttpRequest(URL_CREATE_NOTIFICATION, "GET", paramsCreateNotification);
						}
					}
				}
			} catch (Exception e) {
				System.out.println("Error in SaveEvent.doInBackground(String... args): " + e.getMessage());
				e.printStackTrace();
			}
			return null;
		}

		protected void onPostExecute(String message) {
			pDialog.dismiss();

			if (message != null) {
				Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
			} else {
				Intent intent = new Intent(CreateEventController.this, SingleGroupController.class);
				finish();
				startActivity(intent);
			}
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

		System.out.println(calendar.getTime());
		String format = "kk:mm";
		SimpleDateFormat sdf = new SimpleDateFormat(format, Locale.US);
		eventTime.setText(sdf.format(calendar.getTime()));
	}
}