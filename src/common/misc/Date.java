/*====================================================================*\

Date.java

Date class.

\*====================================================================*/


// PACKAGE


package common.misc;

//----------------------------------------------------------------------


// IMPORTS


import java.util.Calendar;

//----------------------------------------------------------------------


// DATE CLASS


/**
 * This class encapsulates a calendar date of year, month and day of the month.
 */

public class Date
{

////////////////////////////////////////////////////////////////////////
//  Constants
////////////////////////////////////////////////////////////////////////

	public static final	int	NUM_YEAR_DIGITS		= 4;
	public static final	int	NUM_MONTH_DIGITS	= 2;
	public static final	int	NUM_DAY_DIGITS		= 2;

	public static final	char	SEPARATOR_CHAR	= '-';

////////////////////////////////////////////////////////////////////////
//  Constructors
////////////////////////////////////////////////////////////////////////

	/**
	 * Creates a date object for a specified year, month and day of the month.
	 *
	 * @param year   the year.
	 * @param month  the month, zero-based (eg, March = 2).
	 * @param day    the day of the month, zero-based.
	 */

	public Date(int year,
				int month,
				int day)
	{
		this.year = year;
		this.month = month;
		this.day = day;
	}

	//------------------------------------------------------------------

	public Date(Date date)
	{
		year = date.year;
		month = date.month;
		day = date.day;
	}

	//------------------------------------------------------------------

	public Date(Calendar date)
	{
		year = date.get(Calendar.YEAR);
		month = date.get(Calendar.MONTH);
		day = date.get(Calendar.DAY_OF_MONTH) - 1;
	}

	//------------------------------------------------------------------

	/**
	 * @param  str  a string representation of the date, which should have the form "yyyymmdd".
	 * @throws IllegalArgumentException
	 *           if {@code str} does not have 8 digits.
	 *         NumberFormatException
	 *           if one of the date components of {@code str} is not a valid number.
	 */

	public Date(String str)
	{
		if (str.length() != 8)
			throw new IllegalArgumentException();

		int offset = 0;
		int length = NUM_YEAR_DIGITS;
		year = Integer.parseInt(str.substring(offset, offset + length));
		offset += length;

		length = NUM_MONTH_DIGITS;
		month = Integer.parseInt(str.substring(offset, offset + length)) - 1;
		offset += length;

		length = NUM_DAY_DIGITS;
		day = Integer.parseInt(str.substring(offset)) - 1;
		offset += length;
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Instance methods : overriding methods
////////////////////////////////////////////////////////////////////////

	@Override
	public boolean equals(Object obj)
	{
		if (obj instanceof Date)
		{
			Date date = (Date)obj;
			return ((year == date.year) && (month == date.month) && (day == date.day));
		}
		return false;
	}

	//------------------------------------------------------------------

	@Override
	public int hashCode()
	{
		return ((year << 9) | (month << 5) | day);
	}

	//------------------------------------------------------------------

	@Override
	public String toString()
	{
		return (NumberUtils.uIntToDecString(year, 4, '0') + SEPARATOR_CHAR +
									NumberUtils.uIntToDecString(month + 1, 2, '0') + SEPARATOR_CHAR +
									NumberUtils.uIntToDecString(day + 1, 2, '0'));
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Instance methods
////////////////////////////////////////////////////////////////////////

	public boolean isValid(int minYear,
						   int maxYear)
	{
		if ((year < minYear) || (year > maxYear))
			return false;
		Calendar calendar = new ModernCalendar(year, 0, 1);
		if ((month < calendar.getActualMinimum(Calendar.MONTH)) ||
			 (month > calendar.getActualMaximum(Calendar.MONTH)))
			return false;
		calendar = new ModernCalendar(year, month, 1);
		int calendarDay = day + 1;
		return ((calendarDay >= calendar.getActualMinimum(Calendar.DAY_OF_MONTH)) &&
				 (calendarDay <= calendar.getActualMaximum(Calendar.DAY_OF_MONTH)));
	}

	//------------------------------------------------------------------

	public String toShortString()
	{
		return (NumberUtils.uIntToDecString(year, 4, '0') +
													NumberUtils.uIntToDecString(month + 1, 2, '0') +
													NumberUtils.uIntToDecString(day + 1, 2, '0'));
	}

	//------------------------------------------------------------------

	public ModernCalendar toCalendar()
	{
		return new ModernCalendar(year, month, day + 1);
	}

	//------------------------------------------------------------------

	public ModernCalendar toCalendar(Time time)
	{
		return new ModernCalendar(year, month, day + 1, time.hour, time.minute, time.second);
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Instance fields
////////////////////////////////////////////////////////////////////////

	public	int	year;
	public	int	month;
	public	int	day;

}

//----------------------------------------------------------------------
