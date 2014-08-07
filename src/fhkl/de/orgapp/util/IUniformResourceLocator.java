package fhkl.de.orgapp.util;

/**
 * IUniformResourceLocator - Defines all URLs to make database requests
 * 
 * @author Ronaldo Hasiholan, Jochen Jung, Oliver Neubauer
 * @version 1.0
 *
 */

public interface IUniformResourceLocator
{
	/**
	 * Defines the server names
	 */
	
	public static final class DomainName
	{
		// Name for https request
		public static final String SERVER_NAME = "https://pushrply.com/";
		// Name for http request
		public static final String SERVER_NAME_HTTP = "http://pushrply.com/";
	}

	/**
	 * Defines the entire URLs
	 */
	
	public static final class URL
	{
		public static final String URL_COMMENT = DomainName.SERVER_NAME + "PDO_CommentControl.php";
		public static final String URL_EVENT = DomainName.SERVER_NAME + "PDO_EventControl.php";
		public static final String URL_EVENTPERSON = DomainName.SERVER_NAME + "PDO_EventPersonControl.php";
		public static final String URL_EVENTPERSON_HTTP = DomainName.SERVER_NAME_HTTP + "PDO_EventPersonControl.php";
		public static final String URL_EVENTSETTINGS = DomainName.SERVER_NAME + "PDO_EventSettingsControl.php";
		public static final String URL_GROUPS = DomainName.SERVER_NAME + "PDO_GroupsControl.php";
		public static final String URL_NOTIFICATION = DomainName.SERVER_NAME + "PDO_NotificationsControl.php";
		public static final String URL_NOTIFICATION_HTTP = DomainName.SERVER_NAME_HTTP + "PDO_NotificationsControl.php";
		public static final String URL_NOTIFICATIONSETTINGS = DomainName.SERVER_NAME
						+ "PDO_NotificationsSettingsControl.php";
		public static final String URL_PERSON = DomainName.SERVER_NAME + "PDO_PersonControl.php";
		public static final String URL_PRIVILEGE = DomainName.SERVER_NAME + "PDO_PrivilegeControl.php";
	}
}