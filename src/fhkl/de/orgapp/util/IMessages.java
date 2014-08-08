package fhkl.de.orgapp.util;

/**
 * IMessages - Defines all constants to display them. Grouped by message type
 * 
 * @author Ronaldo Hasiholan, Jochen Jung, Oliver Neubauer
 * @version 5.1
 * 
 */

public interface IMessages {
	/**
	 * Messages for buttons
	 */

	public static final class DialogButton {
		public static final String CANCEL = "Cancel";
		public static final String DELETE_GENERIC = "Delete";
		public static final String EDIT_GENERIC = "Edit";
		public static final String LIST = "List";
		public static final String MANUALLY = "Manual";
		public static final String NEW_GENERIC = "New";
		public static final String NO = "No";
		public static final String NO_MEMBER_INVITE = "No member invite";
		public static final String OK = "Ok";
		public static final String SHARE_EVENT_VIA_FACEBOOK = "On Facebook";
		public static final String SHARE_EVENT_VIA_TWITTER = "On Twitter";
		public static final String YES = "Yes";
	}

	/**
	 * Messages for errors
	 */

	public static final class Error {
		public static final String DUPLICATE_EMAIL = "E-mail address duplicated";
		public static final String DUPLICATE_PERSON = "Person already exists";
		public static final String EMPTY_COMMENT = "You don't write any comment yet.";
		public static final String EXIST_USER = "User does not exist: ";
		public static final String GROUP_NOT_DELETED = "Group was not deleted";
		public static final String INSUFFICIENT_PRIVILEGES = "Insufficient privileges";
		public static final String INVALID_COMMENT = "Invalid comment";
		public static final String INVALID_EMAIL = "Invalid e-mail address";
		public static final String INVALID_EVENTDATE = "Invalid event date. Choose a future date";
		public static final String INVALID_EVENTLOCATION = "Invalid event location";
		public static final String INVALID_EVENTTIME = "Invalid event time";
		public static final String INVALID_FIRSTNAME = "Invalid first name";
		public static final String INVALID_INFO = "Invalid Info";
		public static final String INVALID_LASTNAME = "Invalid last name";
		public static final String INVALID_NAME = "Invalid Name";
		public static final String INVALID_NUMBER = "Invalid number";
		public static final String INVALID_PASSWORD = "Invalid password";
		public static final String INVALID_RADIOGROUP_REGULARITY = "Choose an ending date or number of events";
		public static final String INVALID_REGULARITY_DATE = "Invalid date. Choose a future date";
		public static final String INVALID_REGULARITY_DATE_2 = "Invalid date. Event date should be before ending date";
		public static final String INVALID_REGULARITY_DATE_3 = "Invalid date. Ending date and recurring value does not match";
		public static final String INVALID_REGULARITY_NUMBER = "Invalid number. Choose a number between 2 and 50";
		public static final String INVALID_USER = "Invalid user";
		public static final String MISSING_EMAIL = "E-Mail address missing";
		public static final String NO_CHANGES_MADE = "No changes were made";
		public static final String NO_INTERNET_CONNECTION = "Please check your internet connection";
		public static final String NO_MEMBER_SELECTED = "Please select at least one person";
		public static final String NO_NUMBER_ENTERED = "Please enter a number";
		public static final String NUMBER_ZERO_NOT_ALLOW = "Number \"0\" is not allow";
		public static final String PASSWORDS_DO_NOT_MATCH = "Passwords do not match";
		public static final String PERSON_NOT_CREATED = "Error during creation of the person";
		public static final String PRIVATE_INFO_NOT_UPDATED = "You have to update your private information";
		public static final String PRIVILEGE_ADMIN = "Admin privileges cannot be altered";
		public static final String REMOVING_ADMIN = "Admin cannot be removed";
		public static final String REPORT_GENERIC = "Report inappropriate";
		public static final String REQUIRED_FIELDS_NOT_COMPLETE = "You have to fill out the required fields";
		public static final String SECURITY_INFO_NOT_UPDATED = "You have to update your security information";
		public static final String TOO_MANY_GENERA = "Please select at most one gender";
		public static final String UPDATE_WAS_NOT_SUCCESSFUL = "Update was not successful";
		public static final String USER_INVITED = "User was already invited";
	}

	/**
	 * Messages for user notifications
	 */

	public static final class Notification {
		public static final String CREATE_COMMENT_1 = "A new comment in the event \"";
		public static final String CREATE_COMMENT_2 = "\" was added.";
		public static final String DELETE_COMMENT_1 = "The following comment has been deleted: \"";
		public static final String DELETE_COMMENT_2 = "\"";
		public static final String DELETE_GROUP_NOTIFICATION_1 = "Group ";
		public static final String DELETE_GROUP_NOTIFICATION_2 = " has been deleted";
		public static final String EDIT_COMMENT_1 = "The following comment was changed from \"";
		public static final String EDIT_COMMENT_2 = "\" to \"";
		public static final String EDIT_COMMENT_3 = "\"";
		public static final String MESSAGE_CREATE_EVENT_1 = "In the group \"";
		public static final String MESSAGE_CREATE_EVENT_2 = "\" the following new event was added: ";
		public static final String MESSAGE_CREATE_EVENT_3 = "\" the following recurring new event was added: ";
		public static final String MESSAGE_CREATE_EVENT_4 = ". It expires on: ";
		public static final String MESSAGE_DELETE_EVENT_1 = "The following event has been deleted: \"";
		public static final String MESSAGE_DELETE_EVENT_2 = "\"";
		public static final String MESSAGE_INVITE = "You have been invited to the following group: ";
		public static final String NEW_NOTIFICATION = "New notification";
		public static final String NOTIFICATION_ADMIN_LEAVING_GROUP = "You are the only admin in this group. Please set a member to be an admin, otherwise you may not leave.";
		public static final String NOTIFICATION_LEAVING_GROUP = "A member left group ";
		public static final String NOTIFICATION_LEFT_GROUP = "You left the group ";
		public static final String YOU_HAVE_UNREAD_NOTIFICATION_1 = "You have ";
		public static final String YOU_HAVE_UNREAD_NOTIFICATION_2 = " unread notification";
	}

	/**
	 * Messages for security issue
	 */

	public static final class SecurityIssue {
		public static final String COMMENT = "Comment management menu";
		public static final String CONFIRM_LEAVING_GROUP = "Do you really want to leave group ";
		public static final String DELETE_COMMENT = "Delete comment";
		public static final String DELETE_EVENT = "Do you really want to delete this event?";
		public static final String EDIT_COMMENT = "Edit comment";
		public static final String MESSAGE_DELETE_GROUP = "Do you really want to delete the group ";
		public static final String NEW_COMMENT = "New comment";
		public static final String NOTIFICATION = "Notification";
		public static final String QUESTION_DELETE_MEMBER = "Do you really want to remove this member from the group?";
		public static final String QUESTION_MARK = "?";
		public static final String QUESTION_MEMBER = "Do you want do insert member manually or by list?";
		public static final String SHARE_CREATED_EVENT = "Do you want to share the created event?";
		public static final String SHARE_DELETED_EVENT = "Do you want to share the deleted event?";
		public static final String SHARE_EDITED_EVENT = "Do you want to share the edited event?";
		public static final String QUESTION_DELETE_COMMENT = "Do you really want to delete this comment?";
	}

	/**
	 * Messages for status
	 */

	public static final class Status {
		public static final String CHANGING_STATUS = "Changing status...";
		public static final String CHECKING_DATA = "Checking data...";
		public static final String CREATING_GROUP = "Creating group...";
		public static final String CREATING_PERSON = "Creating person...";
		public static final String DELETING_COMMENT = "Deleting comment...";
		public static final String DELETING_EVENT = "Deleting event...";
		public static final String DELETING_GROUP = "Deleting group...";
		public static final String INVITING_MEMBERS = "Inviting members...";
		public static final String LEAVING_GROUP = "Leaving group...";
		public static final String LOADING_CALENDAR = "Loading calendar...";
		public static final String LOADING_EVENT = "Loading event...";
		public static final String LOADING_EVENT_HISTORY = "Loading event history...";
		public static final String LOADING_GROUP = "Loading group...";
		public static final String LOADING_GROUPS = "Loading groups...";
		public static final String LOADING_INFO = "Loading member info...";
		public static final String LOADING_LIST = "Loading list...";
		public static final String LOADING_MEMBER_LIST = "Loading member list...";
		public static final String LOADING_NOTIFICATION_SETTINGS = "Loading notification settings...";
		public static final String LOADING_NOTIFICATIONS = "Loading notifications...";
		public static final String MEMBERLIST_EMPTY = "Memberlist is empty";
		public static final String MESSAGE_INVITED_PERSON = "You have been invited the following persons";
		public static final String REMOVING_MEMBER = "Removing member...";
		public static final String SAVING_COMMENT = "Saving comment...";
		public static final String SAVING_EVENT = "Saving event...";
		public static final String SAVING_GROUP = "Saving group...";
		public static final String SAVING_PRIVILEGES = "Saving privileges...";
		public static final String SAVING_SETTINGS = "Saving settings...";
		public static final String UPDATING = "Updating...";
		public static final String UPDATING_PRIVATE_INFO = "Updating private information...";
		public static final String UPDATING_SECURITY_INFO = "Updating security information...";
	}

	/**
	 * Messages for success
	 */

	public static final class Success
	{
		public static final String GROUP_SUCCESSFUL_DELETED = "You have deleted the group ";
		public static final String PERSON_SUCCESSFUL_CREATED = "You have created the person";
		public static final String UPDATE_WAS_SUCCESSFUL = "Update was successful";
	}
}