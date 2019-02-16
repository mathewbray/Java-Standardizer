/*====================================================================*\

FIntegerSpinner.java

Integer spinner class.

\*====================================================================*/


// PACKAGE


package common.gui;

//----------------------------------------------------------------------


// INTEGER SPINNER CLASS


public class FIntegerSpinner
	extends IntegerSpinner
{

////////////////////////////////////////////////////////////////////////
//  Constructors
////////////////////////////////////////////////////////////////////////

	public FIntegerSpinner(int value,
						   int minValue,
						   int maxValue,
						   int maxLength)
	{
		this(value, minValue, maxValue, maxLength, false);
	}

	//------------------------------------------------------------------

	public FIntegerSpinner(int     value,
						   int     minValue,
						   int     maxValue,
						   int     maxLength,
						   boolean signed)
	{
		super(value, minValue, maxValue, maxLength, signed);
		GuiUtils.setAppFont(Constants.FontKey.TEXT_FIELD, this);
	}

	//------------------------------------------------------------------

}

//----------------------------------------------------------------------
