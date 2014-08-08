package fhkl.de.orgapp.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;

/**
 * JSONParser - Handles the data to execute requests to the server
 * 
 * @author Ronaldo Hasiholan, Jochen Jung, Oliver Neubauer
 * @version 1.0
 *
 */

public class JSONParser
{
	// For the content of response
	static InputStream is = null;
	// Container for the result
	static JSONObject jObj = null;
	// For the parsing process
	static String json = "";

	/**
	 * Executes a HTTPS request.
	 * Offers methods GET and POST.
	 * Only method GET is used, currently
	 * 
	 * @param url the address for the request
	 * @param method the method for the request
	 * @param params the params, which are attached in the url
	 * @param ctx the context, which calls this method
	 * @return the returned data as JSON
	 */
	
	public JSONObject makeHttpsRequest(String url, String method, List<NameValuePair> params, Context ctx)
	{
		try
		{
			// POST method
			// Currently unused
			if (method == "POST")
			{
				DefaultHttpClient httpClient = new DefaultHttpClient();
				HttpPost httpPost = new HttpPost(url);
				httpPost.setEntity(new UrlEncodedFormEntity(params));

				HttpResponse httpResponse = httpClient.execute(httpPost);
				HttpEntity httpEntity = httpResponse.getEntity();
				is = httpEntity.getContent();

			}

			// GET method
			else if (method == "GET")
			{
				// Own HTTPS client
				MyHttpClient httpsClient = new MyHttpClient(ctx);
				
				// Formated parameters
				String paramString = URLEncodedUtils.format(params, "utf-8");
				// Attach the parameters at the address
				url += "?" + paramString;
				// Required object
				HttpGet httpGet = new HttpGet(url);
				// Execute the request
				HttpResponse httpResponse = httpsClient.execute(httpGet);
				// Get the response
				HttpEntity httpEntity = httpResponse.getEntity();
				// Get the content of the response
				is = httpEntity.getContent();
			}
		}
		catch (UnsupportedEncodingException e)
		{
			// Return an empty object in case of error
			return new JSONObject();
		}
		catch (ClientProtocolException e)
		{
			// Return an empty object in case of error
			return new JSONObject();
		}
		catch (IOException e)
		{
			// Return an empty object in case of error
			return new JSONObject();
		}

		try
		{
			// Read the content
			BufferedReader reader = new BufferedReader(new InputStreamReader(is, "iso-8859-1"), 8);
			// Object for the text
			StringBuilder sb = new StringBuilder();
			// Object for each line
			String line = null;
			
			// Read the lines
			while ((line = reader.readLine()) != null)
			{
				// Append lines to the text
				sb.append(line + "\n");
			}
			
			// Close inputstream
			is.close();
			// Convert text to string
			json = sb.toString();
		}
		catch (Exception e)
		{
			// Return an empty object in case of error
			return new JSONObject();
		}

		try
		{
			// Parse text to a JSON object
			jObj = new JSONObject(json);
		}
		catch (JSONException e)
		{
			// Return an empty object in case of error
			return new JSONObject();
		}

		// Return the JSON object
		return jObj;

	}

	/**
	 * Executes a HTTP request.
	 * Offers methods GET and POST.
	 * Only method GET is used, currently
	 * 
	 * @param url the address for the request
	 * @param method the method for the request
	 * @param params the params, which are attached in the url
	 * @return the returned data as JSON
	 */
	
	public JSONObject makeHttpRequest(String url, String method, List<NameValuePair> params)
	{
		try
		{
			// POST method
			// Currently unused
			if (method == "POST")
			{
				DefaultHttpClient httpClient = new DefaultHttpClient();
				HttpPost httpPost = new HttpPost(url);
				httpPost.setEntity(new UrlEncodedFormEntity(params));

				HttpResponse httpResponse = httpClient.execute(httpPost);
				HttpEntity httpEntity = httpResponse.getEntity();
				is = httpEntity.getContent();

			}
			// GET method
			else if (method == "GET")
			{
				// Default HTTP client
				DefaultHttpClient httpClient = new DefaultHttpClient();
				// Formated parameters
				String paramString = URLEncodedUtils.format(params, "utf-8");
				// Attach the parameters at the address
				url += "?" + paramString;
				// Required object
				HttpGet httpGet = new HttpGet(url);
				// Execute the request
				HttpResponse httpResponse = httpClient.execute(httpGet);
				// Get the response
				HttpEntity httpEntity = httpResponse.getEntity();
				// Get the content of the response
				is = httpEntity.getContent();
			}

		}
		catch (UnsupportedEncodingException e)
		{
			// Return an empty object in case of error
			return new JSONObject();
		}
		catch (ClientProtocolException e)
		{
			// Return an empty object in case of error
			return new JSONObject();
		}
		catch (IOException e)
		{
			// Return an empty object in case of error
			return new JSONObject();
		}

		try
		{
			// Read the content
			BufferedReader reader = new BufferedReader(new InputStreamReader(is, "iso-8859-1"), 8);
			// Object for the text
			StringBuilder sb = new StringBuilder();
			// Object for each line
			String line = null;
			
			// Read the lines
			while ((line = reader.readLine()) != null)
			{
				// Append lines to the text
				sb.append(line + "\n");
			}
			
			// Close inputstream
			is.close();
			// Convert text to string
			json = sb.toString();
		}
		catch (Exception e)
		{
			// Return an empty object in case of error
			return new JSONObject();
		}

		// try parse the string to a JSON object
		try
		{
			// Parse text to a JSON object
			jObj = new JSONObject(json);
		}
		catch (JSONException e)
		{
			// Return an empty object in case of error
			return new JSONObject();
		}

		// Return the JSON object
		return jObj;

	}
}