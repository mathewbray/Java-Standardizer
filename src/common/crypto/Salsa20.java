/*====================================================================*\

Salsa20.java

Salsa20 stream cipher class.

\*====================================================================*/


// PACKAGE


package common.crypto;

//----------------------------------------------------------------------


// IMPORTS


import java.io.UnsupportedEncodingException;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import java.util.Arrays;

import common.exception.UnexpectedRuntimeException;

//----------------------------------------------------------------------


// SALSA20 STREAM CIPHER CLASS


/**
 * This class implements the Salsa20 stream cipher with 20 rounds and a 256-bit key.
 */

public class Salsa20
	implements Cloneable
{

////////////////////////////////////////////////////////////////////////
//  Constants
////////////////////////////////////////////////////////////////////////

	/**
	 * The number of bytes per word, as defined in the Salsa20 specification.
	 */
	public static final		int	BYTES_PER_WORD	= 4;

	/**
	 * The key size (in bits) of the cipher.
	 */
	public static final		int	KEY_SIZE_BITS		= 256;

	/**
	 * The key size (in bytes) of the cipher.
	 */
	public static final		int	KEY_SIZE			= KEY_SIZE_BITS / Byte.SIZE;

	/**
	 * The key size (in 32-bit words) of the cipher.
	 */
	public static final		int	KEY_SIZE_WORDS		= KEY_SIZE / BYTES_PER_WORD;

	/**
	 * The nonce size (in bytes) of the cipher.
	 */
	public static final		int	NONCE_SIZE			= 8;

	/**
	 * The nonce size (in 32-bit words) of the cipher.
	 */
	public static final		int	NONCE_SIZE_WORDS	= NONCE_SIZE / BYTES_PER_WORD;

	/**
	 * The counter size (in bytes) of the cipher.
	 */
	public static final		int	COUNTER_SIZE		= 8;

	/**
	 * The counter size (in bytes) of the cipher.
	 */
	public static final		int	COUNTER_SIZE_WORDS	= COUNTER_SIZE / BYTES_PER_WORD;

	/**
	 * The block size (in bytes) of the cipher.
	 */
	public static final		int	BLOCK_SIZE			= 64;

	/**
	 * The block size (in 32-bit words) of the cipher.
	 */
	public static final		int	BLOCK_SIZE_WORDS	= BLOCK_SIZE / BYTES_PER_WORD;

	private static final	int	KEY1_OFFSET				= 1;
	private static final	int	NONCE_OFFSET			= 6;
	private static final	int	BLOCK_COUNTER_OFFSET	= 8;
	private static final	int	KEY2_OFFSET				= 11;

	private static final	int[]	CONSTANT_WORDS_32	=
	{
		0x61707865, 0x3320646E, 0x79622D32, 0x6B206574
	};

	private static final	String	ENCODING_NAME	= "UTF-8";
	private static final	String	HASH_NAME		= "SHA-256";

////////////////////////////////////////////////////////////////////////
//  Constructors
////////////////////////////////////////////////////////////////////////

	/**
	 * Creates an implementation of the Salsa20 stream cipher with a 256-bit key and a specified number of
	 * rounds of the Salsa20 core hash function.
	 *
	 * @param numRounds  the number of rounds of the Salsa20 core hash function that will be performed.
	 */

	public Salsa20(int numRounds)
	{
		this.numRounds = numRounds;
		inBlock = new int[BLOCK_SIZE_WORDS];
		outBlock = new int[BLOCK_SIZE_WORDS];
	}

	//------------------------------------------------------------------

	/**
	 * Creates an implementation of the Salsa20 stream cipher with a 256-bit key and a specified number of
	 * rounds of the Salsa20 core hash function, and initialises the cipher with a specified key and nonce.
	 *
	 * @param  numRounds  the number of rounds of the Salsa20 core hash function that will be performed.
	 * @param  key        the key that will be used for the cipher.
	 * @param  nonce      the nonce that will be used in the cipher.
	 * @throws IllegalArgumentException
	 *           if
	 *           <ul>
	 *             <li>{@code key} is {@code null} or the length of {@code key} is not 8, or</li>
	 *             <li>{@code nonce} is {@code null} or the length of {nonce key} is not 2.</li>
	 *           </ul>
	 */

	public Salsa20(int numRounds,
					int key[],
					int nonce[])
	{
		this.numRounds = numRounds;
		inBlock = new int[BLOCK_SIZE_WORDS];
		outBlock = new int[BLOCK_SIZE_WORDS];
		init(key, nonce);
	}

	//------------------------------------------------------------------

	/**
	 * Creates an implementation of the Salsa20 stream cipher with a 256-bit key and a specified number of
	 * rounds of the Salsa20 core hash function, and initialises the cipher with a specified key and nonce.
	 *
	 * @param  numRounds  the number of rounds of the Salsa20 core hash function that will be performed.
	 * @param  key        the key that will be used for the cipher.
	 * @param  nonce      the nonce that will be used in the cipher.
	 * @throws IllegalArgumentException
	 *           if
	 *           <ul>
	 *             <li>{@code key} is {@code null} or the length of {@code key} is not 8, or</li>
	 *             <li>{@code nonce} is {@code null} or the length of {nonce key} is not 2.</li>
	 *           </ul>
	 */

	public Salsa20(int  numRounds,
					byte key[],
					byte nonce[])
	{
		this.numRounds = numRounds;
		inBlock = new int[BLOCK_SIZE_WORDS];
		outBlock = new int[BLOCK_SIZE_WORDS];
		init(key, nonce);
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Class methods
////////////////////////////////////////////////////////////////////////

	/**
	 * Converts a specified string to a 32-byte sequence, and returns it.  The byte sequence, which is an
	 * SHA-256 hash of the UTF-8 encoding of {@code str}, is suitable for use as the key of a Salsa20
	 * cipher.
	 *
	 * @param  str  the string that will be converted.
	 * @return a SHA-256 hash of the UTF-8 encoding of {@code str}.
	 * @throws UnexpectedRuntimeException
	 *           if the UTF-8 character encoding is not supported by the Java implementation or the {@link
	 *           java.security.MessageDigest} class does not support the SHA-256 algorithm.  (Every
	 *           implementation of the Java platform is required to support the UTF-8 character encoding and
	 *           the SHA-256 algorithm.)
	 */

	public static byte[] stringToKey(String str)
	{
		try
		{
			return MessageDigest.getInstance(HASH_NAME).digest(str.getBytes(ENCODING_NAME));
		}
		catch (UnsupportedEncodingException e)
		{
			throw new UnexpectedRuntimeException(e);
		}
		catch (NoSuchAlgorithmException e)
		{
			throw new UnexpectedRuntimeException(e);
		}
	}

	//------------------------------------------------------------------

	/**
	 * Converts a specified sequence of four bytes in little-endian format to a 32-bit word, and returns it.
	 *
	 * @param  data    the sequence of four bytes that will be converted.
	 * @param  offset  the start offset of the sequence in {@code data}.
	 * @return the 32-bit word that resulted from converting from the input sequence.
	 */

	public static int bytesToWord(byte[] data,
								  int    offset)
	{
		return ((data[offset++] & 0xFF) | (data[offset++] & 0xFF) << 8 | (data[offset++] & 0xFF) << 16 |
																			(data[offset++] & 0xFF) << 24);
	}

	//------------------------------------------------------------------

	/**
	 * Converts a specified 32-bit word to a sequence of four bytes and stores the byte sequence in a
	 * specified buffer.
	 *
	 * @param value   the word value that will be converted.
	 * @param buffer  the buffer in which the byte sequence will be stored.
	 * @param offset  the offset in {@code buffer} at which the first byte of the sequence will be stored.
	 */

	public static void wordToBytes(int    value,
								   byte[] buffer,
								   int    offset)
	{
		buffer[offset++] = (byte)value;
		buffer[offset++] = (byte)(value >>> 8);
		buffer[offset++] = (byte)(value >>> 16);
		buffer[offset++] = (byte)(value >>> 24);
	}

	//------------------------------------------------------------------

	/**
	 * Performs a specified number of rounds of the Salsa20 core hash function on a specified block of data.
	 *
	 * @param inData     the data that will be hashed.
	 * @param outBuffer  a buffer in which the hashed data will be stored.
	 * @param numRounds  the number of rounds of the Salsa20 core hash function that will be performed.
	 */

	public static void hash(int[] inData,
							int[] outBuffer,
							int   numRounds)
	{
		// Copy input array to output array
		System.arraycopy(inData, 0, outBuffer, 0, inData.length);

		// Modify data
		int a = 0;
		int b = 0;
		numRounds /= 2;
		for (int i = 0; i < numRounds; i++)
		{
			a = outBuffer[0] + outBuffer[12];
			b = 7;
			outBuffer[ 4] ^= (a << b) | (a >>> (32 - b));

			a = outBuffer[4] + outBuffer[0];
			b = 9;
			outBuffer[ 8] ^= (a << b) | (a >>> (32 - b));

			a = outBuffer[8] + outBuffer[4];
			b = 13;
			outBuffer[12] ^= (a << b) | (a >>> (32 - b));

			a = outBuffer[12] + outBuffer[8];
			b = 18;
			outBuffer[ 0] ^= (a << b) | (a >>> (32 - b));

			a = outBuffer[5] + outBuffer[1];
			b = 7;
			outBuffer[ 9] ^= (a << b) | (a >>> (32 - b));

			a = outBuffer[9] + outBuffer[5];
			b = 9;
			outBuffer[13] ^= (a << b) | (a >>> (32 - b));

			a = outBuffer[13] + outBuffer[9];
			b = 13;
			outBuffer[ 1] ^= (a << b) | (a >>> (32 - b));

			a = outBuffer[1] + outBuffer[13];
			b = 18;
			outBuffer[ 5] ^= (a << b) | (a >>> (32 - b));

			a = outBuffer[10] + outBuffer[6];
			b = 7;
			outBuffer[14] ^= (a << b) | (a >>> (32 - b));

			a = outBuffer[14] + outBuffer[10];
			b = 9;
			outBuffer[ 2] ^= (a << b) | (a >>> (32 - b));

			a = outBuffer[2] + outBuffer[14];
			b = 13;
			outBuffer[ 6] ^= (a << b) | (a >>> (32 - b));

			a = outBuffer[6] + outBuffer[2];
			b = 18;
			outBuffer[10] ^= (a << b) | (a >>> (32 - b));

			a = outBuffer[15] + outBuffer[11];
			b = 7;
			outBuffer[ 3] ^= (a << b) | (a >>> (32 - b));

			a = outBuffer[3] + outBuffer[15];
			b = 9;
			outBuffer[ 7] ^= (a << b) | (a >>> (32 - b));

			a = outBuffer[7] + outBuffer[3];
			b = 13;
			outBuffer[11] ^= (a << b) | (a >>> (32 - b));

			a = outBuffer[11] + outBuffer[7];
			b = 18;
			outBuffer[15] ^= (a << b) | (a >>> (32 - b));

			a = outBuffer[0] + outBuffer[3];
			b = 7;
			outBuffer[ 1] ^= (a << b) | (a >>> (32 - b));

			a = outBuffer[1] + outBuffer[0];
			b = 9;
			outBuffer[ 2] ^= (a << b) | (a >>> (32 - b));

			a = outBuffer[2] + outBuffer[1];
			b = 13;
			outBuffer[ 3] ^= (a << b) | (a >>> (32 - b));

			a = outBuffer[3] + outBuffer[2];
			b = 18;
			outBuffer[ 0] ^= (a << b) | (a >>> (32 - b));

			a = outBuffer[5] + outBuffer[4];
			b = 7;
			outBuffer[ 6] ^= (a << b) | (a >>> (32 - b));

			a = outBuffer[6] + outBuffer[5];
			b = 9;
			outBuffer[ 7] ^= (a << b) | (a >>> (32 - b));

			a = outBuffer[7] + outBuffer[6];
			b = 13;
			outBuffer[ 4] ^= (a << b) | (a >>> (32 - b));

			a = outBuffer[4] + outBuffer[7];
			b = 18;
			outBuffer[ 5] ^= (a << b) | (a >>> (32 - b));

			a = outBuffer[10] + outBuffer[9];
			b = 7;
			outBuffer[11] ^= (a << b) | (a >>> (32 - b));

			a = outBuffer[11] + outBuffer[10];
			b = 9;
			outBuffer[ 8] ^= (a << b) | (a >>> (32 - b));

			a = outBuffer[8] + outBuffer[11];
			b = 13;
			outBuffer[ 9] ^= (a << b) | (a >>> (32 - b));

			a = outBuffer[9] + outBuffer[8];
			b = 18;
			outBuffer[10] ^= (a << b) | (a >>> (32 - b));

			a = outBuffer[15] + outBuffer[14];
			b = 7;
			outBuffer[12] ^= (a << b) | (a >>> (32 - b));

			a = outBuffer[12] + outBuffer[15];
			b = 9;
			outBuffer[13] ^= (a << b) | (a >>> (32 - b));

			a = outBuffer[13] + outBuffer[12];
			b = 13;
			outBuffer[14] ^= (a << b) | (a >>> (32 - b));

			a = outBuffer[14] + outBuffer[13];
			b = 18;
			outBuffer[15] ^= (a << b) | (a >>> (32 - b));
		}

		// Add the input data to the output data
		for (int i = 0; i < outBuffer.length; i++)
			outBuffer[i] += inData[i];
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Instance methods : overriding methods
////////////////////////////////////////////////////////////////////////

	/**
	 * Creates a copy of this Salsa20 cipher.
	 *
	 * @return a copy of this Salsa20 cipher.
	 */

	@Override
	public Salsa20 clone()
	{
		try
		{
			Salsa20 copy = (Salsa20)super.clone();
			copy.inBlock = inBlock.clone();
			copy.outBlock = outBlock.clone();
			return copy;
		}
		catch (CloneNotSupportedException e)
		{
			throw new UnexpectedRuntimeException(e);
		}
	}

	//------------------------------------------------------------------

	/**
	 * Returns {@code true} if this cipher is equal to a specified object.
	 * <p>
	 * This cipher is considered to be equal to another object if the object is an instance of {@code
	 * Salsa20} and the keys and nonces of the two objects are equal.
	 * </p>
	 *
	 * @return {@code true} if this cipher is equal to the specified object, {@code false} otherwise.
	 */

	@Override
	public boolean equals(Object obj)
	{
		if (obj instanceof Salsa20)
		{
			Salsa20 salsa20 = (Salsa20)obj;
			return Arrays.equals(getId(), salsa20.getId());
		}
		return false;
	}

	//------------------------------------------------------------------

	/**
	 * Returns a hash code for this object.
	 *
	 * @return a hash code for this object.
	 */

	@Override
	public int hashCode()
	{
		return Arrays.hashCode(getId());
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Instance methods
////////////////////////////////////////////////////////////////////////

	/**
	 * Returns the key of this cipher.
	 *
	 * @return the key of this cipher.
	 * @see    #getNonce()
	 */

	public byte[] getKey()
	{
		byte[] key = new byte[KEY_SIZE];
		int offset = 0;

		for (int i = KEY1_OFFSET; i < KEY1_OFFSET + KEY_SIZE_WORDS / 2; i++)
		{
			wordToBytes(inBlock[i], key, offset);
			offset += BYTES_PER_WORD;
		}

		for (int i = KEY2_OFFSET; i < KEY2_OFFSET + KEY_SIZE_WORDS / 2; i++)
		{
			wordToBytes(inBlock[i], key, offset);
			offset += BYTES_PER_WORD;
		}

		return key;
	}

	//------------------------------------------------------------------

	/**
	 * Returns the nonce of this cipher.
	 *
	 * @return the nonce of this cipher.
	 * @see    #getKey()
	 */

	public byte[] getNonce()
	{
		byte[] nonce = new byte[NONCE_SIZE];
		int offset = 0;

		for (int i = NONCE_OFFSET; i < NONCE_OFFSET + NONCE_SIZE_WORDS; i++)
		{
			wordToBytes(inBlock[i], nonce, offset);
			offset += BYTES_PER_WORD;
		}

		return nonce;
	}

	//------------------------------------------------------------------

	/**
	 * Returns the current value of the block counter of this cipher.
	 *
	 * @return the current value of the block counter of this cipher.
	 * @see    #getNextBlock(byte[], int)
	 */

	public long getBlockCounter()
	{
		return blockCounter;
	}

	//------------------------------------------------------------------

	/**
	 * Resets this cipher.
	 * <p>
	 * This method resets the block counter.
	 * </p>
	 */

	public void reset()
	{
		blockCounter = 0;
	}

	//------------------------------------------------------------------

	/**
	 * Initialises this cipher with a specified key and nonce.
	 *
	 * @param  key    the key that will be used for the cipher.
	 * @param  nonce  the nonce that will be used in the cipher.
	 * @throws IllegalArgumentException
	 *           if
	 *           <ul>
	 *             <li>{@code key} is {@code null} or the length of {@code key} is not 32, or</li>
	 *             <li>{@code nonce} is {@code null} or the length of {nonce key} is not 8.</li>
	 *           </ul>
	 * @see    #init(int[], int[])
	 */

	public void init(byte[] key,
					 byte[] nonce)
	{
		// Validate arguments
		if ((key == null) || (key.length != KEY_SIZE) || (nonce == null) || (nonce.length != NONCE_SIZE))
			throw new IllegalArgumentException();

		// Convert key to words
		int[] keyWords = new int[KEY_SIZE_WORDS];
		int offset = 0;
		for (int i = 0; i < KEY_SIZE_WORDS; i++)
		{
			keyWords[i] = bytesToWord(key, offset);
			offset += BYTES_PER_WORD;
		}

		// Convert nonce to words
		int[] nonceWords = new int[NONCE_SIZE_WORDS];
		offset = 0;
		for (int i = 0; i < NONCE_SIZE_WORDS; i++)
		{
			nonceWords[i] = bytesToWord(nonce, offset);
			offset += BYTES_PER_WORD;
		}

		// Initialise input block
		initBlock(keyWords, nonceWords);
	}

	//------------------------------------------------------------------

	/**
	 * Initialises this cipher with a specified key and nonce.
	 *
	 * @param  key    the key that will be used for the cipher.
	 * @param  nonce  the nonce that will be used in the cipher.
	 * @throws IllegalArgumentException
	 *           if
	 *           <ul>
	 *             <li>{@code key} is {@code null} or the length of {@code key} is not 8, or</li>
	 *             <li>{@code nonce} is {@code null} or the length of {nonce key} is not 2.</li>
	 *           </ul>
	 * @see    #init(byte[], byte[])
	 */

	public void init(int[] key,
					 int[] nonce)
	{
		// Validate arguments
		if ((key == null) || (key.length != KEY_SIZE_WORDS) ||
			 (nonce == null) || (nonce.length != NONCE_SIZE_WORDS))
			throw new IllegalArgumentException();

		// Initialise input block
		initBlock(key, nonce);
	}

	//------------------------------------------------------------------

	/**
	 * Generates a block of data with a specified counter value and stores the resulting data as a sequence
	 * of bytes in a specified buffer.
	 *
	 * @param blockCounter  the value of the block counter that will be used for generating the block.
	 * @param buffer        the buffer in which the generated data will be stored.
	 * @param offset        the offset in {@code buffer} at which the first byte of the generated data will
	 *                      be stored.
	 * @see   #getNextBlock(byte[], int)
	 */

	public void getBlock(long   blockCounter,
						 byte[] buffer,
						 int    offset)
	{
		// Set block counter in input block
		inBlock[BLOCK_COUNTER_OFFSET] = (int)blockCounter;
		inBlock[BLOCK_COUNTER_OFFSET + 1] = (int)(blockCounter >>> 32);

		// Perform hash
		hash(inBlock, outBlock, numRounds);

		// Copy output block to buffer
		for (int i = 0; i < BLOCK_SIZE_WORDS; i++)
		{
			wordToBytes(outBlock[i], buffer, offset);
			offset += BYTES_PER_WORD;
		}
	}

	//------------------------------------------------------------------

	/**
	 * Generates a block of data with the current block counter value of this cipher and stores the
	 * resulting data as a sequence of bytes in a specified buffer.
	 * <p>
	 * The block counter is incremented after the block is generated.
	 * </p>
	 *
	 * @param buffer  the buffer in which the generated data will be stored.
	 * @param offset  the offset in {@code buffer} at which the first byte of the generated data will be
	 *                stored.
	 * @see   #getBlock(long, byte[], int)
	 * @see   #getBlockCounter()
	 */

	public void getNextBlock(byte[] buffer,
							 int    offset)
	{
		getBlock(blockCounter++, buffer, offset);
	}

	//------------------------------------------------------------------

	/**
	 * Initialises the input block with a specified key and nonce.
	 *
	 * @param key    the key.
	 * @param nonce  the nonce.
	 */

	private void initBlock(int[] key,
						   int[] nonce)
	{
		// Reset block counter
		blockCounter = 0;

		// Initialise indices
		int i = 0;
		int j = 0;

		// Constant word 1
		inBlock[i++] = CONSTANT_WORDS_32[j++];

		// Key 1
		for (int k = 0; k < KEY_SIZE_WORDS / 2; k++)
			inBlock[i++] = key[k];

		// Constant word 2
		inBlock[i++] = CONSTANT_WORDS_32[j++];

		// Nonce
		for (int k = 0; k < NONCE_SIZE_WORDS; k++)
			inBlock[i++] = nonce[k];

		// Counter
		for (int k = 0; k < COUNTER_SIZE_WORDS; k++)
			inBlock[i++] = 0;

		// Constant word 3
		inBlock[i++] = CONSTANT_WORDS_32[j++];

		// Key 2
		for (int k = KEY_SIZE_WORDS / 2; k < KEY_SIZE_WORDS; k++)
			inBlock[i++] = key[k];

		// Constant word 4
		inBlock[i++] = CONSTANT_WORDS_32[j++];
	}

	//------------------------------------------------------------------

	/**
	 * Returns the identifier of this cipher: a concatenation of the key and nonce.
	 *
	 * @return the identifier of this cipher: a concatenation of the key and nonce.
	 */

	private int[] getId()
	{
		int[] id = new int[KEY_SIZE_WORDS + NONCE_SIZE_WORDS];
		int j = 0;

		for (int i = KEY1_OFFSET; i < KEY1_OFFSET + KEY_SIZE_WORDS / 2; i++)
			id[j++] = inBlock[i];

		for (int i = KEY2_OFFSET; i < KEY2_OFFSET + KEY_SIZE_WORDS / 2; i++)
			id[j++] = inBlock[i];

		for (int i = NONCE_OFFSET; i < NONCE_OFFSET + NONCE_SIZE_WORDS; i++)
			id[j++] = inBlock[i];

		return id;
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Instance fields
////////////////////////////////////////////////////////////////////////

	private	int		numRounds;
	private	long	blockCounter;
	private	int[]	inBlock;
	private	int[]	outBlock;

}

//----------------------------------------------------------------------
