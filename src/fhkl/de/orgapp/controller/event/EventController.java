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
import fhkl.de.orgapp.util.IMessages;
import fhkl.de.orgapp.util.IUniformResourceLocator;
import fhkl.de.orgapp.util.JSONParser;
import fhkl.de.orgapp.util.MenuActivity;
import fhkl.de.orgapp.util.data.EventData;
import fhkl.de.orgapp.util.data.GroupData;
import fhkl.de.orgapp.util.data.UserData;
import fhkl.de.orgapp.util.validator.InputValidator;

public class EventController extends MenuActivity {

	// Prepare progress dialog instance
	private ProgressDialog pDialog;

	// A new JSON parser instance
	JSONParser jsonParser = new JSONParser();

	// Marker tag received from server to client app
	// to inform whether the request is completed or failed.
	private static final String TAG_SUCCESS = "success";

	String message, changedMessage, commentId;

	// Variables to hold event details in the UI
	TextView eventName;
	TextView eventTime;
	TextView eventDate;
	TextView eventLocation;

	// Toggle Button for attending/not attending an event
	ToggleButton buttonAttendance;
	boolean toggleButtonChecked;

	private static final String TAG_COMMENT_ID = "COMMENTID";
	private static final String TAG_PERSON_ID = "PERSONID";
	private static final String TAG_FIRSTNAME = "FIRSTNAME";
	private static final String TAG_LASTNAME = "LASTNAME";
	private static final String TAG_MESSAGE = "MESSAGE";
	private static final String TAG_COMMENTDATETIME = "COMMENTDATETIME";

	// Variables to hold comments data of an event in the UI.
	TextView messageContent;
	TextView messageDateTime;
	TextView firstname;
	TextView lastname;

	// Comment container as an array list and JSON array
	ArrayList<HashMap<String, String>> commentList;
	JSONArray comment = null;
	JSONArray member = null;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.event);

		// Check new notification and notify user
		checkOnNewNotificationsAndNotifyUser();

		// Displays event name on the ActionBar
		this.setTitle(EventData.getNAME());

		// Set the eventName and bind to their UI element in the layout.
		eventName = (TextView) findViewById(R.id.EVENTNAME);
		eventTime = (TextView) findViewById(R.id.EVENTTIME);
		eventDate = (TextView) findViewById(R.id.EVENTDATE);
		eventLocation = (TextView) findViewById(R.id.EVENTLOCATION);

		eventName.setText(EventData.getNAME());
		eventTime.setText(EventData.getEVENTTIME());
		eventDate.setText(EventData.getEVENTDATE());
		eventLocation.setText(EventData.getEVENTLOCATION());

		buttonAttendance = (ToggleButton) findViewById(R.id.BUTTONATTENDANCE);

		// Make the toggle button clickable to
		// execute the ChangeAttendingStatus()
		buttonAttendance.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View view) {

				toggleButtonChecked = ((ToggleButton) view).isChecked();
				new ChangeAttendingStatus().execute();
			}
		});

		// Show an event detail
		new GetEvent().execute();

		commentList = new ArrayList<HashMap<String, String>>();

		// Show the comments related to the current event detail
		new ShowComments().execute();
	}

	public void addComment(View v) {
		// Prepare a alert dialog builder for adding comments dialog
		AlertDialog.Builder builder = new AlertDialog.Builder(EventController.this);

		// Set the UI text dialog for messages and button
		builder.setTitle(IMessages.SecurityIssue.NEW_COMMENT);
		final EditText addComment = new EditText(EventController.this);
		builder.setView(addComment);

		// Make the "+" image as clickable button to let user add a new comment
		builder.setNegativeButton(IMessages.DialogButton.NEW_GENERIC, new DialogInterface.OnClickListener() {
			//
			@Override
			public void onClick(DialogInterface dialog, int which) {

				message = addComment.getText().toString();
				new AddComment().execute();
			}
		});

		// or let the user cancel the action
		builder.setPositiveButton(IMessages.DialogButton.CANCEL, new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				return;
			}
		});
		builder.create().show();
	}

	/**
	 * Begin the background operation using asynchronous task to fetch data
	 * through the network. The extended AsyncTask, GetEvent class retrieves event
	 * detail by requesting the personId and eventId using the HTTP GET request.
	 * The PHP files on the server side handle the request, return the result and
	 * a success marker to the client app.
	 * 
	 * 
	 */
	class GetEvent extends AsyncTask<String, String, String> {

		// Show progress dialog on "updating" after updating an dataset or
		// loading an event after an event on a list clicked/selected or updated.
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
			// Prepare the HTTP GET request parameter and values
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair("do", "readAttendingMember"));
			params.add(new BasicNameValuePair("personId", UserData.getPERSONID()));
			params.add(new BasicNameValuePair("eventId", EventData.getEVENTID()));

			// Set a json object and make a HTTP Request using URL through a GET
			// Request and the prepared parameters,
			// and a context, this controller class, to pass through an HTTPS.
			JSONObject json = jsonParser.makeHttpRequest(IUniformResourceLocator.URL.URL_EVENTPERSON, "GET", params,
							EventController.this);

			// Log the json string
			Log.d("EventPerson: ", json.toString());

			// Toggle button marks the "going/not going" event.
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
			// Dismiss the progress dialog and set the toggle button marker.
			pDialog.dismiss();

			setText();
		}
	}

	private void setText() {

		buttonAttendance.setChecked(toggleButtonChecked);
	}

	/**
	 * Begin the background operation using asynchronous task to change data
	 * through the network. The extended AsyncTask, ChangeAttendingStatus class
	 * modifies attending state by requesting the personId and eventId using the
	 * HTTP GET request. The PHP files on the server side handle the request,
	 * return the result and a success marker to the client app.
	 * 
	 * 
	 */
	class ChangeAttendingStatus extends AsyncTask<String, String, String> {
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			// Run a progress dialog
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
				params.add(new BasicNameValuePair("do", "createPersonInEvent"));
				json = jsonParser.makeHttpRequest(IUniformResourceLocator.URL.URL_EVENTPERSON, "GET", params,
								EventController.this);
			} else {
				params.add(new BasicNameValuePair("do", "deletePersonInEvent"));
				json = jsonParser.makeHttpRequest(IUniformResourceLocator.URL.URL_EVENTPERSON, "GET", params,
								EventController.this);
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

	/**
	 * Begin the background operation using asynchronous task to retrieve data
	 * through the network. The extended AsyncTask, ShowComments class retrieves
	 * all comments of an event by requesting the eventId using the HTTP GET
	 * request. The string "do=showcomment" is being used to execute the
	 * corresponding PHP operation in the back-end. The PHP files on the server
	 * side handle the request, return the result as a list and a success marker
	 * to the client app.
	 * 
	 * 
	 */
	class ShowComments extends AsyncTask<String, String, String> {

		@Override
		protected String doInBackground(String... params) {
			// Prepare the HTTP GET Parameters
			List<NameValuePair> vp = new ArrayList<NameValuePair>();
			vp.add(new BasicNameValuePair("do", "showcomment"));
			vp.add(new BasicNameValuePair("eventId", EventData.getEVENTID()));

			System.out.println("EventData.getEVENTID() : " + EventData.getEVENTID());

			// Send the request as a json object including this context for the HTTPS
			// request
			JSONObject json = jsonParser.makeHttpRequest(IUniformResourceLocator.URL.URL_COMMENT, "GET", vp,
							EventController.this);

			Log.d("Comments: ", json.toString());

			try {
				int success = json.getInt(TAG_SUCCESS);
				if (success == 1) {
					comment = json.getJSONArray("comment");

					for (int i = 0; i < comment.length(); i++) {

						JSONObject c = comment.getJSONObject(i);

						// Main elements: commentId, personId, first name, last name,
						// date and time of a comment (server-time) and the message.
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
			// An adapter to hold the comment item in the list
			runOnUiThread(new Runnable() {
				public void run() {
					ListAdapter adapter = new SimpleAdapter(EventController.this, commentList, R.layout.comment_item,
									new String[] { TAG_COMMENT_ID, TAG_MESSAGE, TAG_FIRSTNAME, TAG_LASTNAME, TAG_COMMENTDATETIME },
									new int[] { R.id.COMMENTID, R.id.MESSAGE, R.id.FIRSTNAME, R.id.LASTNAME, R.id.COMMENTDATETIME });

					ListView commentList = (ListView) findViewById(android.R.id.list);

					// Make each item clickable and popup a dialog by click and hold an
					// item
					commentList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
						@Override
						public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

							// Check whether the personId exists and has the privilege to edit
							// and delete a comment
							if (GroupData.getPERSONID().equals(UserData.getPERSONID())
											|| GroupData.getPRIVILEGE_EDIT_COMMENT().equals("1")
											|| GroupData.getPRIVILEGE_DELETE_COMMENT().equals("1")) {

								// Bind values to UI
								TextView tv = (TextView) view.findViewById(R.id.MESSAGE);
								message = tv.getText().toString();
								tv = (TextView) view.findViewById(R.id.COMMENTID);
								commentId = tv.getText().toString();

								// Prepare an alert dialog builder to let user have a menu
								// to edit, delete a comment or cancel the menu dialog.
								AlertDialog.Builder builder = new AlertDialog.Builder(EventController.this);
								builder.setTitle(IMessages.SecurityIssue.COMMENT);

								// Action: Edit
								builder.setNegativeButton(IMessages.DialogButton.EDIT_GENERIC,
												new android.content.DialogInterface.OnClickListener() {

													@Override
													public void onClick(DialogInterface dialog, int which) {

														// Check whether the user has an Edit privilege
														if (GroupData.getPERSONID().equals(UserData.getPERSONID())
																		|| GroupData.getPRIVILEGE_EDIT_COMMENT().equals("1")) {

															AlertDialog.Builder builder_inside = new AlertDialog.Builder(EventController.this);
															builder_inside.setTitle(IMessages.SecurityIssue.EDIT_COMMENT);

															final EditText editComment = new EditText(EventController.this);
															editComment.setText(message);
															builder_inside.setView(editComment);

															// Action: confirm Edit
															builder_inside.setNegativeButton(IMessages.DialogButton.EDIT_GENERIC,
																			new DialogInterface.OnClickListener() {

																				@Override
																				public void onClick(DialogInterface dialog, int which) {

																					changedMessage = editComment.getText().toString();
																					System.out.println("changedMessage: " + changedMessage);
																					new EditComment().execute();
																				}
																			});

															// Action: cancel edit and exit the menu dialog
															builder_inside.setPositiveButton(IMessages.DialogButton.CANCEL,
																			new DialogInterface.OnClickListener() {

																				@Override
																				public void onClick(DialogInterface dialog, int which) {
																					return;
																				}
																			});

															builder_inside.create().show();
														} else {
															// Inform the user that the action can't be
															// completed due to insufficient privileges.
															Toast.makeText(getApplicationContext(), IMessages.Error.INSUFFICIENT_PRIVILEGES,
																			Toast.LENGTH_LONG).show();
															dialog.dismiss();
														}
													}
												});

								// Action: Delete
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

																					// Action: confirm delete
																					builder_inside_inside.setNegativeButton(
																									IMessages.DialogButton.DELETE_GENERIC,
																									new DialogInterface.OnClickListener() {

																										@Override
																										public void onClick(DialogInterface dialog, int which) {

																											new DeleteComment().execute();
																										}
																									});

																					// Action: cancel delete and exit the
																					// menu dialog
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
															// Inform the user that the action can't be
															// completed due to insufficient privileges.
															Toast.makeText(getApplicationContext(), IMessages.Error.INSUFFICIENT_PRIVILEGES,
																			Toast.LENGTH_LONG).show();
															dialog.dismiss();
														}
													}
												});
								// Action: cancel menu dialog and exit
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
								// Inform the user that the action can't be completed due to
								// insufficient privileges.
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

	/**
	 * Begin the background operation using asynchronous task to delete the data
	 * through the network. The extended AsyncTask, DeleteComment class deletes a
	 * comment in an event by requesting the commentId using the HTTP GET request.
	 * The string "do=deletecomment" is being used to execute the corresponding
	 * PHP delete operation in the back-end. The PHP files on the server side
	 * handle the request, return the result as a list and a success marker to the
	 * client app.
	 * 
	 * 
	 */

	class DeleteComment extends AsyncTask<String, String, String> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			// Run a progress dialog
			pDialog = new ProgressDialog(EventController.this);
			pDialog.setMessage(IMessages.Status.DELETING_COMMENT);
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(true);
			pDialog.show();
		}

		protected String doInBackground(String... args) {
			// Prepare the HTTP GET Parameters
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair("do", "deletecomment"));
			params.add(new BasicNameValuePair("commentId", commentId));

			// Send the request as a json object including this context for the HTTPS
			// request
			JSONObject json = jsonParser.makeHttpRequest(IUniformResourceLocator.URL.URL_COMMENT, "GET", params,
							EventController.this);
			Log.d("comment: ", json.toString());

			try {
				int success = json.getInt(TAG_SUCCESS);
				System.out.println(success);
				if (success == 1) {
					List<NameValuePair> paramsGetAttendingMember = new ArrayList<NameValuePair>();
					paramsGetAttendingMember.add(new BasicNameValuePair("do", "readAllAttendingMember"));
					paramsGetAttendingMember.add(new BasicNameValuePair("personId", UserData.getPERSONID()));
					paramsGetAttendingMember.add(new BasicNameValuePair("eventId", EventData.getEVENTID()));
					json = new JSONParser().makeHttpRequest(IUniformResourceLocator.URL.URL_EVENTPERSON, "GET",
									paramsGetAttendingMember, EventController.this);

					success = json.getInt(TAG_SUCCESS);
					if (success == 1) {

						member = json.getJSONArray("member");

						for (int i = 0; i < member.length(); i++) {
							JSONObject c = member.getJSONObject(i);

							List<NameValuePair> paramsCreateNotification = new ArrayList<NameValuePair>();
							paramsCreateNotification.add(new BasicNameValuePair("do", "create"));
							paramsCreateNotification.add(new BasicNameValuePair("eMail", c.getString("eMail")));
							paramsCreateNotification.add(new BasicNameValuePair("classification", "9"));
							paramsCreateNotification.add(new BasicNameValuePair("syncInterval", "0"));
							paramsCreateNotification.add(new BasicNameValuePair("message", IMessages.Notification.DELETE_COMMENT_1
											+ message + IMessages.Notification.DELETE_COMMENT_2));

							json = jsonParser.makeHttpRequest(IUniformResourceLocator.URL.URL_NOTIFICATION, "GET",
											paramsCreateNotification, EventController.this);
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
			// Run a progress dialog
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

			JSONObject json = jsonParser.makeHttpRequest(IUniformResourceLocator.URL.URL_COMMENT, "GET", params,
							EventController.this);

			Log.d("comment: ", json.toString());

			try {
				int success = json.getInt(TAG_SUCCESS);
				System.out.println(success);
				if (success == 1) {

					List<NameValuePair> paramsGetAttendingMember = new ArrayList<NameValuePair>();
					paramsGetAttendingMember.add(new BasicNameValuePair("do", "readAllAttendingMember"));
					paramsGetAttendingMember.add(new BasicNameValuePair("personId", UserData.getPERSONID()));
					paramsGetAttendingMember.add(new BasicNameValuePair("eventId", EventData.getEVENTID()));
					json = new JSONParser().makeHttpRequest(IUniformResourceLocator.URL.URL_EVENTPERSON, "GET",
									paramsGetAttendingMember, EventController.this);

					success = json.getInt(TAG_SUCCESS);
					if (success == 1) {

						member = json.getJSONArray("member");

						for (int i = 0; i < member.length(); i++) {
							JSONObject c = member.getJSONObject(i);

							List<NameValuePair> paramsCreateNotification = new ArrayList<NameValuePair>();
							paramsCreateNotification.add(new BasicNameValuePair("do", "create"));
							paramsCreateNotification.add(new BasicNameValuePair("eMail", c.getString("eMail")));
							paramsCreateNotification.add(new BasicNameValuePair("classification", "8"));
							paramsCreateNotification.add(new BasicNameValuePair("syncInterval", "0"));
							paramsCreateNotification.add(new BasicNameValuePair("message", IMessages.Notification.EDIT_COMMENT_1
											+ message + IMessages.Notification.EDIT_COMMENT_2 + changedMessage
											+ IMessages.Notification.EDIT_COMMENT_3));

							json = jsonParser.makeHttpRequest(IUniformResourceLocator.URL.URL_NOTIFICATION, "GET",
											paramsCreateNotification, EventController.this);
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

	class AddComment extends AsyncTask<String, String, String> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pDialog = new ProgressDialog(EventController.this);
			pDialog.setMessage(IMessages.Status.SAVING_COMMENT);
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(true);
			pDialog.show();
		}

		@Override
		protected String doInBackground(String... params) {

			if (!InputValidator.isStringLengthInRange(message, 0, 2048)) {
				return IMessages.Error.INVALID_COMMENT;
			}

			String eventId = EventData.getEVENTID();
			String personId = UserData.getPERSONID();

			List<NameValuePair> paramsInsertComment = new ArrayList<NameValuePair>();

			paramsInsertComment.add(new BasicNameValuePair("do", "addcomment"));
			paramsInsertComment.add(new BasicNameValuePair("eventId", eventId));
			paramsInsertComment.add(new BasicNameValuePair("personId", personId));
			paramsInsertComment.add(new BasicNameValuePair("message", message));

			JSONObject json = jsonParser.makeHttpRequest(IUniformResourceLocator.URL.URL_COMMENT, "GET", paramsInsertComment,
							EventController.this);

			Log.d("comment: ", json.toString());

			try {
				int success = json.getInt(TAG_SUCCESS);
				System.out.println(success);
				if (success == 1) {

					List<NameValuePair> paramsGetAttendingMember = new ArrayList<NameValuePair>();
					paramsGetAttendingMember.add(new BasicNameValuePair("do", "readAllAttendingMember"));
					paramsGetAttendingMember.add(new BasicNameValuePair("personId", UserData.getPERSONID()));
					paramsGetAttendingMember.add(new BasicNameValuePair("eventId", EventData.getEVENTID()));
					json = new JSONParser().makeHttpRequest(IUniformResourceLocator.URL.URL_EVENTPERSON, "GET",
									paramsGetAttendingMember, EventController.this);

					success = json.getInt(TAG_SUCCESS);
					if (success == 1) {

						member = json.getJSONArray("member");

						for (int i = 0; i < member.length(); i++) {
							JSONObject c = member.getJSONObject(i);

							List<NameValuePair> paramsCreateNotification = new ArrayList<NameValuePair>();
							paramsCreateNotification.add(new BasicNameValuePair("do", "create"));
							paramsCreateNotification.add(new BasicNameValuePair("eMail", c.getString("eMail")));
							paramsCreateNotification.add(new BasicNameValuePair("classification", "7"));
							paramsCreateNotification.add(new BasicNameValuePair("syncInterval", "0"));
							paramsCreateNotification.add(new BasicNameValuePair("message", IMessages.Notification.CREATE_COMMENT_1
											+ EventData.getNAME() + IMessages.Notification.CREATE_COMMENT_2));

							json = jsonParser.makeHttpRequest(IUniformResourceLocator.URL.URL_NOTIFICATION, "GET",
											paramsCreateNotification, EventController.this);
						}
					}
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}

			return null;
		}

		@Override
		protected void onPostExecute(String message) {
			super.onPostExecute(message);
			pDialog.dismiss();

			if (message != null) {
				Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
			} else {
				finish();
				Intent i = new Intent(EventController.this, EventController.class);
				startActivity(i);

			}
		}
	}
}