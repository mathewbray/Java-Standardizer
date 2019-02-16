/*====================================================================*\

IndexedSub.java

Class: indexed-substitution utility methods.

\*====================================================================*/


// PACKAGE


package common.indexedsub;

//----------------------------------------------------------------------


// CLASS: INDEXED-SUBSTITUTION UTILITY METHODS


/**
 * This class provides methods that substitute specified replacement sequences for occurrences of placeholders in a
 * specified string.  A placeholder has the form "%<i>n</i>", where <i>n</i> is a decimal string representation of an
 * integer greater than or equal to 1.
 */

public class IndexedSub
{

////////////////////////////////////////////////////////////////////////
//  Constants
////////////////////////////////////////////////////////////////////////

	/** The prefix of a placeholder in an input string. */
	public static final		char	PLACEHOLDER_PREFIX_CHAR	= '%';

	/** The base of the index of a substitution. */
	private static final	int		SUBSTITUTION_INDEX_BASE	= 1;

////////////////////////////////////////////////////////////////////////
//  Constructors
////////////////////////////////////////////////////////////////////////

	/**
	 * Prevents this class from being instantiated externally.
	 */

	private IndexedSub()
	{
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Class methods
////////////////////////////////////////////////////////////////////////

	/**
	 * Substitutes the specified replacement sequences for occurrences of placeholders in the specified string, and
	 * returns the resulting string.  A placeholder has the form "%<i>n</i>", where <i>n</i> is a decimal string
	 * representation of an integer greater than or equal to 1.  Each placeholder is replaced by the specified sequence
	 * whose zero-based index is <i>n</i>-1; for example, the placeholder "%3" will be replaced by the sequence at
	 * index 2.  A placeholder that does not have a corresponding replacement sequence is replaced by an empty string.
	 * <p>
	 * If a literal "%" is required in the output string, it must be escaped by prefixing another "%" to it (ie, "%%").
	 * An unescaped "%" will be replaced by an empty string.
	 * </p>
	 *
	 * @param  str
	 *           the string on which substitutions will be performed.
	 * @param  replacements
	 *           the sequences that will replace placeholders in {@code str}.
	 * @return a transformation of the input string in which each placeholder is replaced by the element of {@code
	 *         replacements} at the corresponding index.
	 */

	public static String sub(String          str,
							 CharSequence... replacements)
	{
		// If there are no replacement sequences, return the input string
		if (replacements.length == 0)
			return str;

		// Allocate a buffer for the output string
		StringBuilder buffer = new StringBuilder(str.length() + 32);

		// Perform substitutions on the input string
		int index = 0;
		while (index < str.length())
		{
			// Set the start index to the end of the last placeholder
			int startIndex = index;

			// Get the index of the next placeholder prefix
			index = str.indexOf(PLACEHOLDER_PREFIX_CHAR, index);

			// If there are no more placeholder prefixes, set the index to the end of the input string
			if (index < 0)
				index = str.length();

			// Get the substring of the input string from the end of the last placeholder to the current placeholder
			// prefix, and append it to the output buffer
			if (index > startIndex)
				buffer.append(str.substring(startIndex, index));

			// Increment the index past the current placeholder prefix
			++index;

			// If the placeholder prefix is followed by another one, escape it (ie, append a literal prefix to the
			// output buffer) ...
			if ((index < str.length()) && (str.charAt(index) == PLACEHOLDER_PREFIX_CHAR))
			{
				buffer.append(PLACEHOLDER_PREFIX_CHAR);
				++index;
			}

			// ... otherwise, parse the substitution index and perform a substitution
			else
			{
				// Update the start index to past the placeholder prefix
				startIndex = index;

				// Advance the index to past the end of the substitution index
				while (index < str.length())
				{
					char ch = str.charAt(index);
					if ((ch < '0') || (ch > '9'))
						break;
					++index;
				}

				// If there is a substitution index, parse it and append the corresponding replacement sequence to the
				// output buffer
				if (index > startIndex)
				{
					// Parse the substitution index
					int subIndex = Integer.parseInt(str.substring(startIndex, index)) - SUBSTITUTION_INDEX_BASE;

					// If there is a replacement sequence for the substitution index, append it to the output buffer
					if ((subIndex >= 0) && (subIndex < replacements.length))
					{
						CharSequence replacement = replacements[subIndex];
						if (replacement != null)
							buffer.append(replacement);
					}
				}
			}
		}

		// Return output string
		return buffer.toString();
	}

	//------------------------------------------------------------------

	/**
	 * Substitutes the decimal string representation of the specified integer value for each occurrence of the
	 * placeholder "%1" in the specified string.
	 *
	 * @param  str
	 *           the string on which substitutions will be performed.
	 * @param  value
	 *           the value whose decimal string representation will replace occurrences of "%1" in {@code str}.
	 * @return a transformation of the input string in which each occurrence of "%1" is replaced by the decimal string
	 *         representation of {@code value}.
	 */

	public static String sub(String str,
							 int    value)
	{
		return sub(str, Integer.toString(value));
	}

	//------------------------------------------------------------------

}

//----------------------------------------------------------------------
