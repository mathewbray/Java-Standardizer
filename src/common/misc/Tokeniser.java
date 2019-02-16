/*====================================================================*\

Tokeniser.java

Tokeniser class.

\*====================================================================*/


// PACKAGE


package common.misc;

//----------------------------------------------------------------------


// IMPORTS


import java.util.ArrayList;
import java.util.List;

//----------------------------------------------------------------------


// TOKENISER CLASS


public class Tokeniser
{

////////////////////////////////////////////////////////////////////////
//  Constants
////////////////////////////////////////////////////////////////////////

	public static final	char	INVALID_CHAR	= '\uFFFE';

	private enum FieldState
	{
		START_OF_FIELD,
		FIELD,
		QUOTATION,
		QUOTATION_PENDING,
		END_OF_FIELD,
		STOP
	}

	private enum TokenState
	{
		START_OF_TOKEN,
		TOKEN,
		QUOTATION,
		QUOTATION_PENDING,
		END_OF_QUOTATION,
		END_OF_TOKEN,
		STOP
	}

////////////////////////////////////////////////////////////////////////
//  Member classes : non-inner classes
////////////////////////////////////////////////////////////////////////


	// TOKEN CLASS


	public static class Token
	{

	////////////////////////////////////////////////////////////////////
	//  Constructors
	////////////////////////////////////////////////////////////////////

		public Token()
		{
			text = "";
		}

		//--------------------------------------------------------------

		public Token(String text)
		{
			this.text = text;
		}

		//--------------------------------------------------------------

		public Token(String  text,
					 boolean quoted)
		{
			this.text = text;
			this.quoted = quoted;
		}

		//--------------------------------------------------------------

	////////////////////////////////////////////////////////////////////
	//  Instance methods : overriding methods
	////////////////////////////////////////////////////////////////////

		@Override
		public String toString()
		{
			return getRawText();
		}

		//--------------------------------------------------------------

	////////////////////////////////////////////////////////////////////
	//  Instance methods
	////////////////////////////////////////////////////////////////////

		public String getRawText()
		{
			return (quoted ? "\"" + text.replace("\"", "\"\"") + "\"" : text);
		}

		//--------------------------------------------------------------

		public boolean matches(String str)
		{
			return text.equals(str);
		}

		//--------------------------------------------------------------

		public boolean matchesRaw(String str)
		{
			return (getRawText().equals(str));
		}

		//--------------------------------------------------------------

	////////////////////////////////////////////////////////////////////
	//  Instance fields
	////////////////////////////////////////////////////////////////////

		public	String	text;
		public	boolean	quoted;

	}

	//==================================================================


	// UNCLOSED QUOTATION EXCEPTION CLASS


	public static class UnclosedQuotationException
		extends RuntimeException
	{

	////////////////////////////////////////////////////////////////////
	//  Constructors
	////////////////////////////////////////////////////////////////////

		private UnclosedQuotationException(int index)
		{
			super(Integer.toString(index));
		}

		//--------------------------------------------------------------

	}

	//==================================================================

////////////////////////////////////////////////////////////////////////
//  Constructors
////////////////////////////////////////////////////////////////////////

	public Tokeniser(CharSequence sequence,
					 char         fieldSeparatorChar)
	{
		this(sequence, fieldSeparatorChar, INVALID_CHAR);
	}

	//------------------------------------------------------------------

	public Tokeniser(CharSequence sequence,
					 char         fieldSeparatorChar,
					 char         inputEndChar)
	{
		this.sequence = sequence;
		this.fieldSeparatorChar = fieldSeparatorChar;
		this.inputEndChar = inputEndChar;
		sequenceEndIndex = -1;
	}

	//------------------------------------------------------------------

	public Tokeniser(CharSequence sequence,
					 String       tokenSeparatorChars)
	{
		this(sequence, tokenSeparatorChars, null);
	}

	//------------------------------------------------------------------

	public Tokeniser(CharSequence sequence,
					 String       tokenSeparatorChars,
					 String       inputEndChars)
	{
		if (tokenSeparatorChars == null)
			throw new IllegalArgumentException();

		this.sequence = sequence;
		this.tokenSeparatorChars = tokenSeparatorChars;
		this.inputEndChars = (inputEndChars == null) ? "" : inputEndChars;
		sequenceEndIndex = -1;
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Instance methods
////////////////////////////////////////////////////////////////////////

	/**
	 * Extracts the next token from the input sequence and returns the result.
	 *
	 * @param  quotingEnabled  if {@code true}, a token may be enclosed in double quotation marks.
	 * @return the next token from the input sequence.
	 * @throws UnclosedQuotationException
	 *           if the next token contains an unclosed quotation.
	 */

	public Token next(boolean quotingEnabled)
	{
		prevSequenceIndex = sequenceIndex;
		return ((tokenSeparatorChars == null) ? nextField(quotingEnabled)
											  : nextToken(quotingEnabled));
	}

	//------------------------------------------------------------------

	public Token next()
	{
		return next(false);
	}

	//------------------------------------------------------------------

	public void reset()
	{
		sequenceIndex = 0;
		prevSequenceIndex = 0;
		sequenceEndIndex = -1;
	}

	//------------------------------------------------------------------

	public void putBack()
	{
		sequenceIndex = prevSequenceIndex;
	}

	//------------------------------------------------------------------

	public List<Token> getTokens()
	{
		return getTokens(false);
	}

	//------------------------------------------------------------------

	public List<Token> getTokens(boolean quotingEnabled)
	{
		reset();
		return getRemainingTokens(quotingEnabled);
	}

	//------------------------------------------------------------------

	public List<Token> getRemainingTokens()
	{
		return getRemainingTokens(false);
	}

	//------------------------------------------------------------------

	public List<Token> getRemainingTokens(boolean quotingEnabled)
	{
		List<Token> tokens = new ArrayList<>();
		while (true)
		{
			Token token = next(quotingEnabled);
			if (token == null)
				break;
			tokens.add(token);
		}
		return tokens;
	}

	//------------------------------------------------------------------

	public List<String> getTokenStrings()
	{
		return getTokenStrings(false);
	}

	//------------------------------------------------------------------

	public List<String> getTokenStrings(boolean quotingEnabled)
	{
		reset();
		return getRemainingTokenStrings(quotingEnabled);
	}

	//------------------------------------------------------------------

	public List<String> getRemainingTokenStrings()
	{
		return getRemainingTokenStrings(false);
	}

	//------------------------------------------------------------------

	public List<String> getRemainingTokenStrings(boolean quotingEnabled)
	{
		List<String> tokens = new ArrayList<>();
		while (true)
		{
			Token token = next(quotingEnabled);
			if (token == null)
				break;
			tokens.add(token.text);
		}
		return tokens;
	}

	//------------------------------------------------------------------

	public String getResidue()
	{
		return ((sequenceEndIndex < 0)
							? null
							: sequence.subSequence(sequenceEndIndex, sequence.length()).toString());
	}

	//------------------------------------------------------------------

	public void setSequence(CharSequence sequence)
	{
		this.sequence = sequence;
		reset();
	}

	//------------------------------------------------------------------

	private Token nextField(boolean quotingEnabled)
	{
		String field = null;
		boolean quoted = false;
		StringBuilder buffer = new StringBuilder(128);
		FieldState state = FieldState.START_OF_FIELD;
		while (state != FieldState.STOP)
		{
			switch (state)
			{
				case START_OF_FIELD:
					if (sequenceIndex < sequence.length())
					{
						char ch = sequence.charAt(sequenceIndex);
						if (ch == inputEndChar)
						{
							sequenceEndIndex = sequenceIndex;
							state = FieldState.STOP;
						}
						else
						{
							++sequenceIndex;
							if (ch == fieldSeparatorChar)
								state = FieldState.END_OF_FIELD;
							else if (quotingEnabled && (ch == '"'))
							{
								quoted = true;
								state = FieldState.QUOTATION;
							}
							else
							{
								buffer.append(ch);
								state = FieldState.FIELD;
							}
						}
					}
					else
					{
						sequenceEndIndex = sequenceIndex;
						state = FieldState.STOP;
					}
					break;

				case FIELD:
					if (sequenceIndex < sequence.length())
					{
						char ch = sequence.charAt(sequenceIndex);
						if (ch == inputEndChar)
						{
							sequenceEndIndex = sequenceIndex;
							state = FieldState.END_OF_FIELD;
						}
						else
						{
							++sequenceIndex;
							if (ch == fieldSeparatorChar)
								state = FieldState.END_OF_FIELD;
							else
								buffer.append(ch);
						}
					}
					else
					{
						sequenceEndIndex = sequenceIndex;
						state = FieldState.END_OF_FIELD;
					}
					break;

				case QUOTATION:
					if (sequenceIndex < sequence.length())
					{
						char ch = sequence.charAt(sequenceIndex++);
						if (ch == '"')
							state = FieldState.QUOTATION_PENDING;
						else
							buffer.append(ch);
					}
					else
						throw new UnclosedQuotationException(sequenceIndex - 1);
					break;

				case QUOTATION_PENDING:
					if (sequenceIndex < sequence.length())
					{
						char ch = sequence.charAt(sequenceIndex);
						if (ch == inputEndChar)
						{
							sequenceEndIndex = sequenceIndex;
							state = FieldState.END_OF_FIELD;
						}
						else
						{
							++sequenceIndex;
							if (ch == fieldSeparatorChar)
								state = FieldState.END_OF_FIELD;
							else
							{
								if (ch != '"')
									throw new UnclosedQuotationException(sequenceIndex - 1);
								buffer.append(ch);
								state = FieldState.QUOTATION;
							}
						}
					}
					else
					{
						sequenceEndIndex = sequenceIndex;
						state = FieldState.END_OF_FIELD;
					}
					break;

				case END_OF_FIELD:
					field = buffer.toString();
					state = FieldState.STOP;
					break;

				case STOP:
					// do nothing
					break;
			}
		}
		return ((field == null) ? null : new Token(field, quoted));
	}

	//------------------------------------------------------------------

	private Token nextToken(boolean quotingEnabled)
	{
		String token = null;
		boolean quoted = false;
		StringBuilder buffer = new StringBuilder(128);
		TokenState state = TokenState.START_OF_TOKEN;
		while (state != TokenState.STOP)
		{
			switch (state)
			{
				case START_OF_TOKEN:
					if (sequenceIndex < sequence.length())
					{
						char ch = sequence.charAt(sequenceIndex);
						if (inputEndChars.indexOf(ch) < 0)
						{
							++sequenceIndex;
							if (tokenSeparatorChars.indexOf(ch) < 0)
							{
								if (quotingEnabled && (ch == '"'))
								{
									quoted = true;
									state = TokenState.QUOTATION;
								}
								else
								{
									buffer.append(ch);
									state = TokenState.TOKEN;
								}
							}
						}
						else
						{
							sequenceEndIndex = sequenceIndex;
							state = TokenState.STOP;
						}
					}
					else
					{
						sequenceEndIndex = sequenceIndex;
						state = TokenState.STOP;
					}
					break;

				case TOKEN:
					if (sequenceIndex < sequence.length())
					{
						char ch = sequence.charAt(sequenceIndex);
						if (inputEndChars.indexOf(ch) < 0)
						{
							++sequenceIndex;
							if (tokenSeparatorChars.indexOf(ch) < 0)
								buffer.append(ch);
							else
								state = TokenState.END_OF_TOKEN;
						}
						else
						{
							sequenceEndIndex = sequenceIndex;
							state = TokenState.END_OF_TOKEN;
						}
					}
					else
					{
						sequenceEndIndex = sequenceIndex;
						state = TokenState.END_OF_TOKEN;
					}
					break;

				case QUOTATION:
					if (sequenceIndex < sequence.length())
					{
						char ch = sequence.charAt(sequenceIndex++);
						if (ch == '"')
							state = TokenState.QUOTATION_PENDING;
						else
							buffer.append(ch);
					}
					else
						throw new UnclosedQuotationException(sequenceIndex - 1);
					break;

				case QUOTATION_PENDING:
					if (sequenceIndex < sequence.length())
					{
						char ch = sequence.charAt(sequenceIndex);
						if (inputEndChars.indexOf(ch) < 0)
						{
							++sequenceIndex;
							if (tokenSeparatorChars.indexOf(ch) < 0)
							{
								if (ch != '"')
									throw new UnclosedQuotationException(sequenceIndex - 1);
								buffer.append(ch);
								state = TokenState.QUOTATION;
							}
							else
								state = TokenState.END_OF_QUOTATION;
						}
						else
							state = TokenState.END_OF_QUOTATION;
					}
					else
					{
						sequenceEndIndex = sequenceIndex;
						state = TokenState.END_OF_TOKEN;
					}
					break;

				case END_OF_QUOTATION:
					token = buffer.toString();
					state = TokenState.STOP;
					break;

				case END_OF_TOKEN:
					if (buffer.length() > 0)
						token = buffer.toString();
					state = TokenState.STOP;
					break;

				case STOP:
					// do nothing
					break;
			}
		}
		return ((token == null) ? null : new Token(token, quoted));
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Instance fields
////////////////////////////////////////////////////////////////////////

	private	char			fieldSeparatorChar;
	private	char			inputEndChar;
	private	String			tokenSeparatorChars;
	private	String			inputEndChars;
	private	CharSequence	sequence;
	private	int				sequenceIndex;
	private	int				prevSequenceIndex;
	private	int				sequenceEndIndex;

}

//----------------------------------------------------------------------
