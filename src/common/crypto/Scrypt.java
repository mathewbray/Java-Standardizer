/*====================================================================*\

Scrypt.java

Scrypt password-based key derivation function class.

\*====================================================================*/


// PACKAGE


package common.crypto;

//----------------------------------------------------------------------


// IMPORTS


import java.util.concurrent.Executors;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

import common.exception.UnexpectedRuntimeException;

import common.gui.RunnableMessageDialog;

import common.misc.IStringKeyed;

//----------------------------------------------------------------------


// SCRYPT PASSWORD-BASED KEY DERIVATION FUNCTION CLASS


/**
 * This class implements the scrypt password-based key derivation function (KDF).
 * <p>
 * The scrypt function is specified in an <a
 * href="https://tools.ietf.org/html/draft-josefsson-scrypt-kdf">IETF draft</a>.
 * </p>
 */

public class Scrypt
{

////////////////////////////////////////////////////////////////////////
//  Constants
////////////////////////////////////////////////////////////////////////

	/**
	 * The minimum value of CPU/memory cost parameter, which is the binary logarithm of the <i>N</i>
	 * parameter (CPU/memory cost) of the scrypt algorithm.
	 */
	public static final		int	MIN_COST	= 1;

	/**
	 * The maximum value of CPU/memory cost parameter, which is the binary logarithm of the <i>N</i>
	 * parameter (CPU/memory cost) of the scrypt algorithm.
	 */
	public static final		int	MAX_COST	= 24;

	/**
	 * The minimum value of the <i>r</i> parameter (block size) of the scrypt algorithm.
	 */
	public static final		int	MIN_NUM_BLOCKS	= 1;

	/**
	 * The maximum value of the <i>r</i> parameter (block size) of the scrypt algorithm.
	 */
	public static final		int	MAX_NUM_BLOCKS	= 1024;

	/**
	 * The minimum value of the <i>p</i> parameter (parallelisation) of the scrypt algorithm.
	 */
	public static final		int	MIN_NUM_PARALLEL_BLOCKS	= 1;

	/**
	 * The maximum value of the <i>p</i> parameter (parallelisation) of the scrypt algorithm.
	 */
	public static final		int	MAX_NUM_PARALLEL_BLOCKS	= 64;

	/**
	 * The minimum number of threads that may be created to perform the parallel processing of superblocks
	 * at the highest level of the scrypt algorithm.
	 */
	public static final		int	MIN_NUM_THREADS	= 1;

	/**
	 * The maximum number of threads that may be created to perform the parallel processing of superblocks
	 * at the highest level of the scrypt algorithm.
	 */
	public static final		int	MAX_NUM_THREADS	= 64;

	private static final	int	BYTES_PER_INT	= Integer.SIZE / Byte.SIZE;

	private static final	int	SALSA20_CORE_BLOCK_SIZE		= 64;
	private static final	int	SALSA20_CORE_BLOCK_NUM_INTS	= SALSA20_CORE_BLOCK_SIZE / BYTES_PER_INT;

	private static final	int	BLOCK_SIZE		= 2 * SALSA20_CORE_BLOCK_SIZE;
	private static final	int	BLOCK_NUM_INTS	= BLOCK_SIZE / BYTES_PER_INT;

////////////////////////////////////////////////////////////////////////
//  Enumerated types
////////////////////////////////////////////////////////////////////////


	// NUMBER OF ROUNDS, SALSA20 CORE


	/**
	 * This is an enumeration of the possible values for the number of rounds of the Salsa20 core hash
	 * function, which performs the lowest level of mixing in the scrypt algorithm.
	 * <p>
	 * In the scrypt specification, the number of rounds of the Salsa20 core is fixed at 8, but this
	 * implementation allows 8, 12, 16 or 20 rounds.
	 * </p>
	 */

	public enum Salsa20NumRounds
		implements IStringKeyed
	{

	////////////////////////////////////////////////////////////////////
	//  Constants
	////////////////////////////////////////////////////////////////////

		_8  (8),
		_12 (12),
		_16 (16),
		_20 (20);

		//--------------------------------------------------------------

		/**
		 * The default number of rounds of the Salsa20 core.
		 */
		public static final	Salsa20NumRounds	DEFAULT	= Salsa20NumRounds._8;

	////////////////////////////////////////////////////////////////////
	//  Constructors
	////////////////////////////////////////////////////////////////////

		private Salsa20NumRounds(int value)
		{
			this.value = value;
		}

		//--------------------------------------------------------------

	////////////////////////////////////////////////////////////////////
	//  Class methods
	////////////////////////////////////////////////////////////////////

		/**
		 * Returns the enum constant corresponding to a specified number of rounds.
		 *
		 * @param  numRounds  the number of rounds for which the enum constant is sought.
		 * @return the enum constant corresponding to the specified number of rounds, or {@code null} if
		 *         {@code numRounds} does not correspond to a supported value.
		 */

		public static Salsa20NumRounds forNumRounds(int numRounds)
		{
			for (Salsa20NumRounds value : values())
			{
				if (value.value == numRounds)
					return value;
			}
			return null;
		}

		//--------------------------------------------------------------

	////////////////////////////////////////////////////////////////////
	//  Instance methods : IStringKeyed interface
	////////////////////////////////////////////////////////////////////

		/**
		 * Returns the key of this enum constant.
		 *
		 * @return the key of this enum constant.
		 */

		public String getKey()
		{
			return Integer.toString(value);
		}

		//--------------------------------------------------------------

	////////////////////////////////////////////////////////////////////
	//  Instance methods : overriding methods
	////////////////////////////////////////////////////////////////////

		/**
		 * Returns a string representation of this enum constant.
		 *
		 * @return a string representation of this enum constant.
		 */

		@Override
		public String toString()
		{
			return getKey();
		}

		//--------------------------------------------------------------

	////////////////////////////////////////////////////////////////////
	//  Instance methods
	////////////////////////////////////////////////////////////////////

		/**
		 * Returns the number of rounds of the Salsa20 core as an integer.
		 *
		 * @return the number of rounds of the Salsa20 core as an integer.
		 */

		public int getValue()
		{
			return value;
		}

		//--------------------------------------------------------------

	////////////////////////////////////////////////////////////////////
	//  Instance fields
	////////////////////////////////////////////////////////////////////

		private	int	value;

	}

	//==================================================================

////////////////////////////////////////////////////////////////////////
//  Member classes : non-inner classes
////////////////////////////////////////////////////////////////////////


	// FUNCTION PARAMETERS CLASS


	/**
	 * This class encapsulates the three parameters of the scrypt algorithm: CPU/memory cost, block size and
	 * parallelisation.
	 * <p>
	 * In this class, the parameters are public fields, named as follows:
	 * </p>
	 * <ul>
	 *   <li>cost : CPU/memory cost, <i>N</i></li>
	 *   <li>numBlocks : block size, <i>r</i></li>
	 *   <li>numParallelBlocks : parallelisation, <i>p</i></li>
	 * </ul>
	 * <p>
	 * <b>Note:</b><br>
	 * In this implementation of the scrypt algorithm, the CPU/memory cost parameter is the binary logarithm
	 * of the <i>N</i> parameter in the scrypt specification; that is, the value of <i>N</i> in the scrypt
	 * specification is 2 raised to the power of the value of the parameter in this implementation.
	 * </p>
	 */

	public static class Params
		implements Cloneable
	{

	////////////////////////////////////////////////////////////////////
	//  Constructors
	////////////////////////////////////////////////////////////////////

		/**
		 * Creates a set of parameters of the scrypt algorithm, with a specified value for each of the three
		 * parameters.
		 *
		 * @param cost               the binary logarithm of the scrypt CPU/memory cost parameter, <i>N</i>.
		 * @param numBlocks          the number of blocks: the scrypt block size parameter, <i>r</i>.
		 * @param numParallelBlocks  the number of parallel superblocks: the scrypt parallelisation
		 *                           parameter, <i>p</i>.
		 */

		public Params(int cost,
					  int numBlocks,
					  int numParallelBlocks)
		{
			this.cost = cost;
			this.numBlocks = numBlocks;
			this.numParallelBlocks = numParallelBlocks;
		}

		//--------------------------------------------------------------

	////////////////////////////////////////////////////////////////////
	//  Instance methods : overriding methods
	////////////////////////////////////////////////////////////////////

		/**
		 * Creates a copy of this set of scrypt parameters.
		 *
		 * @return a copy of this set of scrypt parameters.
		 */

		@Override
		public Params clone()
		{
			try
			{
				return (Params)super.clone();
			}
			catch (CloneNotSupportedException e)
			{
				throw new UnexpectedRuntimeException();
			}
		}

		//--------------------------------------------------------------

	////////////////////////////////////////////////////////////////////
	//  Instance fields
	////////////////////////////////////////////////////////////////////

		/**
		 * The binary logarithm of the scrypt CPU/memory cost parameter, <i>N</i>.
		 */
		public	int	cost;

		/**
		 * The number of blocks: the scrypt block size parameter, <i>r</i>.
		 */
		public	int	numBlocks;

		/**
		 * The number of parallel superblocks: the scrypt parallelisation parameter, <i>p</i>.
		 */
		public	int	numParallelBlocks;

	}

	//==================================================================


	// KEY GENERATOR CLASS


	/**
	 * This class implements a convenient mechanism for running the scrypt key derivation function (KDF) in
	 * its own thread.
	 * <p>
	 * It is intended that the KDF be run by invoking the {@link KeyGenerator#run() run()} method of this
	 * class from an instance of {@code common.gui.RunnableMessageDialog}.  The {@link
	 * KeyGenerator#run() run()} method handles the two expected error conditions,
	 * {@code IllegalArgumentException} and {@code OutOfMemoryError}, by setting flags that can be tested by
	 * the caller.
	 * </p>
	 */

	public static class KeyGenerator
		implements RunnableMessageDialog.IRunnable
	{

	////////////////////////////////////////////////////////////////////
	//  Constructors
	////////////////////////////////////////////////////////////////////

		/**
		 * Creates an instance of {@code KeyGenerator} for deriving a key from a specified key and salt by
		 * means of the scrypt key derivation function with the specified KDF parameters.
		 *
		 * @param key            the key from which the key will be derived.
		 * @param salt           the salt from which the key will be derived.
		 * @param params         the parameters of the KDF that will derive the key.
		 * @param maxNumThreads  the maximum number of threads that should be allocated for the processing
		 *                       of parallel superblocks in the KDF.
		 * @param outKeyLength   the length (in bytes) of the derived key, which must be a positive integral
		 *                       multiple of 32.
		 * @param messageStr     the message that will be displayed in the dialog if the KDF is run from an
		 *                       instance of {@code common.gui.RunnableMessageDialog}.
		 */

		public KeyGenerator(byte[]        key,
							byte[]        salt,
							Scrypt.Params params,
							int           maxNumThreads,
							int           outKeyLength,
							String        messageStr)
		{
			this.key = key;
			this.salt = salt;
			this.params = params;
			this.maxNumThreads = maxNumThreads;
			this.outKeyLength = outKeyLength;
			this.messageStr = messageStr;
		}

		//--------------------------------------------------------------

	////////////////////////////////////////////////////////////////////
	//  Instance methods : RunnableMessageDialog.IRunnable interface
	////////////////////////////////////////////////////////////////////

		/**
		 * Returns the message for this generator.
		 * <p>
		 * If the {@link KeyGenerator#run() run()} method of this generator is invoked from an instance of
		 * {@code common.gui.RunnableMessageDialog}, the message will be displayed in the
		 * dialog.
		 * </p>
		 *
		 * @return the message for this generator.
		 */

		@Override
		public String getMessage()
		{
			return messageStr;
		}

		//--------------------------------------------------------------

		/**
		 * Runs the scrypt key derivation function using the key, salt and KDF parameters that were
		 * specified when this object was created.
		 * <p>
		 * The two expected error conditions, {@code IllegalArgumentException} and {@code OutOfMemoryError},
		 * are handled by setting flags that can be tested by the caller with {@link
		 * #isInvalidParameterValue()} and {@code #isOutOfMemory()} respectively.
		 * </p>
		 * <p>
		 * If the KDF completes normally, the derived key can be accessed with {@link #getDerivedKey()}.
		 * </p>
		 */

		@Override
		public void run()
		{
			try
			{
				derivedKey = deriveKey(key, salt, params, maxNumThreads, outKeyLength);
			}
			catch (IllegalArgumentException e)
			{
				invalidParameterValue = true;
			}
			catch (OutOfMemoryError e)
			{
				outOfMemory = true;
			}
		}

		//--------------------------------------------------------------

	////////////////////////////////////////////////////////////////////
	//  Instance methods
	////////////////////////////////////////////////////////////////////

		/**
		 * Returns {@code true} if a parameter of the scrypt KDF was invalid when the KDF was executed from
		 * this object's {@link #run()} method.
		 *
		 * @return {@code true} if a parameter of the scrypt KDF was invalid when the KDF was executed from
		 *         this object's {@link #run()} method, {@code false} otherwise.
		 * @see    #run()
		 */

		public boolean isInvalidParameterValue()
		{
			return invalidParameterValue;
		}

		//--------------------------------------------------------------

		/**
		 * Returns {@code true} if there was not enough memory for the scrypt KDF when it was executed from
		 * this object's {@link #run()} method.
		 *
		 * @return {@code true} if there was not enough memory for the scrypt KDF when it was executed from
		 *         this object's {@link #run()} method, {@code false} otherwise.
		 * @see    #run()
		 */

		public boolean isOutOfMemory()
		{
			return outOfMemory;
		}

		//--------------------------------------------------------------

		/**
		 * Returns the key that was derived by the scrypt KDF when it was executed from this object's {@link
		 * #run()} method.
		 *
		 * @return the key that was derived by the scrypt KDF when it was executed from this object's {@link
		 *         #run()} method.  The returned value will be {@code null} if the KDF has not been executed
		 *         or if it did not complete normally.
		 */

		public byte[] getDerivedKey()
		{
			return derivedKey;
		}

		//--------------------------------------------------------------

	////////////////////////////////////////////////////////////////////
	//  Instance fields
	////////////////////////////////////////////////////////////////////

		private	byte[]			key;
		private	byte[]			salt;
		private	Scrypt.Params	params;
		private	int				maxNumThreads;
		private	int				outKeyLength;
		private	String			messageStr;
		private	byte[]			derivedKey;
		private	boolean			invalidParameterValue;
		private	boolean			outOfMemory;

	}

	//==================================================================


	// MIXER CLASS


	/**
	 * This class implements the mixing of a parallel superblock at the highest level of the scrypt key
	 * derivation function.
	 * <p>
	 * The superblocks of the scrypt KDF can be processed independently of each other, which makes the set
	 * of tasks suitable for execution in parallel.  Multiple instances of this class are created and their
	 * {@link Mixer#run() run()} methods executed concurrently in separate threads that are created by the
	 * {@link Scrypt#deriveKey(byte[], byte[], int, int, int, int, int)} method.
	 * </p>
	 */

	private static class Mixer
		implements Runnable
	{

	////////////////////////////////////////////////////////////////////
	//  Constructors
	////////////////////////////////////////////////////////////////////

		private Mixer(byte[] data,
					  int    offset,
					  int    length,
					  int    cost)
		{
			this.data = data;
			this.offset = offset;
			this.length = length;
			this.cost = cost;
		}

		//--------------------------------------------------------------

	////////////////////////////////////////////////////////////////////
	//  Instance methods : Runnable interface
	////////////////////////////////////////////////////////////////////

		/**
		 * Mixes a single superblock at the highest level of the scrypt KDF.
		 */

		public void run()
		{
			try
			{
				// Convert the key data to integers
				int[] buffer = new int[length];
				int j = offset;
				for (int i = 0; i < length; i++)
					buffer[i] = (data[j++] & 0xFF) | (data[j++] & 0xFF) << 8 |
													(data[j++] & 0xFF) << 16 | (data[j++] & 0xFF) << 24;

				// Mix the key data
				sMix(buffer, buffer, cost);

				// Convert the mixed key data back to bytes
				j = offset;
				for (int i = 0; i < length; i++)
				{
					int value = buffer[i];
					data[j++] = (byte)value;
					data[j++] = (byte)(value >>> 8);
					data[j++] = (byte)(value >>> 16);
					data[j++] = (byte)(value >>> 24);
				}
			}
			catch (OutOfMemoryError e)
			{
				outOfMemory = true;
			}
		}

		//--------------------------------------------------------------

	////////////////////////////////////////////////////////////////////
	//  Class fields
	////////////////////////////////////////////////////////////////////

		private static volatile	boolean	outOfMemory;

	////////////////////////////////////////////////////////////////////
	//  Instance fields
	////////////////////////////////////////////////////////////////////

		private	byte[]	data;
		private	int		offset;
		private	int		length;
		private	int		cost;

	}

	//==================================================================

////////////////////////////////////////////////////////////////////////
//  Constructors
////////////////////////////////////////////////////////////////////////

	/**
	 * Creates an instance of the {@code Scrypt} class, whose methods implement the scrypt key derivation
	 * function.
	 * <p>
	 * All the methods of the {@code Scrypt} class are static, but this constructor has protected access to
	 * allow inheritance.
	 * </p>
	 */

	protected Scrypt()
	{
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Class methods
////////////////////////////////////////////////////////////////////////

	/**
	 * Returns the number of rounds of the Salsa20 core that will be performed by the scrypt key derivation
	 * function.
	 * <p>
	 * In the scrypt specification, the number of rounds is fixed at 8, but this implementation allows the
	 * number of rounds to be 8, 12, 16 or 20.
	 * </p>
	 * <p>
	 * The default value is 8.
	 * </p>
	 *
	 * @return the number of rounds of the Salsa20 core that will be performed by the scrypt key derivation
	 *         function.
	 * @see    #setSalsa20CoreNumRounds(Salsa20NumRounds)
	 */

	public static Salsa20NumRounds getSalsa20CoreNumRounds()
	{
		return salsa20CoreNumRounds;
	}

	//------------------------------------------------------------------

	/**
	 * Sets the number of rounds of the Salsa20 core that will be performed by the scrypt key derivation
	 * function.
	 * <p>
	 * In the scrypt specification, the number of rounds is fixed at 8, but this implementation allows the
	 * number of rounds to be 8, 12, 16 or 20.
	 * </p>
	 *
	 * @param  numRounds  the number of rounds of the Salsa20 core that will be performed by the scrypt key
	 *                    derivation function.
	 * @throws IllegalArgumentException
	 *           if {@code numRounds} is {@code null}.
	 * @see    #getSalsa20CoreNumRounds()
	 */

	public static void setSalsa20CoreNumRounds(Salsa20NumRounds numRounds)
	{
		if (numRounds == null)
			throw new IllegalArgumentException();

		salsa20CoreNumRounds = numRounds;
	}

	//------------------------------------------------------------------

	/**
	 * Derives a key from a specified key and salt using the scrypt key derivation function with the
	 * specified parameters, and returns the derived key.
	 *
	 * @param  key            the key from which the key will be derived.
	 * @param  salt           the salt from which the key will be derived.
	 * @param  params         the parameters of the scrypt algorithm: CPU/memory cost, block size and
	 *                        parallelisation.
	 * @param  maxNumThreads  the maximum number of threads that will be created to perform the mixing of
	 *                        the parallel superblocks at the highest level of the KDF.
	 * @param  outKeyLength   the length (in bytes) of the derived key, which must be a positive integral
	 *                        multiple of 32.
	 * @return a derived key of length {@code outKeyLength}.
	 * @throws IllegalArgumentException
	 *           if
	 *           <ul>
	 *             <li>{@code key} is {@code null}, or</li>
	 *             <li>{@code salt} is {@code null}, or</li>
	 *             <li>{@code params.cost} is less than 1 or greater than 24, or</li>
	 *             <li>{@code params.numBlocks} is less than 1 or greater than 1024, or</li>
	 *             <li>{@code params.numParallelBlocks} is less than 1 or greater than 64, or</li>
	 *             <li>{@code maxNumThreads} is less than 1 or greater than 64, or</li>
	 *             <li>{@code outKeyLength} is not a positive integral multiple of 32.</li>
	 *           </ul>
	 * @see    #deriveKey(byte[], byte[], int, int, int, int, int)
	 * @see    Scrypt.Params
	 */

	public static byte[] deriveKey(byte[] key,
								   byte[] salt,
								   Params params,
								   int    maxNumThreads,
								   int    outKeyLength)
	{
		return deriveKey(key, salt, params.cost, params.numBlocks, params.numParallelBlocks, maxNumThreads,
						 outKeyLength);
	}

	//------------------------------------------------------------------

	/**
	 * Derives a key from a specified key and salt using the scrypt key derivation function with the
	 * specified parameters, and returns the derived key.
	 *
	 * @param  key                the key from which the key will be derived.
	 * @param  salt               the salt from which the key will be derived.
	 * @param  cost               the binary logarithm of the scrypt CPU/memory cost parameter, <i>N</i>.
	 * @param  numBlocks          the number of blocks: the scrypt block size parameter, <i>r</i>.
	 * @param  numParallelBlocks  the number of parallel superblocks: the scrypt parallelisation
	 *                            parameter, <i>p</i>.
	 * @param  maxNumThreads      the maximum number of threads that will be created to perform the mixing
	 *                            of the parallel superblocks at the highest level of the KDF.
	 * @param  outKeyLength       the length (in bytes) of the derived key, which must be a positive
	 *                            integral multiple of 32.
	 * @return a derived key of length {@code outKeyLength}.
	 * @throws IllegalArgumentException
	 *           if
	 *           <ul>
	 *             <li>{@code key} is {@code null}, or</li>
	 *             <li>{@code salt} is {@code null}, or</li>
	 *             <li>{@code cost} is less than 1 or greater than 24, or</li>
	 *             <li>{@code numBlocks} is less than 1 or greater than 1024, or</li>
	 *             <li>{@code numParallelBlocks} is less than 1 or greater than 64, or</li>
	 *             <li>{@code maxNumThreads} is less than 1 or greater than 64, or</li>
	 *             <li>{@code outKeyLength} is not a positive integral multiple of 32.</li>
	 *           </ul>
	 * @see    #deriveKey(byte[], byte[], Params, int, int)
	 */

	public static byte[] deriveKey(byte[] key,
								   byte[] salt,
								   int    cost,
								   int    numBlocks,
								   int    numParallelBlocks,
								   int    maxNumThreads,
								   int    outKeyLength)
	{
		final	int	NUM_ITERATIONS	= 1;

		// Validate arguments
		if ((key == null) || (salt == null) ||
			 (cost < MIN_COST) || (cost > MAX_COST) ||
			 (numBlocks < MIN_NUM_BLOCKS) || (numBlocks > MAX_NUM_BLOCKS) ||
			 (numParallelBlocks < MIN_NUM_PARALLEL_BLOCKS) ||
			 (numParallelBlocks > MAX_NUM_PARALLEL_BLOCKS) ||
			 (maxNumThreads < MIN_NUM_THREADS) || (maxNumThreads > MAX_NUM_THREADS) ||
			 (outKeyLength <= 0) || (outKeyLength % HmacSha256.HASH_VALUE_SIZE != 0))
			throw new IllegalArgumentException();

		// Maximise available memory
		System.gc();

		// Generate key data from the input key and salt
		int parallelBlockSize = numBlocks * BLOCK_SIZE;
		byte[] keyData = pbkdf2HmacSha256(key, salt, NUM_ITERATIONS,
										  numParallelBlocks * parallelBlockSize);

		// Mix the key data using a thread pool
		Mixer.outOfMemory = false;
		ExecutorService executor =
							Executors.newFixedThreadPool(Math.min(numParallelBlocks, maxNumThreads));
		for (int offset = 0; offset < keyData.length; offset += parallelBlockSize)
		{
			executor.execute(new Mixer(keyData, offset, numBlocks * BLOCK_NUM_INTS, cost));
			if (Mixer.outOfMemory)
				break;
		}

		// Wait for threads to terminate
		try
		{
			executor.shutdown();
			if (!executor.awaitTermination(1000, TimeUnit.SECONDS))
				throw new UnexpectedRuntimeException();
		}
		catch (InterruptedException e)
		{
			throw new UnexpectedRuntimeException();
		}
		if (Mixer.outOfMemory)
			throw new OutOfMemoryError();

		// Return a key derived from the input key and processed key data
		return pbkdf2HmacSha256(key, keyData, NUM_ITERATIONS, outKeyLength);
	}

	//------------------------------------------------------------------

	/**
	 * Mixes the specified block of data at the intermediate level of the scrypt KDF.
	 *
	 * @param in   the data that will be mixed.
	 * @param out  a buffer in which the mixed output data will be stored.
	 */

	protected static void blockMix(int[] in,
								   int[] out)
	{
		final	int	BLOCK_LENGTH	= SALSA20_CORE_BLOCK_NUM_INTS;

		// Copy the last block of input data to the X array
		int length = in.length;
		int[] x = new int[BLOCK_LENGTH];
		System.arraycopy(in, in.length - BLOCK_LENGTH, x, 0, BLOCK_LENGTH);

		// Hash the input data with the Salsa20 function
		int[] y = new int[length];
		int[] z = new int[BLOCK_LENGTH];
		int j = 0;
		for (int offset = 0; offset < length; offset += BLOCK_LENGTH)
		{
			for (int i = 0; i < BLOCK_LENGTH; i++)
				z[i] = x[i] ^ in[j++];

			Salsa20.hash(z, x, salsa20CoreNumRounds.value);

			System.arraycopy(x, 0, y, offset, BLOCK_LENGTH);
		}

		// Move the processed input data to the output array
		int offset = 0;
		int offset0 = 0;
		int offset1 = length / 2;
		while (offset < length)
		{
			System.arraycopy(y, offset, out, offset0, BLOCK_LENGTH);
			offset += BLOCK_LENGTH;
			offset0 += BLOCK_LENGTH;

			System.arraycopy(y, offset, out, offset1, BLOCK_LENGTH);
			offset += BLOCK_LENGTH;
			offset1 += BLOCK_LENGTH;
		}
	}

	//------------------------------------------------------------------

	/**
	 * Mixes the specified superblock of data at the highest level of the scrypt KDF.
	 *
	 * @param in    the data that will be mixed.
	 * @param out   a buffer in which the mixed output data will be stored.
	 * @param cost  the binary logarithm of the scrypt CPU/memory cost parameter, <i>N</i>.
	 */

	protected static void sMix(int[] in,
							   int[] out,
							   int   cost)
	{
		// Copy the input data to the X array
		int length = in.length;
		int[] x = new int[length];
		System.arraycopy(in, 0, x, 0, length);

		// Mix the data in the costly V array
		int numIterations = 1 << cost;
		int[][] v = new int[numIterations][length];
		for (int i = 0; i < numIterations; i++)
		{
			System.arraycopy(x, 0, v[i], 0, length);

			blockMix(x, x);
		}

		// Perform further mixing
		int mask = numIterations - 1;
		int[] t = new int[length];
		for (int i = 0; i < numIterations; i++)
		{
			int[] vv = v[x[length - (BLOCK_NUM_INTS / 2)] & mask];
			for (int j = 0; j < length; j++)
				t[j] = x[j] ^ vv[j];

			blockMix(t, x);
		}

		// Copy the mixed data to the output array
		System.arraycopy(x, 0, out, 0, length);
	}

	//------------------------------------------------------------------

	/**
	 * Returns the HMAC-SHA256 hash value for the specified key and data.
	 * <p>
	 * HMAC-SHA256 is a hash-based message authentication code whose underlying function is the SHA-256
	 * cryptographic hash function.
	 * </p>
	 *
	 * @param  key   the key for the HMAC.
	 * @param  data  the data that will be hashed by the HMAC function.
	 * @return the HMAC-SHA256 hash value for the specified key and data.
	 */

	protected static byte[] hmacSha256(byte[] key,
									   byte[] data)
	{
		return new HmacSha256(key).getValue(data);
	}

	//------------------------------------------------------------------

	/**
	 * Derives a key of a specified length from a specified key and salt using the PBKDF2 function whose
	 * underlying hash function is HMAC-SHA256 (hash-based message authentication code using the SHA-256
	 * cryptographic hash function), and returns the derived key.
	 * <p>
	 * PBKDF2 is specified in <a href="http://tools.ietf.org/html/rfc2898">IETF RFC2898</a>.
	 * </p>
	 *
	 * @param  key            the key from which the key will be derived.
	 * @param  salt           the salt from which the key will be derived.
	 * @param  numIterations  the number of iterations of the HMAC-SHA256 algorithm that will be applied.
	 * @param  outKeyLength   the length (in bytes) of the derived key, which must be a positive integral
	 *                        multiple of 32.
	 * @return a derived key of length {@code outKeyLength}.
	 * @throws IllegalArgumentException
	 *           if {@code outKeyLength} is not a positive integral multiple of 32.
	 */

	protected static byte[] pbkdf2HmacSha256(byte[] key,
											 byte[] salt,
											 int    numIterations,
											 int    outKeyLength)
	{
		final	int	INDEX_LENGTH	= 4;

		// Validate length of derived key
		if ((outKeyLength <= 0) || (outKeyLength % HmacSha256.HASH_VALUE_SIZE != 0))
			throw new IllegalArgumentException();

		// Copy salt to array to allow its extension with the block index
		byte[] extendedSalt = new byte[salt.length + INDEX_LENGTH];
		System.arraycopy(salt, 0, extendedSalt, 0, salt.length);

		// Repeatedly hash the input key and the salt extended with the block index
		byte[] outKey = new byte[outKeyLength];
		int index = 0;
		for (int offset = 0; offset < outKeyLength; offset += HmacSha256.HASH_VALUE_SIZE)
		{
			// Extend the salt with the block index
			int value = ++index;
			for (int i = extendedSalt.length - 1; i >= salt.length; i--)
			{
				extendedSalt[i] = (byte)value;
				value >>>= 8;
			}

			// Repeatedly hash the input key and extended salt
			byte[] result = new byte[HmacSha256.HASH_VALUE_SIZE];
			byte[] hashValue = extendedSalt;
			for (int i = 0; i < numIterations; i++)
			{
				hashValue = hmacSha256(key, hashValue);
				for (int j = 0; j < HmacSha256.HASH_VALUE_SIZE; j++)
					result[j] ^= hashValue[j];
			}

			// Copy the result to the output array
			System.arraycopy(result, 0, outKey, offset, HmacSha256.HASH_VALUE_SIZE);
		}

		// Return the derived key
		return outKey;
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Class fields
////////////////////////////////////////////////////////////////////////

	private static	Salsa20NumRounds	salsa20CoreNumRounds	= Salsa20NumRounds.DEFAULT;

}

//----------------------------------------------------------------------
