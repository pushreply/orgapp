package fhkl.de.orgapp.util.validator;

import java.text.ParseException;
import java.text.SimpleDateFormat;

/**
 * InputValidator - Checks the data for correctness entered by the user
 * 
 * @author Oliver Neubauer
 * @version 1.0
 *
 */

public class InputValidator
{
	/**
	 * Checks the email input
	 * 
	 * @param eMail the users email to be checked
	 * @return true if email is valid, false otherwise
	 */
	
	public static boolean isEmailValid(String eMail)
	{
		if (eMail == null)
			return false;

		if (!eMail.matches("[A-Z0-9a-z._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,4}"))
			return false;

		return true;
	}

	/**
	 * Checks the length of a text
	 * 
	 * @param value the text to be checked
	 * @param minLength the minimal allow length
	 * @param maxLength the maximal allow length
	 * @return true if value is in range, false otherwise
	 */
	
	public static boolean isStringLengthInRange(String value, int minLength, int maxLength)
	{
		if (value == null)
			return false;

		if (value.length() == minLength || value.length() > maxLength)
			return false;

		return true;
	}

	/**
	 * Checks the date input
	 * 
	 * @param date the date to be checked
	 * @param dateFormat the allow date format
	 * @return true if date is valid, false otherwise
	 */
	
	public static boolean isDateValid(String date, String dateFormat)
	{
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

	/**
	 * Checks the number input
	 * 
	 * @param number the number to be checked
	 * @return true if number is valid, false otherwise
	 */
	
	public static boolean isNumberValid(String number)
	{
		if (number == null)
			return false;

		try
		{
			Integer.valueOf(number);
		}
		catch (NumberFormatException e)
		{
			return false;
		}
		
		return true;
	}
}