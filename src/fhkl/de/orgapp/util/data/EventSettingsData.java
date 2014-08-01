package fhkl.de.orgapp.util.data;

public class EventSettingsData
{
	private static String EVENT_SETTINGS_ID = "";
	private static String SHOWN_EVENT_ENTRIES = "";

	/**
	 * @return the eVENT_SETTINGS_ID
	 */
	
	public static String getEVENT_SETTINGS_ID()
	{
		return EVENT_SETTINGS_ID;
	}

	/**
	 * @param eVENT_SETTINGS_ID the eVENT_SETTINGS_ID to set
	 */
	
	public static void setEVENT_SETTINGS_ID(String eVENT_SETTINGS_ID)
	{
		EVENT_SETTINGS_ID = eVENT_SETTINGS_ID;
	}

	/**
	 * @return the sHOWN_EVENT_ENTRIES
	 */
	
	public static String getSHOWN_EVENT_ENTRIES()
	{
		return SHOWN_EVENT_ENTRIES;
	}

	/**
	 * @param sHOWN_EVENT_ENTRIES the sHOWN_EVENT_ENTRIES to set
	 */
	
	public static void setSHOWN_EVENT_ENTRIES(String sHOWN_EVENT_ENTRIES)
	{
		SHOWN_EVENT_ENTRIES = sHOWN_EVENT_ENTRIES;
	}
}