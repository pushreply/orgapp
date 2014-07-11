package fhkl.de.orgapp.util.validator;

public class OutputValidator
{
	public static boolean isUserBirthdaySet(String userBirthdayString)
	{
		if(userBirthdayString == null || userBirthdayString.equals("") || userBirthdayString.equalsIgnoreCase("0000-00-00"))
			return false;
		
		return true;
	}
}