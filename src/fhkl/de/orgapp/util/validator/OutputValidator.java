package fhkl.de.orgapp.util.validator;

public class OutputValidator
{
	public static boolean isUserBirthdaySet(String birthday)
	{
		if(birthday == null || birthday.equals("") || birthday.equalsIgnoreCase("0000-00-00"))
			return false;
		
		return true;
	}
	
	public static boolean isUserGenderSet(String gender)
	{
		if(gender == null || gender.equals("") || gender.equals("-") || gender.equalsIgnoreCase("null"))
			return false;
		
		return true;
	}
	
	public static boolean isNotificationSettingsShownEntriesSet(String shownEntries)
	{
		if(shownEntries == null || shownEntries.equals("") || shownEntries.equalsIgnoreCase("null"))
			return false;
		
		return true;
	}
	
	public static boolean isEventSettingsShownEntriesSet(String shownEntries)
	{
		if(shownEntries == null || shownEntries.equals("") || shownEntries.equalsIgnoreCase("null"))
			return false;
		
		return true;
	}
}