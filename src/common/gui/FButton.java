/*====================================================================*\

FButton.java

Button class.

\*====================================================================*/


// PACKAGE


package common.gui;

//----------------------------------------------------------------------


// IMPORTS


import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JButton;

//----------------------------------------------------------------------


// BUTTON CLASS


public class FButton
	extends JButton
{

////////////////////////////////////////////////////////////////////////
//  Constructors
////////////////////////////////////////////////////////////////////////

	public FButton(Action action)
	{
		super(action);
		GuiUtils.setAppFont(Constants.FontKey.MAIN, this);
	}

	//------------------------------------------------------------------

	public FButton(String text)
	{
		super(text);
		GuiUtils.setAppFont(Constants.FontKey.MAIN, this);
	}

	//------------------------------------------------------------------

	public FButton(String text,
				   Icon   icon)
	{
		super(text, icon);
		GuiUtils.setAppFont(Constants.FontKey.MAIN, this);
	}

	//------------------------------------------------------------------

}

//----------------------------------------------------------------------
