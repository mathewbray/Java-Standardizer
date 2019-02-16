/*====================================================================*\

IntegerPair.java

Integer pair class.

\*====================================================================*/


// PACKAGE


package common.misc;

//----------------------------------------------------------------------


// IMPORTS


import common.exception.UnexpectedRuntimeException;

//----------------------------------------------------------------------


// INTEGER PAIR CLASS


public class IntegerPair
	implements Cloneable
{

////////////////////////////////////////////////////////////////////////
//  Constructors
////////////////////////////////////////////////////////////////////////

	public IntegerPair()
	{
	}

	//------------------------------------------------------------------

	public IntegerPair(int value1,
					   int value2)
	{
		this.value1 = value1;
		this.value2 = value2;
	}

	//------------------------------------------------------------------

	public IntegerPair(IntegerPair pair)
	{
		value1 = pair.value1;
		value2 = pair.value2;
	}

	//------------------------------------------------------------------

	/**
	 * @throws NumberFormatException
	 */

	public IntegerPair(String str)
	{
		IntegerPair pair = parse(str);
		value1 = pair.value1;
		value2 = pair.value2;
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Class methods
////////////////////////////////////////////////////////////////////////

	/**
	 * @throws NumberFormatException
	 */

	public static IntegerPair parse(String str)
	{
		String[] strs = str.split(" *, *", -1);
		if (strs.length != 2)
			throw new NumberFormatException();
		return new IntegerPair(Integer.parseInt(strs[0]), Integer.parseInt(strs[1]));
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Instance methods : overriding methods
////////////////////////////////////////////////////////////////////////

	@Override
	public IntegerPair clone()
	{
		try
		{
			return (IntegerPair)super.clone();
		}
		catch (CloneNotSupportedException e)
		{
			throw new UnexpectedRuntimeException(e);
		}
	}

	//------------------------------------------------------------------

	@Override
	public boolean equals(Object obj)
	{
		if (obj instanceof IntegerPair)
		{
			IntegerPair pair = (IntegerPair)obj;
			return ((value1 == pair.value1) && (value2 == pair.value2));
		}
		return false;
	}

	//------------------------------------------------------------------

	@Override
	public int hashCode()
	{
		int sum = value1 + value2;
		return (sum * (sum + 1) / 2 + value1);
	}

	//------------------------------------------------------------------

	@Override
	public String toString()
	{
		return new String(Integer.toString(value1) + ", " + Integer.toString(value2));
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Instance fields
////////////////////////////////////////////////////////////////////////

	public	int	value1;
	public	int	value2;

}

//----------------------------------------------------------------------
