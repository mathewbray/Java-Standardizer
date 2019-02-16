/*====================================================================*\

BitUtils.java

Class: bit-manipulation utility methods.

\*====================================================================*/


// PACKAGE


package common.bitarray;

//----------------------------------------------------------------------


// IMPORTS


import common.math.MathUtils;

//----------------------------------------------------------------------


// CLASS: BIT-MANIPULATION UTILITY METHODS


/**
 * This class contains utility methods for bit manipulation.
 */

public class BitUtils
{

////////////////////////////////////////////////////////////////////////
//  Constructors
////////////////////////////////////////////////////////////////////////

	/**
	 * Prevents this class from being instantiated externally.
	 */

	private BitUtils()
	{
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Class methods
////////////////////////////////////////////////////////////////////////

	/**
	 * Returns a bit mask for a specified length of a bit field.  A value of 0xFFFFFFFF is returned if {@code length}
	 * {@literal >=} 32.
	 *
	 * @param  length  the length in bits of the bit field.
	 * @return a bit mask corresponding to the length of the bit field, or 0xFFFFFFFF if {@code length} {@literal >=}
	 *         32.
	 */

	public static int lengthToMask(int length)
	{
		return ((length > 31) ? 0xFFFFFFFF : (1 << length) - 1);
	}

	//------------------------------------------------------------------

	/**
	 * Returns a bit mask for a specified length and shift count of a bit field.
	 *
	 * @param  shift   the index of the least significant bit of the bit field.
	 * @param  length  the length in bits of the bit field.
	 * @return a bit mask corresponding to the length of the bit field, shifted left by {@code shift} bits.
	 */

	public static int lengthToShiftedMask(int shift,
										  int length)
	{
		return (lengthToMask(length) << shift);
	}

	//------------------------------------------------------------------

	/**
	 * Tests the value of a bit of a 32-bit data value and returns the result.
	 *
	 * @param  data   the 32-bit data value of which a bit is to be tested.
	 * @param  shift  the index of the bit to be tested.
	 * @return {@code true} if the bit is set; {@code false} otherwise.
	 */

	public static boolean isBitSet(int data,
								   int shift)
	{
		return ((data & 1 << shift) != 0);
	}

	//------------------------------------------------------------------

	/**
	 * Sets a specified bit of a 32-bit data value and returns the result.
	 *
	 * @param  data   the 32-bit data value of which a bit is to be set.
	 * @param  shift  the index of the bit to be set.
	 * @return the result of setting the bit in {@code data}.
	 */

	public static int setBit(int data,
							 int shift)
	{
		return (data | 1 << shift);
	}

	//------------------------------------------------------------------

	/**
	 * Converts a value to a bit field and returns the result.  The conversion is performed by masking {@code value}
	 * with a mask of {@code length} bits, then shifting the result left by {@code shift} bits.
	 *
	 * @param  value   the value that is to be set in the bit field.
	 * @param  shift   the index of the least significant bit of the bit field.
	 * @param  length  the length in bits of the bit field.
	 * @return the value of the bit field (ie, {@code value} masked and shifted).
	 */

	public static int valueToBitField(int value,
									  int shift,
									  int length)
	{
		return ((value & lengthToMask(length)) << shift);
	}

	//------------------------------------------------------------------

	/**
	 * Extracts the value of a bit field from a 32-bit data value and returns the result.  The value is extracted by
	 * shifting {@code data} right by {@code shift} bits, then masking the result with a mask of {@code length} bits.
	 *
	 * @param  data    the 32-bit data value from which a bit field is to be extracted.
	 * @param  shift   the index of the least significant bit of the bit field.
	 * @param  length  the length in bits of the bit field.
	 * @return the bit field extracted from {@code data}.
	 */

	public static int getBitField(int data,
								  int shift,
								  int length)
	{
		return ((data >>> shift) & lengthToMask(length));
	}

	//------------------------------------------------------------------

	/**
	 * Extracts the value of a bit field from a 32-bit data value and returns the sign-extended result.  The value is
	 * extracted by shifting {@code data} right by {@code shift} bits, masking the result with a mask of {@code length}
	 * bits, then sign-extending the masked value into the most significant bits of the result.
	 *
	 * @param  data    the 32-bit data value from which a bit field is to be extracted.
	 * @param  shift   the index of the least significant bit of the bit field.
	 * @param  length  the length in bits of the bit field.
	 * @return the sign-extended bit field extracted from {@code data}.
	 */

	public static int getSignedBitField(int data,
										int shift,
										int length)
	{
		int mask = lengthToMask(length);
		int value = (data >>> shift) & mask;
		if ((value & 1 << (length - 1)) != 0)
			value |= ~mask;
		return value;
	}

	//------------------------------------------------------------------

	/**
	 * Sets the value of a bit field in a 32-bit data value and returns the result.  The value is set by masking {@code
	 * value} with a mask of {@code length} bits, shifting the result left by {@code shift} bits, then setting the
	 * result in {@code data}, in which the bit field has been cleared.
	 *
	 * @param  data    the 32-bit data value in which a bit field is to be set.
	 * @param  value   the value that is to be set in the bit field.
	 * @param  shift   the index of the least significant bit of the bit field.
	 * @param  length  the length in bits of the bit field.
	 * @return the result of setting the bit field in {@code data}.
	 */

	public static int setBitField(int data,
								  int value,
								  int shift,
								  int length)
	{
		int mask = lengthToMask(length);
		return ((data & ~(mask << shift)) | ((value & mask) << shift));
	}

	//------------------------------------------------------------------

	/**
	 * Interleaves the bits of a 32-bit integer value.  The effect of this method can be reversed with the
	 * {@link #deinterleave(int, int, int, int) deinterleave} method.
	 *
	 * @param  value       the value whose bits are to be interleaved.
	 * @param  startIndex  the index of the bit of {@code value} at which the interleaving is to start.
	 * @param  length      the length of {@code value} in bits.
	 * @param  interval    the interval between input bits.
	 * @return the input value with its bits interleaved.
	 * @throws IllegalArgumentException
	 *           if {@code length} is negative or greater than 32, or if {@code interval} is negative or
	 *           zero, or if {@code interval} and {@code length} have a common factor greater than 1.
	 * @throws IndexOutOfBoundsException
	 *           if {@code startIndex} is negative or greater than {@code length}.
	 * @see    #deinterleave(int, int, int, int)
	 */

	public static int interleave(int value,
								 int startIndex,
								 int length,
								 int interval)
	{
		// Validate arguments
		if ((length < 0) || (length > Integer.SIZE))
			throw new IllegalArgumentException();
		if ((startIndex < 0) || (startIndex > length))
			throw new IndexOutOfBoundsException();
		if ((interval <= 0) || (MathUtils.gcd(interval, length) > 1))
			throw new IllegalArgumentException();

		// Interleave bits of input value
		int outValue = 0;
		int index = startIndex;
		int mask = 1;
		for (int i = 0; i < length; i++)
		{
			index %= length;
			if ((value & 1 << index) != 0)
				outValue |= mask;
			mask <<= 1;
			index += interval;
		}
		return outValue;
	}

	//------------------------------------------------------------------

	/**
	 * Interleaves the bits of a 64-bit integer value.  The effect of this method can be reversed with the
	 * {@link #deinterleave(long, int, int, int) deinterleave} method.
	 *
	 * @param  value       the value whose bits are to be interleaved.
	 * @param  startIndex  the index of the bit of {@code value} at which the interleaving is to start.
	 * @param  length      the length of {@code value} in bits.
	 * @param  interval    the interval between input bits.
	 * @return the input value with its bits interleaved.
	 * @throws IllegalArgumentException
	 *           if {@code length} is negative or greater than 64, or if {@code interval} is negative or
	 *           zero, or if {@code interval} and {@code length} have a common factor greater than 1.
	 * @throws IndexOutOfBoundsException
	 *           if {@code startIndex} is negative or greater than {@code length}.
	 * @see    #deinterleave(long, int, int, int)
	 */

	public static long interleave(long value,
								  int  startIndex,
								  int  length,
								  int  interval)
	{
		// Validate arguments
		if ((length < 0) || (length > Long.SIZE))
			throw new IllegalArgumentException();
		if ((startIndex < 0) || (startIndex > length))
			throw new IndexOutOfBoundsException();
		if ((interval <= 0) || (MathUtils.gcd(interval, length) > 1))
			throw new IllegalArgumentException();

		// Interleave bits of input value
		long outValue = 0;
		int index = startIndex;
		long mask = 1;
		for (int i = 0; i < length; i++)
		{
			index %= length;
			if ((value & 1L << index) != 0)
				outValue |= mask;
			mask <<= 1;
			index += interval;
		}
		return outValue;
	}

	//------------------------------------------------------------------

	/**
	 * Deinterleaves the bits of a 32-bit integer value.  This method reverses the effect of the {@link
	 * #interleave(int, int, int, int) interleave} method.
	 *
	 * @param  value       the value whose bits are to be deinterleaved.
	 * @param  startIndex  the index of the bit of the <i>output</i> value at which the deinterleaving is to
	 *                     start.
	 * @param  length      the length of {@code value} in bits.
	 * @param  interval    the interval between output bits.
	 * @return the input value with its bits deinterleaved.
	 * @throws IllegalArgumentException
	 *           if {@code length} is negative or greater than 32, or if {@code interval} is negative or
	 *           zero, or if {@code interval} and {@code length} have a common factor greater than 1.
	 * @throws IndexOutOfBoundsException
	 *           if {@code startIndex} is negative or greater than {@code length}.
	 * @see    #interleave(int, int, int, int)
	 */

	public static int deinterleave(int value,
								   int startIndex,
								   int length,
								   int interval)
	{
		// Validate arguments
		if ((length < 0) || (length > Integer.SIZE))
			throw new IllegalArgumentException();
		if ((startIndex < 0) || (startIndex > length))
			throw new IndexOutOfBoundsException();
		if ((interval <= 0) || (MathUtils.gcd(interval, length) > 1))
			throw new IllegalArgumentException();

		// Deinterleave bits of input value
		int outValue = 0;
		int index = startIndex;
		for (int i = 0; i < length; i++)
		{
			index %= length;
			if ((value & 1) != 0)
				outValue |= 1 << index;
			value >>= 1;
			index += interval;
		}
		return outValue;
	}

	//------------------------------------------------------------------

	/**
	 * Deinterleaves the bits of a 64-bit integer value.  This method reverses the effect of the {@link
	 * #interleave(long, int, int, int) interleave} method.
	 *
	 * @param  value       the value whose bits are to be deinterleaved.
	 * @param  startIndex  the index of the bit of the <i>output</i> value at which the deinterleaving is to
	 *                     start.
	 * @param  length      the length of {@code value} in bits.
	 * @param  interval    the interval between output bits.
	 * @return the input value with its bits deinterleaved.
	 * @throws IllegalArgumentException
	 *           if {@code length} is negative or greater than 64, or if {@code interval} is negative or
	 *           zero, or if {@code interval} and {@code length} have a common factor greater than 1.
	 * @throws IndexOutOfBoundsException
	 *           if {@code startIndex} is negative or greater than {@code length}.
	 * @see    #interleave(long, int, int, int)
	 */

	public static long deinterleave(long value,
									int  startIndex,
									int  length,
									int  interval)
	{
		// Validate arguments
		if ((length < 0) || (length > Long.SIZE))
			throw new IllegalArgumentException();
		if ((startIndex < 0) || (startIndex > length))
			throw new IndexOutOfBoundsException();
		if ((interval <= 0) || (MathUtils.gcd(interval, length) > 1))
			throw new IllegalArgumentException();

		// Deinterleave bits of input value
		long outValue = 0;
		int index = startIndex;
		for (int i = 0; i < length; i++)
		{
			index %= length;
			if ((value & 1) != 0)
				outValue |= 1L << index;
			value >>= 1;
			index += interval;
		}
		return outValue;
	}

	//------------------------------------------------------------------

}

//----------------------------------------------------------------------
