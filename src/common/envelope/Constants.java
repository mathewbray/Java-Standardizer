/*====================================================================*\

Constants.java

Constants interface.

\*====================================================================*/


// PACKAGE


package common.envelope;

//----------------------------------------------------------------------


// IMPORTS


import java.awt.Insets;

//----------------------------------------------------------------------


// CONSTANTS INTERFACE


interface Constants
{

////////////////////////////////////////////////////////////////////////
//  Constants
////////////////////////////////////////////////////////////////////////

	// Component constants
	Insets	COMPONENT_INSETS	= new Insets(2, 3, 2, 3);

	// Strings
	String	ELLIPSIS_STR	= "...";
	String	OK_STR			= "OK";
	String	CANCEL_STR		= "Cancel";

	// Font keys
	interface FontKey
	{
		String	MAIN		= "main";
		String	TEXT_FIELD	= "textField";
	}

}

//----------------------------------------------------------------------
