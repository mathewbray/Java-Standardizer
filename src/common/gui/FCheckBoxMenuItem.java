/*====================================================================*\

FCheckBoxMenuItem.java

Check box menu item class.

\*====================================================================*/


// PACKAGE


package common.gui;

//----------------------------------------------------------------------


// IMPORTS


import javax.swing.Action;
import javax.swing.JCheckBoxMenuItem;

//----------------------------------------------------------------------


// CHECK BOX MENU ITEM CLASS


public class FCheckBoxMenuItem
	extends JCheckBoxMenuItem
{

////////////////////////////////////////////////////////////////////////
//  Constructors
////////////////////////////////////////////////////////////////////////

	public FCheckBoxMenuItem(Action action)
	{
		super(action);
		GuiUtils.setAppFont(Constants.FontKey.MAIN, this);
	}

	//------------------------------------------------------------------

	public FCheckBoxMenuItem(Action  action,
							 boolean selected)
	{
		this(action);
		setSelected(selected);
	}

	//------------------------------------------------------------------

	public FCheckBoxMenuItem(Action action,
							 int    mnemonic)
	{
		this(action);
		setMnemonic(mnemonic);
	}

	//------------------------------------------------------------------

}

//----------------------------------------------------------------------
