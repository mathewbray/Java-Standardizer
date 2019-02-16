/*====================================================================*\

StringUtils.java

String utility methods class.

\*====================================================================*/


// PACKAGE


package common.misc;

//----------------------------------------------------------------------


// IMPORTS


import java.io.UnsupportedEncodingException;

import java.nio.charset.UnsupportedCharsetException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import java.util.stream.Collectors;

//----------------------------------------------------------------------


// STRING UTILITY METHODS CLASS


public class StringUtils
{

////////////////////////////////////////////////////////////////////////
//  Constants
////////////////////////////////////////////////////////////////////////

	public static final		char	ESCAPE_PREFIX_CHAR	= '\\';

	public static final		String	ENCODING_NAME_UTF8	= "UTF-8";

	public enum SplitMode
	{
		NONE,
		PREFIX,
		SUFFIX
	}

////////////////////////////////////////////////////////////////////////
//  Constructors
////////////////////////////////////////////////////////////////////////

	/**
	 * Prevents this class from being instantiated externally.
	 */

	private StringUtils()
	{
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Class methods
////////////////////////////////////////////////////////////////////////

	public static boolean isNullOrEmpty(String str)
	{
		return ((str == null) || str.isEmpty());
	}

	//------------------------------------------------------------------

	public static boolean equal(String str1,
								String str2)
	{
		return ((str1 == null) ? (str2 == null) : str1.equals(str2));
	}

	//------------------------------------------------------------------

	public static int getIndex(String[] strs,
							   String   str)
	{
		for (int i = 0; i < strs.length; i++)
		{
			if (strs[i].equals(str))
				return i;
		}
		return -1;
	}

	//------------------------------------------------------------------

	public static char[] createCharArray(char ch,
										 int  length)
	{
		char[] array = new char[length];
		Arrays.fill(array, ch);
		return array;
	}

	//------------------------------------------------------------------

	public static String createCharString(char ch,
										  int  length)
	{
		return new String(createCharArray(ch, length));
	}

	//------------------------------------------------------------------

	public static String firstCharToLowerCase(String str)
	{
		return (str.substring(0, 1).toLowerCase() + str.substring(1));
	}

	//------------------------------------------------------------------

	public static String firstCharsToLowerCase(String str)
	{
		StringBuilder buffer = new StringBuilder(str);
		boolean start = true;
		for (int i = 0; i < buffer.length(); i++)
		{
			char ch = buffer.charAt(i);
			if (start)
				buffer.setCharAt(i, Character.toLowerCase(ch));
			start = (ch == ' ') || (ch == '-');
		}
		return buffer.toString();
	}

	//------------------------------------------------------------------

	public static String firstCharToUpperCase(String str)
	{
		return (str.substring(0, 1).toUpperCase() + str.substring(1));
	}

	//------------------------------------------------------------------

	public static String firstCharsToUpperCase(String str)
	{
		StringBuilder buffer = new StringBuilder(str);
		boolean start = true;
		for (int i = 0; i < buffer.length(); i++)
		{
			char ch = buffer.charAt(i);
			if (start)
				buffer.setCharAt(i, Character.toUpperCase(ch));
			start = (ch == ' ') || (ch == '-');
		}
		return buffer.toString();
	}

	//------------------------------------------------------------------

	public static int getMaximumLength(Iterable<? extends CharSequence> seqs)
	{
		int maxLength = 0;
		for (CharSequence seq : seqs)
		{
			int length = seq.length();
			if (maxLength < length)
				maxLength = length;
		}
		return maxLength;
	}

	//------------------------------------------------------------------

	public static int getMaximumLength(String... strs)
	{
		return getMaximumLength(Arrays.asList(strs));
	}

	//------------------------------------------------------------------

	public static String stripBefore(CharSequence seq)
	{
		for (int i = 0; i < seq.length(); i++)
		{
			char ch = seq.charAt(i);
			if ((ch != '\t') && (ch != ' '))
				return seq.subSequence(i, seq.length()).toString();
		}
		return "";
	}

	//------------------------------------------------------------------

	public static String stripAfter(CharSequence seq)
	{
		for (int i = seq.length() - 1; i >= 0; i--)
		{
			char ch = seq.charAt(i);
			if ((ch != '\t') && (ch != ' '))
				return seq.subSequence(0, i + 1).toString();
		}
		return "";
	}

	//------------------------------------------------------------------

	public static String padBefore(CharSequence seq,
								   int          length)
	{
		return padBefore(seq, length, ' ');
	}

	//------------------------------------------------------------------

	public static String padBefore(CharSequence seq,
								   int          length,
								   char         ch)
	{
		int padLength = length - seq.length();
		return ((padLength > 0) ? createCharString(ch, padLength) + seq.toString() : seq.toString());
	}

	//------------------------------------------------------------------

	public static String padAfter(CharSequence seq,
								  int          length)
	{
		return padAfter(seq, length, ' ');
	}

	//------------------------------------------------------------------

	public static String padAfter(CharSequence seq,
								  int          length,
								  char         ch)
	{
		int padLength = length - seq.length();
		return ((padLength > 0) ? seq.toString() + createCharString(ch, padLength) : seq.toString());
	}

	//------------------------------------------------------------------

	public static String[] splitAt(String    str,
								   int       index,
								   SplitMode splitMode)
	{
		return ((index < 0)
						? new String[]{ str, (splitMode == SplitMode.NONE) ? null : "" }
						: new String[]{ str.substring(0, (splitMode == SplitMode.PREFIX) ? index + 1 : index),
										str.substring((splitMode == SplitMode.SUFFIX) ? index : index + 1) });
	}

	//------------------------------------------------------------------

	public static String[] splitAtFirst(String str,
										char   ch)
	{
		return splitAt(str, str.indexOf(ch), SplitMode.NONE);
	}

	//------------------------------------------------------------------

	public static String[] splitAtFirst(String    str,
										char      ch,
										SplitMode splitMode)
	{
		return splitAt(str, str.indexOf(ch), splitMode);
	}

	//------------------------------------------------------------------

	public static String[] splitAtLast(String str,
									   char   ch)
	{
		return splitAt(str, str.lastIndexOf(ch), SplitMode.NONE);
	}

	//------------------------------------------------------------------

	public static String[] splitAtLast(String    str,
									   char      ch,
									   SplitMode splitMode)
	{
		return splitAt(str, str.lastIndexOf(ch), splitMode);
	}

	//------------------------------------------------------------------

	public static String removeToFirst(String str,
									   char   ch)
	{
		int index = str.indexOf(ch);
		return ((index < 0) ? str : str.substring(index));
	}

	//------------------------------------------------------------------

	public static String removeFromFirst(String str,
										 char   ch)
	{
		int index = str.indexOf(ch);
		return ((index < 0) ? str : str.substring(0, index));
	}

	//------------------------------------------------------------------

	public static String removeToLast(String str,
									  char   ch)
	{
		int index = str.lastIndexOf(ch);
		return ((index < 0) ? str : str.substring(index));
	}

	//------------------------------------------------------------------

	public static String removeFromLast(String str,
										char   ch)
	{
		int index = str.lastIndexOf(ch);
		return ((index < 0) ? str : str.substring(0, index));
	}

	//------------------------------------------------------------------

	public static String removePrefix(String str,
									  String prefix)
	{
		return ((str.isEmpty() || prefix.isEmpty() || !str.startsWith(prefix))
																	? str
																	: str.substring(prefix.length()));
	}

	//------------------------------------------------------------------

	public static String removeSuffix(String str,
									  String suffix)
	{
		return ((str.isEmpty() || suffix.isEmpty() || !str.endsWith(suffix))
												? str
												: str.substring(0, str.length() - suffix.length()));
	}

	//------------------------------------------------------------------

	public static String join(char      separator,
							  String... strs)
	{
		return join(Character.toString(separator), false, Arrays.asList(strs));
	}

	//------------------------------------------------------------------

	public static String join(char                         separator,
							  List<? extends CharSequence> seqs)
	{
		return join(Character.toString(separator), false, seqs);
	}

	//------------------------------------------------------------------

	public static String join(CharSequence separator,
							  String...    strs)
	{
		return join(separator, false, Arrays.asList(strs));
	}

	//------------------------------------------------------------------

	public static String join(CharSequence                 separator,
							  List<? extends CharSequence> seqs)
	{
		return join(separator, false, seqs);
	}

	//------------------------------------------------------------------

	public static String join(char      separator,
							  boolean   trailingSeparator,
							  String... strs)
	{
		return join(Character.toString(separator), trailingSeparator, Arrays.asList(strs));
	}

	//------------------------------------------------------------------

	public static String join(char                         separator,
							  boolean                      trailingSeparator,
							  List<? extends CharSequence> seqs)
	{
		return join(Character.toString(separator), trailingSeparator, seqs);
	}

	//------------------------------------------------------------------

	public static String join(CharSequence separator,
							  boolean      trailingSeparator,
							  String...    strs)
	{
		return join(separator, trailingSeparator, Arrays.asList(strs));
	}

	//------------------------------------------------------------------

	public static String join(CharSequence                 separator,
							  boolean                      trailingSeparator,
							  List<? extends CharSequence> seqs)
	{
		// Calculate length of buffer
		int length = (separator == null) ? 0 : separator.length() * seqs.size();
		for (CharSequence seq : seqs)
			length += seq.length();

		// Concatenate character sequences
		StringBuilder buffer = new StringBuilder(length);
		for (int i = 0; i < seqs.size(); i++)
		{
			if ((separator != null) && (i > 0))
				buffer.append(separator);
			buffer.append(seqs.get(i));
		}
		if ((separator != null) && trailingSeparator)
			buffer.append(separator);
		return buffer.toString();
	}

	//------------------------------------------------------------------

	public static String wrap(CharSequence seq,
							  int          maxLineLength)
	{
		return wrapLines(seq, maxLineLength).stream().collect(Collectors.joining("\n"));
	}

	//------------------------------------------------------------------

	public static List<String> wrapLines(CharSequence seq,
										 int          maxLineLength)
	{
		// Initialise list of lines
		List<String> lines = new ArrayList<>();

		// Break input sequence into lines
		int index = 0;
		while (index < seq.length())
		{
			// Initialise loop variables
			boolean wordBreak = false;
			int startIndex = index;
			int endIndex = startIndex + maxLineLength;
			int breakIndex = startIndex;

			// Find next line break
			for (int i = startIndex; (i <= endIndex) || (breakIndex == startIndex); i++)
			{
				// If end of input, mark line break; stop
				if (i == seq.length())
				{
					if (!wordBreak)
						breakIndex = i;
					break;
				}

				// If character is space ...
				if (seq.charAt(i) == ' ')
				{
					// If not already in a word break, mark start of break
					if (!wordBreak)
					{
						wordBreak = true;
						breakIndex = i;
					}
				}

				// ... otherwise, clear "word break" flag
				else
					wordBreak = false;
			}

			// Add line to list
			if (breakIndex - startIndex > 0)
				lines.add(seq.subSequence(startIndex, breakIndex).toString());

			// Advance to next non-space after line break
			for (index = breakIndex; index < seq.length(); index++)
			{
				if (seq.charAt(index) != ' ')
					break;
			}
		}

		// Return list of lines
		return lines;
	}

	//------------------------------------------------------------------

	public static String applyPrefix(String str,
									 String prefix)
	{
		if (isNullOrEmpty(prefix))
			return str;
		return (prefix + (prefix.endsWith("_") ? str : firstCharToUpperCase(str)));
	}

	//------------------------------------------------------------------

	/**
	 * Converts the specified string to camel case and returns the result.  The conversion, which is
	 * intended to be applied to underscore-separated identifiers, is performed as follows:
	 * <ol>
	 * <li>Each underscore character (U+005F) is removed.</li>
	 * <li>Each non-underscore character that immediately follows an underscore character or a decimal digit
	 *     (U+0030 .. U+0039) is converted to upper case.</li>
	 * <li>All other characters are converted to lower case.</li>
	 * </ol>
	 * @param  str  the string that will be converted.
	 * @return the camel-case string that results from the conversion.
	 */
	public static String identifierToCamelCase(String str)
	{
		StringBuilder buffer = new StringBuilder(str.length());
		boolean toUpper = false;
		for (int i = 0; i < str.length(); i++)
		{
			char ch = str.charAt(i);
			if (ch == '_')
				toUpper = true;
			else if ((ch >= '0') && (ch <= '9'))
			{
				buffer.append(ch);
				toUpper = true;
			}
			else
			{
				buffer.append(toUpper ? Character.toUpperCase(ch) : Character.toLowerCase(ch));
				toUpper = false;
			}
		}
		return buffer.toString();
	}

	//------------------------------------------------------------------

	/**
	 * Decodes the specified UTF-8 sequence to a string.  An {@code IllegalArgumentException} is thrown if
	 * the UTF-8 sequence is malformed or contains bytes that cannot be mapped to a character.  This method
	 * is equivalent to {@code #utf8ToString(data, 0, data.length)}.
	 *
	 * @param  data  an array that contains the UTF-8 sequence that will be decoded.
	 * @return the string that results from decoding the input sequence.
	 * @throws IllegalArgumentException
	 *           if
	 *           <ul>
	 *             <li>the UTF-8 sequence is malformed, or</li>
	 *             <li>the UTF-8 sequence contains bytes that cannot be mapped to a character.</li>
	 *           </ul>
	 * @throws UnsupportedCharsetException
	 *           if the Java implementation does not support the UTF-8 character encoding, which is required
	 *           of all Java implementations.
	 */

	public static String utf8ToString(byte[] data)
	{
		return utf8ToString(data, 0, data.length);
	}

	//------------------------------------------------------------------

	/**
	 * Decodes the specified UTF-8 sequence to a string.  An {@code IllegalArgumentException} is thrown if
	 * the UTF-8 sequence is malformed or if it contains bytes that cannot be mapped to a character.
	 *
	 * @param  data    an array that contains the UTF-8 sequence that will be decoded.
	 * @param  offset  the offset to {@code data} at which the input sequence begins.
	 * @param  length  the length of the input sequence.
	 * @return the string that results from decoding the input sequence.
	 * @throws UnsupportedCharsetException
	 *           if the Java implementation does not support the UTF-8 character encoding, which is required
	 *           of all Java implementations.
	 */

	public static String utf8ToString(byte[] data,
									  int    offset,
									  int    length)
	{
		try
		{
			return new String(data, offset, length, ENCODING_NAME_UTF8);
		}
		catch (UnsupportedEncodingException e)
		{
			throw new UnsupportedCharsetException(ENCODING_NAME_UTF8);
		}
	}

	//------------------------------------------------------------------

	/**
	 * Encodes the specified string as a UTF-8 sequence.
	 *
	 * @param  str  the string that will be encoded.
	 * @return an array containing the UTF-8 sequence that results from encoding the input string.
	 * @throws UnsupportedCharsetException
	 *           if the Java implementation does not support the UTF-8 character encoding, which is required
	 *           of all Java implementations.
	 */

	public static byte[] stringToUtf8(String str)
	{
		try
		{
			return str.getBytes(ENCODING_NAME_UTF8);
		}
		catch (UnsupportedEncodingException e)
		{
			throw new UnsupportedCharsetException(ENCODING_NAME_UTF8);
		}
	}

	//------------------------------------------------------------------

	public static String escape(CharSequence seq,
								String       metachars)
	{
		StringBuilder buffer = new StringBuilder(2 * seq.length());
		for (int i = 0; i < seq.length(); i++)
		{
			char ch = seq.charAt(i);
			if (metachars.indexOf(ch) >= 0)
				buffer.append(ESCAPE_PREFIX_CHAR);
			buffer.append(ch);
		}
		return buffer.toString();
	}

	//------------------------------------------------------------------

	public static String substituteEnvironmentVariables(String str,
														String prefix,
														String suffix)
	{
		StringBuilder buffer = new StringBuilder(str);
		int prefixLength = prefix.length();
		int index = 0;
		while (index < buffer.length())
		{
			index = buffer.indexOf(prefix, index);
			if (index < 0)
				break;
			index += prefixLength;
			int startIndex = index;
			index = buffer.indexOf(suffix, index);
			if (index < 0)
				index = startIndex;
			else
			{
				String value = null;
				if (index > startIndex)
				{
					try
					{
						value = System.getenv(buffer.substring(startIndex, index));
					}
					catch (SecurityException e)
					{
						// ignore
					}
				}
				startIndex -= prefixLength;
				if (value == null)
					value = "";
				buffer.replace(startIndex, index + suffix.length(), value);
				index = startIndex + value.length();
			}
		}
		return buffer.toString();
	}

	//------------------------------------------------------------------

}

//----------------------------------------------------------------------
