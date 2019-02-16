/*====================================================================*\

PropertyString.java

Property string class.

\*====================================================================*/


// PACKAGE


package common.misc;

//----------------------------------------------------------------------


// IMPORTS


import java.io.File;

import java.util.ArrayList;
import java.util.List;

//----------------------------------------------------------------------


// PROPERTY STRING CLASS


public class PropertyString
{

////////////////////////////////////////////////////////////////////////
//  Constants
////////////////////////////////////////////////////////////////////////

	public static final		String	KEY_PREFIX	= "${";
	public static final		String	KEY_SUFFIX	= "}";

	public static final		char	KEY_SEPARATOR_CHAR	= '.';
	public static final		String	KEY_SEPARATOR		= Character.toString(KEY_SEPARATOR_CHAR);

	public static final		String	USER_HOME_PREFIX	= "~";

	private static final	String	CONFIGURATION_NAME	= "blankaspect";

	private static final	int		KEY_PREFIX_LENGTH	= KEY_PREFIX.length();
	private static final	int		KEY_SUFFIX_LENGTH	= KEY_SUFFIX.length();

	private static final	String	ENV_PREFIX_KEY	= "app" + KEY_SEPARATOR + "envPrefix";
	private static final	String	SYS_PREFIX_KEY	= "app" + KEY_SEPARATOR + "sysPrefix";

	private static final	String	DEFAULT_ENV_PREFIX	= "env" + KEY_SEPARATOR;
	private static final	String	DEFAULT_SYS_PREFIX	= "sys" + KEY_SEPARATOR;

////////////////////////////////////////////////////////////////////////
//  Member classes : non-inner classes
////////////////////////////////////////////////////////////////////////


	// SPAN CLASS


	public static class Span
	{

	////////////////////////////////////////////////////////////////////
	//  Constants
	////////////////////////////////////////////////////////////////////

		public enum Kind
		{
			UNKNOWN,
			LITERAL,
			ENVIRONMENT,
			SYSTEM
		}

	////////////////////////////////////////////////////////////////////
	//  Constructors
	////////////////////////////////////////////////////////////////////

		public Span()
		{
			kind = Kind.UNKNOWN;
		}

		//--------------------------------------------------------------

		public Span(Kind   kind,
					String key,
					String value)
		{
			this.kind = kind;
			this.key = key;
			this.value = value;
		}

		//--------------------------------------------------------------

	////////////////////////////////////////////////////////////////////
	//  Instance fields
	////////////////////////////////////////////////////////////////////

		public	Kind	kind;
		public	String	key;
		public	String	value;

	}

	//==================================================================

////////////////////////////////////////////////////////////////////////
//  Constructors
////////////////////////////////////////////////////////////////////////

	private PropertyString()
	{
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Class methods
////////////////////////////////////////////////////////////////////////

	public static String getEnvPrefix()
	{
		try
		{
			String value = System.getProperty(ENV_PREFIX_KEY);
			if (value != null)
				envPrefix = value.endsWith(KEY_SEPARATOR) ? value : value + KEY_SEPARATOR;
		}
		catch (SecurityException e)
		{
			// ignore
		}
		return envPrefix;
	}

	//------------------------------------------------------------------

	public static String getSysPrefix()
	{
		try
		{
			String value = System.getProperty(SYS_PREFIX_KEY);
			if (value != null)
				sysPrefix = value.endsWith(KEY_SEPARATOR) ? value : value + KEY_SEPARATOR;
		}
		catch (SecurityException e)
		{
			// ignore
		}
		return sysPrefix;
	}

	//------------------------------------------------------------------

	public static String getConfigurationName()
	{
		return CONFIGURATION_NAME;
	}

	//------------------------------------------------------------------

	public static String concatenateKeys(CharSequence... keys)
	{
		// Calculate length of buffer
		int length = -1;
		for (CharSequence key : keys)
			length += key.length() + 1;

		// Concatenate keys
		StringBuilder buffer = new StringBuilder(length);
		for (int i = 0; i < keys.length; i++)
		{
			if (i > 0)
				buffer.append(KEY_SEPARATOR_CHAR);
			buffer.append(keys[i]);
		}
		return buffer.toString();
	}

	//------------------------------------------------------------------

	public static List<Span> getSpans(String str)
	{
		// Get environment and system prefixes
		getEnvPrefix();
		getSysPrefix();

		// Create list of spans
		List<Span> spans = new ArrayList<>();
		int index = 0;
		while (index < str.length())
		{
			Span.Kind spanKind = Span.Kind.UNKNOWN;
			String spanKey = null;
			String spanValue = null;
			int startIndex = index;
			index = str.indexOf(KEY_PREFIX, index);
			if (index < 0)
			{
				index = str.length();
				if (index > startIndex)
				{
					spanKind = Span.Kind.LITERAL;
					spanValue = str.substring(startIndex);
				}
			}
			else
			{
				index = str.indexOf(KEY_SUFFIX, index + KEY_PREFIX_LENGTH);
				if (index < 0)
				{
					index = str.length();
					if (index > startIndex)
					{
						spanKind = Span.Kind.LITERAL;
						spanValue = str.substring(startIndex);
					}
				}
				else
				{
					startIndex += KEY_PREFIX_LENGTH;
					String key = str.substring(startIndex, index);
					try
					{
						if (key.startsWith(envPrefix))
						{
							spanKind = Span.Kind.ENVIRONMENT;
							spanKey = key.substring(envPrefix.length());
							spanValue = System.getenv(spanKey);
						}
						else if (key.startsWith(sysPrefix))
						{
							spanKind = Span.Kind.SYSTEM;
							spanKey = key.substring(sysPrefix.length());
							spanValue = System.getProperty(spanKey);
						}
						else
						{
							spanKey = key;
							if (!key.isEmpty())
							{
								String value = System.getProperty(key);
								if (value == null)
								{
									value = System.getenv(key);
									if (value != null)
									{
										spanKind = Span.Kind.ENVIRONMENT;
										spanValue = value;
									}
								}
								else
								{
									spanKind = Span.Kind.SYSTEM;
									spanValue = value;
								}
							}
						}
					}
					catch (SecurityException e)
					{
						// ignore
					}
					index += KEY_SUFFIX_LENGTH;
				}
			}
			spans.add(new Span(spanKind, spanKey, spanValue));
		}

		return spans;
	}

	//------------------------------------------------------------------

	public static String parse(List<Span> spans)
	{
		StringBuilder buffer = new StringBuilder(256);
		for (Span span : spans)
		{
			if (span.value != null)
				buffer.append(span.value);
		}
		return buffer.toString();
	}

	//------------------------------------------------------------------

	public static String parse(String str)
	{
		return parse(getSpans(str));
	}

	//------------------------------------------------------------------

	public static String parsePathname(String str)
	{
		if (str.startsWith(USER_HOME_PREFIX))
		{
			int prefixLength = USER_HOME_PREFIX.length();
			if ((str.length() == prefixLength) || (str.charAt(prefixLength) == File.separatorChar)
				|| (str.charAt(prefixLength) == '/'))
			try
			{
				String pathname = SystemUtils.getUserHomePathname();
				if (pathname != null)
					str = pathname + str.substring(prefixLength);
			}
			catch (SecurityException e)
			{
				// ignore
			}
		}
		return parse(str);
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Class fields
////////////////////////////////////////////////////////////////////////

	private static	String	envPrefix	= DEFAULT_ENV_PREFIX;
	private static	String	sysPrefix	= DEFAULT_SYS_PREFIX;

}

//----------------------------------------------------------------------
