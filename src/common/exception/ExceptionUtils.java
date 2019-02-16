/*====================================================================*\

ExceptionUtils.java

Exception utilities class.

\*====================================================================*/


// PACKAGE


package common.exception;

//----------------------------------------------------------------------


// IMPORTS


import java.io.File;

import java.util.ArrayList;
import java.util.List;

import common.misc.SystemUtils;

//----------------------------------------------------------------------


// EXCEPTION UTILITIES CLASS


public class ExceptionUtils
{

////////////////////////////////////////////////////////////////////////
//  Constants
////////////////////////////////////////////////////////////////////////

	private static final	String	ELLIPSIS_STR		= "...";
	private static final	String	USER_HOME_PREFIX	= "~";

////////////////////////////////////////////////////////////////////////
//  Constructors
////////////////////////////////////////////////////////////////////////

	private ExceptionUtils()
	{
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Class methods
////////////////////////////////////////////////////////////////////////

	public static boolean isUnixStyle()
	{
		return unixStyle;
	}

	//------------------------------------------------------------------

	public static void setUnixStyle(boolean unixStyle)
	{
		ExceptionUtils.unixStyle = unixStyle;
	}

	//------------------------------------------------------------------

	public static String getPathname(File file)
	{
		String pathname = null;
		try
		{
			try
			{
				pathname = file.getCanonicalPath();
			}
			catch (Exception e)
			{
				pathname = file.getAbsolutePath();
			}
		}
		catch (SecurityException e)
		{
			writeError(e);
			pathname = file.getPath();
		}
		return pathname;
	}

	//------------------------------------------------------------------

	public static String getLimitedPathname(File file,
											int  maxLength)
	{
		String pathname = getPathname(file);
		if (unixStyle && (pathname != null))
		{
			try
			{
				String userHome = SystemUtils.getUserHomePathname();
				if ((userHome != null) && pathname.startsWith(userHome))
					pathname = USER_HOME_PREFIX + pathname.substring(userHome.length());
			}
			catch (SecurityException e)
			{
				// ignore
			}
			pathname = pathname.replace(File.separatorChar, '/');
		}

		return getLimitedPathname(pathname, maxLength);
	}

	//------------------------------------------------------------------

	public static String getLimitedPathname(String pathname,
											int    maxLength)
	{
		// Test for null pathname
		if (pathname == null)
			return null;

		// Split the pathname into its components
		char separatorChar = unixStyle ? '/' : File.separatorChar;
		List<String> strs = new ArrayList<>();
		int index = 0;
		while (index < pathname.length())
		{
			int startIndex = index;
			index = pathname.indexOf(separatorChar, index);
			if (index < 0)
				index = pathname.length();
			if (index > startIndex)
				strs.add(pathname.substring(startIndex, index));
			++index;
		}
		if (strs.isEmpty())
			return pathname;

		// Get the maximum number of components
		StringBuilder buffer = new StringBuilder(ELLIPSIS_STR);
		int numComponents = 0;
		for (int i = strs.size() - 1; i >= 0; i--)
		{
			buffer.append(separatorChar);
			buffer.append(strs.get(i));
			if (buffer.length() > maxLength)
				break;
			++numComponents;
		}

		// If last component is too wide, remove leading characters until it fits
		if (numComponents == 0)
		{
			String str = strs.get(strs.size() - 1);
			return (ELLIPSIS_STR + str.substring(Math.max(0, str.length() - maxLength +
																			ELLIPSIS_STR.length())));
		}

		// If the entire pathname fits, return it
		if (numComponents == strs.size())
			return pathname;

		// Construct a reduced pathname
		buffer = new StringBuilder(ELLIPSIS_STR);
		for (int i = strs.size() - numComponents; i < strs.size(); i++)
		{
			buffer.append(separatorChar);
			buffer.append(strs.get(i));
		}
		return buffer.toString();
	}

	//------------------------------------------------------------------

	private static void writeError(Exception e)
	{
		System.err.println(e);
		StackTraceElement[] stackTraceElements = e.getStackTrace();
		if (stackTraceElements.length > 0)
			System.err.println(stackTraceElements[0]);
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Class fields
////////////////////////////////////////////////////////////////////////

	private static	boolean	unixStyle;

}

//----------------------------------------------------------------------
