/*====================================================================*\

Id.java

IFF identifier class.

\*====================================================================*/


// PACKAGE


package common.iff;

//----------------------------------------------------------------------


// IMPORTS


import java.io.DataOutput;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;

import common.misc.NumberUtils;

//----------------------------------------------------------------------


// IFF IDENTIFIER CLASS


public class IffId
{

////////////////////////////////////////////////////////////////////////
//  Constants
////////////////////////////////////////////////////////////////////////

	public static final		int	SIZE	= 4;

	public static final		int	MIN_CHAR	= '\u0020';
	public static final		int	MAX_CHAR	= '\u007E';

	private static final	String	ENCODING_NAME	= "US-ASCII";

	private static final	byte[]	DEFAULT_VALUE	= { ' ', ' ', ' ', ' ' };

////////////////////////////////////////////////////////////////////////
//  Constructors
////////////////////////////////////////////////////////////////////////

	public IffId()
	{
		value = NumberUtils.bytesToUIntBE(DEFAULT_VALUE);
	}

	//------------------------------------------------------------------

	/**
	 * @throws IllegalArgumentException
	 * @throws IndexOutOfBoundsException
	 */

	public IffId(byte[] bytes)
	{
		set(bytes);
	}

	//------------------------------------------------------------------

	/**
	 * @throws IllegalArgumentException
	 * @throws IndexOutOfBoundsException
	 */

	public IffId(byte[] bytes,
				 int    offset)
	{
		set(bytes, offset);
	}

	//------------------------------------------------------------------

	/**
	 * @throws IllegalArgumentException
	 * @throws IndexOutOfBoundsException
	 */

	public IffId(String str)
	{
		set(str);
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Instance methods : overriding methods
////////////////////////////////////////////////////////////////////////

	@Override
	public boolean equals(Object obj)
	{
		return ((obj instanceof IffId) && (value == ((IffId)obj).value));
	}

	//------------------------------------------------------------------

	@Override
	public int hashCode()
	{
		return value;
	}

	//------------------------------------------------------------------

	@Override
	public String toString()
	{
		String str = null;
		try
		{
			str = new String(getBytes(), ENCODING_NAME);
		}
		catch (UnsupportedEncodingException e)
		{
			e.printStackTrace();
		}
		return str;
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Instance methods
////////////////////////////////////////////////////////////////////////

	public int getValue()
	{
		return value;
	}

	//------------------------------------------------------------------

	public byte[] getBytes()
	{
		byte[] buffer = new byte[SIZE];
		NumberUtils.intToBytesBE(value, buffer);
		return buffer;
	}

	//------------------------------------------------------------------

	/**
	 * @throws IllegalArgumentException
	 * @throws IndexOutOfBoundsException
	 */

	public void set(byte[] bytes)
	{
		set(bytes, 0);
	}

	//------------------------------------------------------------------

	/**
	 * @throws IllegalArgumentException
	 * @throws IndexOutOfBoundsException
	 */

	public void set(byte[] bytes,
					int    offset)
	{
		if ((offset < 0) || (offset > bytes.length - SIZE))
			throw new IndexOutOfBoundsException();

		for (int i = 0; i < SIZE; i++)
		{
			int ch = bytes[i + offset] & 0xFF;
			if ((ch < MIN_CHAR) || (ch > MAX_CHAR) || ((i == 0) && (ch == ' ')))
				throw new IllegalArgumentException();
		}
		value = NumberUtils.bytesToUIntBE(bytes, offset, SIZE);
	}

	//------------------------------------------------------------------

	/**
	 * @throws IllegalArgumentException
	 * @throws IndexOutOfBoundsException
	 */

	public void set(String str)
	{
		try
		{
			set(str.getBytes(ENCODING_NAME), 0);
		}
		catch (UnsupportedEncodingException e)
		{
			e.printStackTrace();
		}
	}

	//------------------------------------------------------------------

	public void put(byte[] buffer)
	{
		NumberUtils.intToBytesBE(value, buffer, 0, SIZE);
	}

	//------------------------------------------------------------------

	public void put(byte[] buffer,
					int    offset)
	{
		NumberUtils.intToBytesBE(value, buffer, offset, SIZE);
	}

	//------------------------------------------------------------------

	public void write(OutputStream outStream)
		throws IOException
	{
		outStream.write(getBytes());
	}

	//------------------------------------------------------------------

	public void write(DataOutput dataOutput)
		throws IOException
	{
		dataOutput.write(getBytes());
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Instance fields
////////////////////////////////////////////////////////////////////////

	private	int	value;

}

//----------------------------------------------------------------------
