package fhkl.de.orgapp.util.check;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;
import fhkl.de.orgapp.util.IUniformResourceLocator;
import fhkl.de.orgapp.util.JSONParser;
import fhkl.de.orgapp.util.data.UserData;

public class UserJoinEventChecker {
	private static JSONParser jsonParser = new JSONParser();

	private static final String TAG_SUCCESS = "success";

	public boolean isMemberJoinedEvent(String eventId) {
		List<NameValuePair> params = new ArrayList<NameValuePair>();

		// Required parameters
		params.add(new BasicNameValuePair("do", "checkuserjoinedevent"));
		params.add(new BasicNameValuePair("personId", UserData.getPERSONID()));
		params.add(new BasicNameValuePair("eventId", eventId));

		// Check user joined event
		JSONObject json = jsonParser.makeHttpRequest(IUniformResourceLocator.URL.URL_EVENTPERSON_HTTP, "GET", params);

		Log.d("EventPerson: ", json.toString());

		try {
			int success = json.getInt(TAG_SUCCESS);

			if (success == 1) {
				return true;
			} else {
				return false;
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}

		return false;
	}
}