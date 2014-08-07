package fhkl.de.orgapp.util.data;

/**
 * MemberData - Stores the data of group member for usage in the entire application
 * 
 * @author Oliver Neubauer
 * @version 1.0
 *
 */

public class MemberData {

	private static String PERSONID = "";
	private static String FIRST_NAME = "";
	private static String LAST_NAME = "";
	private static String BIRTHDAY = "";
	private static String GENDER = "";
	private static String EMAIL = "";
	private static String MEMBER_SINCE = "";

	private static String PRIVILEGE_INVITE_MEMBER = "";
	private static String PRIVILEGE_EDIT_MEMBERLIST = "";
	private static String PRIVILEGE_CREATE_EVENT = "";
	private static String PRIVILEGE_EDIT_EVENT = "";
	private static String PRIVILEGE_DELETE_EVENT = "";
	private static String PRIVILEGE_EDIT_COMMENT = "";
	private static String PRIVILEGE_DELETE_COMMENT = "";
	private static String PRIVILEGE_MANAGEMENT = "";
	
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
	 * @return the fIRST_NAME
	 */
	public static String getFIRST_NAME() {
		return FIRST_NAME;
	}
	/**
	 * @param fIRST_NAME the fIRST_NAME to set
	 */
	public static void setFIRST_NAME(String fIRST_NAME) {
		FIRST_NAME = fIRST_NAME;
	}
	/**
	 * @return the lAST_NAME
	 */
	public static String getLAST_NAME() {
		return LAST_NAME;
	}
	/**
	 * @param lAST_NAME the lAST_NAME to set
	 */
	public static void setLAST_NAME(String lAST_NAME) {
		LAST_NAME = lAST_NAME;
	}
	/**
	 * @return the bIRTHDAY
	 */
	public static String getBIRTHDAY() {
		return BIRTHDAY;
	}
	/**
	 * @param bIRTHDAY the bIRTHDAY to set
	 */
	public static void setBIRTHDAY(String bIRTHDAY) {
		BIRTHDAY = bIRTHDAY;
	}
	/**
	 * @return the gENDER
	 */
	public static String getGENDER() {
		return GENDER;
	}
	/**
	 * @param gENDER the gENDER to set
	 */
	public static void setGENDER(String gENDER) {
		GENDER = gENDER;
	}
	/**
	 * @return the eMAIL
	 */
	public static String getEMAIL() {
		return EMAIL;
	}
	/**
	 * @param eMAIL the eMAIL to set
	 */
	public static void setEMAIL(String eMAIL) {
		EMAIL = eMAIL;
	}
	/**
	 * @return the mEMBER_SINCE
	 */
	public static String getMEMBER_SINCE() {
		return MEMBER_SINCE;
	}
	/**
	 * @param mEMBER_SINCE the mEMBER_SINCE to set
	 */
	public static void setMEMBER_SINCE(String mEMBER_SINCE) {
		MEMBER_SINCE = mEMBER_SINCE;
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