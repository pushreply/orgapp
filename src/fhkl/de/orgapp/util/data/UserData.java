package fhkl.de.orgapp.util.data;

/**
 * UserData - Stores the user data for usage in the entire application
 * 
 * @author Ronaldo Hasiholan, Oliver Neubauer, Jochen Jung
 * @version 1.0
 * 
 */

public class UserData {
	private static String PERSONID = "";
	private static String FIRST_NAME = "";
	private static String LAST_NAME = "";
	private static String BIRTHDAY = "";
	private static String GENDER = "";
	private static String EMAIL = "";
	private static String PASSWORD = "";
	private static String MEMBER_SINCE = "";

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
	 * @return the pASSWORD
	 */
	public static String getPASSWORD() {
		return PASSWORD;
	}

	/**
	 * @param pASSWORD the pASSWORD to set
	 */
	public static void setPASSWORD(String pASSWORD) {
		PASSWORD = pASSWORD;
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
}