/*====================================================================*\

StringKVPair.java

Class: string key-value pair.

\*====================================================================*/


// PACKAGE


package common.tuple;

//----------------------------------------------------------------------


// IMPORTS


import common.exception.UnexpectedRuntimeException;

import common.misc.IStringKeyed;

//----------------------------------------------------------------------


// CLASS: STRING KEY-VALUE PAIR


public class StringKVPair
	extends KeyValuePair<String, String>
	implements Cloneable, IStringKeyed
{

////////////////////////////////////////////////////////////////////////
//  Constructors
////////////////////////////////////////////////////////////////////////

	public StringKVPair(String key)
	{
		super(key, "");
	}

	//------------------------------------------------------------------

	public StringKVPair(String key,
						String value)
	{
		super(key, value);
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Class methods
////////////////////////////////////////////////////////////////////////

	/**
	 * Creates and returns a new instance of a string key&ndash;value pair with the specified key and value.
	 *
	 * @param  key    the key of the pair.
	 * @param  value  the value of the pair.
	 * @return a key&ndash;value pair whose key is {@code key} and whose value is {@code value}.
	 */

	public static StringKVPair pair(String key,
									String value)
	{
		return new StringKVPair(key, value);
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Instance methods : overriding methods
////////////////////////////////////////////////////////////////////////

	/**
	 * {@inheritDoc}
	 */

	@Override
	public StringKVPair clone()
	{
		try
		{
			return (StringKVPair)super.clone();
		}
		catch (CloneNotSupportedException e)
		{
			throw new UnexpectedRuntimeException(e);
		}
	}

	//------------------------------------------------------------------

}

//----------------------------------------------------------------------
