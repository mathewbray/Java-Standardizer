/*====================================================================*\

ShaD256.java

Double-iteration SHA-256 hash function class.

\*====================================================================*/


// PACKAGE


package common.crypto;

//----------------------------------------------------------------------


// IMPORTS


import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import common.exception.UnexpectedRuntimeException;

//----------------------------------------------------------------------


// DOUBLE-ITERATION SHA-256 HASH FUNCTION CLASS


/**
 * This class implements a double-iteration SHA-256 cryptographic hash function.
 * <p>
 * The hash value, <i>h</i>, of the double-iteration SHA-256 function for input <i>m</i> is given by:<br>
 * &nbsp;&nbsp;&nbsp; <i>h</i> = sha256(sha256(<i>m</i>)) .
 * </p>
 * <p>
 * This class uses the implementation of the SHA-256 algorithm that is provided by the {@link
 * java.security.MessageDigest} class.
 * </p>
 */

public class ShaD256
	implements Cloneable
{

////////////////////////////////////////////////////////////////////////
//  Constants
////////////////////////////////////////////////////////////////////////

	/**
	 * The size (in bytes) of the hash value.
	 */
	public static final		int	HASH_VALUE_SIZE	= 256 / Byte.SIZE;

	private static final	String	HASH_NAME	= "SHA-256";

	private static final	MessageDigest	OUTER_HASH;

////////////////////////////////////////////////////////////////////////
//  Constructors
////////////////////////////////////////////////////////////////////////

	/**
	 * Creates a double-iteration SHA-256 cryptographic hash function.
	 *
	 * @throws UnexpectedRuntimeException
	 *           if the {@link java.security.MessageDigest} class does not support the SHA-256 algorithm.
	 *           (Every implementation of the Java platform is required to support the SHA-256 algorithm.)
	 */

	public ShaD256()
	{
		try
		{
			hash = MessageDigest.getInstance(HASH_NAME);
		}
		catch (NoSuchAlgorithmException e)
		{
			throw new UnexpectedRuntimeException(e);
		}
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Instance methods : overriding methods
////////////////////////////////////////////////////////////////////////

	/**
	 * Creates a copy of this double-iteration SHA-256 cryptographic hash function.
	 *
	 * @return a copy of this double-iteration SHA-256 cryptographic hash function.
	 * @throws CloneNotSupportedException
	 *           if the implementation of the SHA-256 algorithm by the {@link java.security.MessageDigest}
	 *           class does not support cloning.
	 */

	@Override
	public ShaD256 clone()
		throws CloneNotSupportedException
	{
		ShaD256 copy = (ShaD256)super.clone();
		copy.hash = (MessageDigest)hash.clone();
		return copy;
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Instance methods
////////////////////////////////////////////////////////////////////////

	/**
	 * Updates this hash function with a specified byte.
	 *
	 * @param b  the byte with which the hash function will be updated.
	 */

	public void update(byte b)
	{
		hash.update(b);
	}

	//------------------------------------------------------------------

	/**
	 * Updates this hash function with some specified data.
	 *
	 * @param data  the data with which the hash function will be updated.
	 */

	public void update(byte[] data)
	{
		hash.update(data);
	}

	//------------------------------------------------------------------

	/**
	 * Updates this hash function with data of a specified length.
	 *
	 * @param data    the data with which the hash function will be updated.
	 * @param offset  the start offset of the data in {@code data}.
	 * @param length  the number of bytes with which the hash function will be updated.
	 */

	public void update(byte[] data,
					   int    offset,
					   int    length)
	{
		hash.update(data, offset, length);
	}

	//------------------------------------------------------------------

	/**
	 * Returns the value of this hash function.
	 *
	 * @return the value of this hash function.
	 */

	public byte[] digest()
	{
		return OUTER_HASH.digest(hash.digest());
	}

	//------------------------------------------------------------------

	/**
	 * Updates this hash function with some specified data and returns the value of the hash function.
	 *
	 * @param  data  the data with which the hash function will be updated.
	 * @return the value of this hash function after it has been updated.
	 */

	public byte[] digest(byte[] data)
	{
		update(data);
		return digest();
	}

	//------------------------------------------------------------------

	/**
	 * Resets this hash function so that it can be used again.
	 */

	public void reset()
	{
		hash.reset();
	}

	//------------------------------------------------------------------

	/**
	 * Returns {@code true} if this hash function can be cloned.
	 *
	 * @return {@code true} if this hash function can be cloned, {@code false} otherwise.
	 */

	public boolean canClone()
	{
		try
		{
			hash.clone();
			return true;
		}
		catch (CloneNotSupportedException e)
		{
			return false;
		}
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Static initialiser
////////////////////////////////////////////////////////////////////////

	/**
	 * Initialised the outer SHA-256 hash function.
	 *
	 * @throws UnexpectedRuntimeException
	 */

	static
	{
		try
		{
			OUTER_HASH = MessageDigest.getInstance(HASH_NAME);
		}
		catch (NoSuchAlgorithmException e)
		{
			throw new UnexpectedRuntimeException(e);
		}
	}

////////////////////////////////////////////////////////////////////////
//  Instance fields
////////////////////////////////////////////////////////////////////////

	private	MessageDigest	hash;

}

//----------------------------------------------------------------------
