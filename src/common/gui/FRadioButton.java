/*====================================================================*\

FRadioButton.java

Radio button class.

\*====================================================================*/


// PACKAGE


package common.gui;

//----------------------------------------------------------------------


// IMPORTS


import javax.swing.BorderFactory;
import javax.swing.JRadioButton;

//----------------------------------------------------------------------


// RADIO BUTTON CLASS


public class FRadioButton
	extends JRadioButton
{

////////////////////////////////////////////////////////////////////////
//  Constructors
////////////////////////////////////////////////////////////////////////

	public FRadioButton(String text)
	{
		this(text, false, false);
	}

	//------------------------------------------------------------------

	public FRadioButton(String  text,
						boolean noVerticalBorder)
	{
		this(text, noVerticalBorder, false);
	}

	//------------------------------------------------------------------

	public FRadioButton(String  text,
						boolean noVerticalBorder,
						boolean selected)
	{
		super(text, selected);
		GuiUtils.setAppFont(Constants.FontKey.MAIN, this);
		setBorder(noVerticalBorder ? null : BorderFactory.createEmptyBorder(2, 0, 2, 0));
	}

	//------------------------------------------------------------------

}

//----------------------------------------------------------------------
