/*====================================================================*\

Constants.java

Constants interface.

\*====================================================================*/


// PACKAGE


package common.gui;

//----------------------------------------------------------------------


// IMPORTS


import java.awt.BasicStroke;
import java.awt.Insets;
import java.awt.Stroke;

//----------------------------------------------------------------------


// CONSTANTS INTERFACE


interface Constants
{

////////////////////////////////////////////////////////////////////////
//  Constants
////////////////////////////////////////////////////////////////////////

	// Component constants
	Insets	COMPONENT_INSETS	= new Insets(2, 3, 2, 3);

	// Font keys
	interface FontKey
	{
		String	COMBO_BOX	= "comboBox";
		String	MAIN		= "main";
		String	TEXT_AREA	= "textArea";
		String	TEXT_FIELD	= "textField";
	}

	// Strings
	String	ELLIPSIS_STR	= "...";
	String	OK_STR			= "OK";
	String	CANCEL_STR		= "Cancel";
	String	CLOSE_STR		= "Close";

	// Strokes
	Stroke	BASIC_DASH	= new BasicStroke(1.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10.0f,
										  new float[]{ 1.0f, 1.0f }, 0.5f);

}

//----------------------------------------------------------------------
