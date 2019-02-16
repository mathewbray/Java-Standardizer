/*====================================================================*\

HmacSha256.java

SHA-256 hash-based message authentication code class.

\*====================================================================*/


// PACKAGE


package common.crypto;

//----------------------------------------------------------------------


// IMPORTS


import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import common.exception.UnexpectedRuntimeException;

//----------------------------------------------------------------------


// SHA-256 HASH-BASED MESSAGE AUTHENTICATION CODE CLASS


/**
 * This class implements a hash-based message authentication code (HMAC) whose underlying function is the
 * SHA-256 cryptographic hash function.
 * <p>
 * HMAC is specified in <a href="http://tools.ietf.org/html/rfc2104">IETF RFC2104</a>.
 * </p>
 */

public class HmacSha256
{

////////////////////////////////////////////////////////////////////////
//  Constants
////////////////////////////////////////////////////////////////////////

	/**
	 * The size (in bytes) of the hash value.
	 */
	public static final		int		HASH_VALUE_SIZE	= 256 / Byte.SIZE;

	private static final	int		HASH_BLOCK_SIZE	= 512 / Byte.SIZE;

	private static final	byte	INNER_PADDING	= 0x36;
	private static final	byte	OUTER_PADDING	= 0x5C;

	private static final	String	HASH_NAME	= "SHA-256";

////////////////////////////////////////////////////////////////////////
//  Constructors
////////////////////////////////////////////////////////////////////////

	/**
	 * Creates an SHA-256-based message authentication code (HMAC) with a specified key.
	 *
	 * @param  key  the key for the HMAC.
	 * @throws UnexpectedRuntimeException
	 *           if the {@link java.security.MessageDigest} class does not support the SHA-256 algorithm.
	 *           (Every implementation of the Java platform is required to support the SHA-256 algorithm.)
	 */

	public HmacSha256(byte[] key)
	{
		// Create an SHA-256 hash-function object
		try
		{
			hash = MessageDigest.getInstance(HASH_NAME);
		}
		catch (NoSuchAlgorithmException e)
		{
			throw new UnexpectedRuntimeException(e);
		}

		// If the key is longer than the block size of the hash function, reduce its length by hashing it
		if (key.length > HASH_BLOCK_SIZE)
			key = hash.digest(key);

		// If the key is shorter than the block size of the hash function, pad it with trailing zeros
		if (key.length < HASH_BLOCK_SIZE)
		{
			byte[] k = key;
			key = new byte[HASH_BLOCK_SIZE];
			System.arraycopy(k, 0, key, 0, k.length);
		}

		// Set the key instance field
		this.key = key.clone();

		// Initialise the function
		reset();
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Instance methods
////////////////////////////////////////////////////////////////////////

	/**
	 * Updates this HMAC with some specified data.
	 *
	 * @param data  the data with which the HMAC will be updated.
	 */

	public void update(byte[] data)
	{
		hash.update(data);
	}

	//------------------------------------------------------------------

	/**
	 * Updates this HMAC with data of a specified length.
	 *
	 * @param data    the data with which the HMAC will be updated.
	 * @param offset  the start offset of the data in {@code data}.
	 * @param length  the number of bytes with which the HMAC will be updated.
	 */

	public void update(byte[] data,
					   int    offset,
					   int    length)
	{
		hash.update(data, offset, length);
	}

	//------------------------------------------------------------------

	/**
	 * Returns the value of this HMAC.
	 * <p>
	 * The HMAC is left in an invalid state by this method, and it must be explicitly reset with {@link
	 * #reset()} before it can be used again.
	 * </p>
	 *
	 * @return the value of the HMAC.
	 * @see    #getValue(byte[])
	 * @see    #getValue(byte[], int, int)
	 */

	public byte[] getValue()
	{
		// Get the inner hash value
		byte[] innerHashValue = hash.digest();

		// Create the padded outer key
		byte[] outerKey = key.clone();
		for (int i = 0; i < outerKey.length; i++)
			outerKey[i] ^= OUTER_PADDING;

		// Hash the outer key and inner hash value, and return the result
		hash.update(outerKey);
		return hash.digest(innerHashValue);
	}

	//------------------------------------------------------------------

	/**
	 * Updates this HMAC with some specified data and returns the value of the HMAC.
	 * <p>
	 * The HMAC is left in an invalid state by this method, and it must be explicitly reset with {@link
	 * #reset()} before it can be used again.
	 * </p>
	 *
	 * @param  data  the data with which the HMAC will be updated.
	 * @return the value of the HMAC after it has been updated.
	 * @see    #getValue()
	 * @see    #getValue(byte[], int, int)
	 */

	public byte[] getValue(byte[] data)
	{
		update(data);
		return getValue();
	}

	//------------------------------------------------------------------

	/**
	 * Updates this HMAC with data of a specified length and returns the value of the HMAC.
	 * <p>
	 * The HMAC is left in an invalid state by this method, and it must be explicitly reset with {@link
	 * #reset()} before it can be used again.
	 * </p>
	 *
	 * @param  data    the data with which the HMAC will be updated.
	 * @param  offset  the start offset of the data in {@code data}.
	 * @param  length  the number of bytes with which the HMAC will be updated.
	 * @return the value of the HMAC after it has been updated.
	 * @see    #getValue()
	 * @see    #getValue(byte[])
	 */

	public byte[] getValue(byte[] data,
						   int    offset,
						   int    length)
	{
		update(data, offset, length);
		return getValue();
	}

	//------------------------------------------------------------------

	/**
	 * Resets this HMAC so that it can be used again.
	 * <p>
	 * If the HMAC is to be used again after calling any of the {@code getValue} methods, this method must
	 * be called before the HMAC is updated with any more data.
	 * </p>
	 *
	 * @see #getValue()
	 * @see #getValue(byte[])
	 * @see #getValue(byte[], int, int)
	 */

	public void reset()
	{
		// Reset the hash function
		hash.reset();

		// Create the padded inner key
		byte[] innerKey = key.clone();
		for (int i = 0; i < innerKey.length; i++)
			innerKey[i] ^= INNER_PADDING;

		// Update the hash with the inner key
		hash.update(innerKey);
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Instance fields
////////////////////////////////////////////////////////////////////////

	private	byte[]			key;
	private	MessageDigest	hash;

}

//----------------------------------------------------------------------
