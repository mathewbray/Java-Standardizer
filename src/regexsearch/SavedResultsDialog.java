/*====================================================================*\

SavedResultsDialog.java

Saved results dialog box class.

\*====================================================================*/


// PACKAGE


package regexsearch;

//----------------------------------------------------------------------


// IMPORTS


import java.awt.Component;
import java.awt.Window;

import common.gui.GuiUtils;
import common.gui.NonEditableTextAreaDialog;

//----------------------------------------------------------------------


// SAVED RESULTS DIALOG BOX CLASS


class SavedResultsDialog
	extends NonEditableTextAreaDialog
{

////////////////////////////////////////////////////////////////////////
//  Constants
////////////////////////////////////////////////////////////////////////

	private static final	int	NUM_COLUMNS	= 72;
	private static final	int	NUM_ROWS	= 24;

	private static final	String	TITLE_STR	= "Saved results";

	private static final	String	KEY	= SavedResultsDialog.class.getCanonicalName();

////////////////////////////////////////////////////////////////////////
//  Constructors
////////////////////////////////////////////////////////////////////////

	private SavedResultsDialog(Window owner,
							   String text)
	{
		super(owner, TITLE_STR, KEY, NUM_COLUMNS, NUM_ROWS, text);
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Class methods
////////////////////////////////////////////////////////////////////////

	public static void showDialog(Component parent,
								  String    text)
	{
		new SavedResultsDialog(GuiUtils.getWindow(parent), text);
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Instance methods : overriding methods
////////////////////////////////////////////////////////////////////////

	@Override
	protected void setTextAreaAttributes()
	{
		setCaretToStart();
	}

	//------------------------------------------------------------------

}

//----------------------------------------------------------------------
