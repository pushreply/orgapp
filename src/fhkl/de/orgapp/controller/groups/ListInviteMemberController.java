package fhkl.de.orgapp.controller.groups;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ProgressDialog;
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
import fhkl.de.orgapp.util.JSONParser;
import fhkl.de.orgapp.util.MenuActivity;
import fhkl.de.orgapp.util.UserData;

public class ListInviteMemberController extends MenuActivity
{
	private static String URL_GET_INVITE_MEMBER_LIST = "http://pushrply.com/get_invite_member_list.php";

	Button inviteButton, cancelButton;
	View horizontalLine;
	private ProgressDialog pDialog;
	JSONParser jsonParser = new JSONParser();
	List<HashMap<String, Object>> personList;
	
	// JSON nodes
	private static final String TAG_SUCCESS = "success";
	
	private static final String TAG_TEXT_FIRST_NAME = "TEXT_FIRST_NAME";
	private static final String TAG_FIRST_NAME = "FIRST_NAME";
	private static final String TAG_TEXT_LAST_NAME = "TEXT_LAST_NAME";
	private static final String TAG_LAST_NAME = "LAST_NAME";
	private static final String TAG_TEXT_EMAIL = "TEXT_EMAIL";
	private static final String TAG_EMAIL = "EMAIL";
	private static final String TAG_IS_SELECTED = "IS_SELECTED";
	
	JSONArray persons = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.invite_member_list);
		
		personList = new ArrayList<HashMap<String, Object>>();
		
		new PersonListGetter().execute();
	}

	class PersonListGetter extends AsyncTask<String, String, String>
	{
		@Override
		protected void onPreExecute()
		{
			super.onPreExecute();
			
			pDialog = new ProgressDialog(ListInviteMemberController.this);
			pDialog.setMessage(IMessages.LOADING_LIST);
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(true);
			pDialog.show();
		}

		protected String doInBackground(String... args)
		{
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair("personId", UserData.getPERSONID()));
			JSONObject json = jsonParser.makeHttpRequest(URL_GET_INVITE_MEMBER_LIST, "GET", params);
			int success;
			
			try
			{
				success = json.getInt(TAG_SUCCESS);
				
				if (success == 1)
				{
					persons = json.getJSONArray("memberList");
					int p;
					JSONObject person;
					String firstName, lastName, email;
					HashMap<String, Object> personMap;
					
					for(p = 0; p < persons.length(); p++)
					{
						person = persons.getJSONObject(p);
						
						firstName = person.getString("firstName");
						lastName = person.getString("lastName");
						email = person.getString("eMail");
						
						personMap = new HashMap<String, Object>();
						
						personMap.put(TAG_TEXT_FIRST_NAME, getString(R.string.TEXT_FIRST_NAME) + ": ");
						personMap.put(TAG_FIRST_NAME, firstName);
						personMap.put(TAG_TEXT_LAST_NAME, getString(R.string.TEXT_LAST_NAME) + ": ");
						personMap.put(TAG_LAST_NAME, lastName);
						personMap.put(TAG_TEXT_EMAIL, getString(R.string.TEXT_EMAIL) + ": ");
						personMap.put(TAG_EMAIL, email);
						personMap.put(TAG_IS_SELECTED, false);
						
						personList.add(personMap);
					}
				}
				else
				{
					//TODO error message
				}
			}
			catch(JSONException e)
			{
				System.out.println("Error in ListInviteMemberController.PersonListGetter.doInBackground(String...): " + e.getMessage());
				e.printStackTrace();
			}
			
			return null;
		}

		protected void onPostExecute(String message)
		{
			super.onPostExecute(message);
			
			pDialog.dismiss();
			
			loadLayout();
			
			runOnUiThread(new Runnable()
			{
				public void run()
				{
					ListAdapter adapter = new SimpleAdapter
											(
											   ListInviteMemberController.this,
											   personList,
											   R.layout.invite_member_list_item,
											   new String[]
													   	{
													   		TAG_TEXT_FIRST_NAME,
													   		TAG_FIRST_NAME,
													   		TAG_TEXT_LAST_NAME,
													   		TAG_LAST_NAME,
													   		TAG_TEXT_EMAIL,
													   		TAG_EMAIL
													   	},
											   new int[]
													   	{
													   		R.id.INVITE_MEMBER_LIST_TEXT_FIRST_NAME,
													   		R.id.INVITE_MEMBER_LIST_USER_FIRST_NAME,
													   		R.id.INVITE_MEMBER_LIST_TEXT_LAST_NAME,
													   		R.id.INVITE_MEMBER_LIST_USER_LAST_NAME,
													   		R.id.INVITE_MEMBER_LIST_TEXT_EMAIL,
													   		R.id.INVITE_MEMBER_LIST_USER_EMAIL
													   	}
											);

					// update listview
					final ListView personViewList = (ListView) findViewById(android.R.id.list);
					
					personViewList.setAdapter(adapter);
					
					personViewList.setOnItemClickListener(new AdapterView.OnItemClickListener()
					{
						@Override
						public void onItemClick(AdapterView<?> parent, View view, int position, long id)
						{
							ImageView image = (ImageView) view.findViewById(R.id.INVITE_MEMBER_LIST_IMAGE_ACCEPT);
							HashMap<String, Object> selectedPerson = getSelectedPerson((TextView) view.findViewById(R.id.INVITE_MEMBER_LIST_USER_EMAIL));
							
							if(image.getVisibility() == View.VISIBLE)
							{
								image.setVisibility(View.INVISIBLE);
								
								selectedPerson.put(TAG_IS_SELECTED, false);
								personList.set(personList.indexOf(selectedPerson), selectedPerson);
							}
							else if(image.getVisibility() == View.INVISIBLE)
							{
								image.setVisibility(View.VISIBLE);
								
								selectedPerson.put(TAG_IS_SELECTED, true);
								personList.set(personList.indexOf(selectedPerson), selectedPerson);
							}
						}
						
						private HashMap<String, Object> getSelectedPerson(TextView emailOfSelectedPerson)
						{
							HashMap<String, Object> selectedPerson = new HashMap<String, Object>();
							int p;
							
							for(p=0; p<personList.size(); p++)
								if(personList.get(p).get(TAG_EMAIL).equals(emailOfSelectedPerson.getText().toString()))
									selectedPerson = personList.get(p);
							
							return selectedPerson;
						}
					});
				}
			});
		}
		
		private void loadLayout()
		{
			getViews();
			setTexts();
			setBackgrounds();
		}
		
		private void getViews()
		{
			inviteButton = (Button) findViewById(R.id.INVITE_MEMBER_LIST_INVITE_BUTTON);
			cancelButton = (Button) findViewById(R.id.INVITE_MEMBER_LIST_CANCEL_BUTTON);
			horizontalLine = findViewById(R.id.INVITE_MEMBER_LIST_HORIZONTAL_LINE);
		}
		
		private void setTexts()
		{
			inviteButton.setText(getString(R.string.LIST_INVITE));
			cancelButton.setText(getString(R.string.LIST_CANCEL));
		}
		
		private void setBackgrounds()
		{
			inviteButton.setBackgroundColor(getResources().getColor(android.R.color.transparent));		
			cancelButton.setBackgroundColor(getResources().getColor(android.R.color.transparent));
			horizontalLine.setBackgroundColor(getResources().getColor(android.R.color.black));
		}
	}

	class PersonListInvite extends AsyncTask<String, String, String>
	{
		@Override
		protected void onPreExecute()
		{
			super.onPreExecute();
			pDialog = new ProgressDialog(ListInviteMemberController.this);
			pDialog.setMessage(IMessages.INVITING_MEMBER);
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(false);
			pDialog.show();
		}

		protected String doInBackground(String... params)
		{
			return null;
		}

		protected void onPostExecute(String message)
		{
			pDialog.dismiss();
			
			if(message != null)
			{
				Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
			}
		}
	}
}