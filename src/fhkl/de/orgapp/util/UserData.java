package fhkl.de.orgapp.util;

public class UserData
{
	private static String ID = "";
	private static String FIRST_NAME = "";
	private static String LAST_NAME = "";
	private static String BIRTHDAY = "";
	private static String GENDER = "";
	private static String EMAIL = "";
	private static String MEMBER_SINCE = "";
	
	public static String getID()
	{
		return ID;
	}
	
	public static void setID(String _ID)
	{
		ID = _ID;
	}
	
	public static String getFIRST_NAME()
	{
		return FIRST_NAME;
	}
	
	public static void setFIRST_NAME(String _FIRST_NAME)
	{
		FIRST_NAME = _FIRST_NAME;
	}
	
	public static String getLAST_NAME()
	{
		return LAST_NAME;
	}
	
	public static void setLAST_NAME(String _LAST_NAME)
	{
		LAST_NAME = _LAST_NAME;
	}
	
	public static String getBIRTHDAY()
	{
		return BIRTHDAY;
	}
	
	public static void setBIRTHDAY(String _BIRTHDAY)
	{
		BIRTHDAY = _BIRTHDAY;
	}
	
	public static String getGENDER()
	{
		return GENDER;
	}
	
	public static void setGENDER(String _GENDER)
	{
		GENDER = _GENDER;
	}
	public static String getEMAIL()
	{
		return EMAIL;
	}
	
	public static void setEMAIL(String _EMAIL)
	{
		EMAIL = _EMAIL;
	}
	
	public static String getMEMBER_SINCE()
	{
		return MEMBER_SINCE;
	}
	
	public static void setMEMBER_SINCE(String _MEMBER_SINCE)
	{
		MEMBER_SINCE = _MEMBER_SINCE;
	}
}