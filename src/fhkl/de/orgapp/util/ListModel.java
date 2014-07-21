package fhkl.de.orgapp.util;

public class ListModel {

	private String eventId;
	private String eventName;
	private String eventDate;
	private String eventTime;
	private Integer attending;

	public String getEventId() {
		return eventId;
	}

	public void setEventId(String eventId) {
		this.eventId = eventId;
	}

	public String getEventName() {
		return eventName;
	}

	public void setEventName(String eventName) {
		this.eventName = eventName;
	}

	public String getEventDate() {
		return eventDate;
	}

	public void setEventDate(String eventDate) {
		this.eventDate = eventDate;
	}

	public String getEventTime() {
		return eventTime;
	}

	public void setEventTime(String eventTime) {
		this.eventTime = eventTime;
	}

	public Integer getAttending() {
		return attending;
	}

	public void setAttending(Integer attending) {
		this.attending = attending;
	}
}
