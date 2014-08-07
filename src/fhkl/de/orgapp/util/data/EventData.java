package fhkl.de.orgapp.util.data;

/**
 * EventData - Stores the event data for usage in the entire application
 * 
 * @author Oliver Neubauer
 * @version 1.0
 *
 */

public class EventData {
	private static String EVENTID = "";
	private static String PERSONID = "";
	private static String GROUPID = "";
	private static String NAME = "";
	private static String EVENTDATE = "";
	private static String EVENTTIME = "";
	private static String EVENTLOCATION = "";
	private static String REGULARITY = "";
	private static boolean BACK = false;
	
	/**
	 * @return the eVENTID
	 */
	public static String getEVENTID() {
		return EVENTID;
	}
	/**
	 * @param eVENTID the eVENTID to set
	 */
	public static void setEVENTID(String eVENTID) {
		EVENTID = eVENTID;
	}
	/**
	 * @return the pERSONID
	 */
	public static String getPERSONID() {
		return PERSONID;
	}
	/**
	 * @param pERSONID the pERSONID to set
	 */
	public static void setPERSONID(String pERSONID) {
		PERSONID = pERSONID;
	}
	/**
	 * @return the gROUPID
	 */
	public static String getGROUPID() {
		return GROUPID;
	}
	/**
	 * @param gROUPID the gROUPID to set
	 */
	public static void setGROUPID(String gROUPID) {
		GROUPID = gROUPID;
	}
	/**
	 * @return the nAME
	 */
	public static String getNAME() {
		return NAME;
	}
	/**
	 * @param nAME the nAME to set
	 */
	public static void setNAME(String nAME) {
		NAME = nAME;
	}
	/**
	 * @return the eVENTDATE
	 */
	public static String getEVENTDATE() {
		return EVENTDATE;
	}
	/**
	 * @param eVENTDATE the eVENTDATE to set
	 */
	public static void setEVENTDATE(String eVENTDATE) {
		EVENTDATE = eVENTDATE;
	}
	/**
	 * @return the eVENTTIME
	 */
	public static String getEVENTTIME() {
		return EVENTTIME;
	}
	/**
	 * @param eVENTTIME the eVENTTIME to set
	 */
	public static void setEVENTTIME(String eVENTTIME) {
		EVENTTIME = eVENTTIME;
	}
	/**
	 * @return the eVENTLOCATION
	 */
	public static String getEVENTLOCATION() {
		return EVENTLOCATION;
	}
	/**
	 * @param eVENTLOCATION the eVENTLOCATION to set
	 */
	public static void setEVENTLOCATION(String eVENTLOCATION) {
		EVENTLOCATION = eVENTLOCATION;
	}
	/**
	 * @return the rEGULARITY
	 */
	public static String getREGULARITY() {
		return REGULARITY;
	}
	/**
	 * @param rEGULARITY the rEGULARITY to set
	 */
	public static void setREGULARITY(String rEGULARITY) {
		REGULARITY = rEGULARITY;
	}
	/**
	 * @return the bACK
	 */
	public static boolean isBACK() {
		return BACK;
	}
	/**
	 * @param bACK the bACK to set
	 */
	public static void setBACK(boolean bACK) {
		BACK = bACK;
	}
}