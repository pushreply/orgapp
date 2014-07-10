package fhkl.de.orgapp.controller.event;

import java.util.ArrayList;
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
import fhkl.de.orgapp.controller.event.EditEventController.SocialNetworkSharer;
import fhkl.de.orgapp.controller.groups.SingleGroupController;
import fhkl.de.orgapp.util.EventData;
import fhkl.de.orgapp.util.GroupData;
import fhkl.de.orgapp.util.IMessages;
import fhkl.de.orgapp.util.JSONParser;
import fhkl.de.orgapp.util.MenuActivity;
import fhkl.de.orgapp.util.UserData;

public class DeleteEventController extends MenuActivity {

	private static String URL_DELETE_ALL_MEMBERS_IN_EVENT = "http://pushrply.com/delete_all_members_in_event.php";
	private static String URL_DELETE_EVENT = "http://pushrply.com/delete_event.php";
	private static String URL_GET_MEMBER_LIST = "http://pushrply.com/get_member_list.php";
	private static String URL_CREATE_NOTIFICATION = "http://pushrply.com/create_notification.php";

	private static final String TAG_SUCCESS = "success";
	private ProgressDialog pDialog;
	JSONParser jsonParser = new JSONParser();
	JSONArray member = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		new DeleteEvent().execute();
	}

	class DeleteEvent extends AsyncTask<String, String, String> {
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pDialog = new ProgressDialog(DeleteEventController.this);
			pDialog.setMessage(IMessages.DELETING_EVENT);
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(true);
			pDialog.show();
		}

		@Override
		protected String doInBackground(String... args) {
			List<NameValuePair> paramsDelete = new ArrayList<NameValuePair>();
			paramsDelete.add(new BasicNameValuePair("eventId", EventData.getEVENTID()));

			JSONObject json = jsonParser.makeHttpRequest(URL_DELETE_ALL_MEMBERS_IN_EVENT, "GET", paramsDelete);

			try {
				if (json.getInt(TAG_SUCCESS) == 1) {

					json = jsonParser.makeHttpRequest(URL_DELETE_EVENT, "GET", paramsDelete);
					if (json.getInt(TAG_SUCCESS) == 1) {
						List<NameValuePair> paramsGetMemberList = new ArrayList<NameValuePair>();
						paramsGetMemberList.add(new BasicNameValuePair("personId", UserData.getPERSONID()));
						paramsGetMemberList.add(new BasicNameValuePair("groupId", GroupData.getGROUPID()));
						json = new JSONParser().makeHttpRequest(URL_GET_MEMBER_LIST, "GET", paramsGetMemberList);
						if (json.getInt(TAG_SUCCESS) == 1) {

							member = json.getJSONArray("member");

							for (int i = 0; i < member.length(); i++) {
								JSONObject c = member.getJSONObject(i);

								List<NameValuePair> paramsCreateNotification = new ArrayList<NameValuePair>();
								paramsCreateNotification.add(new BasicNameValuePair("eMail", c.getString("eMail")));
								paramsCreateNotification.add(new BasicNameValuePair("classification", "6"));
								paramsCreateNotification.add(new BasicNameValuePair("syncInterval", "0"));

								paramsCreateNotification.add(new BasicNameValuePair("message", IMessages.MESSAGE_DELETE_EVENT_1
												+ EventData.getNAME() + IMessages.MESSAGE_DELETE_EVENT_2));

								json = jsonParser.makeHttpRequest(URL_CREATE_NOTIFICATION, "GET", paramsCreateNotification);
								if (json.getInt(TAG_SUCCESS) != 1) {
								}
							}
						}
					}
				}
			} catch (JSONException e) {
				System.out.println("Error in DeleteEvent.doInBackground(String... args): " + e.getMessage());
				e.printStackTrace();
			}

			return null;
		}

		@Override
		protected void onPostExecute(String message) {
			pDialog.dismiss();
			showDialogAndGoToSingleGroupController();
//			Intent intent = new Intent(DeleteEventController.this, SingleGroupController.class);
//			finish();
//			startActivity(intent);
		}
		
		private void showDialogAndGoToSingleGroupController()
		{
			AlertDialog.Builder builder = new AlertDialog.Builder(DeleteEventController.this);
			
			builder.setMessage(IMessages.SHARE_DELETED_EVENT);
			builder.setPositiveButton(IMessages.NO_THANKS, new android.content.DialogInterface.OnClickListener()
			{
				@Override
				public void onClick(DialogInterface dialog, int which)
				{
					dialog.dismiss();
					Intent intent = new Intent(DeleteEventController.this, SingleGroupController.class);
					finish();
					startActivity(intent);
				}
			});
			
			builder.setNeutralButton(IMessages.SHARE_EVENT_VIA_TWITTER, new android.content.DialogInterface.OnClickListener()
			{
				@Override
				public void onClick(DialogInterface dialog, int which)
				{
					new SocialNetworkSharer().execute("twitter");
					
					dialog.dismiss();
					Intent intent = new Intent(DeleteEventController.this, SingleGroupController.class);
					finish();
					startActivity(intent);
				}
			});
			builder.setNegativeButton(IMessages.SHARE_EVENT_VIA_FACEBOOK, new android.content.DialogInterface.OnClickListener()
			{
				@Override
				public void onClick(DialogInterface dialog, int which)
				{
					new SocialNetworkSharer().execute("facebook");
					
					dialog.dismiss();
					Intent intent = new Intent(DeleteEventController.this, SingleGroupController.class);
					finish();
					startActivity(intent);
				}
			});
			
			builder.create().show();
		}
	}
	
	class SocialNetworkSharer extends AsyncTask<String, String, String>
	{
		@Override
		protected String doInBackground(String... socialNetworkName)
		{
			return socialNetworkName[0];
		}

		@Override
		protected void onPostExecute(String socialNetworkName)
		{
			super.onPostExecute(socialNetworkName);
			String sharingMessage =
					"Event "
					+ EventData.getNAME() + " "
					+ "was deleted by "
					+ UserData.getFIRST_NAME() + " " + UserData.getLAST_NAME();
			
			shareToSocialNetwork(Intent.ACTION_SEND, socialNetworkName, sharingMessage);
		}
	}
}