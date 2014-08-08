package fhkl.de.orgapp.util.validator;

/**
 * OutputValidator - Checks the data for correctness got from the database
 *
 * @author Oliver Neubauer
 * @version 1.0
 *
 */

public class OutputValidator
{
	/**
	 * Checks the birthday of an user
	 * 
	 * @param birthday the user birthday to be checked
	 * @return true if set, false otherwise
	 */
	
	public static boolean isUserBirthdaySet(String birthday)
	{
		if(birthday == null || birthday.equals("") || birthday.equalsIgnoreCase("null") || birthday.equalsIgnoreCase("0000-00-00"))
			return false;
		
		return true;
	}
	
	/**
	 * Checks the gender of an user
	 * 
	 * @param gender the user gender to be checked
	 * @return true if set, false otherwise
	 */
	
	public static boolean isUserGenderSet(String gender)
	{
		if(gender == null || gender.equals("") || gender.equals("-") || gender.equalsIgnoreCase("null"))
			return false;
		
		return true;
	}
	
	/**
	 * Checks the shown entries of the notification settings
	 * 
	 * @param shownEntries number of displayed notifications
	 * @return true if set, false otherwise
	 */
	
	public static boolean isNotificationSettingsShownEntriesSet(String shownEntries)
	{
		if(shownEntries == null || shownEntries.equals("") || shownEntries.equalsIgnoreCase("null"))
			return false;
		
		return true;
	}
	
	/**
	 * Checks the shown entries of the event settings
	 * 
	 * @param shownEntries number of displayed events
	 * @return true if set, false otherwise
	 */
	
	public static boolean isEventSettingsShownEntriesSet(String shownEntries)
	{
		if(shownEntries == null || shownEntries.equals("") || shownEntries.equalsIgnoreCase("null"))
			return false;
		
		return true;
	}
}