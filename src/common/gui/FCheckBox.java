/*====================================================================*\

FCheckBox.java

Check box class.

\*====================================================================*/


// PACKAGE


package common.gui;

//----------------------------------------------------------------------


// IMPORTS


import javax.swing.BorderFactory;
import javax.swing.JCheckBox;

//----------------------------------------------------------------------


// CHECK BOX CLASS


public class FCheckBox
	extends JCheckBox
{

////////////////////////////////////////////////////////////////////////
//  Constants
////////////////////////////////////////////////////////////////////////

	private static final	int	ICON_TEXT_GAP	= 6;

////////////////////////////////////////////////////////////////////////
//  Constructors
////////////////////////////////////////////////////////////////////////

	public FCheckBox(String text)
	{
		this(text, false);
	}

	//------------------------------------------------------------------

	public FCheckBox(String  text,
					 boolean noVerticalBorder)
	{
		super(text);
		GuiUtils.setAppFont(Constants.FontKey.MAIN, this);
		setBorder(noVerticalBorder ? null : BorderFactory.createEmptyBorder(2, 0, 2, 0));
		setIconTextGap(ICON_TEXT_GAP);
	}

	//------------------------------------------------------------------

}

//----------------------------------------------------------------------
