/*====================================================================*\

FDoubleSpinner.java

Double spinner class.

\*====================================================================*/


// PACKAGE


package common.gui;

//----------------------------------------------------------------------


// IMPORTS


import java.text.NumberFormat;

//----------------------------------------------------------------------


// DOUBLE SPINNER CLASS


public class FDoubleSpinner
	extends DoubleSpinner
{

////////////////////////////////////////////////////////////////////////
//  Constructors
////////////////////////////////////////////////////////////////////////

	public FDoubleSpinner(double       value,
						  double       minValue,
						  double       maxValue,
						  double       stepSize,
						  int          maxLength,
						  NumberFormat format)
	{
		this(value, minValue, maxValue, stepSize, maxLength, format, false);
	}

	//------------------------------------------------------------------

	public FDoubleSpinner(double       value,
						  double       minValue,
						  double       maxValue,
						  double       stepSize,
						  int          maxLength,
						  NumberFormat format,
						  boolean      signed)
	{
		super(value, minValue, maxValue, stepSize, maxLength, format, signed);
		GuiUtils.setAppFont(Constants.FontKey.TEXT_FIELD, this);
	}

	//------------------------------------------------------------------

}

//----------------------------------------------------------------------
