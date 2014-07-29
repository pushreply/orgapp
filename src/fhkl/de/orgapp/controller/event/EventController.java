package fhkl.de.orgapp.controller.event;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;
import fhkl.de.orgapp.R;
import fhkl.de.orgapp.controller.comment.InsertCommentController;
import fhkl.de.orgapp.util.IMessages;
import fhkl.de.orgapp.util.JSONParser;
import fhkl.de.orgapp.util.MenuActivity;
import fhkl.de.orgapp.util.data.CommentData;
import fhkl.de.orgapp.util.data.EventData;
import fhkl.de.orgapp.util.data.GroupData;
import fhkl.de.orgapp.util.data.UserData;

public class EventController extends MenuActivity {

	private ProgressDialog pDialog;
	JSONParser jsonParser = new JSONParser();

	private static String URL_GET_PERSON_IN_EVENT = "http://pushrply.com/get_person_in_event.php";
	private static String URL_CREATE_PERSON_IN_EVENT = "http://pushrply.com/create_person_in_event.php";
	private static String URL_DELETE_PERSON_IN_EVENT = "http://pushrply.com/delete_person_in_event.php";
	private static String URL_COMMENTCONTROL = "http://pushrply.com/pdo_commentcontrol.php";
	private static String URL_GET_ATTENDING_MEMBER = "http://pushrply.com/get_attending_member.php";
	private static String URL_CREATE_NOTIFICATION = "http://pushrply.com/create_notification.php";

	private static final String TAG_SUCCESS = "success";

	String message, changedMessage, commentId;

	TextView eventName;
	TextView eventTime;
	TextView eventDate;
	TextView eventLocation;

	ToggleButton buttonAttendance;
	boolean toggleButtonChecked;

	private static final String TAG_COMMENT_ID = "COMMENTID";
	private static final String TAG_PERSON_ID = "PERSONID";
	private static final String TAG_FIRSTNAME = "FIRSTNAME";
	private static final String TAG_LASTNAME = "LASTNAME";
	private static final String TAG_MESSAGE = "MESSAGE";
	private static final String TAG_COMMENTDATETIME = "COMMENTDATETIME";

	TextView messageContent;
	TextView messageDateTime;
	TextView firstname;
	TextView lastname;

	ArrayList<HashMap<String, String>> commentList;
	JSONArray comment = null;
	JSONArray member = null;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.event);
		checkOnNewNotificationsAndNotifyUser();
		this.setTitle(EventData.getNAME());
		eventName = (TextView) findViewById(R.id.EVENTNAME);
		eventTime = (TextView) findViewById(R.id.EVENTTIME);
		eventDate = (TextView) findViewById(R.id.EVENTDATE);
		eventLocation = (TextView) findViewById(R.id.EVENTLOCATION);

		eventName.setText(EventData.getNAME());
		eventTime.setText(EventData.getEVENTTIME());
		eventDate.setText(EventData.getEVENTDATE());
		eventLocation.setText(EventData.getEVENTLOCATION());

		buttonAttendance = (ToggleButton) findViewById(R.id.BUTTONATTENDANCE);
		buttonAttendance.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View view) {

				toggleButtonChecked = ((ToggleButton) view).isChecked();
				new ChangeAttendingStatus().execute();
			}
		});
		new GetEvent().execute();

		commentList = new ArrayList<HashMap<String, String>>();
		new ShowComments().execute();
	}
	
	public void addComment(View v)
	{
		Intent intent = new Intent(EventController.this, InsertCommentController.class);
		startActivity(intent);
	}

	class GetEvent extends AsyncTask<String, String, String> {
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pDialog = new ProgressDialog(EventController.this);
			if (getIntent().getStringExtra("Refresh") != null)
				pDialog.setMessage(IMessages.Status.UPDATING);
			else
				pDialog.setMessage(IMessages.Status.LOADING_EVENT);
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(true);
			pDialog.show();
		}

		protected String doInBackground(String... args) {
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair("personId", UserData.getPERSONID()));
			params.add(new BasicNameValuePair("eventId", EventData.getEVENTID()));
			JSONObject json = jsonParser.makeHttpRequest(URL_GET_PERSON_IN_EVENT, "GET", params);

			Log.d("EventPerson: ", json.toString());

			try {
				int success = json.getInt(TAG_SUCCESS);
				if (success == 1) {
					toggleButtonChecked = true;
				} else {
					toggleButtonChecked = false;
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
			return null;
		}

		protected void onPostExecute(String message) {
			pDialog.dismiss();

			setText();
		}
	}

	private void setText() {

		buttonAttendance.setChecked(toggleButtonChecked);
	}

	class ChangeAttendingStatus extends AsyncTask<String, String, String> {
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pDialog = new ProgressDialog(EventController.this);
			pDialog.setMessage(IMessages.Status.CHANGING_STATUS);
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(true);
			pDialog.show();
		}

		protected String doInBackground(String... args) {
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair("personId", UserData.getPERSONID()));
			params.add(new BasicNameValuePair("eventId", EventData.getEVENTID()));
			JSONObject json;
			if (toggleButtonChecked) {
				json = jsonParser.makeHttpRequest(URL_CREATE_PERSON_IN_EVENT, "GET", params);
			} else {
				json = jsonParser.makeHttpRequest(URL_DELETE_PERSON_IN_EVENT, "GET", params);
			}

			Log.d("EventPerson: ", json.toString());

			try {
				int success = json.getInt(TAG_SUCCESS);
				if (success == 1) {
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
			return null;
		}

		protected void onPostExecute(String message) {
			pDialog.dismiss();
		}
	}

	// Show comments
	class ShowComments extends AsyncTask<String, String, String> {

		@Override
		protected String doInBackground(String... params) {
			List<NameValuePair> vp = new ArrayList<NameValuePair>();

			CommentData.setACTION("showcomment");
			vp.add(new BasicNameValuePair("do", CommentData.getACTION()));
			vp.add(new BasicNameValuePair("eventId", EventData.getEVENTID()));

			System.out.println("EventData.getEVENTID() : " + EventData.getEVENTID());

			JSONObject json = jsonParser.makeHttpRequest(URL_COMMENTCONTROL, "GET", vp);

			Log.d("Comments: ", json.toString());

			try {
				int success = json.getInt(TAG_SUCCESS);
				if (success == 1) {
					comment = json.getJSONArray("comment");

					for (int i = 0; i < comment.length(); i++) {

						JSONObject c = comment.getJSONObject(i);

						String commentId = c.getString("commentId");
						String personId = c.getString("personId");
						String firstname = c.getString("firstName");
						String lastname = c.getString("lastName");
						String commentdatetime = c.getString("commentDateTime");
						String message = c.getString("message");

						HashMap<String, String> map = new HashMap<String, String>();
						map.put(TAG_COMMENT_ID, commentId);
						map.put(TAG_PERSON_ID, personId);
						map.put(TAG_MESSAGE, message);
						map.put(TAG_FIRSTNAME, firstname);
						map.put(TAG_LASTNAME, lastname);
						map.put(TAG_COMMENTDATETIME, commentdatetime);

						commentList.add(map);
					}
				} else {

				}
			} catch (JSONException e) {
				System.out.println("Error in CommentData.doInBackground(String... arg0): " + e.getMessage());
				e.printStackTrace();
			}

			return null;
		}

		protected void onPostExecute(String file_url) {
			runOnUiThread(new Runnable() {
				public void run() {
					ListAdapter adapter = new SimpleAdapter(EventController.this, commentList, R.layout.comment_item,
									new String[] { TAG_COMMENT_ID, TAG_MESSAGE, TAG_FIRSTNAME, TAG_LASTNAME, TAG_COMMENTDATETIME },
									new int[] { R.id.COMMENTID, R.id.MESSAGE, R.id.FIRSTNAME, R.id.LASTNAME, R.id.COMMENTDATETIME });

					ListView commentList = (ListView) findViewById(android.R.id.list);

					commentList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
						@Override
						public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

							if (GroupData.getPERSONID().equals(UserData.getPERSONID())
											|| GroupData.getPRIVILEGE_EDIT_COMMENT().equals("1")
											|| GroupData.getPRIVILEGE_DELETE_COMMENT().equals("1")) {

								TextView tv = (TextView) view.findViewById(R.id.MESSAGE);
								message = tv.getText().toString();
								tv = (TextView) view.findViewById(R.id.COMMENTID);
								commentId = tv.getText().toString();
								System.out.println("message: " + message);
								System.out.println("commentId: " + commentId);
								AlertDialog.Builder builder = new AlertDialog.Builder(EventController.this);
								builder.setTitle(IMessages.SecurityIssue.COMMENT);

								builder.setNegativeButton(IMessages.DialogButton.EDIT_GENERIC,
												new android.content.DialogInterface.OnClickListener() {

													@Override
													public void onClick(DialogInterface dialog, int which) {

														if (GroupData.getPERSONID().equals(UserData.getPERSONID())
																		|| GroupData.getPRIVILEGE_EDIT_COMMENT().equals("1")) {

															AlertDialog.Builder builder_inside = new AlertDialog.Builder(EventController.this);
															builder_inside.setTitle(IMessages.SecurityIssue.EDIT_COMMENT);

															final EditText editComment = new EditText(EventController.this);
															editComment.setText(message);
															builder_inside.setView(editComment);

															builder_inside.setNegativeButton(IMessages.DialogButton.EDIT_GENERIC,
																			new DialogInterface.OnClickListener() {

																				@Override
																				public void onClick(DialogInterface dialog, int which) {

																					changedMessage = editComment.getText().toString();
																					System.out.println("changedMessage: " + changedMessage);
																					new EditComment().execute();
																				}
																			});

															builder_inside.setPositiveButton(IMessages.DialogButton.CANCEL,
																			new DialogInterface.OnClickListener() {

																				@Override
																				public void onClick(DialogInterface dialog, int which) {
																					return;
																				}
																			});

															builder_inside.create().show();
														} else {
															Toast.makeText(getApplicationContext(), IMessages.Error.INSUFFICIENT_PRIVILEGES,
																			Toast.LENGTH_LONG).show();
															dialog.dismiss();
														}
													}
												});

								builder.setPositiveButton(IMessages.DialogButton.DELETE_GENERIC,
												new android.content.DialogInterface.OnClickListener() {

													@Override
													public void onClick(DialogInterface dialog, int which) {

														if (GroupData.getPERSONID().equals(UserData.getPERSONID())
																		|| GroupData.getPRIVILEGE_DELETE_COMMENT().equals("1")) {

															AlertDialog.Builder builder_inside = new AlertDialog.Builder(EventController.this);
															builder_inside.setTitle(IMessages.SecurityIssue.DELETE_COMMENT);

															TextView deleteComment = new TextView(EventController.this);
															deleteComment.setText(message);
															builder_inside.setView(deleteComment);

															builder_inside.setNegativeButton(IMessages.DialogButton.DELETE_GENERIC,
																			new DialogInterface.OnClickListener() {

																				@Override
																				public void onClick(DialogInterface dialog, int which) {

																					AlertDialog.Builder builder_inside_inside = new AlertDialog.Builder(
																									EventController.this);
																					builder_inside_inside
																									.setTitle(IMessages.SecurityIssue.QUESTION_DELETE_COMMENT);

																					builder_inside_inside.setNegativeButton(
																									IMessages.DialogButton.DELETE_GENERIC,
																									new DialogInterface.OnClickListener() {

																										@Override
																										public void onClick(DialogInterface dialog, int which) {

																											new DeleteComment().execute();
																										}
																									});

																					builder_inside_inside.setPositiveButton(IMessages.DialogButton.CANCEL,
																									new DialogInterface.OnClickListener() {

																										@Override
																										public void onClick(DialogInterface dialog, int which) {
																											return;
																										}
																									});

																					builder_inside_inside.create().show();
																				}
																			});

															builder_inside.setPositiveButton(IMessages.DialogButton.CANCEL,
																			new DialogInterface.OnClickListener() {

																				@Override
																				public void onClick(DialogInterface dialog, int which) {
																					return;
																				}
																			});

															builder_inside.create().show();
														} else {
															Toast.makeText(getApplicationContext(), IMessages.Error.INSUFFICIENT_PRIVILEGES,
																			Toast.LENGTH_LONG).show();
															dialog.dismiss();
														}
													}
												});

								builder.setNeutralButton(IMessages.DialogButton.CANCEL,
												new android.content.DialogInterface.OnClickListener() {

													@Override
													public void onClick(DialogInterface dialog, int which) {

														dialog.dismiss();
													}

												});
								builder.create().show();
								return true;
							} else {
								Toast.makeText(getApplicationContext(), IMessages.Error.INSUFFICIENT_PRIVILEGES, Toast.LENGTH_LONG)
												.show();
								return false;
							}

						}
					});

					commentList.setAdapter(adapter);
				}
			});
		}
	}

	class DeleteComment extends AsyncTask<String, String, String> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pDialog = new ProgressDialog(EventController.this);
			pDialog.setMessage(IMessages.Status.DELETING_COMMENT);
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(true);
			pDialog.show();
		}

		protected String doInBackground(String... args) {
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair("do", "deletecomment"));
			params.add(new BasicNameValuePair("commentId", commentId));

			JSONObject json = jsonParser.makeHttpRequest(URL_COMMENTCONTROL, "GET", params);

			Log.d("comment: ", json.toString());

			try {
				int success = json.getInt(TAG_SUCCESS);
				System.out.println(success);
				if (success == 1) {

					List<NameValuePair> paramsGetAttendingMember = new ArrayList<NameValuePair>();
					paramsGetAttendingMember.add(new BasicNameValuePair("personId", UserData.getPERSONID()));
					paramsGetAttendingMember.add(new BasicNameValuePair("eventId", EventData.getEVENTID()));
					json = new JSONParser().makeHttpRequest(URL_GET_ATTENDING_MEMBER, "GET", paramsGetAttendingMember);

					success = json.getInt(TAG_SUCCESS);
					if (success == 1) {

						member = json.getJSONArray("member");

						for (int i = 0; i < member.length(); i++) {
							JSONObject c = member.getJSONObject(i);

							List<NameValuePair> paramsCreateNotification = new ArrayList<NameValuePair>();
							paramsCreateNotification.add(new BasicNameValuePair("eMail", c.getString("eMail")));
							paramsCreateNotification.add(new BasicNameValuePair("classification", "9"));
							paramsCreateNotification.add(new BasicNameValuePair("syncInterval", "0"));
							paramsCreateNotification.add(new BasicNameValuePair("message", IMessages.Notification.DELETE_COMMENT_1
											+ message + IMessages.Notification.DELETE_COMMENT_2));

							json = jsonParser.makeHttpRequest(URL_CREATE_NOTIFICATION, "GET", paramsCreateNotification);
						}
					}
				}

			} catch (JSONException e) {
				System.out.println("Error in DeleteComment.doInBackground(String... args): " + e.getMessage());
				e.printStackTrace();
			}

			return null;
		}

		protected void onPostExecute(String result) {
			pDialog.dismiss();

			Intent intent = new Intent(EventController.this, EventController.class);
			startActivity(intent);
		}
	}

	class EditComment extends AsyncTask<String, String, String> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pDialog = new ProgressDialog(EventController.this);
			pDialog.setMessage(IMessages.Status.SAVING_COMMENT);
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(true);
			pDialog.show();
		}

		protected String doInBackground(String... args) {

			if (message.equals(changedMessage)) {
				return null;
			}

			List<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair("do", "updatecomment"));
			params.add(new BasicNameValuePair("commentId", commentId));
			params.add(new BasicNameValuePair("message", changedMessage));

			JSONObject json = jsonParser.makeHttpRequest(URL_COMMENTCONTROL, "GET", params);

			Log.d("comment: ", json.toString());

			try {
				int success = json.getInt(TAG_SUCCESS);
				System.out.println(success);
				if (success == 1) {

					List<NameValuePair> paramsGetAttendingMember = new ArrayList<NameValuePair>();
					paramsGetAttendingMember.add(new BasicNameValuePair("personId", UserData.getPERSONID()));
					paramsGetAttendingMember.add(new BasicNameValuePair("eventId", EventData.getEVENTID()));
					json = new JSONParser().makeHttpRequest(URL_GET_ATTENDING_MEMBER, "GET", paramsGetAttendingMember);

					success = json.getInt(TAG_SUCCESS);
					if (success == 1) {

						member = json.getJSONArray("member");

						for (int i = 0; i < member.length(); i++) {
							JSONObject c = member.getJSONObject(i);

							List<NameValuePair> paramsCreateNotification = new ArrayList<NameValuePair>();
							paramsCreateNotification.add(new BasicNameValuePair("eMail", c.getString("eMail")));
							paramsCreateNotification.add(new BasicNameValuePair("classification", "8"));
							paramsCreateNotification.add(new BasicNameValuePair("syncInterval", "0"));
							paramsCreateNotification.add(new BasicNameValuePair("message", IMessages.Notification.EDIT_COMMENT_1
											+ message + IMessages.Notification.EDIT_COMMENT_2 + changedMessage
											+ IMessages.Notification.EDIT_COMMENT_3));

							json = jsonParser.makeHttpRequest(URL_CREATE_NOTIFICATION, "GET", paramsCreateNotification);
						}
					}
				}
			} catch (JSONException e) {
				System.out.println("Error in EditComment.doInBackground(String... args): " + e.getMessage());
				e.printStackTrace();
			}
			return "success";
		}

		protected void onPostExecute(String result) {
			pDialog.dismiss();

			if (result != null) {
				Intent intent = new Intent(EventController.this, EventController.class);
				startActivity(intent);
			}
		}
	}
}