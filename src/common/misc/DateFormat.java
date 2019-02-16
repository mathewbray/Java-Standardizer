/*====================================================================*\

DateFormat.java

Date format class.

\*====================================================================*/


// PACKAGE


package common.misc;

//----------------------------------------------------------------------


// IMPORTS


import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import common.exception.AppException;

//----------------------------------------------------------------------


// DATE FORMAT CLASS


public class DateFormat
{

////////////////////////////////////////////////////////////////////////
//  Constants
////////////////////////////////////////////////////////////////////////

	public static final		String	SEPARATOR	= "##";

	public static final		DateFormat	DEFAULT_FORMAT	= new DateFormat("Default", "%04y-%02m-%02d");

	private static final	int	MIN_FIELD_WIDTH	= 1;
	private static final	int	MAX_FIELD_WIDTH	= 999;

	private static final	char	END_OF_INPUT		= '\0';
	private static final	char	FIELD_PREFIX		= '%';
	private static final	char	PAD_WITH_ZERO_KEY	= '0';
	private static final	char	LEFT_ALIGN_KEY		= '[';
	private static final	char	RIGHT_ALIGN_KEY		= ']';

	private enum Alignment
	{
		NONE,
		LEFT,
		RIGHT
	}

	private enum ParseState
	{
		LITERAL_TEXT,
		FIELD_START,
		FIELD_WIDTH,
		FIELD_ID,
		STOP
	}

////////////////////////////////////////////////////////////////////////
//  Enumerated types
////////////////////////////////////////////////////////////////////////


	// ERROR IDENTIFIERS


	private enum ErrorId
		implements AppException.IId
	{

	////////////////////////////////////////////////////////////////////
	//  Constants
	////////////////////////////////////////////////////////////////////

		MALFORMED_DEFINITION
		("The definition of the date format is malformed."),

		FIELD_WIDTH_EXPECTED
		("A field width was expected."),

		FIELD_WIDTH_OUT_OF_BOUNDS
		("The field width must be between " + MIN_FIELD_WIDTH + " and " + MAX_FIELD_WIDTH + "."),

		FIELD_IDENTIFIER_EXPECTED
		("A field identifier was expected.");

	////////////////////////////////////////////////////////////////////
	//  Constructors
	////////////////////////////////////////////////////////////////////

		private ErrorId(String message)
		{
			this.message = message;
		}

		//--------------------------------------------------------------

	////////////////////////////////////////////////////////////////////
	//  Instance methods : AppException.IId interface
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
//  Member classes : non-inner classes
////////////////////////////////////////////////////////////////////////


	// PARSE EXCEPTION CLASS


	public static class ParseException
		extends RuntimeException
	{

	////////////////////////////////////////////////////////////////////
	//  Constants
	////////////////////////////////////////////////////////////////////

		private static final	String	INDEX_STR	= "Index: ";

	////////////////////////////////////////////////////////////////////
	//  Constructors
	////////////////////////////////////////////////////////////////////

		private ParseException(AppException.IId id)
		{
			this(id, -1);
		}

		//--------------------------------------------------------------

		private ParseException(AppException.IId id,
							   int              index)
		{
			this.id = id;
			this.index = index;
		}

		//--------------------------------------------------------------

	////////////////////////////////////////////////////////////////////
	//  Instance methods : overriding methods
	////////////////////////////////////////////////////////////////////

		@Override
		public String toString()
		{
			return getException().toString();
		}

		//--------------------------------------------------------------

	////////////////////////////////////////////////////////////////////
	//  Instance methods
	////////////////////////////////////////////////////////////////////

		public AppException.IId getId()
		{
			return id;
		}

		//--------------------------------------------------------------

		public int getIndex()
		{
			return index;
		}

		//--------------------------------------------------------------

		public AppException getException()
		{
			return ((index < 0) ? new AppException(id)
								: new AppException(INDEX_STR + index + "\n" + id.getMessage()));
		}

		//--------------------------------------------------------------

	////////////////////////////////////////////////////////////////////
	//  Instance fields
	////////////////////////////////////////////////////////////////////

		private	AppException.IId	id;
		private	int					index;

	}

	//==================================================================


	// FORMAT FIELD CLASS


	private static class Field
	{

	////////////////////////////////////////////////////////////////////
	//  Constants
	////////////////////////////////////////////////////////////////////

		private static final	String	PREFIX_TARGET		= Character.toString(FIELD_PREFIX);
		private static final	String	PREFIX_REPLACEMENT	= PREFIX_TARGET + PREFIX_TARGET;

	////////////////////////////////////////////////////////////////////
	//  Enumerated types
	////////////////////////////////////////////////////////////////////


		// FIELD KIND


		private enum Kind
		{

		////////////////////////////////////////////////////////////////
		//  Constants
		////////////////////////////////////////////////////////////////

			TEXT        (END_OF_INPUT),
			YEAR        ('y'),
			MONTH       ('m'),
			DAY         ('d'),
			MONTH_NAME  ('M'),
			DAY_NAME    ('D');

		////////////////////////////////////////////////////////////////
		//  Constructors
		////////////////////////////////////////////////////////////////

			private Kind(char key)
			{
				this.key = key;
			}

			//----------------------------------------------------------

		////////////////////////////////////////////////////////////////
		//  Class methods
		////////////////////////////////////////////////////////////////

			private static Kind get(char key)
			{
				for (Kind value : values())
				{
					if (value.key == key)
						return value;
				}
				return null;
			}

			//----------------------------------------------------------

		////////////////////////////////////////////////////////////////
		//  Instance fields
		////////////////////////////////////////////////////////////////

			private	char	key;

		}

		//==============================================================

	////////////////////////////////////////////////////////////////////
	//  Constructors
	////////////////////////////////////////////////////////////////////

		private Field(String text)
		{
			kind = Kind.TEXT;
			this.text = text;
			alignment = Alignment.NONE;
		}

		//--------------------------------------------------------------

		private Field(Kind      kind,
					  Alignment alignment,
					  int       width,
					  boolean   padWithZero)
		{
			this.kind = kind;
			this.alignment = alignment;
			this.width = width;
			this.padWithZero = padWithZero;
		}

		//--------------------------------------------------------------

	////////////////////////////////////////////////////////////////////
	//  Instance methods : overriding methods
	////////////////////////////////////////////////////////////////////

		@Override
		public String toString()
		{
			if (kind == Kind.TEXT)
				return text.replace(PREFIX_TARGET, PREFIX_REPLACEMENT);
			StringBuilder buffer = new StringBuilder(32);
			buffer.append(FIELD_PREFIX);
			if (padWithZero)
				buffer.append(PAD_WITH_ZERO_KEY);
			else
			{
				switch (alignment)
				{
					case NONE:
						// do nothing
						break;

					case LEFT:
						buffer.append(LEFT_ALIGN_KEY);
						break;

					case RIGHT:
						buffer.append(RIGHT_ALIGN_KEY);
						break;
				}
			}
			if (width > 0)
				buffer.append(width);
			buffer.append(kind.key);
			return buffer.toString();
		}

		//--------------------------------------------------------------

	////////////////////////////////////////////////////////////////////
	//  Instance methods
	////////////////////////////////////////////////////////////////////

		private String toString(Calendar date)
		{
			String str = null;
			char padChar = padWithZero ? '0' : ' ';
			switch (kind)
			{
				case TEXT:
					str = text;
					break;

				case YEAR:
					str = NumberUtils.uIntToDecString(date.get(Calendar.YEAR), width, padChar);
					break;

				case MONTH:
					str = NumberUtils.uIntToDecString(date.get(Calendar.MONTH) + 1, width, padChar);
					break;

				case DAY:
					str = NumberUtils.uIntToDecString(date.get(Calendar.DAY_OF_MONTH), width,
													  padChar);
					break;

				case MONTH_NAME:
					str = DateUtils.getMonthNames().get(date.get(Calendar.MONTH));
					break;

				case DAY_NAME:
					str = DateUtils.getDayNames().
												get(date.get(Calendar.DAY_OF_WEEK) - Calendar.SUNDAY);
					break;
			}

			if (width > 0)
			{
				if (str.length() < width)
				{
					switch (alignment)
					{
						case NONE:
						case LEFT:
							str = StringUtils.padAfter(str, width);
							break;

						case RIGHT:
							str = StringUtils.padBefore(str, width);
							break;
					}
				}
				else if (str.length() > width)
					str = str.substring(0, width);
			}

			return str;
		}

		//--------------------------------------------------------------

	////////////////////////////////////////////////////////////////////
	//  Instance fields
	////////////////////////////////////////////////////////////////////

		private	Kind		kind;
		private	String		text;
		private	Alignment	alignment;
		private	int			width;
		private	boolean		padWithZero;

	}

	//==================================================================

////////////////////////////////////////////////////////////////////////
//  Constructors
////////////////////////////////////////////////////////////////////////

	/**
	 * Creates an empty date format.
	 *
	 * @throws ParseException
	 *           if there was an error when parsing {@code pattern}.
	 */

	public DateFormat()
	{
		name = "";
		fields = new ArrayList<>();
	}

	//------------------------------------------------------------------

	/**
	 * Creates a date format from a definition string (a combination of a name and a pattern).
	 *
	 * @param  str  a definition string consisting of a name and a pattern separated by ";".
	 * @throws ParseException
	 *           if {@code str} was malformed or there was an error when parsing the pattern.
	 */

	public DateFormat(String str)
	{
		String[] strs = str.split(SEPARATOR, -1);
		if (strs.length != 2)
			throw new ParseException(ErrorId.MALFORMED_DEFINITION);
		name = strs[0];
		fields = new ArrayList<>();
		parse(strs[1]);
	}

	//------------------------------------------------------------------

	/**
	 * Creates a date format with a given name from a pattern.
	 *
	 * @param  name     the name of the date format.
	 * @param  pattern  the pattern that defines the format.
	 * @throws ParseException
	 *           if there was an error when parsing {@code pattern}.
	 */

	public DateFormat(String name,
					  String pattern)
	{
		this.name = name;
		fields = new ArrayList<>();
		parse(pattern);
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Instance methods : overriding methods
////////////////////////////////////////////////////////////////////////

	@Override
	public String toString()
	{
		return (name + SEPARATOR + getPattern());
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Instance methods
////////////////////////////////////////////////////////////////////////

	public String getName()
	{
		return name;
	}

	//------------------------------------------------------------------

	public String getPattern()
	{
		StringBuilder buffer = new StringBuilder(128);
		for (Field field : fields)
			buffer.append(field);
		return buffer.toString();
	}

	//------------------------------------------------------------------

	public String format(Calendar date)
	{
		StringBuilder buffer = new StringBuilder(128);
		for (Field field : fields)
			buffer.append(field.toString(date));
		return buffer.toString();
	}

	//------------------------------------------------------------------

	/**
	 * Parses a date pattern and sets this date object to the result.
	 *
	 * @param  pattern  the pattern to be parsed.
	 * @throws ParseException
	 *           if there was an error when parsing {@code pattern}.
	 */

	public void parse(String pattern)
	{
		fields.clear();
		Alignment alignment = Alignment.NONE;
		int width = 0;
		boolean padWithZero = false;
		StringBuilder buffer = new StringBuilder(256);
		int index = 0;
		ParseState state = ParseState.LITERAL_TEXT;
		while (state != ParseState.STOP)
		{
			char ch = (index < pattern.length()) ? pattern.charAt(index) : END_OF_INPUT;
			switch (state)
			{
				case LITERAL_TEXT:
					if (ch == END_OF_INPUT)
					{
						if (buffer.length() > 0)
						{
							fields.add(new Field(buffer.toString()));
							buffer.setLength(0);
						}
						state = ParseState.STOP;
					}
					else
					{
						if (ch == FIELD_PREFIX)
							state = ParseState.FIELD_START;
						else
							buffer.append(ch);
						++index;
					}
					break;

				case FIELD_START:
					if (ch == FIELD_PREFIX)
					{
						buffer.append(ch);
						++index;
						state = ParseState.LITERAL_TEXT;
					}
					else
					{
						if (buffer.length() > 0)
						{
							fields.add(new Field(buffer.toString()));
							buffer.setLength(0);
						}
						switch (ch)
						{
							case PAD_WITH_ZERO_KEY:
								padWithZero = true;
								++index;
								break;

							case LEFT_ALIGN_KEY:
								alignment = Alignment.LEFT;
								++index;
								break;

							case RIGHT_ALIGN_KEY:
								alignment = Alignment.RIGHT;
								++index;
								break;
						}
						state = ParseState.FIELD_WIDTH;
					}
					break;

				case FIELD_WIDTH:
					if ((ch >= '0') && (ch <= '9'))
					{
						buffer.append(ch);
						++index;
					}
					else
					{
						if (buffer.length() > 0)
						{
							try
							{
								width = Integer.parseInt(buffer.toString());
								if ((width < MIN_FIELD_WIDTH) || (width > MAX_FIELD_WIDTH))
									throw new NumberFormatException();
							}
							catch (NumberFormatException e)
							{
								throw new ParseException(ErrorId.FIELD_WIDTH_OUT_OF_BOUNDS,
														 index - buffer.length());
							}
							buffer.setLength(0);
						}
						else
						{
							if (padWithZero || (alignment != Alignment.NONE))
								throw new ParseException(ErrorId.FIELD_WIDTH_EXPECTED, index);
						}
						state = ParseState.FIELD_ID;
					}
					break;

				case FIELD_ID:
				{
					Field.Kind fieldKind = (ch == END_OF_INPUT) ? null : Field.Kind.get(ch);
					if (fieldKind == null)
						throw new ParseException(ErrorId.FIELD_IDENTIFIER_EXPECTED, index);
					fields.add(new Field(fieldKind, alignment, width, padWithZero));
					alignment = Alignment.NONE;
					width = 0;
					padWithZero = false;
					if (ch == END_OF_INPUT)
						state = ParseState.STOP;
					else
					{
						++index;
						state = ParseState.LITERAL_TEXT;
					}
					break;
				}

				case STOP:
					// do nothing
					break;
			}
		}
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Instance fields
////////////////////////////////////////////////////////////////////////

	private	String		name;
	private	List<Field>	fields;

}

//----------------------------------------------------------------------
