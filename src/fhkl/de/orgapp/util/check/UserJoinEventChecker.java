package fhkl.de.orgapp.util.check;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import fhkl.de.orgapp.util.IUniformResourceLocator;
import fhkl.de.orgapp.util.JSONParser;
import fhkl.de.orgapp.util.data.UserData;

/**
 * UserJoinEventChecker - Checks, whether the logged user joined in an event
 * 
 * @author Jochen Jung
 * @version 3.5
 * 
 */

public class UserJoinEventChecker {
	// For json issues
	private JSONParser jsonParser = new JSONParser();
	private static final String TAG_SUCCESS = "success";

	/**
	 * Makes the request to check, whether logged user joined the event
	 * 
	 * @param eventId the event id
	 * @return true, if logged user joined in the event, false otherwise
	 */
	public boolean isMemberJoinedEvent(String eventId) {
		List<NameValuePair> params = new ArrayList<NameValuePair>();

		// Required parameters
		params.add(new BasicNameValuePair("do", "checkuserjoinedevent"));
		params.add(new BasicNameValuePair("personId", UserData.getPERSONID()));
		params.add(new BasicNameValuePair("eventId", eventId));

		// Check logged user joined event
		JSONObject json = jsonParser.makeHttpRequest(IUniformResourceLocator.URL.URL_EVENTPERSON_HTTP, "GET", params);

		try {
			int success = json.getInt(TAG_SUCCESS);

			// In case of success
			if (success == 1) {
				return true;
			}
			// In case of no success
			else {
				return false;
			}
		}
		// In case of error
		catch (Exception e) {
			return false;
		}
	}
}