/*====================================================================*\

FMenuItem.java

Menu item class.

\*====================================================================*/


// PACKAGE


package common.gui;

//----------------------------------------------------------------------


// IMPORTS


import javax.swing.Action;
import javax.swing.JMenuItem;

//----------------------------------------------------------------------


// MENU ITEM CLASS


public class FMenuItem
	extends JMenuItem
{

////////////////////////////////////////////////////////////////////////
//  Constructors
////////////////////////////////////////////////////////////////////////

	public FMenuItem(Action action)
	{
		super(action);
		GuiUtils.setAppFont(Constants.FontKey.MAIN, this);
	}

	//------------------------------------------------------------------

	public FMenuItem(Action  action,
					 boolean enabled)
	{
		this(action);
		setEnabled(enabled);
	}

	//------------------------------------------------------------------

	public FMenuItem(Action action,
					 int    mnemonic)
	{
		this(action);
		setMnemonic(mnemonic);
	}

	//------------------------------------------------------------------

}

//----------------------------------------------------------------------
