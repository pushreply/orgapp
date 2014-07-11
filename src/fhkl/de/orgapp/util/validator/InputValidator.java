package fhkl.de.orgapp.util.validator;

import java.text.ParseException;
import java.text.SimpleDateFormat;

public class InputValidator {
	public static boolean isEmailValid(String eMail) {
		if (eMail == null)
			return false;

		if (!eMail.matches("[A-Z0-9a-z._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,4}"))
			return false;

		return true;
	}

	public static boolean isStringLengthInRange(String value, int minLength, int maxLength) {
		if (value == null)
			return false;

		if (value.length() == minLength || value.length() > maxLength)
			return false;

		return true;
	}

	public static boolean isDateValid(String date, String dateFormat) {
		if (date == null)
			return false;

		SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
		sdf.setLenient(false);

		try {
			sdf.parse(date);
		} catch (ParseException e) {
			return false;
		}

		return true;
	}

	public static boolean isNumberValid(String number) {
		if (number == null)
			return false;

		try {
			Integer.valueOf(number);
		} catch (NumberFormatException e) {
			return false;
		}
		return true;
	}
}