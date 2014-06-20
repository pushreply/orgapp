package fhkl.de.orgapp.util;

public class InputValidator
{
	public static boolean isEmailValid(String eMail)
	{
		if(eMail == null)
			return false;
		
		if(!eMail.matches("[A-Z0-9a-z._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,4}"))
			return false;
		
		return true;
	}
	
	public static boolean isStringLengthInRange(String value)
	{
		if(value == null)
			return false;
		
		if(value.length() == 0 || value.length() > 255)
			return false;
		
		return true;
	}
}