/*====================================================================*\

FTextArea.java

Text area class.

\*====================================================================*/


// PACKAGE


package common.gui;

//----------------------------------------------------------------------


// IMPORTS


import javax.swing.JTextArea;

import javax.swing.text.Document;

//----------------------------------------------------------------------


// TEXT AREA CLASS


public class FTextArea
	extends JTextArea
{

////////////////////////////////////////////////////////////////////////
//  Constructors
////////////////////////////////////////////////////////////////////////

	public FTextArea()
	{
		_init();
	}

	//------------------------------------------------------------------

	public FTextArea(String text)
	{
		super(text);
		_init();
	}

	//------------------------------------------------------------------

	public FTextArea(int numRows,
					 int numColumns)
	{
		super(numRows, numColumns);
		_init();
	}

	//------------------------------------------------------------------

	public FTextArea(String text,
					 int    numRows,
					 int    numColumns)
	{
		super(text, numRows, numColumns);
		_init();
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Instance methods
////////////////////////////////////////////////////////////////////////

	public boolean isEmpty()
	{
		Document document = getDocument();
		return ((document == null) ? true : (document.getLength() == 0));
	}

	//------------------------------------------------------------------

	private void _init()
	{
		String fontKey = Constants.FontKey.TEXT_AREA;
		if (!GuiUtils.isAppFont(fontKey))
			fontKey = Constants.FontKey.TEXT_FIELD;
		GuiUtils.setAppFont(fontKey, this);
		setBorder(null);
	}

	//------------------------------------------------------------------

}

//----------------------------------------------------------------------
