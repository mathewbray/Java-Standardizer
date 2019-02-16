/*====================================================================*\

XmlConstants.java

XML constants interface.

\*====================================================================*/


// PACKAGE


package common.xml;

//----------------------------------------------------------------------


// XML CONSTANTS INTERFACE


public interface XmlConstants
{

////////////////////////////////////////////////////////////////////////
//  Constants
////////////////////////////////////////////////////////////////////////

	// XML predefined entities
	interface EntityName
	{
		String	AMP		= "amp";
		String	APOS	= "apos";
		String	GT		= "gt";
		String	LT		= "lt";
		String	QUOT	= "quot";
	}

	String	ENTITY_PREFIX	= "&";
	String	ENTITY_SUFFIX	= ";";

	String	ENTITY_AMP	=   ENTITY_PREFIX + EntityName.AMP + ENTITY_SUFFIX;
	String	ENTITY_APOS	=   ENTITY_PREFIX + EntityName.APOS + ENTITY_SUFFIX;
	String	ENTITY_GT	=   ENTITY_PREFIX + EntityName.GT + ENTITY_SUFFIX;
	String	ENTITY_LT	=   ENTITY_PREFIX + EntityName.LT + ENTITY_SUFFIX;
	String	ENTITY_QUOT	=   ENTITY_PREFIX + EntityName.QUOT + ENTITY_SUFFIX;

	// Path separator
	char	PATH_SEPARATOR_CHAR	= '/';
	String	PATH_SEPARATOR		= Character.toString(PATH_SEPARATOR_CHAR);

	// Attribute prefix
	char	ATTRIBUTE_PREFIX_CHAR	= '@';
	String	ATTRIBUTE_PREFIX		= Character.toString(ATTRIBUTE_PREFIX_CHAR);

	// Charset name
	String	ENCODING_NAME_UTF8	= "UTF-8";

	// Temporary-file prefix
	String	TEMP_FILE_PREFIX	= "_$_";

}

//----------------------------------------------------------------------
