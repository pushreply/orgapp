package fhkl.de.orgapp.util;

public class CommentData {
	private static String COMMENTID = "";
	private static String PERSONID = "";
	private static String EVENTID = "";
	private static String COMMENT = "";
	private static String CLASSIFICATION = "";
	private static boolean BACK = false;
	private static int SHOWCOMMENT = 1;
	private static int ADDCOMMENT = 1;
	private static int UPDATECOMMENT = 1;
	private static int DELETECOMMENT = 1;
	
	
	//etwas uebertreiben :-D
	public static int getSHOWCOMMENT() {
		return SHOWCOMMENT;
	}
	public static int getADDCOMMENT() {
		return ADDCOMMENT;
	}
	public static int getUPDATECOMMENT() {
		return UPDATECOMMENT;
	}
	public static int getDELETECOMMENT() {
		return DELETECOMMENT;
	}
	public static String getCOMMENTID() {
		return COMMENTID;
	}
	public static void setCOMMENTID(String cOMMENTID) {
		COMMENTID = cOMMENTID;
	}
	public static String getPERSONID() {
		return PERSONID;
	}
	public static void setPERSONID(String pERSONID) {
		PERSONID = pERSONID;
	}
	public static String getEVENTID() {
		return EVENTID;
	}
	public static void setEVENTID(String eVENTID) {
		EVENTID = eVENTID;
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
