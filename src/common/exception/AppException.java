/*====================================================================*\

AppException.java

Application exception class.

\*====================================================================*/


// PACKAGE


package common.exception;

//----------------------------------------------------------------------


// IMPORTS


import common.indexedsub.IndexedSub;

//----------------------------------------------------------------------


// APPLICATION EXCEPTION CLASS


public class AppException
	extends Exception
{

////////////////////////////////////////////////////////////////////////
//  Constants
////////////////////////////////////////////////////////////////////////

	private static final	int	DEFAULT_MAX_CAUSE_MESSAGE_LINE_LENGTH	= 160;

	private static final	String	NO_ERROR_STR	= "No error";

////////////////////////////////////////////////////////////////////////
//  Member interfaces
////////////////////////////////////////////////////////////////////////


	// EXCEPTION IDENTIFIER INTERFACE


	@FunctionalInterface
	public interface IId
	{

	////////////////////////////////////////////////////////////////////
	//  Methods
	////////////////////////////////////////////////////////////////////

		String getMessage();

		//--------------------------------------------------------------

	}

	//==================================================================

////////////////////////////////////////////////////////////////////////
//  Member classes : non-inner classes
////////////////////////////////////////////////////////////////////////


	// ANONYMOUS IDENTIFIER CLASS


	protected static class AnonymousId
		implements IId
	{

	////////////////////////////////////////////////////////////////////
	//  Constructors
	////////////////////////////////////////////////////////////////////

		protected AnonymousId(String message)
		{
			this.message = message;
		}

		//--------------------------------------------------------------

	////////////////////////////////////////////////////////////////////
	//  Instance methods : Id interface
	////////////////////////////////////////////////////////////////////

		public String getMessage()
		{
			return message;
		}

		//--------------------------------------------------------------

	////////////////////////////////////////////////////////////////////
	//  Instance fields
	////////////////////////////////////////////////////////////////////

		private	String	message;

	}

	//==================================================================

////////////////////////////////////////////////////////////////////////
//  Constructors
////////////////////////////////////////////////////////////////////////

	public AppException()
	{
	}

	//------------------------------------------------------------------

	public AppException(String messageStr)
	{
		this(new AnonymousId(messageStr));
	}

	//------------------------------------------------------------------

	public AppException(String    messageStr,
						String... substitutionStrs)
	{
		this(new AnonymousId(messageStr), substitutionStrs);
	}

	//------------------------------------------------------------------

	public AppException(String    messageStr,
						Throwable cause)
	{
		this(new AnonymousId(messageStr), cause);
	}

	//------------------------------------------------------------------

	public AppException(String    messageStr,
						Throwable cause,
						String... substitutionStrs)
	{
		this(new AnonymousId(messageStr), cause, substitutionStrs);
	}

	//------------------------------------------------------------------

	public AppException(IId id)
	{
		this(id, (Throwable)null);
	}

	//------------------------------------------------------------------

	public AppException(IId       id,
						String... substitutionStrs)
	{
		this(id);
		setSubstitutionStrings(substitutionStrs);
	}

	//------------------------------------------------------------------

	public AppException(IId       id,
						Throwable cause)
	{
		super(getString(id), cause);
		this.id = id;
	}

	//------------------------------------------------------------------

	public AppException(IId       id,
						Throwable cause,
						String... substitutionStrs)
	{
		this(id, cause);
		setSubstitutionStrings(substitutionStrs);
	}

	//------------------------------------------------------------------

	public AppException(AppException exception)
	{
		this(exception, false);
	}

	//------------------------------------------------------------------

	public AppException(AppException exception,
						boolean      ignorePrefixAndSuffix)
	{
		this(exception.id, exception.getCause(), exception.substitutionStrs);
		parentPrefix = exception.parentPrefix;
		parentSuffix = exception.parentSuffix;
		if (!ignorePrefixAndSuffix)
		{
			String prefix = exception.getPrefix();
			if (prefix != null)
				parentPrefix = (parentPrefix == null) ? prefix : prefix + parentPrefix;

			String suffix = exception.getSuffix();
			if (suffix != null)
				parentSuffix = (parentSuffix == null) ? suffix : suffix + parentSuffix;
		}
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Class methods
////////////////////////////////////////////////////////////////////////

	public static int getMaxCauseMessageLineLength()
	{
		return maxCauseMessageLineLength;
	}

	//------------------------------------------------------------------

	public static String getString(IId id)
	{
		return ((id == null) ? NO_ERROR_STR : id.getMessage());
	}

	//------------------------------------------------------------------

	public static void setMaxCauseMessageLineLength(int length)
	{
		maxCauseMessageLineLength = length;
	}

	//------------------------------------------------------------------

	protected static String createString(String    message,
										 String    prefix,
										 String    suffix,
										 String[]  substitutionStrs,
										 Throwable cause)
	{
		// Append the detail message with prefix, suffix and any substitutions
		StringBuilder buffer = new StringBuilder();
		if (message != null)
		{
			if (prefix != null)
				buffer.append(prefix);
			buffer.append((substitutionStrs == null) ? message : IndexedSub.sub(message, substitutionStrs));
			if (suffix != null)
				buffer.append(suffix);
		}

		// Wrap the text of the detail message of the cause and append the text to the detail message
		while (cause != null)
		{
			String str = cause.getMessage();
			if ((str == null) || (cause instanceof AppException))
				str = cause.toString();
			buffer.append("\n- ");
			int index = 0;
			while (index < str.length())
			{
				boolean space = false;
				int breakIndex = index;
				int endIndex = index + Math.max(1, maxCauseMessageLineLength);
				for (int i = index; (i <= endIndex) || (breakIndex == index); i++)
				{
					if (i == str.length())
					{
						if (!space)
							breakIndex = i;
						break;
					}
					if (str.charAt(i) == ' ')
					{
						if (!space)
						{
							space = true;
							breakIndex = i;
						}
					}
					else
						space = false;
				}
				if (breakIndex - index > 0)
					buffer.append(str.substring(index, breakIndex));
				buffer.append("\n  ");
				for (index = breakIndex; index < str.length(); index++)
				{
					if (str.charAt(index) != ' ')
						break;
				}
			}
			index = buffer.length();
			while (--index >= 0)
			{
				if (!Character.isWhitespace(buffer.charAt(index)))
					break;
			}
			buffer.setLength(++index);

			// Descend hierarchy of causes
			cause = cause.getCause();
		}

		return buffer.toString();
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Instance methods : overriding methods
////////////////////////////////////////////////////////////////////////

	@Override
	public String toString()
	{
		// Get the combined prefix
		String prefix = getPrefix();
		if (parentPrefix != null)
			prefix = (prefix == null) ? parentPrefix : prefix + parentPrefix;

		// Get the combined suffix
		String suffix = getSuffix();
		if (parentSuffix != null)
			suffix = (suffix == null) ? parentSuffix : suffix + parentSuffix;

		// Create the string from its components
		return createString(getMessage(), prefix, suffix, substitutionStrs, getCause());
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Instance methods
////////////////////////////////////////////////////////////////////////

	public IId getId()
	{
		return id;
	}

	//------------------------------------------------------------------

	public String getSubstitutionString(int index)
	{
		return substitutionStrs[index];
	}

	//------------------------------------------------------------------

	public String[] getSubstitutionStrings()
	{
		return substitutionStrs;
	}

	//------------------------------------------------------------------

	public void clearSubstitutionStrings()
	{
		substitutionStrs = null;
	}

	//------------------------------------------------------------------

	public void setSubstitutionString(int    index,
									  String str)
	{
		substitutionStrs[index] = str;
	}

	//------------------------------------------------------------------

	public void setSubstitutionStrings(String... strs)
	{
		substitutionStrs = strs;
	}

	//------------------------------------------------------------------

	public void setSubstitutionDecValue(int value)
	{
		setSubstitutionStrings(Integer.toString(value));
	}

	//------------------------------------------------------------------

	protected String getPrefix()
	{
		return null;
	}

	//------------------------------------------------------------------

	protected String getSuffix()
	{
		return null;
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Class fields
////////////////////////////////////////////////////////////////////////

	private static	int	maxCauseMessageLineLength	= DEFAULT_MAX_CAUSE_MESSAGE_LINE_LENGTH;

////////////////////////////////////////////////////////////////////////
//  Instance fields
////////////////////////////////////////////////////////////////////////

	private	IId			id;
	private	String[]	substitutionStrs;
	private	String		parentPrefix;
	private	String		parentSuffix;

}

//----------------------------------------------------------------------
