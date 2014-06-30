package fhkl.de.orgapp.controller.event;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
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
	private static String URL_CREATE_NOTIFICATION = "http://pushrply.com/create_notification.php";

	private ProgressDialog pDialog;

	JSONParser jsonParser = new JSONParser();

	private static final String TAG_SUCCESS = "success";

	EditText name, eventLocation, eventDate, eventTime, regularityChosen;
	TextView regularityQuestion;
	CheckBox regularityDate;
	Spinner regularityDateChosen;

	private Button bSave;
	private Button bCancel;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.new_edit_event);

		name = (EditText) findViewById(R.id.EVENTNAME);
		eventLocation = (EditText) findViewById(R.id.EVENTLOCATION);
		eventDate = (EditText) findViewById(R.id.EVENTDATE);
		eventTime = (EditText) findViewById(R.id.EVENTTIME);

		regularityDate = (CheckBox) findViewById(R.id.REGULARITYDATE);
		regularityDateChosen = (Spinner) findViewById(R.id.REGULARITYDATE_CHOSEN);

		regularityQuestion = (TextView) findViewById(R.id.REGULARITY_QUESTION);
		regularityChosen = (EditText) findViewById(R.id.REGULARITY_CHOSEN);

		bSave = (Button) findViewById(R.id.SAVE);
		bCancel = (Button) findViewById(R.id.CANCEL);

		regularityDate.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

				if (isChecked == true) {
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
								regularityChosen.setVisibility(View.GONE);
							} else {
								regularityQuestion.setVisibility(View.VISIBLE);
								regularityChosen.setVisibility(View.VISIBLE);
							}
						}

						@Override
						public void onNothingSelected(AdapterView<?> parent) {
						}
					});

					regularityDateChosen.setAdapter(adapter);
				} else {
					regularityDateChosen.setVisibility(View.GONE);
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

			if (regularityDate.isChecked()) {
				if (!(InputValidator.isDateValid(regularityChosen.getText().toString(), new String("yyyy-MM-dd"))
								|| InputValidator.isDateValid(regularityChosen.getText().toString(), new String("yyyy.MM.dd"))
								|| InputValidator.isDateValid(regularityChosen.getText().toString(), new String("yyyy/MM/dd"))
								|| InputValidator.isDateValid(regularityChosen.getText().toString(), new String("dd-MM-yyyy"))
								|| InputValidator.isDateValid(regularityChosen.getText().toString(), new String("dd.MM.yyyy"))
								|| InputValidator.isDateValid(regularityChosen.getText().toString(), new String("dd/MM/yyyy")) || InputValidator
									.isNumberValid(regularityChosen.getText().toString()))) {
					return IMessages.INVALID_REGULARITY;
				} else {
					params.add(new BasicNameValuePair("regularityDate", regularityDateChosen.getSelectedItem().toString()));
					params.add(new BasicNameValuePair("regularity", regularityChosen.getText().toString()));
				}
			}

			if (name.getText().toString().isEmpty()) {
				return IMessages.INVALID_NAME;
			} else {
				params.add(new BasicNameValuePair("name", name.getText().toString()));
			}

			if (eventDate.getText().toString().isEmpty()) {
				return IMessages.INVALID_EVENTDATE;
			} else {
				params.add(new BasicNameValuePair("eventDate", eventDate.getText().toString()));
			}

			if (eventTime.getText().toString().isEmpty()) {
				return IMessages.INVALID_EVENTTIME;
			} else {
				params.add(new BasicNameValuePair("eventTime", eventTime.getText().toString()));
			}

			if (eventLocation.getText().toString().isEmpty()) {
				return IMessages.INVALID_EVENTLOCATION;
			} else {
				params.add(new BasicNameValuePair("eventLocation", eventLocation.getText().toString()));
			}
			params.add(new BasicNameValuePair("personId", UserData.getPERSONID()));
			params.add(new BasicNameValuePair("groupId", GroupData.getGROUPID()));

			JSONObject json = new JSONParser().makeHttpRequest(URL_CREATE_EVENT, "GET", params);

			try {
				int success = json.getInt(TAG_SUCCESS);

				if (success != 0) {
					List<NameValuePair> paramsCreateNotification = new ArrayList<NameValuePair>();

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
			}
		}
	}
}