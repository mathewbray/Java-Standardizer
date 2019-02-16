/*====================================================================*\

FTextField.java

Text field class.

\*====================================================================*/


// PACKAGE


package common.gui;

//----------------------------------------------------------------------


// IMPORTS


import javax.swing.JTextField;

import javax.swing.text.Document;

//----------------------------------------------------------------------


// TEXT FIELD CLASS


public class FTextField
	extends JTextField
{

////////////////////////////////////////////////////////////////////////
//  Constructors
////////////////////////////////////////////////////////////////////////

	public FTextField(int numColumns)
	{
		this(null, numColumns);
	}

	//------------------------------------------------------------------

	public FTextField(String text,
					  int    numColumns)
	{
		super(text, numColumns);
		GuiUtils.setAppFont(Constants.FontKey.TEXT_FIELD, this);
		GuiUtils.setTextComponentMargins(this);
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

}

//----------------------------------------------------------------------
