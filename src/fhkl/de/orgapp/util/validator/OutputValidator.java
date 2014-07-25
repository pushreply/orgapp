package fhkl.de.orgapp.util.validator;

public class OutputValidator
{
	public static boolean isUserBirthdaySet(String userBirthdayString)
	{
		if(userBirthdayString == null || userBirthdayString.equals("") || userBirthdayString.equalsIgnoreCase("0000-00-00"))
			return false;
		
		return true;
	}
	
	public static boolean isNotificationSettingsShownEntriesSet(String shownEntries)
	{
		if(shownEntries == null || shownEntries.equals("") || shownEntries.equalsIgnoreCase("null"))
			return false;
		
		return true;
	}
}