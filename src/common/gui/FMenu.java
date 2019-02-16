/*====================================================================*\

FMenu.java

Menu class.

\*====================================================================*/


// PACKAGE


package common.gui;

//----------------------------------------------------------------------


// IMPORTS


import javax.swing.Action;
import javax.swing.JMenu;

//----------------------------------------------------------------------


// MENU CLASS


public class FMenu
	extends JMenu
{

////////////////////////////////////////////////////////////////////////
//  Constructors
////////////////////////////////////////////////////////////////////////

	public FMenu(String text)
	{
		super(text);
		GuiUtils.setAppFont(Constants.FontKey.MAIN, this);
	}

	//------------------------------------------------------------------

	public FMenu(Action action)
	{
		super(action);
		GuiUtils.setAppFont(Constants.FontKey.MAIN, this);
	}

	//------------------------------------------------------------------

	public FMenu(String text,
				 int    mnemonic)
	{
		this(text);
		setMnemonic(mnemonic);
	}

	//------------------------------------------------------------------

}

//----------------------------------------------------------------------
