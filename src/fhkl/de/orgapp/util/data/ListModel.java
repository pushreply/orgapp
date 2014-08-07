package fhkl.de.orgapp.util.data;

/**
 * ListModel - Stores some event attributes for the event list to use it in the entire application
 * 
 * @author Oliver Neubauer
 * @version 1.0
 *
 */

public class ListModel {

	private String eventId;
	private String eventName;
	private String eventDate;
	private String eventTime;
	private Integer attending;
	
	/**
	 * @return the eventId
	 */
	public String getEventId() {
		return eventId;
	}
	/**
	 * @param eventId the eventId to set
	 */
	public void setEventId(String eventId) {
		this.eventId = eventId;
	}
	/**
	 * @return the eventName
	 */
	public String getEventName() {
		return eventName;
	}
	/**
	 * @param eventName the eventName to set
	 */
	public void setEventName(String eventName) {
		this.eventName = eventName;
	}
	/**
	 * @return the eventDate
	 */
	public String getEventDate() {
		return eventDate;
	}
	/**
	 * @param eventDate the eventDate to set
	 */
	public void setEventDate(String eventDate) {
		this.eventDate = eventDate;
	}
	/**
	 * @return the eventTime
	 */
	public String getEventTime() {
		return eventTime;
	}
	/**
	 * @param eventTime the eventTime to set
	 */
	public void setEventTime(String eventTime) {
		this.eventTime = eventTime;
	}
	/**
	 * @return the attending
	 */
	public Integer getAttending() {
		return attending;
	}
	/**
	 * @param attending the attending to set
	 */
	public void setAttending(Integer attending) {
		this.attending = attending;
	}
}