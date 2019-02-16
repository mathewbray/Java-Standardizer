/*====================================================================*\

LongField.java

Long text field class.

\*====================================================================*/


// PACKAGE


package common.textfield;

//----------------------------------------------------------------------


// IMPORTS


import common.misc.NumberUtils;

//----------------------------------------------------------------------


// LONG TEXT FIELD CLASS


public abstract class LongField
	extends LongValueField
{

////////////////////////////////////////////////////////////////////////
//  Member classes : non-inner classes
////////////////////////////////////////////////////////////////////////


	// UNSIGNED LONG FIELD CLASS


	public static class Unsigned
		extends LongField
	{

	////////////////////////////////////////////////////////////////////
	//  Constants
	////////////////////////////////////////////////////////////////////

		private static final	String	VALID_CHARS	= "0123456789";

	////////////////////////////////////////////////////////////////////
	//  Constructors
	////////////////////////////////////////////////////////////////////

		public Unsigned(int maxLength)
		{
			super(maxLength);
		}

		//--------------------------------------------------------------

		public Unsigned(int  maxLength,
						long value)
		{
			super(maxLength, value);
		}

		//--------------------------------------------------------------

	////////////////////////////////////////////////////////////////////
	//  Instance methods : overriding methods
	////////////////////////////////////////////////////////////////////

		/**
		 * @throws NumberFormatException
		 */

		@Override
		public long getValue()
		{
			return NumberUtils.parseULongDec(getText());
		}

		//--------------------------------------------------------------

		@Override
		public void setValue(long value)
		{
			setText(NumberUtils.uLongToDecString(value));
		}

		//--------------------------------------------------------------

		@Override
		protected boolean acceptCharacter(char ch,
										  int  index)
		{
			return (VALID_CHARS.indexOf(ch) >= 0);
		}

		//--------------------------------------------------------------

	}

	//==================================================================


	// SIGNED LONG FIELD CLASS


	public static class Signed
		extends LongField
	{

	////////////////////////////////////////////////////////////////////
	//  Constants
	////////////////////////////////////////////////////////////////////

		private static final	String	VALID_CHARS	= "-0123456789";

	////////////////////////////////////////////////////////////////////
	//  Constructors
	////////////////////////////////////////////////////////////////////

		public Signed(int maxLength)
		{
			super(maxLength);
		}

		//--------------------------------------------------------------

		public Signed(int  maxLength,
					  long value)
		{
			super(maxLength, value);
		}

		//--------------------------------------------------------------

	////////////////////////////////////////////////////////////////////
	//  Instance methods : overriding methods
	////////////////////////////////////////////////////////////////////

		@Override
		protected boolean acceptCharacter(char ch,
										  int  index)
		{
			return (VALID_CHARS.indexOf(ch) >= 0);
		}

		//--------------------------------------------------------------

	}

	//==================================================================

////////////////////////////////////////////////////////////////////////
//  Constructors
////////////////////////////////////////////////////////////////////////

	private LongField(int maxLength)
	{
		super(maxLength);
	}

	//------------------------------------------------------------------

	private LongField(int  maxLength,
					  long value)
	{
		super(maxLength);
		setValue(value);
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Instance methods : overriding methods
////////////////////////////////////////////////////////////////////////

	/**
	 * @throws NumberFormatException
	 */

	@Override
	public long getValue()
	{
		return Long.parseLong(getText());
	}

	//------------------------------------------------------------------

	@Override
	public void setValue(long value)
	{
		setText(Long.toString(value));
	}

	//------------------------------------------------------------------

}

//----------------------------------------------------------------------
