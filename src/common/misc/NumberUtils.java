/*====================================================================*\

NumberUtils.java

Number utility methods class.

\*====================================================================*/


// PACKAGE


package common.misc;

//----------------------------------------------------------------------


// IMPORTS


import java.math.BigInteger;

import java.util.Arrays;
import java.util.List;

import common.collection.CollectionUtils;

import common.exception.ValueOutOfBoundsException;

//----------------------------------------------------------------------


// NUMBER UTILITY METHODS CLASS


public class NumberUtils
{

////////////////////////////////////////////////////////////////////////
//  Constants
////////////////////////////////////////////////////////////////////////

	public static final		String	HEX_DIGITS_UPPER	= "0123456789ABCDEF";
	public static final		String	HEX_DIGITS_LOWER	= "0123456789abcdef";

	public static final		int[]	POWERS_OF_TEN_INT;
	public static final		long[]	POWERS_OF_TEN_LONG;

	public enum DigitCase
	{
		UPPER,
		LOWER
	}

	private static final	int	DEFAULT_BYTES_PER_LINE	= 8;
	private static final	int	DEFAULT_INTS_PER_LINE	= 4;

////////////////////////////////////////////////////////////////////////
//  Enumerated types
////////////////////////////////////////////////////////////////////////


	// RADIX


	public enum Radix
	{

	////////////////////////////////////////////////////////////////////
	//  Constants
	////////////////////////////////////////////////////////////////////

		BINARY      (2,  "0b"),
		DECIMAL     (10, ""),
		HEXADECIMAL (16, "0x");

		//--------------------------------------------------------------

		private static final	int	PREFIX_LENGTH	= 2;

	////////////////////////////////////////////////////////////////////
	//  Constructors
	////////////////////////////////////////////////////////////////////

		private Radix(int    value,
					  String prefix)
		{
			this.value = value;
			this.prefix = prefix;
		}

		//--------------------------------------------------------------

	////////////////////////////////////////////////////////////////////
	//  Instance methods
	////////////////////////////////////////////////////////////////////

		public int getValue()
		{
			return value;
		}

		//--------------------------------------------------------------

		public String getPrefix()
		{
			return prefix;
		}

		//--------------------------------------------------------------

	////////////////////////////////////////////////////////////////////
	//  Instance fields
	////////////////////////////////////////////////////////////////////

		private	int		value;
		private	String	prefix;

	}

	//==================================================================

////////////////////////////////////////////////////////////////////////
//  Constructors
////////////////////////////////////////////////////////////////////////

	/**
	 * Prevents this class from being instantiated externally.
	 */

	private NumberUtils()
	{
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Class methods
////////////////////////////////////////////////////////////////////////

	public static boolean setHexUpper()
	{
		if (hexDigits == HEX_DIGITS_UPPER)
			return false;
		hexDigits = HEX_DIGITS_UPPER;
		return true;
	}

	//------------------------------------------------------------------

	public static boolean setHexLower()
	{
		if (hexDigits == HEX_DIGITS_LOWER)
			return false;
		hexDigits = HEX_DIGITS_LOWER;
		return true;
	}

	//------------------------------------------------------------------

	public static DigitCase setHexDigitCase(DigitCase digitCase)
	{
		DigitCase oldCase = (hexDigits == HEX_DIGITS_UPPER) ? DigitCase.UPPER : DigitCase.LOWER;
		switch (digitCase)
		{
			case UPPER:
				hexDigits = HEX_DIGITS_UPPER;
				break;

			case LOWER:
				hexDigits = HEX_DIGITS_LOWER;
				break;
		}
		return oldCase;
	}

	//------------------------------------------------------------------

	public static int roundUpQuotientInt(int value,
										 int interval)
	{
		return ((value + interval - 1) / interval);
	}

	//------------------------------------------------------------------

	public static long roundUpQuotientLong(long value,
										   long interval)
	{
		return ((value + interval - 1) / interval);
	}

	//------------------------------------------------------------------

	public static int roundUpInt(int value,
								 int interval)
	{
		return ((value + interval - 1) / interval * interval);
	}

	//------------------------------------------------------------------

	public static long roundUpLong(long value,
								   long interval)
	{
		return ((value + interval - 1) / interval * interval);
	}

	//------------------------------------------------------------------

	public static int getNumDecDigitsInt(int value)
	{
		if (value < 0)
		{
			if (value == Integer.MIN_VALUE)
				return POWERS_OF_TEN_INT.length;
			value = -value;
		}
		int i = 1;
		while (i < POWERS_OF_TEN_INT.length)
		{
			if (value < POWERS_OF_TEN_INT[i])
				break;
			++i;
		}
		return i;
	}

	//------------------------------------------------------------------

	public static int getNumDecDigitsLong(long value)
	{
		if (value < 0)
		{
			if (value == Long.MIN_VALUE)
				return POWERS_OF_TEN_LONG.length;
			value = -value;
		}
		int i = 1;
		while (i < POWERS_OF_TEN_LONG.length)
		{
			if (value < POWERS_OF_TEN_LONG[i])
				break;
			++i;
		}
		return i;
	}

	//------------------------------------------------------------------

	public static int bytesToIntBE(byte[] data)
	{
		return bytesToIntBE(data, 0, data.length);
	}

	//------------------------------------------------------------------

	public static int bytesToIntBE(byte[] data,
								   int    offset,
								   int    length)
	{
		int endOffset = offset + length;
		int value = data[offset];
		while (++offset < endOffset)
		{
			value <<= 8;
			value |= data[offset] & 0xFF;
		}
		return value;
	}

	//------------------------------------------------------------------

	public static int bytesToUIntBE(byte[] data)
	{
		return bytesToUIntBE(data, 0, data.length);
	}

	//------------------------------------------------------------------

	public static int bytesToUIntBE(byte[] data,
									int    offset,
									int    length)
	{
		int endOffset = offset + length;
		int value = 0;
		while (offset < endOffset)
		{
			value <<= 8;
			value |= data[offset++] & 0xFF;
		}
		return value;
	}

	//------------------------------------------------------------------

	public static int bytesToIntLE(byte[] data)
	{
		return bytesToIntLE(data, 0, data.length);
	}

	//------------------------------------------------------------------

	public static int bytesToIntLE(byte[] data,
								   int    offset,
								   int    length)
	{
		int i = offset + length;
		int value = data[--i];
		while (--i >= offset)
		{
			value <<= 8;
			value |= data[i] & 0xFF;
		}
		return value;
	}

	//------------------------------------------------------------------

	public static int bytesToUIntLE(byte[] data)
	{
		return bytesToUIntLE(data, 0, data.length);
	}

	//------------------------------------------------------------------

	public static int bytesToUIntLE(byte[] data,
									int    offset,
									int    length)
	{
		int i = offset + length;
		int value = 0;
		while (--i >= offset)
		{
			value <<= 8;
			value |= data[i] & 0xFF;
		}
		return value;
	}

	//------------------------------------------------------------------

	public static void intToBytesBE(int    value,
									byte[] buffer)
	{
		intToBytesBE(value, buffer, 0, buffer.length);
	}

	//------------------------------------------------------------------

	public static void intToBytesBE(int    value,
									byte[] buffer,
									int    offset,
									int    length)
	{
		for (int i = offset + length - 1; i >= offset; i--)
		{
			buffer[i] = (byte)value;
			value >>>= 8;
		}
	}

	//------------------------------------------------------------------

	public static void intToBytesLE(int    value,
									byte[] buffer)
	{
		intToBytesLE(value, buffer, 0, buffer.length);
	}

	//------------------------------------------------------------------

	public static void intToBytesLE(int    value,
									byte[] buffer,
									int    offset,
									int    length)
	{
		int endOffset = offset + length;
		for (int i = offset; i < endOffset; i++)
		{
			buffer[i] = (byte)value;
			value >>>= 8;
		}
	}

	//------------------------------------------------------------------

	public static long bytesToLongBE(byte[] data)
	{
		return bytesToLongBE(data, 0, data.length);
	}

	//------------------------------------------------------------------

	public static long bytesToLongBE(byte[] data,
									 int    offset,
									 int    length)
	{
		int endOffset = offset + length;
		long value = data[offset];
		while (++offset < endOffset)
		{
			value <<= 8;
			value |= data[offset] & 0xFF;
		}
		return value;
	}

	//------------------------------------------------------------------

	public static long bytesToULongBE(byte[] data)
	{
		return bytesToULongBE(data, 0, data.length);
	}

	//------------------------------------------------------------------

	public static long bytesToULongBE(byte[] data,
									  int    offset,
									  int    length)
	{
		int endOffset = offset + length;
		long value = 0;
		while (offset < endOffset)
		{
			value <<= 8;
			value |= data[offset++] & 0xFF;
		}
		return value;
	}

	//------------------------------------------------------------------

	public static long bytesToLongLE(byte[] data)
	{
		return bytesToLongLE(data, 0, data.length);
	}

	//------------------------------------------------------------------

	public static long bytesToLongLE(byte[] data,
									 int    offset,
									 int    length)
	{
		int i = offset + length;
		long value = data[--i];
		while (--i >= offset)
		{
			value <<= 8;
			value |= data[i] & 0xFF;
		}
		return value;
	}

	//------------------------------------------------------------------

	public static long bytesToULongLE(byte[] data)
	{
		return bytesToULongLE(data, 0, data.length);
	}

	//------------------------------------------------------------------

	public static long bytesToULongLE(byte[] data,
									  int    offset,
									  int    length)
	{
		int i = offset + length;
		long value = 0;
		while (--i >= offset)
		{
			value <<= 8;
			value |= data[i] & 0xFF;
		}
		return value;
	}

	//------------------------------------------------------------------

	public static void longToBytesBE(long   value,
									 byte[] buffer)
	{
		longToBytesBE(value, buffer, 0, buffer.length);
	}

	//------------------------------------------------------------------

	public static void longToBytesBE(long   value,
									 byte[] buffer,
									 int    offset,
									 int    length)
	{
		for (int i = offset + length - 1; i >= offset; i--)
		{
			buffer[i] = (byte)value;
			value >>>= 8;
		}
	}

	//------------------------------------------------------------------

	public static void longToBytesLE(long   value,
									 byte[] buffer)
	{
		longToBytesLE(value, buffer, 0, buffer.length);
	}

	//------------------------------------------------------------------

	public static void longToBytesLE(long   value,
									 byte[] buffer,
									 int    offset,
									 int    length)
	{
		int endOffset = offset + length;
		for (int i = offset; i < endOffset; i++)
		{
			buffer[i] = (byte)value;
			value >>>= 8;
		}
	}

	//------------------------------------------------------------------

	public static String byteToHexString(int value)
	{
		return new String(new char[]{ hexDigits.charAt((value >> 4) & 0x0F), hexDigits.charAt(value & 0x0F) });
	}

	//------------------------------------------------------------------

	public static String uIntToBinString(int value)
	{
		return uIntToBinString(value, 0, '\0');
	}

	//------------------------------------------------------------------

	public static String uIntToBinString(int  value,
										 int  numDigits,
										 char padChar)
	{
		char[] buffer = new char[(numDigits > 0) ? numDigits : Integer.SIZE];
		if (numDigits > 0)
			Arrays.fill(buffer, padChar);
		int i = buffer.length;
		while (--i >= 0)
		{
			buffer[i] = (char)((value & 1) + '0');
			value >>>= 1;
			if (value == 0)
				break;
		}
		return ((numDigits > 0) ? new String(buffer) : new String(buffer, i, buffer.length - i));
	}

	//------------------------------------------------------------------

	public static String uIntToPrefixedBinString(int value)
	{
		return uIntToPrefixedBinString(value, 0, '\0');
	}

	//------------------------------------------------------------------

	public static String uIntToPrefixedBinString(int  value,
												 int  numDigits,
												 char padChar)
	{
		return (Radix.BINARY.prefix + uIntToBinString(value, numDigits, padChar));
	}

	//------------------------------------------------------------------

	public static String uLongToBinString(long value)
	{
		return uLongToBinString(value, 0, '\0');
	}

	//------------------------------------------------------------------

	public static String uLongToBinString(long value,
										  int  numDigits,
										  char padChar)
	{
		char[] buffer = new char[(numDigits > 0) ? numDigits : Long.SIZE];
		if (numDigits > 0)
			Arrays.fill(buffer, padChar);
		int i = buffer.length;
		while (--i >= 0)
		{
			buffer[i] = (char)((value & 1) + '0');
			value >>>= 1;
			if (value == 0)
				break;
		}
		return ((numDigits > 0) ? new String(buffer) : new String(buffer, i, buffer.length - i));
	}

	//------------------------------------------------------------------

	public static String uLongToPrefixedBinString(long value)
	{
		return uLongToPrefixedBinString(value, 0, '\0');
	}

	//------------------------------------------------------------------

	public static String uLongToPrefixedBinString(long value,
												  int  numDigits,
												  char padChar)
	{
		return (Radix.BINARY.prefix + uLongToBinString(value, numDigits, padChar));
	}

	//------------------------------------------------------------------

	public static String uIntToDecString(int value)
	{
		return uIntToDecString(value, 0, '\0');
	}

	//------------------------------------------------------------------

	public static String uIntToDecString(int  value,
										 int  numDigits,
										 char padChar)
	{
		char[] buffer = new char[(numDigits > 0) ? numDigits : 10];
		if (numDigits > 0)
			Arrays.fill(buffer, padChar);
		int i = buffer.length;
		while (--i >= 0)
		{
			buffer[i] = (char)(value % 10 + '0');
			value /= 10;
			if (value == 0)
				break;
		}
		return ((numDigits > 0) ? new String(buffer) : new String(buffer, i, buffer.length - i));
	}

	//------------------------------------------------------------------

	public static String uLongToDecString(long value)
	{
		byte[] buffer = new byte[8];
		longToBytesBE(value, buffer, 0, buffer.length);
		return new BigInteger(1, buffer).toString();
	}

	//------------------------------------------------------------------

	public static String uLongToDecString(long value,
										  int  numDigits,
										  char padChar)
	{
		char[] buffer = new char[(numDigits > 0) ? numDigits : 20];
		if (numDigits > 0)
			Arrays.fill(buffer, padChar);
		int i = buffer.length;
		while (--i >= 0)
		{
			buffer[i] = (char)(value % 10 + '0');
			value /= 10;
			if (value == 0)
				break;
		}
		return ((numDigits > 0) ? new String(buffer) : new String(buffer, i, buffer.length - i));
	}

	//------------------------------------------------------------------

	public static String uIntToHexString(int value)
	{
		return uIntToHexString(value, 0, '\0');
	}

	//------------------------------------------------------------------

	public static String uIntToHexString(int  value,
										 int  numDigits,
										 char padChar)
	{
		char[] buffer = new char[(numDigits > 0) ? numDigits : Integer.SIZE >> 2];
		if (numDigits > 0)
			Arrays.fill(buffer, padChar);
		int i = buffer.length;
		while (--i >= 0)
		{
			buffer[i] = hexDigits.charAt(value & 0x0F);
			value >>>= 4;
			if (value == 0)
				break;
		}
		return ((numDigits > 0) ? new String(buffer) : new String(buffer, i, buffer.length - i));
	}

	//------------------------------------------------------------------

	public static String uIntToPrefixedHexString(int value)
	{
		return uIntToPrefixedHexString(value, 0, '\0');
	}

	//------------------------------------------------------------------

	public static String uIntToPrefixedHexString(int  value,
												 int  numDigits,
												 char padChar)
	{
		return (Radix.HEXADECIMAL.prefix + uIntToHexString(value, numDigits, padChar));
	}

	//------------------------------------------------------------------

	public static String uLongToHexString(long value)
	{
		return uLongToHexString(value, 0, '\0');
	}

	//------------------------------------------------------------------

	public static String uLongToHexString(long value,
										  int  numDigits,
										  char padChar)
	{
		char[] buffer = new char[(numDigits > 0) ? numDigits : Long.SIZE >> 2];
		if (numDigits > 0)
			Arrays.fill(buffer, padChar);
		int i = buffer.length;
		while (--i >= 0)
		{
			buffer[i] = hexDigits.charAt((int)value & 0x0F);
			value >>>= 4;
			if (value == 0)
				break;
		}
		return ((numDigits > 0) ? new String(buffer) : new String(buffer, i, buffer.length - i));
	}

	//------------------------------------------------------------------

	public static String uLongToPrefixedHexString(long value)
	{
		return uLongToPrefixedHexString(value, 0, '\0');
	}

	//------------------------------------------------------------------

	public static String uLongToPrefixedHexString(long value,
												  int  numDigits,
												  char padChar)
	{
		return (Radix.HEXADECIMAL.prefix + uLongToHexString(value, numDigits, padChar));
	}

	//------------------------------------------------------------------

	/**
	 * Parses a binary string representation of a signed integer and returns the result.
	 *
	 * @param  str  the string that is to be parsed.
	 * @return the signed integer represented by the binary string {@code str}.
	 * @throws NumberFormatException
	 *           if {@code str} is not a valid binary representation of an integer.
	 * @throws ValueOutOfBoundsException
	 *           if the result of parsing {@code str} cannot be represented in 32 bits.
	 */

	public static int parseIntBin(String str)
	{
		BigInteger value = new BigInteger(str, 2);
		if (value.bitLength() > 32)
			throw new ValueOutOfBoundsException();
		return value.intValue();
	}

	//------------------------------------------------------------------

	/**
	 * Parses a decimal string representation of a signed integer and returns the result.
	 *
	 * @param  str  the string that is to be parsed.
	 * @return the signed integer represented by the decimal string {@code str}.
	 * @throws NumberFormatException
	 *           if {@code str} is not a valid decimal representation of an integer.
	 * @throws ValueOutOfBoundsException
	 *           if the result of parsing {@code str} cannot be represented in 32 bits.
	 */

	public static int parseIntDec(String str)
	{
		BigInteger value = new BigInteger(str);
		if (value.bitLength() > 32)
			throw new ValueOutOfBoundsException();
		return value.intValue();
	}

	//------------------------------------------------------------------

	/**
	 * Parses a hexadecimal string representation of a signed integer and returns the result.
	 *
	 * @param  str  the string that is to be parsed.
	 * @return the signed integer represented by the hexadecimal string {@code str}.
	 * @throws NumberFormatException
	 *           if {@code str} is not a valid hexadecimal representation of an integer.
	 * @throws ValueOutOfBoundsException
	 *           if the result of parsing {@code str} cannot be represented in 32 bits.
	 */

	public static int parseIntHex(String str)
	{
		BigInteger value = new BigInteger(str, 16);
		if (value.bitLength() > 32)
			throw new ValueOutOfBoundsException();
		return value.intValue();
	}

	//------------------------------------------------------------------

	/**
	 * Parses a string representation of a signed integer and returns the result.  The radix of the string
	 * is determined from its prefix:
	 * <ul>
	 *   <li>if the string starts with "0b" or "0B", it is parsed as a binary representation,</li>
	 *   <li>else if it starts with "0x" or "0X", it is parsed as a hexadecimal representation,</li>
	 *   <li>else it is parsed as a decimal representation.</li>
	 * </ul>
	 *
	 * @param  str  the string that is to be parsed.
	 * @return the signed integer represented by {@code str}.
	 * @throws NumberFormatException
	 *           if {@code str} does not represent a valid integer in any of the supported radices.
	 * @throws ValueOutOfBoundsException
	 *           if the result of parsing {@code str} cannot be represented in 32 bits.
	 */

	public static int parseInt(String str)
	{
		if (str.length() >= Radix.PREFIX_LENGTH)
		{
			String prefix = str.substring(0, Radix.PREFIX_LENGTH).toLowerCase();
			if (prefix.equals(Radix.BINARY.prefix))
				return parseIntBin(str.substring(Radix.PREFIX_LENGTH));
			if (prefix.equals(Radix.HEXADECIMAL.prefix))
				return parseIntHex(str.substring(Radix.PREFIX_LENGTH));
		}
		return parseIntDec(str);
	}

	//------------------------------------------------------------------

	/**
	 * Parses a binary string representation of an unsigned integer and returns the result.
	 *
	 * @param  str  the string that is to be parsed.
	 * @return the unsigned integer represented by the binary string {@code str}.
	 * @throws NumberFormatException
	 *           if {@code str} is not a valid binary representation of an integer.
	 * @throws ValueOutOfBoundsException
	 *           if the result of parsing {@code str} cannot be represented in 32 bits.
	 */

	public static int parseUIntBin(String str)
	{
		BigInteger value = new BigInteger(str, 2);
		if (value.signum() < 0)
			throw new NumberFormatException();
		if (value.bitLength() > 32)
			throw new ValueOutOfBoundsException();
		return value.intValue();
	}

	//------------------------------------------------------------------

	/**
	 * Parses a decimal string representation of an unsigned integer and returns the result.
	 *
	 * @param  str  the string that is to be parsed.
	 * @return the unsigned integer represented by the decimal string {@code str}.
	 * @throws NumberFormatException
	 *           if {@code str} is not a valid decimal representation of an integer.
	 * @throws ValueOutOfBoundsException
	 *           if the result of parsing {@code str} cannot be represented in 32 bits.
	 */

	public static int parseUIntDec(String str)
	{
		BigInteger value = new BigInteger(str);
		if (value.signum() < 0)
			throw new NumberFormatException();
		if (value.bitLength() > 32)
			throw new ValueOutOfBoundsException();
		return value.intValue();
	}

	//------------------------------------------------------------------

	/**
	 * Parses a hexadecimal string representation of an unsigned integer and returns the result.
	 *
	 * @param  str  the string that is to be parsed.
	 * @return the unsigned integer represented by the hexadecimal string {@code str}.
	 * @throws NumberFormatException
	 *           if {@code str} is not a valid hexadecimal representation of an integer.
	 * @throws ValueOutOfBoundsException
	 *           if the result of parsing {@code str} cannot be represented in 32 bits.
	 */

	public static int parseUIntHex(String str)
	{
		BigInteger value = new BigInteger(str, 16);
		if (value.signum() < 0)
			throw new NumberFormatException();
		if (value.bitLength() > 32)
			throw new ValueOutOfBoundsException();
		return value.intValue();
	}

	//------------------------------------------------------------------

	/**
	 * Parses a string representation of an unsigned integer and returns the result.  The radix of the
	 * string is determined from its prefix:
	 * <ul>
	 *   <li>if the string starts with "0b" or "0B", it is parsed as a binary representation,</li>
	 *   <li>else if it starts with "0x" or "0X", it is parsed as a hexadecimal representation,</li>
	 *   <li>else it is parsed as a decimal representation.</li>
	 * </ul>
	 *
	 * @param  str  the string that is to be parsed.
	 * @return the unsigned integer represented by {@code str}.
	 * @throws NumberFormatException
	 *           if {@code str} does not represent a valid integer in any of the supported radices.
	 * @throws ValueOutOfBoundsException
	 *           if the result of parsing {@code str} cannot be represented in 32 bits.
	 */

	public static int parseUInt(String str)
	{
		if (str.length() >= Radix.PREFIX_LENGTH)
		{
			String prefix = str.substring(0, Radix.PREFIX_LENGTH).toLowerCase();
			if (prefix.equals(Radix.BINARY.prefix))
				return parseUIntBin(str.substring(Radix.PREFIX_LENGTH));
			if (prefix.equals(Radix.HEXADECIMAL.prefix))
				return parseUIntHex(str.substring(Radix.PREFIX_LENGTH));
		}
		return parseUIntDec(str);
	}

	//------------------------------------------------------------------

	/**
	 * Parses a string representation of an unsigned integer and returns the result.  The radix of the
	 * string is determined from its prefix:
	 * <ul>
	 *   <li>if the string starts with "0b" or "0B", it is parsed as a binary representation,</li>
	 *   <li>else if it starts with "0x" or "0X", it is parsed as a hexadecimal representation,</li>
	 *   <li>else it is parsed as a decimal representation.</li>
	 * </ul>
	 * The radix is returned in the first element of an array.
	 *
	 * @param  str          the string that is to be parsed.
	 * @param  radixBuffer  an array whose first element will be set to the radix of the string
	 *                      representation.
	 * @return the unsigned integer represented by {@code str}.
	 * @throws NumberFormatException
	 *           if {@code str} does not represent a valid integer in any of the supported radices.
	 * @throws ValueOutOfBoundsException
	 *           if the result of parsing {@code str} cannot be represented in 32 bits.
	 */

	public static int parseUInt(String  str,
								Radix[] radixBuffer)
	{
		int value = 0;
		Radix radix = Radix.DECIMAL;
		if (str.length() >= Radix.PREFIX_LENGTH)
		{
			String prefix = str.substring(0, Radix.PREFIX_LENGTH).toLowerCase();
			if (prefix.equals(Radix.BINARY.prefix))
			{
				radix = Radix.BINARY;
				value = parseUIntBin(str.substring(Radix.PREFIX_LENGTH));
			}
			else if (prefix.equals(Radix.HEXADECIMAL.prefix))
			{
				radix = Radix.HEXADECIMAL;
				value = parseUIntHex(str.substring(Radix.PREFIX_LENGTH));
			}
		}
		if (radix == Radix.DECIMAL)
			value = parseUIntDec(str);
		if (radixBuffer != null)
			radixBuffer[0] = radix;
		return value;
	}

	//------------------------------------------------------------------

	/**
	 * Parses a binary string representation of an unsigned long integer and returns the result.
	 *
	 * @param  str  the string that is to be parsed.
	 * @return the unsigned long integer represented by the binary string {@code str}.
	 * @throws NumberFormatException
	 *           if {@code str} is not a valid binary representation of a long integer.
	 * @throws ValueOutOfBoundsException
	 *           if the result of parsing {@code str} cannot be represented in 64 bits.
	 */

	public static long parseULongBin(String str)
	{
		BigInteger value = new BigInteger(str, 2);
		if (value.signum() < 0)
			throw new NumberFormatException();
		if (value.bitLength() > 64)
			throw new ValueOutOfBoundsException();
		return value.longValue();
	}

	//------------------------------------------------------------------

	/**
	 * Parses a decimal string representation of an unsigned long integer and returns the result.
	 *
	 * @param  str  the string that is to be parsed.
	 * @return the unsigned long integer represented by the decimal string {@code str}.
	 * @throws NumberFormatException
	 *           if {@code str} is not a valid decimal representation of a long integer.
	 * @throws ValueOutOfBoundsException
	 *           if the result of parsing {@code str} cannot be represented in 64 bits.
	 */

	public static long parseULongDec(String str)
	{
		BigInteger value = new BigInteger(str);
		if (value.signum() < 0)
			throw new NumberFormatException();
		if (value.bitLength() > 64)
			throw new ValueOutOfBoundsException();
		return value.longValue();
	}

	//------------------------------------------------------------------

	/**
	 * Parses a hexadecimal string representation of an unsigned long integer and returns the result.
	 *
	 * @param  str  the string that is to be parsed.
	 * @return the unsigned long integer represented by the hexadecimal string {@code str}.
	 * @throws NumberFormatException
	 *           if {@code str} is not a valid hexdecimal representation of a long integer.
	 * @throws ValueOutOfBoundsException
	 *           if the result of parsing {@code str} cannot be represented in 64 bits.
	 */

	public static long parseULongHex(String str)
	{
		BigInteger value = new BigInteger(str, 16);
		if (value.signum() < 0)
			throw new NumberFormatException();
		if (value.bitLength() > 64)
			throw new ValueOutOfBoundsException();
		return value.longValue();
	}

	//------------------------------------------------------------------

	/**
	 * Parses a string representation of an unsigned long integer and returns the result.  The radix of the
	 * string is determined from its prefix:
	 * <ul>
	 *   <li>if the string starts with "0b" or "0B", it is parsed as a binary representation,</li>
	 *   <li>else if it starts with "0x" or "0X", it is parsed as a hexadecimal representation,</li>
	 *   <li>else it is parsed as a decimal representation.</li>
	 * </ul>
	 *
	 * @param  str  the string that is to be parsed.
	 * @return the unsigned long integer represented by {@code str}.
	 * @throws NumberFormatException
	 *           if {@code str} does not represent a valid long integer in any of the supported radices.
	 * @throws ValueOutOfBoundsException
	 *           if the result of parsing {@code str} cannot be represented in 64 bits.
	 */

	public static long parseULong(String str)
	{
		if (str.length() >= Radix.PREFIX_LENGTH)
		{
			String prefix = str.substring(0, Radix.PREFIX_LENGTH).toLowerCase();
			if (prefix.equals(Radix.BINARY.prefix))
				return parseULongBin(str.substring(Radix.PREFIX_LENGTH));
			if (prefix.equals(Radix.HEXADECIMAL.prefix))
				return parseULongHex(str.substring(Radix.PREFIX_LENGTH));
		}
		return parseULongDec(str);
	}

	//------------------------------------------------------------------

	/**
	 * Parses a string representation of an unsigned long integer and returns the result.  The radix of the
	 * string is determined from its prefix:
	 * <ul>
	 *   <li>if the string starts with "0b" or "0B", it is parsed as a binary representation,</li>
	 *   <li>else if it starts with "0x" or "0X", it is parsed as a hexadecimal representation,</li>
	 *   <li>else it is parsed as a decimal representation.</li>
	 * </ul>
	 * The radix is returned in the first element of an array.
	 *
	 * @param  str          the string that is to be parsed.
	 * @param  radixBuffer  an array whose first element will be set to the radix of the string
	 *                      representation.
	 * @return the unsigned long integer represented by {@code str}.
	 * @throws NumberFormatException
	 *           if {@code str} does not represent a valid long integer in any of the supported radices.
	 * @throws ValueOutOfBoundsException
	 *           if the result of parsing {@code str} cannot be represented in 64 bits.
	 */

	public static long parseULong(String  str,
								  Radix[] radixBuffer)
	{
		long value = 0;
		Radix radix = Radix.DECIMAL;
		if (str.length() >= Radix.PREFIX_LENGTH)
		{
			String prefix = str.substring(0, Radix.PREFIX_LENGTH).toLowerCase();
			if (prefix.equals(Radix.BINARY.prefix))
			{
				radix = Radix.BINARY;
				value = parseULongBin(str.substring(Radix.PREFIX_LENGTH));
			}
			else if (prefix.equals(Radix.HEXADECIMAL.prefix))
			{
				radix = Radix.HEXADECIMAL;
				value = parseULongHex(str.substring(Radix.PREFIX_LENGTH));
			}
		}
		if (radix == Radix.DECIMAL)
			value = parseULongDec(str);
		if (radixBuffer != null)
			radixBuffer[0] = radix;
		return value;
	}

	//------------------------------------------------------------------

	public static String bytesToHexString(byte[] data)
	{
		return bytesToHexString(data, 0, data.length, 0);
	}

	//------------------------------------------------------------------

	public static String bytesToHexString(byte[] data,
										  int    bytesPerLine)
	{
		return bytesToHexString(data, 0, data.length, bytesPerLine);
	}

	//------------------------------------------------------------------

	public static String bytesToHexString(byte[] data,
										  int    offset,
										  int    length)
	{
		return bytesToHexString(data, offset, length, 0);
	}

	//------------------------------------------------------------------

	public static String bytesToHexString(byte[] data,
										  int    offset,
										  int    length,
										  int    bytesPerLine)
	{
		StringBuilder buffer = new StringBuilder();
		int endOffset = offset + length;
		for (int i = offset; i < endOffset; i++)
		{
			if ((bytesPerLine > 0) && (i > 0) && (i % bytesPerLine == 0))
				buffer.append('\n');
			buffer.append(hexDigits.charAt((data[i] >> 4) & 0x0F));
			buffer.append(hexDigits.charAt(data[i] & 0x0F));
		}
		return buffer.toString();
	}

	//------------------------------------------------------------------

	public static String bytesToPrefixedHexString(byte[] data,
												  int    offset,
												  int    length,
												  int    indent,
												  int    numDigits)
	{
		return bytesToPrefixedHexString(data, offset, length, StringUtils.createCharString(' ', indent), numDigits,
										DEFAULT_BYTES_PER_LINE);
	}

	//------------------------------------------------------------------

	public static String bytesToPrefixedHexString(byte[] data,
												  int    offset,
												  int    length,
												  String linePrefix,
												  int    numDigits,
												  int    bytesPerLine)
	{
		StringBuilder buffer = new StringBuilder(length * 4);
		for (int i = 0; i < length; i++)
		{
			if ((bytesPerLine > 0) && (i % bytesPerLine == 0))
			{
				if (i > 0)
					buffer.append(",\n");
				buffer.append(linePrefix);
			}
			else
				buffer.append(", ");
			buffer.append(uIntToPrefixedHexString(data[offset + i], numDigits, '0'));
		}
		return buffer.toString();
	}

	//------------------------------------------------------------------

	public static String intsToPrefixedHexString(int[] data,
												 int   offset,
												 int   length,
												 int   indent,
												 int   numDigits)
	{
		return intsToPrefixedHexString(data, offset, length, StringUtils.createCharString(' ', indent), numDigits,
									   DEFAULT_INTS_PER_LINE);
	}

	//------------------------------------------------------------------

	public static String intsToPrefixedHexString(int[]  data,
												 int    offset,
												 int    length,
												 String linePrefix,
												 int    numDigits,
												 int    intsPerLine)
	{
		StringBuilder buffer = new StringBuilder(length * 4);
		for (int i = 0; i < length; i++)
		{
			if ((intsPerLine > 0) && (i % intsPerLine == 0))
			{
				if (i > 0)
					buffer.append(",\n");
				buffer.append(linePrefix);
			}
			else
				buffer.append(", ");
			buffer.append(uIntToPrefixedHexString(data[offset + i], numDigits, '0'));
		}
		return buffer.toString();
	}

	//------------------------------------------------------------------

	public static String intsToPrefixedHexString(List<Integer> data,
												 int           offset,
												 int           length,
												 int           indent,
												 int           numDigits)
	{
		return intsToPrefixedHexString(CollectionUtils.intListToArray(data.subList(offset, offset + length)), 0, length,
									   indent, numDigits);
	}

	//------------------------------------------------------------------

	public static String intsToPrefixedHexString(List<Integer> data,
												 int           offset,
												 int           length,
												 String        linePrefix,
												 int           numDigits,
												 int           intsPerLine)
	{
		return intsToPrefixedHexString(CollectionUtils.intListToArray(data.subList(offset, offset + length)), 0, length,
									   linePrefix, numDigits, intsPerLine);
	}

	//------------------------------------------------------------------

	/**
	 * @throws NumberFormatException
	 */

	public static byte[] hexStringToBytes(String str)
	{
		str = str.toUpperCase();
		int length = str.length();
		byte[] bytes = new byte[(length + 1) >>> 1];
		int i = 0;
		int j = 0;
		if (length > bytes.length << 1)
		{
			int index = HEX_DIGITS_UPPER.indexOf(str.charAt(i++));
			if (index < 0)
				throw new NumberFormatException();
			bytes[j++] = (byte)index;
		}
		while (i < length)
		{
			int index1 = HEX_DIGITS_UPPER.indexOf(str.charAt(i++));
			if (index1 < 0)
				throw new NumberFormatException();

			int index2 = HEX_DIGITS_UPPER.indexOf(str.charAt(i++));
			if (index2 < 0)
				throw new NumberFormatException();

			bytes[j++] = (byte)((index1 << 4) | index2);
		}
		return bytes;
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Class fields
////////////////////////////////////////////////////////////////////////

	private static	String	hexDigits	= HEX_DIGITS_UPPER;

////////////////////////////////////////////////////////////////////////
//  Static initialiser
////////////////////////////////////////////////////////////////////////

	static
	{
		POWERS_OF_TEN_INT = new int[10];
		int intValue = 1;
		for (int i = 0; i < POWERS_OF_TEN_INT.length; i++)
		{
			POWERS_OF_TEN_INT[i] = intValue;
			intValue *= 10;
		}

		POWERS_OF_TEN_LONG = new long[19];
		long longValue = 1;
		for (int i = 0; i < POWERS_OF_TEN_LONG.length; i++)
		{
			POWERS_OF_TEN_LONG[i] = longValue;
			longValue *= 10;
		}
	}

}

//----------------------------------------------------------------------
