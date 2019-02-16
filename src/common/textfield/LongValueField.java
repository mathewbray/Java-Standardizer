/*====================================================================*\

LongValueField.java

Long value field class.

\*====================================================================*/


// PACKAGE


package common.textfield;

//----------------------------------------------------------------------


// LONG VALUE FIELD CLASS


public abstract class LongValueField
	extends ConstrainedTextField
{

////////////////////////////////////////////////////////////////////////
//  Constructors
////////////////////////////////////////////////////////////////////////

	protected LongValueField(int maxLength)
	{
		super(maxLength);
	}

	//------------------------------------------------------------------

	protected LongValueField(int    maxLength,
							 String text)
	{
		super(maxLength, text);
	}

	//------------------------------------------------------------------

	protected LongValueField(int maxLength,
							 int columns)
	{
		super(maxLength, columns);
	}

	//------------------------------------------------------------------

	protected LongValueField(int    maxLength,
							 int    columns,
							 String text)
	{
		super(maxLength, columns, text);
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Abstract methods
////////////////////////////////////////////////////////////////////////

	public abstract long getValue();

	//------------------------------------------------------------------

	public abstract void setValue(long value);

	//------------------------------------------------------------------

}

//----------------------------------------------------------------------
