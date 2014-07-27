package fhkl.de.orgapp.util.data;

public class CommentData {
	private static String COMMENTID = "";

	private static String COMMENT = "";
	private static String CLASSIFICATION = "";
	private static boolean BACK = false;
	private static String ACTION = "";
	private static String ADDCOMMENT = "";
	private static String UPDATECOMMENT = "";
	private static String DELETECOMMENT = "";
	private static String COMMENTDATETIME ="";
	
	

	public static String getCOMMENTDATETIME() {
		return COMMENTDATETIME;
	}
	public static String getACTION() {
		return ACTION;
	}
	public static void setACTION(String sACTION) {
		ACTION = sACTION;
	}
	public static String getADDCOMMENT() {
		return ADDCOMMENT;
	}
	public static void setADDCOMMENT(String aDDCOMMENT) {
		ADDCOMMENT = aDDCOMMENT;
	}
	public static String getUPDATECOMMENT() {
		return UPDATECOMMENT;
	}
	public static void setUPDATECOMMENT(String uPDATECOMMENT) {
		UPDATECOMMENT = uPDATECOMMENT;
	}
	public static String getDELETECOMMENT() {
		return DELETECOMMENT;
	}
	public static void setDELETECOMMENT(String dELETECOMMENT) {
		DELETECOMMENT = dELETECOMMENT;
	}
	public static String getCOMMENTID() {
		return COMMENTID;
	}
	public static void setCOMMENTID(String cOMMENTID) {
		COMMENTID = cOMMENTID;
	}
	public static String getCOMMENT() {
		return COMMENT;
	}
	public static void setCOMMENT(String cOMMENT) {
		COMMENT = cOMMENT;
	}
	public static String getCLASSIFICATION() {
		return CLASSIFICATION;
	}
	public static void setCLASSIFICATION(String cLASSIFICATION) {
		CLASSIFICATION = cLASSIFICATION;
	}
	public static boolean isBACK() {
		return BACK;
	}
	public static void setBACK(boolean bACK) {
		BACK = bACK;
	}
	
	
}
