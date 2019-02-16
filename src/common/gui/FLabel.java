/*====================================================================*\

FLabel.java

Label class.

\*====================================================================*/


// PACKAGE


package common.gui;

//----------------------------------------------------------------------


// IMPORTS


import javax.swing.JLabel;

//----------------------------------------------------------------------


// LABEL CLASS


public class FLabel
	extends JLabel
{

////////////////////////////////////////////////////////////////////////
//  Constructors
////////////////////////////////////////////////////////////////////////

	public FLabel(String text)
	{
		super(text);
		GuiUtils.setAppFont(Constants.FontKey.MAIN, this);
	}

	//------------------------------------------------------------------

}

//----------------------------------------------------------------------
