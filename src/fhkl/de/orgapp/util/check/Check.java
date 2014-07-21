package fhkl.de.orgapp.util.check;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;
import fhkl.de.orgapp.util.JSONParser;
import fhkl.de.orgapp.util.UserData;

public class Check {

	static JSONParser jsonParser = new JSONParser();

	private static String URL_GET_PERSON_IN_EVENT = "http://pushrply.com/get_person_in_event.php";

	private static final String TAG_SUCCESS = "success";

	public static boolean attendingMember(String eventId) {

		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("personId", UserData.getPERSONID()));
		params.add(new BasicNameValuePair("eventId", eventId));
		JSONObject json = jsonParser.makeHttpRequest(URL_GET_PERSON_IN_EVENT, "GET", params);

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
