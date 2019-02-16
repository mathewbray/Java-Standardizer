/*====================================================================*\

NoYes.java

No-Yes option enumeration.

\*====================================================================*/


// PACKAGE


package common.misc;

//----------------------------------------------------------------------


// NO-YES OPTION ENUMERATION


public enum NoYes
	implements IStringKeyed
{

////////////////////////////////////////////////////////////////////////
//  Constants
////////////////////////////////////////////////////////////////////////

	NO
	(
		"no", "false"
	),

	YES
	(
		"yes", "true"
	);

////////////////////////////////////////////////////////////////////////
//  Constructors
////////////////////////////////////////////////////////////////////////

	private NoYes(String... keys)
	{
		this.keys = keys;
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Class methods
////////////////////////////////////////////////////////////////////////

	public static NoYes forKey(String key)
	{
		for (NoYes value : values())
		{
			for (String k : value.keys)
			{
				if (k.equals(key))
					return value;
			}
		}
		return null;
	}

	//------------------------------------------------------------------

	public static NoYes forBoolean(boolean value)
	{
		return (value ? YES : NO);
	}

	//------------------------------------------------------------------

	public static String getKey(boolean value)
	{
		return forBoolean(value).getKey();
	}

	//------------------------------------------------------------------

	public static String toString(boolean value)
	{
		return forBoolean(value).toString();
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Instance methods : IStringKeyed interface
////////////////////////////////////////////////////////////////////////

	public String getKey()
	{
		return keys[0];
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Instance methods : overriding methods
////////////////////////////////////////////////////////////////////////

	@Override
	public String toString()
	{
		return StringUtils.firstCharToUpperCase(getKey());
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Instance methods
////////////////////////////////////////////////////////////////////////

	public boolean toBoolean()
	{
		return ((this == NO) ? false : true);
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Instance fields
////////////////////////////////////////////////////////////////////////

	private	String[]	keys;

}

//----------------------------------------------------------------------
