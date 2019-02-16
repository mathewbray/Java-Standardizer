/*====================================================================*\

DateUtils.java

Date utility methods class.

\*====================================================================*/


// PACKAGE


package common.misc;

//----------------------------------------------------------------------


// IMPORTS


import java.text.SimpleDateFormat;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

//----------------------------------------------------------------------


// DATE UTILITY METHODS CLASS


public class DateUtils
{

////////////////////////////////////////////////////////////////////////
//  Constants
////////////////////////////////////////////////////////////////////////

	public static final	int	NUM_MONTH_NAMES	= 12;
	public static final	int	NUM_DAY_NAMES	= 7;

////////////////////////////////////////////////////////////////////////
//  Constructors
////////////////////////////////////////////////////////////////////////

	/**
	 * Prevents this class from being instantiated externally.
	 */

	private DateUtils()
	{
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Class methods
////////////////////////////////////////////////////////////////////////

	public static List<String> getMonthNames()
	{
		return Collections.unmodifiableList(monthNames);
	}

	//------------------------------------------------------------------

	public static List<String> getMonthNames(Locale locale)
	{
		return getMonthNames(locale, 0);
	}

	//------------------------------------------------------------------

	public static List<String> getMonthNames(Locale locale,
											 int    length)
	{
		List<String> names = new ArrayList<>();
		SimpleDateFormat format = new SimpleDateFormat("MMMM", locale);
		Calendar date = new ModernCalendar(1970, 0, 1);
		for (int i = 0; i < NUM_MONTH_NAMES; i++)
		{
			String str = format.format(date.getTime());
			if (length > 0)
				str = str.substring(0, length);
			names.add(str);
			date.add(Calendar.MONTH, 1);
		}
		return names;
	}

	//------------------------------------------------------------------

	public static List<String> getDayNames()
	{
		return Collections.unmodifiableList(dayNames);
	}

	//------------------------------------------------------------------

	public static List<String> getDayNames(Locale locale)
	{
		return getDayNames(locale, 0);
	}

	//------------------------------------------------------------------

	public static List<String> getDayNames(Locale locale,
										   int    length)
	{
		List<String> names = new ArrayList<>();
		SimpleDateFormat format = new SimpleDateFormat("EEEE", locale);
		Calendar date = new ModernCalendar(1970, 0, 4);
		for (int i = 0; i < NUM_DAY_NAMES; i++)
		{
			String str = format.format(date.getTime());
			if (length > 0)
				str = str.substring(0, length);
			names.add(str);
			date.add(Calendar.DAY_OF_MONTH, 1);
		}
		return names;
	}

	//------------------------------------------------------------------

	public static void setMonthNames(List<String> names)
	{
		if ((names == null) || (names.size() != NUM_MONTH_NAMES))
			throw new IllegalArgumentException();

		monthNames.clear();
		monthNames.addAll(names);
	}

	//------------------------------------------------------------------

	public static void setDayNames(List<String> names)
	{
		if ((names == null) || (names.size() != NUM_DAY_NAMES))
			throw new IllegalArgumentException();

		dayNames.clear();
		dayNames.addAll(names);
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Class fields
////////////////////////////////////////////////////////////////////////

	private static	List<String>	monthNames	= getMonthNames(Locale.getDefault());
	private static	List<String>	dayNames	= getDayNames(Locale.getDefault());

}

//----------------------------------------------------------------------
