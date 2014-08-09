package fhkl.de.orgapp.util.data;

/**
 * GroupData - Stores the group data for usage in the entire application
 * 
 * @author Ronaldo Hasiholan, Jochen Jung
 * @version 3.5
 * 
 */

public class GroupData {

	private static String GROUPID = "";
	private static String PERSONID = "";
	private static String GROUPNAME = "";
	private static String GROUPINFO = "";

	private static String PRIVILEGE_INVITE_MEMBER = "";
	private static String PRIVILEGE_EDIT_MEMBERLIST = "";
	private static String PRIVILEGE_CREATE_EVENT = "";
	private static String PRIVILEGE_EDIT_EVENT = "";
	private static String PRIVILEGE_DELETE_EVENT = "";
	private static String PRIVILEGE_EDIT_COMMENT = "";
	private static String PRIVILEGE_DELETE_COMMENT = "";
	private static String PRIVILEGE_MANAGEMENT = "";

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
	 * @return the gROUPNAME
	 */
	public static String getGROUPNAME() {
		return GROUPNAME;
	}

	/**
	 * @param gROUPNAME the gROUPNAME to set
	 */
	public static void setGROUPNAME(String gROUPNAME) {
		GROUPNAME = gROUPNAME;
	}

	/**
	 * @return the gROUPINFO
	 */
	public static String getGROUPINFO() {
		return GROUPINFO;
	}

	/**
	 * @param gROUPINFO the gROUPINFO to set
	 */
	public static void setGROUPINFO(String gROUPINFO) {
		GROUPINFO = gROUPINFO;
	}

	/**
	 * @return the pRIVILEGE_INVITE_MEMBER
	 */
	public static String getPRIVILEGE_INVITE_MEMBER() {
		return PRIVILEGE_INVITE_MEMBER;
	}

	/**
	 * @param pRIVILEGE_INVITE_MEMBER the pRIVILEGE_INVITE_MEMBER to set
	 */
	public static void setPRIVILEGE_INVITE_MEMBER(String pRIVILEGE_INVITE_MEMBER) {
		PRIVILEGE_INVITE_MEMBER = pRIVILEGE_INVITE_MEMBER;
	}

	/**
	 * @return the pRIVILEGE_EDIT_MEMBERLIST
	 */
	public static String getPRIVILEGE_EDIT_MEMBERLIST() {
		return PRIVILEGE_EDIT_MEMBERLIST;
	}

	/**
	 * @param pRIVILEGE_EDIT_MEMBERLIST the pRIVILEGE_EDIT_MEMBERLIST to set
	 */
	public static void setPRIVILEGE_EDIT_MEMBERLIST(String pRIVILEGE_EDIT_MEMBERLIST) {
		PRIVILEGE_EDIT_MEMBERLIST = pRIVILEGE_EDIT_MEMBERLIST;
	}

	/**
	 * @return the pRIVILEGE_CREATE_EVENT
	 */
	public static String getPRIVILEGE_CREATE_EVENT() {
		return PRIVILEGE_CREATE_EVENT;
	}

	/**
	 * @param pRIVILEGE_CREATE_EVENT the pRIVILEGE_CREATE_EVENT to set
	 */
	public static void setPRIVILEGE_CREATE_EVENT(String pRIVILEGE_CREATE_EVENT) {
		PRIVILEGE_CREATE_EVENT = pRIVILEGE_CREATE_EVENT;
	}

	/**
	 * @return the pRIVILEGE_EDIT_EVENT
	 */
	public static String getPRIVILEGE_EDIT_EVENT() {
		return PRIVILEGE_EDIT_EVENT;
	}

	/**
	 * @param pRIVILEGE_EDIT_EVENT the pRIVILEGE_EDIT_EVENT to set
	 */
	public static void setPRIVILEGE_EDIT_EVENT(String pRIVILEGE_EDIT_EVENT) {
		PRIVILEGE_EDIT_EVENT = pRIVILEGE_EDIT_EVENT;
	}

	/**
	 * @return the pRIVILEGE_DELETE_EVENT
	 */
	public static String getPRIVILEGE_DELETE_EVENT() {
		return PRIVILEGE_DELETE_EVENT;
	}

	/**
	 * @param pRIVILEGE_DELETE_EVENT the pRIVILEGE_DELETE_EVENT to set
	 */
	public static void setPRIVILEGE_DELETE_EVENT(String pRIVILEGE_DELETE_EVENT) {
		PRIVILEGE_DELETE_EVENT = pRIVILEGE_DELETE_EVENT;
	}

	/**
	 * @return the pRIVILEGE_EDIT_COMMENT
	 */
	public static String getPRIVILEGE_EDIT_COMMENT() {
		return PRIVILEGE_EDIT_COMMENT;
	}

	/**
	 * @param pRIVILEGE_EDIT_COMMENT the pRIVILEGE_EDIT_COMMENT to set
	 */
	public static void setPRIVILEGE_EDIT_COMMENT(String pRIVILEGE_EDIT_COMMENT) {
		PRIVILEGE_EDIT_COMMENT = pRIVILEGE_EDIT_COMMENT;
	}

	/**
	 * @return the pRIVILEGE_DELETE_COMMENT
	 */
	public static String getPRIVILEGE_DELETE_COMMENT() {
		return PRIVILEGE_DELETE_COMMENT;
	}

	/**
	 * @param pRIVILEGE_DELETE_COMMENT the pRIVILEGE_DELETE_COMMENT to set
	 */
	public static void setPRIVILEGE_DELETE_COMMENT(String pRIVILEGE_DELETE_COMMENT) {
		PRIVILEGE_DELETE_COMMENT = pRIVILEGE_DELETE_COMMENT;
	}

	/**
	 * @return the pRIVILEGE_MANAGEMENT
	 */
	public static String getPRIVILEGE_MANAGEMENT() {
		return PRIVILEGE_MANAGEMENT;
	}

	/**
	 * @param pRIVILEGE_MANAGEMENT the pRIVILEGE_MANAGEMENT to set
	 */
	public static void setPRIVILEGE_MANAGEMENT(String pRIVILEGE_MANAGEMENT) {
		PRIVILEGE_MANAGEMENT = pRIVILEGE_MANAGEMENT;
	}
}